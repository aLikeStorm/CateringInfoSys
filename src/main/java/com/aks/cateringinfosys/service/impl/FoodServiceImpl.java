package com.aks.cateringinfosys.service.impl;

import cn.hutool.json.JSONUtil;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.Food;
import com.aks.cateringinfosys.mappers.FoodMapper;
import com.aks.cateringinfosys.mappers.ImageMapper;
import com.aks.cateringinfosys.service.IFoodService;
import com.aks.cateringinfosys.utils.RedisConstants;
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
 * @date 2023/6/18 11:47
 * @packagename com.aks.cateringinfosys.service.impl
 * @classname FoodServiceImpl
 * @description
 */
@Service
public class FoodServiceImpl implements IFoodService {
    private static final Logger logger = LoggerFactory.getLogger(FoodServiceImpl.class);
    @Autowired
    FoodMapper foodMapper;
    @Autowired
    ImageMapper imageMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedisIdWorker idWorker;
    @Override
    public Result getFoodList(Long restId, String restName,Integer currentPage,Integer pageSize) {
        String key = String.valueOf(currentPage << 10 + pageSize);
        if (restId != null) {

            String foodStr = (String) redisTemplate.opsForHash().get(CACHE_FOOD_KEY + restId,key);
            if (foodStr != null && foodStr != "" && !CACHE_NULL.equals(foodStr)) {
                List<Food> foods = JSONUtil.toList(foodStr, Food.class);
                return Result.ok(foods);
            }
            if (CACHE_NULL.equals(foodStr)) {
                return Result.fail("该店铺还没有食物哦");
            }
        }
        PageHelper.startPage(currentPage,pageSize);
        List<Food> foodList = foodMapper.queryFoodList(restId,restName);
        PageInfo<Food> foodPageInfo = new PageInfo<>(foodList);
        foodList = foodPageInfo.getList();
        if (foodList == null ||foodList.size() == 0) {
            if (restId != null ){
                redisTemplate.opsForHash().put(CACHE_FOOD_KEY+restId,key,CACHE_NULL);
                redisTemplate.expire(CACHE_FOOD_KEY+restId,CACHE_NULL_TTL,TimeUnit.MINUTES);
            }
            return Result.fail("该店铺还没有食物哦");
        }
         foodList = foodList.stream().map(food -> {
             List<String> imageListByForeign = imageMapper.queryImageListByForeign(food.getFoodId());
             food.setImageList(imageListByForeign);
             return food;
         }).collect(Collectors.toList());
        redisTemplate.opsForHash().put(CACHE_FOOD_KEY+foodList.get(0).getFoodFormRest(),key,
                JSONUtil.toJsonStr(foodList));
        redisTemplate.expire(CACHE_FOOD_KEY+restId,CACHE_NULL_TTL,TimeUnit.MINUTES);
        return Result.ok(foodList,foodPageInfo.getTotal());
    }

    @Override
    @Transactional
    public Result addFood(Food food) {
        food.setCreateTime(LocalDateTime.now());
        Long foodId = idWorker.nextId();
        food.setFoodId(foodId);
        Integer flag = foodMapper.insertFood(food);
        if (flag != 0) {
            return Result.fail("添加菜品失败");
        }
        List<String> imageList = food.getImageList();
        if (imageList !=null ){
            imageList.stream().forEach(img ->{imageMapper.insertImage(idWorker.nextId(),img ,foodId);});
        }
        logger.info("用户"+ UserHolder.getUser().getUid()+"添加食物"+food);
        redisTemplate.delete(CACHE_FOOD_KEY+food.getFoodFormRest());
        return Result.ok("添加菜品成功");
    }

    @Override
    @Transactional
    public Result updateFood(Food food) {
        List<String> imageList = food.getImageList();
        Long foodId = food.getFoodId();
        Integer flag = foodMapper.updateFood(food);
        if (flag != 1) {
            return Result.fail("修改菜品失败，请重试");
        }
        if (imageList != null && imageList.size() != 0) {
            imageList.stream().forEach(img->imageMapper.insertImage(idWorker.nextId(),img,foodId));
        }
        logger.info("用户"+ UserHolder.getUser().getUid()+"修改食物"+food);
        redisTemplate.delete(CACHE_FOOD_KEY+food.getFoodFormRest());
        return Result.ok("修改菜品成功");
    }

    @Override
    @Transactional
    public Result deleteFood(Long foodId) {
        Food food = foodMapper.queryFoodById(foodId);
        Long foodFormRest = food.getFoodFormRest();
        Integer flag = foodMapper.deleteFoodById(foodId);
        if (flag != 1) {
            return Result.fail("删除菜品失败");
        }
        imageMapper.deleteByForeign(foodId);
        redisTemplate.delete(CACHE_FOOD_KEY+foodFormRest);
        logger.info("用户"+ UserHolder.getUser().getUid()+"删除食物"+food);
        return Result.ok("删除菜品成功");
    }
}
