package com.madxstudio.co8.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.ui.AddMemberWorkFlow;
import com.madxstudio.co8.ui.BaseFragment;
import com.madxstudio.co8.ui.FamilyProfileActivity;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.MeActivity;
import com.madxstudio.co8.widget.CircularNetworkImage;
import com.madxstudio.co8.widget.MyDialog;

import java.util.List;

/**
 * @的用户详细Adpater
 * Created by zhuweiping on 4/28/15.
 */
public class WallMemberAdapter extends RecyclerView.Adapter<WallMemberAdapter.MGItem>{

    private final static String TAG = WallMemberAdapter.class.getSimpleName();

    private Context mContext;
    private List<UserEntity> mData;
    private BaseFragment mFragment;

    public WallMemberAdapter(Context context, List<UserEntity> data, BaseFragment fragment){
        mContext = context;
        mData = data;
        mFragment = fragment;
    }

    public void add(List<UserEntity> data){
        mData.addAll(data);
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public MGItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_or_group_item, parent, false);
        return new MGItem(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
     * the given position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this
     * method again if the position of the item changes in the data set unless the item itself
     * is invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside this
     * method and should not keep a copy of it. If you need the position of an item later on
     * (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will have
     * the updated adapter position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MGItem holder, int position) {
        UserEntity entity = mData.get(position);
        BitmapTools.getInstance(mContext).display(holder.nivHead, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, entity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
        holder.name.setText(entity.getUser_given_name());
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MGItem extends RecyclerView.ViewHolder {
        /**
         * 头像视图
         */
        CircularNetworkImage nivHead;
        TextView name;

        public MGItem(View itemView) {
            super(itemView);
            nivHead = (CircularNetworkImage) itemView.findViewById(R.id.owner_head);
            name = (TextView) itemView.findViewById(R.id.owner_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final UserEntity userEntity = mData.get(position);
                    String own = userEntity.getOwn_flag();
                    if("1".equals(own)) {
                        // 是自己跳转至me界面
                        Intent intent = new Intent(mContext, MeActivity.class);
                        mContext.startActivity(intent);
                    } else if("0".equals(own)) {
                        // 不是自己显示用户详情或者提示添加
                        String added = userEntity.getAdded_flag();
                        if("1".equals(added)) {
                            // 已经添加为好友，显示用户详情
                            Intent intent = new Intent(mContext, FamilyProfileActivity.class);
                            intent.putExtra(UserEntity.EXTRA_MEMBER_ID, userEntity.getUser_id());
                            intent.putExtra(UserEntity.EXTRA_GROUP_ID, userEntity.getGroup_id());
                            intent.putExtra(UserEntity.EXTRA_GROUP_NAME, userEntity.getUser_given_name());
                            mContext.startActivity(intent);
                        } if("0".equals(added)) {
                            // 没有添加好友，弹出添加好友界面提示框由用户选择是否添加
                            final MyDialog myDialog = new MyDialog(mContext, "", mContext.getString(R.string.alert_ask_add_member));
                            myDialog.setButtonAccept(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 同意添加好友跳转至添加好友界面
                                    Intent intent = new Intent(mContext, AddMemberWorkFlow.class);
                                    intent.putExtra("from", MainActivity.getUser().getUser_id());
                                    intent.putExtra("to", userEntity.getUser_id());
                                    mFragment.startActivityForResult(intent, 10);
                                    myDialog.dismiss();
                                }
                            });
                            myDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 不同意添加，关闭对话框
                                    myDialog.dismiss();
                                }
                            });
                            myDialog.show();
                        }
                    }
                }
            });
        }
    }
}
