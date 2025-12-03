package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "地址簿相关接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查看地址簿列表
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("value = 查看地址簿列表")
    public Result<List<AddressBook>> list() {
        // 设置用户id
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());

        // 查询当前用户的地址簿列表
        log.info("查看用户地址簿: {}", addressBook.getUserId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    /**
     * 新增地址簿
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("新增地址簿")
    public Result save(@RequestBody AddressBook addressBook) {
        log.info("新增地址簿: {}", addressBook);
        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * 根据id查询地址簿
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址簿")
    public Result<AddressBook> getById(@PathVariable Long id) {
        log.info("根据id查询地址簿: {}", id);
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 修改地址簿
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("修改地址簿")
    public Result update(@RequestBody AddressBook addressBook) {
        log.info("修改地址簿: {}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        log.info("设置默认地址: {}", addressBook);
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /**
     * 删除地址簿
     * @param addressBook
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除地址簿")
    public Result delete(@RequestBody AddressBook addressBook) {
        log.info("删除地址簿: {}", addressBook);
        addressBookService.deleteById(addressBook);
        return Result.success();
    }

    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefaultAddress() {
        log.info("查询默认地址");
        // 构造查询条件
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(1);
        // 查询默认地址
        List<AddressBook> list = addressBookService.list(addressBook);
        // 返回结果
        if (list != null && list.size() > 0) {
            return Result.success(list.get(0));
        }
        // 没有找到默认地址
        return Result.error("没有找到默认地址");

    }

}
