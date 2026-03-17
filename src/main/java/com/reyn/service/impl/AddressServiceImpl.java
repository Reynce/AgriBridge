package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.reyn.mapper.AddressMapper;
import com.reyn.objects.entity.Address;
import com.reyn.objects.vo.AddressVO;
import com.reyn.service.AddressService;
import com.reyn.utils.Regex.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;
    @Override
    public SaResult getAddressList(Long uid) {
        if (uid == null) {
            return SaResult.error("用户ID不能为空");
        }
        
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", uid);
        queryWrapper.orderByDesc("is_default"); // 默认地址排在前面
        queryWrapper.orderByDesc("created_at"); // 然后按创建时间倒序
        
        return SaResult.data(addressMapper.selectList(queryWrapper));
    }

    @Override
    public SaResult addAddress(Address address, Long uid) {
        if (uid == null) {
            return SaResult.error("用户ID不能为空");
        }
        
        String phone = address.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)){
            return SaResult.error("电话号码无效");
        }
        
        // 设置用户ID
        address.setUserId(uid);
        
        // 如果是第一个地址，自动设为默认
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", uid);
        long count = addressMapper.selectCount(queryWrapper);
        if (count == 0) {
            address.setIsDefault(1);
        } else if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        
        addressMapper.insert(address);
        return SaResult.ok("添加地址成功");
    }

    @Override
    public SaResult deleteAddress(Long id) {
        // 检查地址是否存在
        Address address = addressMapper.selectById(id);
        if (address == null) {
            return SaResult.error("地址不存在");
        }
        
        addressMapper.deleteById(id);
        return SaResult.ok("删除地址成功");
    }

    @Override
    public SaResult changeAddress(Address address) {
        addressMapper.updateById(address);
        return SaResult.ok();
    }

    @Override
    public SaResult set_default(Long id) {
        // 检查地址是否存在
        Address address = addressMapper.selectById(id);
        if (address == null) {
            return SaResult.error("地址不存在");
        }
        
        // 先将该用户的所有地址设置为非默认
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", address.getUserId());
        
        Address updateAddress = new Address();
        updateAddress.setIsDefault(0);
        addressMapper.update(updateAddress, queryWrapper);
        
        // 将指定地址设置为默认
        updateAddress = new Address();
        updateAddress.setId(id);
        updateAddress.setIsDefault(1);
        addressMapper.updateById(updateAddress);
        
        return SaResult.ok("设置默认地址成功");
    }

    @Override
    public SaResult getDefaultAddr(long userId){
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("is_default", 1);
        Address defaultAddr = addressMapper.selectOne(queryWrapper);

        // 封装后返回
        AddressVO addressVO = new AddressVO();
        addressVO.setId(defaultAddr.getId());
        addressVO.setPhone(defaultAddr.getPhone());
        addressVO.setRecipientName(defaultAddr.getRecipientName());
        addressVO.setFullAddress(defaultAddr.getProvince() + defaultAddr.getCity() + defaultAddr.getDistrict() + defaultAddr.getAddress());
        return SaResult.data(addressVO);
    }

    @Override
    public SaResult selectList(long userId){
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        // 查询所属用户的地址,将默认地址排在最上面
        queryWrapper.eq("user_id", userId)
                .orderByDesc("is_default");

        List<Address> addresses = addressMapper.selectList(queryWrapper);

        // 对查询的结果进行处理后返回
        ArrayList<AddressVO> list = new ArrayList<>();
        for (Address addr : addresses){
            AddressVO addressVO = new AddressVO();
            addressVO.setId(addr.getId());
            addressVO.setPhone(addr.getPhone());
            addressVO.setRecipientName(addr.getRecipientName());
            addressVO.setFullAddress(addr.getProvince() + addr.getCity() + addr.getDistrict() + addr.getAddress());
            list.add(addressVO);
        }
        return SaResult.data(list);
    }
}
