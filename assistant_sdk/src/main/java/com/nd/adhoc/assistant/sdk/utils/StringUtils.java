package com.nd.adhoc.assistant.sdk.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    @NonNull
    public static List<String> splitString(String pSource, String pSeparator){
        List<String> results = new ArrayList<>();

        if(TextUtils.isEmpty(pSource)){
            return results;
        }

        String[] arrays = pSource.split(pSeparator);

        for (String accountNum : arrays) {
            results.add(accountNum);
        }

        return results;
    }

    public static String mergeStringList(List<String> pSource, String pSeparator) {
        if (pSource == null || pSource.isEmpty()) {
            return "";
        }

        String result = "";
        for (int i = 0; i < pSource.size(); i++) {
            result = result + pSource.get(i);
            if (i != pSource.size() - 1) {
                result = result + pSeparator;
            }
        }

        return result;
    }
}
