package com.tanie.fenxiao.dao.impl;

import com.tanie.fenxiao.dao.IProductCateDao;
import com.tanie.fenxiao.entities.ProductCate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("productCateDao")
@Scope("prototype")
public class ProductCateDaoImpl<T extends ProductCate> extends BaseDaoImpl<T>
		implements IProductCateDao<T> {
	public List<T> listByFatherId(int fid) {
		String hql = "from ProductCate where fatherId=" + fid;
		List list = createQuery(hql).list();
		return list;
	}
}
