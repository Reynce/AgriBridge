package com.reyn.objects.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleVO {
    private Long id;                // 文章ID
    private String title;           // 文章标题
    private String summary;         // 摘要
    private String authorName;      // 作者姓名
    private String category;        // 分类
    private Date publishedAt;       // 发布时间
    private Integer articleStatus;  // 状态：0-草稿 1-已发布
}

