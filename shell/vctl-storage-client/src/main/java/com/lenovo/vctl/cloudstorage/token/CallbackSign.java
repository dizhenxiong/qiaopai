package com.lenovo.vctl.cloudstorage.token;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class CallbackSign {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String key = "ad9fe3530b1438a0cb3a1a3260a03896";
	    String body = "{\"data\":{\"album\":\"album\",\"photo\":\"photo\",\"size\":3,\"thumbnails\":[{\"type\":\"200x200\",\"url\":\"http://oss.lenovows.com:30001/object/thumb/003addeec5205c01:Name001/003ade1de07e9c01/200\"},{\"type\":\"400x400\",\"url\":\"http://oss.lenovows.com:30001/object/thumb/003addeec5205c01:Name001/003ade1de07e9c01/x400\"}],\"url\":\"http://oss.lenovows.com:30001/object/003addeec5205c01:Name001/003ade1de07e9c01\"},\"type\":\"photo\",\"uid\":\"10000153748\"}";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(body.getBytes("utf-8"));

			//hmacsha1
			 // String hmac = "";

			    Mac mac = Mac.getInstance("HmacSHA1");
			   SecretKeySpec secret = new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA1");
			    mac.init(secret);
			    byte[] hmac = mac.doFinal(thedigest);
			   
			    System.out.println("hmac:"+hmac);
			    Base64 encoder = new Base64();
			    String sign = encoder.encodeToString(hmac);
				System.out.println("sign:"+sign);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


	}

}
