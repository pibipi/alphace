package com.alphace.tuli;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alphace.constant.FaceData;
import com.alphace.constant.NomalConstant;
import com.alphace.service.BluetoothLeService;
import com.alphace.tuli.Fragment_Single.TipsThread;
import com.alphace.utils.MyUtils;
import com.alphace.utils.SharedpreferencesUtil;
import com.alphace.yuyan.Bind_Device_Anim_Activity;
import com.alphace.yuyan.R;

public class Fragment_Multiply extends Fragment implements OnClickListener,
		OnCheckedChangeListener {
	private final static String TAG = Fragment_Multiply.class.getSimpleName();
	//
	public final static UUID UUID_BLE_CHARACTERISTIC_F1 = UUID
			.fromString(NomalConstant.BLE_CHARACTERISTIC_F1);
	public final static UUID UUID_BLE_CHARACTERISTIC_F2 = UUID
			.fromString(NomalConstant.BLE_CHARACTERISTIC_F2);
	public final static UUID UUID_BLE_CHARACTERISTIC_F3 = UUID
			.fromString(NomalConstant.BLE_CHARACTERISTIC_F3);
	public final static UUID UUID_BLE_SERVICE = UUID
			.fromString(NomalConstant.BLE_SERVICE);

	//
	private ImageView multiply_img;
	private CheckBox skin_protect;
	private TextView tips1;
	private int times = 0;
	private ArrayList<FaceData> list;
	//
	private SharedpreferencesUtil sharedpreferencesUtil;
	private Handler mHandler;
	private boolean state_flag = true;
	private String address;
	private RelativeLayout tips_back2;
	private ImageView tips1_point;
	//
	private int STATE = 0;
	private int STATE_NOTBIND = 1;
	private int STATE_NOTCONNECTED = 2;
	private int STATE_CONNECTED = 3;

	//
	private byte[] bmp;
	private FaceData averageData;
	private int type = 0;

	//
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_multiply, null);
		init(view);
		// init_State();
		Log.e(TAG, "on create");

		return view;
	}

	private void init(View view) {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
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
						tips1_point.setVisibility(View.INVISIBLE);
						tips1.setText("绑定设备");
						tips_back2.setClickable(true);
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
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (Fragment_Multiply.this.isVisible()) {
					bmp = MyUtils.bitmap2bytearray(MyUtils
							.captureScreen(getActivity()));
				}

			}
		}, 1000);
		averageData = new FaceData();
		tips1_point = (ImageView) view.findViewById(R.id.tips1_point);
		tips_back2 = (RelativeLayout) view.findViewById(R.id.tips_back2);
		tips_back2.setOnClickListener(this);
		skin_protect = (CheckBox) view.findViewById(R.id.skin_protect);
		skin_protect.setOnCheckedChangeListener(this);
		tips1 = (TextView) view.findViewById(R.id.tips1);
		sharedpreferencesUtil = new SharedpreferencesUtil(getActivity()
				.getApplicationContext());
		multiply_img = (ImageView) view.findViewById(R.id.multiply_img);
		list = new ArrayList<FaceData>();
		address = new SharedpreferencesUtil(getActivity()).getMyDeviceMac();
	}

	private void init_State() {
		address = new SharedpreferencesUtil(getActivity()
				.getApplicationContext()).getMyDeviceMac();
		System.out.println("multiply onresume" + address + "```");
		if (address.equals("")) {
			STATE = STATE_NOTBIND;
			tips1_point.setVisibility(View.INVISIBLE);
			tips1.setText("绑定设备");
			tips_back2.setClickable(true);
			System.out.println("绑定设备");
		} else {
			System.out.println("address" + address);
			tips_back2.setClickable(false);
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
					Log.e(TAG, "mmm`````````````TipsThread");
					// handler.post(new Runnable() {
					// @Override
					// public void run() {
					// tips1.setText("将检测仪放于任意位置");
					// }
					// });
					if (STATE == STATE_CONNECTED) {
						Message msg1 = new Message();
						msg1.what = 22;
						msg1.obj = "将设备放于下图提示位置";
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
						msg4.obj = "将设备放于下图提示位置";
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
				tips1.setText("将设备放于下图提示位置");
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
					byte[] b = intent
							.getByteArrayExtra(BluetoothLeService.BYTE_DATA);
					System.out.println("Multiply" + "对了F1```" + b.length + "``"
							+ b[0]);
					final FaceData fd = byte2data(b);
					if (list.size() <= 5) {
						list.add(fd);
						System.out.println("list size" + list.size());
						switch (list.size()) {
						case 1:
							multiply_img.setImageResource(R.drawable.multiply2);
							break;
						case 2:
							multiply_img.setImageResource(R.drawable.multiply3);
							break;
						case 3:
							multiply_img.setImageResource(R.drawable.multiply4);
							break;
						case 4:
							multiply_img.setImageResource(R.drawable.multiply5);
							break;
						case 5:
							multiply_img.setImageResource(R.drawable.multiply1);
							handdata(list, fd);
							break;
						default:
							break;
						}
					}
					Log.e(TAG,
							fd.getWater() + "``" + fd.getOil() + "``"
									+ fd.getLight() + "``" + fd.getAverage());

				}

			}
		}
	};

	private void handdata(ArrayList<FaceData> list2, FaceData fd) {
		list2average(list2);
		Intent intent2 = new Intent(getActivity(), SingleResultActivity2.class);
		intent2.putExtra("type", type);
		intent2.putExtra("water", averageData.getWater());
		intent2.putExtra("oil", averageData.getOil());
		intent2.putExtra("light", averageData.getLight());
		intent2.putExtra("average", averageData.getAverage());
		if (bmp == null) {
			bmp = MyUtils
					.bitmap2bytearray(MyUtils.captureScreen(getActivity()));
		}
		intent2.putExtra("bmp", bmp);
		list.clear();
		startActivity(intent2);
	}

	private void list2average(ArrayList<FaceData> list) {
		int water = (list.get(0).getWater() + list.get(1).getWater()
				+ list.get(2).getWater() + list.get(3).getWater() + list.get(4)
				.getWater()) / 5;
		int oil = (list.get(0).getOil() + list.get(1).getOil()
				+ list.get(2).getOil() + list.get(3).getOil() + list.get(4)
				.getOil()) / 5;
		int light = (list.get(0).getLight() + list.get(1).getLight()
				+ list.get(2).getLight() + list.get(3).getLight() + list.get(4)
				.getLight()) / 5;
		// int average = (list.get(0).getAverage() + list.get(1).getAverage()
		// + list.get(2).getAverage() + list.get(3).getAverage() + list
		// .get(4).getAverage()) / 5;

		int ave_water = suan(new int[] { list.get(0).getWater(),
				list.get(1).getWater(), list.get(2).getWater(),
				list.get(3).getWater(), list.get(4).getWater() });
		int ave_oil = suan(new int[] { list.get(0).getOil(),
				list.get(1).getOil(), list.get(2).getOil(),
				list.get(3).getOil(), list.get(4).getOil() });
		int ave_light = suan(new int[] { list.get(0).getLight(),
				list.get(1).getLight(), list.get(2).getLight(),
				list.get(3).getLight(), list.get(4).getLight() });
		int average = (ave_water + ave_oil + ave_light) / 3;
		if (average >= 99) {
			average = 99;
		} else if (average <= 1) {
			average = 1;
		}
		averageData.setWater(water);
		averageData.setOil(oil);
		averageData.setLight(light);
		averageData.setUniform(average);
		averageData.setAverage(average);
	}

	private int suan(int[] array) {
		double ave = 0;
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i] + "");
			ave += array[i];
		}
		ave /= array.length;

		double sum = 0;
		for (int i = 0; i < array.length; i++)
			// sum += (ave - Math.abs((array[i] - ave) / ave));
			sum += ((ave - Math.abs(array[i] - ave)) / ave);
		sum /= array.length;
		sum *= 100;
		Log.e("齐集均方差", sum + "");
		return (int) sum;
		// DecimalFormat format = new DecimalFormat("##.####");
		// String _sum = format.format(sum);

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
		_oil = (int) ((_water - 10 + new Random().nextInt(21)) / 5f + 30 - 10 + new Random()
		.nextInt(21));

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
		getActivity().registerReceiver(mGattUpdateReceiver,
				makeGattUpdateIntentFilter());
		init_State();
		state_flag = true;
		new ScanThread().start();
		new TipsThread(mHandler).start();
		Log.e(TAG, "on resume");
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tips_back2:
			startActivity(new Intent(getActivity(),
					Bind_Device_Anim_Activity.class));
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked == false) {
			type = 0;
		} else {
			type = 1;
		}
	}

}
