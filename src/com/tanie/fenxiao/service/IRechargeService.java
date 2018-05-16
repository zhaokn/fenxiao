package com.tanie.fenxiao.service;

import com.tanie.fenxiao.entities.Recharge;

public abstract interface IRechargeService<T extends Recharge> extends
		IBaseService<T> {
	public abstract Recharge findByNo(String paramString);
}
