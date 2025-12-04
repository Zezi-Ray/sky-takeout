package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "用户订单相关接口")
public class OrderController {

    @Autowired
    private OrderService OrderService;

    /**
     * 提交订单
     * @param orderSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation(value = "提交订单")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrdersSubmitDTO orderSubmitDTO) {
        log.info("用户提交订单: {}", orderSubmitDTO);
        OrderSubmitVO orderSubmitVO = OrderService.submitOrder(orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     * @throws Exception
     */
    @PutMapping("/payment")
    @ApiOperation(value = "订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("用户支付订单: {}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = OrderService.payment(ordersPaymentDTO);
        log.info("生成预支付订单: {}", orderPaymentVO);

        // 模拟交易成功，修改数据库订单状态
        OrderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        log.info("模拟支付成功，修改订单状态完成: {}", ordersPaymentDTO.getOrderNumber());

        return Result.success(orderPaymentVO);
    }

}
