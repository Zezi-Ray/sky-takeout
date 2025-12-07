package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取今日运营数据
     * @param begin
     * @param end
     * @return
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        /**
         * 营业额：当日已完成订单的总金额 turnover
         * 有效订单：当日已完成订单的数量 validOrderCount
         * 订单完成率：有效订单数 / 总订单数 orderCompletionRate
         * 平均客单价：营业额 / 有效订单数 unitPrice
         * 新增用户：当日新增用户的数量 newUsers
         */

        // 封装参数
        Map map = new HashMap<>();
        map.put("beginTime", begin);
        map.put("endTime", end);

        // 调用mapper方法获取总订单数
        Integer totalOrderCount = orderMapper.countByMap(map);

        // 调用mapper方法获取营业额
        map.put("status", Orders.CONFIRMED);
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0.0 : turnover;

        // 调用mapper方法获取有效订单数
        Integer validOrderCount = orderMapper.countByMap(map);

        // 计算平均客单价和订单完成率
        Double unitPrice = 0.0;
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0 && validOrderCount != 0) {
            // 计算平均客单价
            unitPrice = turnover / validOrderCount;
            // 计算订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / validOrderCount;
        }

        // 调用mapper方法获取新增用户数
        Integer newUsers = userMapper.countByMap(map);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 获取套餐概览数据
     * @return
     */
    public SetmealOverViewVO getSetmealOverView() {
        Map map = new HashMap<>();

        // 统计启用的套餐数量
        map.put("status", StatusConstant.ENABLE);
        Integer setmealCount = setmealMapper.countByMap(map);

        // 统计禁用的套餐数量
        map.put("status", StatusConstant.DISABLE);
        Integer disabledSetmealCount = setmealMapper.countByMap(map);

        return SetmealOverViewVO.builder()
                .sold(setmealCount)
                .discontinued(disabledSetmealCount)
                .build();
    }

    /**
     * 获取菜品概览数据
     * @return
     */
    public DishOverViewVO getDishOverView() {
        Map map = new HashMap<>();

        // 统计启用的菜品数量
        map.put("status", StatusConstant.ENABLE);
        Integer dishCount = dishMapper.countByMap(map);

        // 统计禁用的菜品数量
        map.put("status", StatusConstant.DISABLE);
        Integer disabledDishCount = dishMapper.countByMap(map);

        return DishOverViewVO.builder()
                .sold(dishCount)
                .discontinued(disabledDishCount)
                .build();
    }

    /**
     * 获取订单概览数据
     * @return
     */
    public OrderOverViewVO getOrderOverView() {
        Map map = new HashMap<>();
        map.put("begin", LocalDateTime.now().with(LocalDateTime.MIN));

        // 统计待接单数量
        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer pendingCount = orderMapper.countByMap(map);

        // 统计待派送数量
        map.put("status", Orders.CONFIRMED);
        Integer toBeDeliveredCount = orderMapper.countByMap(map);

        // 统计已完成数量
        map.put("status", Orders.COMPLETED);
        Integer completedCount = orderMapper.countByMap(map);

        // 统计已取消数量
        map.put("status", Orders.CANCELLED);
        Integer cancelledCount = orderMapper.countByMap(map);

        // 统计全部订单数量
        map.put("status", null);
        Integer allCount = orderMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .waitingOrders(pendingCount)
                .deliveredOrders(toBeDeliveredCount)
                .completedOrders(completedCount)
                .cancelledOrders(cancelledCount)
                .allOrders(allCount)
                .build();

    }

}
