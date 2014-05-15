package com.lenovo.vctl.cloudstorage.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class JacksonUtil {
	public static JsonNode String2Json(String jsonString) {
		JsonNode df = null;
		if(jsonString == null || jsonString.equals(""))return null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			df = mapper.readValue(jsonString, JsonNode.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return df;
	}
	
	
}
