package com.bondwithme.BondWithMe.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyTextUtil {

    private MyTextUtil() {}


    /**
     * 检查字符是否为空格，/r ,/n等无意义内容
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
     *
     * @param phone
     * @return
     */
    public static String NoZero(String phone)
    {

        if (phone.startsWith("0", 0))
        {
            phone = phone.substring(1);
            return NoZero(phone);
        } else {
            return phone;
        }

    }
}
