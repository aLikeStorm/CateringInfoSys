package com.aks.cateringinfosys.controller;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.SnappedCouponDTO;
import com.aks.cateringinfosys.entry.Coupon;
import com.aks.cateringinfosys.service.ICouponService;
import com.aks.cateringinfosys.utils.SystemConstants;
import com.aks.cateringinfosys.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.aks.cateringinfosys.utils.SystemConstants.*;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 17:21
 * @packagename com.aks.cateringinfosys.controller
 * @classname CouponController
 * @description
 */
@RestController
@RequestMapping("/coupon")
public class CouponController {
    @Autowired
    ICouponService couponService;
    @GetMapping("/getRestCoupon/{type}/{info}/{currentPage}/{pageSize}")
    public Result getRestCoupon(
            @PathVariable("type") Integer type,
            @PathVariable("info") String info,
            @PathVariable("currentPage")Integer currentPage,
            @PathVariable("pageSize")Integer pageSize) {
        Long restId = null;
        String restName = null;
        if (type == 1) {
            restId = new Long(info);
        } else {
            restName = info;
        }
        return couponService.getRestCoupon(restId,restName,currentPage,pageSize);
    }
    @GetMapping("/delete/{couponId}")
    public Result deleteCoupon(@PathVariable("couponId")Long couponId) {
        if (!UserHolder.getUser().getUid().equals(ADMINID)){
            return Result.fail("权限不足");
        }
        return couponService.deleteCoupon(couponId);
    }
    @PostMapping("/update")
    public Result updateCoupon(@RequestBody Coupon coupon) {
        if (!UserHolder.getUser().getUid().equals(ADMINID)){
            return Result.fail("权限不足");
        }
        return couponService.updateCoupon(coupon);
    }
    @PostMapping("/add")
    public Result addCoupon(@RequestBody Coupon coupon){
        if (!UserHolder.getUser().getUid().equals(ADMINID)){
            return Result.fail("权限不足");
        }
        return couponService.addCoupon(coupon);
    }
    @PostMapping("/snappedCoupon")
    public Result snappedCoupon(@RequestBody Long cid) {
        return couponService.snappedCoupon(cid);
    }
    @GetMapping("/getMyCoupons")
    public Result getMyCoupons() {
        return couponService.getMyCoupons();
    }

}
