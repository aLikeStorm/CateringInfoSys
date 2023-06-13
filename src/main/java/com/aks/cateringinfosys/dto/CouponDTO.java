package com.aks.cateringinfosys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 19:14
 * @packagename com.aks.cateringinfosys.dto
 * @classname Coupon
 * @description 优惠卷的包装类 没有优惠券数量与店铺id，店铺名和店铺位置
 */
@Data
@AllArgsConstructor
public class CouponDTO {
    private Long cid;
    private String restName; //店铺id
    private String cDescribe;
    private Float cMoney;
    private String restAddress;
    private LocalDateTime cStartDate;
    private LocalDateTime cEndDate;
}
