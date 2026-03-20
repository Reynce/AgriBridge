package com.reyn.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reyn.objects.entity.SysConfig;
import com.reyn.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 * 
 * @author reyn
 */
@RestController
@RequestMapping("/sys-config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    /**
     * 查询所有配置列表
     */
    @GetMapping("/list")
    @SaCheckRole("ROLE_ADMIN")
    public SaResult list(String configName, String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<SysConfig>()
                .like(configName != null, SysConfig::getConfigName, configName)
                .like(configKey != null, SysConfig::getConfigKey, configKey);
        List<SysConfig> list = sysConfigService.list(wrapper);
        return SaResult.data(list);
    }

    /**
     * 获取配置详细信息
     */
    @GetMapping("/{id}")
    @SaCheckRole("ROLE_ADMIN")
    public SaResult getInfo(@PathVariable Long id) {
        return SaResult.data(sysConfigService.getById(id));
    }

    /**
     * 根据键名查询配置值
     */
    @GetMapping("/configKey/{configKey}")
    public SaResult getConfigKey(@PathVariable String configKey) {
        return SaResult.data(sysConfigService.selectConfigByKey(configKey));
    }

    /**
     * 新增或修改配置
     */
    @PostMapping("/save")
    @SaCheckRole("ROLE_ADMIN")
    public SaResult save(@RequestBody SysConfig sysConfig) {
        boolean success = sysConfigService.saveOrUpdate(sysConfig);
        return success ? SaResult.ok("保存成功") : SaResult.error("保存失败");
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    @SaCheckRole("ROLE_ADMIN")
    public SaResult remove(@PathVariable Long id) {
        boolean success = sysConfigService.removeById(id);
        return success ? SaResult.ok("删除成功") : SaResult.error("删除失败");
    }

    /**
     * 刷新配置缓存
     */
    @PostMapping("/refreshCache")
    @SaCheckRole("ROLE_ADMIN")
    public SaResult refreshCache() {
        sysConfigService.refreshCache();
        return SaResult.ok("刷新成功");
    }
}
