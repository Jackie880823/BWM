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

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madxstudio.co8.R;
import com.madxstudio.co8.base.BaseAdapter;
import com.madxstudio.co8.base.BaseHolderViewInterface;
import com.madxstudio.co8.entity.WorkspaceMemberEntity;

import java.util.List;

/**
 * Created 16/9/9.
 *
 * @author Jackie
 * @version 1.0
 */
public class WorkspaceMembersAdapter extends RecyclerView.Adapter<WorkspaceMembersAdapter.ViewHolder>
        implements BaseAdapter<List<WorkspaceMemberEntity>>{
    private List<WorkspaceMemberEntity> data;
    private Context context;

    public WorkspaceMembersAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_member_workspace,
                parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public void setData(List<WorkspaceMemberEntity> data) {
        this.data = data;
    }

    @Override
    public void addData(List<WorkspaceMemberEntity> data) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            BaseHolderViewInterface<WorkspaceMemberEntity> {
        private WorkspaceMemberEntity entity;
        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public <V extends View> V getViewById(@IdRes int resId) {
            return (V) itemView.findViewById(resId);
        }

        @Override
        public void bindEntity(WorkspaceMemberEntity entity) {
            this.entity = entity;
        }

        @Override
        public void onClick(View v) {

        }
    }

}
