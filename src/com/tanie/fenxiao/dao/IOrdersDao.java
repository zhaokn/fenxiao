package com.tanie.fenxiao.dao;

import com.tanie.fenxiao.entities.Orders;

public abstract interface IOrdersDao extends IBaseDao<Orders> {
	public abstract Orders findByNo(String paramString);
}
