package com.madx.bwm.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.madx.bwm.R;
import com.madx.bwm.adapter.LetterBaseAdapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * ������ĸ�б��listView
 *
 *@Title:
 *@Description:
 *@Author:Justlcw
 *@Since:2014-5-7
 *@Version:
 */
public class LetterListView extends FrameLayout
{
    /** ������ĸ��Ϣ **/
    private final int MSG_HIDE_LETTER = 0x0;
    
    /** ��ĸ�б�Ŀ�� **/
    private final int LETTER_LIST_VIEW_WIDTH = 50;//TODO ������д����,����һ���ı����ȽϺ�
    
    /** �����б� **/
    private ListView mListView;
    /** �����б������� **/
    private LetterBaseAdapter mAdapter;
    
    /** ��ĸ�б� **/
    private ListView mLetterListView;
    private LetterAdapter mLetterAdapter;
    
    private TextView mLetterTextView;
    
    /** ��ĸ��ϢHandler **/
    private Handler mLetterhandler;
    
    /**
     * ���췽��
     * @param context
     */
    public LetterListView(Context context)
    {
        super(context);
        initListView(context);
    }

    /**
     * ���췽��
     * @param context
     * @param attrs
     */
    public LetterListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initListView(context);
    }
    
    /**
     * ��ʼ�� �����б� ��ĸ�б�
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-7
     */
    private void initListView(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        //TODO ������������б�,�����������ListView����һЩ����Ҫ������
        mListView = (ListView) inflater.inflate(R.layout.letter_list_container, null, false);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mListView, lp);
        
        //TODO ���������ĸ�б�,�����������ListView����һЩ����Ҫ������
        mLetterListView = (ListView) inflater.inflate(R.layout.letter_list_letter, null, false);
        mLetterListView.setOnTouchListener(mLetterOnTouchListener);
        LayoutParams letterListLp = new LayoutParams(LETTER_LIST_VIEW_WIDTH, LayoutParams.MATCH_PARENT, Gravity.RIGHT);
        addView(mLetterListView, letterListLp);
        
        //TODO �������ʾ����ĸ��������
        mLetterTextView = (TextView) inflater.inflate(R.layout.letter_list_position, null, false);
        LayoutParams letterLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        addView(mLetterTextView, letterLp);
        mLetterTextView.setVisibility(View.INVISIBLE);
        
        //��ʼ��letter��Ϣ������
        mLetterhandler = new LetterHandler(this);
    }
    
    /**
     * ���������б�������
     *
     * @param adapter {@link LetterBaseAdapter}
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-7
     */
    public void setAdapter(LetterBaseAdapter adapter)
    {
        if(adapter != null)
        {
            mAdapter = adapter;
            mListView.setAdapter(mAdapter);
        }
    }
    
    /**
     * {@link android.widget.AbsListView#setOnItemClickListener(android.widget.AdapterView.OnItemClickListener)}
     * 
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-14
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        mListView.setOnItemClickListener(onItemClickListener);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        
        mLetterAdapter = new LetterAdapter(h-getPaddingTop()-getPaddingBottom());
        mLetterListView.setAdapter(mLetterAdapter);
    }

    /**
     * ��ʾ��ĸ
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-8
     */
    private void showLetter(String letter)
    {
        if(mLetterTextView.getVisibility() != View.VISIBLE)
        {
            mLetterTextView.setVisibility(View.VISIBLE);
            mLetterListView.setBackgroundResource(android.R.color.darker_gray);
        }
        mLetterTextView.setText(letter);
        
        mLetterhandler.removeMessages(MSG_HIDE_LETTER);
        mLetterhandler.sendEmptyMessageDelayed(MSG_HIDE_LETTER, 500);
    }
    
    /**
     * ������Ϣ {@link com.madx.bwm.widget.LetterListView.LetterHandler#handleMessage(android.os.Message)}
     *
     * @param msg ��Ϣ
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-8
     */
    private void handleLetterMessage(Message msg)
    {
        mLetterTextView.setVisibility(View.INVISIBLE);
        mLetterListView.setBackgroundResource(android.R.color.white);
    }
    
    /** ��ĸ��touch�¼� **/
    private OnTouchListener mLetterOnTouchListener = new OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            int height = (int)event.getY() - v.getTop();
            
            int position = mLetterAdapter.getTouchPoistion(height);
            if(position >= 0)
            {
                char letter = (Character) mLetterAdapter.getItem(position);
                //��ʾ��ĸ
                showLetter(String.valueOf(letter));
                
                //��ʾ����ĸ��Ӧ��λ��
                int select = mAdapter.getIndex(letter);
                if(select >= 0)
                {
                    mListView.setSelection(select);
                }
                return true;
            }
            return false;
        }
    };

    /**
     * ��ĸ�б�������
     *@Title:
     *@Description:
     *@Author:Justlcw
     *@Since:2014-5-7
     *@Version:
     */
    private class LetterAdapter extends BaseAdapter
    {
        /** ��ĸ�� **/
        private static final String LETTER_STR = "+ABCDEFGHIJKLMNOPQRSTUVWXYZ#";
        /** ������ʾ����ĸarray **/
        private char[] letterArray;
        /** ÿ����ĸ�ĸ߶� **/
        private int itemHeight;
        
        /**
         * ���췽��
         *
         * @param height view height
         */
        public LetterAdapter(int height)
        {
            if(mAdapter.hideLetterNotMatch())
            {
                List<Character> list = new ArrayList<Character>();
                char[] allArray = LETTER_STR.toCharArray();
                for(int i=0; i<allArray.length; i++)
                {
                    char letter = allArray[i];
                    int position = mAdapter.getIndex(letter);
                    if(position >= 0)
                    {
                        list.add(letter);
                    }
                }
                letterArray = new char[list.size()];
                for(int i=0; i<list.size(); i++)
                {
                    letterArray[i] = list.get(i);
                }
                list.clear();
                list = null;
            }
            else
            {
                letterArray = LETTER_STR.toCharArray();
            }
            itemHeight = height/letterArray.length;
        }
        
        @Override
        public int getCount()
        {
            return letterArray.length;
        }

        @Override
        public Object getItem(int position)
        {
            return letterArray[position];
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
            {
                convertView = new TextView(getContext());
                ((TextView) convertView).setTextColor(getResources().getColor(android.R.color.black));
                ((TextView) convertView).setGravity(Gravity.CENTER);
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT, itemHeight);
                convertView.setLayoutParams(lp);
            }
            ((TextView) convertView).setText(String.valueOf(letterArray[position]));
            
            return convertView;
        }
        
        /**
         * ��ȡtouch��λ��
         *
         * @return position
         * @Description:
         * @Author Justlcw
         * @Date 2014-5-8
         */
        public int getTouchPoistion(int touchHeight)
        {
            int position = touchHeight / itemHeight;
            if(position >= 0 && position < getCount())
            {
                return position;
            }
            return -1;
        }
    }
    
    /**
     * ������ĸ��ʾ��handler.
     *@Title:
     *@Description:
     *@Author:Justlcw
     *@Since:2014-5-8
     *@Version:
     */
    private static class LetterHandler extends Handler
    {
        /** ������ {@link com.madx.bwm.widget.LetterListView} **/
        private SoftReference<LetterListView> srLetterListView;
        
        /**
         * ���췽��
         * @param letterListView {@link com.madx.bwm.widget.LetterListView}
         */
        public LetterHandler(LetterListView letterListView)
        {
            srLetterListView = new SoftReference<LetterListView>(letterListView);
        }

        @Override
        public void handleMessage(Message msg)
        {
            LetterListView letterListView = srLetterListView.get();
            //���viewû�б����ٵ�,����view���������Ϣ
            if(letterListView != null)
            {
                letterListView.handleLetterMessage(msg);
            }
        }
    }
}
