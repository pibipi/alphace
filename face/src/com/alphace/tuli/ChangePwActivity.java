package com.alphace.tuli;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alphace.yuyan.R;

public class ChangePwActivity extends Activity implements OnClickListener {
	private EditText old_pw;
	private EditText pw;
	private EditText confirm_pw;
	private ImageView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pw);
		init();
	}

	private void init() {
		back = (ImageView) findViewById(R.id.back);
		old_pw = (EditText) findViewById(R.id.old_pw);
		pw = (EditText) findViewById(R.id.pw);
		confirm_pw = (EditText) findViewById(R.id.confirm_pw);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			this.finish();
			break;

		default:
			break;
		}
	}
}
