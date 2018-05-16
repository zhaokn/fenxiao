package com.tanie.fenxiao.service;

import com.tanie.fenxiao.entities.Orders;

public abstract interface IOrdersService<T extends Orders> extends
		IBaseService<T> {
	public abstract Orders findByNo(String paramString);
}
