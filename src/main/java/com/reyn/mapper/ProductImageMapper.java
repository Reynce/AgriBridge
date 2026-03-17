package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {
    List<ProductImage> selectByProductId(@Param("productId") Long productId);

    String getMainImgUrlBySkuId(@Param("skuId") Long skuId);
}
