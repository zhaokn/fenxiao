package com.tanie.fenxiao.service;

import com.tanie.fenxiao.entities.Product;

public abstract interface IProductService<T extends Product> extends
		IBaseService<T> {
}
