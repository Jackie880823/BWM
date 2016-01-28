package com.bondwithme.BondCorp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.FamilyMemberEntity;
import com.bondwithme.BondCorp.entity.RelationshipEnum;
import com.bondwithme.BondCorp.http.VolleyUtil;
import com.bondwithme.BondCorp.ui.MainActivity;
import com.bondwithme.BondCorp.ui.OnFamilyItemClickListener;
import com.bondwithme.BondCorp.util.RelationshipUtil;
import com.bondwithme.BondCorp.widget.CircularNetworkImage;

import java.util.ArrayList;

/**
 * Created by Jackie on 8/12/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class RelationshipAdapter extends RecyclerView.Adapter<RelationshipAdapter.RelationHolder> {

    private ArrayList<FamilyMemberEntity> data;
    private Context context;
    private RelationshipEnum type;
    private OnFamilyItemClickListener listener;

    public RelationshipAdapter(Context context, ArrayList<FamilyMemberEntity> data, RelationshipEnum type) {
        this.data = data;
        this.context = context;
        this.type = type;
    }

    public void setData(ArrayList<FamilyMemberEntity> data) {
        this.data = data;
    }

    public void setListener(OnFamilyItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Called when RecyclerView needs a new {@link RelationHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(RelationHolder, int)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(RelationHolder, int)
     */
    @Override
    public RelationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout layout = new LinearLayout(context);
        int padding = context.getResources().getDimensionPixelOffset(R.dimen.default_content_padding);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return new RelationHolder<>(layout);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link RelationHolder#itemView} to reflect the item at
     * the given position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this
     * method again if the position of the item changes in the data set unless the item itself
     * is invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside this
     * method and should not keep a copy of it. If you need the position of an item later on
     * (e.g. in a click listener), use {@link RelationHolder#getAdapterPosition()} which will have
     * the updated adapter position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RelationHolder holder, int position) {
        ArrayList<FamilyMemberEntity> userEntities = new ArrayList<>();
        FamilyMemberEntity member = data.get(position);
        userEntities.add(member);
        ArrayList<FamilyMemberEntity> spouse = member.getSpouse();
        if(spouse != null) {
            userEntities.addAll(spouse);
        }

        int count = holder.getChildCount();
        int minSize = userEntities.size();
        if(count > minSize) {
            holder.removeViews(minSize, count - minSize);
        } else if(count < minSize) {
            addViews(holder, minSize - count, position);
        }

        if(this.type == RelationshipEnum.sibling) {
            checkArrow(holder, position);
        }

        showData(userEntities, holder);
    }

    private void checkArrow(RelationHolder holder, int position) {
        View view = holder.getChildAt(0);
        if(position == 0) {
            view.findViewById(R.id.up_tri_angel_iv).setVisibility(View.VISIBLE);
            view.findViewById(R.id.down_tri_angel_iv).setVisibility(View.VISIBLE);
            view.findViewById(R.id.relationship_item_content).setBackgroundResource(R.color.family_tree_relationship_me_background_color);
        } else {
            view.findViewById(R.id.up_tri_angel_iv).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.down_tri_angel_iv).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.relationship_item_content).setBackgroundResource(R.color.family_tree_relationship_sibling_background_color);
        }
    }

    /**
     * 向{@code holder}中添加{@code count}个{@link FamilyMemberEntity}显示视图
     *
     * @param holder   子项的视图封装实例
     * @param count    需要添加的{@link FamilyMemberEntity}显示视图
     * @param position 数据列表位置
     */
    private void addViews(RelationHolder holder, int count, int position) {
        for(int i = 0; i < count; i++) {
            View child = LayoutInflater.from(context).inflate(R.layout.relationship_item_layout, null);
            if(type == RelationshipEnum.sibling) {
                child.findViewById(R.id.relationship_item_content).setBackgroundResource(R.color.family_tree_relationship_sibling_background_color);
            }
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.addView(child, i == count - 1 && position != 0);
        }
    }

    private void showData(ArrayList<FamilyMemberEntity> familyMemberEntities, RelationHolder holder) {
        int count = holder.getChildCount();
        int dataSize = familyMemberEntities.size();
        for(int i = 0; i < count; i++) {
            View child = holder.getChildAt(i);
            if(i < dataSize) {
                child.findViewById(R.id.relationship_item_name_tv).setVisibility(View.VISIBLE);
                child.findViewById(R.id.relationship_item_relation_tv).setVisibility(View.VISIBLE);
                child.findViewById(R.id.relationship_item_image).setVisibility(View.VISIBLE);
                FamilyMemberEntity familyMemberEntity = familyMemberEntities.get(i);
                child.setTag(familyMemberEntity);
                setChildView(familyMemberEntity, child);
            } else {
                child.setTag(null);
                child.findViewById(R.id.relationship_item_name_tv).setVisibility(View.INVISIBLE);
                child.findViewById(R.id.relationship_item_relation_tv).setVisibility(View.INVISIBLE);
                child.findViewById(R.id.relationship_item_image).setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setChildView(FamilyMemberEntity familyMemberEntity, View childView) {
        CircularNetworkImage headImage = (CircularNetworkImage) childView.findViewById(R.id.relationship_item_image);
        TextView tvName = (TextView) childView.findViewById(R.id.relationship_item_name_tv);
        TextView tvRelation = (TextView) childView.findViewById(R.id.relationship_item_relation_tv);
        VolleyUtil.initNetworkImageView(context, headImage, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, familyMemberEntity.getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);

        String name;
        if(MainActivity.getUser().getUser_id().equals(familyMemberEntity.getUser_id())) {
            name = context.getString(R.string.text_me);
        } else {
            name = familyMemberEntity.getUser_given_name();
        }
        tvName.setText(name);

        String treeTypeName;
        treeTypeName = familyMemberEntity.getTree_type_name();
        if(!TextUtils.isEmpty(treeTypeName)) {
            treeTypeName = RelationshipUtil.getRelationshipName(context, treeTypeName);
        }
        tvRelation.setText(treeTypeName);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RelationHolder<T extends LinearLayout> extends RecyclerView.ViewHolder {
        private T itemView;

        public RelationHolder(T itemView) {
            super(itemView);
            this.itemView = itemView;
        }


        /**
         * Returns the number of children in the group.
         *
         * @return a positive integer representing the number of children in
         * the group
         */
        public int getChildCount() {
            return itemView.getChildCount();
        }

        /**
         * <p>Adds a child view. If no layout parameters are already set on the child, the
         * default parameters for this ViewGroup are set on the child.</p>
         * <p/>
         * <p><strong>Note:</strong> do not invoke this method from
         * {@link LinearLayout#draw(android.graphics.Canvas)}, {@link LinearLayout#onDraw(android.graphics.Canvas)},
         * {@link LinearLayout#dispatchDraw(android.graphics.Canvas)} or any related method.</p>
         *
         * @param child the child view to add
         * @see LinearLayout#generateDefaultLayoutParams()
         */
        public void addView(View child, boolean show) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            child.setLayoutParams(params);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tag = v.getTag();
                    if(listener != null && tag != null) {
                        listener.onClick(v, (FamilyMemberEntity) tag);
                    }
                }
            });
            View nullView = child.findViewById(R.id.null_image_view);
            if(show) {
                nullView.setVisibility(View.VISIBLE);
            } else {
                nullView.setVisibility(View.GONE);
            }
            itemView.addView(child);
        }

        /**
         * Returns the view at the specified position in the group.
         *
         * @param index the position at which to get the view from
         * @return the view at the specified position or null if the position
         * does not exist within the group
         */
        public View getChildAt(int index) {
            return itemView.getChildAt(index);
        }

        /**
         * Removes the specified range of views from the group.
         * <p/>
         * <p><strong>Note:</strong> do not invoke this method from
         * {@link LinearLayout#draw(android.graphics.Canvas)}, {@link LinearLayout#onDraw(android.graphics.Canvas)},
         * {@link LinearLayout#dispatchDraw(android.graphics.Canvas)} or any related method.</p>
         *
         * @param start the first position in the group of the range of views to remove
         * @param count the number of views to remove
         */
        public void removeViews(int start, int count) {
            itemView.removeViews(start, count);
        }

    }
}
