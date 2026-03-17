package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.SkuMapper;
import com.reyn.objects.dto.SkuDTO;
import com.reyn.objects.entity.Sku;
import com.reyn.service.SkuService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {
    
    @Override
    public SaResult createSku(SkuDTO skuDTO) {
        Sku sku = new Sku();
        BeanUtils.copyProperties(skuDTO, sku);
        this.save(sku);
        return SaResult.ok("SKU创建成功").setData(sku.getId());
    }
    
    @Override
    public SaResult updateSku(SkuDTO skuDTO) {
        Sku sku = new Sku();
        BeanUtils.copyProperties(skuDTO, sku);
        this.updateById(sku);
        return SaResult.ok("SKU更新成功");
    }
    
    @Override
    public SaResult deleteSku(Long skuId) {
        this.removeById(skuId);
        return SaResult.ok("SKU删除成功");
    }
    
    @Override
    public SaResult getSkuListByProduct(Long productId) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getProductId, productId);
        List<SkuDTO> skuDTOList = new ArrayList<>();
        for (Sku sku : this.list(wrapper)) {
            SkuDTO skuDTO = new SkuDTO();
            BeanUtils.copyProperties(sku, skuDTO);
            skuDTOList.add(skuDTO);
        }

        return SaResult.ok("获取SKU列表成功").setData(skuDTOList);
    }
    
    @Override
    public SaResult updateSkuStock(Long skuId, Integer stock) {
        Sku sku = new Sku();
        sku.setId(skuId);
        sku.setStock(stock);
        this.updateById(sku);
        return SaResult.ok("SKU库存更新成功");
    }
    
    @Override
    public SaResult updateSkuPrice(Long skuId, Double price) {
        Sku sku = new Sku();
        sku.setId(skuId);
        sku.setPrice(new BigDecimal(price.toString()));
        this.updateById(sku);
        return SaResult.ok("SKU价格更新成功");
    }
}
