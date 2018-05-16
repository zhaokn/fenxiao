package com.tanie.fenxiao.service;

import com.tanie.fenxiao.entities.Admin;

public abstract interface IAdminService<T extends Admin> extends
		IBaseService<T> {
	public abstract Admin login(Admin paramAdmin);

	public abstract Admin getAdminName(String paramString);
}
