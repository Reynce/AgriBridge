package com.reyn.objects.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TraceabilityInfoVO {
    private Long productId;
    private String productName;
    private List<BatchTraceVO> batches; // 批次追踪信息
    private List<FarmingActivityVO> farmingActivities; // 农事活动记录
//    private String productionAddress; // 生产地
//    private LocalDateTime productionDate; // 生产日期
//    private String qualityCertificate; // 质量证书
//    private String inspectionReport; // 检测报告
}

@Data
class BatchTraceVO {
    private Long id;
    private String batchNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer status; // 0-生产中, 1-已完成
}

@Data
class FarmingActivityVO {
    private Long id;
    private String activityTypeName;
    private String activityTime;
    private String costDescription;
    private Double costAmount;
    private Double yieldQuantity;
    private String qualityGrade;
    private String remark;
    private List<String> imageUrls;
}
