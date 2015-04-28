package com.madx.bwm.action;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.R;
import com.madx.bwm.adapter.ChatAdapter;
import com.madx.bwm.entity.MsgEntity;
import com.madx.bwm.util.MslToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quankun on 15/4/27.
 */
public class MessageAction {
    private Context mContext;
    private Handler mHandler;

    public MessageAction(Context mContext, Handler handler) {
        this.mContext = mContext;
        this.mHandler = handler;
    }

    public void sendChatMessage(Map params, String url, final int handlerWhat) {
        new HttpTools(mContext).post(url, params, new HttpCallback() {
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
                    //if ("postText".equals(jsonObject.getString("postType"))) {
                    mHandler.sendMessage(mHandler.obtainMessage(handlerWhat, jsonObject));
                    //getMsg();//成功后再次get
                    // }

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

    public void getChatMessage(Map params, String url, final int handlerWhat) {
        new HttpTools(mContext).get(url, null, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String response) {
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                List<MsgEntity> msgList = gson.fromJson(response, new TypeToken<ArrayList<MsgEntity>>() {
                }.getType());
                Collections.sort(msgList, new SortExpertsTeamDate());
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

    private class SortExpertsTeamDate implements Comparator<MsgEntity>
    {
        private SortExpertsTeamDate() {}

        @Override
        public int compare(MsgEntity lhs, MsgEntity rhs) {
            if ((lhs != null) && (rhs != null) && (rhs.getContent_creation_date().equals(lhs.getContent_creation_date()))) {
                return lhs.getContent_creation_date().compareTo(rhs.getContent_creation_date());
            }
            return 0;
        }
    }
}
