package com.madx.bwm.widget;

import android.app.Dialog;
import android.content.Context;

public class CustomDialog extends Dialog {

	final static int DEFAULT_WIDTH = 160;
	final static int DEFAULT_HEIGHT = 120;

	public CustomDialog(Context context, int layout, int style) {
		this(context, 0, 0, layout, style);
		// this(context,DEFAULT_WIDTH,DEFAULT_HEIGHT,layout,style);
	}

	public CustomDialog(Context context, int width, int height, int layout,
			int style) {
		super(context, style);
		// set content
		setContentView(layout);

//		if (width != 0 && height != 0) {
			// Window window = getWindow();
			// WindowManager.LayoutParams params = window.getAttributes();
			//
			// //set width,height by density and gravity
			// float density = getDensity(context);
			// params.width = (int) (width*density);
			// params.height = (int) (height*density);
			// params.gravity = Gravity.CENTER;
			// window.setAttributes(params);
//		}

	}

//	private float getDensity(Context context) {
//		Resources resources = context.getResources();
//		DisplayMetrics dm = resources.getDisplayMetrics();
//		return dm.density;
//	}

}
