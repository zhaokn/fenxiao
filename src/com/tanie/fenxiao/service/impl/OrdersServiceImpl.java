package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.dao.IOrdersDao;
import com.tanie.fenxiao.entities.Orders;
import com.tanie.fenxiao.service.IOrdersService;

import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("ordersService")
@Scope("prototype")
public class OrdersServiceImpl<T extends Orders> extends BaseServiceImpl<T>
		implements IOrdersService<T> {

	@Resource(name = "ordersDao")
	private IOrdersDao ordersDao;

	public Orders findByNo(String no) {
		return this.ordersDao.findByNo(no);
	}
}
