package com.tanie.fenxiao.dao;

import com.tanie.fenxiao.entities.ProductCate;

import java.util.List;

public abstract interface IProductCateDao<T extends ProductCate> extends
		IBaseDao<T> {
	public abstract List<T> listByFatherId(int paramInt);
}
