package com.reyn.objects.vo;

import lombok.Data;

import java.util.List;

@Data
public class LogisticsPathVO {
    private String startPoint; // 发货地址
    private String endPoint;   // 收货地址
    private Double startLng;   // 起点经度
    private Double startLat;   // 起点纬度
    private Double endLng;     // 终点经度
    private Double endLat;     // 终点纬度
    private List<List<Double>> pathCoordinates; // 路径坐标点 [[经度,纬度], ...]
}


