package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     * @param flavors 口味数据
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除对应的口味数据
     * @param dishid 菜品id
     */
    @Delete("DELETE FROM dish_flavor WHERE dish_id = #{dishid}")
    void deleteByDishId(Long dishid);

    /**
     * 根据菜品ids批量删除对应的口味数据
     * @param dishIds 菜品ids
     */
    void deleteByDishIds(List<Long> dishIds);

    /**
     * 根据菜品id获取对应的口味数据
     * @param dishId 菜品id
     * @return 口味数据
     */
    @Select("SELECT * FROM dish_flavor WHERE dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
