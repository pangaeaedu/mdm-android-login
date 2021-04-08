/**
 * @copyright Copyright 1999-2015 © 99.com All rights reserved.
 * @license http://www.99.com/about
 */
package com.nd.android.aioe.device.activate.dao.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.net.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author SongDeQiang <mail.song.de.qiang@gmail.com>
 * @packge com.nd.im.album.util
 * @class AuthorityUtils
 */
public class AuthorityUtils {


    public static String macAuth(String url, HttpMethod method, String macKey, String accessToken) {

        String path = url.replaceAll("^http.*?//[^/]*?/", "/");
        String host = url.replaceAll("^http.*?//", "").replaceAll("/.*$", "");

        String nonce = System.currentTimeMillis() + ":" + RandomStringUtils.randomAlphanumeric(8);

        StringBuilder sbRawMac = new StringBuilder();
        sbRawMac.append(nonce);
        sbRawMac.append("\n");
        sbRawMac.append(method.name());
        sbRawMac.append("\n");
        sbRawMac.append(path);
        sbRawMac.append("\n");
        sbRawMac.append(host);
        sbRawMac.append("\n");

        String mac = hMac256(sbRawMac.toString(), macKey);

        String auth = String.format("MAC id=\"%s\",nonce=\"%s\",mac=\"%s\"", accessToken, nonce, mac);

        return auth.toString();
    }

    /**
     * @param url
     * @param method
     * @param macKey
     * @return
     */
    public static String mac(String url, HttpMethod method, String accessToken, String macKey) {
        String path = url.replaceAll("^http.*?//[^/]*?/", "/");
        String host = url.replaceAll("^http.*?//", "").replaceAll("/.*$", "");

        String nonce = System.currentTimeMillis() + ":" + RandomStringUtils.randomAlphanumeric(8);

        StringBuilder sbRawMac = new StringBuilder();
        sbRawMac.append(nonce);
        sbRawMac.append("\n");
        sbRawMac.append(method.name());
        sbRawMac.append("\n");
        sbRawMac.append(path);
        sbRawMac.append("\n");
        sbRawMac.append(host);
        sbRawMac.append("\n");

        String mac = hMac256(sbRawMac.toString(), macKey);

        String auth = String.format("MAC id=\"%s\",nonce=\"%s\",mac=\"%s\"", accessToken, nonce, mac);

        return auth.toString();
    }


    /**
     * @param url
     * @return
     */
    public static String encode(String url) {
        Matcher matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(url);

        while (matcher.find()) {
            String tmp = matcher.group();
            try {
                url = url.replaceAll(tmp, URLEncoder.encode(tmp, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                //
            }
        }

        return url;
    }

    /**
     * @param content
     * @param key
     * @return
     * @throws Exception
     */
    public static String hMac256(String content, String key) {
        String resultString = "";

        try {
            // 还原密钥
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            // 实例化Mac
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            // 初始化mac
            mac.init(secretKey);
            // 执行消息摘要
            byte[] digest = mac.doFinal(content.getBytes());

            resultString = new String(Base64.encodeBase64(digest));
        } catch (Exception e) {
            //
        }

        return resultString;
    }

    public static void main(String[] args) {
        mac("http://192.168.254.23:8090/v1.1/enroll/activate", HttpMethod.POST, "", "");
    }


}
