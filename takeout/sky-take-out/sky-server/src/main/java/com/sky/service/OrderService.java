package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /**
     * 提交订单
     * @param orderSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO orderSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment (OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 订单历史
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult orderHistory(Integer page, Integer pageSize, Integer status);

    /**
     * 订单详情
     * @param id
     * @return
     */
    OrderVO getOrderDetail(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancelOrder(Long id) throws Exception;

    /**
     * 重复下单
     * @param id
     */
    void repetition(Long id);

    /**
     * 订单条件查询
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionalPageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 订单统计
     * @return
     */
    OrderStatisticsVO orderStatistics();

    /**
     * 订单确认
     * @param ordersConfirmDTO
     */
    void orderConfirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 订单拒绝
     * @param ordersRejectionDTO
     */
    void orderReject(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 订单取消
     * @param ordersCancelDTO
     */
    void orderCancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 订单派送
     * @param id
     */
    void orderDelivery(Long id);

    /**
     * 订单完成
     * @param id
     */
    void orderComplete(Long id);

    /**
     * 用户催单
     * @param id
     */
    void reminder(Long id);
}
