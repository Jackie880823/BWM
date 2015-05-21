package com.madx.bwm.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.http.PicturesCacheUtil;
import com.madx.bwm.interfaces.StickerViewClickListener;
import com.madx.bwm.ui.BaseActivity;
import com.madx.bwm.ui.BaseFragment;
import com.madx.bwm.ui.MessageChatActivity;
import com.madx.bwm.ui.StickerMainFragment;
import com.madx.bwm.ui.wall.SelectPhotosActivity;
import com.madx.bwm.util.UIUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Jackie Zhu on 5/13/15.
 */
public class SendComment extends FrameLayout implements View.OnClickListener, StickerViewClickListener {

    private static final String TAG = SendComment.class.getSimpleName();

    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";

    public final static int REQUEST_HEAD_PHOTO = 1;
    public final static int REQUEST_HEAD_CAMERA = 2;
    private int cache_count = 0;
    private ImageButton ibMore;
    private ImageButton ibSticker;
    private EditText etChat;
    private TextView tvSend;
    private LinearLayout llMore;
    private LinearLayout llSticker;
    private ImageView ivPhoto;
    private CardView cvLayout;
    private BaseActivity mActivity;
    private BaseFragment fragment;

    public void initViewPager(BaseActivity activity, BaseFragment fragment) {
        mActivity = activity;
        this.fragment = fragment;
        if(mActivity.isFinishing()) {
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
        cvLayout = (CardView) findViewById(R.id.cv_send_comment_images);
        ivPhoto = (ImageView) findViewById(R.id.iv_pic);


        ibMore.setOnClickListener(this);
        ibSticker.setOnClickListener(this);
        etChat.setOnClickListener(this);
        tvSend.setOnClickListener(this);
        findViewById(R.id.camera_tv).setOnClickListener(this);
        findViewById(R.id.album_tv).setOnClickListener(this);
        findViewById(R.id.location_tv).setOnClickListener(this);
        findViewById(R.id.video_tv).setOnClickListener(this);
        findViewById(R.id.contact_tv).setOnClickListener(this);
        findViewById(R.id.pic_delete).setOnClickListener(this);
    }

    /**
     * 设置显示图片
     *
     * @param bitmap
     */
    public void setSendBitmap(Bitmap bitmap) {
        cvLayout.setVisibility(View.VISIBLE);
        ivPhoto.setImageBitmap(bitmap);
    }

    /**
     * 设置显示图片
     *
     * @param drawable
     */
    public void setSendDrawable(Drawable drawable) {
        cvLayout.setVisibility(View.VISIBLE);
        ivPhoto.setImageDrawable(drawable);
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
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(!imm.isActive()) {
                    UIUtil.showKeyboard(getContext(), etChat);
                }
                break;
            case R.id.camera_tv://打开相机
                if(listener != null) {
                    listener.onClickCamera();
                }

                if(mActivity != null) {
                    cache_count++;
                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent2.putExtra("android.intent.extras.CAMERA_FACING", getCameraId(false));
                    intent2.putExtra("autofocus", true);

                    // 下面这句指定调用相机拍照后的照片存储的路径
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(mActivity, CACHE_PIC_NAME_TEMP + cache_count)));
                    // 图片质量为高
                    intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    intent2.putExtra("return-data", true);
                    fragment.startActivityForResult(intent2, REQUEST_HEAD_CAMERA);
                }
                break;
            case R.id.album_tv://打开本地相册
                if(listener != null) {
                    listener.onClickAlbum();
                }

                if(mActivity != null) {
                    Intent intent = new Intent(mActivity, SelectPhotosActivity.class);
                    //                Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    fragment.startActivityForResult(intent, SendComment.REQUEST_HEAD_PHOTO);
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

                hideAllViewState();
                cvLayout.setVisibility(View.GONE);
                if(commentListener != null) {
                    commentListener.onSendCommentClick(etChat);
                }
                break;
            case R.id.pic_delete:
                cvLayout.setVisibility(GONE);
                if(commentListener != null) {
                    commentListener.onRemoveClick();
                }
                break;
        }
    }

    private int getCameraId(boolean front) {
        int num = android.hardware.Camera.getNumberOfCameras();
        for(int i = 0; i < num; i++) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(i, info);
            if(info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT && front) {
                return i;
            }
            if(info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK && !front) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 显示扩展功能视图
     */
    private void showExpandFunctionView() {
        if(llSticker.getVisibility() == View.VISIBLE) {
            llSticker.setVisibility(View.GONE);
            ibSticker.setImageResource(R.drawable.chat_expression_normal);
        }
        llMore.setVisibility(View.VISIBLE);
        ibMore.setImageResource(R.drawable.chat_plus_press);
    }

    /**
     * 显示表视图
     */
    private void showStickerView() {
        if(llMore.getVisibility() == View.VISIBLE) {
            llMore.setVisibility(View.GONE);
            ibMore.setImageResource(R.drawable.chat_plus_normal);
        }
        llSticker.setVisibility(View.VISIBLE);
        ibSticker.setImageResource(R.drawable.chat_expression_press);
    }

    /**
     * 隐藏表、扩展功能和键盘视图
     */
    private void hideAllViewState() {
//        UIUtil.hideKeyboard(getContext(), etChat);
        llMore.setVisibility(View.GONE);
        llSticker.setVisibility(View.GONE);
        ibMore.setImageResource(R.drawable.chat_plus_normal);
        ibSticker.setImageResource(R.drawable.chat_expression_normal);
    }

    /**
     * Receive the result from a previous call to
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult& requestCode = " + requestCode + "; resultCode = " + resultCode);
        if(mActivity.RESULT_OK == resultCode) {

            switch(requestCode) {
                // 如果是直接从相册获取
                case REQUEST_HEAD_PHOTO:
                    if(data != null) {
                        Uri uri = data.getData();
                        if(commentListener != null) {
                            commentListener.onReceiveBitmapUri(uri);
                            hideAllViewState();
                        }
                    } else {

                    }
                    break;
                // 如果是调用相机拍照时
                case REQUEST_HEAD_CAMERA:
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(mActivity, CACHE_PIC_NAME_TEMP + cache_count));
                    Log.i(TAG, "onActivityResult& uri: " + uri.getPath());
                    if(new File(uri.getPath()).exists()) {
                        if(commentListener != null) {
                            commentListener.onReceiveBitmapUri(uri);
                            hideAllViewState();
                        }
                    }
                    break;
                default:
                    break;

            }
        } else {
            Log.w(TAG, "onActivityResult& resultCode = " + resultCode + " invalid");
        }
    }

    /**
     * @param type       sticker的后缀类型(.gif)
     * @param folderName 放置sticker的文件夹名称
     * @param filName    sticker的文件名
     */
    public void showComments(String type, String folderName, String filName) {
        if(listener != null) {
            listener.onStickerItemClick(type, folderName, filName);
        }
        Log.i(TAG, "showComments& type: " + type);
        String path = null;
        if(Constant.Sticker_Gif.equals(type)) {
            path = MessageChatActivity.STICKERS_NAME + File.separator + folderName + File.separator + filName + "_B.gif";
            try {
                GifDrawable gifDrawable = new GifDrawable(mActivity.getAssets(), path);
                if(gifDrawable != null) {
                    setSendDrawable(gifDrawable);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else if(Constant.Sticker_Png.equals(type)) {
            path = MessageChatActivity.STICKERS_NAME + File.separator + folderName + File.separator + filName + "_B.png";
            try {
                InputStream is = mActivity.getAssets().open(path);
                if(is != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    setSendBitmap(bitmap);
                    setSendBitmap(bitmap);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "showComments& path: " + path);
        if(commentListener != null) {
            commentListener.onStickerItemClick(type, folderName, filName);
        }
    }

    ChildViewClickListener listener;

    @Deprecated
    public void setListener(ChildViewClickListener listener) {
        this.listener = listener;
    }

    CommentListener commentListener;

    public void setCommentListenr(CommentListener commentListener) {
        this.commentListener = commentListener;
    }

    @Deprecated
    public interface ChildViewClickListener {
        /**
         * @param type       sticker的后缀类型(.gif)
         * @param folderName 放置sticker的文件夹名称
         * @param filName    sticker的文件名
         */
        @Deprecated
        void onStickerItemClick(String type, String folderName, String filName);

        /**
         * 点击打开相删
         */
        @Deprecated
        void onClickAlbum();

        /**
         * 点击打开相机
         */
        @Deprecated
        void onClickCamera();

        /**
         * 点击添加地址
         */
        @Deprecated
        void onClickLocation();

        /**
         * 点击添加视频
         */
        @Deprecated
        void onClickVideo();

        /**
         * 点击添加联系人
         */
        @Deprecated
        void onClickContact();

        /**
         * 发送评论
         *
         * @param et
         */
        @Deprecated
        void onSendCommentClick(EditText et);
    }

    public interface CommentListener {
        /**
         * @param type       sticker的后缀类型(.gif)
         * @param folderName 放置sticker的文件夹名称
         * @param filName    sticker的文件名
         */
        void onStickerItemClick(String type, String folderName, String filName);

        /**
         * 得到图片uri
         *
         * @param uri
         */
        void onReceiveBitmapUri(Uri uri);

        /**
         * 发送评论
         *
         * @param et
         */
        void onSendCommentClick(EditText et);

        /**
         * 删除被点击,删除添加的图片或表情
         */
        void onRemoveClick();
    }
}
