package com.alphace.tuli;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.alphace.yuyan.R;

public class BindHelpActivity2 extends Activity implements OnClickListener {
	private ViewPager pager;
	private List<View> list;
	private Button start;
	private int flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_help2);
		init();
		flag = getIntent().getIntExtra("flag", 1);
	}

	private void init() {
		start = (Button) findViewById(R.id.start);
		start.setOnClickListener(this);
		list = new ArrayList<View>();
		pager = (ViewPager) findViewById(R.id.pager);
		ImageView iv1 = new ImageView(getApplicationContext());
		iv1.setBackgroundResource(R.drawable.bind1);
		ImageView iv2 = new ImageView(getApplicationContext());
		iv2.setBackgroundResource(R.drawable.bind2);
		ImageView iv3 = new ImageView(getApplicationContext());
		iv3.setBackgroundResource(R.drawable.bind3);
		ImageView iv4 = new ImageView(getApplicationContext());
		iv4.setBackgroundResource(R.drawable.bind4);
		list.add(iv1);
		list.add(iv2);
		list.add(iv3);
		list.add(iv4);
		pager.setAdapter(new MyPagerAdapter());
		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 3) {
					start.setVisibility(View.VISIBLE);
				} else {
					start.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(list.get(position));
			return list.get(position);
		}

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.start:
			switch (flag) {
			case 1:
				this.finish();
				break;
			case 2:
				Intent intent2 = new Intent(BindHelpActivity2.this,
						MainActivity2.class);
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent2);
				break;

			default:
				break;
			}

			break;

		default:
			break;
		}
	}
}
