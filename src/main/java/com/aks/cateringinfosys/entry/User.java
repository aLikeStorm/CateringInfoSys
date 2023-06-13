package com.aks.cateringinfosys.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 16:24
 * @packagename com.aks.cateringinfosys.entry
 * @classname User
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long uid;
    private String username;
    private String password;
    private String email;
    private String desc;
}
