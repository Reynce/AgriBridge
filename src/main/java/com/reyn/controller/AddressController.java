package com.reyn.controller;



import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.entity.Address;
import com.reyn.service.AddressService;
import com.reyn.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("list")
    public SaResult getAddressList(){
        return addressService.getAddressList(LoginHelper.getLoginUserId());
    }

    @PostMapping("addr")
    public SaResult addAddress(@RequestBody Address address){
        return addressService.addAddress(address, LoginHelper.getLoginUserId());
    }

    @PutMapping("addr")
    public SaResult changeAddress(@RequestBody Address address){
        return addressService.changeAddress(address);
    }

    @DeleteMapping("addr")
    public SaResult changeAddress(Long id){
        return addressService.deleteAddress(id);
    }

    @PutMapping("set_default")
    public  SaResult set_default(Long id){
        return addressService.set_default(id);
    }

    @GetMapping("/defaultAddr")
    public SaResult getDefaultAddr(){
        return addressService.getDefaultAddr(LoginHelper.getLoginUserId());
    }

    @GetMapping("/selectList")
    public SaResult selectList(){
        return addressService.selectList(LoginHelper.getLoginUserId());
    }
}
