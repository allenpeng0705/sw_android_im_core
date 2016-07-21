package com.shapewriter.android.softkeyboard;

import java.util.ArrayList;



import android.graphics.Bitmap;
import android.graphics.PointF;

class SWI_KeyboardBase {
	
	public String name;
	public Bitmap background;
	public Bitmap foreground;
	public ArrayList<SWI_SoftKeyBase> keyList;
	
	public int left = 0;
	public int top = 0;
	public int width = 0;
	public int height = 0;
			
	public SWI_KeyboardBase(){
		keyList = new ArrayList<SWI_SoftKeyBase>();
	}
	
	public void destroy(){
		background.recycle();
		foreground.recycle();
		int size = keyList.size();
		for(int i = 0; i < size; i++){
			keyList.get(i).destroy();
		}
		keyList.clear();
		background = null;
		foreground = null;
		keyList = null;
	}


	public SWI_SoftKeyBase getLastKey(){
		return keyList.get(keyList.size() - 1);
	}

	public PointF getKeyCenter(char symbol){
		int size = keyList.size();
		PointF center = new PointF(0,0);
		for(int i = 0; i < size; i++){
			SWI_SoftKeyBase key = keyList.get(i); 
			if(key.valueList.get(0).charAt(0) == symbol){
				center.x = key.left + key.width / 2;
				center.y = key.top + key.height / 2;
				break;
			}
		}
		return center;
	}
}
