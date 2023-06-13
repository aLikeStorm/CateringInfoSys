package com.aks.cateringinfosys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 16:56
 * @packagename com.aks.cateringinfosys.dto
 * @classname LoginEmailDTO
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginEmailDTO {
    private String email;
    private String code;
}
