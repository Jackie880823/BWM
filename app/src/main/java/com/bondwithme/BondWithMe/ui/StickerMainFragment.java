package com.bondwithme.BondWithMe.ui;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.StickerHorizontalRecyclerAdapter;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.interfaces.StickerViewClickListener;
import com.bondwithme.BondWithMe.ui.more.sticker.StickerStoreActivity;
import com.bondwithme.BondWithMe.util.AnimatedGifDrawable;
import com.bondwithme.BondWithMe.util.AudioPlayUtils;
import com.bondwithme.BondWithMe.widget.NoScrollGridView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class StickerMainFragment extends BaseFragment<MainActivity> {
    private Context mContext;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private LinearLayout layout;
    private LinearLayoutManager linearLayoutManager;
    private StickerHorizontalRecyclerAdapter recyclerAdapter;
    private LinkedHashMap<String, List<String>> STICKER_LIST_MAP = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> stickerMap = new LinkedHashMap<>();
    private LinearLayout mNumLayout;
    public static final int SHOW_NUM = 6;
    private StickerViewPagerAdapter adapter;
    private static final int CLICK_POSITION = 0X11;
    private static final int GET_DATA = 0X12;
    private LinkedHashMap<Integer, String> positionMap = new LinkedHashMap<>();
    private LinkedHashMap<Integer, StickerGridViewAdapter> adapterMap = new LinkedHashMap<>();
    private int lastPage;
    private PopupWindow popupWindow;

    private void setPage(String clickName, int position) {
        mNumLayout.removeAllViews();
        List<Integer> list = new ArrayList<>();
        for (Integer key : positionMap.keySet()) {
            if (clickName.equals(positionMap.get(key))) {
                list.add(key);
            }
        }
        if (list.size() == 0) {
            return;
        }
        boolean isLeft = false;
        if (position == list.get(0)) {
            isLeft = true;
        }
        int count = list.size();
        for (int i = 0; i < count; i++) {
            TextView tv = new TextView(mContext);
            if (isLeft && i == 0) {
                tv.setBackgroundResource(R.drawable.bg_num_gray_press_message);
            } else if (!isLeft && i == count - 1) {
                tv.setBackgroundResource(R.drawable.bg_num_gray_press_message);
            } else {
                tv.setBackgroundResource(R.drawable.bg_num_gray_message);
            }
            LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(20, 20);
            mLayoutParams.leftMargin = 10;
            mNumLayout.addView(tv, mLayoutParams);
        }
        viewPager.setOnPageChangeListener(new MyOnPageChanger());
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.activity_new_sticker;
    }

    Thread myThread = new Thread() {
        @Override
        public void run() {
            super.run();
            List<String> stickerList = LocalStickerInfoDao.getInstance(mContext).queryAllSticker();
            for (String string : stickerList) {
                if ("stickers".equals(MainActivity.STICKERS_NAME)) {
                    MainActivity.STICKERS_NAME = new LocalStickerInfoDao(mContext).getSavePath();
                }
                String path = MainActivity.STICKERS_NAME + File.separator + string;
                File file = new File(path);
                File[] files = file.listFiles();
                if (null != files && files.length > 0) {
                    List<String> list = new ArrayList<>();
                    for (File file1 : files) {
                        String filePath = file1.getAbsolutePath();
                        if ((filePath.substring(0, filePath.lastIndexOf("."))).endsWith("S")) {
                            list.add(filePath);
                        }
                    }
                    STICKER_LIST_MAP.put(string, list);
                }
            }
            handler.sendEmptyMessage(GET_DATA);
        }
    };

    @Override
    public void initView() {
        mContext = getActivity();
        viewPager = getViewById(R.id.viewpager);
        recyclerView = getViewById(R.id.sticker_recyclerView);
        layout = getViewById(R.id.sticker_setting_linear);
        mNumLayout = getViewById(R.id.fragment_sticker_linear);
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioPlayUtils.stopAudio();
                startActivity(new Intent(getActivity(), StickerStoreActivity.class));
            }
        });
        myThread.start();
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
                return false;
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLICK_POSITION:
                    String name = (String) msg.obj;
                    setPage(name, stickerMap.get(name));
                    viewPager.setCurrentItem(stickerMap.get(name));
                    break;
                case GET_DATA:
                    recyclerAdapter = new StickerHorizontalRecyclerAdapter(STICKER_LIST_MAP, mContext, linearLayoutManager);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.setPicClickListener(new StickerHorizontalRecyclerAdapter.StickerItemClickListener() {
                        @Override
                        public void showOriginalPic(String positionName) {
                            Message.obtain(handler, CLICK_POSITION, positionName).sendToTarget();
                        }
                    });
                    List<NoScrollGridView> mLists = init();
                    adapter = new StickerViewPagerAdapter(mLists);
                    viewPager.setAdapter(adapter);
                    setPage(recyclerAdapter.getFirstStickerName(), 0);
                    break;
            }
        }
    };

    @Override
    public void requestData() {

    }

    class MyOnPageChanger implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            String nowName = positionMap.get(arg0);
            String lastName = positionMap.get(lastPage);
            List<String> stringList = STICKER_LIST_MAP.get(nowName);
            if (adapterMap.get(arg0).getCount() == 0) {
                adapterMap.get(arg0).addData(stringList);
            }
            if (lastName.equals(nowName)) {
                List<Integer> list = new ArrayList<>();
                for (Integer key : positionMap.keySet()) {
                    if (nowName.equals(positionMap.get(key))) {
                        list.add(key);
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    TextView currentBt = (TextView) mNumLayout.getChildAt(i);
                    if (currentBt == null) {
                        return;
                    }
                    if (arg0 == list.get(i)) {
                        currentBt.setBackgroundResource(R.drawable.bg_num_gray_press_message);
                    } else {
                        currentBt.setBackgroundResource(R.drawable.bg_num_gray_message);
                    }
                }
            } else {
                setPage(nowName, arg0);
                recyclerAdapter.setScrollPosition(nowName);
            }
            lastPage = arg0;
        }

    }

    class StickerViewPagerAdapter extends PagerAdapter {
        private List<NoScrollGridView> mLists;

        public StickerViewPagerAdapter(List<NoScrollGridView> array) {
            this.mLists = array;
        }

        @Override
        public int getCount() {
            return mLists.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {

            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mLists.get(arg1));
            return mLists.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

    }

    private int getCount(int length) {
        return (length % SHOW_NUM) == 0 ? (length / SHOW_NUM) : (length / SHOW_NUM + 1);
    }

    private List<NoScrollGridView> init() {
        List<NoScrollGridView> mLists = new ArrayList<>();
        int page = 0;
        int position = 0;
        for (Map.Entry<String, List<String>> entry : STICKER_LIST_MAP.entrySet()) {
            List<String> list = entry.getValue();
            if (null != list && list.size() > 0) {
                stickerMap.put(entry.getKey(), page);
                int count = getCount(list.size());
                page += count;
                NoScrollGridView gv;
                for (int i = 0; i < count; i++) {
                    positionMap.put(position, entry.getKey());
                    gv = new NoScrollGridView(mContext);
                    List<String> dataList = new ArrayList<>();
                    if (i == 0) {
                        dataList.addAll(list);
                    }
                    final StickerGridViewAdapter gridViewAdapter = new StickerGridViewAdapter(mContext, dataList, i);
                    adapterMap.put(position, gridViewAdapter);
                    position++;
                    gv.setAdapter(gridViewAdapter);
                    gv.setGravity(Gravity.CENTER);
                    gv.setClickable(true);
                    gv.setFocusable(true);
                    gv.setNumColumns(SHOW_NUM / 2);
                    gv.setHorizontalSpacing((int) mContext.getResources().getDimension(R.dimen._20dp));
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            String fileName = (String) gridViewAdapter.getItem(arg2);//.get(stickerNameId);
                            String selectStickerName = fileName.substring(0, fileName.lastIndexOf(File.separator));
                            selectStickerName = selectStickerName.substring(selectStickerName.lastIndexOf(File.separator) + 1);
                            String type = ".gif";
                            if (fileName.indexOf(".") != -1) {
                                type = fileName.substring(fileName.lastIndexOf("."));
                            }
                            String sticker_name = "";
                            if (fileName.indexOf("_") != -1) {
                                sticker_name = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf("_"));
                            }
                            if (mViewClickListener != null) {
                                AudioPlayUtils.stopAudio();
                                mViewClickListener.showComments(type, selectStickerName, sticker_name);
                            }
                        }
                    });

                    gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            String fileName = (String) gridViewAdapter.getItem(position);
                            showPopuWindow(fileName, view);
                            return true;
                        }
                    });
                    gv.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (popupWindow != null && popupWindow.isShowing()) {
                                    popupWindow.dismiss();
                                }
                            }
                            return false;
                        }
                    });
//                    gv.setOnScrollListener(new AbsListView.OnScrollListener() {
//                        @Override
//                        public void onScrollStateChanged(AbsListView view, int scrollState) {
//                            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                                if (popupWindow != null && popupWindow.isShowing()) {
//                                    popupWindow.dismiss();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                        }
//                    });
                    mLists.add(gv);
                }
            }
        }

        return mLists;
    }

    private void showPopuWindow(String filePath, View view) {
        View popView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popupwindow_sticker_imageview, null);
        popupWindow = new PopupWindow(popView, AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        ImageView iv = (ImageView) popView.findViewById(R.id.sticker_imageView);
        GifImageView gif_iv = (GifImageView) popView.findViewById(R.id.gif_iv);
        try {
            File file = new File(filePath);
            if (filePath.endsWith("gif")) {
                gif_iv.setVisibility(View.VISIBLE);
                iv.setVisibility(View.GONE);
                GifDrawable gifDrawable = new GifDrawable(file);
                if (gifDrawable != null) {
                    gif_iv.setImageDrawable(gifDrawable);
                }
            } else {
                iv.setVisibility(View.VISIBLE);
                gif_iv.setVisibility(View.GONE);
                try {
                    InputStream is = new FileInputStream(file);
                    if (is != null) {//如果有图片直接显示，否则网络下载
                        Bitmap bitmap = BitmapFactory.decodeStream(is);//将流转化成Bitmap对象
                        iv.setImageBitmap(bitmap);//显示图片
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int[] arrayOfInt = new int[2];
        //获取点击按钮的坐标
        view.getLocationOnScreen(arrayOfInt);
        int x = arrayOfInt[0];
        int y = arrayOfInt[1];
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = popView.getMeasuredWidth();
        int popupHeight = popView.getMeasuredHeight();
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, (x + view.getWidth() / 2) - popupWidth / 2,
                y - popupHeight);
    }

    public StickerViewClickListener mViewClickListener;

    public void setPicClickListener(StickerViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }

    class StickerGridViewAdapter extends BaseAdapter {
        private List<String> stringList = new ArrayList<>();
        private Context mContext;
        private int position;

        public StickerGridViewAdapter(Context mContext, List<String> list, int spot) {
            this.mContext = mContext;
            position = spot;
            if (null != list && list.size() > 0) {
                int i = spot * SHOW_NUM;
                int end = i + SHOW_NUM;
                while ((i < list.size()) && (i < end)) {
                    stringList.add(list.get(i));
                    i++;
                }
            }
        }

        public void addData(List<String> list) {
            stringList.clear();
            if (null != list && list.size() > 0) {
                int i = position * SHOW_NUM;
                int end = i + SHOW_NUM;
                while ((i < list.size()) && (i < end)) {
                    stringList.add(list.get(i));
                    i++;
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return stringList.size();
        }

        @Override
        public Object getItem(int position) {
            return stringList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_sticker_imageview, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.sticker_imageView);
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen._90dp));
                convertView.setLayoutParams(layoutParams);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            try {
                String filePath = stringList.get(position);
//                InputStream inputStream = mContext.getAssets().open(filePath);
                File file = new File(filePath);
                InputStream inputStream = new FileInputStream(file);
                if (filePath.endsWith("gif")) {
                    AnimatedGifDrawable animatedGifDrawable = new AnimatedGifDrawable(mContext.getResources(), 0, inputStream, null);
                    Drawable drawable = animatedGifDrawable.getDrawable();
                    viewHolder.imageView.setImageDrawable(drawable);
                } else {
                    viewHolder.imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
        }
    }
}
