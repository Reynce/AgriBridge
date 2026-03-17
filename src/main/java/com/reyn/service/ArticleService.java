package com.reyn.service;

import com.reyn.objects.dto.ArticleSaveDTO;
import com.reyn.objects.vo.ArticleDetailVO;
import com.reyn.utils.PageResult;
import com.reyn.objects.vo.ArticleVO;

public interface ArticleService {
    /**
     * 获取文章列表（分页）
     */
    PageResult<ArticleVO> getArticleList(int current, int size, Integer articleStatus);

    /**
     * 获取单篇文章详情
     */
    ArticleDetailVO getArticleDetail(Long id);

    /**
     * 根据id删除文章
     */
    void deleteArticleById(Long id);

    /**
     * 保存文章（仅限管理员）
     */
    void saveArticle(ArticleSaveDTO articleSaveDTO);

    /**
     * 根据关键词搜索文章（标题优先，简介次之），并分页返回结果
     */
    PageResult<ArticleVO> searchArticles(String keyword, int current, int size);

    PageResult<ArticleVO> getAllArticleList(int current, int size);

}
