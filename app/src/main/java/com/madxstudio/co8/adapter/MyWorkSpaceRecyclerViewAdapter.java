/*
 *
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 *             $                                                   $
 *             $                       _oo0oo_                     $
 *             $                      o8888888o                    $
 *             $                      88" . "88                    $
 *             $                      (| -_- |)                    $
 *             $                      0\  =  /0                    $
 *             $                    ___/`-_-'\___                  $
 *             $                  .' \\|     |$ '.                 $
 *             $                 / \\|||  :  |||$ \                $
 *             $                / _||||| -:- |||||- \              $
 *             $               |   | \\\  -  $/ |   |              $
 *             $               | \_|  ''\- -/''  |_/ |             $
 *             $               \  .-\__  '-'  ___/-. /             $
 *             $             ___'. .'  /-_._-\  `. .'___           $
 *             $          ."" '<  `.___\_<|>_/___.' >' "".         $
 *             $         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       $
 *             $         \  \ `_.   \_ __\ /__ _/   .-` /  /       $
 *             $     =====`-.____`.___ \_____/___.-`___.-'=====    $
 *             $                       `=-_-='                     $
 *             $     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   $
 *             $                                                   $
 *             $          Buddha Bless         Never Bug           $
 *             $                                                   $
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 */

package com.madxstudio.co8.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.base.BaseAdapter;
import com.madxstudio.co8.entity.WorkspaceEntity;
import com.madxstudio.co8.ui.workspace.WorkSpaceFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link WorkspaceEntity} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyWorkSpaceRecyclerViewAdapter extends RecyclerView
        .Adapter<MyWorkSpaceRecyclerViewAdapter.ViewHolder> implements BaseAdapter<ArrayList<WorkspaceEntity>> {

    private List<WorkspaceEntity> mValues = new ArrayList<>();
    private final OnListFragmentInteractionListener mListener;

    public MyWorkSpaceRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workspace_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getContent_creation_date());
        holder.mContentView.setText(mValues.get(position).getContent_title());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void setData(ArrayList<WorkspaceEntity> data) {
        mValues = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView icHead;
        public TextView mIdView;
        public TextView mContentView;
        public TextView mDate;
        public WorkspaceEntity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.txt_workspace_title);
            mContentView = (TextView) view.findViewById(R.id.txt_workspace_description);
            mDate = (TextView) view.findViewById(R.id.txt_workspace_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
