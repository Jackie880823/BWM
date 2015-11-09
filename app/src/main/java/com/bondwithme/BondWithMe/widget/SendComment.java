package com.bondwithme.BondWithMe.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.MediaData;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.interfaces.StickerViewClickListener;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.StickerMainFragment;
import com.bondwithme.BondWithMe.ui.StickerMainNewFragment;
import com.bondwithme.BondWithMe.ui.share.SelectPhotosActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.UIUtil;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.File;

/**
 * Created by Jackie Zhu on 5/13/15.
 */
public class SendComment extends FrameLayout implements View.OnClickListener, StickerViewClickListener {

    private static final String TAG = SendComment.class.getSimpleName();

    private int cache_count = 0;
    private ImageButton ibMore;
    private ImageButton ibSticker;
    private EditText etChat;
    private LinearLayout llMore;
    private LinearLayout llSticker;
    private ImageView ivPhoto;
    private CardView cvLayout;
    private BaseActivity mActivity;
    private BaseFragment fragment;
    private InputMethodManager imm;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public void initViewPager(BaseActivity activity, BaseFragment fragment) {
        mActivity = activity;
        this.fragment = fragment;
        if (mActivity.isFinishing()) {
            return;
        }
    }

    public void commitAllowingStateLoss() {

        // 开启一个Fragment事务
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        String filePath = FileUtil.getSaveRootPath(mActivity, false).getAbsolutePath() + File.separator + "Sticker";
        File file = new File(filePath);
        if (file.exists()) {
            StickerMainFragment mainFragment = new StickerMainFragment();//selectStickerName, MessageChatActivity.this, groupId);
            mainFragment.setPicClickListener(this);
            transaction.replace(R.id.sticker_message_fragment, mainFragment);
        } else {
            StickerMainNewFragment mainFragment = new StickerMainNewFragment();//selectStickerName, MessageChatActivity.this, groupId);
            mainFragment.setPicClickListener(this);
            transaction.replace(R.id.sticker_message_fragment, mainFragment);
        }
        //        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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
        ImageButton tvSend = (ImageButton) findViewById(R.id.tv_send);
        cvLayout = (CardView) findViewById(R.id.cv_send_comment_images);
        ivPhoto = (ImageView) findViewById(R.id.iv_pic);


        ibMore.setOnClickListener(this);
        ibSticker.setOnClickListener(this);
        etChat.setOnClickListener(this);
        tvSend.setOnClickListener(this);

        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

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
        switch (v.getId()) {
            case R.id.ib_more://扩展功能按钮
                if (llMore.getVisibility() == View.VISIBLE) {
                    hideAllViewState();
                } else {
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showExpandFunctionView();
                            }
                        }, 100);
                    } else {
                        showExpandFunctionView();
                    }
                }
                break;
            case R.id.ib_sticker://表情功能按钮
                if (llSticker.getVisibility() == View.VISIBLE) {
                    hideAllViewState();
                } else {

                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(etChat.getWindowToken(), 0);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showStickerView();
                            }
                        }, 100);
                    } else {
                        showStickerView();
                    }
                }
                break;
            case R.id.et_chat:
                hideAllViewState();
                break;
            case R.id.camera_tv://打开相机

                if (mActivity != null) {
                    cache_count++;
                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent2.putExtra("android.intent.extras.CAMERA_FACING", getCameraId(false));
                    intent2.putExtra("autofocus", true);

                    // 下面这句指定调用相机拍照后的照片存储的路径
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(mActivity, Constant.CACHE_PIC_NAME_TEMP + cache_count)));
                    // 图片质量为高
                    intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    intent2.putExtra("return-data", true);
                    fragment.startActivityForResult(intent2, Constant.INTENT_REQUEST_HEAD_CAMERA);
                }
                break;
            case R.id.album_tv://打开本地相册

                if (mActivity != null) {
                    Intent intent = new Intent(mActivity, SelectPhotosActivity.class);
                    //                Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.putExtra(MediaData.EXTRA_USE_UNIVERSAL, true);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    fragment.startActivityForResult(intent, Constant.INTENT_REQUEST_HEAD_PHOTO);
                }
                break;
            case R.id.location_tv://打开地图
                break;
            case R.id.video_tv://视频功能
                break;
            case R.id.contact_tv://打开名片
                break;
            case R.id.tv_send:
                hideAllViewState();
                cvLayout.setVisibility(View.GONE);
                if (commentListener != null) {
                    commentListener.onSendCommentClick(etChat);
                }
                break;
            case R.id.pic_delete:
                cvLayout.setVisibility(GONE);
                if (commentListener != null) {
                    commentListener.onRemoveClick();
                }
                break;
        }
    }

    private int getCameraId(boolean front) {
        int num = android.hardware.Camera.getNumberOfCameras();
        for (int i = 0; i < num; i++) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(i, info);
            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT && front) {
                return i;
            }
            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK && !front) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 显示扩展功能视图
     */
    private void showExpandFunctionView() {
        if (llSticker.getVisibility() == View.VISIBLE) {
            llSticker.setVisibility(View.GONE);
            ibSticker.setImageResource(R.drawable.chat_expression_normal);
        }
        ibMore.setImageResource(R.drawable.chat_plus_press);
        llMore.setVisibility(View.VISIBLE);
    }

    /**
     * 显示表视图
     */
    private void showStickerView() {
        if (llMore.getVisibility() == View.VISIBLE) {
            llMore.setVisibility(View.GONE);
            ibMore.setImageResource(R.drawable.chat_plus_normal);
        }
        ibSticker.setImageResource(R.drawable.chat_expression_press);
        llSticker.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏表、扩展功能和键盘视图
     */
    private void hideAllViewState() {
        llMore.setVisibility(View.GONE);
        llSticker.setVisibility(View.GONE);
        ibMore.setImageResource(R.drawable.chat_plus_normal);
        ibSticker.setImageResource(R.drawable.chat_expression_normal);
    }

    public void hideAllViewState(boolean hideKeyboard) {
        if (hideKeyboard) {
            UIUtil.hideKeyboard(getContext(), etChat);
        }
        hideAllViewState();
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
        LogUtil.i(TAG, "onActivityResult& requestCode = " + requestCode + "; resultCode = " + resultCode);
        if (Activity.RESULT_OK == resultCode) {

            switch (requestCode) {
                // 如果是直接从相册获取
                case Constant.INTENT_REQUEST_HEAD_PHOTO:
                    if (data != null) {
                        Uri uri = data.getData();
                        if (commentListener != null) {
                            commentListener.onReceiveBitmapUri(uri);
                            hideAllViewState();
                        }
                    } else {

                    }
                    break;
                // 如果是调用相机拍照时
                case Constant.INTENT_REQUEST_HEAD_CAMERA:
                    Uri uri = Uri.fromFile(PicturesCacheUtil.getCachePicFileByName(mActivity, Constant.CACHE_PIC_NAME_TEMP + cache_count));
                    uri = Uri.parse(ImageDownloader.Scheme.FILE.wrap(uri.getPath()));
                    LogUtil.i(TAG, "onActivityResult& uri: " + uri.getPath());
                    if (new File(uri.getPath()).exists()) {
                        if (commentListener != null) {
                            commentListener.onReceiveBitmapUri(uri);
                            hideAllViewState();
                        }
                    }
                    break;
                default:
                    break;

            }
        } else {
            LogUtil.w(TAG, "onActivityResult& resultCode = " + resultCode + " invalid");
        }
    }

    /**
     * @param type       sticker的后缀类型(.gif)
     * @param folderName 放置sticker的文件夹名称
     * @param filName    sticker的文件名
     */
    public void showComments(String type, String folderName, String filName) {
        LogUtil.i(TAG, "showComments& type: " + type);
        if (commentListener != null) {
            commentListener.onStickerItemClick(type, folderName, filName);
            hideAllViewState(true);
        }
    }


    CommentListener commentListener;

    public void setCommentListener(CommentListener commentListener) {
        this.commentListener = commentListener;
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
