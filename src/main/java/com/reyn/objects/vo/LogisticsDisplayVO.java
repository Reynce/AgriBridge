package com.reyn.objects.vo;

import lombok.Data;

import java.util.List;

@Data
public class LogisticsDisplayVO {
    private LogisticsInfoVO logisticsInfo;  // 物流基本信息
    private RouteInfoVO routeInfo;          // 路线信息
    private List<TrackVO> tracks;           // 物流轨迹
}
