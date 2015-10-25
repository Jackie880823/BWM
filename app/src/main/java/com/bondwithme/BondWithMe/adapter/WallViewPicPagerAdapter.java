package com.bondwithme.BondWithMe.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.http.PicturesCacheUtil;
import com.bondwithme.BondWithMe.util.LocalImageLoader;
import com.bondwithme.BondWithMe.util.LogUtil;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by christepherzhang on 15/10/20.
 */
public class WallViewPicPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private static List<PhotoEntity> mData;


//    private ImageView imageView;
//    private TextView textView;
//    private TextView textViewAll;
//    private View vProgress;

    public WallViewPicPagerAdapter(Context context,LayoutInflater layoutInflater, List<PhotoEntity> data) {
        mContext = context;
        mLayoutInflater = layoutInflater;
        mData = data;

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = mLayoutInflater.inflate(R.layout.wall_view_pic, null);
        container.addView(view);

        final TextView tvCaption;
        tvCaption = (TextView)view.findViewById(R.id.tv_content);

        final TextView tvCaptionAll;
        tvCaptionAll = (TextView)view.findViewById(R.id.tv_content_all);

        final TextView tvMore;
        tvMore = (TextView)view.findViewById(R.id.tv_more);

        final TextView tvCollapse;
        tvCollapse = (TextView)view.findViewById(R.id.tv_collapse);

        final LinearLayout llMore;
        llMore = (LinearLayout)view.findViewById(R.id.ll_more);

        final LinearLayout llCollapse;
        llCollapse = (LinearLayout)view.findViewById(R.id.ll_collapse);

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMore.setVisibility(View.INVISIBLE);
                llCollapse.setVisibility(View.VISIBLE);
            }
        });

        tvCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMore.setVisibility(View.VISIBLE);
                llCollapse.setVisibility(View.INVISIBLE);
            }
        });

        setImageView(position, view);
        tvCaption.setText(mData.get(position).getPhoto_caption());
        tvCaptionAll.setText(mData.get(position).getPhoto_caption());

        tvCaptionAll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int lineCount = tvCaptionAll.getLineCount();
                if (lineCount > 2)
                {
                    tvMore.setVisibility(View.VISIBLE);
                }
                else
                {
                    tvMore.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    private void setImageView(int position,final View view) {
        final ImageView imageView;

        final View vProgress;
        imageView = (ImageView) view.findViewById(R.id.iv_pic);

        vProgress = view.findViewById(R.id.rl_progress);

        String picUrl = String.format(Constant.API_GET_PIC, Constant.Module_Original, mData.get(position).getUser_id(), mData.get(position).getFile_id());

        new HttpTools(mContext).download(mContext, picUrl, PicturesCacheUtil.getCachePicPath(mContext), true, new HttpCallback() {
            @Override
            public void onStart() {
                vProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                vProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String string) {
                //这个string？？？
                LogUtil.d("WallViewPicPagerAdapter", "------" + string);
                if (imageView != null) {
                    Bitmap bitmapCache = LocalImageLoader.loadBitmapFromFile(mContext, string, imageView.getWidth(), imageView.getHeight());
                    imageView.setImageBitmap(bitmapCache);
                    PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
                    mAttacher.update();
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

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    /**
     *让 notifyDataSetChanged() 该方法可用
     * @param object
     * @return
     */
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private View mCurrentView;


    /**
     * 获取当前视图
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentView = (View)object;
    }

    public View getmCurrentView() {
        return mCurrentView;
    }
}
