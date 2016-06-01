package com.alphace.utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class OPActivity extends Activity {
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("OP", "onCreate");
		mHandler = new Handler() {
		};
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				OPActivity.this.finish();
			}
		}, 3000);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e("OP", "onPause");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("OP", "onDestroy");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("OP", "onResume");
	}
}
