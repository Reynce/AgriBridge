package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.Product;
import com.reyn.objects.vo.ProductDetailVO;
import com.reyn.objects.vo.ProductVO;
import com.reyn.objects.vo.SellerInfoVO;
import com.reyn.objects.vo.TraceabilityInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    String getProductTitleBySkuId(@Param("skuId") Long skuId);
    Long getProductSallerIdBySkuId(@Param("skuId") Long skuId);

    /**
     * 更新商品销量
     * @param productId 商品ID
     * @param quantity 购买数量
     */
    void updateProductSales(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
