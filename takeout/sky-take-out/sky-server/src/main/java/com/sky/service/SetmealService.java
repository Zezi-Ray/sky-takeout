package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

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

    /**
     * 修改套餐，同时更新对应的菜品信息
     * @param setmealDTO 套餐信息
     */
    void updateWithDish(SetmealDTO setmealDTO);

    /**
     * 根据id获取套餐及其菜品信息
     * @param id 套餐id
     * @return 套餐及其菜品信息
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 启用或停售套餐
     * @param status 状态
     * @param id 套餐id
     */
    void startOrStop(Integer status, Long id);
}
