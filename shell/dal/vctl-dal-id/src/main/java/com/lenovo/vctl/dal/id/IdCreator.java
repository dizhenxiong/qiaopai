package com.lenovo.vctl.dal.id;

import com.lenovo.vctl.dal.id.exception.CreateIdException;

public interface IdCreator {
	
	public Long nextId(String sKey) throws CreateIdException;
	
}
