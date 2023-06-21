package com.aks.cateringinfosys.service;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.SnappedCouponDTO;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/19 17:17
 * @packagename com.aks.cateringinfosys.service
 * @classname IOrderService
 * @description
 */
public interface IOrderService {
    Result getOrderList(Integer type, Long id,Integer currentPage,Integer pageSize);

    Result snappedCoupon(SnappedCouponDTO snappedCouponDTO);

    Result getDetail(Long orderId);

    Result deleteOrder(Long orderId);
}
