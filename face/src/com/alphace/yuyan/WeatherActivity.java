package com.alphace.yuyan;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class WeatherActivity extends Activity implements OnClickListener {
	private TextView weather_temp;
	private TextView weather_temp2;
	private TextView weather_sk_time;
	private TextView weather_humidity;
	private TextView weather_uv;
	private TextView weather_loc;
	private TextView weather_advice32;
	private TextView weather_week;
	private ImageView back;
	private ImageView weather_icon;
	//
	private LocationClient mLocationClient;
	private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;
	private String tempcoor = "bd09ll";

	//
	private Handler mHandler;
	//
	private String humidity;
	private String sk_temp;
	private String sk_time;
	private String today_temperature;
	private String today_weather;
	private String today_city;
	private String today_week;
	private String uv_index;
	private String dressing_advice;
	private String weather_id_fa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		System.out.println("on cr");
		init();
		initLocation();
		this.mLocationClient.start();
		MyListener listener = new MyListener();
		this.mLocationClient.registerLocationListener(listener);
	}

	private void initLocation() {
		mLocationClient = new LocationClient(getApplicationContext());
		LocationClientOption localLocationClientOption = new LocationClientOption();
		localLocationClientOption.setLocationMode(this.tempMode);
		localLocationClientOption.setCoorType(this.tempcoor);
		localLocationClientOption.setScanSpan(0);
		localLocationClientOption.setOpenGps(true);
		localLocationClientOption.setLocationNotify(true);
		localLocationClientOption.setIgnoreKillProcess(true);
		this.mLocationClient.setLocOption(localLocationClientOption);
	}

	private class MyListener implements BDLocationListener {
		private MyListener() {
		}

		public void onReceiveLocation(BDLocation paramBDLocation) {
			String content = String.valueOf(System.currentTimeMillis()) + "#"
					+ paramBDLocation.getLongitude() + "#"
					+ paramBDLocation.getLatitude();
			final String lon = String.valueOf(paramBDLocation.getLongitude());
			final String lat = String.valueOf(paramBDLocation.getLatitude());
			System.out.println(content + "");
			new Thread(new Runnable() {

				@Override
				public void run() {
					// 国外
					// http://api.openweathermap.org/data/2.5/weather?lat=22&lon=-1&appid=44db6a862fba0b067b1930da0d769e98
					String uriAPI = "http://v.juhe.cn/weather/geo?format=1&key=cab150c9eeb3d5e796ab8c489e997267&lon="
							+ lon + "&lat=" + lat;
					/* 建立HTTP Get对象 */
					HttpGet httpRequest = new HttpGet(uriAPI);
					try {
						HttpResponse httpResponse = new DefaultHttpClient()
								.execute(httpRequest);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							/* 读 */
							String strResult = EntityUtils
									.toString(httpResponse.getEntity());
							System.out.println(strResult);
							printJson(strResult);
						} else {
							System.out.println("失败");
						}
					} catch (ClientProtocolException e) {
						System.out.println(e.getMessage().toString());
						e.printStackTrace();
					} catch (IOException e) {
						System.out.println(e.getMessage().toString());
						e.printStackTrace();
					}
				}

				// TODO天气预报json解析
				private void printJson(String strResult) {
					try {
						JSONObject result_object = new JSONObject(strResult);
						String result_code = result_object
								.optString("resultcode");
						if (Integer.valueOf(result_code) == 200) {
							JSONObject result = result_object
									.getJSONObject("result");

							JSONObject sk = result.getJSONObject("sk");
							humidity = sk.optString("humidity");
							sk_temp = sk.optString("temp");
							sk_time = sk.optString("time");
							JSONObject today = result.getJSONObject("today");
							today_temperature = today.optString("temperature");
							today_weather = today.optString("weather");
							today_city = today.optString("city");
							today_week = today.optString("week");
							uv_index = today.optString("uv_index");
							dressing_advice = today
									.optString("dressing_advice");
							JSONObject weather_id = today
									.optJSONObject("weather_id");
							weather_id_fa = weather_id.optString("fa");
							System.out.println(weather_id_fa + "``"
									+ weather_id.optString("fb"));

							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	private void refreshData() {
		weather_advice32.setText(dressing_advice);
		weather_temp2.setText(today_weather + today_temperature);
		weather_loc.setText(today_city);
		weather_humidity.setText("湿度：" + humidity);
		weather_sk_time.setText("【" + sk_time + "更新】");
		weather_temp.setText(sk_temp + "°");
		weather_uv.setText("紫外线：" + uv_index);
		weather_week.setText(today_week);
		getWeatherIcon();
	}

	private void getWeatherIcon() {
		if (weather_id_fa.equals("00")) {
			weather_icon.setImageResource(R.drawable.weather_1);
		} else if (weather_id_fa.equals("01") || weather_id_fa.equals("02")) {
			weather_icon.setImageResource(R.drawable.weather_2);
		} else if (weather_id_fa.equals("03") || weather_id_fa.equals("06")
				|| weather_id_fa.equals("07") || weather_id_fa.equals("08")
				|| weather_id_fa.equals("09") || weather_id_fa.equals("10")
				|| weather_id_fa.equals("11") || weather_id_fa.equals("12")
				|| weather_id_fa.equals("19") || weather_id_fa.equals("21")
				|| weather_id_fa.equals("22") || weather_id_fa.equals("23")
				|| weather_id_fa.equals("24") || weather_id_fa.equals("25")) {
			weather_icon.setImageResource(R.drawable.weather_3);
		} else if (weather_id_fa.equals("04") || weather_id_fa.equals("05")) {
			weather_icon.setImageResource(R.drawable.weather_4);
		} else if (weather_id_fa.equals("13") || weather_id_fa.equals("14")
				|| weather_id_fa.equals("15") || weather_id_fa.equals("16")
				|| weather_id_fa.equals("17") || weather_id_fa.equals("26")
				|| weather_id_fa.equals("27") || weather_id_fa.equals("28")) {
			weather_icon.setImageResource(R.drawable.weather_5);
		} else if (weather_id_fa.equals("18") || weather_id_fa.equals("53")) {
			weather_icon.setImageResource(R.drawable.weather_6);
		} else if (weather_id_fa.equals("20") || weather_id_fa.equals("29")
				|| weather_id_fa.equals("30") || weather_id_fa.equals("31")) {
			weather_icon.setImageResource(R.drawable.weather_7);
		} else {
			weather_icon.setImageResource(R.drawable.weather_1);
		}
	}

	private void init() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					refreshData();
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}

		};
		weather_icon = (ImageView) findViewById(R.id.weather_icon);
		weather_week = (TextView) findViewById(R.id.weather_week);
		back = (ImageView) findViewById(R.id.back);
		weather_temp = (TextView) findViewById(R.id.weather_temp);
		weather_temp2 = (TextView) findViewById(R.id.weather_temp2);
		weather_sk_time = (TextView) findViewById(R.id.sk_time);
		weather_humidity = (TextView) findViewById(R.id.weather_humidity);
		weather_uv = (TextView) findViewById(R.id.weather_uv);
		weather_loc = (TextView) findViewById(R.id.weather_loc);
		weather_advice32 = (TextView) findViewById(R.id.weather_advice32);
		//
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			this.finish();
			break;

		default:
			break;
		}
	}
}
