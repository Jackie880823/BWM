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
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.interfaces.ArchiveChatViewClickListener;
import com.bondwithme.BondWithMe.util.MyDateUtils;

import java.util.List;


/**
 * Created by liangzemian on 15/7/2.
 */
public class ArchiveChatAdapter extends RecyclerView.Adapter<ArchiveChatAdapter.VHItem>{
    private Context mContext;
    private List<ArchiveChatEntity> data;

    public ArchiveChatAdapter(Context context,List<ArchiveChatEntity> data){
        mContext = context;
        this.data = data;
    }

    public void add(List<ArchiveChatEntity> newData){
        data.addAll(newData);
        notifyItemInserted(data.size());
    }
    @Override
    public ArchiveChatAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.archivechat_item, parent, false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(ArchiveChatAdapter.VHItem holder, int position) {
        final ArchiveChatEntity archive = data.get(position);
        if(TextUtils.isEmpty(archive.getFile_id())){
            holder.imArchiveImages.setVisibility(View.GONE);
        }else {
            //如果有照片
            holder.imArchiveImages.setVisibility(View.VISIBLE);
            //照片的总数
            int PhotoCount = Integer.valueOf(archive.getPhoto_count());
            if(PhotoCount > 1){
                String photoCountStr;
                photoCountStr = PhotoCount + " " + mContext.getString(R.string.text_photos);
                holder.tvPhotoCount.setText(photoCountStr);
                holder.tvPhotoCount.setVisibility(View.VISIBLE);
            }else {
                holder.tvPhotoCount.setVisibility(View.GONE);
            }
            //加载照片
            VolleyUtil.initNetworkImageView(mContext, holder.imArchiveImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, archive.getUser_id(), archive.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);

        }
        holder.tvUserName.setText(archive.getGroup_name());
        holder.tvContent.setText(archive.getText_description());
        holder.tvLoveCount.setText(archive.getLove_count());
        holder.tvCommentCount.setText(archive.getComment_count());
        holder.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext,archive.getContent_creation_timestamp()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        View llWallsImage;

        /**
         * 显示网络图片的视图控件
         */
        NetworkImageView imArchiveImages;

        public VHItem(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
            tvDate = (TextView) itemView.findViewById(R.id.push_date);
            tvPhotoCount = (TextView) itemView.findViewById(R.id.tv_chat_photo_count);
            llWallsImage = itemView.findViewById(R.id.ll_chats_image);
            tvCommentCount = (TextView) itemView.findViewById(R.id.member_comment);
            tvLoveCount = (TextView) itemView.findViewById(R.id.memeber_love);
            tvContent = (TextView) itemView.findViewById(R.id.tv_archive_content);
            tvContent.setMaxLines(9);
            imArchiveImages = (NetworkImageView) imArchiveImages.findViewById(R.id.iv_chats_images);
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

        }
    }

    public ArchiveChatViewClickListener marchiveChatViewClickListener;
    public void setPicClickListener(ArchiveChatViewClickListener archiveChatViewClickListener){
        marchiveChatViewClickListener = archiveChatViewClickListener;
    }
}
