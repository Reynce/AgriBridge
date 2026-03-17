package com.reyn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.entity.ProductTraceability;

public interface ProductTraceabilityService extends IService<ProductTraceability> {


    /**
     * 根据商品ID查询溯源信息
     * @param productId 商品ID
     * @return 溯源信息
     */
    ProductTraceability getByProductId(Long productId);

    /**
     * 根据商品ID删除溯源信息
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean deleteByProductId(Long productId);
}
