package com.bondwithme.BondWithMe.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bondwithme.BondWithMe.App;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.util.DensityUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.PreferencesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangzemian on 15/10/30.
 */
public class InteractivePopupWindow extends PopupWindow {
    private static final String TAG = "InteractivePopupWindow";
    public final static String INTERACTIVE_TIP_START = "Interactive_tip_start";
    public final static String INTERACTIVE_TIP_ADD_MEMBER = "Interactive_tip_add_member";
    public final static String INTERACTIVE_TIP_ADD_PHOTO = "Interactive_tip_add_photo";
    public final static String INTERACTIVE_TIP_ADD_DIARY = "Interactive_tip_add_diary";
    public final static String INTERACTIVE_TIP_FEELING = "Interactive_tip_feeling";
    public final static String INTERACTIVE_TIP_TAG_MEMBER = "Interactive_tip_tag_member";
    public final static String INTERACTIVE_TIP_ALLOW_ME = "Interactive_tip_allow_me";
    public final static String INTERACTIVE_TIP_LOCATION = "Interactive_tip_location";
    public final static String INTERACTIVE_TIP_TAG_POST = "Interactive_tip_tag_post";
    public final static String INTERACTIVE_TIP_CREATE_EVENT = "Interactive_tip_create_event";
    public final static String INTERACTIVE_TIP_SAVE_EVENT = "Interactive_tip_save_event";
    public final static String INTERACTIVE_TIP_ADD_MEMBER_CHATTING = "Interactive_tip_add_member_chatting";
    public final static String INTERACTIVE_TIP_SELECT_MEMBER_CHATTING = "Interactive_tip_select_member_chatting";
    public final static String INTERACTIVE_TIP_DOWNLOAD_STICKIES = "Interactive_tip_download_stickies";


    public final static String[] dirayStrings = new String[]{INTERACTIVE_TIP_ADD_MEMBER,INTERACTIVE_TIP_ADD_PHOTO
    ,INTERACTIVE_TIP_ADD_DIARY,INTERACTIVE_TIP_FEELING,INTERACTIVE_TIP_TAG_MEMBER,INTERACTIVE_TIP_ALLOW_ME
    ,INTERACTIVE_TIP_LOCATION,INTERACTIVE_TIP_TAG_POST,INTERACTIVE_TIP_CREATE_EVENT,INTERACTIVE_TIP_SAVE_EVENT
    ,INTERACTIVE_TIP_ADD_MEMBER_CHATTING,INTERACTIVE_TIP_SELECT_MEMBER_CHATTING,INTERACTIVE_TIP_DOWNLOAD_STICKIES};

    private View conentView;
    private Context mContext;
    private PopupWindow  popText;
    private int contentViewWidth,contentViewHeight;
    private int position_x,position_y;
    private int positionTest_x,positionTest_y;
    private  Rect anchor_rect;
    private View mParent;
    private String mTest;
    private int[] screen_pos;
    private boolean isConnection;
    public static boolean firstOpPop;
    private WindowManager.LayoutParams lp;




    public InteractivePopupWindow(final Activity context, View parent, String text, int model){
        LogUtil.i(TAG,"INTERACTIVE_TIP_START"+PreferencesUtil.getValue(context,INTERACTIVE_TIP_START,false));

//        WindowManager windowManager = context.getWindowManager();
//        Display display =  windowManager.getDefaultDisplay();
        lp = context.getWindow().getAttributes();
        lp.alpha = (float) 0.5; //0.0-1.0
        context.getWindow().setAttributes(lp);

        if(PreferencesUtil.getValue(context,INTERACTIVE_TIP_START,false))
            return;
        if(model == 0){
            conentView = LayoutInflater.from(context).inflate(R.layout.interactive_pop_dialog,null);
        }else {
            conentView = LayoutInflater.from(context).inflate(R.layout.interactive_pop_up_dialog,null);
        }
        mContext = context;
        mParent = parent;
        mTest = text;
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if(popText != null && mPopDismissListener != null){
                    popText.dismiss();
                    lp.alpha = (float) 1.0;
                    context.getWindow().setAttributes(lp);
                    LogUtil.i("==============0","dismiss");
                    mPopDismissListener.popDismiss();
                }
            }
        });
        // 刷新状态
        this.update();
        preparePopupWindow();
    }

    private void preparePopupWindow() {
        if (!this.isShowing()) {
            screen_pos = new int[2];
            mParent.getLocationOnScreen(screen_pos);
            anchor_rect = new Rect(screen_pos[0], screen_pos[1], screen_pos[0] + mParent.getWidth(), screen_pos[1] + mParent.getHeight());
            conentView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            contentViewWidth = conentView.getMeasuredWidth();
            contentViewHeight = conentView.getMeasuredHeight();

            position_x = anchor_rect.centerX() - (contentViewWidth / 2);
            position_y = anchor_rect.bottom - (anchor_rect.height() / 2);

            addPopupText(mTest);
        } else {
            this.dismiss();
        }
    }


    private void addPopupText(String text){
        LinearLayout layout = new LinearLayout(mContext);
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(ToDBC(text));
        textView.setBackground(mContext.getResources().getDrawable(R.drawable.pop_chat_bg));
        textView.setTextSize(14);
        textView.setTextColor(Color.WHITE);
        layout.addView(textView);

        LinearLayout.LayoutParams para =new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        para.setMargins(10, 10, 10, 10);
        para.gravity = Gravity.RIGHT;
        textView.setPadding(13, 13, 13, 13);
        textView.setLayoutParams(para);
        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//强制绘出view
        positionTest_x = anchor_rect.centerX() - (layout.getMeasuredWidth()/2);
        popText = new PopupWindow(layout,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        popText.setFocusable(false);
        popText.setOutsideTouchable(true);
        LogUtil.i("textView.getWidth",": "+textView.getWidth());
    }

    /**
     * 显示PopupWindow在view上方
     */
    public void showPopupWindowUp(){
        LogUtil.i("textView.getWidth_2", ": " + popText.getContentView().getMeasuredWidth());
        this.showAtLocation(mParent, Gravity.NO_GRAVITY, position_x, screen_pos[1] - this.getHeight() - DensityUtil.dip2px(App.getContextInstance(), 10));
        popText.showAtLocation(mParent, Gravity.NO_GRAVITY, positionTest_x, screen_pos[1] - this.getHeight() - DensityUtil.dip2px(App.getContextInstance(), 34));//显示文字
//        this.showAtLocation(mParent, Gravity.NO_GRAVITY, position_x, screen_pos[1] - this.getHeight() - 70);
//        popText.showAtLocation(mParent, Gravity.NO_GRAVITY, positionTest_x, screen_pos[1] - this.getHeight() - 100);//显示文字


    }

    /**
     * 显示PopupWindow在view下方
     */
    public void showPopupWindow(Boolean Connection){
        isConnection = Connection;
        this.showAtLocation(mParent, Gravity.NO_GRAVITY, position_x, position_y + DensityUtil.dip2px(App.getContextInstance(), 20));//显示箭头
        popText.showAtLocation(mParent, Gravity.NO_GRAVITY, position_x, position_y + DensityUtil.dip2px(App.getContextInstance(), 34));//显示文字
//        this.showAtLocation(mParent, Gravity.NO_GRAVITY, position_x, position_y + 70);//显示箭头
//        popText.showAtLocation(mParent, Gravity.NO_GRAVITY, position_x, position_y + 100);//显示文字
    }

    /**
     * 关闭PopupWindow
     */
    public void dismissPopupWindow(){
        this.dismiss();
        popText.dismiss();
    }

    /**
     * 存储已经显示Pop的状态
     */
    public void getSavePopFinish(String key){
        PreferencesUtil.saveValue(mContext, key, true);
    }

    /**
     * 返回上一个Pop是否显示过
     * @return
     */
    private Map<String,Boolean> getLastPop(String key){
        boolean b;
        String s;
        Map<String,Boolean> map = new HashMap<>();
        for (int i = 0; i < dirayStrings.length; i++){
            if(dirayStrings[i] == key){
                s = dirayStrings[i-1];
                b = PreferencesUtil.getValue(mContext,s,false);
                map.put(s,b);
            }
        }
        return map;
    }

    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public PopDismissListener mPopDismissListener;

    public void setDismissListener(PopDismissListener popDismissListener){
        mPopDismissListener = popDismissListener;
    }
    public interface PopDismissListener{
        public void popDismiss();
    }
}
