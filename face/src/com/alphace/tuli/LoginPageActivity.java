package com.alphace.tuli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alphace.yuyan.R;

public class LoginPageActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	private Button login;
	private Button login_cancle;
	private CheckBox pw_btn;
	private EditText phone;
	private EditText password;
	private TextView forget_pw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_page);
		init();
	}

	private void init() {
		forget_pw = (TextView) findViewById(R.id.forget_pw);
		login = (Button) findViewById(R.id.login);
		login_cancle = (Button) findViewById(R.id.login_cancle);
		pw_btn = (CheckBox) findViewById(R.id.pw_btn);
		phone = (EditText) findViewById(R.id.phone);
		password = (EditText) findViewById(R.id.password);

		forget_pw.setOnClickListener(this);
		pw_btn.setOnCheckedChangeListener(this);
		login.setOnClickListener(this);
		login_cancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			startActivity(new Intent(LoginPageActivity.this,
					MainActivity2.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK));
			break;
		case R.id.login_cancle:
			this.finish();
			break;
		case R.id.forget_pw:
			startActivity(new Intent(LoginPageActivity.this,
					ForgetPwActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (arg1) {
			password.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
		} else {
			password.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}
	}

}
