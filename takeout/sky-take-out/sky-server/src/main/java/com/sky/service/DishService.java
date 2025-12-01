package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDTO 菜品信息
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id删除菜品
     * @param ids 菜品ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id获取菜品及其口味信息
     * @param id 菜品id
     * @return 菜品及其口味信息
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 修改菜品，同时更新对应的口味数据
     * @param dishDTO 菜品信息
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品
     * @param categoryId 分类id
     * @return 菜品列表
     */
    List<Dish> getBySetmealId(Long categoryId);

    /**
     * 启用或停售菜品
     * @param status 状态
     * @param id 菜品id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据条件查询菜品及其口味信息
     * @param dish 菜品信息
     * @return 菜品及其口味信息
     */
    List<DishVO> listWithFlavor(Dish dish);
}
