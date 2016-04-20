package com.madxstudio.co8.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.madxstudio.co8.R;
import com.material.widget.Dialog;

public class MyDialog {
    private boolean mCancel = true;
    private Context mContext;
    private AlertDialog mAlertDialog;
    private MyDialog.Builder mBuilder;
    private int mTitleResId;
    private CharSequence mTitle;
    private int mMessageResId;
    private CharSequence mMessage;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private boolean mHasShow = false;
    private View mMessageContentView;
    private int mMessageContentViewResId;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private int pId = -1, nId = -1;
    private String pText, nText;
    View.OnClickListener pListener, nListener;

    public MyDialog(Context context) {
        this.mContext = context;
    }

    public MyDialog(Context context, CharSequence title, CharSequence message) {
        this.mContext = context;
        mTitle = title;
        mMessage = message;
        if (mBuilder != null) {
            mBuilder.setTitle(title);
            mBuilder.setMessage(message);
        }
    }

    public MyDialog(Context context, int titleId, int messageId) {
        this.mContext = context;
        mTitleResId = titleId;
        mMessageResId = messageId;
        if (mBuilder != null) {
            mBuilder.setTitle(titleId);
            mBuilder.setMessage(messageId);
        }
    }

    public MyDialog(Context context, int titleId, CharSequence message) {
        this.mContext = context;
        mTitleResId = titleId;
        mMessage = message;
        if (mBuilder != null) {
            mBuilder.setTitle(titleId);
            mBuilder.setMessage(message);
        }
    }

    public MyDialog(Context context, CharSequence title, View view) {
        this.mContext = context;
        mTitle = title;
        mMessageContentView = view;
        mMessageContentViewResId = 0;
        if (mBuilder != null) {
            mBuilder.setTitle(title);
            mBuilder.setContentView(mMessageContentView);
        }
    }

    public MyDialog(Context context, int titleId, View view) {
        this.mContext = context;
        mTitleResId = titleId;
        mMessageContentView = view;
        mMessageContentViewResId = 0;
        if (mBuilder != null) {
            mBuilder.setTitle(titleId);
            mBuilder.setContentView(mMessageContentView);
        }
    }

    public void show() {
        if (!mHasShow) {
            mBuilder = new Builder();
        } else {
            mAlertDialog.show();
        }
        mHasShow = true;
    }

    public MyDialog setContentView(View view) {
        mMessageContentView = view;
        mMessageContentViewResId = 0;
        if (mBuilder != null) {
            mBuilder.setContentView(mMessageContentView);
        }
        return this;
    }


    /**
     * Set a custom view resource to be the contents of the dialog.
     *
     * @param layoutResId resource ID to be inflated
     */
    public MyDialog setContentView(int layoutResId) {
        mMessageContentViewResId = layoutResId;
        mMessageContentView = null;
        if (mBuilder != null) {
            mBuilder.setContentView(layoutResId);
        }
        return this;
    }

    public void dismiss() {
        mAlertDialog.dismiss();
    }

    private static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public MyDialog setTitle(int resId) {
        mTitleResId = resId;
        if (mBuilder != null) {
            mBuilder.setTitle(resId);
        }
        return this;
    }

    public MyDialog setTitle(CharSequence title) {
        mTitle = title;
        if (mBuilder != null) {
            mBuilder.setTitle(title);
        }
        return this;
    }

    public MyDialog setMessage(int resId) {
        mMessageResId = resId;
        if (mBuilder != null) {
            mBuilder.setMessage(resId);
        }
        return this;
    }

    public MyDialog setMessage(CharSequence message) {
        mMessage = message;
        if (mBuilder != null) {
            mBuilder.setMessage(message);
        }
        return this;
    }


    public MyDialog setButtonAccept(int resId, final View.OnClickListener listener) {
        this.pId = resId;
        this.pListener = listener;
        return this;
    }

    public MyDialog setButtonAccept(String text, final View.OnClickListener listener) {
        this.pText = text;
        this.pListener = listener;
        return this;
    }


    public MyDialog setButtonCancel(int resId, final View.OnClickListener listener) {
        this.nId = resId;
        this.nListener = listener;
        return this;
    }


    public MyDialog setButtonCancel(String text, final View.OnClickListener listener) {
        this.nText = text;
        this.nListener = listener;
        return this;
    }

    public boolean isShowing() {
        if (mAlertDialog != null) {
            return mAlertDialog.isShowing();
        }
        return false;
    }

    /**
     * Sets whether this dialog is canceled when touched outside the window's
     * bounds OR pressed the back key. If setting to true, the dialog is
     * set to be cancelable if not
     * already set.
     *
     * @param cancel Whether the dialog should be canceled when touched outside
     *               the window OR pressed the back key.
     */
    public MyDialog setCanceledOnTouchOutside(boolean cancel) {
        this.mCancel = cancel;
        if (mBuilder != null) {
            mBuilder.setCanceledOnTouchOutside(mCancel);
        }
        return this;
    }


    public MyDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
        return this;
    }


    private class Builder {
        private TextView mTitleView;
        private ViewGroup mMessageContentRoot;
        private TextView mMessageView;
        private Window mAlertDialogWindow;
        private LinearLayout mButtonLayout;

        private Builder() {
            mAlertDialog = new AlertDialog.Builder(mContext).create();
            mAlertDialog.show();
            mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE);
            mAlertDialogWindow = mAlertDialog.getWindow();
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_material_dialog, null);
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);
            mAlertDialogWindow.setBackgroundDrawableResource(R.drawable.material_dialog_window);
            mAlertDialogWindow.setContentView(contentView);
            mTitleView = (TextView) mAlertDialogWindow.findViewById(R.id.title);
            mMessageView = (TextView) mAlertDialogWindow.findViewById(R.id.message);
            mButtonLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.buttonLayout);
            mPositiveButton = (Button) mButtonLayout.findViewById(R.id.btn_p);
            mNegativeButton = (Button) mButtonLayout.findViewById(R.id.btn_n);
            mMessageContentRoot = (ViewGroup) mAlertDialogWindow.findViewById(R.id.message_content_root);
            if (mTitleResId != 0) {
                setTitle(mTitleResId);
            }
            if (mTitle != null) {
                setTitle(mTitle);
            }
            if (mTitle == null && mTitleResId == 0) {
                mTitleView.setVisibility(View.GONE);
            }
            if (mMessageResId != 0) {
                setMessage(mMessageResId);
            }
            if (mMessage != null) {
                setMessage(mMessage);
            }
            if (pId != -1) {
                mPositiveButton.setVisibility(View.VISIBLE);
                mPositiveButton.setText(pId);
                mPositiveButton.setOnClickListener(pListener);
                if (isLollipop()) {
                    mPositiveButton.setElevation(0);
                }
            }
            if (nId != -1) {
                mNegativeButton.setVisibility(View.VISIBLE);
                mNegativeButton.setText(nId);
                mNegativeButton.setOnClickListener(nListener);
                if (isLollipop()) {
                    mNegativeButton.setElevation(0);
                }
            }
            if (!isNullOrEmpty(pText)) {
                mPositiveButton.setVisibility(View.VISIBLE);
                mPositiveButton.setText(pText);
                mPositiveButton.setOnClickListener(pListener);
                if (isLollipop()) {
                    mPositiveButton.setElevation(0);
                }
            }

            if (!isNullOrEmpty(nText)) {
                mNegativeButton.setVisibility(View.VISIBLE);
                mNegativeButton.setText(nText);
                mNegativeButton.setOnClickListener(nListener);
                if (isLollipop()) {
                    mNegativeButton.setElevation(0);
                }
            }
            if (isNullOrEmpty(pText) && pId == -1) {
                mPositiveButton.setVisibility(View.GONE);
            }
            if (isNullOrEmpty(nText) && nId == -1) {
                mNegativeButton.setVisibility(View.GONE);
            }
            if (mMessageContentView != null) {
                this.setContentView(mMessageContentView);
            } else if (mMessageContentViewResId != 0) {
                this.setContentView(mMessageContentViewResId);
            }
            mAlertDialog.setCanceledOnTouchOutside(mCancel);
            mAlertDialog.setCancelable(mCancel);
            if (mOnDismissListener != null) {
                mAlertDialog.setOnDismissListener(mOnDismissListener);
            }
        }


        public void setTitle(int resId) {
            mTitleView.setText(resId);
        }


        public void setTitle(CharSequence title) {
            mTitleView.setText(title);
        }


        public void setMessage(int resId) {
            if (mMessageView != null) {
                mMessageView.setText(resId);
            }
        }

        public void setMessage(CharSequence message) {
            if (mMessageView != null) {
                mMessageView.setText(message);
            }
        }

        public void setContentView(View contentView) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            contentView.setLayoutParams(layoutParams);
            if (contentView instanceof ListView) {
                setListViewHeightBasedOnChildren((ListView) contentView);
            }
            LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.message_content_view);
            if (linearLayout != null) {
                linearLayout.removeAllViews();
                linearLayout.addView(contentView);
            }
            for (int i = 0;
                 i < (linearLayout != null ? linearLayout.getChildCount() : 0);
                 i++) {
                if (linearLayout.getChildAt(
                        i) instanceof AutoCompleteTextView) {
                    AutoCompleteTextView autoCompleteTextView
                            = (AutoCompleteTextView) linearLayout.getChildAt(i);
                    autoCompleteTextView.setFocusable(true);
                    autoCompleteTextView.requestFocus();
                    autoCompleteTextView.setFocusableInTouchMode(true);
                }
            }
        }


        /**
         * Set a custom view resource to be the contents of the dialog. The
         * resource will be inflated into a ScrollView.
         *
         * @param layoutResId resource ID to be inflated
         */
        public void setContentView(int layoutResId) {
            mMessageContentRoot.removeAllViews();
            // Not setting this to the other content view because user has defined their own
            // layout params, and we don't want to overwrite those.
            LayoutInflater.from(mMessageContentRoot.getContext())
                    .inflate(layoutResId, mMessageContentRoot);
        }

        public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            mAlertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            mAlertDialog.setCancelable(canceledOnTouchOutside);
        }
    }


    private boolean isNullOrEmpty(String nText) {
        return nText == null || nText.isEmpty();
    }


    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight +
                (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
