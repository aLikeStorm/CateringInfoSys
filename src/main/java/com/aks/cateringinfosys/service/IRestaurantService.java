package com.aks.cateringinfosys.service;

import com.aks.cateringinfosys.dto.Result;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 12:45
 * @packagename com.aks.cateringinfosys.service
 * @classname IRestaurantServie
 * @description
 */
public interface IRestaurantService {
    Result getRestaurantsFromCity(Integer cityCode);

    Result getRestaurantListFromName(Integer cityCode, Integer typeCode, String rName);

    Result getRestaurantById(Long rid);
}
