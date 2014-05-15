package com.lenovo.vctl.cloudstorage.ftp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * @author songkun1
 * 
 */
public final class FromItem implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4010830435125273896L;
    private String name = "default";
    private List<FtpItem> ftpItems = new ArrayList<FtpItem>();
    private String alias;
    private boolean convert =true;
    
	private int maxActive = -1; //连接池 最多的活跃对象数
	private int maxIdle = -1; //连接池最多的闲置连接数
	private int maxWait = -1; //获取连接最大的等待时间
	private int minEvictableIdleTimeMillis = -1; //最小的整理idle对象 时间
    
    public boolean isConvert() {
		return convert;
	}

	public void setConvert(boolean convert) {
		this.convert = convert;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    
    
    public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public int getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	
	
	public void setMinEvictableIdleTimeMillis(
			int minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public void addFtpItem(FtpItem ftpItem) {
        if (ftpItem != null && StringUtils.equalsIgnoreCase(ftpItem.getStatus(), "run")) {
            int iWeight = NumberUtils.toInt(ftpItem.getWeight(), 1);
            String temp_alias = this.getAlias();
            temp_alias = StringUtils.isNotBlank(temp_alias) ? "/" + temp_alias : "";
            String baseDir = ftpItem.getPath() + temp_alias;
            String baseUrl = ftpItem.getDomain() + temp_alias;
            ftpItem.setBaseDir(baseDir);
            ftpItem.setBaseUrl(baseUrl);
            for (int i = 0; i < iWeight; i++) {
                ftpItems.add(ftpItem);
            }
        }
    }

    public List<FtpItem> getFtpItems() {
        return ftpItems;
    }


    

    @Override
	public String toString() {
		return "FromItem [name=" + name + ", ftpItems=" + ftpItems + ", alias="
				+ alias + ", convert=" + convert + ", maxActive=" + maxActive
				+ ", maxIdle=" + maxIdle + ", maxWait=" + maxWait
				+ ", minEvictableIdleTimeMillis=" + minEvictableIdleTimeMillis
				+ "]";
	}

	public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
