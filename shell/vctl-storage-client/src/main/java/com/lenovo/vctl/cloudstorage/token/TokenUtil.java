package com.lenovo.vctl.cloudstorage.token;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonObject;

public class TokenUtil {

	private final static String APP_ID = "003f21c3ad6e9c00";
	private final static String APP_KEY_ID = "ce28c95b8d7a8b1c9245361221acf658";
	private final static String APP_KEY = "935da8e9de5496db6cb13a86e9839044";
	private final static String USER_SLUG = "songkun1@lenovo.com";
	private final static long EXPIRATION_SECOND = 30 * 24 * 60 * 60l; // second
	private final static long EXPIRATION_BEFORE = 60*60*1L;// 提前一小时失效
	
	private final static Map<String, Token> map = new ConcurrentHashMap<String, Token>();
	private static Token getToken() {
		long expirationEpoch = System.currentTimeMillis() / 1000L
				+ EXPIRATION_SECOND;
		String tokenString = "";
		String targetJson = null;
		JsonObject json = new JsonObject();
		json.addProperty("user_slug", USER_SLUG);
		json.addProperty("app_id", APP_ID);
		json.addProperty("expiration", expirationEpoch);
		targetJson = json.toString();
		try {
			tokenString = URLEncoder.encode(
					AuthenUtil.rc4(APP_KEY.getBytes("utf-8"),
							targetJson.getBytes("utf-8")), "utf-8");
			// StringBuffer tokenBuffer = new StringBuffer();
			// tokenBuffer.append("lws_token ").append(appKeyId).append(":").append(tokenStr);
			// tokenStr = tokenBuffer.toString();
			// System.out.println("generate token:" + token);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		StringBuffer authBuffer = new StringBuffer();
		authBuffer.append("lws_token ").append(APP_KEY_ID).append(":")
				.append(tokenString);
		String authStr = authBuffer.toString();
		Token token = new Token();
		token.setExpireTime((expirationEpoch - EXPIRATION_BEFORE));
		token.setToken(authStr);
		return token;
	}
	
	public static String generate(){
		Token token = map.get("token");
		if(token != null && token.expireTime > (System.currentTimeMillis()/1000)){//还没有过期
			return token.getToken();
		}else{
			token = getToken();
			map.put("token", token);
			return token.getToken();
		}
	}
}
