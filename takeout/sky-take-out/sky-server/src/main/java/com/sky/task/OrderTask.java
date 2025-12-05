package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单相关定时任务
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 定时处理超时未支付订单
     * 每分钟执行一次，处理15分钟前下单且未支付的订单
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟的第0秒执行一次
    // @Scheduled(cron = "0/5 * * * * ?") // 测试用：每5秒执行一次
    public void processTimeoutOrders() {
        log.info("定时处理超时未支付订单: {}", LocalDateTime.now());

        // 计算出超时时间点，当前时间减去15分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // 调用Mapper，取出所有超时未支付订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        // 遍历订单列表，处理每个超时订单
        for (Orders order : ordersList) {
            log.info("处理超时未支付订单，订单号: {}", order.getId());
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason("超时未支付，系统自动取消");
            order.setCancelTime(LocalDateTime.now());
            orderMapper.update(order);
        }

    }

    /**
     * 定时处理派送中的订单
     * 每天凌晨1点执行一次，处理1小时前处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行一次
    // @Scheduled(cron = "1/5 * * * * ?") // 测试用：每5秒执行一次
    public void processDeliveryOrders() {
        log.info("定时处理处于派送中的订单: {}", LocalDateTime.now());

        // 计算出前一天处于派送中的订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        // 调用Mapper，取出所有派送中的订单
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        // 遍历订单列表，处理每个派送中的订单
        for (Orders order : ordersList) {
            log.info("处理派送中订单，订单号: {}", order.getId());
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);
        }
    }

}
