package com.alphace.tuli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alphace.constant.NomalConstant;
import com.alphace.yuyan.R;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class LoginActivity2 extends Activity implements OnClickListener {
	private Button regist_phone;
	private Button wechat_login;
	private Button login;
	private IWXAPI api;
	private RelativeLayout know_more;
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login2);
		init();
	}

	private void init() {
		api = WXAPIFactory.createWXAPI(this, NomalConstant.AppID, true);
		api.registerApp(NomalConstant.AppID);
		regist_phone = (Button) findViewById(R.id.regist_phone);
		wechat_login = (Button) findViewById(R.id.wechat_login);
		login = (Button) findViewById(R.id.login);
		know_more = (RelativeLayout) findViewById(R.id.know_more);
		regist_phone.setOnClickListener(this);
		wechat_login.setOnClickListener(this);
		login.setOnClickListener(this);
		know_more.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.regist_phone:
			startActivity(new Intent(LoginActivity2.this, RegistActivity2.class));
			break;
		case R.id.wechat_login:
			if (!api.isWXAppInstalled()) {
				Toast.makeText(getApplicationContext(), "请先安装微信应用",
						Toast.LENGTH_SHORT).show();
			} else if (!api.isWXAppSupportAPI()) {
				Toast.makeText(getApplicationContext(), "请先更新微信应用",
						Toast.LENGTH_SHORT).show();
			} else {
				final SendAuth.Req req = new SendAuth.Req();
				req.scope = "snsapi_userinfo";
				req.state = "wechat_sdk_demo_test";
				api.sendReq(req);
				this.finish();
			}
			break;
		case R.id.login:
			startActivity(new Intent(LoginActivity2.this,
					LoginPageActivity.class));
			break;
		case R.id.know_more:
			startActivity(new Intent(LoginActivity2.this,
					BindHelpActivity2.class).putExtra("flag", 1));
			break;
		default:
			break;
		}

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
}