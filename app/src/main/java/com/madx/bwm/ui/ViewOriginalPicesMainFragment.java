package com.madx.bwm.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.madx.bwm.Constant;
import com.madx.bwm.R;
import com.madx.bwm.adapter.MyFragmentPagerAdapter;
import com.madx.bwm.adapter.ViewPicAdapter;
import com.madx.bwm.entity.PhotoEntity;
import com.madx.bwm.widget.ViewPagerFixed;

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
    public void initView() {
        rvList = (RecyclerView) getViewById(R.id.small_image_view_container);
        tvIndexOfList = (TextView) getViewById(R.id.tv_index_count);
        llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvList.setLayoutManager(llm);
        //        got_data_in = getArguments().getBoolean("is_data",false);
        request_url = getArguments().getString("request_url");
        if(getArguments() == null || TextUtils.isEmpty(request_url)) {
            got_data_in = true;

        } else {
            got_data_in = false;
            if(data != null)
                data.clear();
        }
        view_paper = (ViewPagerFixed) getViewById(R.id.view_paper);
        viewPaperAdapter = new MyFragmentPagerAdapter(getFragmentManager(), getActivity(), view_paper, fragments);

        viewPaperAdapter.setOnMyPageChangeListenner(new MyFragmentPagerAdapter.OnPageChangeListenner() {
            @Override
            public void onPageChange(int position) {
                String text = null;
                int count = data.size();
                Log.i(TAG, "onPageChange& position = " + position + "; count = " + count);
                if(count == 1) {
                    text = getActivity().getString(R.string.photo_position_no_arrow);
                } else if(position <= 0) {
                    text = getActivity().getString(R.string.photo_position_right_arrow);
                } else if(position == count - 1) {
                    text = getActivity().getString(R.string.photo_position_left_arrow);
                } else {
                    text = getActivity().getString(R.string.photo_position_double_arrow);
                }
                tvIndexOfList.setText(String.format(text, position + 1, data.size()));
            }
        });
        view_paper.setOffscreenPageLimit(0);
//        view_paper.setOffscreenPageLimit(1);
    }

    //    private void setViewPaperItems(int mainIndex) {
    //        if (mainIndex >= 0) {
    //            currentIndex = mainIndex;
    //            if (mainIndex > 0) {
    //                leftIndex = mainIndex - 1;
    //            } else {
    //                leftIndex = -1;
    //            }
    //            if (mainIndex < (data.size() - 1)) {
    //                rightIndex = mainIndex + 1;
    //            } else {
    //                rightIndex = -1;
    //            }
    //        }
    //    }

    @Override
    public void requestData() {
        if(got_data_in) {
            //默认第一张
            initAdapter();
            initViewPaper(0);
        } else {
            if(TextUtils.isEmpty(request_url)) {
                getActivity().finish();
                return;
            }

            new HttpTools(getActivity()).get(request_url, null, new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onResult(String response) {
                    GsonBuilder gsonb = new GsonBuilder();
                    //Json中的日期表达方式没有办法直接转换成我们的Date类型, 因此需要单独注册一个Date的反序列化类.
                    //DateDeserializer ds = new DateDeserializer();
                    //给GsonBuilder方法单独指定Date类型的反序列化方法
                    //gsonb.registerTypeAdapter(Date.class, ds);
                    Gson gson = gsonb.create();
                    data = gson.fromJson(response, new TypeToken<ArrayList<PhotoEntity>>() {}.getType());
                    //默认第一张
                    initAdapter();
                    initViewPaper(0);
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
        if(data != null && !data.isEmpty()) {
            int count = data.size();
            for(int i = 0; i < count; i++) {
                fragments.add(generatePicFragment(data.get(i)));
            }
            viewPaperAdapter.notifyDataSetChanged();

            tvIndexOfList.setVisibility(View.VISIBLE);
            String text = null;
            if(count == 1) {
                text = getActivity().getString(R.string.photo_position_no_arrow);
            } else {
                text = getActivity().getString(R.string.photo_position_right_arrow);
            }
            tvIndexOfList.setText(String.format(text, dataIndex + 1, count));
        } else {
            //
            tvIndexOfList.setVisibility(View.GONE);
        }
    }

    private Fragment generatePicFragment(PhotoEntity photoEntity) {
        String picUrl = String.format(Constant.API_GET_PIC, Constant.Module_Original, photoEntity.getUser_id(), photoEntity.getFile_id());
        ViewPicFragment viewPic = new ViewPicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("pic_url", picUrl);
        viewPic.setArguments(bundle);
        return viewPic;
    }

    private void initAdapter() {
        if(data != null && !data.isEmpty()) {
            adapter = new ViewPicAdapter(getActivity(), data);
            rvList.setAdapter(adapter);
            //                big_url = String.format(Constant.API_GET_PIC, Constant.Module_preview_m, data.get(0).getUser_id(), data.get(0).getFile_id());
            //                if (!TextUtils.isEmpty(big_url)) {
            //                    watting_progressBar.setVisibility(View.VISIBLE);
            //                    VolleyUtil.loadImage(ViewOriginalPicesActivity.this, big_url, new ImageLoader.ImageListener() {
            //                        @Override
            //                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            //                            imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), response.getBitmap()));
            //                            watting_progressBar.setVisibility(View.GONE);
            //                        }
            //
            //                        @Override
            //                        public void onErrorResponse(VolleyError error) {
            //                            watting_progressBar.setVisibility(View.GONE);
            //                        }
            //                    });
            //                }
            adapter.setItemClickListenner(new ViewPicAdapter.ItemClickListenner() {

                @Override
                public void onItemClick(Drawable smallPic, PhotoEntity photoEntity, int position) {
                    //set the small pic first
                    //                        imageSwitcher.setImageDrawable(drawable);
                    //                        watting_progressBar.setVisibility(View.VISIBLE);
                    //                        big_url = String.format(Constant.API_GET_PIC, Constant.Module_preview_xl, photoEntity.getUser_id(), photoEntity.getFile_id());
                    //                        VolleyUtil.loadImage(ViewOriginalPicesActivity.this, big_url, new ImageLoader.ImageListener() {
                    //                            @Override
                    //                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    //                                //change to big pic
                    //                                imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), response.getBitmap()));
                    //                                watting_progressBar.setVisibility(View.GONE);
                    //                            }
                    //
                    //                            @Override
                    //                            public void onErrorResponse(VolleyError error) {
                    //                                watting_progressBar.setVisibility(View.GONE);
                    //                            }
                    //                        });
                    if(currentId != position) {
                        view_paper.setCurrentItem(position);
                        currentId = position;
                    }


                    //                        initViewPaper(position);
                }
            });
            adapter.notifyDataSetChanged();

        }

    }

}
