package com.madx.bwm.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.materialdesign.views.CheckBox;
import com.madx.bwm.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wing on 15/4/8.
 */
public class ContactCursorAdapter extends CursorRecyclerViewAdapter<ContactCursorAdapter.ViewHolder> {

    public ContactCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.contact_name.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
        viewHolder.cb_selector.setTag(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)) + "");

//        if (selectContactIds.contains(viewHolder.getLayoutPosition() + "")) {
//            viewHolder.cb_selector.setChecked(true);
//        } else {
//            viewHolder.cb_selector.setChecked(false);
//        }
        boolean checked = false;
        for (int selectPositon : selectContactIds) {
            if (selectPositon == viewHolder.getLayoutPosition()) {
                checked = true;
                break;
            }
        }
        if (checked) {
            viewHolder.cb_selector.setChecked(true);
        } else {
            viewHolder.cb_selector.setChecked(false);
        }


    }

    private List<Integer> selectContactIds = new ArrayList<>();

    public List<Integer> getSelectContactIds() {
        return selectContactIds;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cb_selector;
        private TextView contact_name;

        public ViewHolder(View itemView) {
            super(itemView);
            cb_selector = (CheckBox) itemView.findViewById(R.id.cb_selector);

            contact_name = (TextView) itemView.findViewById(R.id.contact_name);


            cb_selector.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_UP) {
                        try {
                            if (cb_selector.isCheck()) {
                                Log.i("", "onClick=========1" + getAdapterPosition());
                                selectContactIds.remove(getAdapterPosition());
                            } else {
                                Log.i("", "onClick=========2" + getAdapterPosition());
                                selectContactIds.add(getAdapterPosition());
                            }
                        } catch (Exception e) {
                        }
                    }
                    return false;
                }
            });



        }
    }
}