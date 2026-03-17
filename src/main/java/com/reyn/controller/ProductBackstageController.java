package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.common.core.controller.BaseController;
import com.reyn.objects.entity.Product;
import com.reyn.objects.page.TableDataInfo;
import com.reyn.service.ProductBackstageService;
import com.reyn.utils.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/product/product")
@RestController
@RequiredArgsConstructor
public class ProductBackstageController extends BaseController {
    private final ProductBackstageService productBackstageService;
    @GetMapping("/list")
    public SaResult getProductList(Product product){
        Page page = startPage();
        return SaResult.data(PageResult.data(productBackstageService.getProductRelativeInfoList(page, product)));
    }
}
