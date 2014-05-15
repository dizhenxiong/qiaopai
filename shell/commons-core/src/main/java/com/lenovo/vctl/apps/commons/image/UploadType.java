package com.lenovo.vctl.apps.commons.image;

public enum UploadType {
	PIC("pic", "jpg"), VIDEO("video", "flv"), AUDIO("audio","amr");
    // 成员变量   
    private String type;   
    private String suffix;
    
    // 构造方法   
	private UploadType(String type, String suffix) {
		this.type = type;
		this.suffix = suffix;
	}

	public String getType(){
		return this.type;
	}
	
	public String getSuffix(){
		return this.suffix;
	}
}
