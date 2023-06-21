package com.aks.cateringinfosys.entry;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/18 20:35
 * @packagename com.aks.cateringinfosys.entry
 * @classname Order
 * @description
 */
@Data
public class Order {
    private Long couponOrder;
    private Long OrderCouId;
    private Long OrderUserId;
    private String RestName;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private String couponName;
}
