/**
 * Copyright 2015 Bartosz Lipinski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bondwithme.BondWithMe.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Bartosz Lipinski
 * 31.03.15
 */
public class RecyclerViewHeader extends RelativeLayout {

    private RecyclerView mRecycler;

    private int mDownScroll;
    private int mCurrentScroll;
    private boolean mReversed;
    private boolean mAlreadyAligned;
    private boolean mRecyclerWantsTouchEvent;

    /**
     * Inflates layout from  and encapsulates it with RecyclerViewHeader.
     *
     * @param context   application context.
     * @param layoutRes layout resource to be inflated.
     * @return RecyclerViewHeader view object.
     */
    public static RecyclerViewHeader fromXml(Context context, @LayoutRes int layoutRes) {
        RecyclerViewHeader header = new RecyclerViewHeader(context);
        View.inflate(context, layoutRes, header);
        return header;
    }

    public RecyclerViewHeader(Context context) {
        super(context);
    }

    public RecyclerViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Attaches RecyclerViewHeader to RecyclerView.
     * This method will perform necessary actions to properly align the header within RecyclerView.
     * Be sure that setLayoutManager(...) has been called for ecyclerView before calling this method.
     * Also, if you were planning to use setOnScrollListener(...) method for your RecyclerView, be sure to do it before calling this method.
     *
     * @param recycler RecyclerView to attach RecyclerViewHeader to.
     */
    public void attachTo(RecyclerView recycler) {
        attachTo(recycler, false);
    }

    /**
     * Attaches RecyclerViewHeader to RecyclerView.
     * Be sure that setLayoutManager(...) has been called for RecyclerView before calling this method.
     * Also, if you were planning to use setOnScrollListener(...) method for your RecyclerView, be sure to do it before calling this method.
     *
     * @param recycler             RecyclerView to attach RecyclerViewHeader to.
     * @param headerAlreadyAligned If set to false, method will perform necessary actions to properly align
     *                             the header within RecyclerView. If set to true method will assume,
     *                             that user has already aligned RecyclerViewHeader properly.
     */
    public void attachTo(RecyclerView recycler, boolean headerAlreadyAligned) {
        validateRecycler(recycler, headerAlreadyAligned);

        mRecycler = recycler;
        mAlreadyAligned = headerAlreadyAligned;
        mReversed = isLayoutManagerReversed(recycler);

        setupAlignment(recycler);
        setupHeader(recycler);
    }

    /**
     * 判断视图是否反了
     * @param recycler
     * @return
     */
    private boolean isLayoutManagerReversed(RecyclerView recycler) {
        boolean reversed = false;
        //获取RecyclerView管理器
        RecyclerView.LayoutManager manager = recycler.getLayoutManager();
         //manager是否LinearLayoutManager或者他子类的对象
        if (manager instanceof LinearLayoutManager) {
            reversed = ((LinearLayoutManager) manager).getReverseLayout();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            reversed = ((StaggeredGridLayoutManager) manager).getReverseLayout();
        }
        return reversed;
    }

    private void setupAlignment(RecyclerView recycler) {
        if (!mAlreadyAligned) {
            //setting alignment of header(设置头和对齐)
            //获取现在的父布局参数
            ViewGroup.LayoutParams currentParams = getLayoutParams();
            //新建一个父布局参数
            FrameLayout.LayoutParams newHeaderParams;
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;//获取父布局的宽
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;//获取父布局的高
             //获取位置顶部、底部、中间
            int gravity = (mReversed ? Gravity.BOTTOM : Gravity.TOP) | Gravity.CENTER_HORIZONTAL;
            if (currentParams != null) {
                newHeaderParams = new FrameLayout.LayoutParams(getLayoutParams()); //to copy all the margins
                newHeaderParams.width = width;
                newHeaderParams.height = height;
                newHeaderParams.gravity = gravity;
            } else {
                newHeaderParams = new FrameLayout.LayoutParams(width, height, gravity);
            }
            RecyclerViewHeader.this.setLayoutParams(newHeaderParams);

            //setting alignment of recycler
            //新建FrameLayout 布局
            FrameLayout newRootParent = new FrameLayout(recycler.getContext());
            //将RecyclerView父布局参数设置到FrameLayout上
            newRootParent.setLayoutParams(recycler.getLayoutParams());
            //RecyclerView获取的父布局
            ViewParent currentParent = recycler.getParent();
            if (currentParent instanceof ViewGroup) {
                //获取RecyclerView的位置
                int indexWithinParent = ((ViewGroup) currentParent).indexOfChild(recycler);
                //删除指定位置的视图
                ((ViewGroup) currentParent).removeViewAt(indexWithinParent);
                //设置RecyclerView父视图的参数
                recycler.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                //给FrameLayout布局添加RecyclerView
                newRootParent.addView(recycler);
                //给FrameLayout布局添加头视图
                newRootParent.addView(RecyclerViewHeader.this);
                //把FrameLayout添加到RecyclerView原来到位置
                ((ViewGroup) currentParent).addView(newRootParent, indexWithinParent);
            }
        }
    }

    @SuppressLint("NewApi")
    private void setupHeader(final RecyclerView recycler) {
        //RecyclerView滑动监听
        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mCurrentScroll += dy;
                RecyclerViewHeader.this.setTranslationY(-mCurrentScroll);
            }
        });

        RecyclerViewHeader.this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = RecyclerViewHeader.this.getHeight();
                if (height > 0) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        RecyclerViewHeader.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        RecyclerViewHeader.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    if (mAlreadyAligned) {
                        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
                        height += params.topMargin;
                        height += params.bottomMargin;
                    }

                    recycler.addItemDecoration(new HeaderItemDecoration(recycler.getLayoutManager(), height), 0);
                }
            }
        });
    }

    private void validateRecycler(RecyclerView recycler, boolean headerAlreadyAligned) {
        RecyclerView.LayoutManager layoutManager = recycler.getLayoutManager();
        if (layoutManager == null) {
            throw new IllegalStateException("Be sure to call RecyclerViewHeader constructor after setting your RecyclerView's LayoutManager.");
        } else if (layoutManager.getClass() != LinearLayoutManager.class    //not using instanceof on purpose
                && layoutManager.getClass() != GridLayoutManager.class
                && !(layoutManager instanceof StaggeredGridLayoutManager)) {
            throw new IllegalArgumentException("Currently RecyclerViewHeader supports only LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager.");
        }

        if (layoutManager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) layoutManager).getOrientation() != LinearLayoutManager.VERTICAL) {
                throw new IllegalArgumentException("Currently RecyclerViewHeader supports only VERTICAL orientation LayoutManagers.");
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            if (((StaggeredGridLayoutManager) layoutManager).getOrientation() != StaggeredGridLayoutManager.VERTICAL) {
                throw new IllegalArgumentException("Currently RecyclerViewHeader supports only VERTICAL orientation StaggeredGridLayoutManagers.");
            }
        }

        if (!headerAlreadyAligned) {
            ViewParent parent = recycler.getParent();
            if (parent != null &&
                    !(parent instanceof LinearLayout) &&
                    !(parent instanceof FrameLayout) &&
                    !(parent instanceof RelativeLayout)) {
                throw new IllegalStateException("Currently, NOT already aligned RecyclerViewHeader " +
                        "can only be used for RecyclerView with a parent of one of types: LinearLayout, FrameLayout, RelativeLayout.");
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mRecyclerWantsTouchEvent = mRecycler.onInterceptTouchEvent(ev);
        if (mRecyclerWantsTouchEvent && ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDownScroll = mCurrentScroll;
        }
        return mRecyclerWantsTouchEvent || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRecyclerWantsTouchEvent) {
            int scrollDiff = mCurrentScroll - mDownScroll;
            MotionEvent recyclerEvent =
                    MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(),
                            event.getX(), event.getY() - scrollDiff, event.getMetaState());
            mRecycler.onTouchEvent(recyclerEvent);
            return false;
        }
        return super.onTouchEvent(event);
    }



    private class HeaderItemDecoration extends RecyclerView.ItemDecoration {
        private int mHeaderHeight;
        private int mNumberOfChildren;

        public HeaderItemDecoration(RecyclerView.LayoutManager layoutManager, int height) {
            if (layoutManager.getClass() == LinearLayoutManager.class) {
                mNumberOfChildren = 1;
            } else if (layoutManager.getClass() == GridLayoutManager.class) {
                mNumberOfChildren = ((GridLayoutManager) layoutManager).getSpanCount();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                mNumberOfChildren = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            }
            mHeaderHeight = height;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int value = (parent.getChildLayoutPosition(view) < mNumberOfChildren) ? mHeaderHeight : 0;
            if (mReversed) {
                outRect.bottom = value;
            } else {
                outRect.top = value;
            }
        }
    }
}