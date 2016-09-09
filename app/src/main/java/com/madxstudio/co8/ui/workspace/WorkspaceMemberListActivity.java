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

package com.madxstudio.co8.ui.workspace;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.WorkspaceMembersAdapter;
import com.madxstudio.co8.ui.BaseToolbarActivity;

/**
 * Created 16/9/9.
 *
 * @author Jackie
 * @version 1.0
 */
public class WorkspaceMemberListActivity extends BaseToolbarActivity {
    private SearchView searchView;
    private RecyclerView recMembers;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_workspace_member_list;
    }

    @Override
    public void initView() {
        super.initView();
        searchView = getViewById(R.id.search_members);
        recMembers = getViewById(R.id.rec_members);

        recMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));

        recMembers.setAdapter(new WorkspaceMembersAdapter(this));
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    public void requestData() {

    }
}
