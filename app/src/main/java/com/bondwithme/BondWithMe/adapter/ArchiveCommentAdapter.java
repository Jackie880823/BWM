package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.ArchiveChatEntity;
import com.bondwithme.BondWithMe.entity.ArchiveCommentEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.interfaces.ArchiveChatViewClickListener;
import com.bondwithme.BondWithMe.util.MyDateUtils;

import java.util.List;

/**
 * Created by liangzemian on 15/7/2.
 */
public class ArchiveCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context mContext;
    private List<ArchiveCommentEntity> data;
    private List<ArchiveChatEntity> detailData;
    private int detailItemCount;

    public ArchiveCommentAdapter(Context context,List<ArchiveCommentEntity> data,List<ArchiveChatEntity> detailData){
        mContext = context;
        this.detailData = detailData;
        this.data = data;
        if(detailData != null && detailData.size()>0){
            detailItemCount = 1;
        }
    }

    public void addData(List<ArchiveCommentEntity> newData){
        data.addAll(newData);
        notifyItemInserted(getItemCount());
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_HEADER:
                view = LayoutInflater.from(mContext).inflate(R.layout.fragment_archive_comment_head,parent,false);
                viewHolder = new VHHeader(view);
//                return new VHHeader(view);
                break;
            case TYPE_ITEM:
                view = LayoutInflater.from(mContext).inflate(R.layout.archive_comment_item,parent,false);
                viewHolder = new VHItem(view);
//                return new VHItem(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position == 0){
            VHHeader item = (VHHeader) holder;
            ArchiveChatEntity archive = detailData.get(0);
            if(TextUtils.isEmpty(archive.getFile_id())){
                item.llArchiveImage.setVisibility(View.GONE);
            }else {
                //如果有照片
                item.llArchiveImage.setVisibility(View.VISIBLE);
                //照片的总数
                int PhotoCount = Integer.valueOf(archive.getPhoto_count());
                if(PhotoCount > 1){
                    String photoCountStr;
                    photoCountStr = PhotoCount + " " + mContext.getString(R.string.text_photos);
                    item.tvPhotoCount.setText(photoCountStr);
                    item.tvPhotoCount.setVisibility(View.VISIBLE);
                }else {
                    item.tvPhotoCount.setVisibility(View.GONE);
                }
                //加载照片
                VolleyUtil.initNetworkImageView(mContext, item.imArchiveImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, archive.getUser_id(), archive.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
            }
            item.tvUserName.setText(archive.getGroup_name());

            String Content = archive.getText_description();
            if(TextUtils.isEmpty(Content)){
//            holder.liContent.setVisibility(View.VISIBLE);
                item.liContent.setVisibility(View.GONE);
            }else {
                item.tvContent.setText(Content);
                item.liContent.setVisibility(View.VISIBLE);
            }
            item.tvLoveCount.setText(archive.getLove_count());
            item.tvCommentCount.setText(archive.getComment_count());
//        String tempdate = MyDateUtils.getLocalDateStringFromUTC(mContext, archive.getContent_creation_date());
            item.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, archive.getContent_creation_date()));//时间有问题
        }else {
            VHItem item = (VHItem) holder;
            ArchiveCommentEntity commentEntity = data.get(position-detailItemCount);
            item.tvUserName.setText(commentEntity.getUser_given_name());
            item.tvContent.setText(commentEntity.getComment_content());
            item.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, commentEntity.getComment_creation_date()));
        }
    }


    @Override
    public int getItemCount() {
        return data.size() + detailItemCount;
    }

    class VHHeader extends RecyclerView.ViewHolder implements View.OnClickListener{
        /**
         * 用户昵称视图
         */
        TextView tvUserName;
        /**
         * 用于显示图片总数的提示控件
         */
        TextView tvPhotoCount;
        /**
         * 发表日期显示视图
         */
        TextView tvDate;
        /**
         * 评论总数视图
         */
        TextView tvCommentCount;
        /**
         * 喜欢的总数视图
         */
        TextView tvLoveCount;
        /**
         * 内容视图
         */
        TextView tvContent;

        View liContent;

        View llArchiveImage;

        /**
         * 显示网络图片的视图控件
         */
        NetworkImageView imArchiveImages;
        public VHHeader(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
            tvDate = (TextView) itemView.findViewById(R.id.up_time);
            tvPhotoCount = (TextView) itemView.findViewById(R.id.tv_chat_photo_count);
            llArchiveImage = itemView.findViewById(R.id.ll_chats_image);
            tvCommentCount = (TextView) itemView.findViewById(R.id.member_comment);
            tvLoveCount = (TextView) itemView.findViewById(R.id.memeber_love);
            liContent = itemView.findViewById(R.id.li_content);
            tvContent = (TextView) itemView.findViewById(R.id.tv_archive_content);
            tvContent.setMaxLines(9);
            imArchiveImages = (NetworkImageView) itemView.findViewById(R.id.iv_chats_images);
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

            imArchiveImages.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            ArchiveChatEntity archive = detailData.get(0);
            switch (v.getId()){
                case R.id.iv_chats_images:
                    if(marchiveChatViewClickListener != null){
                        marchiveChatViewClickListener.showOriginalPic(archive.getContent_id());
                    }
            }
        }
    }


    class VHItem extends RecyclerView.ViewHolder {
        /**
         * 用户昵称视图
         */
        TextView tvUserName;
        /**
         * 内容视图
         */
        TextView tvContent;
        /**
         * 发表日期显示视图
         */
        TextView tvDate;


        public VHItem(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
            tvContent = (TextView) itemView.findViewById(R.id.tv_archive_content);
            tvDate = (TextView) itemView.findViewById(R.id.up_time);
            tvContent.setMaxLines(9);
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
    }

    public ArchiveChatViewClickListener marchiveChatViewClickListener;
    public void setPicClickListener(ArchiveChatViewClickListener archiveChatViewClickListener){
        marchiveChatViewClickListener = archiveChatViewClickListener;
    }
}
