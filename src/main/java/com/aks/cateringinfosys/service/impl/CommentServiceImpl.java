package com.aks.cateringinfosys.service.impl;

import cn.hutool.json.JSONUtil;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.Comment;
import com.aks.cateringinfosys.mappers.CommentMapper;
import com.aks.cateringinfosys.mappers.ImageMapper;
import com.aks.cateringinfosys.service.ICommentService;
import com.aks.cateringinfosys.utils.RedisIdWorker;
import com.aks.cateringinfosys.utils.UserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.aks.cateringinfosys.utils.RedisConstants.*;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 13:54
 * @packagename com.aks.cateringinfosys.service.impl
 * @classname CommentServiceImpl
 * @description 评论的服务类
 */
@Service
public class CommentServiceImpl implements ICommentService {
    public static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    @Autowired
    RedisIdWorker idWorker;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    ImageMapper imageMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;


    @Override
    public Result getRestComments(Integer rid) {
        String commentStr = stringRedisTemplate.opsForValue().get(CACHE_COMMENTLIST_KEY + rid);
        if (commentStr != null && commentStr != null && !CACHE_NULL.equals(commentStr)) {
            List<Comment> commentList = JSONUtil.toBean(commentStr, List.class);
            logger.info(UserHolder.getUser().getUid() + "获取店铺"+ rid + "评论"+commentList);
            return Result.ok(commentList);
        }
        if (commentStr.equals(CACHE_NULL)) {
            logger.info(UserHolder.getUser().getUid() + "获取店铺"+ rid + "评论为空");
            return Result.fail("用户评论为空");
        }
        List<Comment> commentList = commentMapper.queryCommentListByRestId(rid);
        if(commentList == null) {
            stringRedisTemplate.opsForValue().set(CACHE_COMMENTLIST_KEY +rid,
                    CACHE_NULL,
                    CACHE_NULL_TTL,
                    TimeUnit.MINUTES);
            logger.info(UserHolder.getUser().getUid() + "获取店铺"+ rid + "评论为空");
            return Result.fail("用户评论为空");
        }
        commentList.
                stream().
                map(comment -> {
                    List<String> imageList = imageMapper.queryImageListByForeign(comment.getCid());
                    comment.setImageList(imageList);
                    return null;
                }).collect(Collectors.toList());
        stringRedisTemplate.opsForValue().set(CACHE_COMMENTLIST_KEY+rid,
                JSONUtil.toJsonStr(commentList),
                CACHE_COMMENTLIST_TTL,
                TimeUnit.MINUTES);

        logger.info(UserHolder.getUser().getUid() + "获取店铺"+ rid + "评论"+commentList);
        return Result.ok(commentList);
    }

    @Override
    public Result likeComment(Integer cid) {

        Long userId = UserHolder.getUser().getUid();

        Integer count = commentMapper.queryLike(cid,userId);
        if (count == 1) {
            logger.info("用户"+userId+"尝试对" +cid+"评论取消点赞");
            Integer flag = commentMapper.deleteLike(cid,userId);
            commentMapper.subLikeNum(cid);
            if (flag == 1) {
                return Result.ok("取消点赞成功");
            }else {
                return Result.fail("取消点赞失败");
            }
        } else {
            logger.info("用户"+userId+"尝试对" +cid+"评论点赞");
            Integer integer = commentMapper.insertLike(idWorker.nextId(), cid, userId);
            Integer integer1 = commentMapper.addLike(cid);
            if (integer1 == integer) {
                return Result.ok("点赞成功");
            }else {
                return Result.fail("点赞失败");
            }
        }
    }

    @Transactional
    @Override
    public Result addComment(Comment comment) {
        Long useId = UserHolder.getUser().getUid();
        comment.setUid(useId);
        comment.setLike(0);
        comment.setCreateTime(LocalDateTime.now());
        long cid = idWorker.nextId();
        comment.setCid(cid);
        Long restId = comment.getRid();
        try {
            Integer count = commentMapper.insertComment(comment);
            if (count != 1) {
                throw new RuntimeException("添加评论失败，请重试");
            }
            for (String s : comment.getImageList()) {
                Integer integer = imageMapper.insertImage(idWorker.nextId(), s, cid);
                if (integer != 1) {
                    throw new RuntimeException("添加评论失败，请重试");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String commentStr = stringRedisTemplate.opsForValue().get(CACHE_COMMENTLIST_KEY + restId);
        if (commentStr != null && commentStr != "") {
            stringRedisTemplate.delete(CACHE_COMMENTLIST_KEY + restId);
        }
        logger.info("用户"+useId+"向餐饮点"+restId+"添加一条评论"+comment);
        return Result.ok("添加评论成功");
    }

    @Override
    public Result getSelfComment() {
        Long id = UserHolder.getUser().getUid();
        String s = stringRedisTemplate.opsForValue().get(CACHE_COMMENTLIST_KEY + id);
        if (s != null && s != "" && !s.equals(CACHE_NULL)) {
            List<Comment> commentList = JSONUtil.toBean(s, List.class);
            logger.info("用户"+id+"获取个人评论" + s);
            return Result.ok(commentList);
        }
        if (s.equals(CACHE_NULL)) {
            logger.info("用户"+id+"获取个人评论失败");
            return Result.fail("你尚未评论,去评论试试吧");
        }
        List<Comment> commentList = commentMapper.queryCommentListByUserId(id);
        if(commentList == null) {
            stringRedisTemplate.opsForValue().set(CACHE_COMMENTLIST_KEY +id,
                    CACHE_NULL,
                    CACHE_NULL_TTL,
                    TimeUnit.MINUTES);
            logger.info("用户" + id + "获取评论为空");
            return Result.fail("你尚未评论,去评论试试吧");
        }
        commentList.
                stream().
                map(comment -> {
                    List<String> imageList = imageMapper.queryImageListByForeign(comment.getCid());
                    comment.setImageList(imageList);
                    return null;
                }).collect(Collectors.toList());
        stringRedisTemplate.opsForValue().set(CACHE_COMMENTLIST_KEY+id,
                JSONUtil.toJsonStr(commentList),
                CACHE_COMMENTLIST_TTL,
                TimeUnit.MINUTES);

        logger.info("用户"+id+"获取个人评论" + commentList);
        return Result.ok(commentList);
    }

    @Override
    @Transactional
    public Result deleteCommentById(Long cid) {
        Integer flag = commentMapper.deleteCommentById(cid);
        if (flag != 1) {
            logger.info("用户" + UserHolder.getUser().getUid()+ "删除一条评论" + cid+"失败");
            throw new RuntimeException("删除评论失败，请重试");
        }
        logger.info("用户" + UserHolder.getUser().getUid()+ "删除一条评论" + cid);
        stringRedisTemplate.delete(CACHE_COMMENTLIST_KEY + UserHolder.getUser().getUid());
        return Result.ok("删除评论成功");
    }

}
