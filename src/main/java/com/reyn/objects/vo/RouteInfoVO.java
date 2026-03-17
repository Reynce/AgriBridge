package com.reyn.objects.vo;

import lombok.Data;

import java.util.List;

@Data
public class RouteInfoVO {
    private LocationVO fromLocation;    // 起点位置
    private LocationVO toLocation;      // 终点位置
    private String distance;            // 距离
    private String estimatedTime;       // 预计时间
    private List<List<Double>> path;    // 路径坐标点数组 [[lng, lat], ...]
}
