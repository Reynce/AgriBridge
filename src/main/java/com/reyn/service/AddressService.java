package com.reyn.service;


import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.entity.Address;

public interface AddressService {
    SaResult getAddressList(Long uid);

    SaResult addAddress(Address address, Long uid);

    SaResult deleteAddress(Long id);

    SaResult changeAddress(Address address);

    SaResult set_default(Long id);

    SaResult getDefaultAddr(long userId);

    SaResult selectList(long userId);
    
}
