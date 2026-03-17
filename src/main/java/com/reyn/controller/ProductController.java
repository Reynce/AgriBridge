package com.reyn.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.util.SaResult;
import com.reyn.mapper.ProductMapper;
import com.reyn.objects.dto.CreateProductDTO;
import com.reyn.objects.entity.Product;
import com.reyn.objects.vo.UpdateProductPreviewVO;
import com.reyn.service.ProductService;
import com.reyn.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;

    @PostMapping
    public SaResult createProduct(@RequestBody CreateProductDTO productDTO) {
        return productService.createProduct(productDTO, LoginHelper.getLoginUserId());
    }

    @PutMapping
    public SaResult updateProduct(@RequestBody CreateProductDTO productDTO) {
        // 校验商品所属者的id
        Product product = productMapper.selectById(productDTO.getProductId());
        if (!LoginHelper.getLoginUserId().equals(product.getSallerId())) return SaResult.error("商品不属于当前用户");

        return productService.updateProduct(productDTO);
    }

    /**
     * 更新商品信息前,根据id获取待更新商品目前的相关信息
     */
    @GetMapping("/relativeInfo")
    @SaIgnore
    public SaResult getProductRelativeInfo(@RequestParam("productId") long productId){
        return SaResult.data(productService.getProductRelativeInfo(productId));
    }

    @DeleteMapping("/{id}")
    public SaResult deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/{id}")
    @SaIgnore
    public SaResult getProductDetail(@PathVariable Long id) {
        return productService.getProductDetail(id);
    }

    @GetMapping("/list")
    @SaIgnore
    public SaResult listProducts() {
        return productService.getProductOverviewList();
    }

    @GetMapping("/merchant")
    @SaIgnore
    public SaResult getProductsBySeller() {
        return productService.getProductsBySeller(LoginHelper.getLoginUserId());
    }

    @GetMapping("/category/{categoryId}")
    @SaIgnore
    public SaResult getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }
}
