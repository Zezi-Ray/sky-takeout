package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.DishDisableFailedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDTO 菜品信息
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO, dish);

        // 向dish表插1条数据
        dishMapper.insert(dish);

        // 获取菜品id
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            // 遍历口味集合
            flavors.forEach(flavor -> {
                // 设置口味对应的菜品id
                flavor.setDishId(dishId);
            });
            // 向dish_flavor表插多条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 分页查询参数
     * @return 分页查询结果
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id删除菜品
     * @param ids 菜品ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {

        // 判断当前菜品是否可以删除---是否存在在起售状态的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 判断当前菜品是否可以删除---是否存在关联的套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品表中的数据
        /* for (Long id : ids) {
            dishMapper.deleteById(id);
            // 删除口味表中的数据
            dishFlavorMapper.deleteByDishId(id);
        } */

        // 根据菜品ids批量删除菜品表中的数据
        dishMapper.deleteByIds(ids);

        // 根据菜品ids批量删除口味表中的数据
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据id获取菜品及其口味信息
     * @param id 菜品id
     * @return 菜品及其口味信息
     */
    public DishVO getByIdWithFlavor(Long id) {
        // 根据ID查询菜品数据
        Dish dish = dishMapper.getById(id);

        // 根据菜品ID查询对应的口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        // 将查询到的口味数据和菜品数据封装到DishVO并返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品，同时更新对应的口味数据
     * @param dishDTO 菜品信息
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 修改菜品表中的数据
        dishMapper.update(dish);

        // 删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dish.getId());

        // 重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            // 遍历口味集合
            flavors.forEach(dishFlavor -> {
                // 设置口味对应的菜品id
                dishFlavor.setDishId(dishDTO.getId());
            });
            // 向dish_flavor表插多条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId 分类id
     * @return 菜品列表
     */
    public List getBySetmealId(Long categoryId) {
        Dish dish = Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
        return dishMapper.selectList(dish);
    }

    /**
     * 启用或停售菜品
     * @param status 状态
     * @param id 菜品id
     */
    public void startOrStop(Integer status, Long id) {
        // 停售时判断菜品是否关联了套餐
        if (StatusConstant.DISABLE == status) {
            List<Setmeal> setmeals = setmealDishMapper.getSetmealByDishIds(id);
            if (setmeals != null && setmeals.size() > 0) {
                setmeals.forEach(setmeal -> {
                    if (StatusConstant.ENABLE == setmeal.getStatus()) {
                        throw new DishDisableFailedException(MessageConstant.DISH_SETMEAL_ON_SALE_DISABLE_FAIL);
                    }
                });
            }
        }
        // 修改菜品状态
        Dish dish = Dish.builder().id(id).status(status).build();
        dishMapper.update(dish);
    }

    /**
     * 根据条件查询菜品及其口味信息
     * @param dish 菜品信息
     * @return 菜品及其口味信息
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        // 根据条件查询菜品
        List<Dish> dishes = dishMapper.selectList(dish);
        // 封装菜品及其口味信息
        List<DishVO> dishVOs = new ArrayList<>();
        // 遍历菜品集合
        for (Dish d : dishes) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);
            // 根据菜品ID查询对应的口味数据
            List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(d.getId());
            dishVO.setFlavors(dishFlavors);
            dishVOs.add(dishVO);
        }
        return dishVOs;
    }

}
