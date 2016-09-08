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

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.MediaData;
import com.madxstudio.co8.ui.BaseToolbarActivity;
import com.madxstudio.co8.ui.share.SelectPhotosActivity;
import com.madxstudio.co8.util.UniversalImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created 16/9/7.
 *
 * @author Jackie
 * @version 1.0
 */
public class CreateWorkSpaceActivity extends BaseToolbarActivity {
    private MenuItem rightTodo;

    private ImageView imgTitle;
    private AppCompatEditText editWorkspaceTitle;
    private AppCompatTextView txtCountLimit;
    private AppCompatEditText editDescriptions;

    private ImageView imgAttachments;
    private ImageView imgMembers;
    private ImageView imgTodoList;
    private ImageView imgPrivilege;
    private View layoutAttachments;
    private View layoutMembers;
    private View layoutTodoList;
    private View layoutPrivilege;
    private View imgCollapseIcon;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_workspace;
    }

    @Override
    public void initView() {
        super.initView();

        imgTitle = getViewById(R.id.img_title);
        editWorkspaceTitle = getViewById(R.id.edit_workspace_title);
        editDescriptions = getViewById(R.id.edit_descriptions);
        txtCountLimit = getViewById(R.id.txt_count_limit);

        imgAttachments = getViewById(R.id.img_attachments);
        imgMembers = getViewById(R.id.img_members);
        imgTodoList = getViewById(R.id.img_todo_list);
        imgPrivilege = getViewById(R.id.img_privilege);
        layoutAttachments = getViewById(R.id.layout_attachments);
        layoutMembers = getViewById(R.id.layout_members);
        layoutTodoList = getViewById(R.id.layout_todo_list);
        layoutPrivilege = getViewById(R.id.layout_privilege);
        imgCollapseIcon = getViewById(R.id.img_collapse_icon);

        imgTitle.setOnClickListener(this);
        imgAttachments.setOnClickListener(this);
        imgMembers.setOnClickListener(this);
        imgTodoList.setOnClickListener(this);
        imgPrivilege.setOnClickListener(this);
        layoutAttachments.setOnClickListener(this);
        layoutMembers.setOnClickListener(this);
        layoutTodoList.setOnClickListener(this);
        layoutPrivilege.setOnClickListener(this);
        imgCollapseIcon.setOnClickListener(this);
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    public void requestData() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_title:

                Intent intent = new Intent(this, SelectPhotosActivity.class);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.putExtra(MediaData.EXTRA_USE_UNIVERSAL, true);
                startActivityForResult(intent, Constant.INTENT_REQUEST_HEAD_PHOTO);
                break;
            case R.id.img_attachments:
            case R.id.layout_attachments:
                break;
            case R.id.img_members:
            case R.id.layout_members:
                break;
            case R.id.img_todo_list:
            case R.id.layout_todo_list:
                break;
            case R.id.img_privilege:
            case R.id.layout_privilege:
                break;
            case R.id.img_collapse_icon:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.INTENT_REQUEST_HEAD_PHOTO:
                Uri uri = data.getData();
                ImageLoader.getInstance().displayImage(uri.toString(), imgTitle,
                        UniversalImageLoaderUtil.options);
                break;
        }
    }
}
