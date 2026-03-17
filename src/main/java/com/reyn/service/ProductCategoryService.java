package com.reyn.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.entity.ProductCategory;
import org.springframework.stereotype.Service;

@Service
public interface ProductCategoryService extends IService<ProductCategory> {
    SaResult createCategory(ProductCategory category);

    SaResult updateCategory(ProductCategory category);

    SaResult deleteCategory(Long categoryId);

    SaResult getCategoryList();

    SaResult getCategoryTree();
}
