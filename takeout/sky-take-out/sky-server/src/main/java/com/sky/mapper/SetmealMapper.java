package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {
    /**
     * 根据分类id统计套餐数量
     * @param categoryId 分类id
     * @return 套餐数量
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入套餐数据
     * @param setmeal 套餐数据
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id获取套餐信息
     * @param id 套餐id
     * @return 套餐信息
     */
    @Select("SELECT * FROM setmeal WHERE id = #{id}")
    Setmeal getById(Long id);

    /**
     * 根据id删除套餐
     * @param setmealId 套餐id
     */
    void deleteById(Long setmealId);

    /**
     * 更新套餐信息
     * @param setmeal 套餐信息
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 根据套餐id查询对应的菜品信息
     * @param id 套餐id
     * @return 菜品信息
     */
    @Select("SELECT d.* FROM dish d JOIN setmeal_dish sd ON d.id = sd.dish_id WHERE sd.setmeal_id = #{id}")
    List<Dish> getBySetmealId(Long id);

    /**
     * 根据条件查询套餐列表
     * @param setmeal 查询条件
     * @return 套餐列表
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询对应的菜品信息
     * @param id 套餐id
     * @return 菜品信息
     */
    @Select("SELECT sd.name, sd.copies, d.image, d.description " +
            "FROM setmeal_dish sd LEFT JOIN dish d ON sd.dish_id = d.id " +
            "WHERE sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long id);

    /**
     * 根据条件统计套餐数量
     * @param map 条件map
     * @return 套餐数量
     */
    Integer countByMap(Map map);
}
