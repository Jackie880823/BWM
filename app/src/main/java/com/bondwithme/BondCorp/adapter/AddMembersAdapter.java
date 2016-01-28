package com.bondwithme.BondCorp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.BitmapTools;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.entity.RecommendEntity;
import com.bondwithme.BondCorp.http.UrlUtil;
import com.bondwithme.BondCorp.ui.MainActivity;
import com.bondwithme.BondCorp.ui.add.AddContactMembersActivity;
import com.bondwithme.BondCorp.ui.add.AddMembersActivity;
import com.bondwithme.BondCorp.util.RelationshipUtil;
import com.bondwithme.BondCorp.widget.CircularNetworkImage;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by christepherzhang on 15/11/3.
 */
public class AddMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private enum ITEM_TYPE
    {
        ITEM_HEAD,
        ITEM_CONTENT
    }
    private Context mContext;
    private List<RecommendEntity> mData;


    public AddMembersAdapter(Context context, List<RecommendEntity> data) {
        mContext = context;
        mData = data;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_HEAD.ordinal())
        {
            return new HeaderViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_add_members_options_head, null));
        }
        else if (viewType == ITEM_TYPE.ITEM_CONTENT.ordinal())
        {
            return new ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_add_members, null));
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder)
        {

        }
        else if (holder instanceof ContentViewHolder)
        {


            ContentViewHolder itemView = (ContentViewHolder)holder;
            if (position == 1)//推荐的第一个要展示提示布局，其余的不用
            {
                itemView.llPrompt.setVisibility(View.VISIBLE);
            }
            else
            {
                itemView.llPrompt.setVisibility(View.GONE);
            }

            BitmapTools.getInstance(mContext).display(itemView.cni, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, mData.get(position - 1).getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
//            VolleyUtil.initNetworkImageView(mContext, itemView.cni, String.format(Constant.API_GET_PHOTO, Constant.Module_profile, mData.get(position - 1).getUser_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
            itemView.tvName.setText(mData.get(position - 1).getUser_given_name());
            itemView.tvRelationship.setText(mData.get(position - 1).getUser_recommend() + mContext.getResources().getText(R.string.text_s) + RelationshipUtil.getRelationshipName(mContext, mData.get(position - 1).getUser_recom_rel()));
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
        {
            return ITEM_TYPE.ITEM_HEAD.ordinal();
        }
        else
        {
            return ITEM_TYPE.ITEM_CONTENT.ordinal();
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout llContacts;
        private LinearLayout llFacebook;
        private LinearLayout llQq;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            llContacts = (LinearLayout) itemView.findViewById(R.id.ll_add_via_contacts);
            llFacebook = (LinearLayout) itemView.findViewById(R.id.ll_facebook);
            llQq = (LinearLayout) itemView.findViewById(R.id.ll_qq);

            llContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setMessage(R.string.text_dialog_ask_contact);
                    dialog.setCancelable(false);

                    dialog.setNegativeButton(R.string.text_dont_allow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.startActivity(new Intent(mContext, AddContactMembersActivity.class));
                        }
                    });

                    //请求后台运行，活动销毁了。会奔溃？？
                    if (!((AddMembersActivity)mContext).isFinishing())
                    {
                        dialog.show();
                    }
                }
            });
        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout llPrompt;
        private RelativeLayout rl;
        private CircularNetworkImage cni;
        private TextView tvName;
        private ImageButton ibAdd;
        private TextView tvRelationship;

        public ContentViewHolder(View itemView) {
            super(itemView);
            llPrompt = (LinearLayout) itemView.findViewById(R.id.ll_prompt);
            rl = (RelativeLayout) itemView.findViewById(R.id.rl);
            cni = (CircularNetworkImage) itemView.findViewById(R.id.cni);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ibAdd = (ImageButton) itemView.findViewById(R.id.ib_add);
            tvRelationship = (TextView)itemView.findViewById(R.id.tv_relationship);

            llPrompt.setLongClickable(false);

            ibAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAddIconClickListener.onAddIconClick(mData.get(getAdapterPosition() - 1));
                }
            });


            //把这段代码放在这边不好。。。有空提到活动上去。
            //调试打印的数据这边看不到？？？why、？？
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setMessage(R.string.alert_comment_del);
                    dialog.setCancelable(false);

                    dialog.setNegativeButton(R.string.text_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.setPositiveButton(R.string.text_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestInfo requestInfo = new RequestInfo();

                            requestInfo.url = Constant.API_BONDALERT_REMOVE_RECOMMEND + MainActivity.getUser().getUser_id();

//                          拼接出这个格式   {"member_id":[33, 35]}
                            Map<String, String> params = new HashMap<>();
                            params.put("member_id", new Gson().toJson(mData.get(getAdapterPosition() - 1).getUser_id()));
                            String jsonParamsString = UrlUtil.mapToJsonstring(params);
                            requestInfo.jsonParam = jsonParamsString;
                            Log.d("", "jsonParamsString=========" + jsonParamsString);
                            Log.d("", "getFullUrl========" + requestInfo.getFullUrl());
                            new HttpTools(mContext).put(requestInfo, ((AddMembersActivity)mContext).getTAG(), new HttpCallback() {
                                @Override
                                public void onStart() {
                                    ((AddMembersActivity) mContext).getvProgress().setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onFinish() {
                                    ((AddMembersActivity) mContext).getvProgress().setVisibility(View.GONE);
                                }

                                @Override
                                public void onResult(String string) {
                                    Log.d("", "onResult:========= " + string);

//                                    应该重新获取该列表比较妥当？？？
                                    ((AddMembersActivity) mContext).requestData();



                                    //TODO
                                    //没有判断是否成功就直接删除了该item，不正确。
//                                    mData.remove(getAdapterPosition() - 1);
//                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onError(Exception e) {

                                }

                                @Override
                                public void onCancelled() {

                                }

                                @Override
                                public void onLoading(long count, long current) {

                                }
                            });

                        }
                    });

                    //请求后台运行，活动销毁了。会奔溃？？
                    if (!((AddMembersActivity)mContext).isFinishing())
                    {
                        dialog.show();
                    }

                    return true;
                }
            });
        }
    }


    OnAddIconClickListener onAddIconClickListener;

    public interface OnAddIconClickListener
    {
        void onAddIconClick(RecommendEntity recommendEntity);
    }

    public void setOnAddIconClickListener(OnAddIconClickListener onAddIconClickListener)
    {
        this.onAddIconClickListener = onAddIconClickListener;
    }

}
