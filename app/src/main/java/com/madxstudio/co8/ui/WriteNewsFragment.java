package com.madxstudio.co8.ui;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.EditDiaryAdapter;
import com.madxstudio.co8.adapter.HeadHolder;
import com.madxstudio.co8.adapter.VideoHolder;
import com.madxstudio.co8.adapter.WriteNewAdapter;
import com.madxstudio.co8.entity.DiaryPhotoEntity;
import com.madxstudio.co8.entity.GroupEntity;
import com.madxstudio.co8.entity.PushedPhotoEntity;
import com.madxstudio.co8.entity.UserEntity;
import com.madxstudio.co8.entity.WallEntity;
import com.madxstudio.co8.interfaces.ImagesRecyclerListener;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.util.UniversalImageLoaderUtil;
import com.madxstudio.co8.widget.MyDialog;
import com.madxstudio.co8.widget.WallEditView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quankun on 16/1/25.
 */
public class WriteNewsFragment extends BaseFragment<WriteNewsActivity> implements View.OnClickListener {
    private View rl_category;
    private MyDialog categoryDialog;
    private TextView category_tv;
    private RecyclerView rvImages;
    private Context mContext;

    public static WriteNewsFragment newInstance(String... params) {

        return createInstance(new WriteNewsFragment());
    }

    public WriteNewsFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.activity_write_new;
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        mContext=getActivity();
        rl_category = getViewById(R.id.rl_category);
        category_tv = getViewById(R.id.category_tv);
        rvImages = getViewById(R.id.rcv_post_photos);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvImages.setLayoutManager(llm);

        rl_category.setOnClickListener(this);
    }

    /**
     * 返回键监听
     *
     * @return
     */
    public boolean backCheck() {
//        if(isEventDate()) {
//            showSaveAlert();
//            return true;
//        } else {
//            getParentActivity().finish();
//            return false;
//        }
        return false;
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onClick(View view) {
        switch ((view.getId())) {
            case R.id.rl_category:
                showCateGoryDialog();
                break;
        }
    }

    private void showCateGoryDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View categoryView = factory.inflate(R.layout.meeting_reminder_list, null);
        ListView listView = (ListView) categoryView.findViewById(R.id.reminder_list_view);
        String[] reminderArrayUs = getActivity().getResources().getStringArray(R.array.category_item);
        final List<String> list = Arrays.asList(reminderArrayUs);
        ArrayAdapter reminderAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(reminderAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categoryDialog.dismiss();
                category_tv.setText(list.get(i));
            }
        });
        categoryDialog = new MyDialog(getParentActivity(), "", categoryView);
        if (!categoryDialog.isShowing()) {
            categoryDialog.show();
        }

    }
}
