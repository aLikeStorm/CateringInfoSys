package com.aks.cateringinfosys.mappers;

import com.aks.cateringinfosys.dto.UserDTO;
import com.aks.cateringinfosys.entry.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 16:14
 * @packagename com.aks.cateringinfosys.mappers
 * @classname UserMapper.xml
 * @description
 */
@Component
@Mapper
public interface UserMapper {
    //todo 修改用户信息
    Integer updateUser(UserDTO userDTO);

    //todo 根据用户名查询用户
    User queryUserByUserName(String username);

    //todo 根据邮箱查询用户
    User queryUserByEmail(String email);

    //根据uid和密码的比较查询用户
    User queryLogin(Long uid, String password);

    //todo 添加用户
    Integer inertUser(User user);
}
