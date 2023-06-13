package com.aks.cateringinfosys.mappers;

import org.apache.ibatis.annotations.Mapper;
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
    List<String> queryImageListByForeign(Long foreign);

    Integer insertImage(long id, String imageName, long foreign);
}
