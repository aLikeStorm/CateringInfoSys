package com.aks.cateringinfosys.mappers;

import com.aks.cateringinfosys.entry.City;
import com.aks.cateringinfosys.entry.Restaurant;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 12:48
 * @packagename com.aks.cateringinfosys.mappers
 * @classname RestaurantMapper
 * @description
 */
@Component
@Mapper
public interface RestaurantMapper {
    //todo 按照城市code查询评论数量在前20的餐馆
    List<Restaurant> queryRestByCityCode(Integer cityCode);

    //todo 根据城市code，餐馆类型，餐馆名模糊餐馆列表
    List<Restaurant> queryRestByName(Integer cityCode, Integer typeCode, String rName);

    // todo 根据餐馆id查询
    Restaurant queryRestById(Long rid);

    // 查询餐馆所在城市
    City queryRestAddress(Long rid);

}
