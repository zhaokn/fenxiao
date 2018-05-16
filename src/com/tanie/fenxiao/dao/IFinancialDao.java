package com.tanie.fenxiao.dao;

import com.tanie.fenxiao.entities.Financial;

import java.util.List;

public abstract interface IFinancialDao extends IBaseDao<Financial> {
	public abstract List<Financial> getByUser(Integer paramInteger);
}
