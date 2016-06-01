package com.alphace.tuli;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alphace.calendar.CustomDate;
import com.alphace.constant.FaceData;
import com.alphace.constant.NomalConstant;
import com.alphace.database.DBHelper;
import com.alphace.utils.GetTypeUtils;
import com.alphace.utils.MyUtils;
import com.alphace.utils.SharedpreferencesUtil;
import com.alphace.view.MyProgressBar;
import com.alphace.yuyan.R;

public class Fragment_History extends Fragment implements OnClickListener,
		LineChartOnValueSelectListener, ViewportChangeListener {
	//
	private LinearLayout layout_chart;
	private LineChartView chart;
	private TextView date_time;
	private TextView date_week;
	private ImageView no_data_img;
	//
	private MyProgressBar face_water_progressBar;
	private MyProgressBar face_oil_progressBar;
	private MyProgressBar skin_light_progressBar;
	private MyProgressBar skin_average_progressBar;
	private TextView water_progress;
	private TextView oil_progress;
	private TextView light_progress;
	private TextView average_progress;
	private TextView water_type;
	private TextView oil_type;
	private TextView light_type;
	private TextView average_type;

	private ArrayList<FaceData> local_data;
	private ArrayList<FaceData> local_data2;
	private int water, oil, light, average;
	private SharedpreferencesUtil sharedpreferencesUtil;
	//
	private DBHelper dbHelper;
	private Handler mHandler;
	//
	// private ArrayList<AxisValue> mAxisValues = new ArrayList<AxisValue>();
	//
	List<PointValue> values;
	List<PointValue> values2;
	LineChartData data;
	LineChartData data2;
	//
	private WaterThread waterThread;
	private OilThread oilThread;
	private LightThread lightThread;
	private AverageThread averageThread;

	//
	private int index;
	private boolean exit = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_history, null);
		init(view);
		initDataBase();
		return view;

	}

	private void init(View view) {
		no_data_img = (ImageView) view.findViewById(R.id.no_data_img);
		date_week = (TextView) view.findViewById(R.id.date_week);
		date_time = (TextView) view.findViewById(R.id.date_time);
		layout_chart = (LinearLayout) view.findViewById(R.id.layout_chart);
		face_water_progressBar = (MyProgressBar) view
				.findViewById(R.id.face_water_progressBar);
		face_oil_progressBar = (MyProgressBar) view
				.findViewById(R.id.face_oil_progressBar);
		skin_light_progressBar = (MyProgressBar) view
				.findViewById(R.id.skin_light_progressBar);
		skin_average_progressBar = (MyProgressBar) view
				.findViewById(R.id.skin_average_progressBar);
		water_progress = (TextView) view.findViewById(R.id.water_progress);
		oil_progress = (TextView) view.findViewById(R.id.oil_progress);
		light_progress = (TextView) view.findViewById(R.id.light_progress);
		average_progress = (TextView) view.findViewById(R.id.average_progress);
		water_type = (TextView) view.findViewById(R.id.water_type);
		oil_type = (TextView) view.findViewById(R.id.oil_type);
		light_type = (TextView) view.findViewById(R.id.light_type);
		average_type = (TextView) view.findViewById(R.id.average_type);
		//
		sharedpreferencesUtil = new SharedpreferencesUtil(getActivity()
				.getApplicationContext());

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					String[] strs = new String[] {};
					strs = (String[]) msg.obj;
					date_time.setText(strs[1] + "");
					date_week.setText(strs[0] + "");
					FaceData f = local_data.get(msg.arg1);
					// showProgressBar(f.getWater(), f.getOil(), f.getLight(),
					// f.getAverage());
					break;
				case 1:
					water_progress.setText(msg.arg1 + "");
					break;
				case 2:
					oil_progress.setText(msg.arg1 + "");
					break;
				case 3:
					light_progress.setText(msg.arg1 + "");
					break;
				case 4:
					average_progress.setText(msg.arg1 + "");
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}

		};
	}

	private void initChartPosition() {
		int year = getActivity().getIntent().getIntExtra("year", 0);
		int month = getActivity().getIntent().getIntExtra("month", 0);
		int day = getActivity().getIntent().getIntExtra("day", 0);
		System.out.println(year + "" + month + "" + day + "");

		if (year != 0 && month != 0 && day != 0) {
			index = day2index(new CustomDate(year, month, day), local_data);
			//
			Message msg = new Message();
			msg.what = 0;
			msg.arg1 = index;
			msg.obj = new String[] {
					MyUtils.long2week(local_data.get(index).getTime()),
					MyUtils.long2date(local_data.get(index).getTime()) };
			mHandler.sendMessage(msg);
			//
			if ((local_data.size() - index) <= 5) {
				index = local_data.size() - 5;
			}
		} else {
			index = local_data.size() - 5;
		}
		//
		final Viewport v = new Viewport(chart.getMaximumViewport());
		v.bottom = -5;
		v.top = 105; //
		// v.left = values.size() - 5.3f; //
		// v.right = values.size() - 0.7f;
		v.left = values.size() - 5f; //
		v.right = values.size() - 1f;
		if (index == 0) {
			v.left = index;
		} else
			// v.left = index - 0.3f;
			// v.right = index + 4.3f;
			v.left = index;
		v.right = index + 4.1f;
		final Viewport v_max = new Viewport(chart.getMaximumViewport());
		v_max.bottom = getMinData(local_data) - 2;
		v_max.top = getMaxData(local_data) + 2;
		v_max.left = 0;
		// v_max.right = values.size() - 0.7f;
		v_max.right = values.size() - 0.9f;

		// You have to set max and current viewports separately.
		// I changing current viewport with animation in this case.
		chart.setMaximumViewport(v_max);
		chart.setCurrentViewport(v);
		// chart.setCurrentViewportWithAnimation(v);
		System.out.println(chart.getCurrentViewport().left + "left right"
				+ chart.getCurrentViewport().right);
		if (local_data.size() == 1) {
			System.out.println(chart.getCurrentViewport().left + "left right"
					+ chart.getCurrentViewport().right);
			final Viewport v1m = new Viewport(chart.getMaximumViewport());
			v1m.bottom = getMinData(local_data) * 0.5f - 7;
			v1m.top = getMaxData(local_data) + 7;
			v1m.left = 0.2f;
			v1m.right = 1.8f;
			chart.setMaximumViewport(v1m);
			chart.setCurrentViewport(v1m);
		}
		if (local_data.size() == 2) {
			System.out.println(chart.getCurrentViewport().left + "left right"
					+ chart.getCurrentViewport().right);
			final Viewport v2m = new Viewport(chart.getMaximumViewport());
			v2m.bottom = getMinData(local_data) * 0.5f - 10;
			v2m.top = getMaxData(local_data) + 10;
			v2m.left = 0.2f;
			v2m.right = 2.8f;
			chart.setMaximumViewport(v2m);
			chart.setCurrentViewport(v2m);
		}

	}

	private void initChartData() {
		local_data = new ArrayList<FaceData>();
		data = new LineChartData();
		values = new ArrayList<PointValue>();

		dbHelper = new DBHelper(getActivity().getApplicationContext(), "face",
				null, 1);
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from face where type=?",
				new String[] { "0" });
		cursor.moveToFirst();
		int count = cursor.getCount();
		System.out.println("count" + count);
		while (!cursor.isAfterLast()) {
			values.add(new PointValue(cursor.getPosition(), cursor
					.getInt(cursor.getColumnIndex("average"))));
			System.out.println(cursor.getPosition() + "``"
					+ cursor.getInt(cursor.getColumnIndex("average")));
			local_data.add(new FaceData(cursor.getInt(cursor
					.getColumnIndex("water")), cursor.getInt(cursor
					.getColumnIndex("oil")), cursor.getInt(cursor
					.getColumnIndex("light")), cursor.getInt(cursor
					.getColumnIndex("uniform")), cursor.getInt(cursor
					.getColumnIndex("average")), Long.valueOf(cursor
					.getString(cursor.getColumnIndex("time")))));
			cursor.moveToNext();
		}
		cursor.close();
		if (count == 1) {
			float y = values.get(0).getY();
			values.add(new PointValue(1, y));
			values.add(new PointValue(2, y * 0.5f));
			values.get(0).set(0, y * 0.5f);
		}
		if (count == 2) {
			float y0 = values.get(0).getY();
			float y1 = values.get(1).getY();
			float ym = getMaxData(local_data);
			values.get(0).set(0, ym * 0.5f);
			values.get(1).set(1, y0);
			values.add(new PointValue(2, y1));
			values.add(new PointValue(3, ym * 0.5f));
		}
		//
		local_data2 = new ArrayList<FaceData>();
		data2 = new LineChartData();
		values2 = new ArrayList<PointValue>();
		//
		Cursor cursor2 = database.rawQuery("select * from face where type=?",
				new String[] { "1" });
		cursor2.moveToFirst();
		int count2 = cursor2.getCount();
		System.out.println("count2" + count2);
		while (!cursor2.isAfterLast()) {
			values2.add(new PointValue(cursor2.getPosition(), cursor2
					.getInt(cursor2.getColumnIndex("average"))));
			local_data2.add(new FaceData(cursor2.getInt(cursor2
					.getColumnIndex("water")), cursor2.getInt(cursor2
					.getColumnIndex("oil")), cursor2.getInt(cursor2
					.getColumnIndex("light")), cursor2.getInt(cursor2
					.getColumnIndex("uniform")), cursor2.getInt(cursor2
					.getColumnIndex("average")), Long.valueOf(cursor2
					.getString(cursor2.getColumnIndex("time")))));
			cursor2.moveToNext();
		}
		cursor2.close();
		//
		// values2.clear();
		// values2.addAll(values);
		// for (int i = 0; i < values2.size(); i++) {
		// values2.set(i, new PointValue(i, 48));
		// }
		//
		// Line line2 = new Line(values2);
		// line2.setColor(0xff00d1c4);
		// line2.setShape(ValueShape.CIRCLE);
		// line2.setCubic(true);
		// line2.setFilled(false);
		// line2.setPointColor(0xff00d1c4);
		// line2.setPointRadius(0);
		// line2.setStrokeWidth(1);
		// line2.setHasLabels(false);
		// // line.setHasLabelsOnlyForSelected(false);
		// line2.setHasLines(true);
		// line2.setHasPoints(true);
		//
		//
		System.out.println(values.size() + "values.size()");
		Line line = new Line(values);
		line.setColor(0xff00d1c4);
		line.setShape(ValueShape.CIRCLE);
		line.setCubic(true);
		line.setFilled(true);
		line.setPointColor(0xff00d1c4);
		line.setPointRadius(5);
		line.setStrokeWidth(0);
		line.setHasLabels(false);
		// line.setHasLabelsOnlyForSelected(false);
		line.setHasLines(true);
		line.setHasPoints(true);

		List<Line> lines = new ArrayList<Line>();
		lines.add(line);
		// lines.add(line2);
		data.setLines(lines);

		// Axis
		Axis axisX = new Axis().setHasLines(false);
		Axis axisY = new Axis().setHasLines(false);
		axisX.setAutoGenerated(false);
		// axisX.setHasTiltedLabels(true);
		axisX.setHasSeparationLine(false);
		axisY.setAutoGenerated(false);
		axisY.setHasSeparationLine(false);
		// TODO
		// axisX.setValues(setAxisXLables(local_data));
		// axisX.setName("");
		// axisY.setName("");
		data.setAxisXBottom(axisX);
		data.setAxisYLeft(axisY);
		chart.setLineChartData(data);

	}

	private void initChartData2() {
		local_data2 = new ArrayList<FaceData>();
		values2 = new ArrayList<PointValue>();
		//
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor2 = database.rawQuery("select * from face where type=?",
				new String[] { "0" });
		cursor2.moveToFirst();
		int count2 = cursor2.getCount();
		System.out.println("count2" + count2);
		while (!cursor2.isAfterLast()) {
			values2.add(new PointValue(cursor2.getPosition(), cursor2
					.getInt(cursor2.getColumnIndex("average"))));
			local_data2.add(new FaceData(cursor2.getInt(cursor2
					.getColumnIndex("water")), cursor2.getInt(cursor2
					.getColumnIndex("oil")), cursor2.getInt(cursor2
					.getColumnIndex("light")), cursor2.getInt(cursor2
					.getColumnIndex("uniform")), cursor2.getInt(cursor2
					.getColumnIndex("average")), Long.valueOf(cursor2
					.getString(cursor2.getColumnIndex("time")))));
			cursor2.moveToNext();
		}
		cursor2.close();
		//
	}

	// private ArrayList<AxisValue> setAxisXLables(ArrayList<FaceData> data) {
	// for (int i = 0; i < data.size(); i++) {
	// mAxisValues.add(new AxisValue(i).setLabel(MyUtils.long2time(data
	// .get(i).getTime())));
	// }
	// return mAxisValues;
	// }

	@SuppressLint("ClickableViewAccessibility")
	private void initChart() {
		chart = new LineChartView(getActivity());

		chart.setOnValueTouchListener(new MyLineChartOnValueSelectListener());

		chart.setViewportChangeListener(this);
		chart.setZoomEnabled(false);
		// chart.setZoomType(ZoomType.HORIZONTAL);
	}

	class NotMyLineChartOnValueSelectListener implements
			LineChartOnValueSelectListener {

		@Override
		public void onValueDeselected() {

		}

		@Override
		public void onValueSelected(int lineIndex, int pointIndex,
				PointValue value) {

		}

	}

	class MyLineChartOnValueSelectListener implements
			LineChartOnValueSelectListener {

		@Override
		public void onValueDeselected() {

		}

		@Override
		public void onValueSelected(int lineIndex, int pointIndex,
				PointValue value) {
			if (local_data.size() == 1) {
				pointIndex = 0;
			}
			if (local_data.size() == 2) {
				pointIndex = pointIndex - 1;
			}
			FaceData d = local_data.get(pointIndex);
			showProgressBar(d.getWater(), d.getOil(), d.getLight(),
					d.getAverage(), true);
			chart.setOnValueTouchListener(new NotMyLineChartOnValueSelectListener());
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					chart.setOnValueTouchListener(new MyLineChartOnValueSelectListener());
				}
			}, 600);
			date_time.setText(MyUtils.long2date(d.getTime()));
			date_week.setText(MyUtils.long2week(d.getTime()));
			System.out.println("````" + MyUtils.long2date(d.getTime()));
			Typeface type = Typeface.createFromAsset(getActivity()
					.getApplicationContext().getAssets(),
					"fonts/HelveticaNeueLTStd-CnO.otf");
			date_time.setTypeface(type);
		}

	}

	private void initDataBase() {
		DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext(),
				"face", null, 1);
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor result = database.rawQuery("select * from face", null);
		int count = result.getCount();
		if (count == 0) {
			no_data_img.setVisibility(View.VISIBLE);
			showProgressBar(60, 60, 60, 60, false);
			System.out.println("no_data_image.setVisibility" + count);
		} else {
			no_data_img.setVisibility(View.INVISIBLE);
			initChart();
			initChartData();
			initChartPosition();
			layout_chart.removeAllViews();
			layout_chart.addView(chart);

			date_week.setText(MyUtils.long2week(local_data.get(
					local_data.size() - 1).getTime()));
			date_time.setText(MyUtils.long2date(local_data.get(
					local_data.size() - 1).getTime()));
			Typeface type = Typeface.createFromAsset(getActivity()
					.getApplicationContext().getAssets(),
					"fonts/HelveticaNeueLTStd-CnO.otf");
			date_time.setTypeface(type);
			FaceData f = local_data.get(local_data.size() - 1);
			showProgressBar(f.getWater(), f.getOil(), f.getLight(),
					f.getAverage(), false);
			// face_water_progressBar.setProgress(f.getWater());
			// face_oil_progressBar.setProgress(f.getOil());
			// skin_light_progressBar.setProgress(f.getLight());
			// skin_average_progressBar.setProgress(f.getAverage());
		}
		// TODO
		// no_data_image.setVisibility(View.GONE);
	}

	@Override
	public void onValueDeselected() {
		System.out.println("onValueDeselected");
	}

	@Override
	public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
		// Toast.makeText(this, "Selected: " + value + "``" + pointIndex,
		// Toast.LENGTH_SHORT).show();
		// if (local_data.size() == 1) {
		// pointIndex = 0;
		// }
		// if (local_data.size() == 2) {
		// pointIndex = pointIndex - 1;
		// }
		// FaceData d = local_data.get(pointIndex);
		// showProgressBar(d.getWater(), d.getOil(), d.getLight(),
		// d.getAverage(),
		// true);
		// chart.setOnValueTouchListener(null);
		// mHandler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// chart.setOnValueTouchListener();
		// }
		// }, 600);
		// date_time.setText(MyUtils.long2date(d.getTime()));
		// date_week.setText(MyUtils.long2week(d.getTime()));
		// System.out.println("````" + MyUtils.long2date(d.getTime()));
		// Typeface type = Typeface.createFromAsset(getActivity()
		// .getApplicationContext().getAssets(),
		// "fonts/HelveticaNeueLTStd-CnO.otf");
		// date_time.setTypeface(type);
	}

	private int getMaxData(ArrayList<FaceData> data) {
		int max = 0;
		for (FaceData faceData : data) {
			if (faceData.getAverage() >= max) {
				max = faceData.getAverage();
			}
		}
		return max;

	}

	private int getMinData(ArrayList<FaceData> data) {
		int min = 100;
		for (FaceData faceData : data) {
			if (faceData.getAverage() <= min) {
				min = faceData.getAverage();
			}
		}
		return min;
	}

	/**
	 * 传入日期，得到viewport需要的index
	 * 
	 * @author kist
	 * @return
	 */
	private int day2index(CustomDate date, ArrayList<FaceData> data_list) {
		Calendar calendar = Calendar.getInstance();
		for (FaceData faceData : data_list) {
			// calendar.setTimeInMillis(NomalConstant.TIMEDATASHIFT+faceData.getTime());
			calendar.set(date.getYear(), date.getMonth() - 1, date.getDay(), 0,
					0);

			if (faceData.getTime() >= calendar.getTimeInMillis()) {
				return data_list.indexOf(faceData);
			}
		}
		return data_list.size();

	}

	private void showProgressBar(int a, int b, int c, int d, boolean isAnim) {
		waterThread = new WaterThread();
		oilThread = new OilThread();
		lightThread = new LightThread();
		averageThread = new AverageThread();
		water = a;
		oil = b;
		light = c;
		average = d;
		water_type.setText(GetTypeUtils.getWaterType(water));
		oil_type.setText(GetTypeUtils.getOilType(oil));
		light_type.setText(GetTypeUtils.getLightType(light));
		average_type.setText(GetTypeUtils.getAverageType(average));
		if (isAnim) {
			waterThread.start();
			oilThread.start();
			lightThread.start();
			averageThread.start();
		} else {
			face_water_progressBar.setProgress(water);
			face_oil_progressBar.setProgress(oil);
			skin_light_progressBar.setProgress(light);
			skin_average_progressBar.setProgress(average);
			water_progress.setText(water + "");
			oil_progress.setText(oil + "");
			light_progress.setText(light + "");
			average_progress.setText(average + "");
		}
	}

	class WaterThread extends Thread {
		@Override
		public void run() {
			for (int i = 1; i <= water; i++) {
				face_water_progressBar.setProgress(i);
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = i;
				mHandler.sendMessage(msg);
				// water_progress.setText(i+"");
				try {
					sleep(600 / water);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	class OilThread extends Thread {

		@Override
		public void run() {
			for (int j = 1; j <= oil; j++) {
				face_oil_progressBar.setProgress(j);
				Message msg = new Message();
				msg.what = 2;
				msg.arg1 = j;
				mHandler.sendMessage(msg);
				// oil_progress.setText(j);
				try {
					sleep(600 / oil);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class LightThread extends Thread {

		@Override
		public void run() {
			for (int k = 1; k <= light; k++) {
				skin_light_progressBar.setProgress(k);
				Message msg = new Message();
				msg.what = 3;
				msg.arg1 = k;
				mHandler.sendMessage(msg);
				// skin_progress.setText(k);
				try {
					sleep(600 / light);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class AverageThread extends Thread {

		@Override
		public void run() {
			for (int m = 1; m <= average; m++) {
				skin_average_progressBar.setProgress(m);
				Message msg = new Message();
				msg.what = 4;
				msg.arg1 = m;
				mHandler.sendMessage(msg);
				// skin_progress.setText(k);
				try {
					sleep(600 / average);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void onViewportChanged(Viewport viewport) {
		// Log.e("onViewportChanged", viewport.left + "``" + viewport.right);
		int index2;
		System.out.println("viewport.left" + viewport.left);
		if (viewport.left == 0.0f) {
			index2 = 0;
		} else {
			index2 = (int) (viewport.left) + 1;
		}
		System.out.println(index2 + "```" + local_data.size());
		//
		if (index2 >= 0 && index2 < local_data.size()) {
			Message msg = new Message();
			msg.what = 0;
			msg.arg1 = index2;
			msg.obj = new String[] {
					MyUtils.long2week(local_data.get(index2).getTime()),
					MyUtils.long2date(local_data.get(index2).getTime()) };
			mHandler.sendMessage(msg);
		}
		//
	}

	@Override
	public void onClick(View arg0) {

	}
}
