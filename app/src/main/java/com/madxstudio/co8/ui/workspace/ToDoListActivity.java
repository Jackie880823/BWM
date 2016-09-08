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
import android.view.KeyEvent;

import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.ToDoListAdapter;
import com.madxstudio.co8.ui.BaseToolbarActivity;

/**
 * Created 16/8/31.
 *
 * @author Jackie
 * @version 1.0
 */
public class ToDoListActivity extends BaseToolbarActivity {
    private static final String TAG = "ToDoListActivity";

    private RecyclerView recToDoList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_to_do_list;
    }

    @Override
    public void initView() {
        super.initView();
        recToDoList = getViewById(R.id.rec_to_do_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        recToDoList.setLayoutManager(new LinearLayoutManager(this));
        recToDoList.setAdapter(new ToDoListAdapter(this));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    public void requestData() {

    }
}
