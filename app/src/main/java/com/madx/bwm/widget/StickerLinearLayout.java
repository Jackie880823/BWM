package com.madx.bwm.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.madx.bwm.R;
import com.madx.bwm.adapter.AlbumDetailAdapter;
import com.madx.bwm.adapter.StickerHorizontalRecyclerAdapter;
import com.madx.bwm.dao.LocalStickerInfoDao;
import com.madx.bwm.entity.AlbumPhotoEntity;
import com.madx.bwm.interfaces.StickerViewClickListener;
import com.madx.bwm.ui.MainActivity;
import com.madx.bwm.util.AnimatedGifDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StickerLinearLayout extends LinearLayout {
    private Context mContext;
    private View rootView;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private LinearLayout layout;
    private LinearLayoutManager linearLayoutManager;
    private StickerHorizontalRecyclerAdapter recyclerAdapter;
    private List<String> stickerList;
    private static List<List<String>> STICKER_LIST = new ArrayList<>();
    private int index = 0;
    private LinearLayout mNumLayout;
    public static final int SHOW_NUM = 6;
    private StickerViewPagerAdapter adapter;
    private int clickPosition;

    public StickerLinearLayout(Context context) {
        super(context);
    }

    public StickerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.activity_new_sticker, null);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.sticker_recyclerView);
        layout = (LinearLayout) rootView.findViewById(R.id.sticker_setting_linear);
        mNumLayout = (LinearLayout) rootView.findViewById(R.id.fragment_sticker_linear);
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (STICKER_LIST.size() == 0) {
            stickerList = LocalStickerInfoDao.getInstance(mContext).queryAllSticker();
            for (String string : stickerList) {
                String path = MainActivity.STICKERS_NAME + File.separator + string;
                File file = new File(path);
                File[] files = file.listFiles();
                if (null != files && files.length > 0) {
                    List<String> list = new ArrayList<>();
                    for (File file1 : files) {
                        String filePath = file1.getAbsolutePath();
                        if (filePath.substring(filePath.lastIndexOf(File.separator) + 1).contains("B")) {
                            list.add(filePath);
                        }
                    }
                    STICKER_LIST.add(list);
                }
            }
        }

        recyclerAdapter = new StickerHorizontalRecyclerAdapter(STICKER_LIST, mContext);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setPicClickListener(new StickerHorizontalRecyclerAdapter.StickerItemClickListener() {
            @Override
            public void showOriginalPic(int position) {
                clickPosition = position;
            }
        });
        List<NoScrollGridView> mLists = init(STICKER_LIST.get(clickPosition));
        adapter = new StickerViewPagerAdapter(mLists);
        viewPager.setAdapter(adapter);
        setPage(getBitmapPathList(clickPosition).size());
    }

    private void setPage(int clickSize) {
        int count = (clickSize % SHOW_NUM) == 0 ? (clickSize / SHOW_NUM) : (clickSize / SHOW_NUM + 1);
        for (int i = 0; i < count; i++) {
            TextView tv = new TextView(mContext);
            if (i == 0) {
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


    private List<String> getBitmapPathList(int position) {
        return STICKER_LIST.get(position);
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
            for (int i = 0; i < viewPager.getCurrentItem(); i++) {
                TextView currentBt = (TextView) mNumLayout.getChildAt(i);
                if (currentBt == null) {
                    return;
                }
                if (arg0 == i) {
                    currentBt.setBackgroundResource(R.drawable.bg_num_gray_press_message);
                } else {
                    currentBt.setBackgroundResource(R.drawable.bg_num_gray_message);
                }
            }
            index = arg0;
        }

    }

    class StickerViewPagerAdapter extends PagerAdapter {
        private List<NoScrollGridView> mLists;

        public StickerViewPagerAdapter(List<NoScrollGridView> array) {
            this.mLists = array;
        }

        public void setNewData(List<NoScrollGridView> array){
            if(array!=null&&array.size()>0){
                mLists.clear();
                mLists.addAll(array);
                notifyDataSetChanged();
            }
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

    private List<NoScrollGridView> init(final List<String> pathList) {

        List<NoScrollGridView> mLists = new ArrayList<>();
        NoScrollGridView gv;
        int count = (pathList.size() % SHOW_NUM) == 0 ? (pathList.size() / SHOW_NUM) : (pathList.size() / SHOW_NUM + 1);
        for (int i = 0; i < count; i++) {
            gv = new NoScrollGridView(mContext);
            gv.setAdapter(new StickerGridViewAdapter(mContext, pathList, i));
            gv.setGravity(Gravity.CENTER);
            gv.setClickable(true);
            gv.setFocusable(true);
            gv.setNumColumns(3);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    int stickerNameId = index * SHOW_NUM + arg2;
                    String fileName = pathList.get(stickerNameId);
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
                        mViewClickListener.showComments(type, selectStickerName, sticker_name);
                    }
                }
            });
            mLists.add(gv);
        }
        return mLists;
    }

    public StickerViewClickListener mViewClickListener;

    public void setPicClickListener(StickerViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }

    class StickerGridViewAdapter extends BaseAdapter {
        private List<String> stringList = new ArrayList<>();
        private Context mContext;

        public StickerGridViewAdapter(Context mContext, List<String> list, int spot) {
            this.mContext = mContext;
            int i = spot * SHOW_NUM;
            int end = i + SHOW_NUM;
            while ((i < list.size()) && (i < end)) {
                stringList.add(list.get(i));
                i++;
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
