package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.dao.IBaseDao;
import com.tanie.fenxiao.entities.Kami;
import com.tanie.fenxiao.entities.Product;
import com.tanie.fenxiao.service.IKamiService;
import com.tanie.fenxiao.service.IProductService;

import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("productService")
@Scope("prototype")
public class ProductServiceImpl<T extends Product> extends BaseServiceImpl<T>
		implements IProductService<T> {

	@Resource(name = "kamiService")
	private IKamiService<Kami> kamiService;

	public boolean delete(T baseBean) {
		return this.baseDao.delete(baseBean);
	}
}
