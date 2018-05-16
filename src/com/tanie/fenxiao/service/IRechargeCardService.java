package com.tanie.fenxiao.service;

import com.tanie.fenxiao.entities.RechargeCard;

public abstract interface IRechargeCardService<T extends RechargeCard> extends
		IBaseService<T> {
	public abstract RechargeCard getByNo(String paramString);
}
