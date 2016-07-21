package com.shapewriter.android.softkeyboard;

import com.shapewriter.android.softkeyboard.recognizer.RCO;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

public class SWI_KeyboardView extends View {
	
	protected static final int MAX_MOVE_COUNT_BEFORE_DELETE = 6;
	
	protected SWI_PageManager mPageManager;
	protected SWI_KeyboardBase mKeyboardBase;
	protected SWI_KeyPopupWindow mPreviewPopup;
	protected SWI_MiniKeyPopupWindow mMiniPreviewPopup;
	protected boolean mMiniKeyboardOnScreen = false;
	protected Paint mMaskPaint;
	protected SWI_PopKeyboard mPopkeyboard;
        
    /**Indicate whether the typed key is '.', '@' or ':)' , added by andy */
    public boolean mIsPunctuation = false;
    public void resetMiniKeyboard(){};
    public void hidenMiniKeyboard(){};

	protected void checkForDelete() {
		mPageManager.StartBackspace();
	};
	
	/** The keyboard bitmap for faster updates */
    private Bitmap mBuffer;
    /** The canvas for keyboard bitmap: mBuffer */
    protected Canvas mCanvas;		
    protected SWI_SoftKeyBase mCurKey;
    
    
    public void setRCO(RCO rco){};
    public void setCmdStrokes(SWI_CommandStrokes commandStrokes){};
    public boolean isWordInRCOLexicon(String word) {return false;};
    public void showCandidateWordListByResamplePoints(String str) {};
    public void replay(){};
    public void setShowIdealShape(boolean show){};
    protected void onBufferDraw() {};
    public void showminikeyboard(){};	
	protected void handleQuickDoubleClickInView(MotionEvent event){};
    public boolean handleQuickDoubleClick(MotionEvent event,
			SWI_SoftKeyBase key, int mouseStatus){
		if (mCurKey.label.equals(SWI_SoftKeyBase.LABEL_SPACE)
				|| mCurKey.label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)
				/*|| mCurKey.label.equals(SWI_SoftKeyBase.LABEL_LANGUAGE)*/) {
			if (SWI_QuickDoubleClickHandle.instance().handleQuickDoubleClick(
					event, key, mouseStatus)) {
				handleQuickDoubleClickInView(event);
				return true;
			}
		}		
		return false;
    }
	public SWI_KeyboardView(Context context) {
		super(context);	
		mPreviewPopup = new SWI_KeyPopupWindow(context);
		mMiniPreviewPopup = new SWI_MiniKeyPopupWindow(context);
		mPopkeyboard = new SWI_PopKeyboard(context, this);
		mPopkeyboard.attach(this);
		mMaskPaint = new Paint();
		mMaskPaint.setStyle(Style.FILL);
		mMaskPaint.setARGB(180, 0, 0, 0);
	}
	
	public void destroy(){
		mPreviewPopup.destroy();
		mBuffer.recycle();
		mPreviewPopup = null;
		mMaskPaint = null;
		mCanvas = null;
		mPageManager = null;
		mKeyboardBase = null;
		mBuffer = null;
	}
	
	public void setPageLayout(SWI_PageManager pageLayout){
		mPageManager = pageLayout;
	}
	
	public void setKeyboardBase(SWI_KeyboardBase keyboardBase){
		mKeyboardBase = keyboardBase;
		mBuffer = Bitmap.createBitmap(mKeyboardBase.width, mKeyboardBase.height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBuffer);
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mKeyboardBase.width, mKeyboardBase.height);
	}
    
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawBitmap(mBuffer, 0, 0, null);
	}

	
	protected SWI_SoftKeyBase getKey(int x, int y){
		int size = mKeyboardBase.keyList.size();
		for(int i = 0; i < size; i++){
			if(mKeyboardBase.keyList.get(i).isMouseIn(x, y)){
				return mKeyboardBase.keyList.get(i);
			}
		}
		return null;
	}
	
	protected boolean hasPopupWindow(SWI_SoftKeyBase key){
		if( key == null)
			return false;
		String type = key.type;
		String value = key.valueList.get(0);
		if (/*key.label.equals(SWI_SoftKeyBase.LABEL_LANGUAGE)
				|| */key.label.equals(SWI_SoftKeyBase.LABEL_CASE)
				|| key.label.equals(SWI_SoftKeyBase.LABEL_COMMAND)) {
			return true;
		}
		if(type.equals(SWI_SoftKeyBase.TYPE_FUNCTION) || type.equals(SWI_SoftKeyBase.TYPE_COMMAND))
			return false;
		else if(value.equals(""+' ') || value.equals(""+'\t'))
			return false;
		return true;
	}
	
	protected void clearKeyState(){
		int size = mKeyboardBase.keyList.size();
		for(int i = 0; i < size; i++){
			mKeyboardBase.keyList.get(i).isPress = false;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			mPageManager.onPress();
		} else if (action == MotionEvent.ACTION_UP) {
			mPageManager.StopBackspace();
		}
		return true;
	}
	public void sendtext(String label,String value){
		mPageManager.handleKey(label, value);
	}
	
}
