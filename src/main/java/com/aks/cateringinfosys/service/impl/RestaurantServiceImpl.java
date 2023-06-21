package com.aks.cateringinfosys.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.RestType;
import com.aks.cateringinfosys.entry.Restaurant;
import com.aks.cateringinfosys.mappers.CityMapper;
import com.aks.cateringinfosys.mappers.ImageMapper;
import com.aks.cateringinfosys.mappers.RestaurantMapper;
import com.aks.cateringinfosys.service.IRestaurantService;
import com.aks.cateringinfosys.utils.RedisIdWorker;
import com.aks.cateringinfosys.utils.UserHolder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.aks.cateringinfosys.utils.RedisConstants.*;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 12:46
 * @packagename com.aks.cateringinfosys.service.impl
 * @classname RestaurantServiceImpl
 * @description
 */
@Service
public class RestaurantServiceImpl implements IRestaurantService {
    public static Logger logger = LoggerFactory.getLogger(RestaurantServiceImpl.class);
    @Autowired
    CityMapper cityMapper;
    @Autowired
    RestaurantMapper restaurantMapper;
    @Autowired
    ImageMapper imageMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedisIdWorker idWorker;

    @Override
    public Result getRestaurantsFromCity(String cityName) {
        Integer cityCode = cityMapper.queryCityCodeByCityName(cityName);
        //todo 1. 首先查询redis获取
        String restStr = redisTemplate.opsForValue().get(CACHE_HOT_RESTAURANT_KEY + cityCode);

        // todo 2. 判断redis中有该城市餐馆的缓存
        if (restStr != null && restStr != "" && !restStr.equals(CACHE_NULL)) {
            List<Restaurant> restList = JSONUtil.toList(restStr, Restaurant.class);
            logger.info(UserHolder.getUser().getUid() + "查询城市"+cityCode + "获取餐馆列表" + restList);
            return Result.ok(restList);
        }
        if (CACHE_NULL.equals(restStr)) {
            logger.info(UserHolder.getUser().getUid() + "查询城市"+cityCode + "获取餐馆列表为空");
            return Result.fail("该城市没有餐馆!");
        }

        // todo 3. 没有缓存，查询数据库，获取
        List<Restaurant> restList = restaurantMapper.queryRestByAddres(cityName);

        // todo 4. 没有缓存，将该城市没有餐馆
        if (restList == null || restList.size() == 0) {
            // todo 4.1 向redis中这个城市的热点餐馆缓存为空,缓存的保存时间不应该太久
            redisTemplate.opsForValue().set(CACHE_HOT_RESTAURANT_KEY + cityCode,
                    CACHE_NULL,
                    CACHE_NULL_TTL,
                    TimeUnit.MINUTES);
            logger.info(UserHolder.getUser().getUid() + "查询城市"+cityCode + "获取餐馆列表为空");
            return Result.ok("该城市餐饮店为空");
        }
        // todo 5. 查询餐馆的相关图片 将该城市的餐馆列表存入redis中缓存
        restList.stream().map(restaurant -> {
            List<String> imageList = imageMapper.queryImageListByForeign(restaurant.getRestId());
            restaurant.setImageList(imageList);

            return null;
        }).collect(Collectors.toList());

        restStr = JSONUtil.toJsonStr(restList);
        redisTemplate.opsForValue().set(CACHE_HOT_RESTAURANT_KEY + cityCode,
                restStr,
                CACHE_RESTAURANT_TTL,
                TimeUnit.MINUTES);
        logger.info(UserHolder.getUser().getUid() + "查询城市"+cityCode + "获取餐馆列表"+restList);
        return Result.ok(restList);
    }

    @Override
    public Result getRestaurantListFromName(Integer cityCode, Integer typeCode, String rName,
                                            Integer currentPage,
                                            Integer pageSize) {

        PageHelper.startPage(currentPage,pageSize);
        List<Restaurant> restList = restaurantMapper.queryRestByName(cityCode, typeCode, rName);
        PageInfo<Restaurant> restaurantPageInfo = new PageInfo<>(restList, 3);
        restList = restaurantPageInfo.getList();
        // todo 5. 查询餐馆的相关图片 将该城市的餐馆列表存入redis中缓存
        restList.stream().map(restaurant -> {
            List<String> imageList = imageMapper.queryImageListByForeign(restaurant.getRestId());
            restaurant.setImageList(imageList);

            return null;
        }).collect(Collectors.toList());
        logger.info(UserHolder.getUser().getUid() + "查询城市"+cityCode +"类型"+typeCode+"模糊名" + rName+ "获取餐馆列表"+restList);
        return Result.ok(restList,restaurantPageInfo.getTotal());
    }

    @Override
    public Result getRestaurantById(Long rid) {

        // todo 1. 去redis查询餐馆
        String restStr = redisTemplate.opsForValue().get(CACHE_RESTAURANT_KEY  + rid);

        // todo 2. 命中
        if (restStr != null && restStr != "") {
            Restaurant rest = JSONUtil.toBean(restStr, Restaurant.class);
            logger.info(UserHolder.getUser().getUid() + "查询餐馆"+rid + "获取餐馆"+rest);
            return Result.ok(rest);
        }

        // todo 3. 未命中，去数据库查询
        Restaurant rest = restaurantMapper.queryRestById(rid);

        // todo 4. 不存在
        if(rest == null) {
            logger.info(UserHolder.getUser().getUid() + "查询餐馆"+rid + "获取餐馆不存在");
            return Result.fail("该餐馆已经不存在");
        }

        // todo 5. 查询到存入redis 返回
        rest.setImageList(imageMapper.queryImageListByForeign(rid));
        redisTemplate.opsForValue().set(CACHE_RESTAURANT_KEY + rid,
                JSONUtil.toJsonStr(rest),
                CACHE_RESTAURANT_TTL,
                TimeUnit.MINUTES);
        logger.info(UserHolder.getUser().getUid() + "查询餐馆"+rid + "获取餐馆"+rest);
        return Result.ok(rest);
    }

    @Override
    @Transactional
    public Result addRest(Restaurant restaurant) {
        String restAddress = restaurant.getRestAddress();

        //获取城市code
        String city = StrUtil.subBefore(restAddress, "市", false);
        Integer length = city.length();
        city = city.substring(length - 2,length);
        Integer code = cityMapper.queryCityCodeByCityName(city);
        restaurant.setRestCity(Long.valueOf(code));
        restaurant.setRestId(idWorker.nextId());
        restaurant.setRestLikeNum(0);
        restaurant.setRestScore(1.00f);
        restaurant.setCreateTime(LocalDateTime.now());
        Integer integer = restaurantMapper.insertRestaurant(restaurant);
        if (integer == 1) {
            redisTemplate.delete(CACHE_RESTAURANT_KEY+code);
            return Result.ok("添加餐饮点成功");
        }
        return Result.fail("添加餐饮点失败");
    }

    @Override
    public Result getRestType() {
        String typeStr = redisTemplate.opsForValue().get(CACHE_RESTAURANT_KEY + "type");
        // todo 2. 判断redis中有该城市餐馆的缓存
        if (typeStr != null && typeStr != "" && !typeStr.equals(CACHE_NULL)) {
            List<RestType> typeList = JSONUtil.toList(typeStr,RestType.class);
            return Result.ok(typeList);
        }
        if (CACHE_NULL.equals(typeStr)) {
            return Result.fail("该城市没有餐馆!");
        }
        List<RestType> restTypes = restaurantMapper.queryType();
        if (restTypes == null ) {
            redisTemplate.opsForValue().set(CACHE_RESTAURANT_KEY + "type",CACHE_NULL,CACHE_NULL_TTL,TimeUnit.MINUTES);
            return Result.fail("该城市没有餐馆!");
        }
        redisTemplate.opsForValue().set(CACHE_RESTAURANT_KEY + "type",JSONUtil.toJsonStr(restTypes),CACHE_RESTAURANT_TTL,TimeUnit.MINUTES);

        return Result.ok(restTypes);
    }

    @Transactional
    @Override
    public Result deleteRest(Long restId) {
        Integer flag = restaurantMapper.deleteRestById(restId);
        imageMapper.deleteByForeign(restId);
        if (flag == 1) {
            logger.info("用户"+UserHolder.getUser().getUid()+"删除一个店铺"+restId);
            redisTemplate.delete(CACHE_RESTAURANT_KEY+restId);
            return Result.ok("删除成功");
        } else {
            return Result.fail("删除失败，刷新试试");
        }
    }

    @Override
    public Result updateRest(Restaurant restaurant) {
        Integer flag = restaurantMapper.updateRest(restaurant);
        if (flag != 1) {
            return Result.fail("修改失败，刷新试试");
        }
        if (restaurant.getImageList() != null) {
            logger.info("用户"+UserHolder.getUser().getUid()+"删除外键为"+restaurant.getRestId() +"的所有图片");
            imageMapper.deleteByForeign(restaurant.getRestId());
            restaurant.getImageList().forEach(file->{
                long l = idWorker.nextId();
                logger.info("用户"+UserHolder.getUser().getUid()+"插入一张图片"+l);
                imageMapper.insertImage(l,file,restaurant.getRestId());
            });
        }
        redisTemplate.delete(CACHE_RESTAURANT_KEY+restaurant.getRestId());
        return Result.ok("修改成功");
    }


}
