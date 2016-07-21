package com.shapewriter.android.softkeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SWI_MiniKeyPopupWindow extends PopupWindow{

	private static final int MAX_QUERY_TIMES = 10;
	private SWI_SoftKeyBase mKey;
	private PopupTextView mTextView;
	private View mParent;
	private int mQueryTimes = 0;
	private int mWidth;
	private int mHeight;
	private int mX;
	private int mY;
	private static final int MIN_WIDTH = 50;
	private int mLocation;
	private float mOrginalX;
	private  int KEYWIDTH; 
	
	public SWI_MiniKeyPopupWindow(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mTextView = new PopupTextView(context);
	}
	
	public void setProperty( View parent, SWI_SoftKeyBase key){
		mParent = parent;
		mKey = key;
		mQueryTimes = 0;
		mWidth = Math.max(key.width, MIN_WIDTH);
		mHeight = key.height;
		mX = (int)mOrginalX - (mWidth - key.width) / 2;	
		mY = parent.getHeight() - key.top + 60;	
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
	
	public boolean shouldShow(SWI_SoftKeyBase key){
		mQueryTimes++;
		if(mQueryTimes > MAX_QUERY_TIMES)
			return true;
		else 
			return false;
	}
	
	public void show(){
		showAtLocation(mParent, Gravity.LEFT | Gravity.BOTTOM, mX + KEYWIDTH *(mLocation - 2) , mY);
	}

	public void update(){
		update(mX, mY, mWidth, mHeight);
	}
	// new add
	public void setvalueposition(int position){
		mLocation = position;
	}
	public void setoriginalposition(float originalposition){
		mOrginalX = originalposition;
	}
	public void setkeywidth(int keywidth){
		KEYWIDTH = keywidth;
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
			Rect outRect =  new Rect();
			getDrawingRect(outRect);
			RectF outRectF = new RectF(outRect);
			Paint pt = new Paint();	

			pt.setARGB(0xff, 80, 80, 80);
			pt.setAntiAlias(true);
			pt.setStrokeWidth(1.0f);
			pt.setStyle(Paint.Style.STROKE);
			//canvas.drawRoundRect(outRectF, 6, 6, pt); 
			
			outRectF.left+= 1;
			outRectF.right-=1;
			outRectF.top+=1;
			outRectF.bottom-=1;
			pt.setColor(0xefef6500);
			pt.setStyle(Paint.Style.FILL);
			canvas.drawRoundRect(outRectF, 5, 5, pt); 
			
			pt.setColor(Color.BLACK);
			pt.setTextSize(35);
			pt.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(mKey.valueList.get(mLocation), outRectF.centerX() , outRectF.centerY()+10, pt);
		}
	}
}
