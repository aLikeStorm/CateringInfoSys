package com.aks.cateringinfosys.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 10:04
 * @packagename com.aks.cateringinfosys
 * @classname IndexController
 * @description
 */
@RestController

public class IndexController {
    Logger logger = LoggerFactory.getLogger(IndexController.class);
    @GetMapping("/index")
    public String index() {
        logger.info("首页登陆");
        return "Hello World";
    }
}
