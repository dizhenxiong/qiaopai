package com.lenovo.vctl.dal.dao.datasource;

import java.sql.SQLFeatureNotSupportedException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    private static Logger logger = Logger.getLogger(DynamicDataSource.class);

    protected Object determineCurrentLookupKey() {
        Object datasourceName = ContextHolder.getDataSource();
        //logger.info("current datasourceName is: " + ObjectUtils.toString(datasourceName, "idCenterDS"));
        if (datasourceName == null) {
            return "idCenterDS";
        } else {
            return datasourceName;
        }
    }


	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
