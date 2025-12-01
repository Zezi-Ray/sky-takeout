package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 查询菜品列表
     * @param categoryId 分类id
     * @return 菜品列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询菜品列表")
    public Result<List<DishVO>> list(Long categoryId){
        log.info("查询菜品列表，分类id：{}", categoryId);
        // 构造查询条件对象
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        // 根据条件查询菜品及其口味信息
        List<DishVO> dishlist = dishService.listWithFlavor(dish);
        return Result.success(dishlist);
    }
}
