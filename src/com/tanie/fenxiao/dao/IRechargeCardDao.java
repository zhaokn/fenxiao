package com.tanie.fenxiao.dao;

import com.tanie.fenxiao.entities.RechargeCard;

public abstract interface IRechargeCardDao extends IBaseDao<RechargeCard> {
	public abstract RechargeCard getByNo(String paramString);
}
