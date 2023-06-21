package com.aks.cateringinfosys.entry;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/18 11:24
 * @packagename com.aks.cateringinfosys.entry
 * @classname Food
 * @description
 */
@Data
public class Food {
    private Long foodId;
    private String foodName;
    private String foodDescription;
    private Long foodFormRest;
    private Float foodPrice;
    private LocalDateTime createTime;
    private List<String> imageList;
}
