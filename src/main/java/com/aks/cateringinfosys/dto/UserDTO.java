package com.aks.cateringinfosys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long uid;
    private String nickName;
    private String email;
    private String address;
}
