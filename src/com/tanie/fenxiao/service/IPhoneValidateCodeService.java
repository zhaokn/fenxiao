package com.tanie.fenxiao.service;

import com.tanie.fenxiao.entities.PhoneValidateCode;

public abstract interface IPhoneValidateCodeService<T extends PhoneValidateCode>
		extends IBaseService<T> {
	public abstract PhoneValidateCode getPhoneValidateCode(String paramString1,
			String paramString2);
}
