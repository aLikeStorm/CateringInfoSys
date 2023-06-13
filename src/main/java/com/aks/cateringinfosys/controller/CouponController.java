package com.aks.cateringinfosys.controller;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.SnappedCouponDTO;
import com.aks.cateringinfosys.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/getRestCoupon/{rid}")
    public Result getRestCoupon(@PathVariable("rid") Long rid) {
        return couponService.getRestCoupon(rid);
    }
    // 抢购优惠卷
    @PostMapping("/snappedCoupon")
    public Result snappedCoupon(@RequestBody Long cid) {
        return couponService.snappedCoupon(cid);
    }
    @GetMapping("/getMyCoupons")
    public Result getMyCoupons() {
        return couponService.getMyCoupons();
    }

}
