package com.madxstudio.co8.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.ArchiveChatEntity;

import java.util.List;

/**
 * Created by liangzemian on 15/7/2.
 */
public class ArchiveDetailAdapter extends RecyclerView.Adapter<ArchiveDetailAdapter.VHItem> {

    private Context mContext;
    private List<ArchiveChatEntity> data;

    public ArchiveDetailAdapter(Context context, List<ArchiveChatEntity> data){
        mContext = context;
        this.data = data;
    }

    public void addData(List<ArchiveChatEntity> newData){
        data.addAll(newData);
        notifyDataSetChanged();
    }
    @Override
    public ArchiveDetailAdapter.VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.archive_comment_item,parent,false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(VHItem holder, int position) {
        final ArchiveChatEntity archiveChatEntity = data.get(position);
//        holder.tvUserName.setText(archiveCommentEntity.getUser_given_name());
//        holder.tvContent.setText(archiveCommentEntity.getComment_content());
//        holder.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, archiveCommentEntity.getComment_creation_date()));
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

        @Override
        public void onClick(View v) {

        }
    }
}
