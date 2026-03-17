package com.reyn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.Article;
import com.reyn.mapper.ArticleBackstageMapper;
import com.reyn.service.IArticleBackstageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 知识库文章Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-13
 */
@Service
public class ArticleBackstageServiceImpl implements IArticleBackstageService
{
    @Autowired
    private ArticleBackstageMapper articleBackstageMapper;

    /**
     * 查询知识库文章
     * 
     * @param id 知识库文章主键
     * @return 知识库文章
     */
    @Override
    public Article selectArticleById(Long id)
    {
        return articleBackstageMapper.selectArticleById(id);
    }

    /**
     * 查询知识库文章列表
     * 
     * @param article 知识库文章
     * @return 知识库文章
     */
    @Override
    public Page<Article> selectArticleList(Page page, Article article)
    {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        // 封装 title 查询条件
        if (article.getTitle() != null && !article.getTitle().isEmpty()) {
            queryWrapper.like("title", article.getTitle());
        }

        // 封装 authorName 查询条件
        if (article.getAuthorName() != null && !article.getAuthorName().isEmpty()) {
            queryWrapper.like("author_name", article.getAuthorName());
        }

        // 封装 category 查询条件
        if (article.getCategory() != null && !article.getCategory().isEmpty()) {
            queryWrapper.like("category", article.getCategory());
        }

        // 封装 articleStatus 查询条件
        if (article.getArticleStatus() != null) {
            queryWrapper.eq("article_status", article.getArticleStatus());
        }

        // 执行查询
        return articleBackstageMapper.selectPage(page, queryWrapper);
    }

    /**
     * 新增知识库文章
     * 
     * @param article 知识库文章
     * @return 结果
     */
    @Override
    public int insertArticle(Article article)
    {
        return articleBackstageMapper.insertArticle(article);
    }

    /**
     * 修改知识库文章
     * 
     * @param article 知识库文章
     * @return 结果
     */
    @Override
    public int updateArticle(Article article)
    {
        return articleBackstageMapper.updateArticle(article);
    }

    /**
     * 批量删除知识库文章
     * 
     * @param ids 需要删除的知识库文章主键
     * @return 结果
     */
    @Override
    public int deleteArticleByIds(Long[] ids)
    {
        return articleBackstageMapper.deleteArticleByIds(ids);
    }

    /**
     * 删除知识库文章信息
     * 
     * @param id 知识库文章主键
     * @return 结果
     */
    @Override
    public int deleteArticleById(Long id)
    {
        return articleBackstageMapper.deleteArticleById(id);
    }
}
