package com.alphace.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {

	Paint paint, textpaint, paint2, textpaint2, circle_paint, circle_paint2;
	RectF area;
	int value = 100;
	LinearGradient shader;

	public CircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CircleView(Context context) {
		super(context);
		init();
	}

	public void setProgress(int value) {
		this.value = value;
		invalidate();
	}

	public void init() {
		paint = new Paint();
		paint.setStrokeWidth(50f);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);

		paint2 = new Paint();
		paint2.setStrokeWidth(50f);
		paint2.setColor(0xfff4f4f4);
		paint2.setStyle(Style.STROKE);
		paint2.setAntiAlias(true);

		textpaint = new Paint();
		textpaint.setTextSize(100f);
		textpaint.setColor(Color.YELLOW);

		textpaint2 = new Paint();
		textpaint2.setTextSize(100f);
		textpaint2.setColor(Color.YELLOW);
		textpaint2.setTextSize(50f);
		area = new RectF(100, 100, 500, 500);

		circle_paint = new Paint();
		circle_paint.setStrokeWidth(38f);
		circle_paint.setColor(0xff8f8ce7);
		circle_paint.setStyle(Style.STROKE);
		circle_paint.setAntiAlias(true);

		circle_paint2 = new Paint();
		circle_paint2.setStrokeWidth(38f);
		circle_paint2.setColor(0xfff4f4f4);
		circle_paint2.setStyle(Style.STROKE);
		circle_paint2.setAntiAlias(true);

		shader = new LinearGradient(0, 400, 400, 0, new int[] { 0xff901d20, 0xee901d20 }, null, Shader.TileMode.CLAMP);

		paint.setShader(shader);
		circle_paint.setShader(shader);
		// paint2.setShader(new LinearGradient(0, 0, 400, 0, 0xff8f8ce7,
		// 0xff75b8ef, Shader.TileMode.CLAMP));

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawColor(Color.WHITE);

		canvas.drawArc(area, 120, 300, false, paint2);
		canvas.drawArc(area, 120, 300 * value / 100, false, paint);
		// canvas.drawText(value + "", 240, 340, textpaint);
		// canvas.drawText("测试得分", 240, 280, textpaint2);
		// canvas.drawText("分", 240, 420, textpaint2);
		
		if (value == 100) {
			//末尾是有色圆形
			canvas.drawCircle((float)getX(0), (float)getY(0), 6, circle_paint);
			canvas.drawCircle((float)getX(100), (float)getY(100), 6, circle_paint);
		} else if(value==0){
			//首末是灰色圆形
			canvas.drawCircle((float)getX(0), (float)getY(0), 6, circle_paint2);
			canvas.drawCircle((float)getX(100), (float)getY(100), 6, circle_paint2);
		}
		else{
			//末尾是灰色圆形
			canvas.drawCircle((float)getX(0), (float)getY(0), 6, circle_paint);
			canvas.drawCircle((float)getX(100), (float)getY(100), 6, circle_paint2);
			canvas.drawCircle((float)getX(value), (float)getY(value), 6, circle_paint);
			
		}
	}
	
	private static double getX(int progress) {
		double a = ((progress / 100f) * 300f + 120f) * 2f * Math.PI / 360f;
		double x =  (300f + 200f * Math.cos(a));
		double y =  (300f + 200f * Math.sin(a));
		return x;
	}
	private static double getY(int progress) {
		double a = ((progress / 100f) * 300f + 120f) * 2f * Math.PI / 360f;
		double x =  (300f + 200f * Math.cos(a));
		double y =  (300f + 200f * Math.sin(a));
		return y;
	}

}
