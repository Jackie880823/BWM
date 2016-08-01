package com.madxstudio.co8.ui.workspace;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.workspace.WorkSpaceFragment.OnListFragmentInteractionListener;
import com.madxstudio.co8.ui.workspace.dummy.DummyContent.Workspace;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Workspace} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyWorkSpaceRecyclerViewAdapter extends RecyclerView
        .Adapter<MyWorkSpaceRecyclerViewAdapter.ViewHolder> {

    private final List<Workspace> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyWorkSpaceRecyclerViewAdapter(List<Workspace> items,
                                          OnListFragmentInteractionListener listener) {
        mValues = items;
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
        holder.mIdView.setText(mValues.get(position).getId());
        holder.mContentView.setText(mValues.get(position).getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mIdView;
        public TextView mContentView;
        public TextView mDate;
        public Workspace mItem;

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
