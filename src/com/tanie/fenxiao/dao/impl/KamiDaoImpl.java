package com.tanie.fenxiao.dao.impl;

import com.tanie.fenxiao.dao.IKamiDao;
import com.tanie.fenxiao.entities.Kami;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("kamiDao")
@Scope("prototype")
public class KamiDaoImpl<T extends Kami> extends BaseDaoImpl<T> implements
		IKamiDao<T> {
}
