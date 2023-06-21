package com.aks.cateringinfosys.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 13:02
 * @packagename com.aks.cateringinfosys.mappers
 * @classname ImageMapper
 * @description
 */
@Component
@Mapper
public interface ImageMapper {
    @Select("SELECT IMAGENAME FROM TB_IMAGE WHERE FOREIGNID = #{foreign}")
    List<String> queryImageListByForeign(Long foreign);

    @Insert("INSERT INTO TB_IMAGE VALUES (#{id},#{imageName},#{foreign})")
    Integer insertImage(@Param("id") long id, @Param("imageName")String imageName,@Param("foreign") long foreign);

    @Select("DELETE FROM TB_IMAGE WHERE FOREIGNID = #{foreign}")
    void deleteByForeign(Long foreign);
}
