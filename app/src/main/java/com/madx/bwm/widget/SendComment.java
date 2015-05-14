package com.madx.bwm.widget;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.madx.bwm.R;
import com.madx.bwm.interfaces.StickerViewClickListener;
import com.madx.bwm.ui.BaseActivity;
import com.madx.bwm.ui.StickerMainFragment;
import com.madx.bwm.util.UIUtil;

/**
 * Created by zhuweiping on 5/13/15.
 */
public class SendComment extends FrameLayout implements View.OnClickListener, StickerViewClickListener {

    private ImageButton ibMore;
    private ImageButton ibSticker;
    private EditText etChat;
    private TextView tvSend;
    private LinearLayout llMore;
    private LinearLayout llSticker;

    public void initViewPager(BaseActivity activity) {
        if(activity.isFinishing()) {
            return;
        }
        // 开启一个Fragment事务
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        StickerMainFragment mainFragment = new StickerMainFragment();//selectStickerName, MessageChatActivity.this, groupId);
        mainFragment.setPicClickListener(this);
        transaction.replace(R.id.sticker_message_fragment, mainFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p/>
     * <p/>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public SendComment(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_send_commond, this);
        ibMore = (ImageButton) findViewById(R.id.ib_more);
        llMore = (LinearLayout) findViewById(R.id.ll_more);
        ibSticker = (ImageButton) findViewById(R.id.ib_sticker);
        llSticker = (LinearLayout) findViewById(R.id.ll_sticker);
        etChat = (EditText) findViewById(R.id.et_chat);
        tvSend = (TextView) findViewById(R.id.tv_send);

        ibMore.setOnClickListener(this);
        ibSticker.setOnClickListener(this);
        etChat.setOnClickListener(this);
        tvSend.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ib_more://扩展功能按钮
                if(llMore.getVisibility() == View.VISIBLE) {
                    hideAllViewState();
                } else {
                    UIUtil.hideKeyboard(getContext(), etChat);
                    showExpandFunctionView();
                }
                break;
            case R.id.ib_sticker://表情功能按钮
                if(llSticker.getVisibility() == View.VISIBLE) {
                    hideAllViewState();
                } else {
                    UIUtil.hideKeyboard(getContext(), etChat);
                    showStickerView();
                }
                break;
            case R.id.et_chat:
                hideAllViewState();
                UIUtil.showKeyboard(getContext(), etChat);
                break;
            case R.id.camera_tv://打开相机
                if(listener != null) {
                    listener.onClickCamera();
                }
                break;
            case R.id.album_tv://打开本地相册
                if(listener != null) {
                    listener.onClickAlbum();
                }
                break;
            case R.id.location_tv://打开地图
                if(listener != null) {
                    listener.onClickLocation();
                }
                break;
            case R.id.video_tv://视频功能
                if(listener != null) {
                    listener.onClickVideo();
                }
                break;
            case R.id.contact_tv://打开名片
                if(listener != null) {
                    listener.onClickContact();
                }
                break;
            case R.id.tv_send:
                if(listener != null) {
                    listener.onSendCommentClick(etChat);
                }
                break;
        }
    }


    private void showExpandFunctionView() {
        if(llSticker.getVisibility() == View.VISIBLE) {
            llSticker.setVisibility(View.GONE);
            ibSticker.setImageResource(R.drawable.chat_expression_normal);
        }
        llMore.setVisibility(View.VISIBLE);
        ibMore.setImageResource(R.drawable.chat_plus_press);
    }

    private void showStickerView() {
        if(llMore.getVisibility() == View.VISIBLE) {
            llMore.setVisibility(View.GONE);
            ibMore.setImageResource(R.drawable.chat_plus_normal);
        }
        llSticker.setVisibility(View.VISIBLE);
        ibSticker.setImageResource(R.drawable.chat_expression_press);
    }

    private void hideAllViewState() {
        UIUtil.hideKeyboard(getContext(), etChat);
        llMore.setVisibility(View.GONE);
        llSticker.setVisibility(View.GONE);
        ibMore.setImageResource(R.drawable.chat_plus_normal);
        ibSticker.setImageResource(R.drawable.chat_expression_normal);
    }

    /**
     * @param type       sticker的后缀类型(.gif)
     * @param folderName 放置sticker的文件夹名称
     * @param filName    sticker的文件名
     */
    @Override
    public void showComments(String type, String folderName, String filName) {
        if(listener != null) {
            listener.onStickerItemClick(type, folderName, filName);
        }
    }

    ChildViewClickListener listener;

    public void setListener(ChildViewClickListener listener) {
        this.listener = listener;
    }

    public interface ChildViewClickListener {
        /**
         * @param type       sticker的后缀类型(.gif)
         * @param folderName 放置sticker的文件夹名称
         * @param filName    sticker的文件名
         */
        void onStickerItemClick(String type, String folderName, String filName);

        void onClickAlbum();

        void onClickCamera();

        void onClickLocation();

        void onClickVideo();

        void onClickContact();

        void onSendCommentClick(EditText et);
    }
}
