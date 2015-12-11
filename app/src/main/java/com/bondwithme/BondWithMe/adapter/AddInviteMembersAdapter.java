package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.ContactDetailEntity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christepherzhang on 15/12/10.
 */
public class AddInviteMembersAdapter extends RecyclerView.Adapter<AddInviteMembersAdapter.PhoneAndEmailViewHolder>{

    private ContactDetailEntity mData;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Integer> checked = new ArrayList<>();
    private List<String> phoneNumbers = new ArrayList<>();
    private List<String> emails = new ArrayList<>();

    public List<Integer> getChecked() {
        return checked;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public List<String> getEmails() {
        return emails;
    }

    public AddInviteMembersAdapter(ContactDetailEntity mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(this.mContext);
        for (int i = 0; i < mData.getPhoneNumbers().size() + mData.getEmails().size(); i++) {
            checked.add(new Integer(i));
        }
        phoneNumbers.addAll(mData.getPhoneNumbers());
        emails.addAll(mData.getEmails());
    }

    @Override
    public PhoneAndEmailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhoneAndEmailViewHolder(mLayoutInflater.inflate(R.layout.item_invite_phone_email, null));
    }

    @Override
    public void onBindViewHolder(PhoneAndEmailViewHolder holder, int position) {
        if (position < mData.getPhoneNumbers().size())
        {
            holder.tvInfo.setText(mData.getPhoneNumbers().get(position));
        }
        else
        {
            holder.tvInfo.setText(mData.getEmails().get(position - mData.getPhoneNumbers().size()));
        }

        holder.cb.setTag(new Integer(position));
        if(checked.contains(new Integer(position)))
        {
            holder.cb.setChecked(true);
        }
        else
        {
            holder.cb.setChecked(false);
        }
        oncheckedCheckBox(holder, position);
    }

    private void oncheckedCheckBox(final PhoneAndEmailViewHolder holder, final int position) {

        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //记录cb状态,手机号，邮箱
                if (isChecked)
                {

                    if (!checked.contains(holder.cb.getTag()))
                    {
                        Log.e("*****1", "!!!!!!getTag---->" + holder.cb.getTag());
                        checked.add(new Integer(position));

                        if (position < mData.getPhoneNumbers().size())
                        {
                            Log.e("add", "!!!!!!add---->" + mData.getPhoneNumbers().get(position).toString());
                            phoneNumbers.add(mData.getPhoneNumbers().get(position).toString());
                        }
                        else
                        {
                            Log.e("add", "!!!!!!add---->" + mData.getEmails().get(position - mData.getPhoneNumbers().size()).toString());
                            emails.add(mData.getEmails().get(position - mData.getPhoneNumbers().size()).toString());
                        }
                    }
                }
                else
                {

                    if (checked.contains(holder.cb.getTag()))
                    {
                        Log.e("*****2", "!!!!!!getTag---->" + holder.cb.getTag());
                        checked.remove(new Integer(position));

                        if (position < mData.getPhoneNumbers().size())
                        {
                            Log.e("remove", "!!!!!!remove---->" + mData.getPhoneNumbers().get(position).toString());
                            phoneNumbers.remove(mData.getPhoneNumbers().get(position).toString());
                        }
                        else
                        {
                            Log.e("remove", "!!!!!!remove---->" + mData.getEmails().get(position - mData.getPhoneNumbers().size()).toString());
                            emails.remove(mData.getEmails().get(position - mData.getPhoneNumbers().size()).toString());
                        }
                    }

                }

                Log.e("*****", "!!!!!!---->" + new Gson().toJson(getPhoneNumbers()));
                Log.e("*****", "!!!!!!---->" + new Gson().toJson(getEmails()));
                Log.e("*****", "!!!!!!---->" + new Gson().toJson(getChecked()));

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.getPhoneNumbers().size() + mData.getEmails().size();
    }

    public class PhoneAndEmailViewHolder extends RecyclerView.ViewHolder
    {

        private TextView tvInfo;
        private CheckBox cb;

        public PhoneAndEmailViewHolder(View itemView) {
            super(itemView);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_info);
            cb = (CheckBox) itemView.findViewById(R.id.cb);


        }
    }
}
