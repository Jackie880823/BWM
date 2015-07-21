package com.android.volley.ext.tools;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.toolbox.DownloadRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.MultiPartRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpTools{
    private Context mContext;
    private static RequestQueue sRequestQueue;
    private static RequestQueue sDownloadQueue;
    private static Map<String, String> headers = new HashMap<String, String>();

    public HttpTools(Context context) {
        mContext = context.getApplicationContext();
    }

    public static void init(Context context) {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newNoCacheRequestQueue(context.getApplicationContext());
        }
    }

    public static void initHeaders(Map<String, String> headers) {
        HttpTools.headers = headers;
    }

    public static Map<String, String> getHeaders() {
        return HttpTools.headers;
    }

    public static RequestQueue getHttpRequestQueue() {
        return sRequestQueue;
    }

    /**
     * get 请求
     *
     * @param url
     * @param paramsMap
     * @param httpResult
     */
    public void get(String url, Map<String, String> paramsMap, Object tag,final HttpCallback httpResult) {
        get(new RequestInfo(url, paramsMap), tag,httpResult);
    }

    /**
     * get 请求
     * get
     *
     * @param requestInfo
     * @param httpResult
     * @since 3.5
     */
    public void get(RequestInfo requestInfo, Object tag, final HttpCallback httpResult) {
        sendRequest(Request.Method.GET, requestInfo, tag, httpResult);
    }

    /**
     * post请求
     *
     * @param url
     * @param paramsMap
     * @param httpResult
     */
    public void post(final String url, final Map<String, String> paramsMap, Object tag,final HttpCallback httpResult) {
        post(new RequestInfo(url, paramsMap), tag, httpResult);
    }

    /**
     * post请求
     * post
     *
     * @param requestInfo
     * @param httpResult
     * @since 3.5
     */
    public void post(RequestInfo requestInfo, Object tag,final HttpCallback httpResult) {
        sendRequest(Request.Method.POST, requestInfo, tag, httpResult);
    }

    /**
     * delete 请求
     *
     * @param requestInfo
     * @param httpResult
     * @since 3.5
     */
    public void delete(RequestInfo requestInfo, Object tag,final HttpCallback httpResult) {
        sendRequest(Request.Method.DELETE, requestInfo, tag,httpResult);
    }

    /**
     * put 请求
     *
     * @param requestInfo
     * @param httpResult
     * @since 3.5
     */
    public void put(RequestInfo requestInfo, Object tag,final HttpCallback httpResult) {
        sendRequest(Request.Method.PUT, requestInfo, tag ,httpResult);
    }

    /**
     * upload 请求
     *
     * @param url
     * @param params
     * @param httpResult
     * @since 3.4
     */
    public void upload(final String url, final Map<String, Object> params, Object tag,final HttpCallback httpResult) {
        if (TextUtils.isEmpty(url)) {
            if (httpResult != null) {
                httpResult.onStart();
                httpResult.onError(new Exception("url can not be empty!"));
                httpResult.onFinish();
            }
            return;
        }
        final Map<String, String> paramsMap = new HashMap<String, String>();
        final Map<String, File> fileParams = new HashMap<String, File>();
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = params.get(key);
            if (value instanceof File) {
                fileParams.put(key, (File) value);
            } else if (value instanceof String) {
                paramsMap.put(key, (String) value);
            }
        }
        VolleyLog.d("volley", "upload->" + url + "\nfile->" + fileParams + "\nform->" + paramsMap);
        if (httpResult != null) {
            httpResult.onStart();
        }
        MultiPartRequest<String> request = new MultiPartRequest<String>(Method.POST, url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (httpResult != null) {
                    httpResult.onResult(response);
                    httpResult.onFinish();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (httpResult != null) {
                    httpResult.onError(error);
                    httpResult.onFinish();
                }
            }
        }, new Response.LoadingListener() {

            @Override
            public void onLoading(long count, long current) {
                if (httpResult != null) {
                    httpResult.onLoading(count, current);
                }
            }
        }) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                } catch (NullPointerException e) {
                    parsed = "";
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public void cancel() {
                super.cancel();
                if (httpResult != null) {
                    httpResult.onCancelled();
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return HttpTools.headers;
            }

        };

        if (paramsMap != null && paramsMap.size() != 0) {
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                request.addPart(entry.getKey(), entry.getValue());
            }
        }
        if (fileParams != null && fileParams.size() != 0) {
            for (Map.Entry<String, File> entry : fileParams.entrySet()) {
                String key = entry.getKey();
                Pattern pattern = Pattern.compile("\\d+$");
                Matcher matcher = pattern.matcher(key);
                if (matcher.find()) {
                    key = key.substring(0, key.length() - matcher.group().length());
                }
                request.addPart(key, entry.getValue());
            }
        }
        request.setTag(tag);
        sRequestQueue.add(request);
    }

    public DownloadRequest download(String url, String target, final boolean isResume, final HttpCallback httpResult) {
        DownloadRequest request = new DownloadRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (httpResult != null) {
                    httpResult.onResult(response);
                    httpResult.onFinish();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (httpResult != null) {
                    httpResult.onError(error);
                    httpResult.onFinish();
                }
            }

        }, new Response.LoadingListener() {

            @Override
            public void onLoading(long count, long current) {
                if (httpResult != null) {
                    httpResult.onLoading(count, current);
                }
            }
        }) {
            @Override
            public void stopDownload() {
                super.stopDownload();
                if (httpResult != null) {
                    httpResult.onCancelled();
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return HttpTools.getHeaders();
            }
        };

        request.setResume(isResume);
        request.setTarget(target);
        if (httpResult != null) {
            httpResult.onStart();
        }
        if (TextUtils.isEmpty(url)) {
            if (httpResult != null) {
                httpResult.onError(new Exception("url can not be empty!"));
                httpResult.onFinish();
            }
            return request;
        }
        /**启用缓存，wing 20150625*/
        if (sDownloadQueue == null) {
            sDownloadQueue = Volley.newRequestQueue(mContext);
//            sDownloadQueue = Volley.newNoCacheRequestQueue(mContext);
        }
        request.setShouldCache(true);
        sDownloadQueue.add(request);
        return request;
    }

    private void sendRequest(final int method, final RequestInfo requestInfo,Object tag, final HttpCallback httpResult) {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newNoCacheRequestQueue(mContext);
        }
        if (httpResult != null) {
            httpResult.onStart();
        }
        if (requestInfo == null || TextUtils.isEmpty(requestInfo.url)) {
            if (httpResult != null) {
                httpResult.onError(new Exception("url can not be empty!"));
                httpResult.onFinish();
            }
            return;
        }
        switch (method) {
            case Request.Method.GET:
                requestInfo.url = requestInfo.getFullUrl();
                VolleyLog.d("volley", "get->" + requestInfo.url);
                break;
            case Request.Method.DELETE:
                requestInfo.url = requestInfo.getFullUrl();
                VolleyLog.d("volley", "delete->" + requestInfo.url);
                break;

            default:
                break;
        }
        final StringRequest request = new StringRequest(method, requestInfo.url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (httpResult != null) {
                    httpResult.onResult(response);
                    httpResult.onFinish();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (httpResult != null) {
                    httpResult.onError(error);
                    httpResult.onFinish();
                }
            }
        }, new Response.LoadingListener() {

            @Override
            public void onLoading(long count, long current) {
                if (httpResult != null) {
                    httpResult.onLoading(count, current);
                }
            }
        }) {

            @Override
            public void cancel() {
                super.cancel();
                if (httpResult != null) {
                    httpResult.onCancelled();
                }
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                if (method == Request.Method.POST ) {
                if (method == Request.Method.POST || method == Request.Method.PUT) {
                    return requestInfo.params;
                }

                return super.getParams();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                if(requestInfo.jsonParam!=null){
                    return requestInfo.jsonParam.getBytes();
                }
                return super.getBody();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return HttpTools.getHeaders();
            }
        };
        request.setTag(tag);
        sRequestQueue.add(request);
    }

    /**
     * TODO
     * @param method
     * @param requestInfo
     * @param tag
     * @param httpResult
     * @param updateCache
     */
    private void sendRequest(final int method, final RequestInfo requestInfo,Object tag, final HttpCallback httpResult,boolean updateCache) {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newNoCacheRequestQueue(mContext);
        }
        if (httpResult != null) {
            httpResult.onStart();
        }
        if (requestInfo == null || TextUtils.isEmpty(requestInfo.url)) {
            if (httpResult != null) {
                httpResult.onError(new Exception("url can not be empty!"));
                httpResult.onFinish();
            }
            return;
        }
        switch (method) {
            case Request.Method.GET:
                requestInfo.url = requestInfo.getFullUrl();
                VolleyLog.d("volley", "get->" + requestInfo.url);
                break;
            case Request.Method.DELETE:
                requestInfo.url = requestInfo.getFullUrl();
                VolleyLog.d("volley", "delete->" + requestInfo.url);
                break;

            default:
                break;
        }
        final StringRequest request = new StringRequest(method, requestInfo.url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (httpResult != null) {
                    httpResult.onResult(response);
                    httpResult.onFinish();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (httpResult != null) {
                    httpResult.onError(error);
                    httpResult.onFinish();
                }
            }
        }, new Response.LoadingListener() {

            @Override
            public void onLoading(long count, long current) {
                if (httpResult != null) {
                    httpResult.onLoading(count, current);
                }
            }
        }) {

            @Override
            public void cancel() {
                super.cancel();
                if (httpResult != null) {
                    httpResult.onCancelled();
                }
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                if (method == Request.Method.POST ) {
                if (method == Request.Method.POST || method == Request.Method.PUT) {
                    return requestInfo.params;
                }

                return super.getParams();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                if(requestInfo.jsonParam!=null){
                    return requestInfo.jsonParam.getBytes();
                }
                return super.getBody();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return HttpTools.getHeaders();
            }
        };
        request.setTag(tag);
        sRequestQueue.add(request);
    }

    public void cancelRequestByTag(Object tag){
        if (sRequestQueue != null) {
            sRequestQueue.cancelAll(tag);
        }
    }

    public void cancelAllRequest() {
        if (sRequestQueue != null) {
            sRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }

    public void quitDownloadQueue() {
        if (sDownloadQueue != null) {
            sDownloadQueue.stop();
            sDownloadQueue = null;
        }
    }

    public Map<String, String> urlEncodeMap(Map<String, String> paramsMap) {
        if (paramsMap != null && !paramsMap.isEmpty()) {
            Iterator<String> iterator = paramsMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                try {
                    paramsMap.put(key, URLEncoder.encode(paramsMap.get(key), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return paramsMap;
    }

}
