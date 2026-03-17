package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.reyn.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/sku")
@RequiredArgsConstructor
public class SkuController {
    private final SkuService skuService;
    @GetMapping("/listByProductId")
    public SaResult listByProductId(@RequestParam @NotNull(message = "id不能为空") Long productId){
        return skuService.getSkuListByProduct(productId);
    }
}
