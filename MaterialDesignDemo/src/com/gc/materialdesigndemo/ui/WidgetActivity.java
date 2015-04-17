package com.gc.materialdesigndemo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.ColorSelector;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.gc.materialdesigndemo.R;

public class WidgetActivity extends Activity {

	private int backgroundColor = Color.parseColor("#1E88E5");

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widgets);
		
//		SHOW SNACKBAR
		findViewById(R.id.buttonSnackBar).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View flatButton) {
				new SnackBar(WidgetActivity.this,
						"Do you want change color of this button to red?",
						"yes", new OnClickListener() {

							@Override
							public void onClick(View v) {
								ButtonFlat btn = (ButtonFlat) findViewById(R.id.buttonSnackBar);
//								btn.setTextColor(Color.RED);
							}
						}).show();
			}
		});
//		SHOW DiALOG
		findViewById(R.id.buttonDialog).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View flatButton) {
				Dialog dialog = new Dialog(WidgetActivity.this, "Title");
//				dialog.setOnAcceptButtonClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Toast.makeText(WidgetActivity.this, "Click accept button", Toast.LENGTH_LONG).show();
//					}
//				});
//				dialog.setOnCancelButtonClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Toast.makeText(WidgetActivity.this, "Click cancel button", Toast.LENGTH_LONG).show();
//					}
//				});
				dialog.show();
			}
		});
//		SHOW COLOR SEECTOR
		findViewById(R.id.buttonColorSelector).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View flatButton) {
				new ColorSelector(WidgetActivity.this, Color.RED, null).show();
			}
		});
	}

}
