package com.bondwithme.BondWithMe.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quankun on 15/5/13.
 */
public class PinYin4JUtil {
    /**
     * 只转换汉字为拼音，其他字符不变
     *
     * @param src
     * @return
     */
    public static String getPinyinWithMark(String src) {
        if (src != null && !src.trim().equalsIgnoreCase("")) {
            char[] srcChar;
            srcChar = src.toCharArray();
            //汉语拼音格式输出类
            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
            //输出设置，大小写，音标方式等
            hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE); //小写
            hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE); //无音调
            hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE); //'¨¹' is "v"
            StringBuffer output = new StringBuffer();
            for (int i = 0; i < srcChar.length; i++) {
                char c = srcChar[i];
                //是中文转换拼音(我的需求，是保留中文)
                if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) { //中文字符
                    try {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], hanYuPinOutputFormat);
                        output.append(temp[0]);
                       // output.append(" ");
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        e.printStackTrace();
                    }
                } else { //其他字符
                    output.append(String.valueOf(srcChar[i]));
                }
            }
            return output.toString();
        }
        return null;
    }

    public static List<String> getPinyinList(List<String> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        List<String> changeStringList = new ArrayList<>();
        for (String string : list) {
            changeStringList.add(getPinyinWithMark(string));
        }
        return changeStringList;
    }
}