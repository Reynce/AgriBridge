package com.reyn.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.entity.Merchant;

public interface IMerchantService extends IService<Merchant> {
    Page listMerchant(Page pageParam, Merchant merchant);
}
