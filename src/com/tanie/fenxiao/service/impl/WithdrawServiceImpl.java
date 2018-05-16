package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.entities.Withdraw;
import com.tanie.fenxiao.service.IWithdrawService;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("withdrawService")
@Scope("prototype")
public class WithdrawServiceImpl<T extends Withdraw> extends BaseServiceImpl<T>
		implements IWithdrawService<T> {
}
