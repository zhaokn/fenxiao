package com.tanie.fenxiao.dao;

import com.tanie.fenxiao.entities.PhoneValidateCode;

public abstract interface IPhoneValidateCodeDao<T extends PhoneValidateCode>
		extends IBaseDao<T> {
	public abstract PhoneValidateCode getPhoneValidateCode(String paramString1,
			String paramString2);
}
