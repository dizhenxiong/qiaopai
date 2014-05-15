package com.lenovo.vctl.cloudstorage.exception;

public class CloudStorageException extends Exception{

	private static final long serialVersionUID = 1L;

	public CloudStorageException(){
	}
	
	public CloudStorageException(Exception e){
		super(e);
	}
	public CloudStorageException(String e){
		super(e);
	}
}
