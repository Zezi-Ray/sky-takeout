package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO 菜品信息
     * @return 处理结果
     */
    @PostMapping
    @ApiOperation("新增菜品接口")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品: {}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询接口")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id删除菜品
     * @param ids 菜品ids
     * @return 处理结果
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除接口")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除菜品: {}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id 菜品id
     * @return 菜品信息和对应的口味信息
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品 接口")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品: {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO 菜品信息
     * @return 处理结果
     */
    @PutMapping
    @ApiOperation("修改菜品接口")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品: {}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId 分类id
     * @return 菜品列表
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品接口")
    public Result<List<Dish>> getBySetmealId(Long categoryId) {
        log.info("根据分类id查询菜品: {}", categoryId);
        List<Dish> dishList = dishService.getBySetmealId(categoryId);
        return Result.success(dishList);
    }

}
