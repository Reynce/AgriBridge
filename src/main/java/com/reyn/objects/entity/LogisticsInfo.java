package com.reyn.objects.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("logistics_info")
public class LogisticsInfo {
    @TableId(type = IdType.AUTO)
    private Long id;                // 主键

    private String trackingNumber;  // 快递单号

    private Long orderDetailId;     // 订单项ID

    private String logisticsCompany; // 物流公司

    private String startFrom;       // 发货地点

    private String currentLocation; // 当前位置

    private String destination;     // 目的地

    private Date shippedAt;         // 发货时间

    private Date deliveredAt;       // 签收时间

    private Date createdAt;         // 创建时间

    private Date updatedAt;         // 更新时间
}
