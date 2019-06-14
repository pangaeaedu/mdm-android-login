package com.nd.android.adhoc.communicate.request.interceptor;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.log.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Created by HuangYK on 2019/6/14.
 */
class AdhocRequestUtil {

    private static final String TAG = "AdhocRequestUtil";

    private static final String SUPPORT_CONTENT_TYPE = "application/json";


    /**
     * 读取请求体的参数内容，转为 json 串
     *
     * @param request Request 请求对象
     * @return 请求内容
     * @throws Exception 异常信息
     */
    public static String readRequestBody(Request request) throws Exception {
        if (request.body() == null) {
            return null;
        }
        try {
            RequestBody body = request.body();
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset;
            MediaType contentType = body.contentType();
            if (contentType != null && contentType.toString().trim().contains(SUPPORT_CONTENT_TYPE)) {
                charset = contentType.charset(Charset.forName("UTF-8"));
            } else {
                Logger.w(TAG, contentType != null ? "Unsupported contentType: " + contentType.toString() : "contentType is null");
                throw new AdhocException(contentType != null ? "Unsupported contentType: " + contentType.toString() : "contentType is null");
            }
            if (isPlaintext(buffer)) {
                return buffer.readString(charset);
            } else {
                Logger.w(TAG, "Body is not plain text.");
                throw new AdhocException("Unsupported contentType = " + contentType.toString());
            }
        } catch (IOException e) {
            Logger.w(TAG, "Body Parsing error: " + e);
        }
        return "";
    }


    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            Logger.w(TAG, "check Body plain text error: " + e);
            return false; // Truncated UTF-8 sequence.
        }
    }
}
