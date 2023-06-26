package com.aks.cateringinfosys.mappers;

import com.aks.cateringinfosys.entry.City;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/12 12:31
 * @packagename com.aks.cateringinfosys.mappers
 * @classname CityMapper
 * @description
 */
@Component
@Mapper
public interface CityMapper {
    @Select("SELECT * FROM tb_city")
    List<City> queryCityList();
    Integer queryCityCodeByCityName(@Param("cityName") String cityName);

    City queryCityByCityName(String cityName);
}
