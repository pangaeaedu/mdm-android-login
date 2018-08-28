package com.nd.adhoc.assistant.sdk.utils;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5相关工具类
 * <br>Created 2014-8-22 下午4:07:24
 * @version  
 * by   huangyx
 * @see 	 
 */
public final class MD5ArithmeticUtils {
    /**
     * 构造器
     */
    private MD5ArithmeticUtils(){}
	/**
	 * 获取MD5
	 * <br>Created 2014-8-22 下午4:08:07
	 * @param str 源string
	 * @return 返回MD5
	 * by       huangyx
	 */
	public static synchronized String getMd5(String str) throws NoSuchAlgorithmException {
		if (str == null) {
			str = "";
		}
		MessageDigest digest = MessageDigest.getInstance("MD5");
        try {
			digest.update(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
			digest.update(str.getBytes(Charset.defaultCharset()));
            e.printStackTrace();
        }
		return getHashString(digest.digest());
	}

	/**
	 * 获取Hash字符串
	 * <br>Created 2014-8-22 下午4:09:12
	 * @param bytes md5 bytes
	 * @return Hash字符串
	 * by       huangyx
	 */
	private static String getHashString(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(Integer.toHexString((b >> 4) & 0xf));
			builder.append(Integer.toHexString(b & 0xf));
		}
		return builder.toString();
	}
       
}
