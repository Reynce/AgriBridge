package com.reyn.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.dto.SkuDTO;
import com.reyn.objects.entity.Sku;
import org.springframework.stereotype.Service;

@Service
public interface SkuService extends IService<Sku> {
    SaResult createSku(SkuDTO skuDTO);
    
    SaResult updateSku(SkuDTO skuDTO);
    
    SaResult deleteSku(Long skuId);
    
    SaResult getSkuListByProduct(Long productId);
    
    SaResult updateSkuStock(Long skuId, Integer stock);
    
    SaResult updateSkuPrice(Long skuId, Double price);
}
