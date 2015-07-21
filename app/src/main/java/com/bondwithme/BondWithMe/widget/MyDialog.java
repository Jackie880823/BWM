package com.bondwithme.BondWithMe.widget;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.material.widget.Dialog;

public class MyDialog extends Dialog {


    View mContentView;
    String message;
    boolean isOnlyMessage;

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside){
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    //using default layout
    public MyDialog(Context context, int titleResource, int messageResource) {
        this(context,context.getString(titleResource),context.getString(messageResource));
    }

    /**
     * using custom layout
     * @param context
     * @param titleResource
     * @param contentView
     */
    public MyDialog(Context context, int titleResource, View contentView) {
        this(context,context.getString(titleResource),contentView);
    }

    //using default layout
    public MyDialog(Context context, String title, String message) {
        super(context,title);
        this.context = context;// init Context
        this.title = title;
        this.message = message;
        isOnlyMessage = true;
    }

    /**
     * using custom layout
     * @param context
     * @param title
     * @param contentView
     */
    public MyDialog(Context context, String title, View contentView) {
        super(context,title);
        this.context = context;// init Context
        this.title = title;
        mContentView = contentView;
        isOnlyMessage = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewStyle();
        LinearLayout llContent = (LinearLayout) findViewById(com.material.widget.R.id.contentContainer);

        if(isOnlyMessage){
            TextView textView = new TextView(context);
            textView.setText(message);
            textView.setTextColor(context.getResources().getColor(R.color.default_text_color_dark));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_medium_size));
            llContent.addView(textView);
        }else if(mContentView!=null){
            llContent.addView(mContentView);
        }
    }

    void initViewStyle(){
        //Not mandatory
    }


}
