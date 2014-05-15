package com.lenovo.vctl.ftp;

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

    /**
     * 
     * @return
     * @author
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("FromItem[");
        buffer.append("ftpItems = ").append(ftpItems);
        buffer.append(",\n name = ").append(name);
        buffer.append("]");
        return buffer.toString();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
