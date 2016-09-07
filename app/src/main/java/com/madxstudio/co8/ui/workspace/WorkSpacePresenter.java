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

import android.text.TextUtils;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.entity.WorkspaceEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created 16/9/7.
 *
 * @author Jackie
 * @version 1.0
 */
public class WorkSpacePresenter implements WorkspaceContracts.Presenter {
    private static final String TAG = "WorkSpacePresenter";

    public static final String GET_WORKSPACE = TAG + "_WORKSPACE";

    private final WorkspaceContracts.View view;
    public Parameter parameter;

    public WorkSpacePresenter(WorkspaceContracts.View view, Parameter parameter) {
        this.view = view;
        this.parameter = parameter;

        view.setPresenter(this);
    }

    @Override
    public void onStart() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", MainActivity.getUser().getUser_id());
        params.put("limit", parameter.limit + "");
        params.put("start", parameter.start + "");
        if (!TextUtils.isEmpty(parameter.member_id)) {
            params.put("member_id", parameter.member_id + "");
        }

        LogUtil.i(TAG, "requestData& startIndex: " + parameter.start);

        String url = UrlUtil.generateUrl(Constant.API_WALL_MAIN, params);
        new HttpTools(view.getContext()).get(url, params, GET_WORKSPACE, new HttpCallback() {

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {
                LogUtil.d(TAG, "onResult() called with: " + "string = [" + string + "]");
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                ArrayList<WorkspaceEntity> data = gson.fromJson(string, new
                        TypeToken<ArrayList<WorkspaceEntity>>() {
                        }.getType());
                view.loadComplete(data);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onCancelled() {
                LogUtil.w(TAG, "onCancelled: ");
            }

            @Override
            public void onLoading(long count, long current) {
                LogUtil.d(TAG, "onLoading() called with: " + "count = [" + count + "], current = " +
                        "[" + current + "]");
            }
        });
    }

    /**
     * start: "0" // posting content creator ID
     * limit: "10"
     * user_id: "1" // own id 当前用户
     * member_id: "3", // member id 如果是自己的空间，就放自己的ID
     */
    public static class Parameter {
        protected String start;
        protected String limit;
        protected String user_id;
        protected String member_id;
    }
}
