package com.bondwithme.BondWithMe.util;

import android.content.Context;

import com.bondwithme.BondWithMe.R;

import java.util.Arrays;

/**
 * Created by Jackie on 8/20/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class RelationshipUtil {
    private static final String TAG = RelationshipUtil.class.getSimpleName();

    public static String getRelationshipName(Context context, String relationsType){
        String result;
        String[] values = context.getResources().getStringArray(R.array.relationship_value);
        int position = Arrays.asList(values).indexOf(relationsType);
        String[] relationships = context.getResources().getStringArray(R.array.relationship_item);
        if(position >=0 && position < relationships.length){
            result = relationships[position];
        } else {
            result = relationsType;
        }
        LogUtil.i(TAG, "getRelationshipName: relationsType" + relationsType + "; position: " + position + "; result: " + result);
        return result;
    }
}
