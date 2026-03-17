package com.reyn.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.entity.ProductTraceability;
import com.reyn.service.ProductTraceabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/traceability")
@RequiredArgsConstructor
public class ProductTraceabilityController {

    private final ProductTraceabilityService traceabilityService;

    /**
     * 根据主键查询溯源信息
     */
    @GetMapping("/{id}")
    public SaResult getTraceabilityById(@PathVariable Long id) {
        ProductTraceability traceability = traceabilityService.getById(id);
        return SaResult.data(traceability);
    }

    /**
     * 根据商品ID查询溯源信息
     */
    @GetMapping("/product/{productId}")
    public SaResult getTraceabilityByProductId(@PathVariable Long productId) {
        ProductTraceability traceability = traceabilityService.getByProductId(productId);
        return SaResult.data(traceability);
    }

    /**
     * 新增溯源信息（仅管理员）
     */
    @PostMapping
    public SaResult addTraceability(@RequestBody @Valid ProductTraceability traceability) {
        // 检查该商品是否已有溯源信息
        ProductTraceability existing = traceabilityService.getByProductId(traceability.getProductId());
        if (existing != null) {
            return SaResult.error("该商品已存在溯源信息，请使用更新接口");
        }

        boolean result = traceabilityService.save(traceability);
        if (result) {
            return SaResult.ok("溯源信息添加成功");
        } else {
            return SaResult.error("溯源信息添加失败");
        }
    }

    /**
     * 更新溯源信息（仅管理员）
     */
    @PutMapping
    public SaResult updateTraceability(@RequestBody @Valid ProductTraceability traceability) {
        if (traceability.getId() == null) {
            return SaResult.error("请提供溯源信息ID");
        }

        ProductTraceability existing = traceabilityService.getById(traceability.getId());
        if (existing == null) {
            return SaResult.error("溯源信息不存在");
        }

        boolean result = traceabilityService.updateById(traceability);
        if (result) {
            return SaResult.ok("溯源信息更新成功");
        } else {
            return SaResult.error("溯源信息更新失败");
        }
    }

    /**
     * 根据主键删除溯源信息（仅管理员）
     */
    @SaCheckRole("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public SaResult deleteTraceabilityById(@PathVariable Long id) {
        ProductTraceability existing = traceabilityService.getById(id);
        if (existing == null) {
            return SaResult.error("溯源信息不存在");
        }

        boolean result = traceabilityService.removeById(id);
        if (result) {
            return SaResult.ok("溯源信息删除成功");
        } else {
            return SaResult.error("溯源信息删除失败");
        }
    }

    /**
     * 根据商品ID删除溯源信息（仅管理员）
     */
    @SaCheckRole("ROLE_ADMIN")
    @DeleteMapping("/product/{productId}")
    public SaResult deleteTraceabilityByProductId(@PathVariable Long productId) {
        ProductTraceability existing = traceabilityService.getByProductId(productId);
        if (existing == null) {
            return SaResult.error("该商品暂无溯源信息");
        }

        boolean result = traceabilityService.deleteByProductId(productId);
        if (result) {
            return SaResult.ok("商品溯源信息删除成功");
        } else {
            return SaResult.error("商品溯源信息删除失败");
        }
    }
}
