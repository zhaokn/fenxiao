package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.dao.IFinancialDao;
import com.tanie.fenxiao.entities.Financial;
import com.tanie.fenxiao.service.IFinancialService;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("financialService")
@Scope("prototype")
public class FinancialServiceImpl<T extends Financial> extends
		BaseServiceImpl<T> implements IFinancialService<T> {

	@Resource(name = "financialDao")
	private IFinancialDao financialDao;

	public List<Financial> getByUser(Integer userId) {
		return this.financialDao.getByUser(userId);
	}
}
