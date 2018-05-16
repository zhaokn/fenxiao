package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.dao.IArticleCateDao;
import com.tanie.fenxiao.entities.ArticleCate;
import com.tanie.fenxiao.service.IArticleCateService;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("articleCateService")
@Scope("prototype")
public class ArticleCateServiceImpl<T extends ArticleCate> extends
		BaseServiceImpl<T> implements IArticleCateService<T> {

	@Resource(name = "articleCateDao")
	private IArticleCateDao<T> articleCateDao;

	public List<T> listByFatherId(int fid) {
		return this.articleCateDao.listByFatherId(fid);
	}
}
