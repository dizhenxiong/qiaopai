package com.lenovo.vctl.storage.test;

public class StringTest {
	public static void main(String[] args) {
		String test = "http://p1.ifaceshow.com/a/2012/1010/1900/abcccde_12394_abc.jpg";
		System.out.println(test.replaceAll("http://", "").replaceAll("\\.ifaceshow.com", ""));
	}
}
