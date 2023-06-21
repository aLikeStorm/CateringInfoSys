package com.aks.cateringinfosys.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 18:01
 * @packagename com.aks.cateringinfosys.entry
 * @classname Coupon
 * @description 优惠卷
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    private Long couponId;
    private Long couRestId; //店铺id
    private String couponName;
    private Float couponAmount;
    private Integer couponNum;
    private Integer couponRemainingNum;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;

}
