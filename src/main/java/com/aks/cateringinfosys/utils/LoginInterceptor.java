package com.aks.cateringinfosys.utils;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author 安克松
 * @Date 2022/11/15 10:50
 * @PackageName com.aks.utils
 * @ClassName LoginInterceptor
 * @Description
 * @Version 1.0.0
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //todo 判断是否需要拦截,如果刷新拦截器中没有取到User，那么就不然这些通过
        if (UserHolder.getUser() == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
