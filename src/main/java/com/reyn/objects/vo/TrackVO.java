package com.reyn.objects.vo;

import lombok.Data;

@Data
public class TrackVO {
    private String status;      // 物流状态描述
    private String location;    // 位置信息
    private String time;        // 时间
}
