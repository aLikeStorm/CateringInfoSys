package com.aks.cateringinfosys.service;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.Restaurant;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 12:45
 * @packagename com.aks.cateringinfosys.service
 * @classname IRestaurantServie
 * @description
 */
public interface IRestaurantService {
    Result getRestaurantsFromCity(String cityName);

    Result getRestaurantListFromName(Integer cityCode, Integer typeCode,
                                     String rName,Integer currentPage,Integer pagSize);

    Result getRestaurantById(Long rid);

    Result addRest(Restaurant restaurant);

    Result getRestType();

    Result deleteRest(Long restId);

    Result updateRest(Restaurant restaurant);

    Result likeRestaurant(Long restId);
}
