package com.bondwithme.BondWithMe.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.EventCommentEntity;
import com.bondwithme.BondWithMe.entity.EventEntity;
import com.bondwithme.BondWithMe.exception.StickerTypeException;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.wall.WallMembersOrGroupsActivity;
import com.bondwithme.BondWithMe.util.DensityUtil;
import com.bondwithme.BondWithMe.util.LocationUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.SDKUtil;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.util.WallUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.FreedomSelectionTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class EventCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<EventCommentEntity> data;
    private EventEntity detailData;
    private int detailItemCount;
    RecyclerView recyclerView;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_SECOND = 2;
    private static final int TYPE_FOOTER = 3;

    private int itemDistance;

    public EventCommentAdapter(Context context, EventEntity detailData, List<EventCommentEntity> data, RecyclerView recyclerView) {
        mContext = context;
        this.detailData = detailData;
        this.data = data;
        this.recyclerView = recyclerView;
//        if(detailData != null){
//            detailItemCount = 1;
//        }
        detailItemCount = 1;

    }

    //添加数据到实体
    public void addData(List<EventCommentEntity> newdData) {
        data.addAll(newdData);
        notifyDataSetChanged();
    }

    public void removeCommentData() {
        data.clear();
        notifyDataSetChanged();
    }

    public void alterHeader(EventEntity detailData) {
        this.detailData = detailData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
//        Log.i("getItemViewType=====",position+"");
//        Log.i("getItemViewDagt=====",data.size()+"");

        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == 1) {
            return TYPE_SECOND;
        } else if (position < data.size() && position >= 2) {
            return TYPE_ITEM;
        } else if (position == data.size()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    private void updateItem(EventCommentEntity q, int pos) {
        data.remove(pos);
        notifyItemRemoved(pos);
        addItem(q);
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(EventCommentEntity q) {
        data.add(q);
        notifyItemInserted(data.size() - 1);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 加载Item的布局.布局中用到的真正的CardView.
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
//        Log.i("onCreateViewHolder=====",viewType+"");
        switch (viewType) {
            case TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event_detail_head, parent, false);
                viewHolder = new VHHeader(view);
                break;
            case TYPE_SECOND:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_head_item, parent, false);
                viewHolder = new mViewHolder(view);
                break;
            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_item, parent, false);
                viewHolder = new mViewHolder(view);
                break;
            case TYPE_FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_comment_footer_item, parent, false);
                viewHolder = new mViewHolder(view);
                break;
        }
        return viewHolder;

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        Log.i("onBindViewHolder=====",position+"");
        LinearLayout.LayoutParams layoutParam;
        if (position == 0) {
            VHHeader header = (VHHeader) holder;
            EventEntity detail = detailData;

            if (detailData == null) {
                return;
            }
            setDetail(header, detail);
//            Log.i("getGroup_owner_id()", detail.getGroup_owner_id()+"");
            BitmapTools.getInstance(mContext).display(((VHHeader) holder).ownerHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, detail.getGroup_owner_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
            header.pushDate.setText(MyDateUtils.getEventLocalDateStringFromUTC(mContext, detail.getGroup_creation_date()));
            header.eventTitle.setText(detail.getGroup_name());
            header.tvUserName.setText(detail.getUser_given_name());
            header.tvContent.setText(detail.getText_description());
            header.tvDate.setText(MyDateUtils.getEventLocalDateStringFromUTC(mContext, detail.getGroup_event_date()));
            header.TvLocation.setText(detail.getLoc_name());
            header.tvMaybe.setText(detail.getTotal_maybe());
            header.tvNotGoing.setText(detail.getTotal_no());
            if (MainActivity.getUser().getUser_id().equals(detail.getGroup_owner_id())) {
                try {
                    header.tvGoing.setText((Integer.valueOf(detail.getTotal_yes()) - 1) + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                header.tvGoing.setText(detail.getTotal_yes());
            }
            if (!TextUtils.isEmpty(detail.getLoc_latitude()) && !TextUtils.isEmpty(detail.getLoc_longitude())) {
                header.eventMapCation.setVisibility(View.VISIBLE);
                BitmapTools.getInstance(mContext).display(header.eventMapCation, LocationUtil.getLocationPicUrl(mContext, detail.getLoc_latitude(), detail.getLoc_longitude(), detail.getLoc_type()), R.drawable.network_image_default, R.drawable.network_image_default);


            } else {
                header.eventMapCation.setVisibility(View.GONE);
            }

        }
        if (position == detailItemCount) {
            mViewHolder second = (mViewHolder) holder;

            if (updateListener != null) {
                updateListener.updateHeadView(second.itemView);
            }

            if (data.isEmpty()) {
                second.itemView.findViewById(R.id.comment_content_ll).setVisibility(View.GONE);
                second.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.event_detail_one_shape));
                return;
            } else {
                second.itemView.findViewById(R.id.comment_content_ll).setVisibility(View.VISIBLE);
            }

            EventCommentEntity entity = data.get(0);
            if (SDKUtil.IS_L) {
                itemDistance = DensityUtil.dip2px(mContext, 2);
            } else {
                itemDistance = DensityUtil.dip2px(mContext, 0);
            }

            if (data.size() == 1) {
                second.itemView.findViewById(R.id.comment_content_ll).setVisibility(View.VISIBLE);
                second.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.event_detail_one_shape));
                layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParam.setMargins(itemDistance, 14, itemDistance, DensityUtil.dip2px(mContext, 55));
//              layoutParam.setMarginEnd(500);
                second.itemView.setLayoutParams(layoutParam);

                second.line.setVisibility(View.INVISIBLE);
            } else {
                second.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.event_detail_shape));
                layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParam.setMargins(itemDistance, 14, itemDistance, DensityUtil.dip2px(mContext, 0));
//              layoutParam.setMarginEnd(500);
                second.itemView.setLayoutParams(layoutParam);

                second.line.setVisibility(View.VISIBLE);
            }

            BitmapTools.getInstance(mContext).display(((mViewHolder) holder).civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, entity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);

            second.tv_comment_owner_name.setText(entity.getUser_given_name());
            second.comment_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, entity.getComment_creation_date()));
            if (!TextUtils.isEmpty(entity.getComment_content().trim())) {//如果文字不为空
                second.tv_comment_content.setVisibility(View.VISIBLE);
                second.tv_comment_content.setText(entity.getComment_content());
                second.chatsImage.setVisibility(View.GONE);
            } else {
                second.tv_comment_content.setVisibility(View.GONE);
                setCommentPic(second.gifImageView, second.networkImageView, second.chatsImage, entity);
            }

            if (updateListener != null) {
                updateListener.updateListSecondView(second.itemView);
            }

            setComment(second.btn_comment_del, second.iv_agree, second.tv_agree_count, position, entity);
        } else if (position > detailItemCount && position < data.size()) {
            mViewHolder vhItem = (mViewHolder) holder;
            EventCommentEntity entity = data.get(position - detailItemCount);

            layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParam.setMargins(itemDistance, 0, itemDistance, DensityUtil.dip2px(mContext, 0));
            vhItem.itemView.setLayoutParams(layoutParam);

            BitmapTools.getInstance(mContext).display(((mViewHolder) holder).civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, entity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
            vhItem.tv_comment_owner_name.setText(entity.getUser_given_name());
            vhItem.comment_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, entity.getComment_creation_date()));

            if (!TextUtils.isEmpty(entity.getComment_content().trim())) {//如果文字不为空
//                Log.i("文字=====", position + "");
                vhItem.tv_comment_content.setVisibility(View.VISIBLE);
                vhItem.tv_comment_content.setText(entity.getComment_content());
                vhItem.chatsImage.setVisibility(View.GONE);

            } else {
                vhItem.tv_comment_content.setVisibility(View.GONE);
                setCommentPic(vhItem.gifImageView, vhItem.networkImageView, vhItem.chatsImage, entity);
            }

            setComment(vhItem.btn_comment_del, vhItem.iv_agree, vhItem.tv_agree_count, position, entity);
        } else if (
                position > detailItemCount && position == data.size()) {
            mViewHolder footer = (mViewHolder) holder;
            EventCommentEntity entity = data.get(position - detailItemCount);

            layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParam.setMargins(itemDistance, 0, itemDistance, DensityUtil.dip2px(mContext, 55));
            footer.itemView.setLayoutParams(layoutParam);
            footer.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.event_detail_footer_shape));

            BitmapTools.getInstance(mContext).display(((mViewHolder) holder).civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, entity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);

            footer.line.setVisibility(View.INVISIBLE);
            footer.tv_comment_owner_name.setText(entity.getUser_given_name());
            footer.comment_date.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, entity.getComment_creation_date()));

            if (!TextUtils.isEmpty(entity.getComment_content().trim())) {//如果文字不为空
//                Log.i("文字=====", position + "");
                footer.tv_comment_content.setVisibility(View.VISIBLE);
                footer.tv_comment_content.setText(entity.getComment_content());
                footer.chatsImage.setVisibility(View.GONE);

            } else {
                footer.tv_comment_content.setVisibility(View.GONE);
                setCommentPic(footer.gifImageView, footer.networkImageView, footer.chatsImage, entity);
            }

            setComment(footer.btn_comment_del, footer.iv_agree, footer.tv_agree_count, position, entity);
        }


    }

    private void setDetail(RecyclerView.ViewHolder holder, EventEntity entity) {

    }

    private void setComment(ImageButton btn_comment_del, ImageButton iv_agree, TextView tv_agree_count, int position, EventCommentEntity entity) {

        if (MainActivity.getUser().getUser_id().equals(entity.getUser_id())) {//如果是自己发送到评论，则显示删除按钮，否则隐藏
            btn_comment_del.setVisibility(View.VISIBLE);
        } else {
            btn_comment_del.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(entity.getLove_id())) {//如果有人点赞
            iv_agree.setImageResource(R.drawable.love_normal);
        } else {
            iv_agree.setImageResource(R.drawable.love_press);
        }
        if (lovedate != null && lovedate.containsKey(position)) {
            tv_agree_count.setText(lovedate.get(position));
        } else {
            tv_agree_count.setText((TextUtils.isEmpty(entity.getLove_count()) ? "0" : entity.getLove_count()));
        }


//        setCommentPic(holder,entity);
    }

    private void setCommentPic(GifImageView gifImageView, NetworkImageView networkImageView, View chatsImage, EventCommentEntity comment) {
//        Log.i(TAG, "setCommentPic& file_id: " + comment.getFile_id() + "; StickerName is " + comment.getSticker_name());
        if (!TextUtils.isEmpty(comment.getFile_id())) {
            chatsImage.setVisibility(View.VISIBLE);
            networkImageView.setVisibility(View.VISIBLE);
            gifImageView.setVisibility(View.GONE);
            BitmapTools.getInstance(mContext).display(networkImageView, String.format(Constant.API_GET_COMMENT_PIC, Constant.Module_preview_m, comment.getUser_id(), comment.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        } else if (!TextUtils.isEmpty(comment.getSticker_group_path())) {
            chatsImage.setVisibility(View.VISIBLE);
            gifImageView.setVisibility(View.VISIBLE);
            networkImageView.setVisibility(View.GONE);
            gifImageView.setImageDrawable(null);
            try {
                UniversalImageLoaderUtil.decodeStickerPic(gifImageView, comment.getSticker_group_path(), comment.getSticker_name(), comment.getSticker_type());
            } catch (StickerTypeException e) {
                e.printStackTrace();
            }
        } else {
            chatsImage.setVisibility(View.GONE);
        }


    }

    /**
     * 获取图片的byte数组
     *
     * @param urlPath
     * @return
     */
    private static byte[] getImageByte(String urlPath) {
        InputStream in = null;
        byte[] result = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection httpURLconnection = (HttpURLConnection) url
                    .openConnection();
            httpURLconnection.setDoInput(true);
            httpURLconnection.connect();
            if (httpURLconnection.getResponseCode() == 200) {
                in = httpURLconnection.getInputStream();
                result = readInputStream(in);
                in.close();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 将输入流转为byte数组
     *
     * @param in
     * @return
     * @throws Exception
     */
    private static byte[] readInputStream(InputStream in) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        baos.close();
        in.close();
        return baos.toByteArray();

    }


    @Override
    public int getItemCount() {
        if (data.isEmpty()) {
            return detailItemCount + 1;
        } else {
            return data.size() + detailItemCount;
        }
    }


    class VHHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * 提交时间
         */
        private TextView pushDate;
        /**
         * 用户昵称
         */
        private TextView tvUserName;
        /**
         * 用户头像
         */
        private CircularNetworkImage ownerHead;
        /**
         * event头目
         */
        private TextView eventTitle;
        /**
         * event地图
         */
        private NetworkImageView eventMapCation;
        /**
         * event内容
         */
        private FreedomSelectionTextView tvContent;
        /**
         * 日历
         */
        private TextView tvDate;
        /**
         * 地址
         */
        private TextView TvLocation;
        /**
         * 被邀请好友视图
         */
        private RelativeLayout intentAll;
        /**
         * 参加
         */
        private LinearLayout intentAgree;
        /**
         * 不确定
         */
        private LinearLayout intentMaybe;
        /**
         * 拒绝
         */
        private LinearLayout intentNo;

        private TextView tvGoing;
        private TextView tvMaybe;
        private TextView tvNotGoing;
        private View intenAll;
//        private View defaultComment;

        public VHHeader(View itemView) {
            super(itemView);
            pushDate = (TextView) itemView.findViewById(R.id.push_date);
            tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
            ownerHead = (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventMapCation = (NetworkImageView) itemView.findViewById(R.id.event_picture_4_location);
            tvContent = (FreedomSelectionTextView) itemView.findViewById(R.id.event_desc);
            tvDate = (TextView) itemView.findViewById(R.id.event_date);
            TvLocation = (TextView) itemView.findViewById(R.id.location_desc);
            intentAll = (RelativeLayout) itemView.findViewById(R.id.btn_intent_all);
            intentAgree = (LinearLayout) itemView.findViewById(R.id.iv_intent_agree);
            intentMaybe = (LinearLayout) itemView.findViewById(R.id.iv_intent_maybe);
            intentNo = (LinearLayout) itemView.findViewById(R.id.iv_intent_no);
            tvGoing = (TextView) itemView.findViewById(R.id.going_count);
            tvMaybe = (TextView) itemView.findViewById(R.id.maybe_count);
            tvNotGoing = (TextView) itemView.findViewById(R.id.not_going_count);
            intenAll = itemView.findViewById(R.id.btn_intent_all);
//            defaultComment = itemView.findViewById(R.id.default_comment);
            intenAll.setOnClickListener(this);
            intentAgree.setOnClickListener(this);
            intentMaybe.setOnClickListener(this);
            intentNo.setOnClickListener(this);
            eventMapCation.setOnClickListener(this);
            intentAgree.setOnClickListener(this);
            intentMaybe.setOnClickListener(this);
            intentNo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_intent_all:
                    if (mCommentActionListener != null) {
                        mCommentActionListener.setIntentAll(detailData, 0);
                    }
                    break;
                case R.id.event_picture_4_location:
                    if (TextUtils.isEmpty(detailData.getLoc_latitude()) || TextUtils.isEmpty(detailData.getLoc_longitude())) {
                        return;
                    }
                    LocationUtil.goNavigation(mContext, Double.valueOf(detailData.getLoc_latitude()), Double.valueOf(detailData.getLoc_longitude()), detailData.getLoc_type());
                    break;
                case R.id.iv_intent_agree:
                    if (mCommentActionListener != null) {
                        mCommentActionListener.setIntentAll(detailData, 1);
                    }
                    break;
                case R.id.iv_intent_maybe:
                    if (mCommentActionListener != null) {
                        mCommentActionListener.setIntentAll(detailData, 2);
                    }
                    break;
                case R.id.iv_intent_no:
                    if (mCommentActionListener != null) {
                        mCommentActionListener.setIntentAll(detailData, 3);
                    }
                    break;
            }
        }
    }

    class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * 用户头像视图
         */
        CircularNetworkImage civ_comment_owner_head;
        /**
         * 用户昵称视图
         */
        TextView tv_comment_owner_name;
        /**
         *
         */
        FreedomSelectionTextView tv_comment_content;
        /**
         *
         */
        TextView tv_agree_count;
        TextView tv_like_desc;
        ImageButton iv_agree;
        ImageButton btn_comment_del;
        TextView comment_date;
        NetworkImageView networkImageView;
        GifImageView gifImageView;
        ImageView pngImageView;
        //        CircularProgress progressBar;
        View agreeTouch;
        private View chatsImage;
        private View line;

        public mViewHolder(View itemView) {
            super(itemView);
            gifImageView = (GifImageView) itemView.findViewById(R.id.message_pic_gif_iv);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.message_pic_iv);
            pngImageView = (ImageView) itemView.findViewById(R.id.message_png_iv);

            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
            tv_comment_content = (FreedomSelectionTextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
            agreeTouch = itemView.findViewById(R.id.rl_agree);
            btn_comment_del = (ImageButton) itemView.findViewById(R.id.btn_comment_del);
//            progressBar = (CircularProgress) itemView.findViewById(R.id.message_progress_bar);
            chatsImage = itemView.findViewById(R.id.ll_chats_image);
            tv_like_desc = (TextView) itemView.findViewById(R.id.tv_like_desc);
            tv_like_desc.setOnClickListener(this);
            iv_agree.setOnClickListener(this);
            line = itemView.findViewById(R.id.line);
            agreeTouch.setOnClickListener(this);
            btn_comment_del.setOnClickListener(this);
            chatsImage.setOnClickListener(this);
            networkImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            int commentPosition = position - detailItemCount;
            if (commentPosition <= 0 && commentPosition >= data.size()) return;

            EventCommentEntity commentEntity = data.get(commentPosition);
            switch (v.getId()) {
                case R.id.btn_comment_del:
                    if (mCommentActionListener != null) {
                        if (MainActivity.getUser().getUser_id().equals(commentEntity.getUser_id())) {
                            mCommentActionListener.doDelete(commentEntity.getComment_id());
                        }
                    }
                    break;
                case R.id.message_pic_iv:
                    if (mCommentActionListener != null) {
                        mCommentActionListener.showOriginalPic(commentEntity.getUser_id(), commentEntity.getFile_id());
                    }
                    break;
                case R.id.iv_agree:
                    newClick = true;
                    int count = Integer.valueOf(tv_agree_count.getText().toString());
                    if (TextUtils.isEmpty(commentEntity.getLove_id())) {
                        iv_agree.setImageResource(R.drawable.love_press);
                        commentEntity.setLove_id(MainActivity.getUser().getUser_id());
                        tv_agree_count.setText(count + 1 + "");
                    } else {
                        iv_agree.setImageResource(R.drawable.love_normal);
                        commentEntity.setLove_id(null);
                        tv_agree_count.setText(count - 1 + "");
                    }
                    lovedate.put(position, tv_agree_count.getText().toString());
                    //判断是否已经有进行中的判断
                    if (!runningList.contains(commentPosition)) {
                        runningList.add(commentPosition);
                        clickList.add(commentPosition);
                        check(commentPosition);
                    }
                    break;
                case R.id.tv_like_desc:
                    if (Integer.valueOf(tv_agree_count.getText().toString()) > 0) {
                        Intent intent = new Intent(mContext, WallMembersOrGroupsActivity.class);
                        intent.setAction(Constant.ACTION_SHOW_LOVED_USER);
                        intent.putExtra(WallUtil.GET_LOVE_LIST_VIEWER_ID, MainActivity.getUser().getUser_id());
                        intent.putExtra(WallUtil.GET_LOVE_LIST_REFER_ID, commentEntity.getComment_id());
                        intent.putExtra(WallUtil.GET_LOVE_LIST_TYPE, WallUtil.LOVE_MEMBER_COMMENT_TYPE);
                        mContext.startActivity(intent);
                    }

                    break;
            }

        }
    }


    boolean newClick;
    List<Integer> runningList = new ArrayList<Integer>();
    List<Integer> clickList = new ArrayList<Integer>();
    HashMap<Integer, String> lovedate = new HashMap<Integer, String>();

    private void check(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();//点击时间
                long nowTime = System.currentTimeMillis();
                //缓冲时间为1000
                while (nowTime - startTime < 1000) {
                    if (newClick) {
                        startTime = System.currentTimeMillis();
                        newClick = false;
                    }
                    nowTime = System.currentTimeMillis();
                }
                try {
                    runningList.remove(position);
                } catch (Exception e) {
                }
                final EventCommentEntity commentEntity = data.get(position);
                if (mCommentActionListener != null) {
                    if (TextUtils.isEmpty(commentEntity.getLove_id())) {
                        mCommentActionListener.doLove(commentEntity, false);
                    } else {
                        mCommentActionListener.doLove(commentEntity, true);
                    }
                }

            }
        }).start();
    }

    private CommentActionListener mCommentActionListener;

    public void setCommentActionListener(CommentActionListener commentActionListener) {
        mCommentActionListener = commentActionListener;
    }

    public interface CommentActionListener {
        void doLove(final EventCommentEntity commentEntity, final boolean love);

        void doDelete(String commentId);

        void showOriginalPic(String User_id, String File_id);

        void setIntentAll(EventEntity entity, int member);
    }

    private ListViewItemViewUpdateListener updateListener;

    public void setUpdateListener(ListViewItemViewUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public interface ListViewItemViewUpdateListener {
        void updateHeadView(View headView);

        void updateListSecondView(View listHeadView);
    }

}