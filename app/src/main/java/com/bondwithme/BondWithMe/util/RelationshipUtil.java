package com.bondwithme.BondWithMe.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.exception.RelationshipException;

import java.util.Arrays;
import java.util.List;

/**
 * 关系用户工具类
 * Created by Jackie on 8/20/15.
 *
 * @author Jackie
 * @version 2.0
 */
public class RelationshipUtil {
    private static final String TAG = RelationshipUtil.class.getSimpleName();

    /**
     * 获取关系对照表，此列表与服务器的关系列表一致
     *
     * @param context 资源引导
     * @return 返回对照表
     */
    public static List<String> getDataValue(@NonNull Context context) {
        String[] relationArrayUs = context.getResources().getStringArray(R.array.relationship_value);
        return Arrays.asList(relationArrayUs);
    }

    /**
     * 用服务得到的关系名称获取当前语言下对应的关系名称的下标位置
     *
     * @param context       资源引导
     * @param relationsType 关系名称一定要使用服务器上得到的关系
     * @return 返回匹配的关系对应的下标位置
     * @throws RelationshipException 但传的关系名称跟本地与服务器对应的关系列表没有能匹配的值将抛{@link RelationshipException}
     */
    public static int getRelationshipPosition(@NonNull Context context, @NonNull String relationsType) throws RelationshipException {
        int result;
        result = getDataValue(context).indexOf(relationsType);
        if(result >= 0) {
            return result;
        } else {
            throw new RelationshipException("relationshipType is error!");
        }
    }

    /**
     * 用服务得到的关系名称获取当前语言下对应的关系名称
     *
     * @param context       资源引导
     * @param relationsType 关系名称一定要使用服务器上得到的关系，否则返回传入的时的名称
     * @return 返回匹配到的关系名称
     */
    public static String getRelationshipName(@NonNull Context context, @NonNull String relationsType) {
        String result;
        // 获取关系名称在对照表的位置
        int position = -1;
        try {
            position = getRelationshipPosition(context, relationsType);
        } catch(RelationshipException e) {
            e.printStackTrace();
        }

        // 对应语言关系列表
        String[] temp = context.getResources().getStringArray(R.array.relationship_item);
        List<String> relationships = Arrays.asList(temp);

        if(position >= 0 && position < relationships.size()) {
            result = relationships.get(position);
        } else {
            result = relationsType;
        }
        LogUtil.i(TAG, "getRelationshipName: relationsType" + relationsType + "; position: " + position + "; result: " + result);
        return result;
    }

    /**
     * 用关系列表下标位置获取当前语言下对应的关系名称
     *
     * @param context  资源引导
     * @param position 关系下标位置
     * @return 返回下标名称
     */
    public static String getRelationshipName(@NonNull Context context, int position) throws RelationshipException {
        String[] relationships = context.getResources().getStringArray(R.array.relationship_item);
        if(position >= 0 && position < relationships.length) {
            return relationships[position];
        } else {
            throw new RelationshipException(String.format("position out of list: size is %d; position = %d", relationships.length, position));
        }
    }

}
