package com.tanie.fenxiao.dao;

import com.tanie.fenxiao.entities.ArticleCate;

import java.util.List;

public abstract interface IArticleCateDao<T extends ArticleCate> extends
		IBaseDao<T> {
	public abstract List<T> listByFatherId(int paramInt);
}
