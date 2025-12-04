package com.sky.mapper;

import com.sky.entity.Orders;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid 微信openid
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 新增用户
     * @param user 用户信息
     */
    void insert(User user);

    /**
     * 根据用户id查询用户
     * @param userId 用户id
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE id = #{userId}")
    User getById(Long userId);

}
