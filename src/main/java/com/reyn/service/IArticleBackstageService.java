package com.reyn.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.Article;


/**
 * 知识库文章Service接口
 * 
 * @author ruoyi
 * @date 2026-02-13
 */
public interface IArticleBackstageService
{
    /**
     * 查询知识库文章
     * 
     * @param id 知识库文章主键
     * @return 知识库文章
     */
    public Article selectArticleById(Long id);

    /**
     * 查询知识库文章列表
     * 
     * @param article 知识库文章
     * @return 知识库文章集合
     */
    public Page<Article> selectArticleList(Page page, Article article);

    /**
     * 新增知识库文章
     * 
     * @param article 知识库文章
     * @return 结果
     */
    public int insertArticle(Article article);

    /**
     * 修改知识库文章
     * 
     * @param article 知识库文章
     * @return 结果
     */
    public int updateArticle(Article article);

    /**
     * 批量删除知识库文章
     * 
     * @param ids 需要删除的知识库文章主键集合
     * @return 结果
     */
    public int deleteArticleByIds(Long[] ids);

    /**
     * 删除知识库文章信息
     * 
     * @param id 知识库文章主键
     * @return 结果
     */
    public int deleteArticleById(Long id);
}
