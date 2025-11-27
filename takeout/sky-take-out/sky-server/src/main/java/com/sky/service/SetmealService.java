package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import java.util.List;

public interface SetmealService {

    /**
     * 新增套餐，同时保存对应的菜品信息
     * @param setmealDTO 套餐信息
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id删除套餐
     * @param ids 套餐ids
     */
    void deleteBatch(List<Long> ids);
}
