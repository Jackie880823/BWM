package com.bondwithme.BondWithMe.util;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.WallEntity;
import com.bondwithme.BondWithMe.interfaces.WallViewClickListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jackie Zhu on 5/29/15.
 */
public class WallUtil {
    private static final String TAG = WallUtil.class.getSimpleName();

    /**
     * @群组的正则表达
     */
    public static final String AT_GROUPS = "@%1$sgroups";
    /**
     * @用户的正则表达
     */
    public static final String AT_MEMBER = "@%1$smembers";

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
            LogUtil.d("WallUtil", "setSpanContent=================" + wall.getContent_group_id());
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

            String target = String.format(AT_MEMBER, tagMemberCount);
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

            String target = String.format(AT_GROUPS, tagGroupCount);
            setSpecialText(ssb, target, ssGroup);
        }

        setClickNormal(ssb, strMember, strGroup, wall);
        tvContent.setText(ssb);
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
        LogUtil.i(TAG, "setClickNormal& description: " + description + "; member: " + strMember + "; group: " + strGroup);
        int startMember = description.indexOf(strMember);
        int endMember = startMember + strMember.length();
        int startGroup = description.indexOf(strGroup);
        int endGroup = startGroup + strGroup.length();
        if(endGroup == endMember) {
            // @群组和用户结束所在的位置相等说明没有任何@可以不做处理
            LogUtil.w(TAG, "setClickNormal& no action");
            return;
        } else {

            int length = description.length();
            if(startMember > startGroup | TextUtils.isEmpty(strMember)) {
                LogUtil.i(TAG, "setClickNormal& group first");

                setSpecialText(ssb, wallEntity, description, 0, startGroup);

                if(endGroup < startMember) {
                    LogUtil.i(TAG, "setClickNormal& split 4 part");
                    setSpecialText(ssb, wallEntity, description, endGroup, startMember);

                    setSpecialText(ssb, wallEntity, description, endMember, length);
                } else {
                    LogUtil.i(TAG, "setClickNormal& split 1 part");
                    setSpecialText(ssb, wallEntity, description, endGroup, length);
                }
            } else {
                LogUtil.i(TAG, "setClickNormal& member first");

                setSpecialText(ssb, wallEntity, description, 0, startMember);

                if(endMember < startGroup) {
                    LogUtil.i(TAG, "setClickNormal& split 4 part");
                    setSpecialText(ssb, wallEntity, description, endMember, startGroup);

                    setSpecialText(ssb, wallEntity, description, endGroup, length);
                } else {
                    LogUtil.i(TAG, "setClickNormal& split 1 part");
                    setSpecialText(ssb, wallEntity, description, endMember, length);
                }
            }
        }
    }

    private void setSpecialText(SpannableStringBuilder ssb, WallEntity wallEntity, String description, int start, int end) {
        if(start >= 0 && start < end) {
            String strMind = description.substring(start, end);
            SpannableString ssMind = new SpannableString(strMind);
            setSpanClickShowComments(strMind, ssMind, wallEntity);
            setSpecialText(ssb, strMind, ssMind);
        }
    }

    /**
     * 普通文字的点击事件，跳转到评论详情
     *
     * @param str
     * @param s
     * @param wallEntity
     */
    private void setSpanClickShowComments(String str, SpannableString s, final WallEntity wallEntity) {
        s.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                LogUtil.i(TAG, "setClickNormal& onClick");
                if(mViewClickListener != null) {
                    long currentTime = System.currentTimeMillis();
                    if(currentTime - lastClickTimeMills > 500) {
                        // 部分手机会连续执行两次，500毫秒之内的连续执行被认为一次点击只执行一次点击事件
                        lastClickTimeMills = currentTime;
                        mViewClickListener.showComments(wallEntity.getContent_group_id(), wallEntity.getGroup_id());
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
        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
