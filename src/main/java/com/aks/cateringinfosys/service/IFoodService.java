package com.aks.cateringinfosys.service;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.Food;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/18 11:46
 * @packagename com.aks.cateringinfosys.service
 * @classname IFoodService
 * @description
 */
public interface IFoodService {
    Result getFoodList(Long restId, String restName,Integer currentPage,Integer pageSize);

    Result addFood(Food food);

    Result updateFood(Food food);

    Result deleteFood(Long foodId);
}
