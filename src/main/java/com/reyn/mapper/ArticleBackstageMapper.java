package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 知识库文章Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-13
 */
@Mapper
public interface ArticleBackstageMapper extends BaseMapper<Article>
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
    public List<Article> selectArticleList(Article article);

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
     * 删除知识库文章
     * 
     * @param id 知识库文章主键
     * @return 结果
     */
    public int deleteArticleById(Long id);

    /**
     * 批量删除知识库文章
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteArticleByIds(Long[] ids);
}
