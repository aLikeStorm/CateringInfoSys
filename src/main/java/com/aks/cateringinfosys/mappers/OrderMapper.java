package com.aks.cateringinfosys.mappers;

import com.aks.cateringinfosys.entry.Order;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/19 17:20
 * @packagename com.aks.cateringinfosys.mappers
 * @classname OrderMapper.xml
 * @description
 */
@Component
@Mapper
public interface OrderMapper {
    List<Order> queryOrderListByUserId(Long id);

    List<Order> queryOrderListByRestId(Long id);

    @Select("SELECT * FROM TB_COUPON_ORDER WHERE COUPONORDER= #{id}")
    Order queryOrderByOrderId(Long id);

    @Select("SELECT * FROM TB_COUPON_ORDER WHERE ORDERCOUID=#{orderCouId} AND ORDERUSERID=#{orderUserId}")
    Order queryOrderByUserAndCoupon(@Param("orderCouId") Long orderCouId, @Param("orderUserId") Long orderUserId);

    Integer insertOrder(Order order1);

    @Delete("DELETE FROM TB_COUPON_ORDER WHERE COUPONORDER=#{orderId}")
    Integer deleteOrderByOrderId(Long orderId);
}
