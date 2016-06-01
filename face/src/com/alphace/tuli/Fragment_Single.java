package com.alphace.tuli;

import java.util.Random;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alphace.constant.FaceData;
import com.alphace.constant.NomalConstant;
import com.alphace.service.BluetoothLeService;
import com.alphace.utils.GetTypeUtils;
import com.alphace.utils.SharedpreferencesUtil;
import com.alphace.view.MyProgressBar;
import com.alphace.view.RoundProgressBar;
import com.alphace.yuyan.Bind_Device_Anim_Activity;
import com.alphace.yuyan.R;

public class Fragment_Single extends Fragment implements OnClickListener {
	private final static String TAG = Fragment_Single.class.getSimpleName();
	private TextView tips1;
	private String address;
	private Handler mHandler;
	private SharedpreferencesUtil sharedpreferencesUtil;
	private boolean state_flag = true;

	//
	public final static UUID UUID_BLE_CHARACTERISTIC_F1 = UUID
			.fromString(NomalConstant.BLE_CHARACTERISTIC_F1);
	public final static UUID UUID_BLE_CHARACTERISTIC_F2 = UUID
			.fromString(NomalConstant.BLE_CHARACTERISTIC_F2);
	public final static UUID UUID_BLE_CHARACTERISTIC_F3 = UUID
			.fromString(NomalConstant.BLE_CHARACTERISTIC_F3);
	public final static UUID UUID_BLE_SERVICE = UUID
			.fromString(NomalConstant.BLE_SERVICE);

	private byte[] bmp;
	//
	//
	private MyProgressBar face_water_progressBar;
	private MyProgressBar face_oil_progressBar;
	private MyProgressBar skin_light_progressBar;
	//
	private TextView water_progress;
	private TextView oil_progress;
	private TextView light_progress;
	//
	private TextView water_type;
	private TextView oil_type;
	private TextView light_type;
	private int water = 60, oil = 60, light = 60;
	private int result = 60;
	private ImageView result_img;
	private RoundProgressBar result_progress;
	private RelativeLayout tips1_back;
	private ImageView tips1_point;
	//
	private int STATE = 0;
	private int STATE_NOTBIND = 1;
	private int STATE_NOTCONNECTED = 2;
	private int STATE_CONNECTED = 3;

	//
	private ServiceConnection mServiceConnection;
	private BluetoothLeService mBluetoothLeService;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_single, null);
		init(view);
		// init_State();
		Log.e(TAG, "on create");
		// connectService();
		// byte[] b = new byte[1];
		// b[0] = 3;
		// mBluetoothLeService.writeCharacteristic(b,
		// NomalConstant.BLE_CHARACTERISTIC_F2);
		// MainActivity2 ma = new MainActivity2();
		// ma.setOnBleChangeListener(new MainActivity2.OnBleChangeListener() {
		//
		// @Override
		// public void onBle(int i) {
		// System.out.println("````````huidiao" + i);
		// }
		// });
		// ma.doBle(666);

		// result_progress.setProgress(60);
		// face_water_progressBar.setProgress(60);
		// face_oil_progressBar.setProgress(60);
		// skin_light_progressBar.setProgress(60);
		// water_type.setText(GetTypeUtils.getWaterType(water));
		// oil_type.setText(GetTypeUtils.getOilType(oil));
		// light_type.setText(GetTypeUtils.getLightType(light));
		// GetTypeUtils.setResultImg(60, result_img);
		// new TestThread().start();
		return view;
	}

	private void init_State() {
		address = new SharedpreferencesUtil(getActivity()
				.getApplicationContext()).getMyDeviceMac();
		if (address.equals("")) {
			STATE = STATE_NOTBIND;
			tips1_point.setVisibility(View.INVISIBLE);
			tips1.setText("绑定设备");
			tips1_back.setClickable(true);
			System.out.println("绑定设备");
		} else {
			System.out.println("address" + address);
			tips1_back.setClickable(false);
			if (sharedpreferencesUtil.getConnectState()) {
				STATE = STATE_CONNECTED;
				System.out.println("tips thread");
			} else {
				STATE = STATE_NOTCONNECTED;
				tips1.setText("设备未连接，请打开设备");
				System.out.println("正在连接");
			}
		}

	}

	private void init(View view) {
		sharedpreferencesUtil = new SharedpreferencesUtil(getActivity()
				.getApplicationContext());

		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					result_progress.setProgress(msg.arg1);

					break;
				case 1:
					water_type.setText(GetTypeUtils.getWaterType(water));
					water_progress.setText(msg.arg1 + "");
					break;
				case 2:
					oil_type.setText(GetTypeUtils.getOilType(oil));
					oil_progress.setText(msg.arg1 + "");
					break;
				case 3:
					light_type.setText(GetTypeUtils.getLightType(light));
					light_progress.setText(msg.arg1 + "");
					break;
				case 5:
					tips1_point.setVisibility(View.VISIBLE);
					STATE = STATE_CONNECTED;
					break;
				case 6:
					if (!address.equals("")) {
						tips1_point.setVisibility(View.INVISIBLE);
						tips1.setText("设备未连接，请打开设备");
						STATE = STATE_NOTCONNECTED;
					} else {
						STATE = STATE_NOTBIND;
						tips1.setText("绑定设备");
						tips1_point.setVisibility(View.INVISIBLE);
						tips1_back.setClickable(true);
					}
					break;
				case 22:
					tips1.setText(msg.obj.toString());
					tips1_point.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}

		};
		tips1_point = (ImageView) view.findViewById(R.id.tips1_point);
		tips1_back = (RelativeLayout) view.findViewById(R.id.tips1_back);
		tips1_back.setOnClickListener(this);
		result_img = (ImageView) view.findViewById(R.id.result_img);
		result_progress = (RoundProgressBar) view
				.findViewById(R.id.result_progress);
		Typeface type = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(), "fonts/Impacted.ttf");
		result_progress.setTypeface(type);
		water_type = (TextView) view.findViewById(R.id.water_type);
		oil_type = (TextView) view.findViewById(R.id.oil_type);
		light_type = (TextView) view.findViewById(R.id.light_type);

		water_progress = (TextView) view.findViewById(R.id.water_progress);
		oil_progress = (TextView) view.findViewById(R.id.oil_progress);
		light_progress = (TextView) view.findViewById(R.id.light_progress);

		face_water_progressBar = (MyProgressBar) view
				.findViewById(R.id.face_water_progressBar);
		face_oil_progressBar = (MyProgressBar) view
				.findViewById(R.id.face_oil_progressBar);
		skin_light_progressBar = (MyProgressBar) view
				.findViewById(R.id.skin_light_progressBar);
		tips1 = (TextView) view.findViewById(R.id.tips1);
		address = new SharedpreferencesUtil(getActivity()).getMyDeviceMac();

		String[] str = sharedpreferencesUtil.getLastSingleResult();
		water = Integer.valueOf(str[0]);
		oil = Integer.valueOf(str[1]);
		light = Integer.valueOf(str[2]);
		result = Integer.valueOf(str[3]);
		water_progress.setText(water + "");
		oil_progress.setText(oil + "");
		light_progress.setText(light + "");
		water_type.setText(GetTypeUtils.getWaterType(water));
		oil_type.setText(GetTypeUtils.getOilType(oil));
		light_type.setText(GetTypeUtils.getLightType(light));
		showProgressBar(water, oil, light, false);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tips1_back:
			startActivityForResult(new Intent(getActivity(),
					Bind_Device_Anim_Activity.class), 2);
			// startActivity(new Intent(getActivity(),
			// Bind_Device_Anim_Activity.class));
			break;
		default:
			break;
		}
	}

	class TipsThread extends Thread {
		private Handler handler;

		public TipsThread(Handler mHandler) {
			super();
			this.handler = mHandler;
		}

		@Override
		public void run() {
			while (state_flag) {
				if (STATE == STATE_CONNECTED) {
					Log.e(TAG, "sss````````````TipsThread");
					// handler.post(new Runnable() {
					//
					// @Override
					// public void run() {
					// tips1.setText("将检测仪放于任意位置");
					// }
					// });
					if (STATE == STATE_CONNECTED) {
						Message msg1 = new Message();
						msg1.what = 22;
						msg1.obj = "将检测仪放于任意位置";
						handler.sendMessage(msg1);
						try {
							sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// handler.post(new Runnable() {
					//
					// @Override
					// public void run() {
					// tips1.setText("等待1~2秒，听到“滴”声");
					// }
					// });
					if (STATE == STATE_CONNECTED) {
						Message msg2 = new Message();
						msg2.what = 22;
						msg2.obj = "等待1~2秒，感受到震动";
						handler.sendMessage(msg2);
						try {
							sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// handler.post(new Runnable() {
					//
					// @Override
					// public void run() {
					// tips1.setText("完成检测");
					// }
					// });
					if (STATE == STATE_CONNECTED) {
						Message msg3 = new Message();
						msg3.what = 22;
						msg3.obj = "完成检测";
						handler.sendMessage(msg3);
						try {
							sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (STATE == STATE_CONNECTED) {
						Message msg4 = new Message();
						msg4.what = 22;
						msg4.obj = "将检测仪放于任意位置";
						handler.sendMessage(msg4);
						try {
							sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				}
			}
		}

	}

	class ScanThread extends Thread {

		@Override
		public void run() {
			while (state_flag) {
				boolean state = sharedpreferencesUtil.getConnectState();
				Log.e(TAG, "ScanThread" + state);
				Message msg = new Message();
				if (state) {
					msg.what = 5;
				} else {
					msg.what = 6;
				}
				mHandler.sendMessage(msg);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				tips1.setText("将检测仪放于任意位置");
				tips1_point.setVisibility(View.VISIBLE);
				Log.e(TAG, "connected");
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				System.out.println("disconnect");
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				System.out
						.println("intent.getStringExtra(BluetoothLeService.DATA_UUID)"
								+ intent.getStringExtra(
										BluetoothLeService.DATA_UUID)
										.toString());
				if (intent.getStringExtra(BluetoothLeService.DATA_UUID).equals(
						UUID_BLE_CHARACTERISTIC_F1.toString())) {
					// byte[] b = intent
					// .getByteArrayExtra(BluetoothLeService.BYTE_DATA);
					// FaceData fd = byte2data(b);
					// //
					// long time = System.currentTimeMillis();
					// DBHelper dbHelper = new DBHelper(getActivity()
					// .getApplicationContext(), "face", null, 1);
					// long row = dbHelper
					// .insert(fd.getWater(), fd.getOil(), fd.getLight(),
					// fd.getAverage(), fd.getWater(), time);
					// System.out.println("row插入成功" + row);
					// //
					System.out
							.println("intent.getStringExtra(BluetoothLeService.DATA_UUID)"
									+ intent.getStringExtra(
											BluetoothLeService.DATA_UUID)
											.toString());
					if (intent.getStringExtra(BluetoothLeService.DATA_UUID)
							.equals(UUID_BLE_CHARACTERISTIC_F1.toString())) {
						byte[] b = intent
								.getByteArrayExtra(BluetoothLeService.BYTE_DATA);
						System.out.println("Single" + "对了F1```" + b.length
								+ "``" + b[0]);
						FaceData fd = byte2data(b);
						water = fd.getWater();
						oil = fd.getOil();
						light = fd.getLight();
						result = (int) (light * 0.5f + water * 0.3f + 0.2f * oil);
						showProgressBar(water, oil, light, true);

						Log.e(TAG, fd.getWater() + "``" + fd.getOil() + "``"
								+ fd.getLight() + "``" + fd.getAverage());

					}
				}

			}
		}
	};

	class ResultAnimThread extends Thread {

		@Override
		public void run() {
			int t = 600 / result;
			for (int c = 5; c <= result; c++) {
				try {
					sleep(t);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = 0;
				msg.arg1 = c;
				mHandler.sendMessage(msg);
			}

		}

	}

	private void showProgressBar(int a, int b, int c, boolean isAnim) {
		sharedpreferencesUtil.setLastSingleResult(new String[] { water + "",
				oil + "", light + "", result + "" });
		water = a;
		oil = b;
		light = c;
		water_type.setText(GetTypeUtils.getWaterType(water));
		oil_type.setText(GetTypeUtils.getOilType(oil));
		light_type.setText(GetTypeUtils.getLightType(light));
		GetTypeUtils.setResultImg(result, result_img);
		if (isAnim) {
			new WaterThread().start();
			new OilThread().start();
			new SkinThread().start();
			new ResultAnimThread().start();
		} else {
			face_water_progressBar.setProgress(water);
			face_oil_progressBar.setProgress(oil);
			skin_light_progressBar.setProgress(light);
			result_progress.setProgress(result);
		}
		Log.e(TAG, water + "1" + oil + "1" + light + "1" + result);
	}

	// class TestThread extends Thread {
	//
	// @Override
	// public void run() {
	// for (int i = 90; i < 101; i++) {
	// final int p = i;
	// mHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// GetTypeUtils.setResultImg(p, result_img);
	// result_progress.setProgress(p);
	// }
	// });
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// super.run();
	// }
	//
	// }

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
					// TODO Auto-generated catch block
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	class SkinThread extends Thread {

		@Override
		public void run() {
			for (int k = 1; k <= light; k++) {
				skin_light_progressBar.setProgress(k);
				Message msg = new Message();
				msg.what = 3;
				msg.arg1 = k;
				mHandler.sendMessage(msg);
				try {
					sleep(600 / light);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private FaceData byte2data(byte[] b) {
		// 原始数据
		int light = (int) ((int) b[1] & 0xff) * 256 + (int) ((int) b[0] & 0xff);
		int res = (int) ((int) b[5] & 0xff) * 256 + (int) ((int) b[4] & 0xff);
		int water = (int) ((int) b[3] & 0xff) * 256 + (int) ((int) b[2] & 0xff);
		// color
		int oil = (int) ((int) b[7] & 0xff) * 256 + (int) ((int) b[6] & 0xff);
		int blue = (int) ((int) b[9] & 0xff) * 256 + (int) ((int) b[8] & 0xff);
		int green = (int) ((int) b[11] & 0xff) * 256
				+ (int) ((int) b[10] & 0xff);
		int red = (int) ((int) b[13] & 0xff) * 256 + (int) ((int) b[12] & 0xff);
		System.out.println(res + "``" + water + "``" + oil + "``" + light
				+ "``");
		// 处理后的数据
		// 处理water
		int _water;
		if (water >= 250) {
			_water = 200 / 3 + water / 30;
		} else {
			_water = water * 3 / 10;
		}
		if (_water > 99) {
			_water = 99;
		} else if (_water < 1) {
			_water = 1;
		}

		// 处理light
		int _light = 0;
		if (light >= 500) {
			_light = light / 50 + 80;
		} else if (light >= 300 && light < 500) {
			_light = light / 10 + 40;
		} else if (light >= 200 && light < 300) {
			_light = light * 3 / 10 - 20;
		} else if (light < 200) {
			_light = light / 5;
		}
		if (_light > 99) {
			_light = 99;
		} else if (_light < 1) {
			_light = 1;
		}

		// 处理oil
		int _oil;
		_oil = _water - 10 + new Random().nextInt(21);

		if (_oil <= 10) {
			_oil = 10;
		} else if (_oil >= 90) {
			_oil = 90;
		}

		int _average = (int) (_water * 0.4f + _light * 0.5f + _oil * 0.1f);
		FaceData fd = new FaceData(_water, _oil, _light, _average);
		return fd;

	}

	@Override
	public void onPause() {
		state_flag = false;
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		Log.e(TAG, "on pause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// if (tips1 != null) {
		// System.out.println("s````````````````");
		// }
		getActivity().registerReceiver(mGattUpdateReceiver,
				makeGattUpdateIntentFilter());
		init_State();
		state_flag = true;
		new ScanThread().start();
		new TipsThread(mHandler).start();
		Log.e(TAG, "on resume");
		super.onResume();
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	//
	// switch (requestCode) {
	// case 1:
	// System.out.println(resultCode + "resultCode");
	// if (resultCode == 0) {
	// // 拒绝打开
	// System.out.println("single  jujue");
	// } else if (resultCode == -1) {
	// // 同意打开蓝牙
	// }
	// break;
	// case 2:
	// if (resultCode == 0) {
	// // 拒绝打开
	// System.out.println("single 0000000000");
	// } else if (resultCode == 1) {
	// // 同意打开蓝牙
	// System.out.println("single 1111111111 ");
	// }
	// break;
	// default:
	// break;
	// }
	// super.onActivityResult(requestCode, resultCode, data);
	// }
	private void connectService() {
		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName componentName,
					IBinder service) {
				mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
						.getService();
				if (!mBluetoothLeService.initialize()) {
					Log.e(TAG, "Unable to initialize Bluetooth");
					getActivity().finish();
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {
				mBluetoothLeService = null;
			}
		};
		Intent gattServiceIntent = new Intent(getActivity(),
				BluetoothLeService.class);
		getActivity().bindService(gattServiceIntent, mServiceConnection, 1);

	}
}
