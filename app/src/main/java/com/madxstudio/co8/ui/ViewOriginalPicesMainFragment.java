package com.madxstudio.co8.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.adapter.MyFragmentPagerAdapter;
import com.madxstudio.co8.adapter.ViewPicAdapter;
import com.madxstudio.co8.entity.PhotoEntity;
import com.madxstudio.co8.widget.ViewPagerFixed;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wing on 15/3/23.
 */
public class ViewOriginalPicesMainFragment extends BaseFragment {

    /**
     * 当前类LGO信息的TAG，打印调试信息时用于识别输出LOG所在的类
     */
    private final static String TAG = ViewOriginalPicesMainFragment.class.getSimpleName();

    /**
     * 小图预览列表
     */
    private RecyclerView rvList;

    /**
     * 图片浏览进度提示: 当前图片位置/总图片数
     */
    private TextView tvIndexOfList;

    /**
     * 水平布局
     */
    LinearLayoutManager llm;
    private String request_url;
    private static List<PhotoEntity> data;
    private ViewPicAdapter adapter;
    private ViewPagerFixed view_paper;
    private MyFragmentPagerAdapter viewPaperAdapter;
    private List<Fragment> fragments = new ArrayList<>();
    private int currentId;
    private String memberId;

    /**
     * 标识是否传入数据(不需请求)
     */
    private static boolean got_data_in;


    @Override
    public void onDestroy() {
        super.onDestroy();
        got_data_in = false;
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_view_pices_main;
    }

    /**
     * 只用于数据实体传入
     * 单张图片的时候
     *
     * @param inDatas
     * @return
     */
    public static ViewOriginalPicesMainFragment newInstance(List<PhotoEntity> inDatas) {
        got_data_in = true;
        data = inDatas;
        return createInstance(new ViewOriginalPicesMainFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        rvList = (RecyclerView) getViewById(R.id.small_image_view_container);
        tvIndexOfList = (TextView) getViewById(R.id.tv_index_count);
        llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvList.setLayoutManager(llm);
        rvList.setItemAnimator(null);
        //        got_data_in = getArguments().getBoolean("is_data",false);
        request_url = getArguments().getString(Constant.REQUEST_URL);
        memberId = getArguments().getString("memberId");
        if (getArguments() == null || TextUtils.isEmpty(request_url)) {
            got_data_in = true;

        } else {
            got_data_in = false;
            if (data != null)
                data.clear();
        }
        view_paper = (ViewPagerFixed) getViewById(R.id.view_paper);
        viewPaperAdapter = new MyFragmentPagerAdapter(getFragmentManager(), getActivity(),
                view_paper, fragments);

        viewPaperAdapter.setOnMyPageChangeListenner(new MyFragmentPagerAdapter
                .OnPageChangeListenner() {
            @Override
            public void onPageChange(int position) {
                String text;
                int count = data.size();
                Log.i(TAG, "onPageChange& position = " + position + "; count = " + count);
                if (count == 1) {
                    text = getActivity().getString(R.string.photo_position_no_arrow);
                } else if (position <= 0) {
                    text = getActivity().getString(R.string.photo_position_right_arrow);
                } else if (position == count - 1) {
                    text = getActivity().getString(R.string.photo_position_left_arrow);
                } else {
                    text = getActivity().getString(R.string.photo_position_double_arrow);
                }
                currentId = position;

                // 如果TextView是GONE状态setText()会出异常，且需要显示调用setText()才有意义
                if (tvIndexOfList.getVisibility() != View.VISIBLE) {
                    tvIndexOfList.setVisibility(View.VISIBLE);
                }
                tvIndexOfList.setText(String.format(text, position + 1, data.size()));
            }
        });
        view_paper.setOffscreenPageLimit(0);
    }

    @Override
    public void requestData() {
        //传入数据或者
        if (got_data_in) {
            //默认第一张
            initAdapter();
            initViewPaper(currentId);
        } else {
            if (TextUtils.isEmpty(request_url)) {
                getActivity().finish();
                return;
            }

            new HttpTools(getActivity()).get(request_url, null, this, new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onResult(String response) {
                    try {
                        GsonBuilder gsonb = new GsonBuilder();
                        //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                        //DateDeserializer ds = new DateDeserializer();
                        //给GsonBuilder方法单独指定Date类型的反序列化方法
                        //gsonb.registerTypeAdapter(Date.class, ds);
                        Gson gson = gsonb.create();
//                        if (response.startsWith("{\"data\":")) {
                        JSONObject jsonObject = new JSONObject(response);
                        String dataString = jsonObject.optString("data");
                        data = gson.fromJson(dataString, new TypeToken<ArrayList<PhotoEntity>>() {
                        }.getType());
//                        } else {
//                            data = gson.fromJson(response, new
// TypeToken<ArrayList<PhotoEntity>>() {
//                            }.getType());
//                        }
                        //默认第一张
                        initAdapter();
                        initViewPaper(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
    }


    /**
     * 初始化
     *
     * @param dataIndex
     */
    private void initViewPaper(int dataIndex) {
        //        setViewPaperItems(dataIndex);
        if (data != null && !data.isEmpty()) {
            int count = data.size();
            for (int i = 0; i < count; i++) {
                fragments.add(generatePicFragment(data.get(i)));
            }
            viewPaperAdapter.notifyDataSetChanged();

            tvIndexOfList.setVisibility(View.VISIBLE);
            String text;
            if (count == 1) {
                text = getActivity().getString(R.string.photo_position_no_arrow);
            } else {
                text = getString(R.string.photo_position_right_arrow);
            }
            tvIndexOfList.setText(String.format(text, dataIndex + 1, count));
            view_paper.setCurrentItem(currentId);
        } else {
            //
            tvIndexOfList.setVisibility(View.GONE);
        }
    }

    private Fragment generatePicFragment(PhotoEntity photoEntity) {
        String userId = photoEntity.getUser_id();
        if (TextUtils.isEmpty(userId)) {
            userId = memberId;
        }
        String picUrl = String.format(Constant.API_GET_PIC, Constant.Module_Original, userId,
                photoEntity.getFile_id());
        ViewPicFragment viewPic = new ViewPicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("pic_url", picUrl);
        viewPic.setArguments(bundle);
        return viewPic;
    }

    private void initAdapter() {
        if (data != null && !data.isEmpty()) {
            adapter = new ViewPicAdapter(getActivity(), data, memberId);
            rvList.setAdapter(adapter);
            adapter.setItemClickListenner(new ViewPicAdapter.ItemClickListenner() {

                @Override
                public void onItemClick(Drawable smallPic, PhotoEntity photoEntity, int position) {
                    if (currentId != position) {
                        view_paper.setCurrentItem(position);
                    }
                }
            });
            adapter.notifyDataSetChanged();

        }

    }

}
