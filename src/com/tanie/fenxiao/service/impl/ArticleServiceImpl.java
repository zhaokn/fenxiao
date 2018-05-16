package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.entities.Article;
import com.tanie.fenxiao.service.IArticleService;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("articleService")
@Scope("prototype")
public class ArticleServiceImpl<T extends Article> extends BaseServiceImpl<T>
		implements IArticleService<T> {
}
