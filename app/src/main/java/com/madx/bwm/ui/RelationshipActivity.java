package com.madx.bwm.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.madx.bwm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * add member flow part 2, part 1 is AddMemberWorkFlow
 */
public class RelationshipActivity extends BaseActivity {

    ListView lvRelationship;

    List<String> data = new ArrayList<String>();

    private int result = RESULT_CANCELED;
    Intent intent = new Intent();

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

        getData();

        lvRelationship = getViewById(R.id.lv_relationship);

        lvRelationship.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data));

        lvRelationship.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("relationship", data.get(position));
//                Log.d("", "data---->" + data.get(position));
                result = RESULT_OK;
//                setResult(result, intent);
                finish();
            }
        });
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

    private List<String> getData(){
        String [] ralationArray=getResources().getStringArray(R.array.relationship_item);
        for (int i=0;i<ralationArray.length;i++){
            data.add(ralationArray[i]);
        }
        return data;
    }
}
