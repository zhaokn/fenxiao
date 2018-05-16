package com.tanie.fenxiao.dao;

import com.tanie.fenxiao.entities.Commission;

import java.util.List;

public abstract interface ICommissionDao extends IBaseDao<Commission> {
	public abstract List<Commission> getByUser(Integer paramInteger);
}
