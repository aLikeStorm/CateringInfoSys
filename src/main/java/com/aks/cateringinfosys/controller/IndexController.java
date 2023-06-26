package com.aks.cateringinfosys.controller;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.City;
import com.aks.cateringinfosys.mappers.CityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 10:04
 * @packagename com.aks.cateringinfosys
 * @classname IndexController
 * @description
 */
@RestController
@RequestMapping("/get")
@CrossOrigin
public class IndexController {
    Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    CityMapper cityMapper;
    @GetMapping("/getCity/{cityName}")
    public Result getCityByCityName(@PathVariable("cityName")String cityName) {
        City city = cityMapper.queryCityByCityName(cityName);
        if (city == null ) {
            return Result.fail("查询城市信息为空");
        }
        return Result.ok(city);
    }
}
