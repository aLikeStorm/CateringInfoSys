package com.aks.cateringinfosys.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 12:38
 * @packagename com.aks.cateringinfosys.entry
 * @classname Restaurant
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {
    private Long rid;
    private String rName;
    private String rDescription;
    private String type;
    private String city;
    private LocalDateTime rCreateTime;
    private List<String> imageList;
}
