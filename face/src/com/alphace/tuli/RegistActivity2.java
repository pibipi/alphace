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

import com.alphace.utils.CountDownButtonHelper;
import com.alphace.utils.CountDownButtonHelper.OnFinishListener;
import com.alphace.yuyan.R;

public class RegistActivity2 extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	private Button regist_cancle;
	private Button regist;
	private EditText phone;
	private EditText code;
	private EditText password;
	private CheckBox pw_btn;
	private Button code_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist2);
		init();
	}

	private void init() {
		phone = (EditText) findViewById(R.id.phone);
		code = (EditText) findViewById(R.id.code);
		password = (EditText) findViewById(R.id.password);
		pw_btn = (CheckBox) findViewById(R.id.pw_btn);
		regist_cancle = (Button) findViewById(R.id.regist_cancle);
		regist = (Button) findViewById(R.id.regist);
		code_btn = (Button) findViewById(R.id.code_btn);
		code_btn.setOnClickListener(this);
		regist.setOnClickListener(this);
		regist_cancle.setOnClickListener(this);
		pw_btn.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.regist:
			startActivity(new Intent(RegistActivity2.this,
					CompleteDataActivity2.class));
			break;
		case R.id.regist_cancle:
			this.finish();
			break;
		case R.id.code_btn:
			CountDownButtonHelper helper = new CountDownButtonHelper(code_btn,
					"", 60, 1);
			helper.setOnFinishListener(new OnFinishListener() {
				@Override
				public void finish() {
					code_btn.setText("重新发送");
				}
			});
			helper.start();
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
