package com.reyn.objects.entity;

import java.util.Date;

/**
 * 知识库文章对象 article
 *
 */
public class Article
{
    private static final long serialVersionUID = 1L;

    /** 文章ID，主键 */
    private Long id;

    /** 文章标题 */
    private String title;

    /** 摘要/简介 */
    private String summary;

    /** 富文本内容，支持HTML或Markdown */
    private String content;

    /** 作者姓名 */
    private String authorName;

    /** 分类：tech/policy/market 等 */
    private String category;

    /** 状态：0-草稿 1-已发布 */
    private Long articleStatus;

    /** 发布时间（草稿时为NULL） */
    private Date publishedAt;

    /** 创建时间 */
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setSummary(String summary) 
    {
        this.summary = summary;
    }

    public String getSummary() 
    {
        return summary;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setAuthorName(String authorName) 
    {
        this.authorName = authorName;
    }

    public String getAuthorName() 
    {
        return authorName;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setArticleStatus(Long articleStatus) 
    {
        this.articleStatus = articleStatus;
    }

    public Long getArticleStatus() 
    {
        return articleStatus;
    }

    public void setPublishedAt(Date publishedAt) 
    {
        this.publishedAt = publishedAt;
    }

    public Date getPublishedAt() 
    {
        return publishedAt;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }
}
