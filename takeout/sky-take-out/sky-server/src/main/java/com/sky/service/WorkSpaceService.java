package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {

    /**
     * 获取今日运营数据
     * @return
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 获取套餐概览数据
     * @return
     */
    SetmealOverViewVO getSetmealOverView();

    /**
     * 获取菜品概览数据
     * @return
     */
    DishOverViewVO getDishOverView();

    /**
     * 获取订单概览数据
     * @return
     */
    OrderOverViewVO getOrderOverView();
}
