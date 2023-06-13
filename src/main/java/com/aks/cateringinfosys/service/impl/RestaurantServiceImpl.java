package com.aks.cateringinfosys.service.impl;

import cn.hutool.json.JSONUtil;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.Restaurant;
import com.aks.cateringinfosys.mappers.ImageMapper;
import com.aks.cateringinfosys.mappers.RestaurantMapper;
import com.aks.cateringinfosys.service.IRestaurantService;
import com.aks.cateringinfosys.utils.UserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
    RestaurantMapper restaurantMapper;
    @Autowired
    ImageMapper imageMapper;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public Result getRestaurantsFromCity(Integer cityCode) {
        //todo 1. 首先查询redis获取
        String restStr = redisTemplate.opsForValue().get(CACHE_HOT_RESTAURANT_KEY + ":"+cityCode);

        // todo 2. 判断redis中有该城市餐馆的缓存
        if (restStr != null && restStr != "" && !restStr.equals(CACHE_NULL)) {
            List<Restaurant> restList = JSONUtil.toBean(restStr, List.class);
            logger.info(UserHolder.getUser().getUid() + "查询城市"+cityCode + "获取餐馆列表" + restList);
            return Result.ok(restList);
        }
        if (CACHE_NULL.equals(restStr)) {
            logger.info(UserHolder.getUser().getUid() + "查询城市"+cityCode + "获取餐馆列表为空");
            return Result.fail("该城市没有餐馆!");
        }

        // todo 3. 没有缓存，查询数据库，获取
        List<Restaurant> restList = restaurantMapper.queryRestByCityCode(cityCode);

        // todo 4. 没有缓存，将该城市没有餐馆
        if (restList == null || restList.size() == 0) {
            // todo 4.1 向redis中这个城市的热点餐馆缓存为空,缓存的保存时间不应该太久
            redisTemplate.opsForValue().set(CACHE_HOT_RESTAURANT_KEY+ ":"+cityCode,
                    CACHE_NULL,
                    CACHE_NULL_TTL,
                    TimeUnit.MINUTES);
            logger.info(UserHolder.getUser().getUid() + "查询城市"+cityCode + "获取餐馆列表为空");
            return Result.ok("该城市餐饮店为空");
        }
        // todo 5. 查询餐馆的相关图片 将该城市的餐馆列表存入redis中缓存
        restList.stream().map(restaurant -> {
            List<String> imageList = imageMapper.queryImageListByForeign(restaurant.getRid());
            restaurant.setImageList(imageList);

            return null;
        }).collect(Collectors.toList());

        restStr = JSONUtil.toJsonStr(restList);
        redisTemplate.opsForValue().set(CACHE_HOT_RESTAURANT_KEY+ ":"+cityCode,
                restStr,
                CACHE_RESTAURANT_TTL,
                TimeUnit.MINUTES);
        logger.info(UserHolder.getUser().getUid() + "查询城市"+cityCode + "获取餐馆列表"+restList);
        return Result.ok(restList);
    }

    @Override
    public Result getRestaurantListFromName(Integer cityCode, Integer typeCode, String rName) {
        List<Restaurant> restList = restaurantMapper.queryRestByName(cityCode, typeCode, rName);
        // todo 5. 查询餐馆的相关图片 将该城市的餐馆列表存入redis中缓存
        restList.stream().map(restaurant -> {
            List<String> imageList = imageMapper.queryImageListByForeign(restaurant.getRid());
            restaurant.setImageList(imageList);

            return null;
        }).collect(Collectors.toList());
        logger.info(UserHolder.getUser().getUid() + "查询城市"+cityCode +"类型"+typeCode+"模糊名" + rName+ "获取餐馆列表"+restList);
        return Result.ok(restList);
    }

    @Override
    public Result getRestaurantById(Long rid) {

        // todo 1. 去redis查询餐馆
        String restStr = redisTemplate.opsForValue().get(CACHE_RESTAURANT_KEY + ":" + rid);

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
        redisTemplate.opsForValue().set(CACHE_RESTAURANT_KEY+":"+rid,
                JSONUtil.toJsonStr(rest),
                CACHE_RESTAURANT_TTL,
                TimeUnit.MINUTES);
        logger.info(UserHolder.getUser().getUid() + "查询餐馆"+rid + "获取餐馆"+rest);
        return Result.ok(rest);
    }


}
