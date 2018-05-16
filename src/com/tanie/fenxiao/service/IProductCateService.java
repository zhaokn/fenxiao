package com.tanie.fenxiao.service;

import com.tanie.fenxiao.entities.ProductCate;

import java.util.List;

public abstract interface IProductCateService<T extends ProductCate> extends
		IBaseService<T> {
	public abstract List<T> listByFatherId(int paramInt);
}
