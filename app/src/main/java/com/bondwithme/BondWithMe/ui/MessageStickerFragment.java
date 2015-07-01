package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.interfaces.StickerViewClickListener;
import com.bondwithme.BondWithMe.util.AnimatedGifDrawable;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.widget.NoScrollGridView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MessageStickerFragment extends BaseFragment<MainActivity> {
    private String selectStickerName;
    private Context mContext;
    private StickerViewPagerAdapter adapter;
    private ViewPager mPager;
    private int count;
    private int index = 0;
    private LinearLayout mNumLayout;
    public static final String SHOW_KIND = "B";
    public static final int SHOW_NUM = 6;
    private String type;
    private List<String> stickerPathList;
    //private String stickerFileName;


    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_sticker_layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void initView() {
        Bundle bundle = getArguments();
        selectStickerName = bundle.getString("selectStickerName");
        mContext = getActivity();
        mPager = getViewById(R.id.viewpager);
        mNumLayout = getViewById(R.id.fragment_sticker_linear);
//        if (null != selectStickerName && selectStickerName.indexOf(File.separator) != -1) {
//            stickerFileName = selectStickerName.substring(selectStickerName.lastIndexOf(File.separator) + 1);
//        }
        stickerPathList = getBitmapPathList();
        count = (stickerPathList.size() % SHOW_NUM) == 0 ? (stickerPathList.size() / SHOW_NUM) : (stickerPathList.size() / SHOW_NUM + 1);
        List<NoScrollGridView> mLists = init(stickerPathList);
        adapter = new StickerViewPagerAdapter(mLists);
        mPager.setAdapter(adapter);
        setPage();
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

    private void setPage() {
        for (int i = 0; i < count; i++) {
            TextView tv = new TextView(getActivity());
            if (i == 0) {
                tv.setBackgroundResource(R.drawable.bg_num_gray_press_message);
            } else {
                tv.setBackgroundResource(R.drawable.bg_num_gray_message);
            }
            LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(20, 20);
            mLayoutParams.leftMargin = 10;
            mNumLayout.addView(tv, mLayoutParams);
        }

        mPager.setOnPageChangeListener(new MyOnPageChanger());
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
            for (int i = 0; i < count; i++) {
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

    private List<String> getBitmapPathList() {
        List<String> filePaths = new ArrayList<>();
        String path = MainActivity.STICKERS_NAME + File.separator + selectStickerName;
        File file = new File(path);
        File[] files = file.listFiles();
        if (null != files && files.length > 0) {
            for(File file1:files){
                String filePath=file1.getAbsolutePath();
                if(filePath.substring(filePath.lastIndexOf(File.separator) + 1).contains(SHOW_KIND)){
                    filePaths.add(filePath);
                }
            }
        }
//        List<String> fileNames = FileUtil.getAllFilePathsFromAssets(getActivity(), selectStickerName);
//        List<String> filePaths = new ArrayList<>();
//        if (fileNames != null) {
//            for (String name : fileNames) {
//                if (name.contains(SHOW_KIND)) {
//                    filePaths.add(selectStickerName + "/" + name);
//                }
//            }
//        }
        return filePaths;
    }

    private List<NoScrollGridView> init(List<String> pathList) {

        List<NoScrollGridView> mLists = new ArrayList<>();
        NoScrollGridView gv;
        for (int i = 0; i < count; i++) {
            gv = new NoScrollGridView(mContext);
            gv.setAdapter(new StickerGridViewAdapter(mContext, pathList, i));
            gv.setGravity(Gravity.CENTER);
            gv.setClickable(true);
            gv.setFocusable(true);
            gv.setNumColumns(3);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    int stickerNameId = index * MessageStickerFragment.SHOW_NUM + arg2;
                    String fileName = stickerPathList.get(stickerNameId);
                    if (fileName.indexOf(".") != -1) {
                        type = fileName.substring(fileName.lastIndexOf("."));
                    }
                    String Sticker_name = "";
                    if (fileName.indexOf("_") != -1) {
                        Sticker_name = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf("_"));
                    }
                    if (mViewClickListener != null) {
                        mViewClickListener.showComments(type, selectStickerName, Sticker_name);
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
            int i = spot * MessageStickerFragment.SHOW_NUM;
            int end = i + MessageStickerFragment.SHOW_NUM;
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

    @Override
    public void requestData() {

    }
}
