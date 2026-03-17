package com.reyn.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.entity.ProductCategory;
import com.reyn.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class ProductCategoryController {
    @Autowired
    private ProductCategoryService productCategoryService;
    
    @PostMapping
    public SaResult createCategory(@RequestBody ProductCategory category) {
        return productCategoryService.createCategory(category);
    }
    
    @PutMapping
    public SaResult updateCategory(@RequestBody ProductCategory category) {
        return productCategoryService.updateCategory(category);
    }
    
    @DeleteMapping("/{id}")
    public SaResult deleteCategory(@PathVariable Long id) {
        return productCategoryService.deleteCategory(id);
    }
    
    @GetMapping
    @SaIgnore
    public SaResult getCategoryList() {
        return productCategoryService.getCategoryList();
    }
    
    @GetMapping("/tree")
    @SaIgnore
    public SaResult getCategoryTree() {
        return productCategoryService.getCategoryTree();
    }
}
