package com.shapewriter.android.softkeyboard;

import android.util.Log;

import com.shapewriter.android.softkeyboard.recognizer.RCO;

public class SWI_PageChineseTrace extends SWI_PageManager {

	private RCO mRCO;
	public SWI_PageChineseTrace(SWI_SoftkeyboardService service) {
		super(service);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void destroy(){
		mRCO = null;
		super.destroy();
	}

	@Override
	public void setRCO(SWI_RCOSet rcoSet,RCO rco){
		Log.e("chen","setRCO in child");
		mRCO = rco;
	}

}
