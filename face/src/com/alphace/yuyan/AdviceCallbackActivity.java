package com.alphace.yuyan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AdviceCallbackActivity extends Activity implements OnClickListener {
	private ImageView advice_callback_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advice_callback);
		init();
	}

	private void init() {
		advice_callback_back = (ImageView) findViewById(R.id.advice_callback_back);

		advice_callback_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.advice_callback_back:
			finish();
			break;

		default:
			break;
		}
	}

}
