package com.aks.cateringinfosys.controller;

import com.aks.cateringinfosys.dto.LoginEmailDTO;
import com.aks.cateringinfosys.dto.LoginFormDTO;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.UserDTO;
import com.aks.cateringinfosys.entry.User;
import com.aks.cateringinfosys.service.IUserService;
import com.aks.cateringinfosys.utils.RedisConstants;
import com.aks.cateringinfosys.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 15:45
 * @packagename com.aks.cateringinfosys.controller
 * @classname UseController
 * @description
 */
@RestController
@RequestMapping("/user")
public class UseController {
    @Autowired
    IUserService userService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @GetMapping
    public Result getUser() {
        return Result.ok(UserHolder.getUser());
    }
    @PostMapping("/update")
    public Result editUser(@RequestBody UserDTO userDTO) {
        return userService.editUser(userDTO);
    }

    @PostMapping("/login/admin")
    public Result loginAdmin(@RequestBody LoginFormDTO loginFormDTO) {
        return userService.loginAdmin(loginFormDTO);
    }

    @PostMapping("/login/account")
    public Result loginByUserName(@RequestBody LoginFormDTO loginFrom) {
        return userService.loginByUserName(loginFrom);
    }
    @PostMapping("/login/email")
    public Result loginSendCode(@RequestBody String email) {
        return userService.sendCode(email);
    }
    @PostMapping("login/code")
    public Result loginEmail(@RequestBody LoginEmailDTO loginEmailDTO) {
        return userService.loginByEmail(loginEmailDTO);
    }
    @PostMapping("/register")
    public Result register(User user) {
        return userService.register(user);
    }
    @GetMapping("/logout")
    public Result logout() {
        Boolean delete = redisTemplate.delete(RedisConstants.LOGIN_USER_KEY + ":" + UserHolder.getUser().getUid());
        if (delete) {
            return Result.ok("登出成功");
        }else {
            return Result.fail("登出失败，可能未登录");
        }
    }

}
