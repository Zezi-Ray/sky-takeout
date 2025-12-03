package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 查询购物车列表
     * @param shoppingCart 购物车信息
     * @return 购物车列表
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id更新购物车中菜品或套餐的数量
     * @param shoppingCart 购物车信息
     */
    @Select("UPDATE shopping_cart SET number = #{number} WHERE id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入购物车记录
     * @param shoppingCart 购物车信息
     */
    @Insert("INSERT INTO shopping_cart " +
            "(user_id, dish_id, setmeal_id, name, image, amount, number, create_time, dish_flavor) " +
            "VALUES (#{userId}, #{dishId}, #{setmealId}, #{name}, #{image}, #{amount}, #{number}, #{createTime}, #{dishFlavor})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id删除购物车记录
     * @param userId 用户id
     */
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 根据id删除购物车记录
     * @param id 购物车id
     */
    @Delete("DELETE FROM shopping_cart WHERE id = #{id}")
    void deleteById(Long id);
}
