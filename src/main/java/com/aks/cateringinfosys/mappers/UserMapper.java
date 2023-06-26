package com.aks.cateringinfosys.mappers;

import com.aks.cateringinfosys.dto.UserDTO;
import com.aks.cateringinfosys.entry.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

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
    Integer updateUser(User user);

    //todo 根据用户名查询用户
    User queryUserByUserName(String username);

    //todo 根据邮箱查询用户
    User queryUserByEmail(String email);

    //根据uid和密码的比较查询用户
    User queryLogin(Long uid, String password);

    //todo 添加用户
    Integer inertUser(User user);

    List<User> queryUserList(String info);

    @Select("SELECT * FROM TB_USER WHERE USERID = #{userId}")
    User queryUserByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM TB_USER WHERE USERID = #{userId}")
    Integer deleteUserById(Long userId);

    @Select("SELECT NICKNAME FROM TB_USER WHERE USERID=#{uid}")
    String queryNameByUid(Long uid);
}
