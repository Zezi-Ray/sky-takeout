package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/order")
@RestController("adminOrderController")
@Slf4j
@Api(tags = "订单管理相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单条件查询接口
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation(value = "订单条件查询接口")
    public Result<PageResult> conditionalPageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单条件查询接口：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionalPageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各种状态订单统计接口
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation(value = "订单统计接口")
    public Result<OrderStatisticsVO> orderStatistics() {
        log.info("各种状态订单统计");
        OrderStatisticsVO statisticsVO = orderService.orderStatistics();
        return Result.success(statisticsVO);
    }

    /**
     * 订单详情接口
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation(value = "订单详情接口")
    public Result<OrderVO> getOrderDetail(@PathVariable  Long id) {
        log.info("订单详情，订单id：{}", id);
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 订单确认接口
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation(value = "订单确认接口")
    public Result orderConfirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("订单确认接口：{}", ordersConfirmDTO);
        orderService.orderConfirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 订单拒绝接口
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation(value = "订单拒绝接口")
    public Result orderReject(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("订单拒绝：{}", ordersRejectionDTO);
        orderService.orderReject(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 订单取消接口
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation(value = "订单取消接口")
    public Result orderCancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("订单取消：{}", ordersCancelDTO);
        orderService.orderCancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 订单派送接口
     * @param id
     * @return
     */
    @PutMapping("delivery/{id}")
    @ApiOperation(value = "订单派送接口")
    public Result orderDelivery(@PathVariable  Long id) {
        log.info("订单派送，订单id：{}", id);
        orderService.orderDelivery(id);
        return Result.success();
    }

    /**
     * 订单完成接口
     * @param id
     * @return
     */
    @PutMapping("complete/{id}")
    @ApiOperation(value = "订单完成接口")
    public Result orderComplete(@PathVariable  Long id) {
        log.info("订单完成，订单id：{}", id);
        orderService.orderComplete(id);
        return Result.success();
    }

}
