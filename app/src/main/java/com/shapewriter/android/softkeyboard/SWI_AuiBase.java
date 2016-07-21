package com.shapewriter.android.softkeyboard;

import android.graphics.Bitmap;

public class SWI_AuiBase {
	
	public static final String VERTICAL = "vertical";
	public static final String HORIZONTAL = "horizontal";
		
	public int left = 0;
	public int top = 0;
	public int width = 0;
	public int height = 0;
	
	public int fontSize = 16;
	public int basicLine = 29;
	
	public String name;
	public String mode;

	public int firstArrowLeft;
	public int firstArrowTop;
	public int firstArrowWidth;
	public int firstArrowHeight;
	
	public int secondArrowLeft;
	public int secondArrowTop;
	public int secondArrowWidth;
	public int secondArrowHeight;
	
	public int highLightBegin;
	public int highLightEnd;
	
	public int logoTop;
	public int logoLeft;
	
	public Bitmap background;
	public Bitmap firstArrowSelect;
	public Bitmap firstArrowUnSelect;
	public Bitmap secondArrowSelect;
	public Bitmap secondArrowUnSelect;
	public Bitmap candidateHighLight;
	public Bitmap logo;
	
	public SWI_AuiBase(){
		
	}

	public void destroy(){
		background.recycle();
		firstArrowSelect.recycle();
		firstArrowUnSelect.recycle();
		secondArrowSelect.recycle();
		secondArrowUnSelect.recycle();
		candidateHighLight.recycle();
		logo.recycle();
		
		background = null;
		firstArrowSelect = null;
		firstArrowUnSelect = null;
		secondArrowSelect = null;
		secondArrowUnSelect = null;
		candidateHighLight = null;
		logo = null;
	}
}
