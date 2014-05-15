package com.lenovo.vctl.dal.id.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;

import com.lenovo.vctl.dal.id.IdCreator;
import com.lenovo.vctl.dal.id.TimeIdHelper;
import com.lenovo.vctl.dal.id.exception.CreateIdException;

public class TimeIdCreator implements IdCreator {

	private ConcurrentMap<String, AtomicLong> automicLongMap = new ConcurrentHashMap<String, AtomicLong>();
	private Integer idc = null;

	@Override
	public Long nextId(String sKey) throws CreateIdException {

		if (StringUtils.isEmpty(sKey)) {
			throw new CreateIdException("sKey must have value");
		} else {
			String atomicKey = sKey;
			AtomicLong seqAtomic = automicLongMap.get(atomicKey);

			if (seqAtomic == null) {
				AtomicLong memAtomic = new AtomicLong(0);
				seqAtomic = automicLongMap.putIfAbsent(atomicKey, memAtomic);
				seqAtomic = seqAtomic == null ? memAtomic : seqAtomic;
			}

			return TimeIdHelper.getIdByDate(System.currentTimeMillis(), seqAtomic, idc);

		}
	}

	public TimeIdCreator(Integer idc) {
		this.idc = idc;
	}

}
