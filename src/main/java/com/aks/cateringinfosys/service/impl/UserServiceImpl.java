package com.aks.cateringinfosys.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.aks.cateringinfosys.dto.LoginEmailDTO;
import com.aks.cateringinfosys.dto.LoginFormDTO;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.UserDTO;
import com.aks.cateringinfosys.entry.User;
import com.aks.cateringinfosys.mappers.UserMapper;
import com.aks.cateringinfosys.service.IUserService;
import com.aks.cateringinfosys.utils.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.aks.cateringinfosys.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.aks.cateringinfosys.utils.RedisConstants.LOGIN_CODE_TTL;
import static com.aks.cateringinfosys.utils.SystemConstants.*;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 16:12
 * @packagename com.aks.cateringinfosys.service.impl
 * @classname UserServiceImpl
 * @description
 */
@Service
public class UserServiceImpl implements IUserService {
    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    UserMapper userMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedisIdWorker idWorker;
    @Override
    @Transactional
    public Result editUser(User user) {
        User userFind = userMapper.queryUserByUserId(user.getUserId());
        if (userFind == null) {
            return Result.fail("用户已经不存在");
        }
        user.setPassword(PasswordEncoder.encode(user.getPassword()));
        Integer integer = userMapper.updateUser(user);
        Long userId = UserHolder.getUser().getUid();
        if (integer != 1) {
            logger.error("用户"+ userId + "修改信息失败");
            return Result.fail("修改信息失败");
        }
        // 如果他登陆过，删除token让他重新登陆
        redisTemplate.delete(RedisConstants.LOGIN_USER_KEY +user.getUserId());
        logger.info("用户"+ userId + "修改信息成功"+user);
        return Result.ok("用户信息修改成功");
    }

    @Override
    public Result loginByPassword(LoginFormDTO loginFrom) {
        User user = userMapper.queryUserByEmail(loginFrom.getEmail());
        if (user == null) {
            logger.error(loginFrom+ "登陆失败,用户不存在");
            return Result.fail("用户不存在");
        }
        if (!PasswordEncoder.matches(user.getPassword(),loginFrom.getPassword())) {
            logger.error(loginFrom+ "登陆失败，密码错误");
            return Result.fail("密码错误");
        }
        // todo 生成随机token返回给浏览器，用做redis的key UUID不是java默认的UUID，是htool的，true为不需要下划线
        String token = UUID.randomUUID().toString(true);
        //注意敏感信息
        UserDTO userDTO = new UserDTO(user.getUserId(), user.getNickName(), user.getEmail(),user.getUserAddress());
        // todo 在类型转换时，不管什么类型都转化为字符串类型
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("uid",userDTO.getUid().toString());
        userMap.put("username",userDTO.getNickName());
        userMap.put("email",userDTO.getEmail());
        userMap.put("userAddress",userDTO.getAddress());
        String key = RedisConstants.LOGIN_USER_KEY+token;
        redisTemplate.opsForHash().putAll(key,userMap);
        // todo 设置有效期，但是只要用户不断访问就不断更新访问日期
        redisTemplate.expire(key,RedisConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
        // todo 给前端返回token
        return Result.ok(token);
    }

    @Override
    public Result sendCode(String email) {
        if (!RegexUtils.isEmailInvalid(email)){
            return Result.fail("邮件名不合法");
        }
        User user = userMapper.queryUserByEmail(email);
        if (user == null) {
            logger.error(email+ "登陆失败,用户不存在");
            return Result.fail("用户不存在");
        }
        String code = RandomUtil.randomNumbers(6);
        System.out.println(code);
        logger.error(email+ "发送验证码"+code);
        redisTemplate.opsForValue().set(LOGIN_CODE_KEY + email,code,LOGIN_CODE_TTL,TimeUnit.MINUTES);
        return Result.ok(code);
    }

    @Override
    public Result loginByEmail(LoginEmailDTO loginEmailDTO) {
        String s = redisTemplate.opsForValue().get(LOGIN_CODE_KEY + loginEmailDTO.getEmail());
        if (!loginEmailDTO.equals(s)) {
            return Result.fail("验证码错误");
        }
        User user = userMapper.queryUserByEmail(loginEmailDTO.getEmail());
        // todo 生成随机token返回给浏览器，用做redis的key UUID不是java默认的UUID，是htool的，true为不需要下划线
        String token = UUID.randomUUID().toString(true);
        //注意敏感信息
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // todo 在类型转换时，不管什么类型都转化为字符串类型
        // todo 在类型转换时，不管什么类型都转化为字符串类型
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("uid",userDTO.getUid().toString());
        userMap.put("nickName",userDTO.getNickName());
        userMap.put("email",userDTO.getEmail());
        userMap.put("address",userDTO.getAddress());
        String key = RedisConstants.LOGIN_USER_KEY+token;
        redisTemplate.opsForHash().putAll(key,userMap);
        // todo 设置有效期，但是只要用户不断访问就不断更新访问日期
        redisTemplate.expire(key,RedisConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
        logger.info("用户" +user.getUserId()+"登陆成功");
        // todo 给前端返回token
        return Result.ok(token);
    }

    @Override
    @Transactional
    public Result register(User user) {
        if (isInvalidUser(user)) {
            return Result.fail("注册失败,用户邮箱格式错误或密码为空");
        }
        user.setUserId(idWorker.nextId());
        //todo 对密码进行加密
        user.setPassword(PasswordEncoder.encode(user.getPassword()));
        Integer flag = userMapper.inertUser(user);
        if(flag != 1) {
            logger.error(user+"注册失败");
            return Result.fail("注册失败,可能用户名或邮箱已存在");
        }
        logger.info(user+"注册成功");
        return Result.ok("注册成功,登陆试试");
    }
    private boolean isInvalidUser(User user){
        if (!RegexUtils.isEmailInvalid(user.getEmail())) {
            return false;
        }
        if (user.getPassword() == null || user.getPassword().equals("")) {
            return false;
        }
        if (user.getNickName() == null) {
            return false;
        }
        return true;
    }

    @Override
    public Result loginAdmin(LoginFormDTO loginFormDTO) {

        User user = userMapper.queryUserByEmail(loginFormDTO.getEmail());
        if (user == null) {
            logger.error(loginFormDTO+ "登陆失败,用户不存在");
            return Result.fail("用户不存在");
        }
        if (user.getUserId() != ADMINID) {
            return Result.fail("非管理员无法登陆");
        }

        if (!PasswordEncoder.matches(user.getPassword(),loginFormDTO.getPassword())) {
            logger.error(loginFormDTO+ "登陆失败，密码错误");
            return Result.fail("密码错误");
        }

        // todo 生成随机token返回给浏览器，用做redis的key UUID不是java默认的UUID，是htool的，true为不需要下划线
        String token = UUID.randomUUID().toString(true);
        UserDTO userDTO = new UserDTO(user.getUserId(), user.getNickName(), user.getEmail(),user.getUserAddress());
        // todo 在类型转换时，不管什么类型都转化为字符串类型
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("uid",userDTO.getUid().toString());
        userMap.put("nickName",userDTO.getNickName());
        userMap.put("email",userDTO.getEmail());
        userMap.put("address",userDTO.getAddress());
        String key = RedisConstants.LOGIN_USER_KEY+token;
        redisTemplate.opsForHash().putAll(key,userMap);
        // todo 设置有效期，但是只要用户不断访问就不断更新访问日期
        redisTemplate.expire(key,RedisConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
        // todo 给前端返回token
        return Result.ok(token);
    }

    @Override
    public Result getUserList(String info, Integer currentPage, Integer pageSize) {
        if (UserHolder.getUser().getUid() != ADMINID) {
            return Result.fail("权限不足");
        }
        if (info.equals("默认")) {
            info = null;
        }
        PageHelper.startPage(currentPage,pageSize);
        List<User> userList = userMapper.queryUserList(info);
        PageInfo<User> userPageInfo = new PageInfo<>(userList, 3);
        return Result.ok(userPageInfo.getList(),userPageInfo.getTotal());
    }

    @Override
    @Transactional
    public Result deleteUser(Long userId) {
        Integer integer = userMapper.deleteUserById(userId);
        if (integer != 1) {
            return Result.fail("删除用户失败");
        }
        logger.info("用户" + UserHolder.getUser().getUid() + "删除用户"+userId);
        return Result.ok("删除用户成功");
    }
}
