package com.aks.cateringinfosys.controller;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.Comment;
import com.aks.cateringinfosys.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 13:53
 * @packagename com.aks.cateringinfosys.controller
 * @classname CommentController
 * @description 评论的controller类
 */
@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    ICommentService commentService;
    @GetMapping("getRestComments/{rid}")
    public Result getRestComments(@PathVariable("rid") Integer rid) {
        return commentService.getRestComments(rid);
    }
    @PatchMapping("/likeComment/{cid}")
    public Result likeComment(@PathVariable("cid") Integer cid) {
        return commentService.likeComment(cid);
    }
    @PostMapping("/addComment")
    public Result addComment(@RequestBody Comment comment) {
        return commentService.addComment(comment);
    }
    @GetMapping("/self")
    public Result getSelfComment() {
        return commentService.getSelfComment();
    }
    @GetMapping("/delete/{cid}")
    public Result deleteCommentById(@PathVariable("cid") Long cid) {
        return commentService.deleteCommentById(cid);
    }
}
