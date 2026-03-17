package com.reyn.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "gaode.map")
public class GaodeMapConfig {
    /**
     * 高德地图WebService Key
     */
    private String webServiceKey;

    /**
     * 高德地图JS API Key（前端使用）
     */
    private String jsApiKey;

    /**
     * REST API基础URL
     */
    private String restApiUrl;
}
