package com.madxstudio.co8.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.RecommendAdapter;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.exception.RelationshipException;
import com.madxstudio.co8.util.RelationshipUtil;
import com.madxstudio.co8.widget.MyDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * add member flow part 2, part 1 is AddMemberWorkFlow
 */
public class RelationshipActivity extends BaseActivity {

    private String TAG = "RelationshipActivity";
    ListView lvRelationship;

    List<String> data = new ArrayList<String>();

    private int result = RESULT_CANCELED;

    Intent intent = new Intent();

    Intent intentFromRecommend = new Intent();
    private MyDialog confirmDialog;
    private String whichActivity = "";
    List<String> data_Zh = new ArrayList<String>();
    List<String> data_Us = new ArrayList<String>();
    private boolean isZh;

    @Override
    public int getLayout() {
        return R.layout.activity_relationship;
    }

    @Override
    protected void titleLeftEvent() {
//        setResult(result, intent);
        super.titleLeftEvent();
    }

    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(getString(R.string.title_select_relationship));
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        getDataEn();
        if (Locale.getDefault().toString().equals("zh_CN")) {
            isZh = true;
            getDataZh();
        }

        intentFromRecommend = getIntent();
        if (intentFromRecommend != null) {
            whichActivity = getIntent().getStringExtra(RecommendActivity.TAG);
        }

        getData();

        lvRelationship = getViewById(R.id.lv_relationship);

        lvRelationship.setAdapter(new RelationShipAdapter());

        lvRelationship.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (whichActivity != null && whichActivity.equals(RecommendAdapter.From_RecommendActivity)) {
                    showConfirmDialog(position);
                    return;
                }

                try {
                    intent.putExtra("relationship", RelationshipUtil.getRelationshipValue(RelationshipActivity.this, position));//标准英文
                } catch (RelationshipException e) {
                    e.printStackTrace();
                }
                intent.putExtra("selectMemeber", position);  //下标

                result = RESULT_OK;
                finish();
            }
        });
    }

    class RelationShipAdapter extends BaseAdapter {
        public RelationShipAdapter() {
        }

        @Override
        public int getCount() {
            if (data == null) {
                return 0;
            }
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            if (data == null) {
                return null;
            }
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(RelationshipActivity.this).inflate(R.layout.relationship_list_item, null);
            UserEntity userEntity = MainActivity.getUser();
            TextView tv = (TextView) view.findViewById(R.id.tv_relation);
            tv.setText(data.get(position));
            if (userEntity != null && !TextUtils.isEmpty(userEntity.getOrganisation())) {
                if (position == 0 || position == 1 || position == 2) {
                    tv.setVisibility(View.GONE);
                }
            }
            return view;
        }
    }

    private void showConfirmDialog(final int position) {
        getDataEn();
        if (Locale.getDefault().toString().equals("zh_CN")) {
            isZh = true;
            getDataZh();
        }

        String relationship = data_Us.get(position);
        if (TextUtils.isEmpty(relationship)) {
//            continue;
        }
        if (isZh) {
            int index = data_Us.indexOf(relationship);
            if (index != -1) {
                relationship = getDataZh().get(index);
                confirmDialog = new MyDialog(this, this.getString(R.string.text_tips_title), this.getString(R.string.text_confirm_relation) + relationship + " ?");
            }
        } else {
            confirmDialog = new MyDialog(this, this.getString(R.string.text_tips_title), this.getString(R.string.text_confirm_relation) + " " + relationship + " ?");
        }

        confirmDialog.setButtonAccept(this.getString(R.string.text_yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("relationship", data_Us.get(position));
                intent.putExtra("selectMemeber", position);
                result = RESULT_OK;
                finish();
            }
        });

        confirmDialog.setButtonCancel(this.getString(R.string.text_dialog_no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });

        if (confirmDialog != null && !confirmDialog.isShowing()) {
            confirmDialog.show();
        }
    }

    @Override
    public void finish() {
        setResult(result, intent);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        setResult(result, intent);
        super.onBackPressed();
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private List<String> getData() {
        String[] ralationArray = getResources().getStringArray(R.array.relationship_item);
        for (int i = 0; i < ralationArray.length; i++) {
            data.add(ralationArray[i]);
        }
        return data;
    }

    private List<String> getDataZh() {
        Configuration configuration = new Configuration();
        //设置应用为简体中文
        configuration.locale = Locale.SIMPLIFIED_CHINESE;
        this.getResources().updateConfiguration(configuration, null);
        String[] ralationArrayZh = this.getResources().getStringArray(R.array.relationship_item);
        data_Zh = Arrays.asList(ralationArrayZh);
        return data_Zh;
    }

    private List<String> getDataEn() {
        Configuration configuration = new Configuration();
        //设置应用为英文
        configuration.locale = Locale.US;
        this.getResources().updateConfiguration(configuration, null);
        String[] ralationArrayUs = this.getResources().getStringArray(R.array.relationship_item);
        data_Us = Arrays.asList(ralationArrayUs);
        return data_Us;
    }

}
