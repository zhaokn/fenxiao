package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.dao.IProductCateDao;
import com.tanie.fenxiao.entities.Product;
import com.tanie.fenxiao.entities.ProductCate;
import com.tanie.fenxiao.service.IProductCateService;
import com.tanie.fenxiao.service.IProductService;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("productCateService")
@Scope("prototype")
public class ProductCateServiceImpl<T extends ProductCate> extends
		BaseServiceImpl<T> implements IProductCateService<T> {

	@Resource(name = "productCateDao")
	private IProductCateDao<T> productCateDao;

	@Resource(name = "productService")
	private IProductService<Product> productService;

	public boolean delete(T baseBean) {
		return this.productCateDao.delete(baseBean);
	}

	public List<T> listByFatherId(int fid) {
		return this.productCateDao.listByFatherId(fid);
	}
}
