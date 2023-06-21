package com.aks.cateringinfosys.service;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.SnappedCouponDTO;
import com.aks.cateringinfosys.entry.Coupon;
import com.aks.cateringinfosys.service.impl.CouponServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 17:24
 * @packagename com.aks.cateringinfosys.service
 * @classname ICouponService
 * @description
 */
public interface ICouponService {
    Result getRestCoupon(Long restId,String restName,Integer currentPage,Integer pageSize);

    Result snappedCoupon(Long cid);

    Result getMyCoupons();

    Result updateCoupon(Coupon coupon);

    Result addCoupon(Coupon coupon);

    Result deleteCoupon(Long couponId);
}
