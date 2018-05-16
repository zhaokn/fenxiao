package com.tanie.fenxiao.dao;

import com.tanie.fenxiao.entities.Recharge;

public abstract interface IRechargeDao extends IBaseDao<Recharge> {
	public abstract Recharge findByNo(String paramString);
}
