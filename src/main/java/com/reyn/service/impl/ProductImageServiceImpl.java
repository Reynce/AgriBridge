package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.ProductImageMapper;
import com.reyn.objects.dto.ProductImageDTO;
import com.reyn.objects.entity.ProductImage;
import com.reyn.service.ProductImageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage> 
        implements ProductImageService {
    
    @Override
    public SaResult addProductImages(Long productId, String[] imageUrls) {
        for (String url : imageUrls) {
            ProductImage image = new ProductImage();
            image.setProductId(productId);
            image.setUrl(url);
            image.setImageType((byte) 1); // 默认为副图
            this.save(image);
        }
        return SaResult.ok("商品图片添加成功");
    }
    
    @Override
    public SaResult deleteProductImage(Long imageId) {
        this.removeById(imageId);
        return SaResult.ok("商品图片删除成功");
    }
    
    @Override
    public SaResult getProductImages(Long productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId);
        List<ProductImage> images = this.list(wrapper);
        return SaResult.ok("获取商品图片成功").setData(images);
    }
}
