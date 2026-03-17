package com.reyn.objects.dto;

import com.reyn.objects.enums.RecType;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 首页推荐/轮播图 DTO
 * 包含商品基本信息 + 协同过滤算法生成的推荐特征
 */
@Data
public class HomeRecommendationDTO {

    // ================= 商品基础信息 (来自 product, sku, merchant) =================

    private Long productId;
    private Long userId;            // 用户ID (用于购买历史关联)
    private String title;
    private String brief;
    private String mainImage;       // 商品主图 (来自 product_image)
    private BigDecimal price;       // 当前最低售价或默认 SKU 价格 (来自 sku)
    private Integer sales;          // 销量 (来自 product)
    private Long merchantId;        // 商家ID
    private String shopName;        // 店铺名称 (来自 merchant)
    private String categoryName;    // 分类名称 (来自category)

    // ================= 溯源特色信息 (来自 product_traceability) =================
    // 如果是农产品，展示溯源标签能增加点击率
    private Boolean hasTraceability; // 是否有溯源信息

    // ================= 协同过滤算法特征 (核心亮点) =================

    /**
     * 推荐类型/场景
     * 枚举值:
     * - HOT_SALE (热销爆款 - 基于全局统计)
     * - PERSONALIZED (千人千面 - 基于 User-Based CF)
     * - SIMILAR_ITEM (看了又看 - 基于 Item-Based CF)
     * - NEW_ARRIVAL (新品推荐 - 基于时间权重)
     */
    private RecType recType;

    /**
     * 推荐理由文案
     * 前端可根据此字段展示不同标签，例如：
     * "和您浏览过的【有机苹果】相似"
     * "您所在地区的用户都买过"
     * "今日热销 TOP 1"
     */
    private String reason;

    /**
     * 算法置信度/相似度得分 (0.0 - 1.0)
     * 用于后端排序，前端通常不直接展示，但可用于控制展示优先级
     */
    private Double score;

    /**
     * 关联标签列表
     * 例如：["有机", "当季", "高复购", "产地直发"]
     * 这些标签可以基于商品属性和用户行为动态生成
     */
    private List<String> tags;
}
