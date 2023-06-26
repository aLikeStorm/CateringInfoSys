package com.aks.cateringinfosys.controller;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.Comment;
import com.aks.cateringinfosys.service.ICommentService;
import com.aks.cateringinfosys.utils.SystemConstants;
import com.aks.cateringinfosys.utils.UserHolder;
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
@CrossOrigin
public class CommentController {
    @Autowired
    ICommentService commentService;
    @GetMapping("/getRestComments/{rid}")
    public Result getRestComments(@PathVariable("rid") Long rid) {
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
    @GetMapping("/getCommentList/{type}/{id}/{currentPage}/{pageSize}")
    public Result getCommentList(@PathVariable("type")Integer type,
                                 @PathVariable("id")Long id,
                                 @PathVariable("currentPage")Integer currentPage,
                                 @PathVariable("pageSize")Integer pageSize){
        // todo 若根据用户id查询，但是并不是自己的id，不是管理员则没有权限
        if (type == 1
                && !UserHolder.getUser().getUid().equals(id)
                && !UserHolder.getUser().equals(SystemConstants.ADMINID)) {
            return Result.fail("权限不足");
        }
        return commentService.getCommentList(type,id,currentPage,pageSize);
    }
}
