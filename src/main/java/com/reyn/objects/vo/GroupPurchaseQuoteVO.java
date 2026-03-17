package com.reyn.objects.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GroupPurchaseQuoteVO {
    private Long id;
    private Long requestId;
    private String requestTitle;    // 求购标题
    private Byte requestStatus;     // 求购状态：1-进行中，2-已成交，3-已过期
    private BigDecimal quotedPrice; // 报价单价
    private String deliveryDesc;    // 发货说明
    private String productImage;    // 货品图片
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
