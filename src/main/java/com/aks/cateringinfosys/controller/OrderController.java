package com.aks.cateringinfosys.controller;

import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.dto.SnappedCouponDTO;
import com.aks.cateringinfosys.service.IOrderService;
import com.aks.cateringinfosys.utils.SystemConstants;
import com.aks.cateringinfosys.utils.UserHolder;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.aks.cateringinfosys.utils.SystemConstants.ADMINID;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/18 20:38
 * @packagename com.aks.cateringinfosys.controller
 * @classname OrderController
 * @description
 */
@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {
    @Autowired
    IOrderService iOrderService;


    @GetMapping("/getOrderList/{type}/{id}/{currentPage}/{pageSize}")
    public Result getOrderListById(@PathVariable("type")Integer type,
                                   @PathVariable("id") Long id,
                                   @PathVariable("currentPage") Integer currentPage,
                                   @PathVariable("pageSize")Integer pageSize) {
        if (type < 1 || type > 3 || id == null || currentPage <= 1 && pageSize <= 1){
            return Result.fail("url参数错误");
        }

        return iOrderService.getOrderList(type,id,currentPage,pageSize);
    }

    @PostMapping("/place")
    public Result snappedCoupon(@RequestBody SnappedCouponDTO snappedCouponDTO) {
        if (snappedCouponDTO.getType() < 1 || snappedCouponDTO.getType() > 2 || snappedCouponDTO.getOrderCouId() == null){
            return Result.fail("url参数错误");
        }
        // 为别人下单只有管理员才可以
        if (snappedCouponDTO.getType() == 2  && UserHolder.getUser().getUid() != ADMINID) {
            return Result.fail("权限不足");
        }
        return iOrderService.snappedCoupon(snappedCouponDTO);
    }
    @GetMapping("/getDetail/{orderId}")
    public Result getDetail(@PathVariable("orderId") Long orderId) {
        if (orderId == null) {
            return Result.fail("url参数错误");
        }
        return iOrderService.getDetail(orderId);
    }
    @GetMapping("/delete/{orderId}")
    public Result deleteOrder(@PathVariable("orderId")Long orderId) {
        if (orderId == null) {
            return Result.fail("url参数错误");
        }
        if (!UserHolder.getUser().getUid().equals(ADMINID)) {
            return Result.fail("权限不足");
        }
        return iOrderService.deleteOrder(orderId);
    }

}
