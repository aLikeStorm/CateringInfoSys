package com.aks.cateringinfosys.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 14:04
 * @packagename com.aks.cateringinfosys.entry
 * @classname Comment
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long cid;
    private String comment; // 评论文字部分
    private LocalDateTime createTime; // 评论时间
    private List<String> imageList; //评论的图片文件列表
    private Long rid; //评论的店铺id
    private Long uid; // 评论用户
    private Integer like; // 点赞人数
}
