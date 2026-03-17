package com.reyn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.mapper.ArticleMapper;
import com.reyn.objects.dto.ArticleSaveDTO;
import com.reyn.objects.entity.Article;
import com.reyn.objects.vo.ArticleDetailVO;
import com.reyn.objects.vo.ArticleVO;
import com.reyn.service.ArticleService;
import com.reyn.utils.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PageResult<ArticleVO> getArticleList(int current, int size, Integer articleStatus) {
        // 构造分页对象
        Page<Article> page = new Page<>(current, size);

        // 查询条件：只查询已发布的文章
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("article_status", articleStatus); // 已发布状态
        queryWrapper.orderByDesc("published_at"); // 按发布时间倒序

        // 执行分页查询
        Page<Article> resultPage = articleMapper.selectPage(page, queryWrapper);

        // 转换为 VO
        List<ArticleVO> articleVOList = new ArrayList<>();
        for (Article article : resultPage.getRecords()) {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(article, articleVO);
            articleVOList.add(articleVO);
        }

        // 精简返回返回内容,封装为PageResult
        PageResult<ArticleVO> pageResult = PageResult.data(resultPage);

        return pageResult;
    }

    @Override
    public ArticleDetailVO getArticleDetail(Long id) {
        // 查询文章详情
        Article article = articleMapper.selectById(id);
        if (article == null || article.getArticleStatus() != 1) {
            throw new RuntimeException("文章不存在或未发布");
        }

        // 转换为 VO 返回
        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        BeanUtils.copyProperties(article, articleDetailVO);
        return articleDetailVO;
    }

    @Override
    public void saveArticle(ArticleSaveDTO articleSaveDTO) {
        // 将 DTO 转换为实体类
        Article article = new Article();
        BeanUtils.copyProperties(articleSaveDTO, article);

        // 设置默认值
        article.setCreatedAt(new Date());
        article.setUpdatedAt(new Date());

        // 如果是已发布状态，则设置发布时间
        if (article.getArticleStatus() == 1 && article.getPublishedAt() == null) {
            article.setPublishedAt(new Date());
        }

        // 插入数据库
        articleMapper.insert(article);
    }

    @Override
    public PageResult<ArticleVO> searchArticles(String keyword, int current, int size) {
        // 构造分页对象
        Page<Article> page = new Page<>(current, size);

        // 查询标题匹配的文章
        QueryWrapper<Article> titleQueryWrapper = new QueryWrapper<>();
        titleQueryWrapper.like("title", keyword);
        titleQueryWrapper.eq("article_status", 1); // 只查询已发布的文章
        Page<Article> titleResults = articleMapper.selectPage(page, titleQueryWrapper);

        // 查询简介匹配的文章
        QueryWrapper<Article> summaryQueryWrapper = new QueryWrapper<>();
        summaryQueryWrapper.like("summary", keyword);
        summaryQueryWrapper.eq("article_status", 1); // 只查询已发布的文章
        Page<Article> summaryResults = articleMapper.selectPage(page, summaryQueryWrapper);

        // 合并结果并去重
        List<Article> mergedResults = new ArrayList<>();
        mergedResults.addAll(titleResults.getRecords());
        for (Article article : summaryResults.getRecords()) {
            if (!mergedResults.contains(article)) {
                mergedResults.add(article);
            }
        }

        // 转换为 VO 并封装到 PageResult
        List<ArticleVO> articleVOList = new ArrayList<>();
        for (Article article : mergedResults) {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(article, articleVO);
            articleVOList.add(articleVO);
        }

        PageResult<ArticleVO> pageResult = new PageResult<>();
        pageResult.setData(articleVOList);
        pageResult.setCurrent(current);
        pageResult.setSize(size);
        pageResult.setTotal(mergedResults.size()); // 注意：这里总数量需要重新计算
        pageResult.setPages((int) Math.ceil((double) mergedResults.size() / size));

        return pageResult;
    }

    @Override
    public void deleteArticleById(Long id){
        articleMapper.deleteById(id);
    }

    @Override
    public PageResult<ArticleVO> getAllArticleList(int current, int size){
        // 构造分页对象

        return null;
    }

}
