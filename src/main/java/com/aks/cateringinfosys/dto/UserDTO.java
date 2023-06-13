package com.aks.cateringinfosys.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long uid;
    private String username;
    private String email;
    private String desc;
}
