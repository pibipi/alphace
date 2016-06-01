package com.alphace.tuli;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alphace.yuyan.R;
import com.pickerview.OptionsPopupWindow;
import com.pickerview.OptionsPopupWindow.OnOptionsSelectListener;

public class CompleteDataActivity2 extends Activity implements OnClickListener {
	private TextView sex;
	private TextView age;
	private TextView type;
	private Button done;
	private Button cancle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_completedata2);
		init();
	}

	private void init() {
		sex = (TextView) findViewById(R.id.sex);
		age = (TextView) findViewById(R.id.age);
		type = (TextView) findViewById(R.id.type);
		initSex();
		initAge();
		initType();
		done = (Button) findViewById(R.id.done);
		cancle = (Button) findViewById(R.id.cancle);
		done.setOnClickListener(this);
		cancle.setOnClickListener(this);

	}

	private void initType() {
		// 选项选择器
		final OptionsPopupWindow pwOptions = new OptionsPopupWindow(this);
		// 选项1
		final ArrayList<String> options1Items = new ArrayList<String>();
		options1Items.add("干性肌肤");
		options1Items.add("油性肌肤");
		options1Items.add("混合性肌肤");
		options1Items.add("中性肌肤");
		options1Items.add("敏感性肌肤");

		pwOptions.setPicker(options1Items);

		pwOptions.setSelectOptions(0);

		pwOptions.setOnoptionsSelectListener(new OnOptionsSelectListener() {

			@Override
			public void onOptionsSelect(int options1, int option2, int options3) {
				// 返回的分别是三个级别的选中位置
				String tx = options1Items.get(options1);
				type.setText(tx);
			}
		});

		// 点击弹出选项选择器
		type.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pwOptions.showAtLocation(type, Gravity.BOTTOM, 0, 0);
			}
		});
	}

	private void initAge() {
		// 选项选择器
		final OptionsPopupWindow pwOptions = new OptionsPopupWindow(this);
		// 选项1
		final ArrayList<String> options1Items = new ArrayList<String>();
		options1Items.add("15-20岁");
		options1Items.add("21-25岁");
		options1Items.add("26-30岁");

		pwOptions.setPicker(options1Items);

		pwOptions.setSelectOptions(0);

		pwOptions.setOnoptionsSelectListener(new OnOptionsSelectListener() {

			@Override
			public void onOptionsSelect(int options1, int option2, int options3) {
				// 返回的分别是三个级别的选中位置
				String tx = options1Items.get(options1);
				age.setText(tx);
			}
		});

		// 点击弹出选项选择器
		age.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pwOptions.showAtLocation(age, Gravity.BOTTOM, 0, 0);
			}
		});
	}

	private void initSex() {
		// 选项选择器
		final OptionsPopupWindow pwOptions = new OptionsPopupWindow(this);
		// 选项1
		final ArrayList<String> options1Items = new ArrayList<String>();
		options1Items.add("男");
		options1Items.add("女");

		pwOptions.setPicker(options1Items);

		pwOptions.setSelectOptions(0);

		pwOptions.setOnoptionsSelectListener(new OnOptionsSelectListener() {

			@Override
			public void onOptionsSelect(int options1, int option2, int options3) {
				// 返回的分别是三个级别的选中位置
				String tx = options1Items.get(options1);
				sex.setText(tx);
			}
		});

		// 点击弹出选项选择器
		sex.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pwOptions.showAtLocation(sex, Gravity.BOTTOM, 0, 0);
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.done:
			Intent intent = new Intent(CompleteDataActivity2.this,
					BindHelpActivity2.class);
			intent.putExtra("flag", 2);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.cancle:
			this.finish();
			break;

		default:
			break;
		}
	}
}
