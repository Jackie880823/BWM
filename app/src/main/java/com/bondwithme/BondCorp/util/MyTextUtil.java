package com.madxstudio.co8.util;

import android.graphics.Paint;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyTextUtil {

    private MyTextUtil() {
    }


    /**
     * 检查字符是否为空格，/r ,/n等无意义内容
     *
     * @return
     */
    public static boolean isInvalidText(String text) {
        Pattern p = Pattern.compile("^\\s*$", java.util.regex.Pattern.MULTILINE);
        Matcher m = p.matcher(text);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param phone
     * @return
     */
    public static String NoZero(String phone) {

        if (!TextUtils.isEmpty(phone) && (phone.startsWith("0", 0))) {
            phone = phone.substring(1);
            return NoZero(phone);
        }
        return phone;
    }

    public static boolean isHasEmpty(String... str) {
        if (str != null) {
            for (int i = 0; i < str.length; i++) {
                if (TextUtils.isEmpty(str[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取字符长度，忽略文字和半全角
     *
     * @param currentMax
     * @param strings
     * @param p
     * @return
     */
    public static int computeMaxStringWidth(int currentMax, String[] strings, Paint p) {
        float maxWidthF = 0.0f;
        int len = strings.length;
        for (int i = 0; i < len; i++) {
            float width = p.measureText(strings[i]);
            maxWidthF = Math.max(width, maxWidthF);
        }
        int maxWidth = (int) (maxWidthF + 0.5);
        if (maxWidth < currentMax) {
            maxWidth = currentMax;
        }
        return maxWidth;
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {

        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);

        return returnString;
    }

}
