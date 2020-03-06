package com.example.myapplication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.example.myapplication.R;

public class CircleProgressBar extends View {

	private int progress;
	private int max = 100;
	private int width;
	private int height;
	private int mp; // 取宽和高的小值
	private Paint paint;
	private RectF oval;
	private int strokeWidth;// 画笔宽度
	
	private int circleProgressColor;

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		invalidate();
	}

	public CircleProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TypedArray是存放资源的array,1.通过上下文得到这个数组,attrs是构造函数传进来的,对应attrs.xml
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
		// 获得xml里定义的属性,格式为 名称_属性名 后面是默认值 
        circleProgressColor = typedArray.getColor(R.styleable.CircleProgressBar_circleProgressColor,Color.parseColor("ffff772d"));
        strokeWidth = typedArray.getInt(R.styleable.CircleProgressBar_circleProgressStrokeWidth, 14);
		paint = new Paint();
		oval = new RectF();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = View.MeasureSpec.getSize(widthMeasureSpec);
		height = View.MeasureSpec.getSize(heightMeasureSpec);
		if (width > height) {
			mp = height;
		} else {
			mp = width;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setAntiAlias(true);// 设置是否抗锯齿
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);// 帮助消除锯齿
		paint.setColor(Color.parseColor("#f2f2f2"));// 设置画笔灰色
		paint.setStrokeWidth(strokeWidth);// 设置画笔宽度为控件大小的十分之一
		paint.setStyle(Paint.Style.STROKE);// 设置中空的样式
		canvas.drawCircle(width / 2, height / 2, 2 * mp / 5, paint);// 在画布中心画个半径为一半的圆
//		if (progress == max) {
//			canvas.drawLine(width / 2 + 4 * mp / 15, height / 2 - 4 * mp / 15,
//					width / 2 - 4 * mp / 15, height / 2 + 4 * mp / 15, paint);// 画一条直线
			// paint.reset();// 将画笔重置
			// paint.setStrokeWidth(5);// 再次设置画笔的宽度
			// paint.setTextSize(40);// 设置文字的大小
			// paint.setColor(getResources().getColor(R.color.font_gray));//
			// 设置画笔颜色
			// canvas.drawText("已满标", 30, 110, paint);
//		} else {
			paint.setColor(circleProgressColor);// 设置画笔颜色
			oval.set(width / 2 - 2 * mp / 5, height / 2 - 2 * mp / 5, width / 2
					+ 2 * mp / 5, height / 2 + 2 * mp / 5);// 设置类似于左上角坐标（20,20），右下角坐标（180,180），这样也就保证了半径为80
			canvas.drawArc(oval, -90, ((float) progress / max) * 360, false,
					paint);// 画圆弧，第二个参数为：起始角度，第三个为跨的角度，第四个为true的时候是实心，false的时候为空心
			// paint.reset();// 将画笔重置
			// paint.setStrokeWidth(5);// 再次设置画笔的宽度
			// paint.setTextSize(50);// 设置文字的大小
			// paint.setColor(getResources().getColor(R.color.app_orange));//
			// 设置画笔颜色
			// if(progress<10) {
			// canvas.drawText(progress + "%", 55, 110, paint);
			// }else{
			// canvas.drawText(progress + "%", 50, 110, paint);
			// }
//		}
	}
}
