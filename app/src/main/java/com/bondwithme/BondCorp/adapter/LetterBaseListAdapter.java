package com.bondwithme.BondCorp.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bondwithme.BondCorp.util.LetterUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ͨ�ô�����ĸ�б�ķ��Ͷ���adapter
 *@Title:
 *@Description:
 *@Author:Justlcw
 *@Since:2014-5-9
 *@Version:
 */
public abstract class LetterBaseListAdapter<T> extends LetterBaseAdapter
{
    /** log tag. **/
    private static final String TAG = "LetterBaseListAdapter";
    
    /** Ĭ�ϴ���ͷ����ĸ. **/
    private static final char ERROR_LETTER = ' ';
    
    /** view type����������  **/
    private static final int TYPE_COUNT = 2;
    /** ��ĸ���� **/
    private static final int TYPE_LETTER = 0;
    /** ʵ������ **/
    private static final int TYPE_CONTAINER = 1;
    
    /** �����ĸ֮���list **/
    public final List<T> list;
    /** ��ĸͷλ�ñ�ʾmap **/
    private final Map<Character, Integer> letterMap;
    
    /**
     * ���췽��
     */
    public LetterBaseListAdapter()
    {
        list = new ArrayList<T>();
        letterMap = new HashMap<Character, Integer>();
    }
    
    /**
     * ���췽��
     * @param dataArray ��������
     */
    public LetterBaseListAdapter(T[] dataArray)
    {
        this();
        setContainerList(dataArray);
    }
    
    /**
     * ���췽��
     * @param dataList �����б�
     */
    public LetterBaseListAdapter(List<T> dataList)
    {
        this();
        setContainerList(dataList);
    }
    
    /**
     * ������������
     * 
     * @param dataArray ʵ������
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-9
     */
    protected final void setContainerList(T[] dataArray)
    {
        if(!list.isEmpty())
        {
            list.clear();
        }
        if(!letterMap.isEmpty())
        {
            letterMap.clear();
        }
        
        char letter = ERROR_LETTER;
        int index = 0;
        for(int i=0; i<dataArray.length; i++)
        {
            T t = dataArray[i];
            
            char l = getHeaderLetter(t);
            
            if(letter != l && l != ERROR_LETTER)
            {
                //������������ĸû����ӹ�,����һ�±�ʾ
                letter = l;
                //����һ��T���͵���ĸͷ�Ž�ȥ
                T tl = create(letter);
                if(tl != null)
                {
                    //��������ɹ�,����뵽�б���
                    list.add(tl);
                }
                //���������ĸ��Ӧ��λ��
                letterMap.put(letter, index);
                index++;
            }
            //���ԭ�������ʵ����
            list.add(t);
            index++;
        }
    }
    
    /**
     * ������������.
     * 
     * @param dataList ʵ���б�
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-9
     */
    protected final void setContainerList(List<T> dataList)
    {
        if(!list.isEmpty())
        {
            list.clear();
        }
        if(!letterMap.isEmpty())
        {
            letterMap.clear();
        }
        
        char letter = ' ';
        int index = 0;
        for(int i=0; i<dataList.size(); i++)
        {
            T t = dataList.get(i);
            
            char l = getHeaderLetter(t);
            
            if(letter != l && l != ERROR_LETTER)
            {
                //������������ĸû����ӹ�,����һ�±�ʾ
                letter = l;
                //����һ��T���͵���ĸͷ�Ž�ȥ
                T tl = create(letter);
                if(tl != null)
                {
                    //��������ɹ�,����뵽�б���
                    list.add(tl);
                }
                //���������ĸ��Ӧ��λ��
                letterMap.put(letter, index);
                index++;
            }
            //���ԭ�������ʵ����
            list.add(t);
            index++;
        }
    }
    
    /**
     * @param t <ʵ��item����>
     * 
     * @return <ʵ��item����> ����ĸ, ��ȡʧ�ܷ��� {@link #ERROR_LETTER}
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-12
     */
    private char getHeaderLetter(T t)
    {
        //��ȡitem��Ӧ���ַ���
        String str = getItemString(t);
        //���Ϊ��,��������
        if(TextUtils.isEmpty(str))
        {
            Log.e(TAG, "item string empty in " + t.toString());
            return ERROR_LETTER;
        }
        char l;
        //��ȡ��һ����ĸ
        char firstChar = str.charAt(0);
        if(firstChar == HEADER || firstChar == FOOTER || LetterUtil.isLetter(firstChar))
        {
            l = firstChar;//�����ͷ,β,��ĸ,ֱ�Ӹ�ֵ
        }
        else
        {
            String[] letterArray = LetterUtil.getFirstPinyin(firstChar);
            //����Ǻ���,ȡƴ������ĸ
            if(letterArray != null && letterArray.length > 0)
            {
                l = letterArray[0].charAt(0);
            }
            else
            {
                //�������תƴ��ʧ����,����
                Log.e(TAG, firstChar + " turn to letter fail, " + t.toString());
                return ERROR_LETTER;
            }
        }
        
        //�����Сд��ĸ,ת��Ϊ��д��ĸ
        if(l >= 'a')
        {
            l = (char) (l - 32);
        }
        return l;
    }

    @Override
    public final int getCount()
    {
        return list.size();
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent)
    {
        if(getItemViewType(position) == TYPE_LETTER)
        {
            return getLetterView(position, convertView, parent);
        }
        return getContainerView(position, convertView, parent);
    }
    
    @Override
    public final int getItemViewType(int position)
    {
        if(isLetter(list.get(position)))
        {
            return TYPE_LETTER;
        }
        return TYPE_CONTAINER;
    }

    @Override
    public final int getViewTypeCount()
    {
        return TYPE_COUNT;
    }

    @Override
    public boolean hideLetterNotMatch()
    {
        return false;
    }
    
    @Override
    public final int getIndex(char letter)
    {
        Integer index = letterMap.get(letter);
        if(index == null)
        {
            return -1;
        }
        return index;
    }

    /**
     * @param //T <ʵ��item����>
     * 
     * @return <ʵ��item����>��Ӧ��String,������ȡ<ƴ������ĸ>
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-9
     */
    public abstract String getItemString(T t);
    
    /**
     * @param letter <��ĸ>
     * 
     * @return ����<��ĸ>����һ��<ʵ��item����>,������ʾ<��ĸitem>
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-9
     */
    public abstract T create(char letter);
    
    /**
     * @param t <ʵ��item����>
     * 
     * @return ����<ʵ��item����>,�ж��Ƿ���<��ĸitem>
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-9
     */
    public abstract boolean isLetter(T t);
    
    /**
     * ���� <��ĸitem>����,������ͬ<P>{@link #getView(int, android.view.View, android.view.ViewGroup)}
     * 
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-9
     */
    public abstract View getLetterView(int position, View convertView, ViewGroup parent);
    
    /**
     * ����<ʵ��item>����,������ͬ<P>{@link #getView(int, android.view.View, android.view.ViewGroup)}
     * 
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-9
     */
    public abstract View getContainerView(int position, View convertView, ViewGroup parent);
}
