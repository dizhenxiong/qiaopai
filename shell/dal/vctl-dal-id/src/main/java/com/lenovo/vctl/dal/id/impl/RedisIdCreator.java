package com.lenovo.vctl.dal.id.impl;

import com.lenovo.vctl.dal.id.IdCreator;
import com.lenovo.vctl.dal.id.exception.CreateIdException;

public class RedisIdCreator implements IdCreator {
	
	
	
	
	@Override
	public Long nextId(String sKey) throws CreateIdException {
		return null;
	}

}
