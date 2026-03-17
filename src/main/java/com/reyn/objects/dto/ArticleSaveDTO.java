// 文件路径: src/main/java/com/reyn/objects/dto/ArticleSaveDTO.java
package com.reyn.objects.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ArticleSaveDTO {

    @NotBlank(message = "文章标题不能为空")
    private String title;               // 文章标题

    private String summary;             // 摘要/简介（可选）

    @NotBlank(message = "文章内容不能为空")
    private String content;             // 富文本内容

    @NotBlank(message = "作者姓名不能为空")
    private String authorName;          // 作者姓名

    @NotBlank(message = "分类不能为空")
    private String category;            // 分类：tech/policy/market 等

    @NotNull(message = "文章状态不能为空")
    private Integer articleStatus;      // 状态：0-草稿 1-已发布
}
