package com.alphace.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

public class MyRadioButton extends RadioButton {
	private Context context;

	private Drawable mButtonDrawable;
	private int mButtonResource;

	public MyRadioButton(Context context) {
		super(context);
		this.context = context;
	}

	public MyRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	public void setButtonDrawable(int resid) {
		if (resid != 0 && resid == mButtonResource) {
			return;
		}

		mButtonResource = resid;

		Drawable d = null;
		if (mButtonResource != 0) {
			d = getResources().getDrawable(mButtonResource);
		}
		setButtonDrawable(d);
	}

	@Override
	public void setButtonDrawable(Drawable d) {
		if (d != null) {
			if (mButtonDrawable != null) {
				mButtonDrawable.setCallback(null);
				unscheduleDrawable(mButtonDrawable);
			}
			d.setCallback(this);
			d.setState(getDrawableState());
			d.setVisible(getVisibility() == VISIBLE, false);
			mButtonDrawable = d;
			mButtonDrawable.setState(null);
			setMinHeight(mButtonDrawable.getIntrinsicHeight());
		}

		refreshDrawableState();
	}

	// 核心代码部分
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final Drawable buttonDrawable = mButtonDrawable;
		if (buttonDrawable != null) {
			final int height = buttonDrawable.getIntrinsicHeight();
			final int width = buttonDrawable.getIntrinsicWidth();

			System.out.println(width+"w``h"+height);
			System.out.println(getWidth()+"w``h"+getHeight());
			int y = 0;
			int x = 0;
			x = (getWidth() - width) / 2;
			y = (getHeight() - height) / 2;

			buttonDrawable.setBounds(x, y, x + width, y + height);
			buttonDrawable.draw(canvas);
		}
	}
}