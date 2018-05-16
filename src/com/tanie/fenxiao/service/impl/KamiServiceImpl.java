package com.tanie.fenxiao.service.impl;

import com.tanie.fenxiao.entities.Kami;
import com.tanie.fenxiao.service.IKamiService;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("kamiService")
@Scope("prototype")
public class KamiServiceImpl<T extends Kami> extends BaseServiceImpl<T>
		implements IKamiService<T> {
}
