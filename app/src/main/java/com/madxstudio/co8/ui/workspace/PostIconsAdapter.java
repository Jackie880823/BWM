package com.madxstudio.co8.ui.workspace;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madxstudio.co8.R;

/**
 * Created 16/8/4.
 *
 * @author Jackie
 * @version 1.0
 */
public class PostIconsAdapter extends RecyclerView.Adapter<PostIconsAdapter.Holder> {
    private Context context;

    /**
     * 适配器封装的{@link View}是否是以{@link android.support.v7.widget.GridLayoutManager}形式加载
     */
    private boolean isGrid = true;

    public PostIconsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_post_icons, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (isGrid) {
            holder.txtDesc.setVisibility(View.GONE);
            holder.txtNumber.setVisibility(View.GONE);
        } else {
            holder.txtDesc.setVisibility(View.VISIBLE);
            holder.txtNumber.setVisibility(View.VISIBLE);
        }
    }

    public boolean isGrid() {
        return isGrid;
    }

    public void setGrid(boolean grid) {
        isGrid = grid;
    }

    int count = 4;

    @Override
    public int getItemCount() {
        return count;
    }

    public void setItemCount(int count) {
        this.count = count;
    }

    static class Holder extends RecyclerView.ViewHolder{
        public ImageView imageIcon;
        public TextView txtDesc;
        public TextView txtNumber;
        public Holder(View itemView) {
            super(itemView);

            imageIcon = (ImageView) itemView.findViewById(R.id.img_icon);
            txtDesc = (TextView) itemView.findViewById(R.id.txt_desc);
            txtNumber = (TextView) itemView.findViewById(R.id.txt_number);
        }
    }
}
