package com.reyn.objects.vo;

import lombok.Data;

import java.util.Date;

@Data
public class LogisticsInfoVO {
    private String trackingNumber;      // 快递单号
    private String logisticsCompany;    // 物流公司
    private String startFrom;           // 发货地点
    private String currentLocation;     // 当前位置
    private String destination;         // 目的地
    private Date shippedAt;             // 发货时间
    private Date deliveredAt;           // 签收时间
    private Integer shippedStatus;      // 物流状态：0未发货，1已发货，2已签收
}
