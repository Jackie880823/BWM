package com.madx.bwm.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.madx.bwm.R;

/**
 * Created by liangzemian on 15/5/11.
 */
public class SendCommentView extends FrameLayout{
    /**
     * extends FrameLayout
     */
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

    LinearLayout llCamera;
    LinearLayout llAlbum;
    LinearLayout llLocation;
    LinearLayout llStickers;
    LinearLayout llVideo;
    LinearLayout llContact;

    Context context;

    public SendCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //将自定义的控件布局渲染成view
        View view = View.inflate(context, R.layout.send_comment,this);


    }


}
