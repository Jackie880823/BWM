package com.bondwithme.BondWithMe.util;

import android.content.Context;

import com.bondwithme.BondWithMe.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by christepherzhang on 15/8/25.
 * @deprecated
 */
public class NoUse {

    public static String getLocaleRelationship(Context context, String str)
    {
        String[] relationshipArrayStandard = context.getResources().getStringArray(R.array.relationship_value);//英文标准用于和服务器返回数据对比，获取下标
        List<String> relationshipListStandard = Arrays.asList(relationshipArrayStandard);

        String[] relationshipArrayLocale = context.getResources().getStringArray(R.array.relationship_item);//根据下标，把当前对应的语言返回
//        List<String> relationshipListLocale = Arrays.asList(relationshipArrayLocale);

        int index = relationshipListStandard.indexOf(str);

        if (-1 == index)
        {
            return str;//出错找不到匹配
        }
        else
        {
            if (index > relationshipArrayLocale.length)
            {
                return str;//越界处理？？
            }
            else
            {
                return relationshipArrayLocale[index];//正常数据
            }
        }

    }
}
