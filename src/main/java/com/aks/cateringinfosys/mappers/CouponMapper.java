package com.aks.cateringinfosys.mappers;

import com.aks.cateringinfosys.entry.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 17:59
 * @packagename com.aks.cateringinfosys.mappers
 * @classname CouponMapper
 * @description
 */
@Component
@Mapper
public interface CouponMapper {
    // todo 获取指定店铺的优惠卷
    List<Coupon> queryCouponList(Long rid);
    //todo 根据优惠卷id获取优惠卷
    Coupon queryCouponByCid(Long cid);

    // todo 插入优惠卷订单
    Integer insertOrder(Long cid, Long uid);

    // todo 根据用户id和优惠卷id查询是否已经有此订单
    Long queryOrderByUidAndCid(Long uid, Long cid);

    // todo 通过个人id查询自己拥有的优惠券
    List<Coupon> queryCouponByUid(Long uid);
}
