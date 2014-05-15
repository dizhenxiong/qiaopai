package com.lenovo.vctl.apps.commons.utils;

public class FlagUtil {
	
	/**
	 * 判断给定值的某二进制位是否为1
	 * @param value : 给定值
	 * @param flagType ：标志位
	 * @return true标识该位为1，false表示该位为0
	 */
	public static boolean hasSetFlag(long value, long flagType) {
		if (value < 0L || flagType < 0L) return false;
		
		return (value & flagType) > 0 ? true : false;
	}
}
