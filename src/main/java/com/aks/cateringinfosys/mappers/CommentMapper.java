package com.aks.cateringinfosys.mappers;

import com.aks.cateringinfosys.entry.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 13:58
 * @packagename com.aks.cateringinfosys.mappers
 * @classname CommentMapper
 * @description
 */
@Component
@Mapper
public interface CommentMapper {
    //todo 根据餐饮店id查询评论列表
    List<Comment> queryCommentListByRestId(Integer rid);

    //todo 查询点赞表获取这个评论的点赞表中该用户是否点赞
    Integer queryLike(Integer cid, Long userId);

    // todo 删除该用户对此评论的点赞
    Integer deleteLike(Integer cid, Long userId);

    //todo 将该评论的点赞数减一
    void subLikeNum(Integer cid);
    //todo 此用户向该评论点赞
    Integer insertLike(long nextId, Integer cid, Long userId);
    //todo 将该评论的点赞数加一
    Integer addLike(Integer cid);

    //todo 添加评论
    Integer insertComment(Comment comment);

    //todo 根据用户id获取评论
    List<Comment> queryCommentListByUserId(Long id);

    //删除指定id的评论
    Integer deleteCommentById(Long cid);
}
