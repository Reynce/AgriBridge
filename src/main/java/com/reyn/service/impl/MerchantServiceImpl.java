package com.reyn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.MerchantMapper;
import com.reyn.objects.entity.Merchant;
import com.reyn.service.IMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements IMerchantService {
    private final MerchantMapper merchantMapper;

    @Override
    public Page listMerchant(Page pageParam, Merchant merchant) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        if (merchant.getName() != null && !merchant.getName().isEmpty()){
            queryWrapper.like("name", merchant.getName());
        }
        if (merchant.getDescription() != null && !merchant.getDescription().isEmpty()){
            queryWrapper.like("description", merchant.getDescription());
        }
        if (merchant.getStatus() != null){
            queryWrapper.eq("status", merchant.getStatus());
        }

        return merchantMapper.selectPage(pageParam, queryWrapper);
    }
}
