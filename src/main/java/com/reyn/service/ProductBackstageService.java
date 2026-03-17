package com.reyn.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.entity.Product;
import com.reyn.objects.vo.MerchantProductsVO;

public interface ProductBackstageService extends IService<Product> {
    /**
     * 分页查询所有商品的相关信息
     */
    Page getProductRelativeInfoList(Page pageParam, Product product);
}
