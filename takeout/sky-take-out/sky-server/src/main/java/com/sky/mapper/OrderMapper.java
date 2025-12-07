package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * 统计指定状态的订单数量
     * @param status 订单状态
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status}")
    Integer countByStatus(Integer status);

    /**
     * 根据状态和下单时间小于指定时间查询订单列表
     * @param status 订单状态
     * @param order_time 指定时间
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE status = #{status} AND order_time < #{order_time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime order_time);

    /**
     * 根据条件统计订单金额总和
     * @param map 条件参数
     * @return 订单金额总和
     */
    Double sumByMap(Map map);

    /**
     * 根据条件统计订单数量
     * @param map 条件参数
     * @return 订单数量
     */
    Integer countByMap(Map map);

    /**
     * 获取指定时间段内的销售额前十的商品
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 商品销售数据列表
     */
    List<GoodsSalesDTO> getSalesTop10(@Param("beginTime") LocalDateTime beginTime,
                                      @Param("endTime") LocalDateTime endTime);
}
