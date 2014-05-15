package com.lenovo.vctl.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


public class FtpConfig {
	private static String CONFIG_FILE = "ftpserver.xml";
	private static FtpConfig config;
	private static Logger logger = Logger.getLogger(FtpConfig.class);
	private Map<String, FromItem> fromItemMap = new HashMap<String, FromItem>();
	private FromItem defaultFromItem = null;

	public static FtpConfig getInstance() {
		if (config == null) {
			synchronized (FtpConfig.class) {
				if (config == null) {
					config = new FtpConfig();
				}
			}
		}
		return config;
	}

	private FtpConfig() {
		try {
			this.init(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE));
			// this.datasourceAssign();
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				e.printStackTrace(System.out);
			} else {
				logger.error("init config file " + CONFIG_FILE + " error: " + e.getMessage());
			}
		}
	}

	/**
	 * 解析Object的配置
	 * 
	 * @param groupConfigFile
	 */
	private void init(InputStream groupConfigFile) {
		Digester digester = new Digester();
		digester.setValidating(false);

		// 对象
		digester.addObjectCreate("root", ArrayList.class);

		digester.addObjectCreate("root/default", FromItem.class);
		digester.addSetNext("root/default", "add");
		digester.addSetProperties("root/default");

		digester.addObjectCreate("root/default/ftp", FtpItem.class);
		digester.addSetNext("root/default/ftp", "addFtpItem");
		digester.addSetProperties("root/default/ftp");

		digester.addObjectCreate("root/from", FromItem.class);
		digester.addSetNext("root/from", "add");
		digester.addSetProperties("root/from");

		digester.addObjectCreate("root/from/ftp", FtpItem.class);
		digester.addSetNext("root/from/ftp", "addFtpItem");
		digester.addSetProperties("root/from/ftp");

		try {
			Object objectList = digester.parse(groupConfigFile);
			if (objectList != null && objectList instanceof ArrayList) {
				List fromItems = (List) objectList;
				for (Iterator iterator = fromItems.iterator(); iterator.hasNext();) {
					FromItem fromItem = (FromItem) iterator.next();
					if (fromItem != null) {
						if (StringUtils.isEmpty(fromItem.getName())) {
							logger.error("From tag must have value of name property");
							continue;
						}
						if (!StringUtils.equalsIgnoreCase("default", fromItem.getName())) {
							if (fromItemMap.containsKey(fromItem.getName())) {
								logger.error("Duplicate " + fromItem.getName() + " From Tag - This From may already exist more than two in " + CONFIG_FILE
										+ ". Please check");
							} else {
								fromItemMap.put(fromItem.getName(), fromItem);
							}
						} else {
							if (defaultFromItem != null) {
								logger.error("Duplicate default From Tag - This From may already exist more than two in " + CONFIG_FILE + ". Please check");
							} else {
								defaultFromItem = fromItem;
							}
						}
					}
				}
			}
		} catch (IOException e) {
			if (logger.isDebugEnabled()) {
				e.printStackTrace(System.err);
			} else {
				logger.error("init error: " + e.getMessage());
			}
		} catch (SAXException e) {
			if (logger.isDebugEnabled()) {
				e.printStackTrace(System.err);
			} else {
				logger.error("init parse fail : " + e.getMessage());
			}
		}

	}

	/**
	 * return FromItem by fromItemName, if fromItemName not exist!, return
	 * defaultFromItem
	 * 
	 * @param fromItemName
	 * @return
	 */
	public FromItem getFromItem(String fromItemName) {
		if (StringUtils.isEmpty(fromItemName)) {
			return defaultFromItem;
		}
		FromItem fromItem = fromItemMap.get(fromItemName);
		System.out.println("fromItem =" + fromItem);
		return fromItem == null ? defaultFromItem : fromItem;
	}

	/**
	 * 
	 * @param fromItemName
	 * @return
	 */
	public boolean isExist(String fromItemName) {
		return StringUtils.isBlank(fromItemName) ? false : fromItemMap.containsKey(fromItemName);
	}

	public static void main(String[] args) {
		FromItem fromItem = FtpConfig.getInstance().getFromItem(null);
		System.out.println(fromItem);
	}

	/**
	 * return FromItem by fromItemName, if fromItemName not exist!, return
	 * defaultFromItem
	 * 
	 * @param fromItemName
	 * @return
	 */
	public String getAlias(String from) {
		String alias;
		if (StringUtils.isEmpty(from)) {
			alias = defaultFromItem.getAlias();
		} else {
			FromItem fromItem = fromItemMap.get(from);
			fromItem = fromItem == null ? defaultFromItem : fromItem;
			alias = fromItem == null ? "" : fromItem.getAlias();
		}
		return alias == null ? from : alias;
	}

	public Map<String, FromItem> getFromItemMap() {
		return this.fromItemMap;
	}

	public FromItem getDefaultFromItem() {
		return defaultFromItem;
	}

}
