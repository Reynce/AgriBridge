package com.reyn.objects.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("product_traceability")
public class ProductTraceability {
    @TableId(type = IdType.AUTO)
    private Long id;                    // 主键ID

    private Long productId;             // 关联的商品ID（与商品表一对一）

    // 种植环节
    private String farmName;            // 种植基地名称
    private String farmAddress;         // 基地详细地址
    private String farmer;              // 负责人/农户姓名
    private String farmImageUrl;        // 基地实景图URL（可为空）

    // 采摘环节
    private LocalDate harvestDate;      // 采摘日期
    private String harvester;           // 采摘人员或团队
    private String weather;             // 采摘当日天气情况
    private String harvestImageUrl;     // 采摘现场图URL（可为空）

    // 检测环节
    private String inspectionAgency;    // 检测机构名称
    private LocalDate inspectionDate;   // 检测日期
    private Boolean isPassed;           // 是否检测合格：true-合格，false-不合格
    private String inspectionReportUrl; // 检测报告图片或PDF链接

    // 包装环节
    private LocalDate packageDate;      // 包装日期
    private String packageSpec;         // 包装规格（如：5斤/礼盒、10kg/箱）
    private String packageUrl;          // 包装过程图片

    // 发货环节
    private LocalDate shipDate;         // 发货日期
    private String logistics;           // 物流公司名称

    private LocalDateTime createdAt;    // 记录创建时间
    private LocalDateTime updatedAt;    // 记录最后更新时间
}
