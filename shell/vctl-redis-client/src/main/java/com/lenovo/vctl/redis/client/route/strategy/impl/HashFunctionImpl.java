package com.lenovo.vctl.redis.client.route.strategy.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

import com.lenovo.vctl.redis.client.route.strategy.HashFunction;

public class HashFunctionImpl implements HashFunction {

	// Constants
	public static final int NATIVE_HASH = 0;					// native String.hashCode();
	public static final int OLD_COMPAT_HASH = 1;				// original compatibility hashing algorithm (works with other clients)
	public static final int NEW_COMPAT_HASH = 2;				// new CRC32 based compatibility hashing algorithm (works with other clients)
	public static final int KETAMA_HASH = 3;					// MD5 Based
	
	private int hashingAlg 		      = KETAMA_HASH;		// default to using the native hash as it is the fastest
	private static MessageDigest md5   = null;				// avoid recurring construction
	
	/**
	 * 	NATIVE_HASH = 0
	 *	OLD_COMPAT_HASH = 1
	 *  NEW_COMPAT_HASH = 2
	 *	KETAMA_HASH = 3
	 * @param key
	 * @return
	 */
	public Long calculateHash(String key) {

		switch ( hashingAlg ) {
			case NATIVE_HASH:
				return (long)key.hashCode();
			case OLD_COMPAT_HASH:
				return (long)origCompatHashingAlg(key);
			case NEW_COMPAT_HASH:
				return (long)newCompatHashingAlg(key);
			case KETAMA_HASH:
				return md5HashingAlg(key);
			default:
				// use the native hash as a default
				hashingAlg = NATIVE_HASH;
				return (long)key.hashCode();
		}		
	}	
	
	/** 
	 * Internal private hashing method.
	 *
	 * This is the original hashing algorithm from other clients.
	 * Found to be slow and have poor distribution.
	 * 
	 * @param key String to hash
	 * @return hashCode for this string using our own hashing algorithm
	 */
	private static int origCompatHashingAlg( String key ) {
		int hash    = 0;
		char[] cArr = key.toCharArray();

		for ( int i = 0; i < cArr.length; ++i ) {
			hash = (hash * 33) + cArr[i];
		}

		return hash;
	}
	
	/** 
	 * Internal private hashing method.
	 *
	 * This is the new hashing algorithm from other clients.
	 * Found to be fast and have very good distribution. 
	 *
	 * UPDATE: This is dog slow under java
	 * 
	 * @param key 
	 * @return 
	 */
	private static int newCompatHashingAlg( String key ) {
		CRC32 checksum = new CRC32();
		checksum.update( key.getBytes() );
		int crc = (int) checksum.getValue();

		return (crc >> 16) & 0x7fff;
	}


	/**
	 * Calculates the ketama hash value for a string 
	 * @param s
	 * @return
	 */
	public static Long md5HashingAlg(String key) {
		if(md5==null) {
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException( "++++ no md5 algorythm found");			
			}
		}

		md5.reset();
		md5.update(key.getBytes());
		byte[] bKey = md5.digest();
		long res = ((long)(bKey[3]&0xFF) << 24) | ((long)(bKey[2]&0xFF) << 16) | ((long)(bKey[1]&0xFF) << 8) | (long)(bKey[0]&0xFF);
		return res;
	}

	public Long hash(Object key) {
		return calculateHash(String.valueOf(key));
	}

}
