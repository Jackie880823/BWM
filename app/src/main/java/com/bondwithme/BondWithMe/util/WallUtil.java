package com.bondwithme.BondWithMe.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.entity.WallCommentEntity;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jackie Zhu on 5/29/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class WallUtil {
    private static final String TAG = WallUtil.class.getSimpleName();

    /**
     * @ 群组的正则表达
     */
    public static final String AT_GROUPS = "@%1$sgroups";
    /**
     * @ 用户的正则表达
     */
    public static final String AT_MEMBER = "@%1$smembers";

    /**
     * Wall中已赞会员类型
     */
    public static final String LOVE_MEMBER_WALL_TYPE = "wall";

    /**
     * 评论中已赞会员类型
     */
    public static final String LOVE_MEMBER_COMMENT_TYPE = "comment";

    /**
     * 当前用户ID {@link WallEntity#user_id}
     */
    public static final String GET_LOVE_LIST_VIEWER_ID = "viewer_id";

    /**
     * 日志拿content_id {@link WallEntity#content_id} / 评论拿comment_id {@link WallCommentEntity#comment_id}
     */
    public static final String GET_LOVE_LIST_REFER_ID = "refer_id";

    /**
     * 赞的模块属性 日志是 {@link WallUtil#LOVE_MEMBER_COMMENT_TYPE} / 评论是 {@link WallUtil#LOVE_MEMBER_WALL_TYPE}
     */
    public static final String GET_LOVE_LIST_TYPE = "type";

    private static Long lastClickTimeMills = 0L;

    private Context mContext;
    private WallViewClickListener mViewClickListener;

    public WallUtil(Context context, WallViewClickListener viewClickListener) {
        this.mContext = context;
        mViewClickListener = viewClickListener;
    }

    /**
     * 有TAG用户或分组需要显示字符特效
     *
     * @param tvContent
     * @param wall
     * @param atDescription
     * @param tagMemberCount
     * @param tagGroupCount
     */
    public void setSpanContent(TextView tvContent, final WallEntity wall, String atDescription, int tagMemberCount, int tagGroupCount) {// 设置文字可点击，实现特殊文字点击跳转必需添加些设置
        // 设置文字可点击，实现特殊文字点击跳转必需添加些设置
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableStringBuilder ssb = new SpannableStringBuilder(atDescription);

        String strMember = "";
        if(tagMemberCount > 0) {
            strMember = String.format(mContext.getString(R.string.text_diary_content_at_member_desc), tagMemberCount);
//            LogUtil.d("WallUtil", "setSpanContent=================" + wall.getContent_group_id());
            // 文字特殊效果设置
            SpannableString ssMember = new SpannableString(strMember);

            // 给文字添加点击响应，跳转至显示被@的用户
            ssMember.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if(mViewClickListener != null) {
                        LogUtil.i(TAG, "onClick& mViewClickListener not null showMembers");
                        long currentTime = System.currentTimeMillis();
                        if(currentTime - lastClickTimeMills > 500) {
                            // 部分手机会连续执行两次，500毫秒之内的连续执行被认为一次点击只执行一次点击事件
                            lastClickTimeMills = currentTime;
                            mViewClickListener.showMembers(wall.getContent_group_id(), wall.getGroup_id());
                        }
                    } else {
                        LogUtil.i(TAG, "onClick& mViewClickListener do nothing");
                    }
                }
            }, 0, strMember.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            // 设置文字的前景色为蓝色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLUE);
            ssMember.setSpan(colorSpan, 0, ssMember.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            String target;
            if (atDescription.contains(strMember)){
                target = strMember;
            } else {
                target = String.format(AT_MEMBER, tagMemberCount);
            }
            setSpecialText(ssb, target, ssMember);
        }

        String strGroup = "";
        if(tagGroupCount > 0) {
            strGroup = String.format(mContext.getString(R.string.text_diary_content_at_group_desc), tagGroupCount);
            // 文字特殊效果设置
            SpannableString ssGroup = new SpannableString(strGroup);

            // 给文字添加点击响应，跳转至显示被@的群组
            ssGroup.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if(mViewClickListener != null) {
                        LogUtil.i(TAG, "onClick& mViewClickListener not null showGroups");
                        long currentTime = System.currentTimeMillis();
                        if(currentTime - lastClickTimeMills > 500) {
                            // 部分手机会连续执行两次，500毫秒之内的连续执行被认为一次点击只执行一次点击事件
                            lastClickTimeMills = currentTime;
                            mViewClickListener.showGroups(wall.getContent_group_id(), wall.getGroup_id());
                        }
                    } else {
                        LogUtil.i(TAG, "onClick& mViewClickListener do nothing");
                    }
                }
            }, 0, strGroup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 设置文字的前景色为蓝色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLUE);
            ssGroup.setSpan(colorSpan, 0, ssGroup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            String target;
            if (atDescription.contains(strGroup)) {
                target = strGroup;
            } else {
                target = String.format(AT_GROUPS, tagGroupCount);
            }
            setSpecialText(ssb, target, ssGroup);
        }
//        LogUtil.i(TAG, "setSpanContent setting end at description: " + ssb.toString());
        setClickNormal(ssb, strMember, strGroup, wall);
//        LogUtil.i(TAG, "setSpanContent setting end normal description: " + ssb.toString());
        tvContent.setText(ssb);
        tvContent.setAutoLinkMask(Linkify.ALL);
    }


    /**
     * 设置字符特殊效果
     *
     * @param ssb
     * @param strAt
     * @param ssAt
     */
    private void setSpecialText(SpannableStringBuilder ssb, String strAt, SpannableString ssAt) {
        try {
            Pattern p = Pattern.compile(strAt);
            Matcher m = p.matcher(ssb.toString());
            if(m.find()) {
                int start = m.start();
                int end = m.end();
                ssb.replace(start, end, ssAt);
            } else {
                ssb.append(ssAt);
            }
        } catch(Exception e) {
            ssb.append(ssAt);
            e.printStackTrace();
        }
    }


    /**
     * 分割出普通文字并设置点击事件，跳转到评论详情
     *
     * @param ssb
     * @param strMember
     * @param strGroup
     * @param wallEntity
     */
    private void setClickNormal(SpannableStringBuilder ssb, String strMember, String strGroup, WallEntity wallEntity) {
        String description = ssb.toString();
//        LogUtil.i(TAG, "setClickNormal& description: " + description + "; member: " + strMember + "; group: " + strGroup);
        int startMember = description.indexOf(strMember);
        int endMember = startMember + strMember.length();
        int startGroup = description.indexOf(strGroup);
        int endGroup = startGroup + strGroup.length();
        if(endGroup == endMember) {
            // @群组和用户结束所在的位置相等说明没有任何@,全字符可点击
            setNormalSpecialText(ssb, wallEntity, 0, description.length());
//            LogUtil.w(TAG, "setClickNormal& no action description: " + ssb.toString());
            return;
        } else {

            int length = description.length();
            if(startMember > startGroup || TextUtils.isEmpty(strMember)) {
                setNormalSpecialText(ssb, wallEntity, 0, startGroup);
//                LogUtil.i(TAG, "setClickNormal& group first description: " + ssb.toString());

                if(endGroup < startMember) {
                    setNormalSpecialText(ssb, wallEntity, endGroup, startMember);
                    LogUtil.i(TAG, "setClickNormal& split 4_1 part description: " + ssb.toString());

                    setNormalSpecialText(ssb, wallEntity, endMember, length);
                    LogUtil.i(TAG, "setClickNormal& split 4_2 part description: " + ssb.toString());
                } else {
                    LogUtil.i(TAG, "setClickNormal& split 1 part");
                    setNormalSpecialText(ssb, wallEntity, endGroup, length);
                }
            } else {

                setNormalSpecialText(ssb, wallEntity, 0, startMember);
                LogUtil.i(TAG, "setClickNormal& member first");

                if(endMember < startGroup) {
                    setNormalSpecialText(ssb, wallEntity, endMember, startGroup);
                    LogUtil.i(TAG, "setClickNormal& split 4_1 part description: " + ssb.toString());

                    setNormalSpecialText(ssb, wallEntity, endGroup, length);
                    LogUtil.i(TAG, "setClickNormal& split 4_2 part description: " + ssb.toString());
                } else {
                    setNormalSpecialText(ssb, wallEntity, endMember, length);
                    LogUtil.i(TAG, "setClickNormal& split 1 part description: " + ssb.toString());
                }
            }
        }
    }

    /**
     * @param ssb
     * @param wallEntity
     * @param start
     * @param end
     */
    private void setNormalSpecialText(SpannableStringBuilder ssb, WallEntity wallEntity, int start, int end) {
        if(start >= 0 && start < end) {
            CharSequence strMind = ssb.subSequence(start, end);
            SpannableString ssMind = new SpannableString(strMind);
            boolean autoLink = Linkify.addLinks(ssMind, Linkify.ALL);
            if(!autoLink) {
                setSpanClickShowComments(ssMind, wallEntity);
                ssb.replace(start, end, ssMind);
            }
        }
    }

    /**
     * 普通文字的点击事件，跳转到评论详情
     *
     * @param s
     * @param wallEntity
     */
    private void setSpanClickShowComments(SpannableString s, final WallEntity wallEntity) {
        s.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                LogUtil.i(TAG, "setClickNormal& onClick");
                if(mViewClickListener != null) {
                    long currentTime = System.currentTimeMillis();
                    if(currentTime - lastClickTimeMills > 500) {
                        // 部分手机会连续执行两次，500毫秒之内的连续执行被认为一次点击只执行一次点击事件
                        lastClickTimeMills = currentTime;
                        if (WallEntity.CONTENT_TYPE_ADS.equals(wallEntity.getContent_type())) {
                            if (TextUtils.isEmpty(wallEntity.getVideo_filename())) { // 没有视频可以跳转
                                String trackUrl = wallEntity.getTrack_url() + MainActivity.getUser().getUser_id();
                                if (!TextUtils.isEmpty(trackUrl)) {
                                    Uri uri = Uri.parse(trackUrl);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    widget.getContext().startActivity(intent);
                                }
                            }
                        } else {
                            mViewClickListener.showDiaryInformation(wallEntity);
                        }
                    }
                }
            }

            /**
             * Makes the text underlined and in the link color.
             *
             * @param ds
             */
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.BLACK);
            }
        }, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    public static void getLoveList(HttpTools httpTools, TextView tvLoveList, String viewerId, String referId, String type) {
        final TextView textView = tvLoveList;
        HashMap<String, String> params = new HashMap<>();
        params.put(WallUtil.GET_LOVE_LIST_VIEWER_ID, viewerId);
        params.put(WallUtil.GET_LOVE_LIST_REFER_ID, referId);
        params.put(WallUtil.GET_LOVE_LIST_TYPE, type);
        LogUtil.i(TAG, "getLoveList& params: " + params.toString());
        httpTools.cancelRequestByTag(tvLoveList);
        httpTools.get(Constant.API_WALL_GET_LOVE_MEMBER_LIST, params, tvLoveList, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String response) {
                LogUtil.i(TAG, "get loved list onResult& response: " + response);
                Gson gson = new Gson();
                ArrayList<UserEntity> users = gson.fromJson(response, new TypeToken<ArrayList<UserEntity>>() {
                }.getType());
                int size = users.size();
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < size && i < 4; i++) {
                    text.append(users.get(i).getUser_given_name()).append(" ");
                }
                textView.setText(text.toString());
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

}
