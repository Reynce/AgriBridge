package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.common.core.controller.BaseController;
import com.reyn.mapper.UserRoleMapper;
import com.reyn.objects.entity.Merchant;
import com.reyn.objects.entity.UserRole;
import com.reyn.service.IMerchantService;
import com.reyn.utils.LoginHelper;
import com.reyn.utils.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController extends BaseController {
    private final IMerchantService merchantService;
    private final UserRoleMapper userRoleMapper;

    @GetMapping("/list")
    public SaResult listMerchant(Merchant merchant){
        Page pageParam = startPage();
        Page page = merchantService.listMerchant(pageParam, merchant);

        return SaResult.data(PageResult.data(page));
    }
    @PostMapping("/apply")
    public SaResult applyMerchant(@RequestBody Merchant merchantForm){
        // 查询目前用户是否存在过商家
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, LoginHelper.getLoginUserId());
        Merchant m = merchantService.getOne(wrapper);

        // 已经存在商家信息
        if (m != null){
            // 根据商家状态进行不同的更新
            switch (m.getStatus()){
                // 封禁
                case 0:{
                    return SaResult.error("您已被禁止申请商家，请联系平台管理员");
                }

                // 审核中或已拒绝，则更新信息
                case 1, 3:{
                    merchantForm.setId(m.getId());
                    merchantForm.setStatus((byte) 1);
                    merchantService.updateById(merchantForm);
                    return SaResult.ok("更新申请信息成功");
                }
                // 已上线
                case 2:{
                    return SaResult.error("您已是商家，无需申请");
                }

                default:return SaResult.error("商家状态未知，请联系平台管理员");
            }
        }

        // 不存在商家信息
        merchantForm.setStatus((byte) 1);
        merchantService.save(merchantForm);

        return SaResult.ok();
    }

    @PostMapping
    public SaResult insertMerchant(@RequestBody Merchant merchant){
        Merchant m = null;
        // 根据用户查询商家
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (merchant.getUserId() != null){
            wrapper.eq(Merchant::getUserId, merchant.getUserId());
            m = merchantService.getOne(wrapper);
        }

        if (m != null) return SaResult.error("该用户以及存在商家");

        return SaResult.ok();
    }

    @PutMapping
    public SaResult updateMerchant(@RequestBody Merchant merchant){
        merchantService.updateById(merchant);
        if(merchant.getStatus() == 2){
            UserRole userRole = new UserRole();
            userRole.setUserId(merchant.getUserId());
            userRole.setRoleId(3L);
            userRoleMapper.insert(userRole);
        }
        return SaResult.ok();
    }

    @GetMapping("/{id}")
    public SaResult selectById(@PathVariable Long id){
        return SaResult.data(merchantService.getById(id));
    }

    @DeleteMapping("/{id}")
    public SaResult deleteById(@PathVariable Long id){
        return SaResult.data(merchantService.removeById(id));
    }
}
