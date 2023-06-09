package com.li.blog.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.annotations.VisibleForTesting;
import com.li.blog.bean.PageDTO;
import com.li.blog.bean.R;
import com.li.blog.bean.UnCheck;
import com.li.blog.entity.dto.ArticleDTO;
import com.li.blog.entity.dto.QueryArticleDTO;
import com.li.blog.entity.po.Article;
import com.li.blog.entity.vo.ArticleTimeVo;
import com.li.blog.entity.vo.ArticleVO;
import com.li.blog.entity.vo.LabelVo;
import com.li.blog.entity.vo.UserVo;
import com.li.blog.service.ArticleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author nine
 * @since 2023-04-12
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @PostMapping("/getArticlePage")
    @UnCheck
    @ApiOperation(value = "文章分页")
    public R<IPage<ArticleVO>> getArticlePage(@RequestBody @Validated QueryArticleDTO pageDTO){
        return articleService.getArticlePage(pageDTO);
    }

    @UnCheck
    @GetMapping("/getLabel")
    @ApiOperation(value = "分类标签")
    public R<List<LabelVo>> getLabel() {
        return articleService.getLabel();
    }

    @UnCheck
    @GetMapping("/getArticle")
    @ApiOperation(value = "文章详情")
    public R<Article> getArticle(String id) {
        return articleService.getArticle(id);
    }

    @UnCheck
    @GetMapping("/getArticleTIme")
    @ApiOperation(value = "文章归档")
    public R<List<ArticleTimeVo>> getArticleTIme(){
        return articleService.getArticleTIme();
    }


    @PostMapping("/addArticle")
    @ApiOperation(value = "新增文章")
    R<String> addArticle(@RequestBody @Validated ArticleDTO articleDTO){
        return articleService.addArticle(articleDTO);
    }

    @PostMapping("/updateArticle")
    @ApiOperation(value = "更新文章")
    R<String> updateArticle(@RequestBody @Validated ArticleDTO articleDTO){
        return articleService.updateArticle(articleDTO);
    }

    @GetMapping("/deleteArticle")
    public R<Article> deleteArticle(String id){
        return articleService.deleteArticle(id);
    }
}

