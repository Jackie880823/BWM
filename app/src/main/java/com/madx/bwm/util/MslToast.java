package com.madx.bwm.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by quankun on 15/4/27.
 */
public class MslToast {
    private Context mContext;
    private static MslToast mslToast;

    public static MslToast getInstance(Context mContext) {
        if (mslToast == null) {
            mslToast = new MslToast(mContext);
        }
        return mslToast;
    }

    private MslToast(Context mContext) {
        this.mContext = mContext;
    }

    private Toast mToast;

    public void showShortToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void showLongToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    public void showToast(String text, int showTime) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, showTime);
        } else {
            mToast.setText(text);
            mToast.setDuration(showTime);
        }
        mToast.show();
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
