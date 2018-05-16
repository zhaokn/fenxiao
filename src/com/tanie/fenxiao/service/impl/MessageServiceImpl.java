package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.entities.Message;
import com.tanie.fenxiao.service.IMessageService;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("messageService")
@Scope("prototype")
public class MessageServiceImpl<T extends Message> extends BaseServiceImpl<T>
		implements IMessageService<T> {
}
