package com.shapewriter.android.softkeyboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;



public class SWI_MargingroundView extends View {

	private int height;
	private int width;
	private Rect rect;
	private Bitmap marginBitmap;
	
	public SWI_MargingroundView(Context context, SWI_PageBase pageBase) {
		super(context);
		marginBitmap = pageBase.marginground;
		rect = new Rect();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    	int screenWidth = wm.getDefaultDisplay().getWidth();
		width = Math.max(0, (screenWidth - pageBase.width) / 2);
		height = pageBase.height;
		rect.set(0, 0, width, height);
	}

	public SWI_MargingroundView(Context context, int mainWidth, int mainHeignt, Bitmap margin) {
		super(context);
		rect = new Rect();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    	int screenWidth = wm.getDefaultDisplay().getWidth();
		width = Math.max(0, (screenWidth - mainWidth) / 2);
		height = mainHeignt;
		marginBitmap = margin;
		rect.set(0, 0, width, height);
	}
	
	public void destroy(){
		marginBitmap.recycle();
		marginBitmap = null;
		rect = null;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(marginBitmap, null, rect, null);
	}
}
