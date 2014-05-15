package com.lenovo.vctl.redis.client.route.strategy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.lenovo.vctl.redis.client.config.helper.RedisConfigHelper;
import com.lenovo.vctl.redis.client.config.model.KeyPatternItem;
import com.lenovo.vctl.redis.client.exception.StrategyException;
import com.lenovo.vctl.redis.client.route.strategy.IStrategy;


public class DefaultStrategy implements IStrategy {
    private static Logger logger = Logger.getLogger(DefaultStrategy.class);

    public Object playStrategy(String regionName, Object key) throws StrategyException {
        if (StringUtils.isEmpty(regionName)) {
            throw new StrategyException("region name must have a value");
        }

        if (key == null) {
            throw new StrategyException("key must have a value");
        }
        
        Map<String, KeyPatternItem> keyPatternMap = RedisConfigHelper.getKeyKeyPatternItems(regionName);
        
        if (MapUtils.isEmpty(keyPatternMap)) { //如果找不到配置，就用缺省的
            logger.warn("don't find KeyPatternItems of the " + regionName + " , begin use default set!");            
            keyPatternMap = RedisConfigHelper.getDefaultKeyKeyPatternItems();
        }

        if (MapUtils.isNotEmpty(keyPatternMap)) {
            KeyPatternItem keyPatternItem = findPatternItem(keyPatternMap, key);
            return keyPatternItem != null ? keyPatternItem.getDatasource() : null;
        }
        return null;
    }

	@Override
	public List<String> getAllResouceName(String regionName)
			throws StrategyException {
		List<String> poolNames = new ArrayList<String>();
		if (StringUtils.isEmpty(regionName)) {
			return poolNames;
		}
		
		Map<String, KeyPatternItem> keyPatternMap = RedisConfigHelper.getKeyKeyPatternItems(regionName);
		if (MapUtils.isEmpty(keyPatternMap)) {
			return poolNames;
		}
		
		for(Entry<String, KeyPatternItem> kpItem : keyPatternMap.entrySet()) {
			if (null == kpItem.getValue()) continue;
			
			Object dsName = kpItem.getValue().getDatasource();
			if (null == dsName) continue;
			
			poolNames.add((String)dsName);
		}
		
		return poolNames;
	}
	
    /**
     * 想找到想用的patternItem
     * 
     * @param patternMap
     * @param account
     * @return
     */
    private KeyPatternItem findPatternItem(Map<String, KeyPatternItem> patternMap, Object key) {
        KeyPatternItem patternItem = null;
        Collection<KeyPatternItem> patternItems = patternMap.values();
        for (Iterator<KeyPatternItem> iterator = patternItems.iterator(); iterator.hasNext();) {
            KeyPatternItem item = iterator.next();
            if (item != null) {
                String value = item.getValue();
                if (StringUtils.isNotEmpty(value)) {
                    Pattern pattern = Pattern.compile(value);
                    if (pattern.matcher(ObjectUtils.toString(key)).find()) { // 找到了就退出
                        patternItem = item;
                        break;
                    }
                }
            }
        }
        return patternItem != null ? patternItem : patternItems.iterator().next();
    }

}
