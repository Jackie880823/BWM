package com.madx.bwm.http;

import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by wing on 15/1/29.
 */
public class UrlUtil {

    public static String generateUrl(String url,Map<String,? extends Object> params){
        StringBuilder handUrl = new StringBuilder(url +"?");
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            handUrl.append(key + "=" + params.get(key));
            if (iterator.hasNext()) {
                handUrl.append("&");
            }else
                break;
        }
        return handUrl.toString();
    }


    public static String mapToJsonstring(Map<String,String> params){
        JsonObject jsonObject = new JsonObject();
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = params.get(key);
            if(!TextUtils.isEmpty(value)) {
                jsonObject.addProperty(key, value);
            }
        }
        return  jsonObject.toString();
    }


}
