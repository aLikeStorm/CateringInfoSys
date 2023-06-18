package com.aks.cateringinfosys.service;

import com.aks.cateringinfosys.dto.LoginEmailDTO;
import com.aks.cateringinfosys.dto.LoginFormDTO;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.UserDTO;
import com.aks.cateringinfosys.entry.User;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 16:12
 * @packagename com.aks.cateringinfosys.service
 * @classname IUserService
 * @description
 */
public interface IUserService {
    Result editUser(User user);

    Result loginByPassword(LoginFormDTO loginFrom);

    Result sendCode(String email);

    Result loginByEmail(LoginEmailDTO loginEmailDTO);

    Result register(User user);

    Result loginAdmin(LoginFormDTO loginFormDTO);

    Result getUserList(String info, Integer currentPage, Integer pageSize);

    Result deleteUser(Long userId);
}
