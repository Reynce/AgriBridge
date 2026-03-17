package com.reyn.objects.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 订单详情对象 order_detail
 *
 */
@Data
@TableName("order_detail")
public class OrderDetail
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 规格id,用于释放库存 */
    private Long skuId;

    /** 商品id */
    private Long productId;

    /** 卖家ID */
    private Long sallerId;

    /** 商品标题快照 */
    private String productTitle;

    /** 规格描述快照（如 颜色:红;尺寸:L） */
    private String skuSpecification;

    /** 单价 */
    private BigDecimal price;

    /** 购买数量 */
    private Integer quantity;

    /** 商品主图快照URL */
    private String productMainImage;

    /** 物流状态：0未发货，1已发货，2已签收 */
    private Long shippedStatus;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    /** 是否评价 */
    private Byte isReview;
}
