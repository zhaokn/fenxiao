package com.tanie.fenxiao.service;

import com.tanie.fenxiao.entities.Message;

public abstract interface IMessageService<T extends Message> extends
		IBaseService<T> {
}
