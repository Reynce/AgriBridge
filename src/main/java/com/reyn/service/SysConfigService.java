package com.reyn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.entity.SysConfig;

/**
 * 系统配置Service接口
 * 
 * @author reyn
 */
public interface SysConfigService extends IService<SysConfig> {
    
    /**
     * 根据键名查询参数配置
     * 
     * @param configKey 参数键名
     * @return 参数键值
     */
    String selectConfigByKey(String configKey);

    /**
     * 根据键名查询参数配置并转换为int
     * 
     * @param configKey 参数键名
     * @param defaultValue 默认值
     * @return 参数键值
     */
    int selectConfigByKeyInt(String configKey, int defaultValue);

    /**
     * 根据键名查询参数配置并转换为boolean
     *
     * @param configKey 参数键名
     * @param defaultValue 默认值
     * @return 参数键值
     */
    boolean selectConfigByKeyBoolean(String configKey, boolean defaultValue);

    /**
     * 刷新所有配置缓存
     */
    void refreshCache();
}
