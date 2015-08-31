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
     * get 服务器表
     *
     * @param context 资源引导
     * @return 返回对照表
     */
    public static List<String> getDataValue(@NonNull Context context) {
        String[] relationArrayUs = context.getResources().getStringArray(R.array.relationship_value);
        return Arrays.asList(relationArrayUs);
    }

    /**
     * 服务器关系名称 >>>>>>> 下标
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
     * 服务器关系名称 >>>>>>>> 翻译关系名称
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
     * 下标 >>>>>>>>>>> 翻译关系名称
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

    /**
     * 翻译关系名称 >>>>>>>> 服务器关系名称
     * @param context
     * @param relationshipName
     * @return
     * @throws RelationshipException
     */
    public static String getRelationshipValue(@NonNull Context context, @NonNull String relationshipName) throws RelationshipException {

        // 对应语言关系列表
        String[] temp = context.getResources().getStringArray(R.array.relationship_item);
        List<String> relationships = Arrays.asList(temp);
        int position = relationships.indexOf(relationshipName);
        try {
            return getRelationshipName(context, position);
        } catch (RelationshipException e) {
            throw new RelationshipException("relationship name error, this relationship: " + relationshipName);
        }
    }

    /**
     * 下标 >>>>>>>>> 服务器关系名称
     * @param context
     * @param position
     * @return
     * @throws RelationshipException
     */
    public static String getRelationshipValue(@NonNull Context context, int position) throws RelationshipException
    {
        List<String> temp = getDataValue(context);
        if (position >= 0 && position < temp.size())
        {
            return temp.get(position);
        }
        else
        {
            throw new RelationshipException(String.format("position out of list: size is %d; position = %d", temp.size(), position));
        }
    }


}
