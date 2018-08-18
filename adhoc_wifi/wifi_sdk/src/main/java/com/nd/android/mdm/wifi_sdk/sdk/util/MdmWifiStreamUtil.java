package com.nd.android.mdm.wifi_sdk.sdk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by HuangYK on 2018/7/5.
 */
public final class MdmWifiStreamUtil {

    /**
     * 解析流里的字符串内容
     *
     * @param stream 流对象
     * @return 字符串内容
     */
    public static String readStream(InputStream stream) {
        return readStream(stream, "UTF-8");
    }

    /**
     * 按照编码格式解析流里的字符串内容
     *
     * @param stream 流对象
     * @param encode 编码格式
     * @return 内容
     */
    public static String readStream(InputStream stream, String encode) {
        if (stream != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encode));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                stream.close();
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
