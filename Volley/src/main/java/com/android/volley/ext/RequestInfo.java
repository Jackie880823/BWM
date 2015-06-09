package com.android.volley.ext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RequestInfo {
	
	public String url ;
	public Map<String,String> params = new HashMap<String, String>() ;
	public String jsonParam  ;
	public Map<String, String> headers = new HashMap<String, String>();
	
    public RequestInfo() {
    }

    public RequestInfo(String url, Map<String, String> params) {
        this.url = url;
        this.params = params;
    }
    

    public String getFullUrl() {
        if (url != null && params != null) {
            StringBuilder sb = new StringBuilder();
            if (!url.contains("?")) {
                url = url + "?";
            } else {
                if (!url.endsWith("?")) {
                    url = url + "&";
                }
            }
            Iterator<String> iterotor = params.keySet().iterator();
            while (iterotor.hasNext()) {
                String key = (String) iterotor.next();
                if (key != null) {
                    try {
                        /**wing modifed for encode get or delete url params*/
                        sb.append(key).append("=").append(URLEncoder.encode(params.get(key), "utf-8")).append("&");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (sb.lastIndexOf("&") == sb.length() - 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return url + sb.toString();
        }
        return url;
    }
	
}
