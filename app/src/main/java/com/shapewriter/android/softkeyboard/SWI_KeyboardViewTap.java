package com.shapewriter.android.softkeyboard;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class SWI_KeyboardViewTap extends SWI_KeyboardView {
	
	//private SWI_SoftKeyBase mCurKey;

	public SWI_KeyboardViewTap(Context context) {
		super(context);
	}
	
	@Override
	public void destroy() {
		mCurKey = null;
		super.destroy();
	}

	@Override
	public void setKeyboardBase(SWI_KeyboardBase keyboardBase) {
		super.setKeyboardBase(keyboardBase);
		onBufferDraw();
	}
	
	protected void onBufferDraw() {
        final Canvas canvas = mCanvas;
        canvas.drawBitmap(mKeyboardBase.background, 0, 0, null);
        
        int size = mKeyboardBase.keyList.size();
        List<SWI_SoftKeyBase> keyList = mKeyboardBase.keyList;
        for(int i = 0; i < size; i++){
        	if(keyList.get(i).isPress){
        		canvas.drawBitmap(keyList.get(i).highLightImage, 
        				keyList.get(i).highLightLeft, keyList.get(i).highLightTop, null);
        		break;
        	}
        }
        
        canvas.drawBitmap(mKeyboardBase.foreground, 0, 0, null);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		super.onTouchEvent(event);
		
		int action = event.getAction();	
		int x = (int)event.getX();
		int y = (int)event.getY();
		clearKeyState();
		
		mCurKey = getKey(x, y);
		if(mCurKey == null){
			clearKeyState();
			mPreviewPopup.dismiss();
			onBufferDraw();
			invalidate();
			return true;
		}
		mCurKey.isPress = true;
		
		/**
		 * Handle quick double click for some keys
		 */
		if (handleQuickDoubleClick(event, mCurKey, 1)) {
			return true;
		}
			
		if (action == MotionEvent.ACTION_DOWN) {	
			/**
			 * Show Pop-Up Window
			 */
			if (hasPopupWindow(mCurKey)) {
				mPreviewPopup.setProperty(this, mCurKey);
				if (mCurKey.valueList.size()>1) {
					mPreviewPopup.timer1Start();
				}
				mPreviewPopup.show();
			}
			
			checkForDelete();
		} 			
		
		else if(action == MotionEvent.ACTION_MOVE){
			/**
			 * Show Pop-Up Window
			 */
			if (hasPopupWindow(mCurKey)) {
				if (!mPreviewPopup.isSameProperty(mCurKey)) { // new key down
					mPreviewPopup.setProperty(this, mCurKey);
					mPreviewPopup.timer1Cancel();
					mPageManager.StopBackspace();
					mPreviewPopup.dismiss();
					mPreviewPopup.show();
					
					if (mCurKey.valueList.size()>1) {
						mPreviewPopup.timer1Start();
					}
				}
			} else {
				if (mPreviewPopup.isShowing()) {
					mPreviewPopup.dismiss();
				}
				mPreviewPopup.timer1Cancel();			
			}
		}

		
		/**
		 * action == MotionEvent.ACTION_UP
		 */
		else {
			
			if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_COMMAND)) {
				//mPageManager.handleKey(SWI_SoftKey.LABEL_LANGUAGE, mCurKey.getValueList().get(0));
			}else if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)){
				// do nothing here
			}
			else{
				mPageManager.handleKey(mCurKey.label, mPreviewPopup.getShowText());
			}
			
			mPreviewPopup.dismiss();
			mPreviewPopup.timer1Cancel();
			clearKeyState();
		}
	
		
		
		onBufferDraw();
		invalidate();
		return true;
	}
	
	@Override
	protected void checkForDelete() {		
		if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)){
			mPageManager.handleKey(mCurKey.label, null);	
			super.checkForDelete();
		}
	}
	
	@Override
	protected void handleQuickDoubleClickInView(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			clearKeyState();
			onBufferDraw();
			invalidate();
		}
	};
}
