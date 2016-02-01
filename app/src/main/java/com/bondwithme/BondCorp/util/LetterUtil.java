package com.madxstudio.co8.util;


import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * ��ĸ������
 *@Title:
 *@Description:
 *@Author:Justlcw
 *@Since:2014-5-8
 *@Version:
 */
public class LetterUtil
{
    /**
     * @param chinese һ������
     * @return ƴ������ĸ
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-8
     */
    public static String[] getFirstPinyin(char chinese)
    {
        return PinyinHelper.toHanyuPinyinStringArray(chinese);
    }
    
    /**
     * �Ƿ�����ĸ
     * 
     * @return true ��ĸ,false ����ĸ
     * @Description:
     * @Author Justlcw
     * @Date 2014-5-8
     */
    public static boolean isLetter(char c)
    {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 112);
    }
}
