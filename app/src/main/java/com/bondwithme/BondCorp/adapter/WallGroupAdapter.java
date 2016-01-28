package com.bondwithme.BondCorp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.ext.tools.BitmapTools;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.GroupEntity;
import com.bondwithme.BondCorp.ui.MessageChatActivity;
import com.bondwithme.BondCorp.util.LogUtil;
import com.bondwithme.BondCorp.widget.CircularNetworkImage;
import com.bondwithme.BondCorp.widget.MyDialog;

import java.util.List;

/**
 * @的分组详细的Adapter
 * Created by Jackie on 4/28/15.
 * @author Jackie
 * @version 1.0
 */
public class WallGroupAdapter extends RecyclerView.Adapter<WallGroupAdapter.MGItem>{

    private final static String TAG = WallGroupAdapter.class.getSimpleName();

    private Context mContext;
    private List<GroupEntity> mData;

    public WallGroupAdapter(Context context, List<GroupEntity> data){
        mContext = context;
        mData = data;
    }

    public void add(List<GroupEntity> data){
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
        GroupEntity entity = mData.get(position);
        BitmapTools.getInstance(mContext).display(holder.nivHead, String.format(Constant.API_GET_GROUP_PHOTO, entity.getGroup_id()), R.drawable.network_image_default, R.drawable.network_image_default);
        holder.name.setText(entity.getGroup_name());
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
                    GroupEntity groupEntity = mData.get(position);
                    String group_flag = groupEntity.getGroup_flag();
                    LogUtil.i(TAG, "onClick& position: " + position + "; group_flag = " + group_flag);
                    if("0".equals(group_flag)) {
                        // 不在此分组中弹出提示
                        final MyDialog myDialog = new MyDialog(mContext, "", mContext.getString(R.string.alert_no_join_group));
                        myDialog.setButtonAccept(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                        myDialog.show();
                    } else if("1".equals(group_flag)) {
                        // 在此分组中跳转至分组群聊界面
                        Intent intent = new Intent(mContext, MessageChatActivity.class);
                        intent.putExtra("type", 1);
                        intent.putExtra("groupId", groupEntity.getGroup_id());
                        intent.putExtra("titleName", groupEntity.getGroup_name());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
