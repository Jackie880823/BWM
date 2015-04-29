package com.madx.bwm.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.entity.MsgEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by christepherzhang on 15/4/1.
 */
public class StickerFragment extends BaseFragment {

    int page;
    int allPage;
    String path;
    String type;
    String groupId;
    String [] photos;
    int picNum;

    ImageButton ib[];

    ChatActivity chatActivity;


    public StickerFragment()
    {
        super();
    }


    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_sticker;
    }

    @Override
    public void initView() {

        page = getArguments().getInt("page");
        allPage = getArguments().getInt("allPage");
        groupId = getArguments().getString("groupId");
        Log.d("", "groupId" + groupId);
        path = getArguments().getString("path");
        type = getArguments().getString("type");
        chatActivity = (ChatActivity) getActivity();

        ImageButton ib[] = new ImageButton[6];

        ib[0] = (ImageButton)getViewById(R.id.ib_1);
        ib[1] = (ImageButton)getViewById(R.id.ib_2);
        ib[2] = (ImageButton)getViewById(R.id.ib_3);
        ib[3] = (ImageButton)getViewById(R.id.ib_4);
        ib[4] = (ImageButton)getViewById(R.id.ib_5);
        ib[5] = (ImageButton)getViewById(R.id.ib_6);


        try {
            photos = getActivity().getAssets().list(path);
            picNum = photos.length / 2;

        } catch (IOException e) {
            e.printStackTrace();
        }

        //下面这个算法很笨。需改进。fragment+viewpaper使用的不好。
        if (page - allPage != 0)//该页大于等于6个时
        {
            for (int i = 0; i < 6; i++)
            {
                final int stickerName;
                try {
                    InputStream is = getActivity().getAssets().open(path + "/" + String.valueOf( (page -1) * 6 + i + 1) + "_B" + type);
                    stickerName = (page -1) * 6 + i + 1;
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    ib[i].setImageBitmap(bitmap);
                    ib[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("","--->" + path + type + stickerName);

                            MsgEntity msgEntity = new MsgEntity();
                            msgEntity.setUser_id(MainActivity.getUser().getUser_id());
                            msgEntity.setSticker_type(type);
                            msgEntity.setSticker_group_path(path);
                            msgEntity.setSticker_name(stickerName+"");
                            msgEntity.setIsNate("true");
                            chatActivity.setMsgList(msgEntity);
                            chatActivity.setListView();

                            postSticker(stickerName);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            for (int j = 0; j < picNum - (page - 1) * 6; j++)//最后一页的个数
            {
                final int stickerName;
                try {
                    InputStream is = getActivity().getAssets().open(path + "/" + String.valueOf( (page -1) * 6 + j + 1 ) + "_B" + type);
                    stickerName = (page -1) * 6 + j + 1 ;
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    ib[j].setImageBitmap(bitmap);
                    ib[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("","--->" + path + type + stickerName);

                            MsgEntity msgEntity = new MsgEntity();
                            msgEntity.setUser_id(MainActivity.getUser().getUser_id());
                            msgEntity.setSticker_type(type);
                            msgEntity.setSticker_group_path(path);
                            msgEntity.setSticker_name(stickerName+"");
                            msgEntity.setIsNate("true");
                            chatActivity.setMsgList(msgEntity);
                            chatActivity.setListView();

                            postSticker(stickerName);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void requestData() {

    }

    //上传sticker 成功后再下载
    void postSticker(final int stickerName)
    {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("group_id", groupId);
        params.put("content_type", "post");
        params.put("sticker_group_path", path);
        params.put("sticker_name", stickerName+"");
        params.put("sticker_type", type);

        new HttpTools(getActivity()).post(Constant.API_MESSAGE_POST_TEXT, params, new HttpCallback() {
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

                chatActivity.getMsg();

                try {
                    JSONObject jsonObject = new JSONObject(response);


                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getActivity(), getString(R.string.text_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });















//        StringRequest stringRequestPost = new StringRequest(Request.Method.POST, Constant.API_POST_STICKER, new Response.Listener<String>() {
//
//            GsonBuilder gsonb = new GsonBuilder();
//
//            Gson gson = gsonb.create();
//
//            @Override
//            public void onResponse(String response) {
//
////                getParentActivity();
////                getActivity();//这两个什么区别呢?
//
//                chatActivity.getMsg();
//
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //TODO
//                error.printStackTrace();
//                Toast.makeText(getActivity(), getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("content_creator_id", MainActivity.getUser().getUser_id());
//                params.put("group_id", groupId);
//                params.put("content_type", "post");
//                params.put("sticker_group_path", path);
//                params.put("sticker_name", stickerName+"");
//                params.put("sticker_type", type);
//                return params;
//            }
//        };
//        VolleyUtil.addRequest2Queue(getActivity(), stringRequestPost, "");
    }
}
