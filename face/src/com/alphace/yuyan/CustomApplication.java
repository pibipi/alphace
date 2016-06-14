package com.alphace.yuyan;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.analytics.MobclickAgent.UMAnalyticsConfig;

public class CustomApplication extends Application {

	public CustomApplication() {
		super();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		MobclickAgent.startWithConfigure(new UMAnalyticsConfig(
				getApplicationContext(), "575fca8867e58edb8e00347d",
				"Yingyongbao", EScenarioType.E_UM_NORMAL));
	}
}
