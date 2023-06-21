package com.aks.cateringinfosys.mappers;

import com.aks.cateringinfosys.entry.Food;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/18 11:48
 * @packagename com.aks.cateringinfosys.mappers
 * @classname FoodMapper
 * @description
 */
@Mapper
@Component
public interface FoodMapper {
    public List<Food> queryFoodList(Long restId, String restName);

    Integer insertFood(Food food);

    Integer updateFood(Food food);

    @Select("SELECT * FROM TB_FOOD WHERE FOODID = #{foodId}")
    Food queryFoodById(Long foodId);
    @Delete("DELETE FROM TB_FOOD WHERE FOODID = #{foodId}")
    Integer deleteFoodById(Long foodId);
}
