package com.madx.bwm.adapter;

import android.widget.BaseAdapter;

/**
 * ���в����ĸ�б��listView������
 * 
 *@Title:
 *@Description:
 *@Author:Justlcw
 *@Since:2014-5-8
 *@Version:
 */
public abstract class LetterBaseAdapter extends BaseAdapter
{
    /** ��ĸ��ͷ�� **/
    protected static final char HEADER = '+';
    /** ��ĸ��β�� **/
    protected static final char FOOTER = '#';

    /**
     * �Ƿ���Ҫ����û��ƥ�䵽����ĸ
     * 
     * @return true ����, false ������
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-8
     */
    public abstract boolean hideLetterNotMatch();
    
    /**
     * ��ȡ��ĸ��Ӧ��λ��
     * 
     * @return position
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-8
     */
    public abstract int getIndex(char letter);
}
