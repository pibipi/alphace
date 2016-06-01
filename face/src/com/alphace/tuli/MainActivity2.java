package com.alphace.tuli;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.alphace.constant.NomalConstant;
import com.alphace.service.BluetoothLeService;
import com.alphace.utils.MyUtils;
import com.alphace.utils.OPActivity;
import com.alphace.utils.SharedpreferencesUtil;
import com.alphace.yuyan.HistoryDataActivity;
import com.alphace.yuyan.R;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity2 extends FragmentActivity implements
		OnCheckedChangeListener, OnClickListener {
	private FragmentManager fragmentManager;
	private RadioGroup radioGroup;
	private ImageView single_small;
	private ImageView multiply_small;
	private ImageView history_small;
	private ImageView setting;
	private byte[] bmp;
	private Handler mHandler;
	private ImageView calendar;
	private ImageView single_share;
	private RadioButton single_btn;
	private RadioButton multiply_btn;
	private RadioButton history_btn;
	//
	private final static String TAG = MainActivity2.class.getSimpleName();
	private boolean mConnected = false;
	private SharedpreferencesUtil sharedpreferencesUtil;
	private String mDeviceAddress;
	private IWXAPI api;
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
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothLeService mBluetoothLeService;
	private ServiceConnection mServiceConnection;
	//
	private long mExitTime;
	private final static int BLESTART = 1;
	private boolean state_flag = true;
	//
	private OnBleChangeListener listener;

	//
	private AlertDialog ble_Dialog;
	private AlertDialog reset_Dialog;
	private NotificationManager mNotificationManager;
	private String versionName = "";
	private String latestVersion = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main2);
		// startActivity(new Intent(MainActivity2.this,OPActivity.class));
		init();
		// 默认第一页
		int page = getIntent().getIntExtra("page", 2);
		System.out.println("page" + page);
		switch (page) {
		case 1:
			changeFragment(new Fragment_Single(), true);
			single_btn.setChecked(true);
			calendar.setVisibility(View.INVISIBLE);
			// single_share.setVisibility(View.VISIBLE);
			break;
		case 2:
			calendar.setVisibility(View.INVISIBLE);
			single_share.setVisibility(View.INVISIBLE);
			changeFragment(new Fragment_Multiply(), true);
			multiply_btn.setChecked(true);
			break;
		case 3:
			history_btn.setChecked(true);
			changeFragment(new Fragment_History(), true);
			calendar.setVisibility(View.VISIBLE);
			single_share.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
		//
		sharedpreferencesUtil = new SharedpreferencesUtil(
				getApplicationContext());
		mDeviceAddress = sharedpreferencesUtil.getMyDeviceMac();
		initBle();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		System.out.println(mDeviceAddress);

	}

	private void initUpdate() {
		boolean needUpdate = getIntent().getBooleanExtra("needUpdate", false);
		if (needUpdate) {
			showDialog();
		}
	}

	private void init() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		api = WXAPIFactory.createWXAPI(this, NomalConstant.AppID, false);
		single_btn = (RadioButton) findViewById(R.id.single_btn);
		multiply_btn = (RadioButton) findViewById(R.id.multiply_btn);
		history_btn = (RadioButton) findViewById(R.id.history_btn);
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 666:
					int progress = msg.arg1;
					if (progress > 0 && progress < 100) {
						initNotify(progress, 100, "Alphace正在下载更新...");
					} else {
						// initNotify(0, 0, "下载完成，点击安装");
						mNotificationManager.cancelAll();
					}
					break;
				case 999:
					showDialog();
				default:
					break;
				}
			}
		};
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!MainActivity2.this.isFinishing()) {
					bmp = MyUtils.bitmap2bytearray(MyUtils
							.captureScreen(MainActivity2.this));
				}

			}
		}, 1000);
		reset_Dialog = new AlertDialog.Builder(MainActivity2.this).create();
		ble_Dialog = new AlertDialog.Builder(MainActivity2.this).create();
		fragmentManager = getSupportFragmentManager();
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(this);
		single_small = (ImageView) findViewById(R.id.single_small);
		multiply_small = (ImageView) findViewById(R.id.multiply_small);
		history_small = (ImageView) findViewById(R.id.history_small);
		setting = (ImageView) findViewById(R.id.setting);
		calendar = (ImageView) findViewById(R.id.calendar);
		single_share = (ImageView) findViewById(R.id.single_share);
		calendar.setOnClickListener(this);
		single_share.setOnClickListener(this);
		setting.setOnClickListener(this);
		try {
			versionName = MyUtils.getVersionName(getApplicationContext());
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = "http://api.qijitek.com/getVersion/";
				HttpGet httpRequest = new HttpGet(url);
				try {
					HttpResponse httpResponse = new DefaultHttpClient()
							.execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						String strResult = EntityUtils.toString(httpResponse
								.getEntity());
						System.out.println("latestVersion" + strResult);
						System.out.println("versionName" + versionName);
						latestVersion = strResult;
						boolean needUpdate = false;
						if (latestVersion.equals("")) {
							needUpdate = false;
						} else {
							if (latestVersion.equals(versionName)) {
								needUpdate = false;
							} else {
								needUpdate = true;
							}
						}
						if (needUpdate) {
							Message msg = new Message();
							msg.what = 999;
							mHandler.sendMessage(msg);
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("id````" + e.toString());
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void changeFragment(Fragment fragment, boolean isInit) {
		FrameLayout fff = (FrameLayout) findViewById(R.id.fragment_container);
		int a = MyUtils.px2dip(getApplicationContext(), fff.getHeight());
		int b = MyUtils.px2dip(getApplicationContext(), fff.getWidth());
		System.out.println(b + "dp" + a);
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);
		if (!isInit) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkId) {
		switch (checkId) {
		case R.id.single_btn:
			changeFragment(new Fragment_Single(), true);
			single_small.setVisibility(View.VISIBLE);
			multiply_small.setVisibility(View.INVISIBLE);
			history_small.setVisibility(View.INVISIBLE);
			calendar.setVisibility(View.INVISIBLE);
			// single_share.setVisibility(View.VISIBLE);
			break;
		case R.id.multiply_btn:
			changeFragment(new Fragment_Multiply(), true);
			single_small.setVisibility(View.INVISIBLE);
			multiply_small.setVisibility(View.VISIBLE);
			history_small.setVisibility(View.INVISIBLE);
			calendar.setVisibility(View.INVISIBLE);
			single_share.setVisibility(View.INVISIBLE);
			break;
		case R.id.history_btn:
			changeFragment(new Fragment_History(), true);
			single_small.setVisibility(View.INVISIBLE);
			multiply_small.setVisibility(View.INVISIBLE);
			history_small.setVisibility(View.VISIBLE);
			calendar.setVisibility(View.VISIBLE);
			single_share.setVisibility(View.INVISIBLE);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.setting:
			Intent intent = new Intent(MainActivity2.this,
					PersonalActivity.class);
			if (bmp == null) {
				bmp = MyUtils.bitmap2bytearray(MyUtils
						.captureScreen(MainActivity2.this));
			}
			intent.putExtra("bmp", bmp);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			// changeFragment(new Fragment_Set(), true);
			break;
		case R.id.calendar:
			startActivity(new Intent(MainActivity2.this,
					HistoryDataActivity.class));
			break;
		case R.id.single_share:
			share2wechatUrl();
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化BLE：检测是否支持蓝牙，检测蓝牙是否已经打开。
	 */
	private void initBle() {
		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(getApplicationContext(), "不支持BLE蓝牙", 1).show();
			System.out.println("不支持ble蓝牙");
		} else
			System.out.println("支持ble蓝牙");
		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		// 检查设备上是否支持蓝牙
		if (bluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(),
					"error_bluetooth_not_supported", Toast.LENGTH_SHORT).show();
		} // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, BLESTART);
		} else {
			initUpdate();
			if (!mDeviceAddress.equals("")) {
				startService(new Intent(MainActivity2.this,
						BluetoothLeService.class));
				connectService();
			}
		}
		// if (!bluetoothAdapter.isEnabled()) {
		// if (sharedpreferencesUtil.getAutoBle()) {
		// bluetoothAdapter.enable();
		// if (!mDeviceAddress.equals("")) {
		// startService(new Intent(MainActivity2.this,
		// BluetoothLeService.class));
		// }
		// } else {
		// checkBLE();
		// }
		// } else {
		// startService(new Intent(MainActivity2.this,
		// BluetoothLeService.class));
		// }

	}

	private void checkBLE() {
		ble_Dialog.show();
		Window window = ble_Dialog.getWindow();
		window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置

		ble_Dialog.getWindow().setContentView(R.layout.dialog_ble);
		ble_Dialog.getWindow().findViewById(R.id.yes)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						bluetoothAdapter.enable();
						ble_Dialog.dismiss();
					}
				});
		ble_Dialog.getWindow().findViewById(R.id.no)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						ble_Dialog.dismiss();
					}
				});
		CheckBox checkBox = (CheckBox) ble_Dialog.getWindow().findViewById(
				R.id.checkBox);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					sharedpreferencesUtil.setAutoBle(isChecked);
				} else {
					sharedpreferencesUtil.setAutoBle(isChecked);
				}
			}
		});
		ble_Dialog.setCancelable(false);
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
				mConnected = true;
				sharedpreferencesUtil.setConnectState(mConnected);
				Log.e(TAG, "connected");
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				// connect(mDeviceAddress);
				mConnected = false;
				sharedpreferencesUtil.setConnectState(mConnected);
				System.out.println("disconnect");
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				// displayGattServices(mBluetoothLeService
				// .getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				// System.out
				// .println("intent.getStringExtra(BluetoothLeService.DATA_UUID)"
				// + intent.getStringExtra(
				// BluetoothLeService.DATA_UUID)
				// .toString());

			}
		}
	};

	private void share2wechatUrl() {
		// 初始化一个WXWebpageObject，填写URL
		WXWebpageObject webpageObject = new WXWebpageObject();
		webpageObject.webpageUrl = "https://www.baidu.com/";
		// 用webpageObject初始化一个WXMediaMessage对象，填写标题、描述
		WXMediaMessage msg = new WXMediaMessage(webpageObject);
		msg.title = "title";
		msg.description = "description";
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		msg.thumbData = MyUtils.bitmap2bytearray(bitmap);
		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");// transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;

		api.sendReq(req);
	}

	/**
	 * 构造一个用于请求的唯一标识
	 * 
	 * @param type
	 *            分享的内容类型
	 * @return
	 */
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	class BLEScanThread extends Thread {

		@Override
		public void run() {
			while (state_flag) {
				if (mBluetoothLeService != null) {
					final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
					BluetoothAdapter bluetoothAdapter = bluetoothManager
							.getAdapter();
					if (bluetoothAdapter.isEnabled()) {
						mBluetoothLeService.initialize();
						if (mBluetoothLeService.mBluetoothGatt == null) {
							System.out.println("mBluetoothGatt is null");
							mBluetoothLeService.connect_tuli();
						}
						System.out.println("mBluetoothGatt is not null");
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("BLEScanThread");
			}
		}

	}

	private void connectService() {
		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName componentName,
					IBinder service) {
				mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
						.getService();
				if (!mBluetoothLeService.initialize()) {
					Log.e(TAG, "Unable to initialize Bluetooth");
					finish();
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {
				mBluetoothLeService = null;
			}
		};
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

	}

	@Override
	public void onPause() {
		state_flag = false;
		unregisterReceiver(mGattUpdateReceiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		state_flag = true;
		new BLEScanThread().start();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mBluetoothLeService != null && mServiceConnection != null) {
			unbindService(mServiceConnection);
		}
		stopService(new Intent(MainActivity2.this, BluetoothLeService.class));
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();

			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case BLESTART:
			System.out.println(resultCode + "resultCode");
			if (resultCode == 0) {
				this.finish();
				// 拒绝打开
			} else if (resultCode == -1) {
				// 同意打开蓝牙
				initUpdate();
				if (!mDeviceAddress.equals("")) {
					startService(new Intent(MainActivity2.this,
							BluetoothLeService.class));
				}
			}
			break;
		default:
			break;
		}
	}

	public void setOnBleChangeListener(OnBleChangeListener l) {
		this.listener = l;
	}

	public interface OnBleChangeListener {
		public void onBle(int i);
	}

	public void doBle(int i) {
		listener.onBle(i);
	}

	private void showDialog() {
		reset_Dialog.show();

		Window window = reset_Dialog.getWindow();
		window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
		// window.setWindowAnimations(R.style.mystyle); // 添加动画

		reset_Dialog.getWindow().setContentView(R.layout.dialog_update);
		reset_Dialog.getWindow().findViewById(R.id.yes)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(getApplicationContext(), "后台下载中...", 0)
								.show();
						new Thread(new Runnable() {

							@Override
							public void run() {
								MyUtils.downloadApk(getApplicationContext(),
										mHandler);
							}
						}).start();
						reset_Dialog.dismiss();
					}
				});
		reset_Dialog.getWindow().findViewById(R.id.no)
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						reset_Dialog.dismiss();
					}
				});
		reset_Dialog.setCancelable(false);
	}

	private void initNotify(int progress, int max, String title) {
		final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this);
		mBuilder.setContentTitle(title)
				// 设置通知栏标题
				// .setContentIntent(getDefalutIntent(Notification.FLAG_NO_CLEAR))
				// 设置通知栏点击意图
				.setTicker("Alphace正在下载更新...")
				// 通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())
				// 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
				.setPriority(Notification.PRIORITY_MAX)
				// 设置该通知优先级
				// .setAutoCancel(false)
				// 设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(false)
				// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setProgress(max, progress, false)
				.setSmallIcon(R.drawable.ic_launcher);// 设置通知小ICON
		if (progress != 0) {
			mBuilder.setContentText(progress + "%");
		} else {
			mBuilder.setContentText("");
			mBuilder.setAutoCancel(true);

		}
		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_NO_CLEAR;
		mNotificationManager.notify(1, notification);
	}
}
