package com.alphace.yuyan;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.alphace.calendar.CalendarCard;
import com.alphace.calendar.CalendarViewAdapter;
import com.alphace.calendar.CustomDate;
import com.alphace.calendar.CalendarCard.OnCellClickListener;
import com.alphace.constant.NomalConstant;
import com.alphace.database.DBHelper;
import com.alphace.tuli.MainActivity2;

public class HistoryDataActivity extends Activity implements
		OnCellClickListener, OnClickListener {
	private ViewPager mViewPager;
	private int mCurrentIndex = 498;
	private CalendarCard[] mShowViews;
	private CalendarViewAdapter<CalendarCard> adapter;
	private SildeDirection mDirection = SildeDirection.NO_SILDE;

	ArrayList<CustomDate> date_list;

	enum SildeDirection {
		RIGHT, LEFT, NO_SILDE;
	}

	private ImageView history_data_close;
	private TextView monthText;
	private TextView yearText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initDatabase();
		setContentView(R.layout.activity_history_data);

		mViewPager = (ViewPager) this.findViewById(R.id.vp_calendar);
		monthText = (TextView) this.findViewById(R.id.tvCurrentMonth);
		yearText = (TextView) this.findViewById(R.id.yearText);
		history_data_close = (ImageView) findViewById(R.id.history_data_close);
		history_data_close.setOnClickListener(this);

		CalendarCard[] views = new CalendarCard[3];
		for (int i = 0; i < 3; i++) {
			views[i] = new CalendarCard(this, this);
		}
		adapter = new CalendarViewAdapter<>(views);
		setViewPager();
	}

	private void initDatabase() {
		DBHelper dbHelper = new DBHelper(getApplicationContext(), "face", null,
				1);
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor result = database.rawQuery("select * from face", null);
		// NomalConstant.multiply_test_date_list = new ArrayList<CustomDate>();
		date_list = new ArrayList<CustomDate>();
		result.moveToFirst();
		Calendar calendar = Calendar.getInstance();
		while (!result.isAfterLast()) {
			calendar.setTimeInMillis(Long.valueOf(result.getString(result
					.getColumnIndex("time"))));
			// System.out.println(calendar.get(Calendar.YEAR) + "`" +
			// calendar.get(Calendar.MONTH) + 1 + "`"
			// + calendar.get(Calendar.DAY_OF_MONTH));
			date_list.add(new CustomDate(calendar.get(Calendar.YEAR), calendar
					.get(Calendar.MONTH) + 1, calendar
					.get(Calendar.DAY_OF_MONTH)));
			result.moveToNext();
		}
		date_list = refreshData(date_list);
		NomalConstant.multiply_test_date_list = date_list;
	}

	/**
	 * list去重
	 * 
	 * @author kist
	 */
	private ArrayList<CustomDate> refreshData(ArrayList<CustomDate> list) {
		ArrayList<CustomDate> temp_list = new ArrayList<CustomDate>();
		for (CustomDate date : list) {
			if (!isContains(date, temp_list)) {
				temp_list.add(date);
			}
		}
		return temp_list;
	}

	private boolean isContains(CustomDate date, ArrayList<CustomDate> list) {
		if (list.size() == 0) {
			return false;
		}
		for (CustomDate cd : list) {
			if (cd.getYear() == date.getYear()
					&& cd.getMonth() == date.getMonth()
					&& cd.getDay() == date.getDay()) {
				return true;
			}
		}
		return false;

	}

	private void setViewPager() {
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(498);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				measureDirection(position);
				updateCalendarView(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public void clickDate(CustomDate date) {
		// Toast.makeText(getApplicationContext(), date.toString(), 0).show();
		System.out.println(date_list.toString());
		if (isContains(date, date_list)) {
			Intent intent = new Intent(HistoryDataActivity.this,
					MainActivity2.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("page", 3);
			intent.putExtra("year", date.getYear());
			intent.putExtra("month", date.getMonth());
			intent.putExtra("day", date.getDay());
			startActivity(intent);
		}
	}

	@Override
	public void changeDate(CustomDate date) {
		Typeface type = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "fonts/HelveticaNeueLTStd-BdCn.otf");
		monthText.setTypeface(type);
		yearText.setTypeface(type);
		monthText.setText(date.month + "");
		yearText.setText(date.year + "");
	}

	/**
	 * 计算方向
	 * 
	 * @param arg0
	 */
	private void measureDirection(int arg0) {

		if (arg0 > mCurrentIndex) {
			mDirection = SildeDirection.RIGHT;

		} else if (arg0 < mCurrentIndex) {
			mDirection = SildeDirection.LEFT;
		}
		mCurrentIndex = arg0;
	}

	// 更新日历视图
	private void updateCalendarView(int arg0) {
		mShowViews = adapter.getAllItems();
		if (mDirection == SildeDirection.RIGHT) {
			mShowViews[arg0 % mShowViews.length].rightSlide();
		} else if (mDirection == SildeDirection.LEFT) {
			mShowViews[arg0 % mShowViews.length].leftSlide();
		}
		mDirection = SildeDirection.NO_SILDE;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.history_data_close:
			finish();
			break;

		default:
			break;
		}
	}

}
