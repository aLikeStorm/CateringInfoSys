package com.aks.cateringinfosys.service.impl;

import cn.hutool.json.JSONUtil;
import com.aks.cateringinfosys.dto.CouponDTO;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.UserDTO;
import com.aks.cateringinfosys.entry.City;
import com.aks.cateringinfosys.entry.Coupon;
import com.aks.cateringinfosys.entry.Restaurant;
import com.aks.cateringinfosys.mappers.CouponMapper;
import com.aks.cateringinfosys.mappers.RestaurantMapper;
import com.aks.cateringinfosys.service.ICouponService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.aks.cateringinfosys.utils.RedisConstants.*;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 17:24
 * @packagename com.aks.cateringinfosys.service.impl
 * @classname CouponServiceImpl
 * @description
 */
@Service
public class CouponServiceImpl implements ICouponService {
    public static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);
    @Autowired
    CouponMapper couponMapper;
    @Autowired
    RestaurantMapper restaurantMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedisIdWorker idWorker;
    @Override
    public Result getRestCoupon(Long restId,String restName,Integer currentPage,Integer pageSize) {
        if (restId != null){
            String couponStr = redisTemplate.opsForValue().get(CACHE_COUPON_KEY + restId);
            if (couponStr != null && couponStr != "" && !CACHE_NULL.equals(couponStr)){
                List<Coupon> list = JSONUtil.toBean(couponStr, List.class);
                logger.info(UserHolder.getUser().getUid() + "查询店铺"+restId+"优惠卷获得"+list);
                return Result.ok(list);
            }
            if (CACHE_NULL.equals(couponStr)) {
                logger.info(UserHolder.getUser().getUid() + "查询店铺"+restId+"优惠卷为空");
                return Result.fail("店铺没有优惠券");
            }
        }
        PageHelper.startPage(currentPage,pageSize);
        List<Coupon> couponList = couponMapper.queryCouponList(restId,restName);
        PageInfo<Coupon> couponPageInfo = new PageInfo<>(couponList);
        couponList = couponPageInfo.getList();
        if (couponList == null) {
            if (restId != null){
                redisTemplate.opsForValue().set(CACHE_COUPON_KEY + restId,
                        CACHE_NULL,CACHE_NULL_TTL,
                        TimeUnit.MINUTES);
            }
            logger.info(UserHolder.getUser().getUid() + "查询店铺优惠卷为空");
            return Result.fail("店铺没有优惠券");
        }
        restId = couponList.get(0).getCouRestId();
        redisTemplate.opsForValue().set(CACHE_COUPON_KEY + restId,
                JSONUtil.toJsonStr(couponList),
                CACHE_COUPON_TTL,
                TimeUnit.MINUTES);
        logger.info(UserHolder.getUser().getUid() + "查询店铺"+restId+"优惠卷获得"+couponList);
        return Result.ok(couponList);
    }

    @Override
    @Transactional
    public Result snappedCoupon(Long cid) {
        UserDTO user = UserHolder.getUser();
        if (user == null ) {
            return Result.fail("未登录");
        }
        Long orderId = couponMapper.queryOrderByUidAndCid(user.getUid(),cid);
        if (orderId != null) {
            return Result.fail("您已经有此优惠啦，请前往个人中心使用该优惠卷");
        }
        Coupon coupon = null;
        String couponStr = redisTemplate.opsForValue().get(CACHE_COUPON_KEY + "cid");
        if (couponStr == null || couponStr == "" || CACHE_NULL.equals(couponStr)){
            coupon = couponMapper.queryCouponByCid(cid);

        }else {
            coupon = JSONUtil.toBean(couponStr, Coupon.class);

        }
        if (coupon == null) {
            logger.info(UserHolder.getUser().getUid() + "查询优惠卷不存在");
            return Result.fail("优惠券已经消失啦");
        }
        LocalDateTime cEndDate = coupon.getEndTime();
        LocalDateTime cStartDate = coupon.getBeginTime();
        // 活动已经截止
        if (cEndDate.isBefore(LocalDateTime.now())) {
            return Result.fail("优惠卷已经过期");
        }
        if (cStartDate.isAfter(LocalDateTime.now())) {
            return Result.fail("活动已经截止");
        }
        if (coupon.getCouponId() <= 0) {
            return Result.fail("优惠卷已经抢光啦");
        }
        // todo 一人一张优惠卷一把锁
        String key = SNAPPED_LOCK + cid;
        boolean lock = tryLock(key, 6L);
        if (!lock) {
            return Result.fail("不允许使用抢票程序进行抢票");
        }
        //todo 获取得到锁，进行数据更新
        try {
            Integer flag = couponMapper.insertOrder(cid,user.getUid());
            if (flag != 1) {
                throw new RuntimeException("抢购优惠卷失败");
            }
            return Result.ok("抢购优惠卷成功");

        } finally {
            unLock(key);
        }
    }

    @Override
    public Result getMyCoupons() {
        Long uid = UserHolder.getUser().getUid();
        if (uid == null) {
            return Result.fail("未登录");
        }
        List<Coupon> coupons = couponMapper.queryCouponByUid(uid);
        if (coupons == null) {
            return Result.fail("您还没有优惠卷哟，快去抢购试试");
        }
        ArrayList<CouponDTO> couponDTOS = new ArrayList<>();
        coupons.stream().forEach(coupon -> {
            City city = restaurantMapper.queryRestAddress(coupon.getCouRestId());
            Restaurant restaurant = restaurantMapper.queryRestById(coupon.getCouRestId());
            String address = null;
            if (! city.getCityName().equals(city.getProvinceName())) {
                address = city.getProvinceName() + " " +city.getCityName();
            }else {
                address = city.getCityName();
            }
        });
        return Result.ok("couponDTOS");
    }

    @Override
    public Result updateCoupon(Coupon coupon) {
        if (coupon.getCouponId() == null
                || coupon.getCouRestId() == null
                || coupon.getBeginTime().isAfter(coupon.getEndTime())
                || coupon.getCouponAmount() == null
                || coupon.getCouponAmount() <= 0
                || coupon.getCouponNum() == null
                || coupon.getCouponNum() <= 0
        ){
            return Result.fail("优惠卷信息错误");
        }

        Integer flag = couponMapper.updateCoupon(coupon);
        if (flag != 1) {
            return Result.fail("修改优惠卷信息失败");
        }
        Long couRestId = coupon.getCouRestId();
        redisTemplate.delete(CACHE_COUPON_KEY+couRestId);
        return Result.ok("修改优惠卷信息成功");
    }

    @Override
    public Result addCoupon(Coupon coupon) {
        if (coupon.getCouRestId() == null) {
            return Result.fail("优惠卷没有对应的餐饮店ID");
        }
        if (coupon.getCouponNum() == null ||
        coupon.getCouponNum() <= 0 ||
        coupon.getCouponAmount() == null||
        coupon.getCouponAmount() <= 0){
            return Result.fail("优惠卷数量或优惠卷金额错误");
        }
        if (coupon.getBeginTime().isAfter(coupon.getEndTime())) {
            return Result.fail("优惠卷活动开启时间不能晚于活动截止时间");
        }
        coupon.setCreateTime(LocalDateTime.now());
        coupon.setCouponId(idWorker.nextId());
        Integer flag = couponMapper.insertCoupon(coupon);
        if (flag != 1) {
            return Result.fail("添加优惠卷失败");
        }
        redisTemplate.delete(CACHE_COUPON_KEY+coupon.getCouRestId());
        return Result.ok("添加优惠卷成功");
    }

    @Override
    public Result deleteCoupon(Long couponId) {
        Coupon coupon = couponMapper.queryCouponByCid(couponId);
        if (coupon == null){
            return Result.fail("优惠卷不存在啦");
        }
        Integer flag = couponMapper.deleteCouponById(couponId);
        if (flag != 0) {
            return Result.fail("删除优惠卷失败");
        }
        redisTemplate.delete(CACHE_COUPON_KEY+coupon.getCouRestId());
        return Result.ok("删除优惠卷成功");
    }

    String value = Thread.currentThread().getName() + "正在抢该优惠券";
    private boolean tryLock(String key,Long time) {
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
        return lock;
    }
    private void unLock(String key) {
        String s = redisTemplate.opsForValue().get(key);
        if (value.equals(s) )
        redisTemplate.delete(key);
    }

}
