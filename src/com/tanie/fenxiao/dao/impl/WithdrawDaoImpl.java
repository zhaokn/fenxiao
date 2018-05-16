package com.tanie.fenxiao.dao.impl;

import com.tanie.fenxiao.dao.IWithdrawDao;
import com.tanie.fenxiao.entities.Withdraw;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("withdrawDao")
@Scope("prototype")
public class WithdrawDaoImpl extends BaseDaoImpl<Withdraw> implements
		IWithdrawDao {
}
