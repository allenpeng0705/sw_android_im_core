package com.shapewriter.android.softkeyboard;

public class SWI_PageTap extends SWI_PageManager {

	public SWI_PageTap(SWI_SoftkeyboardService service) {
		super(service);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean handleKey(String label, String value){
		if(!super.handleKey(label, value)){
			if(label.equals(SWI_SoftKeyBase.LABEL_SPACE))	mService.sendText(" ", 0);
			else if(label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)) mService.sendText(null, 1);
			else	mService.sendText(value, 0);
		}
		return true;
	}
	
	@Override
	public void receiveKeyboardText(String text, boolean bInsert){
		if ((text == null) && (bInsert == false)) {
			mService.sendText(null, 1);
		}
	}
	
}
