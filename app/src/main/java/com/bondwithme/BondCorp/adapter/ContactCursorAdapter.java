package com.bondwithme.BondCorp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bondwithme.BondCorp.R;

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
        Log.d("", "onBindViewHolder" + cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
//        if (selectContactIds.contains(viewHolder.getLayoutPosition() + "")) {
//            viewHolder.cb_selector.setChecked(true);
//        } else {
//            viewHolder.cb_selector.setChecked(false);
//        }
        boolean checked = false;
        for (String selectPositon : contactsIds) {
            if (selectPositon.equals(viewHolder.cb_selector.getTag() + "")) {
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
    private List<String> contactsIds = new ArrayList<>();

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
                            if (cb_selector.isChecked()) {
                                selectContactIds.remove(getAdapterPosition());
//                                boolean asd = contactsIds.remove(cb_selector.getTag() + "");
//                                cb_selector.setChecked(false);
//                                Log.d("", "onTouch " + cb_selector.getTag() + "--------" + contactsIds.size() + "--------" + asd +"--------" + contactsIds);
                            } else {
                                selectContactIds.add(getAdapterPosition());
//                                if (!contactsIds.contains(cb_selector.getTag() + ""))
//                                {
//                                    contactsIds.add(cb_selector.getTag() + "");
//                                    cb_selector.setChecked(true);
//                                }
                            }
                        } catch (Exception e) {
                        }

                    }
                    return false;
                }
            });

            //            christopher begin
            cb_selector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        if (isChecked) {
                            if (!contactsIds.contains(cb_selector.getTag() + ""))
                                contactsIds.add(cb_selector.getTag() + "");
                            Log.d("", "onTouch " + cb_selector.getTag() + "--------" + contactsIds.size() + "--------" + contactsIds);
                        } else {
                            if (contactsIds.contains(cb_selector.getTag() + ""))
                                contactsIds.remove(cb_selector.getTag() + "");
                            Log.d("", "onTouch " + cb_selector.getTag() + "--------" + contactsIds.size() + "--------" + contactsIds);
                        }

                    } catch (Exception e) {

                    }
                }
            });
            //            christopher end

        }
    }

    public List<String> getContactsIds()
    {
        return contactsIds;
    }
}