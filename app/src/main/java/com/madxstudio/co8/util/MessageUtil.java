package com.madxstudio.co8.util;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import com.madxstudio.co8.widget.CustomDialog;


public class MessageUtil {

    static CustomDialog wattingDialog;
    static Dialog alertDialog;

    private Context mContext;
    private static MessageUtil mslToast;

    public static MessageUtil getInstance(Context mContext) {
        if (mslToast == null) {
            mslToast = new MessageUtil(mContext);
        }
        return mslToast;
    }

    private MessageUtil(Context mContext) {
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

    public void showShortToast(int id) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, id, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(mContext.getString(id));
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void showLongToast(int id) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, id, Toast.LENGTH_LONG);
        } else {
            mToast.setText(mContext.getString(id));
            mToast.setDuration(Toast.LENGTH_LONG);
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

//	/**
//	 * 隐藏等待提示
//	 */
//	public static void dismissWattingDialog(Context context) {
//		if (wattingDialog != null&&wattingDialog.getContext()==context)
//			wattingDialog.dismiss();
//	}

    /**
     * 弹出等待提示
     *
     * @param context
     */
//	public static void showWaitting(Context context,int msgId) {
//		if (context == null) {
//			return;
//		}
//
//		//加上context判断是为了解决在不同activity重用wattingDailog，"is your activity runnning"问题
//		if (wattingDialog == null||wattingDialog.getContext()!=context) {
//			wattingDialog = new CustomDialog(context, R.layout.waitting_dialog,
//					R.style.custom_dialog_no_dim);
//			((TextView) wattingDialog.findViewById(R.id.tv_msg))
//					.setText(msgId);
//			wattingDialog.setCancelable(false);
//		}
//		if (wattingDialog.isShowing()) {
//			wattingDialog.dismiss();
//		}
//		wattingDialog.show();
//	}
    public static void showMessage(Context context, String msg) {
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    public static void showMessage(Context context, int resourceId) {
        if (context != null) {
            Toast.makeText(context, resourceId, Toast.LENGTH_LONG).show();
        }
    }

    public static void showMessage(Context context, int resourceId, int showTime) {
        if (context != null) {
            Toast.makeText(context, resourceId, showTime).show();
        }
    }

    public static void showMessage(Context context, String msg, int showTime) {
        if (context != null) {
            Toast.makeText(context, msg, showTime).show();
        }
    }

    public static void showAlert(Context context, int msg, int content, int showTime, AlertListener alertListener) {
    }
//	public static void showAlert(Context context, String msg,String content,int showTime,final AlertListener alertListener) {
//		if (context == null) {
//			return;
//		}
//		//加上context判断是为了解决在不同activity重用wattingDailog，"is your activity runnning"问题
//		if (alertDialog == null||alertDialog.getContext()!=context) {
//			LayoutInflater inflater = (LayoutInflater) context
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			alertDialog = new Dialog(context, R.style.custom_dialog_with_dim);
//			View layout = inflater.inflate(R.layout.alter_dialog, null);
//			alertDialog.addContentView(layout, new LayoutParams(
//					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//
//			Window dialogWindow = alertDialog.getWindow();
//			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//			lp.width = LayoutParams.MATCH_PARENT;
//			lp.height = LayoutParams.WRAP_CONTENT;
//			dialogWindow.setGravity(Gravity.CENTER);
//			dialogWindow.setAttributes(lp);
//
//			((TextView) alertDialog.findViewById(R.id.tv_title)).setText(msg);
//			((TextView) alertDialog.findViewById(R.id.tv_content)).setText(content);
//			alertDialog.findViewById(R.id.btn_cancle).setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					alertListener.cancle();
//				}
//			});
//			alertDialog.findViewById(R.id.btn_confirm).setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					alertListener.confirm();
//				}
//			});
//			alertDialog.setCancelable(false);
//		}
//		if (alertDialog.isShowing()) {
//			alertDialog.dismiss();
//		}
//		alertDialog.show();
//	}

    /**
     * 隐藏alert
     */
    public static void dismissAlertDialog() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }

    private static AlertListener mAlertListener;

    public interface AlertListener {
        void cancle();

        void confirm();
    }


}
