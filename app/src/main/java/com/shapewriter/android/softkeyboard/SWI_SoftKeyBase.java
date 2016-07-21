package com.shapewriter.android.softkeyboard;

import java.util.ArrayList;

import android.graphics.Bitmap;


public class SWI_SoftKeyBase {
	public static final String TYPE_FUNCTION = "function";
	public static final String TYPE_SYMBOL = "symbol";
	public static final String TYPE_NUMBER = "number";
	public static final String TYPE_ALPHA = "alpha";
	public static final String TYPE_COMMAND = "command";
	
	public final static String LABEL_ENTER = "enter";
	public final static String LABEL_CASE = "case";
	public final static String LABEL_COMMAND = "command";
	public final static String LABEL_NUMBERS = "number";
	public final static String LABEL_ALPHA = "alpha";
	public final static String LABEL_BACKSPACE = "backspace";
	public final static String LABEL_LANGUAGE = "language";
	public final static String LABEL_SPACE = "space";
	public final static String LABEL_SMILE = ":)";
	public final static String LABEL_AT = "@";
	public final static String LABEL_PERIOD = ".";
		
	
	public int left;
	public int top;
	public int width;
	public int height;
	public String type;
	public String label;
	public ArrayList<String> valueList;
	public ArrayList<Character> mapList;
	public Bitmap highLightImage;
	public int highLightLeft;
	public int highLightTop;
	public boolean isPress;
	public String anchor;
	
	public SWI_SoftKeyBase(){
		valueList = new ArrayList<String>();
		mapList = new ArrayList<Character>();
		isPress = false;
	}
	
	public void destroy(){
		valueList.clear();
		mapList.clear();
		highLightImage.recycle();
		valueList = null;
		mapList = null;
		highLightImage = null;
	}

	
	public boolean isMouseIn(int x, int y){
		return (x >= left && x < (left + width) && y >= top && y < (top + height));
	}
	
	public boolean isRCOKey(){
		return (type.equals(TYPE_ALPHA) || type.equals(TYPE_COMMAND) );
	}
}