package com.madxstudio.co8.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.madxstudio.co8.R;
import com.madxstudio.co8.util.MyDateUtils;
import com.madxstudio.co8.widget.NumberPicker.OnValueChangeListener;

import java.util.Calendar;

public class DatePicker extends FrameLayout {

	private Context mContext;
	private NumberPicker mDayPicker;
	private NumberPicker mMonthPicker;
	private NumberPicker mYearPicker;
	private Calendar mCalendar;

	private String[] mMonthDisplay;

	public DatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mCalendar = Calendar.getInstance();
		initMonthDisplay();
		((LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.date_picker, this, true);
		mDayPicker = (NumberPicker) findViewById(R.id.date_day);
		mMonthPicker = (NumberPicker) findViewById(R.id.date_month);
		mYearPicker = (NumberPicker) findViewById(R.id.date_year);

		mDayPicker.setMinValue(1);
		mDayPicker.setMaxValue(31);
		mDayPicker.setValue(20);
		mDayPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);

		mMonthPicker.setMinValue(0);
		mMonthPicker.setMaxValue(11);
		mMonthPicker.setDisplayedValues(mMonthDisplay);
		mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));

		mYearPicker.setMinValue(1950);
		mYearPicker.setMaxValue(2100);
		mYearPicker.setValue(mCalendar.get(Calendar.YEAR));

		mMonthPicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				mCalendar.set(Calendar.MONTH, newVal);
				updateDate();
			}
		});
		mDayPicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {

				mCalendar.set(Calendar.DATE, newVal);
				updateDate();
			}
		});
		mYearPicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				mCalendar.set(Calendar.YEAR, newVal);
				updateDate();

			}
		});

		updateDate();

	}

	private void initMonthDisplay() {

		/**wing modified for system month name desc*/
//        mMonthDisplay = mContext.getResources().getStringArray(R.array.months);
        mMonthDisplay = MyDateUtils.getMonthNameArray(false);
		/**wing modified for system month name desc*/
	}

	private void updateDate() {
		mDayPicker.setMinValue(mCalendar.getActualMinimum(Calendar.DATE));
		mDayPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.DATE));
		mDayPicker.setValue(mCalendar.get(Calendar.DATE));
		mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));
		mYearPicker.setValue(mCalendar.get(Calendar.YEAR));
	}

	public DatePicker(Context context) {
		this(context, null);
	}

	public String getDate() {
		String date = mYearPicker.getValue() + "-"
				+ (mMonthPicker.getValue() + 1) + "-" + mDayPicker.getValue();
		return date;

	}

	public int getDay() {
		return mCalendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getMonth() {
		return mCalendar.get(Calendar.MONTH);
	}

	public int getYear() {
		return mCalendar.get(Calendar.YEAR);
	}

	public void setCalendar(Calendar calendar) {
		mCalendar = calendar;
		updateDate();
	}

}
