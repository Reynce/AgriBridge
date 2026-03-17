package com.reyn.objects.vo;

import lombok.Data;

@Data
public class LocationVO {
    private Double lat;     // 纬度
    private Double lng;     // 经度
    private String address; // 地址
}
