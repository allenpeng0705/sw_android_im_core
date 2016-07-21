package com.shapewriter.android.softkeyboard;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class SWI_PageBase {
	
	public static final int LANGUAGE = 1;
	public static final int NUMBER = 2;
	public static final int ALPHA = 3;
	
	public static final String NAME_CHN_QWERTY_LAND = "chn_qwerty_landscape";
	public static final String NAME_ENG_QWERTY_LAND = "eng_qwerty_landscape";
	public static final String NAME_NUMBER_LAND = "number_landscape";
	
	public static final String NAME_CHN_QWERTY_PORT = "chn_qwerty_portrait";
	public static final String NAME_ENG_QWERTY_PORT = "eng_qwerty_portrait";
	public static final String NAME_NUMBER_PORT = "number_portrait";
	
	
	public int width;
	public int height;
	public String type;
	public String name;
	public boolean traceable;
	public ArrayList<SWI_AuiBase> auiList;
	public SWI_KeyboardBase keyboard;
	public Bitmap marginground;
		
	public SWI_PageBase(){
		auiList = new ArrayList<SWI_AuiBase>();
		keyboard = new SWI_KeyboardBase();
	}
	
	public SWI_AuiBase getLastAui(){
		return auiList.get(auiList.size() - 1);
	}

	public void destroy(){
		marginground.recycle();
		keyboard.destroy();
		for(int i = 0; i < auiList.size(); i++){
			auiList.get(i).destroy();
		}
		auiList.clear();
		marginground = null;
		keyboard = null;
		auiList = null;
	}
}
