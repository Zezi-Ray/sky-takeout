package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Api(tags = "工作台相关接口")
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 获取今日运营数据
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("获取今日运营数据")
    public Result<BusinessDataVO> getBusinessData() {
        log.info("获取今日运营数据");

        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

        BusinessDataVO businessDataVO = workSpaceService.getBusinessData(begin, end);
        return Result.success(businessDataVO);
    }

    /**
     * 获取套餐概览数据
     * @return
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation("获取套餐概览数据")
    public Result<SetmealOverViewVO> getSetmealOverView() {
        log.info("获取套餐概览数据");
        SetmealOverViewVO setmealOverViewVO = workSpaceService.getSetmealOverView();
        return Result.success(setmealOverViewVO);
    }

    /**
     * 获取菜品概览数据
     * @return
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("获取菜品概览数据")
    public Result<DishOverViewVO> getDishOverView() {
        log.info("获取菜品概览数据");
        DishOverViewVO dishOverViewVO = workSpaceService.getDishOverView();
        return Result.success(dishOverViewVO);
    }

    /**
     * 获取订单概览数据
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("获取订单概览数据")
    public Result<OrderOverViewVO> getOrderOverView() {
        log.info("获取订单概览数据");
        OrderOverViewVO orderOverViewVO = workSpaceService.getOrderOverView();
        return Result.success(orderOverViewVO);
    }

}
