package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.Article;
import com.reyn.service.IArticleBackstageService;
import com.reyn.common.core.controller.BaseController;
import com.reyn.objects.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 知识库文章Controller
 * 
 * @author ruoyi
 * @date 2026-02-13
 */
@RestController
@RequestMapping("/article/article")
public class ArticleBackstageController extends BaseController
{
    @Autowired
    private IArticleBackstageService articleService;

    /**
     * 查询知识库文章列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Article article)
    {
        Page page = startPage();
        return getDataTable(articleService.selectArticleList(page, article));
    }

    /**
     * 获取知识库文章详细信息
     */
    @GetMapping(value = "/{id}")
    public SaResult getInfo(@PathVariable("id") Long id)
    {
        return SaResult.data(articleService.selectArticleById(id));
    }

    /**
     * 新增知识库文章
     */
    @PostMapping
    public SaResult add(@RequestBody Article article)
    {
        return SaResult.data(articleService.insertArticle(article));
    }

    /**
     * 修改知识库文章
     */
    @PutMapping
    public SaResult edit(@RequestBody Article article)
    {
        return SaResult.data(articleService.updateArticle(article));
    }

    /**
     * 删除知识库文章
     */
	@DeleteMapping("/{ids}")
    public SaResult remove(@PathVariable Long[] ids)
    {
        return SaResult.data(articleService.deleteArticleByIds(ids));
    }
}
