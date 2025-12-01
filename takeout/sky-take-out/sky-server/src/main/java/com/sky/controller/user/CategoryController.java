package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询分类列表
     * @param type 分类类型
     * @return 分类列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询分类列表")
    public Result<List<Category>> list(Integer type){
        log.info("查询分类列表，分类类型：{}", type);
        List<Category> categoryList = categoryService.list(type);
        return Result.success(categoryList);
    }
}
