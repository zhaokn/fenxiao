package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.dao.IPhoneValidateCodeDao;
import com.tanie.fenxiao.entities.PhoneValidateCode;
import com.tanie.fenxiao.service.IPhoneValidateCodeService;

import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("phoneValidateCodeService")
@Scope("prototype")
public class PhoneValidateCodeServiceImpl<T extends PhoneValidateCode> extends
		BaseServiceImpl<T> implements IPhoneValidateCodeService<T> {

	@Resource(name = "phoneValidateCodeDao")
	private IPhoneValidateCodeDao<PhoneValidateCode> phoneValidateCodeDao;

	public PhoneValidateCode getPhoneValidateCode(String phone, String code) {
		return this.phoneValidateCodeDao.getPhoneValidateCode(phone, code);
	}
}
