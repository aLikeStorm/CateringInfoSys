package com.aks.cateringinfosys.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.aks.cateringinfosys.dto.OrderDetailDTO;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.SnappedCouponDTO;
import com.aks.cateringinfosys.entry.Coupon;
import com.aks.cateringinfosys.entry.Order;
import com.aks.cateringinfosys.entry.Restaurant;
import com.aks.cateringinfosys.entry.User;
import com.aks.cateringinfosys.mappers.CouponMapper;
import com.aks.cateringinfosys.mappers.OrderMapper;
import com.aks.cateringinfosys.mappers.RestaurantMapper;
import com.aks.cateringinfosys.mappers.UserMapper;
import com.aks.cateringinfosys.service.IOrderService;
import com.aks.cateringinfosys.utils.RedisIdWorker;
import com.aks.cateringinfosys.utils.UserHolder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.aks.cateringinfosys.utils.RedisConstants.*;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/19 17:18
 * @packagename com.aks.cateringinfosys.service.impl
 * @classname OrderServiceImpl
 * @description
 */
@Service
public class OrderServiceImpl implements IOrderService {
    public static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    CouponMapper couponMapper;
    @Autowired
    RestaurantMapper restaurantMapper;
    @Autowired
    RedisIdWorker idWorker;


    @Override
    public Result getOrderList(Integer type, Long id,Integer currentPage,Integer pageSize) {

        String orderStr = redisTemplate.opsForValue().get(CACHE_ORDER_KEY+id);
        if (orderStr != null && orderStr.equals(CACHE_NULL)){
            return Result.fail("查询Id的订单数为空");
        }
        if (orderStr != null &&!orderStr.equals(CACHE_NULL)) {
            List<Order> orders = JSONUtil.toList(orderStr, Order.class);
            return Result.ok(orders);
        }
        List<Order> orderList = null;
        PageInfo<Order> orderPageInfo = null;

        //根据用户id查询
        if (type == 1) {
            PageHelper.startPage(currentPage,pageSize);
            orderList = orderMapper.queryOrderListByUserId(id);
            orderPageInfo = new PageInfo<>(orderList);

        }else if (type == 2) {
            PageHelper.startPage(currentPage,pageSize);
            orderList = orderMapper.queryOrderListByRestId(id);
            orderPageInfo = new PageInfo<>(orderList);
        } else if (type == 3){
            Order order = orderMapper.queryOrderByOrderId(id);
            ArrayList<Order> orders = new ArrayList<>();
            orders.add(order);
            return Result.ok(orders,1l);
        }else {
            return Result.ok("应该根据用户id或者店铺id查询订单");
        }
        if (orderList == null || orderList.size() == 0){
            redisTemplate.opsForValue().set(CACHE_ORDER_KEY+id,CACHE_NULL,CACHE_NULL_TTL, TimeUnit.MINUTES);
            return Result.fail("你查询的信息订单为空");
        }
        redisTemplate.opsForValue().set(CACHE_ORDER_KEY+id, JSONUtil.toJsonStr(orderList),CACHE_ORDER_TTL, TimeUnit.MINUTES);
        return Result.ok(orderList,orderPageInfo.getTotal());
    }

    @Override
    public Result snappedCoupon(SnappedCouponDTO snappedCouponDTO) {
        // todo 先查询订单表中是否该用户已经对该优惠卷下单
        if (snappedCouponDTO.getType() == 1) {
            snappedCouponDTO.setOrderUserId(UserHolder.getUser().getUid());
        }
        Long orderUserId = snappedCouponDTO.getOrderUserId();
        Long orderCouId = snappedCouponDTO.getOrderCouId();
        Order order = orderMapper.queryOrderByUserAndCoupon(orderCouId,orderUserId);
        if (order != null) {
            return Result.fail("该优惠卷您已经下单过啦");
        }
        User user = userMapper.queryUserByUserId(orderUserId);
        if (user == null ){
            return Result.fail("该用户已经不存在啦");
        }
        Coupon coupon = couponMapper.queryCouponByCid(orderCouId);
        if (coupon == null) {
            return Result.fail("优惠卷不存在啊，重新添加试试");
        }
        if (coupon.getCouponRemainingNum() <= 0) {
            return Result.fail("优惠卷已经抢光啦，下次再来吧");
        }
        if (coupon.getBeginTime().isAfter(coupon.getEndTime())) {
            return Result.fail("活动已经截止，下次再来吧");
        }
        Order order1 = new Order();
        order1.setCouponOrder(idWorker.nextId());
        order1.setOrderCouId(coupon.getCouponId());
        order1.setOrderUserId(orderUserId);
        order1.setCreateTime(LocalDateTime.now());
        order1.setBeginTime(coupon.getBeginTime());
        order1.setEndTime(coupon.getEndTime());
        order1.setCouponName(coupon.getCouponName());
        Restaurant restaurant = restaurantMapper.queryRestById(coupon.getCouRestId());
        order1.setRestName(restaurant.getRestName());
        // todo 一人一张优惠卷一把锁
        String key = SNAPPED_LOCK + orderCouId;
        boolean lock = tryLock(key, 6L);
        if (!lock) {
            return Result.fail("不允许使用抢票程序进行抢票");
        }
        //todo 获取得到锁，进行数据更新
        try {
            // todo 优惠卷数量建议
            Boolean bool = couponMapper.subtractCouponNum();
            Integer flag = orderMapper.insertOrder(order1);
            if (flag != 1 || bool) {
                throw new RuntimeException("抢购优惠卷失败");
            }
            redisTemplate.delete(CACHE_ORDER_KEY+coupon.getCouRestId());
            redisTemplate.delete(CACHE_ORDER_KEY+orderUserId);
            logger.info("用户"+orderUserId+"下单一张优惠卷"+orderCouId);
            return Result.ok("抢购优惠卷成功");

        } finally {
            unLock(key);
        }
    }

    @Override
    public Result getDetail(Long orderId) {
        Order order = orderMapper.queryOrderByOrderId(orderId);
        if (order == null) {
            return Result.fail("订单已经不存在");
        }
        OrderDetailDTO orderDetailDTO = BeanUtil.copyProperties(order, OrderDetailDTO.class);
        Coupon coupon = couponMapper.queryCouponByCid(order.getOrderCouId());
        Restaurant restaurant = restaurantMapper.queryRestById(coupon.getCouRestId());
        User user = userMapper.queryUserByUserId(order.getOrderUserId());
        String type = restaurantMapper.queryTypeByTypeId(restaurant.getRestType());
        orderDetailDTO.setCouponAmount(coupon.getCouponAmount());
        orderDetailDTO.setRestAddress(restaurant.getRestAddress());
        orderDetailDTO.setEmail(user.getEmail());
        orderDetailDTO.setNickName(user.getNickName());
        orderDetailDTO.setUserAddress(user.getUserAddress());
        orderDetailDTO.setRestType(type);
        orderDetailDTO.setRestDescription(restaurant.getRestDescription());
        return Result.ok(orderDetailDTO);
    }

    @Override
    public Result deleteOrder(Long orderId) {
        Order order = orderMapper.queryOrderByOrderId(orderId);
        Coupon coupon = couponMapper.queryCouponByCid(order.getOrderCouId());
        if (order == null || coupon == null) {
            return Result.fail("订单不存在啦");
        }
        Integer flag = orderMapper.deleteOrderByOrderId(orderId);

        if (flag != flag) {
            return Result.fail("删除订单失败");
        }
        logger.info("用户"+UserHolder.getUser().getUid()+"删除一个订单"+orderId);
        redisTemplate.delete(CACHE_ORDER_KEY+order.getOrderUserId());
        redisTemplate.delete(CACHE_ORDER_KEY+coupon.getCouRestId());
        return Result.ok("删除订单成功");
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
