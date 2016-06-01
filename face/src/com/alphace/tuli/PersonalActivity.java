package com.alphace.tuli;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alphace.utils.MyUtils;
import com.alphace.yuyan.Personal_Setting_Activity;
import com.alphace.yuyan.R;

public class PersonalActivity extends Activity implements OnClickListener {
	private LinearLayout background;
	private ImageView setting;
	private Handler mHandler;
	private TextView about_us;
	private TextView help;
	private TextView personal_setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal2);
		init();
		initAnim();
		initBlur();
		initVs();

	}

	private void initVs() {
		Animation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(500);
		animation.setStartOffset(200);
		about_us.startAnimation(animation);
		help.startAnimation(animation);
		personal_setting.startAnimation(animation);
	}

	private void init() {
		mHandler = new Handler() {
		};
		about_us = (TextView) findViewById(R.id.about_us);
		help = (TextView) findViewById(R.id.help);
		personal_setting = (TextView) findViewById(R.id.personal_setting);
		about_us.setOnClickListener(this);
		help.setOnClickListener(this);
		personal_setting.setOnClickListener(this);
	}

	private void initBlur() {
		background = (LinearLayout) findViewById(R.id.background);
		// Bitmap bmpBlurred = BitmapFactory.decodeResource(getResources(),
		// R.drawable.multiply_help3_background);
		// Bitmap newImg = Blur.fastblur(TestAc.this, bmpBlurred, 25);
		// testt.setImageBitmap(newImg);
		Bitmap bmp1 = MyUtils.bytearray2bitmap(getIntent().getByteArrayExtra(
				"bmp"));
		// Bitmap bmp2 = Blur.fastblur(getApplicationContext(), bmp1, 25);
		// Bitmap bmp2=fastblur(getApplicationContext(), bmp1, 1000);
		Bitmap bmp2 = MyUtils.fastblurscale(bmp1, 0.05f, 15);
		background.setBackground(new BitmapDrawable(getResources(), bmp2));
	}

	private void initAnim() {
		setting = (ImageView) findViewById(R.id.setting);
		setting.setOnClickListener(this);
		final RotateAnimation rotateAnimation = new RotateAnimation(0f, 90f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setDuration(500);
		// final Animation rotateAnimation = AnimationUtils.loadAnimation(this,
		// R.anim.round_anim);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				setting.startAnimation(rotateAnimation);
			}
		}, 20);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				setting.clearAnimation();
				setting.setImageResource(R.drawable.tuli_main_set2);
			}
		}, 800);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.setting:
			finish2anim();
			break;
		case R.id.about_us:
			startActivity(new Intent(PersonalActivity.this,
					AboutusActivity.class));
			break;
		case R.id.help:
			Intent intent = new Intent(PersonalActivity.this,
					HelpActivity.class);
			startActivity(intent);
			break;
		case R.id.personal_setting:
			Intent intent2 = new Intent(PersonalActivity.this,
					Personal_Setting_Activity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish2anim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void finish2anim() {
		setting.clearAnimation();
		setting.setImageResource(R.drawable.tuli_main_set2);
		final RotateAnimation rotateAnimation = new RotateAnimation(0f, -90f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setDuration(500);
		// final Animation rotateAnimation =
		// AnimationUtils.loadAnimation(this,
		// R.anim.round_anim);
		setting.startAnimation(rotateAnimation);
		//
		initVs2();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				PersonalActivity.this.finish();
			}
		}, 500);
	}

	private void initVs2() {
		Animation animation = new AlphaAnimation(1.0f, 0.1f);
		animation.setDuration(500);
		animation.setFillAfter(true);
		about_us.startAnimation(animation);
		help.startAnimation(animation);
		personal_setting.startAnimation(animation);
	}
}
