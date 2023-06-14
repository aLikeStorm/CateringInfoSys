package com.aks.cateringinfosys.config;

import com.aks.cateringinfosys.utils.LoginInterceptor;
import com.aks.cateringinfosys.utils.RefreshInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/14 8:29
 * @packagename com.aks.cateringinfosys.config
 * @classname MVCConfig
 * @description
 */
@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RefreshInterceptor(redisTemplate)).addPathPatterns("/**");
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns("/user/**",
                        "/restaurants/**",
                        "/file/**",
                        "/coupon/getRestCoupon/*",
                        "/comments/getRestComments/*");
    }
}
