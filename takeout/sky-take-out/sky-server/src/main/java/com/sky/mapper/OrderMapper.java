package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber 订单号
     * @param userId 用户id
     * @return 订单信息
     */
    @Select("SELECT * FROM orders WHERE number = #{orderNumber} AND user_id = #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 更新订单信息
     * @param orders 订单信息
     */
    void update(Orders orders);

    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单id查询订单信息
     * @param id 订单id
     * @return 订单信息
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders getById(Long id);
}
