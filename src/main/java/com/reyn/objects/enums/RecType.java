package com.reyn.objects.enums;

/**
 * 推荐策略类型
 */
public enum RecType {
    HOT_SALE("热销爆款", "大家都在买"),
    PERSONALIZED("猜你喜欢", "根据您的浏览习惯推荐"),
    SIMILAR_ITEM("相似推荐", "与您关注的商品相似"),
    NEW_ARRIVAL("当季新品", "刚刚上架的新鲜好货"),
    TRACEABILITY_HIGH("溯源优选", "全程可追溯的高分商品"); // 结合你毕设特色的类型

    private final String code;
    private final String defaultReason;

    RecType(String code, String defaultReason) {
        this.code = code;
        this.defaultReason = defaultReason;
    }

    public String getCode() { return code; }
    public String getDefaultReason() { return defaultReason; }
}
