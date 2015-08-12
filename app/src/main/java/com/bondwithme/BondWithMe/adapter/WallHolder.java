package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.WallUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.FreedomSelectionTextView;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Jackie on 8/7/15.
 * <p/>
 * 日志内容的{@link RecyclerView.ViewHolder}, 在Wall界面各Comment都使用这个Holder来显示日志内容
 *
 * @author Jackie
 * @version 1.0
 */
public class WallHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = WallHolder.class.getSimpleName();

    private static final String POST_LOVE = TAG + "_POST_LOVE";

    private WallViewClickListener mViewClickListener;
    private WallEntity wallEntity;
    private HttpTools mHttpTools;

    /**
     * 头像视图
     */
    private CircularNetworkImage nivHead;
    /**
     * 日志文字内容
     */
    private FreedomSelectionTextView tvContent;

    /**
     * 发表日期显示视图
     */
    private TextView tvDate;

    /**
     * 用户昵称视图
     */
    private TextView tvUserName;

    /**
     * 网络图片视图父控件
     */
    private View llWallsImage;

    /**
     * 显示网络图片的视图控件
     */
    private NetworkImageView imWallsImages;

    /**
     * 用于显示图片总数的提示控件
     */
    private TextView tvPhotoCount;

    /**
     * 红星总数视图
     */
    private TextView tvAgreeCount;

    /**
     * 点赞用户名显示列表
     */
//    private TextView tvLoveList;


    /**
     * 评论总数视图
     */
    private final TextView tvCommentCount;

    /**
     * 红心按钮点击添加或者取消赞
     */
    private ImageButton ibAgree;

    /**
     * 删除按钮，点击删除日志
     */
    private ImageButton btn_del;

    /**
     * 心情视图
     */
    private ImageView iv_mood;

    /**
     * 显示地址信息的布局
     */
    private LinearLayout llLocation;
    /**
     * 显示地图标，可点击跳转至地图
     */
    private ImageView ivLocation;
    /**
     * 显示地址名称，可点击跳转至地图
     */
    private TextView tvLocation;

    public final TextView getTvCommentCount() {
        return tvCommentCount;
    }

    public void setWallEntity(WallEntity wallEntity) {
        this.wallEntity = wallEntity;
    }

    public void setViewClickListener(WallViewClickListener mViewClickListener) {
        this.mViewClickListener = mViewClickListener;
    }

    /**
     * @param itemView  日志视图的整个UI
     * @param httpTools 网络上传工具
     * @param needFull  是否显示全部日志信息：{@value true} 显示全部日志信息；{@value false} 显示部份日志
     */
    public WallHolder(View itemView, HttpTools httpTools, boolean needFull) {
        // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
        super(itemView);
        mHttpTools = httpTools;

        nivHead = (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
        tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
        tvContent = (FreedomSelectionTextView) itemView.findViewById(R.id.tv_wall_content);
        tvDate = (TextView) itemView.findViewById(R.id.push_date);
        llWallsImage = itemView.findViewById(R.id.ll_walls_image);
        imWallsImages = (NetworkImageView) itemView.findViewById(R.id.iv_walls_images);
        tvPhotoCount = (TextView) itemView.findViewById(R.id.tv_wall_photo_count);
        tvAgreeCount = (TextView) itemView.findViewById(R.id.tv_wall_agree_count);
//        tvLoveList = (TextView) itemView.findViewById(R.id.tv_love_list);
        tvCommentCount = (TextView) itemView.findViewById(R.id.tv_wall_relay_count);
        ibAgree = (ImageButton) itemView.findViewById(R.id.iv_love);
        btn_del = (ImageButton) itemView.findViewById(R.id.btn_del);
        iv_mood = (ImageView) itemView.findViewById(R.id.iv_mood);
        llLocation = (LinearLayout) itemView.findViewById(R.id.ll_location);
        ivLocation = (ImageView) itemView.findViewById(R.id.iv_location);
        tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
        tvContent.setMaxLines(9);

        if(!needFull) {
            tvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 字符显示超过9行，只显示到第九行
                    int lineCount = tvContent.getLineCount();
                    if(lineCount > 8) {
                        // 第九行只显示十个字符
                        int maxLineEndIndex = tvContent.getLayout().getLineEnd(8);
                        int maxLineStartIndex = tvContent.getLayout().getLineStart(8);
                        String sourceText = tvContent.getText().toString();
                        if(maxLineEndIndex - maxLineStartIndex > 7) {
                            // 截取到第九行文字的第7个字符，其余字符用...替代
                            String newText = sourceText.substring(0, maxLineStartIndex + 7) + "...";
                            tvContent.setText(newText);
                        } else if(lineCount > 9) {
                            // 截取到第九行文字未满7个字符，行数超过9号，说明有换行，将换行替换成"..."
                            String newText = sourceText.substring(0, maxLineEndIndex - 1) + "...";
                            tvContent.setText(newText);
                        }

                    }
                }
            });
        }

        itemView.findViewById(R.id.top_event).setOnClickListener(this);
        itemView.findViewById(R.id.ll_comment).setOnClickListener(this);
        tvAgreeCount.setOnClickListener(this);
//        tvLoveList.setOnClickListener(this);
        ibAgree.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        imWallsImages.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.tv_wall_agree_count:
            case R.id.tv_love_list:
                if(Integer.valueOf(wallEntity.getLove_count()) > 0) {
                    if(mViewClickListener != null) {
                        mViewClickListener.showLovedMember(MainActivity.getUser().getUser_id(), wallEntity.getContent_id(), WallUtil.LOVE_MEMBER_WALL_TYPE);
                    }
                }
                break;

            case R.id.ll_love:
            case R.id.iv_love:
                updateLovedView();

                if(TextUtils.isEmpty(wallEntity.getLove_id())) {
                    doLove(wallEntity, false);
                } else {
                    doLove(wallEntity, true);
                }

                //判断是否已经有进行中的判断
                //                if(!runningList.contains(position)) {
                //                    runningList.add(position);
                //                    check(position);
                //                }
                break;
            case R.id.tv_wall_content:
            case R.id.ll_comment:
            case R.id.top_event:
                if(mViewClickListener != null) {
                    mViewClickListener.showComments(wallEntity.getContent_group_id(), wallEntity.getGroup_id());
                }
                break;
            case R.id.iv_walls_images:
                if(mViewClickListener != null) {
                    mViewClickListener.showOriginalPic(wallEntity.getContent_id());
                }
                break;
            case R.id.btn_del:
                if(mViewClickListener != null) {
                    mViewClickListener.remove(wallEntity.getContent_group_id());
                }
                break;
        }
    }

    /**
     * 更新点赞相关视图
     */
    private void updateLovedView() {
        int count = Integer.valueOf(wallEntity.getLove_count());
//        String text = tvLoveList.getText().toString();
//        String name = MainActivity.getUser().getUser_given_name();
        int resId;

        if(TextUtils.isEmpty(wallEntity.getLove_id())) {
            count += 1;
            resId = R.drawable.love_press;
            wallEntity.setLove_id(MainActivity.getUser().getUser_id());

//            if(count > 1) {
//                text += (name + " ");
//            } else {
//                text = name;
//            }
        } else {
            count -= 1;
            resId = R.drawable.love_normal;
            wallEntity.setLove_id(null);

//            if(count > 0) {
//                StringBuilder temp = new StringBuilder();
//                String split;
//                split = name + " ";
//
//                for(String str : text.split(split)) {
//                    temp.append(str);
//                }
//                text = temp.toString();
//            } else {
//                text = "";
//            }
        }

        wallEntity.setLove_count(String.valueOf(count));
        ibAgree.setImageResource(resId);
        tvAgreeCount.setText(String.format(tvAgreeCount.getContext().getString(R.string.loves_count), count));
//        tvLoveList.setText(text);
    }

    private void doLove(final WallEntity wallEntity, final boolean love) {

        HashMap<String, String> params = new HashMap<>();
        params.put("content_id", wallEntity.getContent_id());
        params.put("love", love ? "1" : "0");// 0-取消，1-赞
        params.put("user_id", "" + MainActivity.getUser().getUser_id());

        RequestInfo requestInfo = new RequestInfo(Constant.API_WALL_LOVE, params);

        mHttpTools.post(requestInfo, POST_LOVE, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {

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

    public void setContent(WallEntity wallEntity, final Context context) {
        this.wallEntity = wallEntity;
        VolleyUtil.initNetworkImageView(context, nivHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, wallEntity.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);

        String atDescription = this.wallEntity.getText_description();
        if(TextUtils.isEmpty(atDescription)) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(atDescription);
        }
        LogUtil.i(TAG, "onBindViewHolder& description: " + atDescription);

        int tagMemberCount = this.wallEntity.getTag_member() == null ? 0 : this.wallEntity.getTag_member().size();
        int tagGroupCount = this.wallEntity.getTag_member() == null ? 0 : this.wallEntity.getTag_group().size();
        if(tagMemberCount >= 0 || tagGroupCount >= 0) {
            // 有TAG用户或分组需要显示字符特效
            WallUtil util = new WallUtil(context, mViewClickListener);
            util.setSpanContent(tvContent, this.wallEntity, atDescription, tagMemberCount, tagGroupCount);
        } else {
            tvContent.setOnClickListener(this);
        }

        // 显示发表的时间
        tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(context, this.wallEntity.getContent_creation_date()));
        // 用户名
        tvUserName.setText(this.wallEntity.getUser_given_name());

        // file_id 为空表示没有发表图片，有则需要显示图片
        if(TextUtils.isEmpty(this.wallEntity.getFile_id())) {
            llWallsImage.setVisibility(View.GONE);
        } else {
            llWallsImage.setVisibility(View.VISIBLE);

            // 有图片显示图片总数
            int count = Integer.valueOf(this.wallEntity.getPhoto_count());
            if(count > 1) {
                String photoCountStr;
                photoCountStr = count + " " + context.getString(R.string.text_photos);
                tvPhotoCount.setText(photoCountStr);
                tvPhotoCount.setVisibility(View.VISIBLE);
            } else {
                tvPhotoCount.setVisibility(View.GONE);
            }

            VolleyUtil.initNetworkImageView(context, imWallsImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, this.wallEntity.getUser_id(), this.wallEntity.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        }
         /*is owner wall*/
        //        if (!TextUtils.isEmpty(wall.getUser_id())&&wall.getUser_id().equals("49")) {
        //            ibDelete.setVisibility(View.VISIBLE);
        //        } else {
        //            ibDelete.setVisibility(View.GONE);
        //        }

        try {
            if(this.wallEntity.getDofeel_code() != null) {
                StringBuilder b = new StringBuilder(this.wallEntity.getDofeel_code());
                int charIndex = this.wallEntity.getDofeel_code().lastIndexOf("_");
                b.replace(charIndex, charIndex + 1, "/");

                InputStream is = context.getAssets().open(b.toString());
                iv_mood.setImageBitmap(BitmapFactory.decodeStream(is));
            } else {
                iv_mood.setVisibility(View.GONE);
            }
        } catch(Exception e) {
            iv_mood.setVisibility(View.GONE);
        }

        /*location*/
        //        if (TextUtils.isEmpty(wall.getLoc_name())) {
        //            at.setVisibility(View.GONE);
        //        } else {
        //            at.setVisibility(View.VISIBLE);
        //            tvLocation.setText(wall.getLoc_name());
        //        }
//        int count = Integer.valueOf(this.wallEntity.getLove_count());
        tvAgreeCount.setText(String.format(tvAgreeCount.getContext().getString(R.string.loves_count), Integer.valueOf(this.wallEntity.getLove_count())));
//        if(count > 0) {
//            WallUtil.getLoveList(mHttpTools, tvLoveList, MainActivity.getUser().getUser_id(), wallEntity.getContent_id(), WallUtil.LOVE_MEMBER_WALL_TYPE);
//        } else {
//            tvLoveList.setText("");
//        }

        tvCommentCount.setText(this.wallEntity.getComment_count());


        if(MainActivity.getUser().getUser_id().equals(this.wallEntity.getUser_id())) {
            btn_del.setVisibility(View.VISIBLE);
        } else {
            btn_del.setVisibility(View.GONE);
        }

        if(TextUtils.isEmpty(this.wallEntity.getLove_id())) {
            ibAgree.setImageResource(R.drawable.love_normal);
        } else {
            ibAgree.setImageResource(R.drawable.love_press);
        }

        String locationName = this.wallEntity.getLoc_name();
        if(TextUtils.isEmpty(locationName) || TextUtils.isEmpty(this.wallEntity.getLoc_latitude()) || TextUtils.isEmpty(this.wallEntity.getLoc_longitude())) {
            llLocation.setVisibility(View.GONE);
        } else {
            llLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(locationName);
            llLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(context, WallHolder.this.wallEntity);
                }
            });
            tvLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(context, WallHolder.this.wallEntity);
                }
            });
            ivLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLocationSetting(context, WallHolder.this.wallEntity);
                }
            });
        }
    }

    /**
     * 跳至地图
     *
     * @param context 资源
     * @param wall    {@link WallEntity}
     */
    private void gotoLocationSetting(Context context, WallEntity wall) {
        if(TextUtils.isEmpty(wall.getLoc_latitude()) || TextUtils.isEmpty(wall.getLoc_longitude())) {
            return;
        }

        LocationUtil.goNavigation(context, Double.valueOf(wall.getLoc_latitude()), Double.valueOf(wall.getLoc_longitude()), wall.getLoc_type());
    }
}
