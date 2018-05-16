package com.tanie.fenxiao.dao.impl;

import com.tanie.fenxiao.dao.IArticleCateDao;
import com.tanie.fenxiao.entities.ArticleCate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("articleCateDao")
@Scope("prototype")
public class ArticleCateDaoImpl<T extends ArticleCate> extends BaseDaoImpl<T>
		implements IArticleCateDao<T> {
	public List<T> listByFatherId(int fid) {
		String hql = "from ArticleCate where fatherId=" + fid;
		List list = createQuery(hql).list();
		return list;
	}
}
