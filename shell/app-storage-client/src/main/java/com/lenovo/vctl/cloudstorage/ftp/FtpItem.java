package com.lenovo.vctl.cloudstorage.ftp;

import java.io.Serializable;

/**
 * 
 * @author songkun1
 * 
 */
public final class FtpItem implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -4554305385418565784L;

    private String ip;
    private int port;
    private String domain;
    private String status = "run"; // stop
    private String weight = "1";
    private String path;

    private String passwd;
    private String user;
    
    private transient String baseDir=null; //Store File path directory

    private transient String baseUrl=null; //baseUrl 


    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    /**
         * 
         * @return 
         * @author 
         */
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("FtpItem[");
            buffer.append("domain = ").append(domain);
            buffer.append(",\n ip = ").append(ip);
            buffer.append(",\n passwd = ").append(passwd);
            buffer.append(",\n path = ").append(path);
            buffer.append(",\n port = ").append(port);
            buffer.append(",\n status = ").append(status);
            buffer.append(",\n user = ").append(user);
            buffer.append(",\n weight = ").append(weight);
            buffer.append("]");
            return buffer.toString();
        }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

}
