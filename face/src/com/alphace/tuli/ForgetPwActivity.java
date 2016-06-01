package com.alphace.tuli;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alphace.utils.CountDownButtonHelper;
import com.alphace.utils.CountDownButtonHelper.OnFinishListener;
import com.alphace.yuyan.R;

public class ForgetPwActivity extends Activity implements OnClickListener {
	private ImageView back;
	private EditText phone;
	private EditText code;
	private EditText password;
	private EditText password2;
	private Button code_btn;
	private Button done;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pw);
		init();
	}

	private void init() {
		back = (ImageView) findViewById(R.id.back);
		phone = (EditText) findViewById(R.id.phone);
		code = (EditText) findViewById(R.id.code);
		password = (EditText) findViewById(R.id.password);
		password2 = (EditText) findViewById(R.id.password2);
		code_btn = (Button) findViewById(R.id.code_btn);
		done = (Button) findViewById(R.id.done);
		code_btn.setOnClickListener(this);
		done.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			this.finish();
			break;
		case R.id.done:
//			Toast.makeText(getApplicationContext(), "密码修改成功", 0).show();
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
}
