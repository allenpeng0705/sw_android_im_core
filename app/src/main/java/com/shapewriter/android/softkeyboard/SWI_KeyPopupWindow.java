package com.shapewriter.android.softkeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SWI_KeyPopupWindow extends PopupWindow{
		
	private static final int MAX_POPUP_TIME = 1800;
	private static final int INTERVAL_POPUP_TIME = 600; 
	private static final int INTERVAL_POPUP_TIME_DRAG = 200; 
	private static final int MIN_WIDTH = 50;
	private static final int FONT_SIZE = 35;
	
	private Timer1 mTimer1;
	private Timer2 mTimer2;
	private Timer3 mTimer3;
	private String mShowText;
	private Rect mOutRect;
	private RectF mOutRectF;
	private Paint mPt;
	private boolean mIsTimer1Start = false;
	private boolean mIsTimer2Start = false;
	private boolean mIsTimer3Start = false;
	
	private SWI_SoftKeyBase mKey;
	private PopupTextView mTextView;
	private SWI_KeyboardView mParent;
	private int mWidth;
	private int mHeight;
	private int mX;
	private int mY;
	private int mFontSize;
	
	public SWI_KeyPopupWindow(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mTextView = new PopupTextView(context);
		mTimer1 = new Timer1(INTERVAL_POPUP_TIME, INTERVAL_POPUP_TIME);
		mTimer2 = new Timer2(MAX_POPUP_TIME, MAX_POPUP_TIME);
		mTimer3 = new Timer3(INTERVAL_POPUP_TIME_DRAG,INTERVAL_POPUP_TIME_DRAG);
		mOutRect =  new Rect();
		mOutRectF = new RectF(mOutRect);
		mPt = new Paint();
	}
	
	public void destroy(){
		mTextView = null;
		mTimer1 = null;
		mTimer2 = null;
		mTimer3 = null;
		mOutRect = null;
		mOutRectF = null;
		mPt = null;		
	}
	
	public Timer1 getTimer1() {
		return mTimer1;
	}

	public Timer2 getTimer2() {
		return mTimer2;
	}

	public String getShowText() {
		return mShowText;
	}
	
	public void setShowText(String text){
		mShowText = text;
	}
	
	public void setPopupWindowWidth(int width){
		mWidth = width;
	}

	public void setProperty( SWI_KeyboardView parent, SWI_SoftKeyBase key){
		mParent = parent;
		
		mShowText = key.valueList.get(0);
		mKey = key;
		
		mWidth = Math.max(key.width, MIN_WIDTH);		
		mHeight = key.height;
		mFontSize = FONT_SIZE;
		
		if (key.label.equals(SWI_SoftKeyBase.LABEL_LANGUAGE)) {
			mWidth *= 3;
			mFontSize = 26;
			mHeight = key.height - 10;
		} else if (key.label.equals(SWI_SoftKeyBase.LABEL_CASE)) {
			mWidth *= 2;
			mFontSize = 26;
			mHeight = key.height - 10;
		} else if (key.label.equals(SWI_SoftKeyBase.LABEL_COMMAND)) {
			mWidth *= 2;
			mFontSize = 26;
			mHeight = key.height - 5;
		}
				
		mX = parent.getLeft() + key.left - (mWidth - key.width) / 2;
		mY = parent.getHeight() - key.top + 15;
		
		
		setWidth(mWidth);
		setHeight(mHeight);
		
		setTouchable(false);
		setBackgroundDrawable(null);
		mTextView.initialize();
		setContentView(mTextView);
	}
	
	public boolean isSameProperty(SWI_SoftKeyBase key){
		if(mKey == key)
			return true;
		else
			return false;
	}
	
	public void show(){
		showAtLocation(mParent, Gravity.LEFT | Gravity.BOTTOM, mX, mY);
	}
	
	public void update(){
		update(mX, mY, mWidth, mHeight);
	}
	
	
	class PopupTextView extends TextView{
		
		public PopupTextView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		public void initialize(){
			setBackgroundColor(0x00ffffff);
			
			setTextSize(30);
			setTextColor(Color.BLACK);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			
			getDrawingRect(mOutRect);
			mOutRectF.set(mOutRect);
			mPt.setAntiAlias(true);
			mPt.setStrokeWidth(1.0f);
			
			mOutRectF.left+= 1;
			mOutRectF.right-=1;
			mOutRectF.top+=1;
			mOutRectF.bottom-=1;
			mPt.setARGB(239, 247, 117, 0);
			mPt.setStyle(Paint.Style.FILL);
			canvas.drawRoundRect(mOutRectF, 5, 5, mPt); 
			
			mPt.setColor(Color.BLACK);
			mPt.setTextSize(mFontSize);
			
			mPt.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(mShowText, mOutRectF.centerX() , mOutRectF.centerY()+10, mPt);
		}
	}
	

	public void timer1Start() {
		if (!mIsTimer1Start) {
			mTimer1.start();
			mIsTimer1Start = true;
		}
	}

	public void timer1Cancel() {
		if (mIsTimer1Start) {
			mTimer1.cancel();
			mIsTimer1Start = false;
		}
	}
	
	public void timer3Start() {
		if (!mIsTimer3Start) {
			mTimer3.start();
			mIsTimer3Start = true;
		}
	}

	public void timer3Cancel() {
		if (mIsTimer3Start) {
			mTimer3.cancel();
			mIsTimer3Start = false;
		}
	}

	public void timer1and2Start() {
		if (!mIsTimer1Start) {
			mTimer1.start();
			mIsTimer1Start = true;
		}
		if (!mIsTimer2Start) {
			mTimer2.start();
			mIsTimer2Start = true;
		}
	}

	public void timer1and2Cancel() {
		if (mIsTimer1Start) {
			mTimer1.cancel();
			mIsTimer1Start = false;
		}
		if (mIsTimer2Start) {
			mTimer2.cancel();
			mIsTimer2Start = false;
		}
	}
	
	public class Timer1 extends CountDownTimer{

		public Timer1(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			dismiss();
			mShowText = mKey.valueList.get(1);	
			show();
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}

	public class Timer2 extends CountDownTimer{

		public Timer2(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			if (mKey.valueList.size() > 2) {
				dismiss();
				mParent.showminikeyboard();
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}
	
	public class Timer3 extends CountDownTimer{

		public Timer3(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			dismiss();
			mShowText = mKey.valueList.get(0);
			show();
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}

}
