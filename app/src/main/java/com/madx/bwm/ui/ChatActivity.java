package com.madx.bwm.ui;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.ChatAdapter;
import com.madx.bwm.adapter.StickerPaperAdapter;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.entity.MsgEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.http.PicturesCacheUtil;
import com.madx.bwm.http.UrlUtil;
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.util.LocalImageLoader;
import com.madx.bwm.util.MyTextUtil;
import com.madx.bwm.util.SDKUtil;
import com.madx.bwm.util.UIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends BaseActivity implements View.OnClickListener{

    private ListView listView;//聊天框
//    ProgressBarCircularIndeterminate progressBar;

    ProgressDialog progressDialog;

    private ImageButton cb1;//加号
    private ImageButton cb2;//表情

    private EditText etChat;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh;

    private LinearLayout ll1;//加号
    private LinearLayout ll2;//表情库

    private ViewPager mPager;
    private LinearLayout llSticker;
    private LinearLayout sticker1;
    private LinearLayout sticker2;
    private LinearLayout sticker3;
    private LinearLayout sticker4;
    private LinearLayout sticker5;
    private LinearLayout sticker6;
    private LinearLayout sticker7;
    private LinearLayout sticker8;
    private LinearLayout sticker9;
    private LinearLayout sticker10;

    int type = -1;

    LinearLayout llCamera;
    LinearLayout llAlbum;
    LinearLayout llLocation;
    LinearLayout llStickers;
    LinearLayout llVideo;
    LinearLayout llContact;

    private TextView btnSend;

    private String groupId;//API的一个参数

    private List<MsgEntity> msgList = new ArrayList<>();//对话消息

    private ChatAdapter chatAdapter;

    private UserEntity userEntity = new UserEntity();//传进来的,他人个人信息
    private GroupEntity groupEntity = new GroupEntity();//传进来的

    private final static int REQUEST_GET_GROUP_NAME = 4;

    /*相册和相机使用的参数*/
    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;
    private final static int REQUEST_HEAD_FINAL = 3;

    Uri mCropImagedUri;//裁剪后的Uri
    private String imagePath;
    Uri uri;//原图uri
    /**
     * 头像缓存文件名称
     */
    public final static String CACHE_PIC_NAME = "head_cache";
    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";

    public void setMsgList(MsgEntity msgEntity) {
        msgList.add(0, msgEntity);
    }

    public void setListView() {
        listView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
        listView.setSelection(listView.getBottom());
    }



    @Override
    public int getLayout() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        changeTitleColor(R.color.tab_color_press3);
        if (type == 0) {
            rightButton.setVisibility(View.INVISIBLE);
        }
        rightButton.setImageResource(R.drawable.btn_group_setting);
    }

    @Override
    protected void setTitle() {

        if (getIntent().getIntExtra("type", -1) == 0) {
            tvTitle.setText(userEntity.getUser_given_name());
        } else if (getIntent().getIntExtra("type", -1) == 1) {
            tvTitle.setText(groupEntity.getGroup_name());
        } else {
            tvTitle.setText("error");
        }
    }

    @Override
    protected void titleRightEvent() {

        //私聊->跳转到资料页？type == 0
        //群聊->跳转到群组设置? type == 1
        Intent intent;
        if (type == 0) {
            intent = new Intent(ChatActivity.this, FamilyProfileActivity.class);
            intent.putExtra("member_id", userEntity.getUser_id());//传进来的,他人个人信息
            startActivity(intent);
        } else if (type == 1) {
            intent = new Intent(ChatActivity.this, GroupSettingActivity.class);
            intent.putExtra("groupEntity", groupEntity);
            startActivityForResult(intent, REQUEST_GET_GROUP_NAME);
        } else {
            finish();
        }

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    ArrayList<Fragment> fragmentsList;
    Bundle bundle;
    StickerPaperAdapter stickerPaperAdapter;

    @Override
    public void initView() {

        progressDialog = new ProgressDialog(this,getResources().getString(R.string.text_dialog_loading));

        chatAdapter = new ChatAdapter(ChatActivity.this, msgList);
        //type 0.私聊 1.群聊
        type = getIntent().getIntExtra("type", -1);//获得聊天类型 私聊or群聊

        if (getIntent().getIntExtra("type", -1) == 0) {
            userEntity = (UserEntity) getIntent().getExtras().getSerializable("userEntity");
            groupId = userEntity.getGroup_id();

        } else if (getIntent().getIntExtra("type", -1) == 1) {
            groupEntity = (GroupEntity) getIntent().getExtras().getSerializable("groupEntity");
            groupId = groupEntity.getGroup_id();
        } else {
            finish();
        }

        getMsgFirst();//接收对话消息

        listView = getViewById(R.id.chat_lv);

        cb1 = getViewById(R.id.cb_1);
        cb2 = getViewById(R.id.cb_2);

        mPager = getViewById(R.id.viewpager);

        llSticker = getViewById(R.id.ll_sticker);

        sticker1 = getViewById(R.id.ib_1);
        sticker2 = getViewById(R.id.ib_2);
        sticker3 = getViewById(R.id.ib_3);
        sticker4 = getViewById(R.id.ib_4);
        sticker5 = getViewById(R.id.ib_5);
        sticker6 = getViewById(R.id.ib_6);
        sticker7 = getViewById(R.id.ib_7);
        sticker8 = getViewById(R.id.ib_8);
        sticker9 = getViewById(R.id.ib_9);
        sticker10 = getViewById(R.id.ib_10);

        cb1.setOnClickListener(this);
        cb2.setOnClickListener(this);

        sticker1.setOnClickListener(this);
        sticker2.setOnClickListener(this);
        sticker3.setOnClickListener(this);
        sticker4.setOnClickListener(this);
        sticker5.setOnClickListener(this);
        sticker6.setOnClickListener(this);
        sticker7.setOnClickListener(this);
        sticker8.setOnClickListener(this);
        sticker9.setOnClickListener(this);
        sticker10.setOnClickListener(this);
        onClick(sticker1);


        etChat = getViewById(R.id.et_chat);
        etChat.setOnClickListener(this);

        swipeRefreshLayout = getViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getHistoryMsg();
            }

        });

        ll1 = getViewById(R.id.ll_1);
        ll2 = getViewById(R.id.ll_2);

        llCamera = getViewById(R.id.ll_camera);
        llAlbum = getViewById(R.id.ll_album);
        llLocation = getViewById(R.id.ll_location);
        llStickers = getViewById(R.id.ll_stickers);
        llVideo = getViewById(R.id.ll_video);
        llContact = getViewById(R.id.ll_contact);

        btnSend = getViewById(R.id.btn_send);

//        etChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ll1.setVisibility(View.GONE);
//                ll2.setVisibility(View.GONE);
//
//                isCb1 = false;
//                isCb2 = false;
//
//                cb1.setImageDrawable(getResources().getDrawable(R.drawable.chat_plus_normal));
//                cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_normal));
//                listView.setSelection(listView.getBottom());
//                onClick(etChat);
//            }
//        });

//        etChat.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!MyTextUtil.isInvalidText(etChat.getText().toString()))
//                {
//                    btnSend.setVisibility(View.VISIBLE);
//                }
//                else
//                {
//                    btnSend.setVisibility(View.GONE);
//                }
//            }
//        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    UIUtil.hideKeyboard(ChatActivity.this, etChat);
                    ll1.setVisibility(View.GONE);
                    ll2.setVisibility(View.GONE);
                    isCb1 = false;
                    isCb2 = false;
                    cb1.setImageDrawable(getResources().getDrawable(R.drawable.chat_plus_normal));
                    cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_normal));
                }

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent2.putExtra("android.intent.extras.CAMERA_FACING", 1);
                intent2.putExtra("camerasensortype", 2);

                // 下面这句指定调用相机拍照后的照片存储的路径
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                        .fromFile(PicturesCacheUtil.getFile(ChatActivity.this,
                                CACHE_PIC_NAME_TEMP)));
                // 图片质量为高
                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                intent2.putExtra("return-data", false);
                startActivityForResult(intent2, REQUEST_HEAD_CAMERA);
            }
        });

        llAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_HEAD_PHOTO);
            }
        });

        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        llStickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                UIUtil.hideKeyboard(ChatActivity.this, etChat);
                cb1.setImageDrawable(getResources().getDrawable(R.drawable.chat_plus_normal));
                cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_press));
                ll1.setVisibility(View.GONE);
                ll2.setVisibility(View.VISIBLE);
                isCb1 = false;
                isCb2 = true;
            }
        });

        llVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        llContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = etChat.getText().toString();

                listView.setSelection(listView.getBottom());

                if (!MyTextUtil.isInvalidText(content)) {
                    //直接显示在list上
                    MsgEntity msgEntity = new MsgEntity();
                    msgEntity.setUser_id(MainActivity.getUser().getUser_id());
                    msgEntity.setText_id("1");
                    msgEntity.setText_description(content);
                    msgList.add(0, msgEntity);
                    listView.setAdapter(chatAdapter);
                    chatAdapter.notifyDataSetChanged();
                    etChat.setText("");
                    listView.setSelection(listView.getBottom());

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("content_creator_id", MainActivity.getUser().getUser_id());
                    params.put("group_id", groupId);
                    params.put("content_type", "post");
                    params.put("text_description", content);
                    params.put("group_ind_type", "");
                    params.put("content_group_public", "0");


                    new HttpTools(ChatActivity.this).post(Constant.API_MESSAGE_POST_TEXT, params, new HttpCallback() {
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

                                if ("postText".equals(jsonObject.getString("postType"))) {
                                    getMsg();//成功后再次get
                                }

                            } catch (JSONException e) {
                                Toast.makeText(ChatActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ChatActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled() {

                        }

                        @Override
                        public void onLoading(long count, long current) {

                        }
                    });
                }


//                if (!MyTextUtil.isInvalidText(content)) {
//
//                    //直接显示在list上
//                    MsgEntity msgEntity = new MsgEntity();
//                    msgEntity.setUser_id(MainActivity.getUser().getUser_id());
//                    msgEntity.setText_id("1");
//                    msgEntity.setText_description(content);
//                    msgList.add(0, msgEntity);
//                    listView.setAdapter(chatAdapter);
//                    chatAdapter.notifyDataSetChanged();
//                    etChat.setText("");
//                    listView.setSelection(listView.getBottom());
//
//                    //上传文本内容
//                    StringRequest srText = new StringRequest(Request.Method.POST, Constant.API_MESSAGE_POST_TEXT, new Response.Listener<String>() {
//
//                        @Override
//                        public void onResponse(String response) {
//
//                            Log.d("", "-------" + response);
//
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//
//                                if ("postText".equals(jsonObject.getString("postType"))) {
//
////                                    Toast.makeText(ChatActivity.this, "Success", Toast.LENGTH_SHORT).show();
//
//                                    getMsg();//成功后再次get
//
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            error.printStackTrace();
//                            Toast.makeText(ChatActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
//
//                        }
//                    }) {
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            HashMap<String, String> params = new HashMap<String, String>();
//                            params.put("content_creator_id", MainActivity.getUser().getUser_id());
//                            params.put("group_id", groupId);
//                            params.put("content_type", "post");
//                            params.put("text_description", content);
//                            params.put("group_ind_type", "");
//                            params.put("content_group_public", "0");
//                            return params;
//                        }
//                    };
//                    VolleyUtil.addRequest2Queue(ChatActivity.this, srText, "");
//                }


            }
        });


    }

    @Override
    public void requestData() {


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void getMsg() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", "10");
        params.put("offset", "0");
        params.put("group_id", groupId);
        params.put("page", "1");
        params.put("start", "0");
        params.put("view_user", MainActivity.getUser().getUser_id());
        String url = UrlUtil.generateUrl(Constant.API_GET_MESSAGE, params);

        new HttpTools(ChatActivity.this).get(url, null, new HttpCallback() {
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

                msgList = gson.fromJson(response, new TypeToken<ArrayList<MsgEntity>>() {
                }.getType());

                chatAdapter = new ChatAdapter(ChatActivity.this, msgList);
                listView.setAdapter(chatAdapter);
                listView.setSelection(listView.getBottom());
                etChat.setText("");

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ChatActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });






//        StringRequest srMsg = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                GsonBuilder gsonb = new GsonBuilder();
//
//                Gson gson = gsonb.create();
//
//                msgList = gson.fromJson(response, new TypeToken<ArrayList<MsgEntity>>() {
//                }.getType());
//
//                chatAdapter = new ChatAdapter(ChatActivity.this, msgList);
//                listView.setAdapter(chatAdapter);
//                listView.setSelection(listView.getBottom());
//                etChat.setText("");
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        });
//        VolleyUtil.addRequest2Queue(ChatActivity.this, srMsg, "");
    }


    private int startIndex = 0;
    private int offset = 20;

    public void getHistoryMsg() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", offset + "");
        params.put("offset", "0");
        params.put("group_id", groupId);
        params.put("page", "1");
        params.put("start", startIndex + "");
        String url = UrlUtil.generateUrl(Constant.API_GET_MESSAGE, params);

        new HttpTools(ChatActivity.this).get(url, null, new HttpCallback() {
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

                msgList = gson.fromJson(response, new TypeToken<ArrayList<MsgEntity>>() {
                }.getType());
                if (isRefresh) {
                    finishReFresh();
                    offset += 10;
                }
                chatAdapter = new ChatAdapter(ChatActivity.this, msgList);
                listView.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ChatActivity.this, getResources().getString(R.string.text_error), Toast.LENGTH_SHORT).show();
                if (isRefresh) {
                    finishReFresh();
                }
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });


//
//
//        StringRequest srMsg = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                GsonBuilder gsonb = new GsonBuilder();
//
//                Gson gson = gsonb.create();
//
//                msgList = gson.fromJson(response, new TypeToken<ArrayList<MsgEntity>>() {
//                }.getType());
//
//                if (isRefresh) {
//                    finishReFresh();
//                    offset += 10;
//                }
//
//                chatAdapter = new ChatAdapter(ChatActivity.this, msgList);
//                listView.setAdapter(chatAdapter);
//
//                chatAdapter.notifyDataSetChanged();
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (isRefresh) {
//                    finishReFresh();
//                }
//            }
//        });
//
//        VolleyUtil.addRequest2Queue(ChatActivity.this, srMsg, "");
    }

    List<Uri> pickUries = new ArrayList();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ChatActivity.RESULT_OK == resultCode) {

            switch (requestCode) {
                // 如果是直接从相册获取
                case REQUEST_HEAD_PHOTO:
                    pickUries.clear();
                    if (data != null) {

                        if (SDKUtil.IS_JB) {
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                int size = clipData.getItemCount();
                                for (int i = 0; i < size; i++) {
                                    Uri uri = clipData.getItemAt(i).getUri();
                                    pickUries.add(uri);
                                }
                            } else {
                                pickUries.add(data.getData());
                            }
                        } else {
                            pickUries.add(data.getData());
                        }

//                        //直接显示在list上， 会在相册停留太久
                        for (int i = 0; i < pickUries.size(); i++) {
                            MsgEntity msgEntity = new MsgEntity();
                            msgEntity.setSticker_type(".png");
                            msgEntity.setUser_id(MainActivity.getUser().getUser_id());
                            msgEntity.setUri(pickUries.get(i));
                            msgList.add(0, msgEntity);
                            chatAdapter.notifyDataSetChanged();
                            listView.setSelection(listView.getBottom());
                        }

                        handler.sendEmptyMessage(0);

                    }

                    break;

                // 如果是调用相机拍照时
                case REQUEST_HEAD_CAMERA:

                    uri = Uri.fromFile(PicturesCacheUtil.getFile(ChatActivity.this, CACHE_PIC_NAME_TEMP));

                    handler.sendEmptyMessage(1);


                    //直接显示在list上， 会在相册停留太久
                    MsgEntity msgEntity = new MsgEntity();
                    msgEntity.setSticker_type(".png");
                    msgEntity.setUser_id(MainActivity.getUser().getUser_id());
                    msgEntity.setUri(uri);
                    msgList.add(0, msgEntity);
                    chatAdapter.notifyDataSetChanged();
                    listView.setSelection(listView.getBottom());
                    break;

                // 取得裁剪后的图片
                case REQUEST_HEAD_FINAL:
                    break;


                case REQUEST_GET_GROUP_NAME:
                    tvTitle.setText(data.getStringExtra("group_name"));

                    break;

                default:
                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void uploadImages(List<Uri> uries) {
        if (uries != null) {
            for (Uri uri : uries) {
                uploadImage(uri);
            }
        }
    }

    private void uploadImage(Uri uri) {

        String path = LocalImageLoader.compressBitmap(this, FileUtil.getRealPathFromURI(this, uri), 480, 800, false);
        File file = new File(path);

//        File file = new File(FileUtil.getRealPathFromURI(this, uri));
        if (!file.exists()) {
            return;
        }

        Log.d("", "url@@@@@@@@@" + FileUtil.getRealPathFromURI(this, uri));

        Map<String, Object> params = new HashMap<>();
        params.put("content_creator_id", MainActivity.getUser().getUser_id());
        params.put("group_id", groupId);
        params.put("content_type", "post");
        params.put("content_group_public", "0");
        params.put("photo_caption", "");
        params.put("multiple", "0");
        params.put("file", file);
        params.put("photo_fullsize", "1");

        new HttpTools(ChatActivity.this).upload(Constant.API_MESSAGE_POST_PNG, params, new HttpCallback() {
            @Override
            public void onStart() {
                Log.i("", "11response==========");
            }

            @Override
            public void onFinish() {
                Log.i("", "222response==========");
            }

            @Override
            public void onResult(String response) {
                Log.i("", "333response==========" + response);
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if ("postPhoto".equals(jsonObject.getString("postType"))) {
                        getMsg();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.i("", "444response==========");
                /**
                 * begin QK
                 */
                Toast.makeText(ChatActivity.this, getResources().getString(R.string.text_error_try_again), Toast.LENGTH_SHORT).show();
                /**
                 * end
                 */
            }

            @Override
            public void onCancelled() {
                Log.i("", "555response==========");
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    private void finishReFresh() {
        swipeRefreshLayout.setRefreshing(false);
        isRefresh = false;
    }

    void removeFg() {
        if (fragmentsList != null) {
            for (int i = 0; i < fragmentsList.size(); i++) {
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragmentsList.get(i));
                fragmentTransaction.commit();
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //上传相册图片
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadImages(pickUries);
                        }
                    }).start();
                    break;
                case 1:
                    //上传相机拍照图片
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadImage(uri);
                        }
                    }).start();
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    boolean isCb1 = false;
    boolean isCb2 = false;

    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cb_1:
                if (!isCb1)
                {
                    isCb1 = true;
                    isCb2 = false;
                    UIUtil.hideKeyboard(ChatActivity.this, etChat);
                    ll1.setVisibility(View.VISIBLE);
                    cb1.setImageDrawable(getResources().getDrawable(R.drawable.chat_plus_press));
                    ll2.setVisibility(View.GONE);
                    cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_normal));
                    listView.setSelection(listView.getBottom());
                }
                else
                {
                    isCb1 = false;
                    ll1.setVisibility(View.GONE);
                    cb1.setImageDrawable(getResources().getDrawable(R.drawable.chat_plus_normal));
                }

                break;

            case R.id.cb_2:
                if (!isCb2)
                {
                    isCb2 = true;
                    isCb1 = false;
                    UIUtil.hideKeyboard(ChatActivity.this, etChat);
                    ll1.setVisibility(View.GONE);
                    cb1.setImageDrawable(getResources().getDrawable(R.drawable.chat_plus_normal));
                    ll2.setVisibility(View.VISIBLE);
                    cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_press));
                    listView.setSelection(listView.getBottom());
                }
                else
                {
                    isCb2 = false;
                    ll2.setVisibility(View.GONE);
                    cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_normal));
                }

                break;

            case R.id.ib_1:
                sticker1.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));


                removeFg();
                fragmentsList = new ArrayList<Fragment>();
                for (int i = 0; i < 2; i++) {
                    bundle = new Bundle();
                    StickerFragment stickerFragment = new StickerFragment();

                    bundle.putInt("page", i + 1);
                    bundle.putInt("allPage", 2);
                    bundle.putString("groupId", groupId);
                    bundle.putString("path", "A_day_life_of_Ping");
                    bundle.putString("type", ".gif");
                    stickerFragment.setArguments(bundle);

                    fragmentsList.add(stickerFragment);
                }
                stickerPaperAdapter = new StickerPaperAdapter(ChatActivity.this.getSupportFragmentManager(), fragmentsList);
                mPager.setAdapter(stickerPaperAdapter);
                break;
            case R.id.ib_2:
                sticker2.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));


                removeFg();
                fragmentsList = new ArrayList<Fragment>();
                for (int i = 0; i < 2; i++) {
                    bundle = new Bundle();
                    StickerFragment stickerFragment = new StickerFragment();

                    bundle.putInt("page", i + 1);
                    bundle.putInt("allPage", 2);
                    bundle.putString("groupId", groupId);
                    bundle.putString("path", "Baby_Trunky");
                    bundle.putString("type", ".gif");
                    stickerFragment.setArguments(bundle);

                    fragmentsList.add(stickerFragment);
                }
                stickerPaperAdapter = new StickerPaperAdapter(ChatActivity.this.getSupportFragmentManager(), fragmentsList);
                mPager.setAdapter(stickerPaperAdapter);
                break;
            case R.id.ib_3:
                sticker3.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));

                removeFg();
                fragmentsList = new ArrayList<Fragment>();
                for (int i = 0; i < 2; i++) {
                    bundle = new Bundle();
                    StickerFragment stickerFragment = new StickerFragment();

                    bundle.putInt("page", i + 1);
                    bundle.putInt("allPage", 2);
                    bundle.putString("groupId", groupId);
                    bundle.putString("path", "Bara-Bara_Na");
                    bundle.putString("type", ".png");
                    stickerFragment.setArguments(bundle);

                    fragmentsList.add(stickerFragment);
                }
                stickerPaperAdapter = new StickerPaperAdapter(ChatActivity.this.getSupportFragmentManager(), fragmentsList);
                mPager.setAdapter(stickerPaperAdapter);
                break;
            case R.id.ib_4:
                sticker4.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));


                removeFg();
                fragmentsList = new ArrayList<Fragment>();
                for (int i = 0; i < 2; i++) {
                    bundle = new Bundle();
                    StickerFragment stickerFragment = new StickerFragment();

                    bundle.putInt("page", i + 1);
                    bundle.putInt("allPage", 2);
                    bundle.putString("groupId", groupId);
                    bundle.putString("path", "Blue_Chip_Chip");
                    bundle.putString("type", ".png");
                    stickerFragment.setArguments(bundle);

                    fragmentsList.add(stickerFragment);
                }
                stickerPaperAdapter = new StickerPaperAdapter(ChatActivity.this.getSupportFragmentManager(), fragmentsList);
                mPager.setAdapter(stickerPaperAdapter);
                break;
            case R.id.ib_5:
                sticker5.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));


                removeFg();
                fragmentsList = new ArrayList<Fragment>();
                for (int i = 0; i < 2; i++) {
                    bundle = new Bundle();
                    StickerFragment stickerFragment = new StickerFragment();

                    bundle.putInt("page", i + 1);
                    bundle.putInt("allPage", 2);
                    bundle.putString("groupId", groupId);
                    bundle.putString("path", "CNY_greeting");
                    bundle.putString("type", ".png");
                    stickerFragment.setArguments(bundle);

                    fragmentsList.add(stickerFragment);
                }
                stickerPaperAdapter = new StickerPaperAdapter(ChatActivity.this.getSupportFragmentManager(), fragmentsList);
                mPager.setAdapter(stickerPaperAdapter);
                break;
            case R.id.ib_6:
                sticker6.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));

                removeFg();
                fragmentsList = new ArrayList<Fragment>();
                for (int i = 0; i < 2; i++) {
                    bundle = new Bundle();
                    StickerFragment stickerFragment = new StickerFragment();

                    bundle.putInt("page", i + 1);
                    bundle.putInt("allPage", 2);
                    bundle.putString("groupId", groupId);
                    bundle.putString("path", "CNY_greeting2");
                    bundle.putString("type", ".gif");
                    stickerFragment.setArguments(bundle);

                    fragmentsList.add(stickerFragment);
                }
                stickerPaperAdapter = new StickerPaperAdapter(ChatActivity.this.getSupportFragmentManager(), fragmentsList);
                mPager.setAdapter(stickerPaperAdapter);
                break;

            case R.id.ib_7:
                sticker7.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));


                removeFg();
                fragmentsList = new ArrayList<Fragment>();
                for (int i = 0; i < 2; i++) {
                    bundle = new Bundle();
                    StickerFragment stickerFragment = new StickerFragment();

                    bundle.putInt("page", i + 1);
                    bundle.putInt("allPage", 2);
                    bundle.putString("groupId", groupId);
                    bundle.putString("path", "Cantonese_Slang");
                    bundle.putString("type", ".gif");
                    stickerFragment.setArguments(bundle);

                    fragmentsList.add(stickerFragment);
                }
                stickerPaperAdapter = new StickerPaperAdapter(ChatActivity.this.getSupportFragmentManager(), fragmentsList);
                mPager.setAdapter(stickerPaperAdapter);
                break;

            case R.id.ib_8:
                sticker8.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));


                removeFg();
                fragmentsList = new ArrayList<Fragment>();
                for (int i = 0; i < 2; i++) {
                    bundle = new Bundle();
                    StickerFragment stickerFragment = new StickerFragment();

                    bundle.putInt("page", i + 1);
                    bundle.putInt("allPage", 2);
                    bundle.putString("groupId", groupId);
                    bundle.putString("path", "Cherry_Cherry");
                    bundle.putString("type", ".png");
                    stickerFragment.setArguments(bundle);

                    fragmentsList.add(stickerFragment);
                }
                stickerPaperAdapter = new StickerPaperAdapter(ChatActivity.this.getSupportFragmentManager(), fragmentsList);
                mPager.setAdapter(stickerPaperAdapter);
                break;

            case R.id.ib_9:
                sticker9.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));


                removeFg();
                fragmentsList = new ArrayList<Fragment>();
                for (int i = 0; i < 2; i++) {
                    bundle = new Bundle();
                    StickerFragment stickerFragment = new StickerFragment();

                    bundle.putInt("page", i + 1);
                    bundle.putInt("allPage", 2);
                    bundle.putString("groupId", groupId);
                    bundle.putString("path", "DailyLife");
                    bundle.putString("type", ".png");
                    stickerFragment.setArguments(bundle);

                    fragmentsList.add(stickerFragment);
                }
                stickerPaperAdapter = new StickerPaperAdapter(ChatActivity.this.getSupportFragmentManager(), fragmentsList);
                mPager.setAdapter(stickerPaperAdapter);
                break;

            case R.id.ib_10:
                sticker10.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));

                removeFg();
                fragmentsList = new ArrayList<Fragment>();
                for (int i = 0; i < 2; i++) {
                    bundle = new Bundle();
                    StickerFragment stickerFragment = new StickerFragment();

                    bundle.putInt("page", i + 1);
                    bundle.putInt("allPage", 2);
                    bundle.putString("groupId", groupId);
                    bundle.putString("path", "Easter");
                    bundle.putString("type", ".gif");
                    stickerFragment.setArguments(bundle);

                    fragmentsList.add(stickerFragment);
                }
                stickerPaperAdapter = new StickerPaperAdapter(ChatActivity.this.getSupportFragmentManager(), fragmentsList);
                mPager.setAdapter(stickerPaperAdapter);
                break;

            case R.id.et_chat:

                ll1.setVisibility(View.GONE);
                ll2.setVisibility(View.GONE);

                isCb1 = false;
                isCb2 = false;

                cb1.setImageDrawable(getResources().getDrawable(R.drawable.chat_plus_normal));
                cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_normal));

                listView.setSelection(listView.getBottom());
                listView.smoothScrollToPosition(listView.getBottom());

                break;
            default:
                super.onClick(v);
                break;
        }
    }


    public void getMsgFirst() {
        progressDialog.show();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", "10");
        params.put("offset", "0");
        params.put("group_id", groupId);
        params.put("page", "1");
        params.put("start", "0");
        params.put("view_user", MainActivity.getUser().getUser_id());
        String url = UrlUtil.generateUrl(Constant.API_GET_MESSAGE, params);


        new HttpTools(ChatActivity.this).get(url, null, new HttpCallback() {
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

                msgList = gson.fromJson(response, new TypeToken<ArrayList<MsgEntity>>() {
                }.getType());

                chatAdapter = new ChatAdapter(ChatActivity.this, msgList);
                listView.setAdapter(chatAdapter);
                listView.setSelection(listView.getBottom());
                etChat.setText("");
                progressDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });


//        StringRequest srMsg = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                GsonBuilder gsonb = new GsonBuilder();
//
//                Gson gson = gsonb.create();
//
//                msgList = gson.fromJson(response, new TypeToken<ArrayList<MsgEntity>>() {
//                }.getType());
//
//                chatAdapter = new ChatAdapter(ChatActivity.this, msgList);
//                listView.setAdapter(chatAdapter);
//                listView.setSelection(listView.getBottom());
//                etChat.setText("");
//                progressDialog.dismiss();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
//            }
//        });
//        VolleyUtil.addRequest2Queue(ChatActivity.this, srMsg, "");
    }

    @Override
    protected void onDestroy() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}
