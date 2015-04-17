package com.gc.materialdesign.widgets;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.gc.materialdesign.R;
import com.gc.materialdesign.views.ButtonFlat;

public class Dialog extends android.app.Dialog{
	
	protected Context context;
    protected View view;
    protected View backView;
    protected String title;
    protected TextView titleTextView;

    protected ButtonFlat buttonAccept;
    protected ButtonFlat buttonCancel;

    protected String buttonAcceptText;
    protected String buttonCancelText;
//	String message;

    protected View.OnClickListener onAcceptButtonClickListener;
    protected View.OnClickListener onCancelButtonClickListener;

//    private View mContentView;
    protected  boolean canceledOnTouchOutside = true;

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside){
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    public Dialog(Context context,String title) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;// init Context
		this.title = title;
//        mContentView = contentView;
	}

	@Override
	  protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog);
	    
		view = findViewById(R.id.contentDialog);
		backView = findViewById(R.id.dialog_rootView);
		backView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (canceledOnTouchOutside&&(event.getX() < view.getLeft()
						|| event.getX() >view.getRight()
						|| event.getY() > view.getBottom() 
						|| event.getY() < view.getTop())) {
					dismiss();
				}
				return false;
			}
		});


		
	    this.titleTextView = (TextView) findViewById(R.id.title);

        if(!TextUtils.isEmpty(title)){
            titleTextView.setVisibility(View.VISIBLE);
	        setTitle(title);
        }

        if(!TextUtils.isEmpty(buttonAcceptText)&&onAcceptButtonClickListener!=null){
            showButtons = true;
            buttonAccept = (ButtonFlat) findViewById(R.id.button_accept);
            buttonAccept.setVisibility(View.VISIBLE);
            buttonAccept.setText(buttonAcceptText);
	        buttonAccept.setOnClickListener(onAcceptButtonClickListener);
        }

	    if(buttonCancelText != null&&onCancelButtonClickListener!=null){
            showButtons = true;
		    buttonCancel = (ButtonFlat) findViewById(R.id.button_cancel);
		    buttonCancel.setVisibility(View.VISIBLE);
		    buttonCancel.setText(buttonCancelText);
            buttonCancel.setOnClickListener(onCancelButtonClickListener);
        }

        if(showButtons){
            findViewById(R.id.ll_buttons).setVisibility(View.VISIBLE);
        }
	}

    boolean showButtons;

    public void setButtonAccept(int textResource,View.OnClickListener clickListener){
        setButtonAccept(context.getString(textResource),clickListener);
    }

    public void setButtonCancel(int Resource,View.OnClickListener clickListener){
        setButtonCancel(context.getString(Resource),clickListener);
    }
    public void setButtonAccept(String text,View.OnClickListener clickListener){
        buttonAcceptText = text;
        onAcceptButtonClickListener = clickListener;
    }

    public void setButtonCancel(String text,View.OnClickListener clickListener){
        buttonCancelText = text;
        onCancelButtonClickListener = clickListener;
    }
	
	@Override
	public void show() {
		// TODO 自动生成的方法存根
		super.show();
		// set dialog enter animations
		view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
		backView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_root_show_amin));
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		if(TextUtils.isEmpty(title))
			titleTextView.setVisibility(View.GONE);
		else{
			titleTextView.setVisibility(View.VISIBLE);
			titleTextView.setText(title);
		}
	}

    boolean dismissing;
	@Override
	public void dismiss() {
        if(!dismissing&&isShowing()) {
            dismissing = true;
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.dialog_main_hide_amination);
            anim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Dialog.super.dismiss();
//                        }
//                    },200);//200要和dialog_main_hide_amination中的duration一致
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Dialog.super.dismiss();
                    dismissing = false;
                }
            });

            anim.setFillAfter(true);
            Animation backAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_root_hide_amin);
            backAnim.setFillAfter(true);
            view.startAnimation(anim);
            backView.startAnimation(backAnim);
        }
	}
	


}
