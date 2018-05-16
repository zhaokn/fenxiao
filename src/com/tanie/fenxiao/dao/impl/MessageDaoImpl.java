package com.tanie.fenxiao.dao.impl;

import com.tanie.fenxiao.dao.IMessageDao;
import com.tanie.fenxiao.entities.Message;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("messageDao")
@Scope("prototype")
public class MessageDaoImpl<T extends Message> extends BaseDaoImpl<T> implements
		IMessageDao<T> {
}
