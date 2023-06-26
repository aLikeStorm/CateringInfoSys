package com.aks.cateringinfosys.service.impl;

import cn.hutool.json.JSONUtil;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.entry.Comment;
import com.aks.cateringinfosys.entry.User;
import com.aks.cateringinfosys.mappers.CommentMapper;
import com.aks.cateringinfosys.mappers.ImageMapper;
import com.aks.cateringinfosys.mappers.UserMapper;
import com.aks.cateringinfosys.service.ICommentService;
import com.aks.cateringinfosys.utils.RedisIdWorker;
import com.aks.cateringinfosys.utils.UserHolder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    @Autowired
    UserMapper userMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;


    /**
     * 根据店铺id获取评论
     * @param rid
     * @return
     */
    @Override
    public Result getRestComments(Long rid) {
        // todo 在redis中获取指定店铺的评论列表
        String commentStr = stringRedisTemplate.opsForValue().get(CACHE_COMMENTLIST_KEY + rid);
        // todo 判断评论为空字符串或者为设置的 is null
        if (commentStr != null && !CACHE_NULL.equals(commentStr)) { // todo 非空则输出
            List<Comment> commentList = JSONUtil.toList(commentStr, Comment.class);
            logger.info(UserHolder.getUser().getUid() + "获取店铺"+ rid + "评论"+commentList);
            return Result.ok(commentList);
        }
        // todo 为空返回前端为空
        if (CACHE_NULL.equals(commentStr)) {
            logger.info(UserHolder.getUser().getUid() + "获取店铺"+ rid + "评论为空");
            return Result.fail("用户评论为空");
        }
        List<Comment> commentList = commentMapper.queryCommentListByRestId(rid);
        if(commentList == null || commentList.size() == 0) {
            stringRedisTemplate.opsForValue().set(CACHE_COMMENTLIST_KEY +rid,
                    CACHE_NULL,
                    CACHE_NULL_TTL,
                    TimeUnit.MINUTES);
            logger.info(UserHolder.getUser().getUid() + "获取店铺"+ rid + "评论为空");
            return Result.fail("用户评论为空");
        }
        // todo 流操作
        List<Object> collect = commentList.
                stream().
                map(comment -> {
                    String nickName = userMapper.queryNameByUid(comment.getUid());
                    List<String> imageList = imageMapper.queryImageListByForeign(comment.getCid());
                    comment.setImageList(imageList);
                    comment.setNickName(nickName);
                    return null;
                }).collect(Collectors.toList());
        stringRedisTemplate.opsForValue().set(CACHE_COMMENTLIST_KEY+rid,
                JSONUtil.toJsonStr(collect),
                CACHE_COMMENTLIST_TTL,
                TimeUnit.MINUTES);

        logger.info(UserHolder.getUser().getUid() + "获取店铺"+ rid + "评论"+commentList);
        return Result.ok(collect);
    }

    /**
     * 对指定评论点赞，由于点赞的数量对于数据的要求并不严格，并不需要进行严格的数据一致性要求，
     * 无需删除redis中的数据，采用过期后自动更新
     * @param cid
     * @return
     */
    @Transactional
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

    /**
     * 为指定店铺添加评论
     * @param comment
     * @return
     */
    @Transactional
    @Override
    public Result addComment(Comment comment) {
        Long useId = UserHolder.getUser().getUid();
        comment.setUid(useId);
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

    /**
     * 获取自己的所有评论
     * @return
     */
    @Override
    public Result getSelfComment() {
        Long id = UserHolder.getUser().getUid();
        String s = stringRedisTemplate.opsForValue().get(CACHE_COMMENTLIST_KEY + id);
        if (s != null && s != "" && !s.equals(CACHE_NULL)) {
            List<Comment> commentList = JSONUtil.toBean(s, List.class);
            logger.info("用户"+id+"获取个人评论" + s);
            return Result.ok(commentList);
        }
        if (CACHE_NULL.equals(s)) {
            logger.info("用户"+id+"获取个人评论为空");
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
        //流操作获取所有的评论照片
        List<Object> collect = commentList.
                stream().
                map(comment -> {
                    List<String> imageList = imageMapper.queryImageListByForeign(comment.getCid());
                    comment.setImageList(imageList);
                    return null;
                }).collect(Collectors.toList());
        stringRedisTemplate.opsForValue().set(CACHE_COMMENTLIST_KEY+id,
                JSONUtil.toJsonStr(collect),
                CACHE_COMMENTLIST_TTL,
                TimeUnit.MINUTES);

        logger.info("用户"+id+"获取个人评论" + collect);
        return Result.ok(collect);
    }

    /**
     * 删除指定id的评论
     * @param cid
     * @return
     */
    @Override
    @Transactional
    public Result deleteCommentById(Long cid) {
        Comment comment = commentMapper.queryCommentByCid(cid);
        if (comment == null ) {
            return Result.fail("评论已经不存在啦");
        }
        Integer flag = commentMapper.deleteCommentById(cid);
        if (flag != 1) {
            logger.info("用户" + UserHolder.getUser().getUid()+ "删除一条评论" + cid+"失败");
            throw new RuntimeException("删除评论失败，请重试");
        }
        logger.info("用户" + UserHolder.getUser().getUid()+ "删除一条评论" + cid);
        stringRedisTemplate.delete(CACHE_COMMENTLIST_KEY+comment.getUid());
        stringRedisTemplate.delete(CACHE_COMMENTLIST_KEY+comment.getRid());
        stringRedisTemplate.delete(CACHE_COMMENTLIST_KEY + UserHolder.getUser().getUid());
        return Result.ok("删除评论成功");
    }

    @Override
    public Result getCommentList(Integer type, Long id, Integer currentPage, Integer pageSize) {
        String key = String.valueOf(currentPage << 10 + pageSize);
        String commentStr = (String) stringRedisTemplate.opsForHash().get(CACHE_COMMENTLIST_KEY + id, key);
        if (commentStr != null && !CACHE_NULL.equals(commentStr)) {
            List<Comment> comments = JSONUtil.toList(commentStr, Comment.class);
            return Result.ok(comments);
        }
        if (CACHE_NULL.equals(commentStr)) {
            return Result.fail("查询的信息为空");
        }
        List<Comment> comments = null;

        PageHelper.startPage(currentPage,pageSize);
        PageInfo<Comment> pageInfo = null;
        if (type == 1) {
            comments = commentMapper.queryCommentListByUserId(id);
            pageInfo = new PageInfo<>(comments);
        } else if (type == 2) {
            comments = commentMapper.queryCommentListByRestId(id);
            pageInfo = new PageInfo<>(comments);
        } else {
            comments = commentMapper.queryCommentListAll();
            pageInfo = new PageInfo<>(comments);
        }
        comments = pageInfo.getList();
        if (comments == null || comments.size() == 0) {
            stringRedisTemplate.opsForHash().put(CACHE_COMMENTLIST_KEY+id,key,CACHE_NULL);
            stringRedisTemplate.expire(CACHE_COMMENTLIST_KEY+id,CACHE_NULL_TTL,TimeUnit.MINUTES);
            return Result.fail("查询的评论为空");
        }
        comments.stream().map(comment -> {
            Long uid = comment.getUid();
            User user = userMapper.queryUserByUserId(uid);
            if (user != null) {
                comment.setNickName(user.getNickName());
            }
            return null;
        }).collect(Collectors.toList());
        stringRedisTemplate.opsForHash().put(CACHE_COMMENTLIST_KEY+id,key,JSONUtil.toJsonStr(comments));
        stringRedisTemplate.expire(CACHE_COMMENTLIST_KEY+id,CACHE_COMMENTLIST_TTL,TimeUnit.MINUTES);

        return Result.ok(comments);
    }

}
