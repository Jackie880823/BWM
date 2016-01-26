package com.bondwithme.BondWithMe.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.entity.WallCommentEntity;
import com.bondwithme.BondWithMe.exception.StickerTypeException;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.ViewOriginalPicesActivity;
import com.bondwithme.BondWithMe.ui.wall.EditCommentActivity;
import com.bondwithme.BondWithMe.ui.wall.WallMembersOrGroupsActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.util.UniversalImageLoaderUtil;
import com.bondwithme.BondWithMe.util.WallUtil;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;
import com.bondwithme.BondWithMe.widget.FreedomSelectionTextView;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class DiaryCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = DiaryCommentAdapter.class.getSimpleName();

    private Context mContext;
    private List<WallCommentEntity> data;
    private BaseFragment fragment;

    public DiaryCommentAdapter(BaseFragment fragment, List<WallCommentEntity> data) {
        this.fragment = fragment;
        mContext = fragment.getContext();
        this.data = data;
    }

    public void addData(List<WallCommentEntity> newData) {
        int index = data.size();
        data.addAll(newData);
//        notifyDataSetChanged();
        notifyItemRangeInserted(index, data.size() - index);
    }

    public void setData(List<WallCommentEntity> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.i(TAG, "onCreateViewHolder& viewType is " + viewType);
//        if(VIEW_TYPE_HEAD != viewType) {
        // 加载Item的布局.布局中用到的真正的CardView.
        View view = LayoutInflater.from(mContext).inflate(R.layout.diary_comment_item, parent, false);
        // ViewHolder参数一定要是Item的Root节点.
        return new VHItem(view);
//        } else {
//            View view = LayoutInflater.from(mContext).inflate(R.layout.wall_item, parent, false);
//            return new VHHeadItem(view);
//        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
        VHItem item = (VHItem) viewHolder;

        WallCommentEntity comment = data.get(i);
//            WallUtil.getLoveList(new HttpTools(mContext), item.tv_agree, MainActivity.getUser().getUser_id(), comment.getComment_id(), WallUtil.LOVE_MEMBER_COMMENT_TYPE);
        BitmapTools.getInstance(mContext).display(item.civ_comment_owner_head, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, comment.getUser_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        item.tv_comment_owner_name.setText(comment.getUser_given_name());
        item.tv_comment_content.setText(comment.getComment_content());
        int count = TextUtils.isEmpty(comment.getLove_count()) ? 0 : Integer.valueOf(comment.getLove_count());
        item.tv_agree_count.setText(String.format(mContext.getString(R.string.loves_count), count));

        String commentDate;
        if ("1".equals(comment.getComment_edited())) {
            commentDate = mContext.getString(R.string.comment_date_edited) + MyDateUtils.getLocalDateStringFromUTC(mContext, comment.getComment_creation_date());
        } else {
            commentDate = MyDateUtils.getLocalDateStringFromUTC(mContext, comment.getComment_creation_date());
        }
        item.comment_date.setText(commentDate);


        if (MainActivity.getUser().getUser_id().equals(comment.getUser_id())) {
            item.ibtnOptions.setVisibility(View.VISIBLE);
        } else {
            item.ibtnOptions.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(comment.getLove_id())) {
            item.iv_agree.setImageResource(R.drawable.goodjob_nonclicked);
        } else {
            item.iv_agree.setImageResource(R.drawable.goodjob_clicked);
        }
        setCommentPic(item.iv_comment_pic, item.niv_comment_pic, comment);
    }

    private void setCommentPic(GifImageView iv, NetworkImageView niv, WallCommentEntity comment) {
        LogUtil.i(TAG, "setCommentPic& file_id: " + comment.getFile_id() + "; StickerName is " + comment.getSticker_name());
        if (!TextUtils.isEmpty(comment.getFile_id())) {
            niv.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);
            BitmapTools.getInstance(mContext).display(niv, String.format(Constant.API_GET_COMMENT_PIC, Constant.Module_preview_m, comment.getUser_id(), comment.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        } else if (!TextUtils.isEmpty(comment.getSticker_group_path())) {
            iv.setVisibility(View.VISIBLE);
            niv.setVisibility(View.GONE);
            try {
                UniversalImageLoaderUtil.decodeStickerPic(iv, comment.getSticker_group_path(), comment.getSticker_name(), comment.getSticker_type());
            } catch (StickerTypeException e) {
                e.printStackTrace();
            }
        } else {
            niv.setVisibility(View.GONE);
            iv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircularNetworkImage civ_comment_owner_head;
        TextView tv_comment_owner_name;
        FreedomSelectionTextView tv_comment_content;
        TextView tv_agree_count;
        ImageButton iv_agree;
        ImageButton ibtnOptions;
        TextView comment_date;
        GifImageView iv_comment_pic;
        NetworkImageView niv_comment_pic;
        View commentContent;
//        TextView tv_agree;

        public VHItem(View itemView) {
            super(itemView);
            civ_comment_owner_head = (CircularNetworkImage) itemView.findViewById(R.id.civ_comment_owner_head);
            tv_comment_content = (FreedomSelectionTextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_owner_name = (TextView) itemView.findViewById(R.id.tv_comment_owner_name);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            tv_agree_count = (TextView) itemView.findViewById(R.id.tv_agree_count);
//            tv_agree = (TextView) itemView.findViewById(R.id.tv_agree);
            iv_agree = (ImageButton) itemView.findViewById(R.id.iv_agree);
            ibtnOptions = (ImageButton) itemView.findViewById(R.id.ibtn_options);
            iv_comment_pic = (GifImageView) itemView.findViewById(R.id.iv_comment_pic);
            niv_comment_pic = (NetworkImageView) itemView.findViewById(R.id.niv_comment_pic);
            commentContent = itemView.findViewById(R.id.comment_content);
//            tv_agree.setOnClickListener(this);
            iv_agree.setOnClickListener(this);
            tv_agree_count.setOnClickListener(this);
            ibtnOptions.setOnClickListener(this);
            niv_comment_pic.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            WallCommentEntity commentEntity = data.get(position);
            switch (v.getId()) {
                case R.id.tv_agree_count:
                case R.id.tv_agree:
                    if (Integer.valueOf(commentEntity.getLove_count()) > 0) {
                        Intent intent = new Intent(mContext, WallMembersOrGroupsActivity.class);
                        intent.setAction(Constant.ACTION_SHOW_LOVED_USER);
                        intent.putExtra(WallUtil.GET_LOVE_LIST_VIEWER_ID, MainActivity.getUser().getUser_id());
                        intent.putExtra(WallUtil.GET_LOVE_LIST_REFER_ID, commentEntity.getComment_id());
                        intent.putExtra(WallUtil.GET_LOVE_LIST_TYPE, WallUtil.LOVE_MEMBER_COMMENT_TYPE);
                        mContext.startActivity(intent);
                    }
                    break;

                case R.id.iv_agree:
                    newClick = true;
                    updateLovedView(commentEntity);

                    if (mCommentActionListener != null) {
                        if (TextUtils.isEmpty(commentEntity.getLove_id())) {
                            mCommentActionListener.doLove(commentEntity, false);
                        } else {
                            mCommentActionListener.doLove(commentEntity, true);
                        }
                    }

                    //                    //判断是否已经有进行中的判断
                    //                    if(!runningList.contains(position)) {
                    //                        runningList.add(position);
                    //                        check(position);
                    //                    }

                    break;
                case R.id.ibtn_options: {
                    initItemMenu(commentEntity, v);
                    break;
                }
                case R.id.niv_comment_pic: {
                    Intent intent = new Intent(mContext, ViewOriginalPicesActivity.class);

                    ArrayList<PhotoEntity> dataList = new ArrayList<>();

                    PhotoEntity peData = new PhotoEntity();
                    peData.setUser_id(commentEntity.getUser_id());
                    peData.setFile_id(commentEntity.getFile_id());
                    peData.setPhoto_caption(Constant.Module_Original);
                    peData.setPhoto_multipe("false");
                    dataList.add(peData);

                    intent.putExtra("is_data", true);
                    intent.putExtra("datas", dataList);
                    mContext.startActivity(intent);
                    break;
                }
            }
        }

        /**
         * 更新点击相关视图
         *
         * @param commentEntity {@link WallCommentEntity}实例
         */
        private void updateLovedView(WallCommentEntity commentEntity) {
            int count = Integer.valueOf(commentEntity.getLove_count());
//            String text = tv_agree.getText().toString();
            String name = MainActivity.getUser().getUser_given_name();
            int resId;
            if (TextUtils.isEmpty(commentEntity.getLove_id())) {
                count += 1;
                resId = R.drawable.goodjob_clicked;
                commentEntity.setLove_id(MainActivity.getUser().getUser_id());

//                if(count > 1) {
//                    text += (name + " ");
//                } else {
//                    text = name;
//                }

            } else {
                count -= 1;
                resId = R.drawable.goodjob_nonclicked;
                commentEntity.setLove_id(null);

//                if(count > 0) {
//                    StringBuilder temp = new StringBuilder();
//                    String split = name + " ";
//
//                    for(String str : text.split(split)) {
//                        temp.append(str);
//                    }
//                    text = temp.toString();
//                } else {
//                    text = "";
//                }
            }

            commentEntity.setLove_count(String.valueOf(count));
//            tv_agree.setText(text);
            iv_agree.setImageResource(resId);
            tv_agree_count.setText(String.format(mContext.getString(R.string.loves_count), count));
        }
    }

    boolean newClick;
    List<Integer> runningList = new ArrayList<>();

    private void check(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (DiaryCommentAdapter.this) {
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
                        e.printStackTrace();
                    }
                    final WallCommentEntity commentEntity = data.get(position);
                    if (mCommentActionListener != null) {
                        if (TextUtils.isEmpty(commentEntity.getLove_id())) {
                            mCommentActionListener.doLove(commentEntity, false);
                        } else {
                            mCommentActionListener.doLove(commentEntity, true);
                        }
                    }
                }

            }
        }).start();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initItemMenu(final WallCommentEntity commentEntity, View v) {
        if (commentEntity == null) {
            return;
        }

        final String commentID = commentEntity.getComment_id();

        PopupMenu popupMenu;
        Object object = v.getTag();
        if (object == null || !(object instanceof PopupMenu)) {
            popupMenu = new PopupMenu(mContext, v);
            popupMenu.inflate(R.menu.comment_menu);
            //使用反射，强制显示菜单图标
//            try {
//                Field field = popupMenu.getClass().getDeclaredField("mPopup");
//                field.setAccessible(true);
//                MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
//                mHelper.setForceShowIcon(true);
//            } catch (IllegalAccessException | NoSuchFieldException e) {
//                e.printStackTrace();
//            }

            v.setTag(popupMenu);
        } else {
            popupMenu = (PopupMenu) object;
        }

        if (!TextUtils.isEmpty(commentEntity.getFile_id()) || !TextUtils.isEmpty(commentEntity.getSticker_name())) {
            // 有图片或者表情不显示 “编辑选项”
            popupMenu.getMenu().findItem(R.id.menu_edit).setVisible(false);
        } else {
            popupMenu.getMenu().findItem(R.id.menu_edit).setVisible(true);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean result = false;
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        //自己发的或event creator 可以删除
                        if (mCommentActionListener != null) {
                            mCommentActionListener.doDelete(commentID);
                        }
                        result = true;
                        break;
                    case R.id.menu_edit:// 跳至 EditCommentActivity编辑评论
                        int index = data.indexOf(commentEntity);

                        Intent intent = new Intent(mContext, EditCommentActivity.class);
                        intent.putExtra(EditCommentActivity.DATA_INDEX, index);
                        intent.putExtra(EditCommentActivity.WALL_COMMENT_ENTITY, commentEntity);
                        fragment.startActivityForResult(intent, Constant.EDIT_COMMENT_REQUEST_CODE);
                        break;
                }
                return result;
            }
        });

        popupMenu.show();
    }

    private CommentActionListener mCommentActionListener;

    public void setCommentActionListener(CommentActionListener commentActionListener) {
        mCommentActionListener = commentActionListener;
    }

    public interface CommentActionListener {
        void doLove(final WallCommentEntity commentEntity, final boolean love);

        void doDelete(String commentId);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}