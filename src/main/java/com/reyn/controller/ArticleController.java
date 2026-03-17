package com.reyn.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.dto.ArticleSaveDTO;
import com.reyn.service.ArticleService;
import com.reyn.utils.PageResult;
import com.reyn.objects.vo.ArticleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 获取文章列表（分页）
     */
    @GetMapping("/list")
    public SaResult getArticleList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return SaResult.data(articleService.getArticleList(current, size, 1));
    }

    /**
     * 获取单篇文章详情
     */
    @GetMapping("/{id}")
    public SaResult getArticleDetail(@PathVariable Long id) {
        return SaResult.data(articleService.getArticleDetail(id));
    }

    /**
     * 删除单篇文章
     */
    @DeleteMapping("/{id}")
    public SaResult deleteArticleById(@PathVariable Long id) {
        articleService.deleteArticleById(id);
        return SaResult.ok("文章删除成功");
    }

    /**
     * 根据关键词搜索文章
     */
    @GetMapping("/search")
    public SaResult searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return SaResult.data(articleService.searchArticles(keyword, current, size));
    }

    /**
     * 保存文章
     */
    @SaCheckRole("ROLE_ADMIN")
    @PostMapping("/save")
    public SaResult saveArticle(@RequestBody @Valid ArticleSaveDTO articleSaveDTO) {
        articleService.saveArticle(articleSaveDTO);
        return SaResult.ok("文章保存成功");
    }

    /**
     * 管理员接口,获取所有文章详情
     */
    @GetMapping("/listAll")
    @SaCheckRole("ROLE_ADMIN")
    public SaResult getAllArticleList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return SaResult.data(articleService.getAllArticleList(current, size));
    }

}
