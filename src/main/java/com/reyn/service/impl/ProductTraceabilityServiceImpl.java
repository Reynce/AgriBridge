package com.reyn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.ProductTraceabilityMapper;
import com.reyn.objects.entity.ProductTraceability;
import com.reyn.service.ProductTraceabilityService;
import org.springframework.stereotype.Service;

@Service
public class ProductTraceabilityServiceImpl extends ServiceImpl<ProductTraceabilityMapper, ProductTraceability>
        implements ProductTraceabilityService {

    private final ProductTraceabilityMapper traceabilityMapper;

    public ProductTraceabilityServiceImpl(ProductTraceabilityMapper traceabilityMapper) {
        this.traceabilityMapper = traceabilityMapper;
    }

    @Override
    public ProductTraceability getByProductId(Long productId) {
        LambdaQueryWrapper<ProductTraceability> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductTraceability::getProductId, productId);
        return traceabilityMapper.selectOne(wrapper);
    }


    @Override
    public boolean deleteByProductId(Long productId) {
        LambdaQueryWrapper<ProductTraceability> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductTraceability::getProductId, productId);
        return traceabilityMapper.delete(wrapper) > 0;
    }
}
