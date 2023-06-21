package com.aks.cateringinfosys.dto;

import com.aks.cateringinfosys.entry.Order;
import lombok.Data;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/19 20:39
 * @packagename com.aks.cateringinfosys.dto
 * @classname OrderDetailDTO
 * @description
 */
@Data
public class OrderDetailDTO extends Order {
    private Float couponAmount;
    private String restAddress;
    private String email;
    private String nickName;
    private String userAddress;
    private String restType;
    private String restDescription;
}
