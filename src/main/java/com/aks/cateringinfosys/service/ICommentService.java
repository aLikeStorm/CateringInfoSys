package com.aks.cateringinfosys.service;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.Comment;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 13:54
 * @packagename com.aks.cateringinfosys.service
 * @classname ICommentService
 * @description
 */
public interface ICommentService {
    Result getRestComments(Integer rid);

    Result likeComment(Integer cid);

    Result addComment(Comment comment);

    Result getSelfComment();

    Result deleteCommentById(Long cid);
}
