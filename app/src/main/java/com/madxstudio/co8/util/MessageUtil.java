package com.madxstudio.co8.util;

import android.content.Context;
import android.widget.Toast;

import com.madxstudio.co8.App;


public class MessageUtil {


    private Context mContext;
    private static MessageUtil mslToast;

    public static MessageUtil getInstance() {
        if (mslToast == null) {
            mslToast = new MessageUtil(App.getContextInstance());
        }
        return mslToast;
    }

    private MessageUtil(Context mContext) {
        this.mContext = mContext;
    }

    private Toast mToast;

    public void showShortToast(String text) {
        showToast(text,Toast.LENGTH_SHORT);
    }

    public void showLongToast(String text) {
        showToast(text,Toast.LENGTH_LONG);
    }
    public void showShortToast(int resourceId) {
        showToast(resourceId,Toast.LENGTH_SHORT);
    }

    public void showLongToast(int resourceId) {
        showToast(resourceId,Toast.LENGTH_LONG);
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

    public void showToast(int resourceId, int showTime) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, resourceId, showTime);
        } else {
            mToast.setText(resourceId);
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
//	public void dismissWattingDialog(Context context) {
//		if (wattingDialog != null&&wattingDialog.getContext()==context)
//			wattingDialog.dismiss();
//	}

    /**
     * 弹出等待提示
     *
     * @param context
     */
//	public void showWaitting(Context context,int msgId) {
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

}
