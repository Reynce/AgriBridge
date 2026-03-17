package com.reyn.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.dto.ProductImageDTO;
import com.reyn.objects.entity.ProductImage;
import org.springframework.stereotype.Service;

@Service
public interface ProductImageService extends IService<ProductImage> {
    SaResult addProductImages(Long productId, String[] imageUrls);
    
    SaResult deleteProductImage(Long imageId);
    
    SaResult getProductImages(Long productId);
}
