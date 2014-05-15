package com.lenovo.vctl.cloudstorage;

/**
 * Created with IntelliJ IDEA.
 * User: kangyang1
 * Date: 14-5-15
 * Time: 上午11:43
 * To change this template use File | Settings | File Templates.
 */
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
