package com.aks.cateringinfosys.controller;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.UserDTO;
import com.aks.cateringinfosys.entry.Restaurant;
import com.aks.cateringinfosys.service.IRestaurantService;
import com.aks.cateringinfosys.utils.SystemConstants;
import com.aks.cateringinfosys.utils.UserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.aks.cateringinfosys.utils.SystemConstants.ADMINID;

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
@CrossOrigin
public class RestaurantController {
    Logger logger = LoggerFactory.getLogger(RestController.class);
    @Autowired
    IRestaurantService restaurantService;
    @GetMapping("/like/{restId}")
    public Result likeRestaurant(@PathVariable("restId")Long restId) {
        if (restId == null || restId.equals(0)) {
            return Result.fail("url参数错误");
        }

        return restaurantService.likeRestaurant(restId);
    }

    @GetMapping("/{cityName}")
    public Result getRestaurantsFromCity(@PathVariable("cityName")String cityName){
        if (cityName == null) {
            return Result.fail("url参数错误");
        }
        return restaurantService.getRestaurantsFromCity(cityName);
    }
    @GetMapping("/getRestType")
    public Result getRestType() {
        return restaurantService.getRestType();
    }
    @PostMapping("/add")
    public Result addRest(@RequestBody Restaurant restaurant) {
        if (!UserHolder.getUser().getUid().equals(ADMINID)) {
            return Result.fail("权限不足");
        }
        return restaurantService.addRest(restaurant);
    }
    @PostMapping("/delete")
    public Result deleteRest(@RequestBody Long restId) {
        if (UserHolder.getUser().getUid() != ADMINID) {
            return Result.fail("权限不足");
        }
        return restaurantService.deleteRest(restId);
    }
    @PostMapping("/update")
    public Result updateRest(@RequestBody Restaurant restaurant) {
        if (UserHolder.getUser().getUid() != ADMINID) {
            return Result.fail("权限不足");
        }
        return restaurantService.updateRest(restaurant);
    }
    @GetMapping("/findRestList/{cityCode}/{typeCode}/{rName}/{currentPage}/{pageSize}")
    public Result getRestaurantListFromName(
            @PathVariable("cityCode") Integer cityCode,
            @PathVariable("typeCode") Integer typeCode,
            @PathVariable("rName") String rName,
            @PathVariable("currentPage") Integer currentPage,
            @PathVariable("pageSize") Integer pagSize
            ) {
        if (cityCode == null || typeCode == null || currentPage < 1 || pagSize < 1) {
            return Result.fail("url参数错误");
        }
        if (cityCode == 1) {
            cityCode = null;
        }
        if (typeCode == 1) {
            typeCode = null;
        }
        if (rName.equals("all")) {
            rName = null;
        }
        return restaurantService.getRestaurantListFromName(cityCode,typeCode,rName,currentPage,pagSize);
    }
    @GetMapping("/getDetail/{rid}")
    public Result getRestaurantById(@PathVariable("rid") Long rid) {
        if (rid == null ){
            return Result.fail("url参数错误");
        }
        return restaurantService.getRestaurantById(rid);
    }
}
