package com.alphace.yuyan;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RelativeLayout;

import com.alphace.tuli.MainActivity2;
import com.alphace.utils.MyUtils;
import com.alphace.utils.SharedpreferencesUtil;

public class WelcomeActivity extends Activity {
	private Handler mHandler;
	private SharedpreferencesUtil sharedpreferencesUtil;
	private RelativeLayout background;
	private String versionName;
	private String latestVersion = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		init();
		// ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.2f, 1, 1.2f,
		// Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
		// 0.5f);
		// scaleAnimation.setDuration(3000);
		// scaleAnimation.setFillAfter(true);
		// background.setAnimation(scaleAnimation);
		try {
			versionName = MyUtils.getVersionName(getApplicationContext());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void init() {
		String SDCard = Environment.getExternalStorageDirectory() + "";
		String pathName1 = SDCard + "/alphace/Alphace.apk";// 文件存储路径
		String pathName2 = SDCard + "/alphace";// 文件存储路径
		File file1 = new File(pathName1);
		File file2 = new File(pathName2);
		if (file1.exists()) {
			file1.delete();
		}
		if (file2.exists()) {
			file2.delete();
		}
		background = (RelativeLayout) findViewById(R.id.background);
		sharedpreferencesUtil = new SharedpreferencesUtil(
				getApplicationContext());
		sharedpreferencesUtil.setConnectState(false);
		mHandler = new Handler(){};
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				finish();
				// startActivity(new Intent(WelcomeActivity.this,
				// LoginActivity2.class));
				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity2.class);
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
				intent.putExtra("needUpdate", false);
				startActivity(intent);
			}
		}, 2000);
	}

}
