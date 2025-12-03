package com.sky.controller.user;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
