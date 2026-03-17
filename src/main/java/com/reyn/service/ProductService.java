package com.reyn.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.dto.CreateProductDTO;
import com.reyn.objects.vo.UpdateProductPreviewVO;
import com.reyn.objects.entity.Product;
import org.springframework.stereotype.Service;

@Service
public interface ProductService extends IService<Product> {
    SaResult createProduct(CreateProductDTO productDTO, Long userId);
    
    SaResult updateProduct(CreateProductDTO productDTO);

    UpdateProductPreviewVO getProductRelativeInfo(long productId);
    
    SaResult deleteProduct(Long productId);
    
    SaResult getProductDetail(Long productId);
    
    SaResult getProductsBySeller(Long userId);
    
    SaResult getProductsByCategory(Long categoryId);

    SaResult getProductOverviewList();
    
    SaResult listProducts();
}
