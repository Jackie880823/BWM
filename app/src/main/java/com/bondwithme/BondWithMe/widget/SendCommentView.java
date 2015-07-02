package com.bondwithme.BondWithMe.widget;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.util.UIUtil;

/**
 * Created by liangzemian on 15/5/11.
 */
public class SendCommentView extends FrameLayout implements View.OnClickListener{
    /**
     * extends FrameLayout
     */

    private ImageButton cb1;//加号
    private ImageButton cb2;//表情
    private EditText etChat;//输入框
    private LinearLayout ll1;//加号隐藏布局
    private LinearLayout ll2;//表情库隐藏布局
    private ViewPager mPager;//表情库ViewPager


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

    LinearLayout llCamera;//相机
    LinearLayout llAlbum;//图库
    LinearLayout llLocation;//地图
    LinearLayout llStickers;//
    LinearLayout llVideo;//录像
    LinearLayout llContact;//联系

    private TextView btnSend;//聊天发送
    Context context;

    /*相册和相机使用的参数*/
    private final static int REQUEST_HEAD_PHOTO = 1;
    private final static int REQUEST_HEAD_CAMERA = 2;
    private final static int REQUEST_HEAD_FINAL = 3;

    private String imagePath;
    /**
     * 原图uri
     */
    Uri uri;//原图uri
    /**
     * 头像缓存文件名称
     */
    public final static String CACHE_PIC_NAME = "head_cache";
    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";




    public SendCommentView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //将自定义的控件布局渲染成view
        View view = View.inflate(context, R.layout.send_comment,this);
        cb1 = (ImageButton) findViewById(R.id.cb_1);
        cb2 = (ImageButton) findViewById(R.id.cb_2);

        mPager = (ViewPager) findViewById(R.id.viewpager);

//        sticker1 = (LinearLayout) findViewById(R.id.ib_1);
//        sticker2 = (LinearLayout) findViewById(R.id.ib_2);
//        sticker3 = (LinearLayout) findViewById(R.id.ib_3);
//        sticker4 = (LinearLayout) findViewById(R.id.ib_4);
//        sticker5 = (LinearLayout) findViewById(R.id.ib_5);
//        sticker6 = (LinearLayout) findViewById(R.id.ib_6);
//        sticker7 = (LinearLayout) findViewById(R.id.ib_7);
//        sticker8 = (LinearLayout) findViewById(R.id.ib_8);
//        sticker9 = (LinearLayout) findViewById(R.id.ib_9);
//        sticker10 = (LinearLayout) findViewById(R.id.ib_10);

        cb1.setOnClickListener(this);
        cb2.setOnClickListener(this);

//        sticker1.setOnClickListener(this);
//        sticker2.setOnClickListener(this);
//        sticker3.setOnClickListener(this);
//        sticker4.setOnClickListener(this);
//        sticker5.setOnClickListener(this);
//        sticker6.setOnClickListener(this);
//        sticker7.setOnClickListener(this);
//        sticker8.setOnClickListener(this);
//        sticker9.setOnClickListener(this);
//        sticker10.setOnClickListener(this);
//        onClick(sticker1);

        etChat = (EditText) findViewById(R.id.et_chat);
        etChat.setOnClickListener(this);

        ll1 = (LinearLayout) findViewById(R.id.ll_1);
        ll2 = (LinearLayout) findViewById(R.id.ll_2);

        llCamera = (LinearLayout) findViewById(R.id.ll_camera);
        llAlbum = (LinearLayout) findViewById(R.id.ll_album);
        llLocation = (LinearLayout) findViewById(R.id.ll_location);
        llStickers = (LinearLayout) findViewById(R.id.ll_stickers);
        llVideo = (LinearLayout) findViewById(R.id.ll_video);
        llContact = (LinearLayout) findViewById(R.id.ll_contact);

        btnSend = (TextView) findViewById(R.id.btn_send);


        //相机点击事件
        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //TODO
                if (commentClickListener != null ) {
                    commentClickListener.cameraClick();
                }
//                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                intent2.putExtra("android.intent.extras.CAMERA_FACING", 1);
//                intent2.putExtra("camerasensortype", 2);
//
//                // 下面这句指定调用相机拍照后的照片存储的路径
//                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri
//                        .fromFile(PicturesCacheUtil.getCachePicFileByName(context,
//                                CACHE_PIC_NAME_TEMP)));
//                // 图片质量为高
//                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//                intent2.putExtra("return-data", false);
//                startActivityForResult(intent2, REQUEST_HEAD_CAMERA);
//                this.startActivityForResult(intent2,2);
            }
        });
        llAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //TODO
                if(commentClickListener != null){
                    commentClickListener.albumClick();
                }
//                Intent intent = new Intent(Intent.ACTION_PICK, null);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(intent, REQUEST_HEAD_PHOTO);
            }
        });
        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if(commentClickListener != null){
                    commentClickListener.locationClick();
                }
            }
        });

        llStickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if(commentClickListener != null){
                    commentClickListener.stickersClick();
                }
                UIUtil.hideKeyboard(context, etChat);
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
                if(commentClickListener != null){
                    commentClickListener.videoClick();
                }
            }
        });
        llContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if(commentClickListener != null){
                    commentClickListener.contactClick();
                }
            }
        });
        llStickers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentClickListener != null){
                    commentClickListener.stickersClick();
                }

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                if(commentClickListener != null){
                    commentClickListener.sendClick();
                }
            }
        });

    }


    boolean isCb1 = false;
    boolean isCb2 = false;

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cb_1:
                if(!isCb1)
                {
                    isCb1 = true;
                    isCb2 = false;
                    UIUtil.hideKeyboard(context, etChat);
                    ll1.setVisibility(View.VISIBLE);
                    cb1.setImageDrawable(getResources().getDrawable(R.drawable.chat_plus_press));
                    ll2.setVisibility(View.GONE);
                    cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_normal));
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
                    UIUtil.hideKeyboard(context, etChat);
                    ll1.setVisibility(View.GONE);
                    cb1.setImageDrawable(getResources().getDrawable(R.drawable.chat_plus_normal));
                    ll2.setVisibility(View.VISIBLE);
                    cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_press));
//                    listView.setSelection(listView.getBottom());
                }
                else
                {
                    isCb2 = false;
                    ll2.setVisibility(View.GONE);
                    cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_normal));
                }
                break;
//            case R.id.ib_1:
//                sticker1.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//
//                //fragment
//                break;
//
//            case R.id.ib_2:
//                sticker2.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                break;
//            case R.id.ib_3:
//                sticker3.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                break;
//            case R.id.ib_4:
//                sticker4.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                break;
//            case R.id.ib_5:
//                sticker5.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                break;
//            case R.id.ib_6:
//                sticker6.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                break;
//            case R.id.ib_7:
//                sticker7.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                break;
//            case R.id.ib_8:
//                sticker8.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                break;
//            case R.id.ib_9:
//                sticker9.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker10.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                break;
//            case R.id.ib_10:
//                sticker10.setBackgroundColor(getResources().getColor(R.color.default_text_color_while));
//                sticker1.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker2.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker3.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker4.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker5.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker6.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker7.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker8.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                sticker9.setBackgroundColor(getResources().getColor(R.color.default_sticker));
//                break;
            case R.id.et_chat:

                ll1.setVisibility(View.GONE);
                ll2.setVisibility(View.GONE);

                isCb1 = false;
                isCb2 = false;

                cb1.setImageDrawable(getResources().getDrawable(R.drawable.chat_plus_normal));
                cb2.setImageDrawable(getResources().getDrawable(R.drawable.chat_expression_normal));

//                listView.setSelection(listView.getBottom());
//                listView.smoothScrollToPosition(listView.getBottom());

                break;
            default:
//                super.onClick(v);
                break;
        }

    }

    public CommentClickListener commentClickListener;

    public void setCommentClickListener(CommentClickListener commentClickListener) {
        this.commentClickListener = commentClickListener;
    }

    public interface CommentClickListener {
        void cameraClick();
        void albumClick();
        void locationClick();
        void stickersClick();
        void videoClick();
        void contactClick();
        void sendClick();

    }

}
