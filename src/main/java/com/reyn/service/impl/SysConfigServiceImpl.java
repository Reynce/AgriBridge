package com.reyn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.SysConfigMapper;
import com.reyn.objects.entity.SysConfig;
import com.reyn.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * 系统配置Service业务层处理
 * 
 * @author reyn
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String SYS_CONFIG_CACHE_KEY = "sys_config:";

    @Override
    public String selectConfigByKey(String configKey) {
        // 1. 先从Redis缓存获取
        String cacheValue = stringRedisTemplate.opsForValue().get(SYS_CONFIG_CACHE_KEY + configKey);
        if (cacheValue != null) {
            return cacheValue;
        }

        // 2. 缓存不存在，从数据库查询
        SysConfig config = this.getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey));
        
        if (config != null) {
            String value = config.getConfigValue();
            // 3. 存入Redis缓存，设置1小时过期
            stringRedisTemplate.opsForValue().set(SYS_CONFIG_CACHE_KEY + configKey, value, 1, TimeUnit.HOURS);
            return value;
        }
        
        return null;
    }

    @Override
    public int selectConfigByKeyInt(String configKey, int defaultValue) {
        String configValue = this.selectConfigByKey(configKey);
        if (configValue != null && !configValue.isEmpty()) {
            try {
                return Integer.parseInt(configValue);
            } catch (NumberFormatException e) {
                // 如果格式不对，返回默认值
            }
        }
        return defaultValue;
    }

    @Override
    public boolean selectConfigByKeyBoolean(String configKey, boolean defaultValue) {
        String configValue = this.selectConfigByKey(configKey);
        if (configValue != null && !configValue.isEmpty()) {
            return Boolean.parseBoolean(configValue);
        }
        return defaultValue;
    }

    /**
     * 重写saveOrUpdate，清空缓存
     */
    @Override
    public boolean saveOrUpdate(SysConfig entity) {
        boolean result = super.saveOrUpdate(entity);
        if (result && entity.getConfigKey() != null) {
            stringRedisTemplate.delete(SYS_CONFIG_CACHE_KEY + entity.getConfigKey());
        }
        return result;
    }

    /**
     * 重写updateById，清空缓存
     */
    @Override
    public boolean updateById(SysConfig entity) {
        SysConfig oldConfig = this.getById(entity.getId());
        boolean result = super.updateById(entity);
        if (result && oldConfig != null) {
            stringRedisTemplate.delete(SYS_CONFIG_CACHE_KEY + oldConfig.getConfigKey());
        }
        return result;
    }

    @Override
    public void refreshCache() {
        // 获取所有缓存键并删除
        java.util.Set<String> keys = stringRedisTemplate.keys(SYS_CONFIG_CACHE_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }
}
