package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.ProductCategoryMapper;
import com.reyn.mapper.ProductImageMapper;
import com.reyn.mapper.ProductMapper;
import com.reyn.mapper.SkuMapper;
import com.reyn.objects.entity.*;
import com.reyn.objects.vo.MerchantProductsVO;
import com.reyn.service.ProductBackstageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductBackstageServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductBackstageService {
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final ProductCategoryMapper productCategoryMapper;
    private final SkuMapper skuMapper;

    @Override
    public Page getProductRelativeInfoList(Page pageParam, Product product) {
        // 1. 构建查询条件
        LambdaQueryWrapper<Product> queryWrapper = buildProductQueryWrapper(product);

        // 2. 查询商品列表
        Page<Product> selectPage = productMapper.selectPage(
                pageParam,
                queryWrapper
        );
        List<Product> products = selectPage.getRecords();

        // 3. 组装 VO 列表
        List<MerchantProductsVO> voList = products.stream().map(prod -> {
            MerchantProductsVO vo = new MerchantProductsVO();
            vo.setId(prod.getId());
            vo.setTitle(prod.getTitle());
            vo.setProduct_status(prod.getProductStatus());

            // 4. 获取分类名称
            if (prod.getCategoryId() != null) {
                ProductCategory category = productCategoryMapper.selectById(prod.getCategoryId());
                if (category != null) {
                    vo.setCategory(category.getName());
                }
            }

            // 5. 计算库存总和
            LambdaQueryWrapper<Sku> skuWrapper = new LambdaQueryWrapper<>();
            skuWrapper.eq(Sku::getProductId, prod.getId());
            List<Sku> skus = skuMapper.selectList(skuWrapper);
            long totalStock = skus.stream().mapToLong(Sku::getStock).sum();
            vo.setStock(totalStock);

            // 6. 获取主图 URL
            LambdaQueryWrapper<ProductImage> imageWrapper = new LambdaQueryWrapper<>();
            imageWrapper.eq(ProductImage::getProductId, prod.getId())
                    .eq(ProductImage::getImageType, 0); // 主图
            ProductImage mainImage = productImageMapper.selectOne(imageWrapper);
            if (mainImage != null) {
                vo.setImage(mainImage.getUrl());
            }

            // 7. 查找最低价格的 SKU 并设置价格
            if (!skus.isEmpty()) {
                BigDecimal minPrice = skus.stream()
                        .map(Sku::getPrice)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                vo.setPrice(minPrice);
            }

            return vo;
        }).collect(Collectors.toList());

        // 8. 创建新的分页对象并设置正确的分页信息
        Page<MerchantProductsVO> resultPage = new Page<>(pageParam.getCurrent(), pageParam.getSize());
        resultPage.setRecords(voList);
        resultPage.setTotal(selectPage.getTotal());
        resultPage.setPages(selectPage.getPages());

        return resultPage;
    }

    /**
     * 根据Product对象构建查询条件
     * @param product 查询条件对象
     * @return LambdaQueryWrapper<Product>
     */
    private LambdaQueryWrapper<Product> buildProductQueryWrapper(Product product) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();

        // 按分类ID查询
        if (product.getCategoryId() != null) {
            queryWrapper.eq(Product::getCategoryId, product.getCategoryId());
        }

        // 按标题模糊查询
        if (product.getTitle() != null && !product.getTitle().trim().isEmpty()) {
            queryWrapper.like(Product::getTitle, product.getTitle().trim());
        }

        // 按商品状态查询
        if (product.getProductStatus() != null) {
            queryWrapper.eq(Product::getProductStatus, product.getProductStatus());
        }

        // 按商家ID查询（如果需要的话）
        if (product.getMerchantId() != null) {
            queryWrapper.eq(Product::getMerchantId, product.getMerchantId());
        }

        // 按卖家ID查询（如果需要的话）
        if (product.getSallerId() != null) {
            queryWrapper.eq(Product::getSallerId, product.getSallerId());
        }

        // 按商品简介查询（如果需要的话）
        if (product.getBrief() != null && !product.getBrief().trim().isEmpty()) {
            queryWrapper.like(Product::getBrief, product.getBrief().trim());
        }

        return queryWrapper;
    }
}
