package com.aks.cateringinfosys.utils;

import cn.hutool.core.bean.BeanUtil;
import com.aks.cateringinfosys.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author 安克松
 * @Date 2022/11/15 10:50
 * @PackageName com.aks.utils
 * @ClassName LoginInterceptor
 * @Description
 * @Version 1.0.0
 */
public class RefreshInterceptor implements HandlerInterceptor {
    //这个StringRedisTemlate配置这个对象的MVCConfig来进行传入
    private StringRedisTemplate stringRedisTemplate;

    public RefreshInterceptor(final StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取session
        //HttpSession session = request.getSession();
        // todo 获取token，token在请求头中
        String token = request.getHeader("authorization");
        if (token == null) {
            UserHolder.saveUser(new UserDTO(0l,"游客","",""));
            return true;
        }
        String key = RedisConstants.LOGIN_USER_KEY+token;
        //2.获取用户
        //Object user = session.getAttribute("user");
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
        //3.判断用户是否存在，存在方向
        if (map == null){
            UserHolder.saveUser(new UserDTO(0l,"游客","",""));
            return true;
        }
        //强转为一个不存密码等详细信息的UserDTO存入ThreadLocal线程域中
        UserHolder.saveUser(BeanUtil.fillBeanWithMap(map,new UserDTO(),false));
        // todo 每次请求就代表还在使用，再次刷新token的时间
        stringRedisTemplate.expire(key,RedisConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //当前会话结束，移除user
        UserHolder.removeUser();
    }
}
