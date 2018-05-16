package com.tanie.fenxiao.dao.impl;

import com.tanie.fenxiao.dao.IProductDao;
import com.tanie.fenxiao.entities.Product;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("productDao")
@Scope("prototype")
public class ProductDaoImpl<T extends Product> extends BaseDaoImpl<T> implements
		IProductDao<T> {
}
