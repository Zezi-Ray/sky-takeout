package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;

    /**
     * 提交订单
     * @param orderSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder (OrdersSubmitDTO orderSubmitDTO){

        // 处理业务异常（地址簿为空 购物车为空）

        // 查询地址簿数据
        AddressBook addressBook = addressBookMapper.getById(orderSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 查询当前用户的购物车数据
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 向订单表插入一条数据
        Orders order = new Orders();
        BeanUtils.copyProperties(orderSubmitDTO,order);
        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setPhone(addressBook.getPhone());
        order.setConsignee(addressBook.getConsignee());
        order.setUserId(userId);

        orderMapper.insert(order);

        // 向订单明细表插入多条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetailList);

        // 清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        // 返回订单提交结果（OrderSubmitVO）
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderTime(order.getOrderTime())
                .orderAmount(order.getAmount())
                .build();

        return orderSubmitVO;

    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception{
        // 当前用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        // 调用微信支付下单接口，生成预支付交易单
        // JSONObject jsonObject = weChatPayUtil.pay(
                // 商户订单号
        //      ordersPaymentDTO.getOrderNumber(),
                // 支付金额
        //      new BigDecimal(0.01),
                // 支付描述
        //      "SKY-STORE-ORDER",
                // 用户openid
        //      user.getOpenid()
        // );

        // 生成空JSON对象，模拟调用微信支付下单接口
        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付，请勿重复支付");
        }

        OrderPaymentVO orderPaymentVO = jsonObject.toJavaObject(OrderPaymentVO.class);
        orderPaymentVO.setPackageStr(jsonObject.getString("package"));

        return orderPaymentVO;
    }

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumberAndUserId(outTradeNo, userId);

        // 根据订单id更新订单状态，支付方式，支付方式，支付时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

    }

    /**
     * 订单历史
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    public PageResult orderHistory(Integer page, Integer pageSize, Integer status) {
        // 设置分页参数
        PageHelper.startPage(page, pageSize);

        // 分页条件查询
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        Page<Orders> pageQuery = orderMapper.pageQuery(ordersPageQueryDTO);

        // 查询出订单明细，并封装入OrderVO进行响应
        List<OrderVO> list = new ArrayList<>();

        if (page != null && pageQuery.getTotal() > 0) {
            for (Orders orders : pageQuery) {
                // 订单ID
                Long orderId = orders.getId();
                // 根据订单ID查询订单明细
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderId);
                // 封装OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                // 添加到集合中
                list.add(orderVO);
            }
        }
        return new PageResult(pageQuery.getTotal(), list);
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    public OrderVO getOrderDetail(Long id) {
        // 根据订单ID查询订单
        Orders orders = orderMapper.getById(id);

        // 根据订单ID查询订单明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 根据订单和订单明细封装OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 取消订单
     * @param id
     */
    public void cancelOrder(Long id) throws Exception {
        // Verification

        // 根据订单ID查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在 [存在性]
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 校验订单状态是否可以取消 [状态合法性]
        // 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (ordersDB.getStatus() > Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // Prepare update

        // new Orders对象，绑定id，用于MyBatis更新
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // Check status

        // 若订单状态为待接单，需退款 [判断订单状态]
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            // 调用微信退款接口
            // weChatPayUtil.refund(
                    // 商户订单号
            //        ordersDB.getNumber(),
                    //商户退款单号
            //        ordersDB.getNumber(),
                    //退款金额
            //        new BigDecimal(0.01),
                    //原订单金额
            //        new BigDecimal(0.01));

            // 设置支付状态为已退款
            orders.setPayStatus(Orders.REFUND);
        }

        // Perform update

        // 更新订单状态
        orders.setStatus(Orders.CANCELLED);
        // 设置取消原因
        orders.setCancelReason("用户取消");
        // 设置取消时间
        orders.setCancelTime(LocalDateTime.now());
        // 执行更新
        orderMapper.update(orders);

    }

    /**
     * 再来一单
     * @param id
     */
    public void repetition(Long id) {
        // 获取当前用户ID
        Long userId = BaseContext.getCurrentId();

        // 根据订单ID查询订单明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 将订单明细数据转换为购物车数据
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将订单详情转换为购物车对象
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 批量插入购物车数据
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 订单条件查询
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult conditionalPageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 设置分页参数，用于MyBatis分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        // 执行分页条件查询
        Page<Orders> pageQuery = orderMapper.pageQuery(ordersPageQueryDTO);

        // 部分订单状态需要查询订单明细并封装OrderVO
        List<OrderVO> list = getOrderVOList(pageQuery);

        // 返回分页结果
        return new PageResult(pageQuery.getTotal(), list);
    }

    // 获取OrderVO列表
    private List<OrderVO> getOrderVOList(Page<Orders> pageQuery) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> list = new ArrayList<>();

        List<Orders> ordersList = pageQuery.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                // 将共同属性拷贝到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);

                // 将订单菜品信息设置到OrderVO
                orderVO.setOrderDishes(orderDishes);
                // 添加到返回列表
                list.add(orderVO);
            }
        }
        // 获取订单菜品信息字符串
        return list;
    }

    // 根据订单获取订单菜品信息字符串
    private  String getOrderDishesStr(Orders orders) {
        // 根据订单ID查询订单明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 拼接订单菜品信息字符串
        List<String> orderDishesList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将菜品信息列表转换为字符串返回
        return String.join(" ", orderDishesList);
    }

    /**
     * 订单统计
     * @return
     */
    public OrderStatisticsVO orderStatistics() {
        // 查询各种状态订单数量
        Integer toBeConfirmed = orderMapper.countByStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countByStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS);

        // 封装并返回订单统计结果
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);

        return orderStatisticsVO;
    }

    /**
     * 订单确认
     * @param ordersConfirmDTO
     * @return
     */
    public void orderConfirm(OrdersConfirmDTO ordersConfirmDTO) {
        // 根据订单ID更新订单状态为已接单
        Orders orders = new Orders();
        orders.setId(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);

        orderMapper.update(orders);
    }

    /**
     * 订单取消
     * @param ordersRejectionDTO
     * @return
     */
    public void orderReject(OrdersRejectionDTO ordersRejectionDTO) {
        // 根据ID获取订单
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());

        // 检查订单状态
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 检查支付状态，若已支付则退款
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        if (ordersDB.getPayStatus().equals(Orders.PAID)) {
            // 调用微信退款接口
            // weChatPayUtil.refund(
            // 商户订单号
            //        ordersDB.getNumber(),
            //商户退款单号
            //        ordersDB.getNumber(),
            //退款金额
            //        new BigDecimal(0.01),
            //原订单金额
            //        new BigDecimal(0.01));
            // 设置支付状态为已退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 构造订单取消信息并更新订单状态
        Orders orderReject = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        BeanUtils.copyProperties(orderReject, orders);

        orderMapper.update(orders);
    }

    /**
     * 订单取消
     * @param ordersCancelDTO
     * @return
     */
    public void orderCancel(OrdersCancelDTO ordersCancelDTO) {
        // 根据ID获取订单
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());

        // 检查订单状态
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(!ordersDB.getStatus().equals(Orders.PENDING_PAYMENT) && !ordersDB.getPayStatus().equals(Orders.CONFIRMED) && !ordersDB.getPayStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 检查支付状态，若已支付则退款
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        if (ordersDB.getPayStatus().equals(Orders.PAID)) {
            // 调用微信退款接口
            // weChatPayUtil.refund(
            // 商户订单号
            //        ordersDB.getNumber(),
            //商户退款单号
            //        ordersDB.getNumber(),
            //退款金额
            //        new BigDecimal(0.01),
            //原订单金额
            //        new BigDecimal(0.01));

            // 设置支付状态为已退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 构造订单取消信息并更新订单状态
        Orders orderCancel = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        BeanUtils.copyProperties(orderCancel, orders);

        orderMapper.update(orders);
    }

    /**
     * 订单派送
     * @param id
     * @return
     */
    public void orderDelivery(Long id) {
        // 检查订单状态
        Orders ordersDB = orderMapper.getById(id);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 根据订单ID更新订单状态为派送中
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        orderMapper.update(orders);
    }

    public void orderComplete(Long id) {
        // 检查订单状态
        Orders ordersDB = orderMapper.getById(id);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 根据订单ID更新订单状态为已完成
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

}