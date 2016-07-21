package com.shapewriter.android.softkeyboard;

import java.util.HashMap;

import android.content.Context;


public class SWI_KeyboardViewSet {
	private Context mContext;
	private HashMap<String, SWI_KeyboardView> mKeyboardViewPool;  
	
	public SWI_KeyboardViewSet(Context context){
		mContext = context;
		mKeyboardViewPool = new HashMap<String, SWI_KeyboardView>();
	}
	
	public void destroy(){
		for(String key : mKeyboardViewPool.keySet()){ 
			mKeyboardViewPool.get(key).destroy(); 
		} 
		mKeyboardViewPool.clear();
		mKeyboardViewPool = null;
		mContext = null;
	}
	
	public SWI_KeyboardView getKeyboardView(SWI_KeyboardBase keyboardBase, boolean isTraceable){
		String name = keyboardBase.name;
		if(mKeyboardViewPool.containsKey(name)){
			return mKeyboardViewPool.get(name);
		}
		else{
			SWI_KeyboardView keyboardView;
			if(isTraceable)
				keyboardView = new SWI_KeyboardViewTrace(mContext);
			else
				keyboardView = new SWI_KeyboardViewTap(mContext);
			keyboardView.setKeyboardBase(keyboardBase);
			mKeyboardViewPool.put(name, keyboardView);
			return keyboardView;
		}		
	}
}
