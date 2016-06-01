package com.alphace.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
import android.widget.Scroller;

public class SlowScrollView extends ScrollView {
	private Scroller mScroller;

	public SlowScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);
	}

	public SlowScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
	}

	public SlowScrollView(Context context) {
		super(context);
		mScroller = new Scroller(context);
	}

	/**
	 * 滑动事件
	 */
	@Override
	public void fling(int velocityY) {
		super.fling(velocityY );// 这里设置滑动的速度
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}

	public void smoothScrollToSlow(int fx, int fy, int duration) {
		int dx = fx - getScrollX();// mScroller.getFinalX(); 普通view使用这种方法
		int dy = fy - getScrollY(); // mScroller.getFinalY();
		smoothScrollBySlow(dx, dy, duration);
	}

	private void smoothScrollBySlow(int dx, int dy, int duration) {
		// TODO Auto-generated method stub
		// 设置mScroller的滚动偏移量
		mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, duration);// scrollView使用的方法（因为可以触摸拖动）
		// mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(),
		// dx, dy, duration); //普通view使用的方法
		invalidate();// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
	}

	@Override
	public void computeScroll() {

		// 先判断mScroller滚动是否完成
		if (mScroller.computeScrollOffset()) {

			// 这里调用View的scrollTo()完成实际的滚动
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

			// 必须调用该方法，否则不一定能看到滚动效果
			postInvalidate();
		}
		super.computeScroll();
	}
}