package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 查询地址簿列表
     * @param addressBook
     * @return
     */
    List<AddressBook> selectAddressBookList(AddressBook addressBook);

    /**
     * 插入地址簿
     * @param addressBook
     */
    @Insert("INSERT INTO address_book (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) " +
            "VALUES (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    /**
     * 根据id查询地址簿
     * @param id
     * @return
     */
    @Select("SELECT * FROM address_book WHERE id = #{id}")
    AddressBook getById(Long id);

    /**
     * 修改地址簿
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 将该用户的所有地址设置为非默认地址
     * @param addressBook
     */
    @Update("UPDATE address_book SET is_default = 0 WHERE user_id = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 删除地址簿
     * @param addressBook
     */
    @Delete("DELETE FROM address_book WHERE id = #{id}")
    void deleteById(AddressBook addressBook);
}
