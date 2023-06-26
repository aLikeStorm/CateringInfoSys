package com.aks.cateringinfosys.controller;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.Food;
import com.aks.cateringinfosys.entry.User;
import com.aks.cateringinfosys.service.IFoodService;
import com.aks.cateringinfosys.utils.SystemConstants;
import com.aks.cateringinfosys.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.aks.cateringinfosys.utils.SystemConstants.ADMINID;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/18 11:28
 * @packagename com.aks.cateringinfosys.controller
 * @classname FoodController
 * @description
 */
@RestController
@RequestMapping("/food")
@CrossOrigin
public class FoodController {
    @Autowired
    IFoodService foodService;
    @GetMapping("/getFoodList/{type}/{info}/{currentPage}/{pageSize}")
    public Result getFoodList(@PathVariable("type")Integer type,
                              @PathVariable("info") String info,
                              @PathVariable("currentPage")Integer currentPage,
                              @PathVariable("pageSize")Integer pageSize){
        if (type < 1 || type > 2 || currentPage < 0 || pageSize < 0 ) {
            return Result.fail("url参数错误");
        }

        Long restId = null;
        String restName = null;
        if (type == 1){
            restId = new Long(info);
        } else {
            restName = info;
        }
        return foodService.getFoodList(restId,restName,currentPage,pageSize);
    }
    @PostMapping("/add")
    public Result addFood(@RequestBody Food food) {
        if (!UserHolder.getUser().getUid().equals(ADMINID)) {
            return Result.fail("权限不足");
        }
        if (food.getFoodFormRest() == null ){
            return Result.fail("店铺未明确");
        }
        return foodService.addFood(food);
    }
    @PostMapping("/update")
    public Result updateFood(@RequestBody Food food) {
        if (!UserHolder.getUser().getUid().equals(ADMINID)) {
            return Result.fail("权限不足");
        }
        if (food.getFoodFormRest() == null ){
            return Result.fail("店铺未明确");
        }
        return foodService.updateFood(food);
    }
    @GetMapping("/delete/{foodId}")
    public Result deleteFood(@PathVariable("foodId") Long foodId) {
        if (foodId == null || foodId.equals(0)) {
            return Result.fail("url参数错误");
        }
        if (!UserHolder.getUser().getUid().equals(ADMINID)) {
            return Result.fail("权限不足");
        }
        return foodService.deleteFood(foodId);
    }
}
