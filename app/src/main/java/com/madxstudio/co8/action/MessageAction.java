package com.madxstudio.co8.action;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.MsgEntity;
import com.madxstudio.co8.ui.MessageChatActivity;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
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

    public static final String POST_TEXT = "postText";
    public static final String POST_PHOTO = "postPhoto";
    public static final String POST_STICKER = "postSticker";
    public static final String POST_VIDEO = "postVideo";
    public static final String POST_AUDIO = "postAudio";


    private Context mContext;
    private Handler mHandler;
    private String TAG;

    public MessageAction(Context mContext, Handler handler) {
        this.mContext = mContext;
        this.mHandler = handler;
        TAG = mContext.getClass().getSimpleName();
    }

    public void doRequest(String requestWay, Map paramsMap, String url, int handlerWhat, Map<String, MsgEntity> sendMap, String key, String postType) {
        new MessageThread(requestWay, paramsMap, url, handlerWhat, sendMap, key, postType).start();
    }

    class MessageThread extends Thread {
        private Map paramsMap;
        private String url;
        private int handlerWhat;
        private String requestWay;
        private Map<String, MsgEntity> sendMap;
        private String key;
        private String postType;

        public MessageThread(String requestWay, Map paramsMap, String url, int handlerWhat, Map<String, MsgEntity> sendMap, String key, String postType) {
            this.paramsMap = paramsMap;
            this.url = url;
            this.handlerWhat = handlerWhat;
            this.requestWay = requestWay;
            this.sendMap = sendMap;
            this.key = key;
            this.postType = postType;
        }

        @Override
        public void run() {
            super.run();
            if (REQUEST_POST.equals(requestWay)) {
                if (!NetworkUtil.isNetworkConnected(mContext)) {
                    modifyData(sendMap, key);
                    mHandler.sendEmptyMessage(MessageChatActivity.NOTIFY_DATA);
                } else {
                    sendChatMessage(paramsMap, url, handlerWhat, sendMap, key, postType);
                }
            } else if (REQUEST_UPLOAD.equals(requestWay)) {
                if (!NetworkUtil.isNetworkConnected(mContext)) {
                    modifyData(sendMap, key);
                    mHandler.sendEmptyMessage(MessageChatActivity.NOTIFY_DATA);
                } else {
                    uploadFileMessage(paramsMap, url, handlerWhat, sendMap, key, postType);
                }
            } else if (REQUEST_GET.equals(requestWay)) {
                getChatMessage(paramsMap, url, handlerWhat);
            } else if (REQUEST_PUT.equals(requestWay)) {

            }
        }
    }

    private void uploadFileMessage(Map params, String url, final int handlerWhat, final Map<String, MsgEntity> sendMap, final String key, final String postType) {
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
                    try {
                        String type = jsonObject.optString("postType");
                        if (postType != null && postType.equals(type)) {
                            removeData(sendMap, key);
                        } else {
                            modifyData(sendMap, key);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(handlerWhat, jsonObject));
                } catch (JSONException e) {
                    MessageUtil.getInstance(mContext).showShortToast(mContext.getResources().getString(R.string.text_error));
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                modifyData(sendMap, key);
                mHandler.sendEmptyMessage(MessageChatActivity.NOTIFY_DATA);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void removeData(Map<String, MsgEntity> sendMap, String key) {
        if (sendMap != null) {
            for (String mapKey : sendMap.keySet()) {
                if (key != null && key.equals(mapKey)) {
                    sendMap.remove(mapKey);
                    break;
                }
            }
        }
    }

    private void modifyData(Map<String, MsgEntity> sendMap, String key) {
        if (sendMap != null) {
            for (String mapKey : sendMap.keySet()) {
                if (key != null && key.equals(mapKey)) {
                    sendMap.get(key).setSendStatus(MsgEntity.SEND_FAIL);
                    break;
                }
            }
        }
    }

    private void sendChatMessage(Map params, String url, final int handlerWhat, final Map<String, MsgEntity> sendMap, final String key, final String postType) {
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
                    try {
                        String type = jsonObject.optString("postType");
                        if (postType != null && postType.equals(type)) {
                            removeData(sendMap, key);
                        } else {
                            modifyData(sendMap, key);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(handlerWhat, jsonObject));
                } catch (JSONException e) {
                    MessageUtil.getInstance(mContext).showShortToast(mContext.getResources().getString(R.string.text_error));
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                modifyData(sendMap, key);
                mHandler.sendEmptyMessage(MessageChatActivity.NOTIFY_DATA);
//                MslToast.getInstance(mContext).showShortToast(mContext.getResources().getString(R.string.text_error));
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
                    Collections.reverse(msgList);
                   // Collections.sort(msgList, new SortExpertsTeamDate());
                }
                mHandler.sendMessage(mHandler.obtainMessage(handlerWhat, msgList));
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.getInstance(mContext).showShortToast(mContext.getResources().getString(R.string.text_error));
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

//    public static class SortExpertsTeamDate implements Comparator<MsgEntity> {
//        public SortExpertsTeamDate() {
//        }
//
//        @Override
//        public int compare(MsgEntity lhs, MsgEntity rhs) {
//            if ((lhs != null) && (rhs != null) && (!rhs.getContent_creation_date().equals(lhs.getContent_creation_date()))) {
//                return lhs.getContent_creation_date().compareTo(rhs.getContent_creation_date());
//            }
//            return 0;
//        }
//    }
}
