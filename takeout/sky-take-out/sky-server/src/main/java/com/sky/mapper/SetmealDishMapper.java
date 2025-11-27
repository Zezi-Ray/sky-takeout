package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品ids查询对应的套餐ids
     * @param dishIds 菜品ids
     * @return 套餐ids
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐菜品关系数据
     * @param setmealDishes 套餐菜品关系数据
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除对应的套餐菜品关系数据
     * @param setmealId 套餐id
     */
    @Delete("DELETE FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询对应的套餐菜品关系数据
     * @param id 套餐id
     * @return 套餐菜品关系数据
     */
    @Select("SELECT * FROM setmeal_dish WHERE setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    /**
     * 根据菜品ids查询对应的套餐信息
     * @param id 菜品id
     * @return 套餐信息
     */
    @Select("SELECT DISTINCT s.* FROM setmeal s JOIN setmeal_dish sd ON s.id = sd.setmeal_id WHERE sd.dish_id = #{id}")
    List<Setmeal> getSetmealByDishIds(Long id);
}
