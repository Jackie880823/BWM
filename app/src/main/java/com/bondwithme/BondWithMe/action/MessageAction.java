package com.bondwithme.BondWithMe.action;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.MsgEntity;
import com.bondwithme.BondWithMe.util.MslToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 15/4/27.
 */
public class MessageAction {
    public static final String REQUEST_POST = "post";
    public static final String REQUEST_UPLOAD = "upload";
    public static final String REQUEST_GET = "get";
    public static final String REQUEST_PUT = "put";

    private Context mContext;
    private Handler mHandler;
    private String TAG;

    public MessageAction(Context mContext, Handler handler) {
        this.mContext = mContext;
        this.mHandler = handler;
        TAG = mContext.getClass().getSimpleName();
    }

    public void doRequest(String requestWay, Map paramsMap, String url, int handlerWhat) {
        new MessageThread(requestWay, paramsMap, url, handlerWhat).start();
    }

    class MessageThread extends Thread {
        private Map paramsMap;
        private String url;
        private int handlerWhat;
        private String requestWay;

        public MessageThread(String requestWay, Map paramsMap, String url, int handlerWhat) {
            this.paramsMap = paramsMap;
            this.url = url;
            this.handlerWhat = handlerWhat;
            this.requestWay = requestWay;
        }

        @Override
        public void run() {
            super.run();
            if (REQUEST_POST.equals(requestWay)) {
                sendChatMessage(paramsMap, url, handlerWhat);
            } else if (REQUEST_UPLOAD.equals(requestWay)) {
                uploadFileMessage(paramsMap, url, handlerWhat);
            } else if (REQUEST_GET.equals(requestWay)) {
                getChatMessage(paramsMap, url, handlerWhat);
            } else if (REQUEST_PUT.equals(requestWay)) {

            }
        }
    }

    private void uploadFileMessage(Map params, String url, final int handlerWhat) {
        new HttpTools(mContext).upload(url, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    mHandler.sendMessage(mHandler.obtainMessage(handlerWhat, jsonObject));
                } catch (JSONException e) {
                    MslToast.getInstance(mContext).showShortToast(mContext.getResources().getString(R.string.text_error));
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void sendChatMessage(Map params, String url, final int handlerWhat) {
        new HttpTools(mContext).post(url, params, TAG, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    mHandler.sendMessage(mHandler.obtainMessage(handlerWhat, jsonObject));
                } catch (JSONException e) {
                    MslToast.getInstance(mContext).showShortToast(mContext.getResources().getString(R.string.text_error));
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                MslToast.getInstance(mContext).showShortToast(mContext.getResources().getString(R.string.text_error));
            }

            @Override
            public void onCancelled() {
            }

            @Override
            public void onLoading(long count, long current) {
            }
        });
    }

    private void getChatMessage(Map params, String url, final int handlerWhat) {
        new HttpTools(mContext).get(url, null, TAG, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String response) {
                List<MsgEntity> msgList = null;
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                if (TextUtils.isEmpty(response)) {
                    mHandler.sendMessage(mHandler.obtainMessage(handlerWhat, msgList));
                    return;
                }
                msgList = gson.fromJson(response, new TypeToken<ArrayList<MsgEntity>>() {
                }.getType());
                if (null != msgList && msgList.size() > 0) {
                    Collections.sort(msgList, new SortExpertsTeamDate());
                }
                mHandler.sendMessage(mHandler.obtainMessage(handlerWhat, msgList));
            }

            @Override
            public void onError(Exception e) {
                MslToast.getInstance(mContext).showShortToast(mContext.getResources().getString(R.string.text_error));
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private class SortExpertsTeamDate implements Comparator<MsgEntity> {
        private SortExpertsTeamDate() {
        }

        @Override
        public int compare(MsgEntity lhs, MsgEntity rhs) {
            if ((lhs != null) && (rhs != null) && (!rhs.getContent_creation_date().equals(lhs.getContent_creation_date()))) {
                return lhs.getContent_creation_date().compareTo(rhs.getContent_creation_date());
            }
            return 0;
        }
    }
}
