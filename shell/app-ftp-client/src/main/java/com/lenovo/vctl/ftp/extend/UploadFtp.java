package com.lenovo.vctl.ftp.extend;

import org.apache.commons.net.ftp.FTPClient;

import com.lenovo.vctl.ftp.FtpItem;



public class UploadFtp extends FTPClient {
    private FtpItem ftpItem;

    public FtpItem getFtpItem() {
        return ftpItem;
    }

    public void setFtpItem(FtpItem ftpItem) {
        this.ftpItem = ftpItem;
    }

}
