package com.tanie.fenxiao.dao.impl;

import com.tanie.fenxiao.dao.IRechargeCardDao;
import com.tanie.fenxiao.entities.RechargeCard;

import javax.annotation.Resource;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("rechargeCardDao")
@Scope("prototype")
public class RechargeCardDaoImpl extends BaseDaoImpl<RechargeCard> implements
		IRechargeCardDao {

	@Resource(name = "sessionFactory")
	private SessionFactory sessionFactory;

	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}

	public RechargeCard getByNo(String no) {
		String hql = "from RechargeCard where no like '%" + no
				+ "%' and deleted=0";
		RechargeCard rechargeCard = (RechargeCard) getSession()
				.createQuery(hql).uniqueResult();
		return rechargeCard;
	}
}
