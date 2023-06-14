package com.aks.cateringinfosys.config;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.utils.UserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/14 17:13
 * @packagename com.aks.cateringinfosys.config
 * @classname WebExceptionConfig
 * @description  服务器异常处理
 */
@RestControllerAdvice
public class WebExceptionConfig {
    public static final Logger logger = LoggerFactory.getLogger(WebExceptionConfig.class);
    @ExceptionHandler(RuntimeException.class)
    public Result handlerRuntimeException(RuntimeException e) {
        logger.error(UserHolder.getUser().getUid() + "发生异常,异常信息: "+e.getMessage());
        return Result.fail(e.getMessage());
    }
}
