package com.lenovo.vctl.cloudstorage.ftp.validate;

public class Validator {
	public static void validateStringIsNotEmpty(String className, String methodName, String paramsName, String value)throws Exception{
		if(value == null || value.trim().equals("")) throw new Exception(className +" "+ methodName + " " + paramsName +" validateStringIsEmpty error");
	}
	
	
	public static void main(String[] args) {
		String xx = "";
		try {
			Validator.validateStringIsNotEmpty("Validator","main","tmp",xx);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
