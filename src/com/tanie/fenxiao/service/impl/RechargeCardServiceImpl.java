package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.dao.IRechargeCardDao;
import com.tanie.fenxiao.entities.RechargeCard;
import com.tanie.fenxiao.service.IRechargeCardService;

import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("rechargeCardService")
@Scope("prototype")
public class RechargeCardServiceImpl<T extends RechargeCard> extends
		BaseServiceImpl<T> implements IRechargeCardService<T> {

	@Resource(name = "rechargeCardDao")
	IRechargeCardDao rechargeCardDao;

	public RechargeCard getByNo(String no) {
		return this.rechargeCardDao.getByNo(no);
	}
}
