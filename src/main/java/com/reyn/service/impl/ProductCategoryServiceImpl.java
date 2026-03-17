package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.ProductCategoryMapper;
import com.reyn.objects.entity.ProductCategory;
import com.reyn.service.ProductCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory>
        implements ProductCategoryService {

    @Override
    public SaResult createCategory(ProductCategory category) {
        this.save(category);
        return SaResult.ok("分类创建成功").setData(category.getId());
    }

    @Override
    public SaResult updateCategory(ProductCategory category) {
        this.updateById(category);
        return SaResult.ok("分类更新成功");
    }

    @Override
    public SaResult deleteCategory(Long categoryId) {
        // 检查是否有子分类
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategory::getParentId, categoryId);
        long count = this.count(wrapper);

        if (count > 0) {
            return SaResult.error("存在子分类，无法删除");
        }

        this.removeById(categoryId);
        return SaResult.ok("分类删除成功");
    }

    @Override
    public SaResult getCategoryList() {
        List<ProductCategory> categories = this.list();
        return SaResult.ok("获取分类列表成功").setData(categories);
    }

    @Override
    public SaResult getCategoryTree() {
        List<ProductCategory> allCategories = this.list();
        Map<Long, List<ProductCategory>> parentMap = allCategories.stream()
                .collect(Collectors.groupingBy(ProductCategory::getParentId));

        List<ProductCategory> rootCategories = parentMap.get(0L);
        if (rootCategories == null) {
            rootCategories = new ArrayList<>();
        }

        // 构建树形结构
        List<ProductCategory> tree = buildCategoryTree(rootCategories, parentMap);

        return SaResult.ok("获取分类树成功").setData(tree);
    }

    private List<ProductCategory> buildCategoryTree(List<ProductCategory> parentCategories,
                                                   Map<Long, List<ProductCategory>> parentMap) {
        for (ProductCategory category : parentCategories) {
            List<ProductCategory> children = parentMap.get(category.getId());
            if (children != null && !children.isEmpty()) {
                category.setChildren(children);
                buildCategoryTree(children, parentMap);
            }
        }
        return parentCategories;
    }
}
