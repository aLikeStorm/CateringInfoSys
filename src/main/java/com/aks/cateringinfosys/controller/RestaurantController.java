package com.aks.cateringinfosys.controller;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.service.IRestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 12:23
 * @packagename com.aks.cateringinfosys.controller
 * @classname RestaurantController
 * @description 餐饮点controller页面
 */
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    Logger logger = LoggerFactory.getLogger(RestController.class);
    @Autowired
    IRestaurantService restaurantService;
    @GetMapping("/{cityCode}")
    public Result getRestaurantsFromCity(@PathVariable("cityCode")Integer cityCode){
        return restaurantService.getRestaurantsFromCity(cityCode);
    }
    @GetMapping("/{cityCode}/{typeCode}/rName")
    public Result getRestaurantListFromName(
            @PathVariable("cityCode") Integer cityCode,
            @PathVariable("typeCode") Integer typeCode,
            @PathVariable("rName") String rName
            ) {
        return restaurantService.getRestaurantListFromName(cityCode,typeCode,rName);
    }
    @GetMapping("/getDetail/{rid}")
    public Result getRestaurantById(@PathVariable("rid") Long rid) {
        return restaurantService.getRestaurantById(rid);
    }
}
