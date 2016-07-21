package com.shapewriter.android.softkeyboard;

import java.util.ArrayList;
import java.util.List;


import com.shapewriter.android.softkeyboard.recognizer.InputSignal;
import com.shapewriter.android.softkeyboard.recognizer.RCO;
import com.shapewriter.android.softkeyboard.recognizer.ResultSet;
import com.shapewriter.android.softkeyboard.recognizer.SamplePoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

public class SWI_KeyboardViewTrace extends SWI_KeyboardView {
	
	private static final int MOUSE_DOWN = 1;
	private static final int MOUSE_DRAG = 2;
	private static final int MOUSE_NONE = 3;

	
	private static final int MSG_CLEAR_STANDARD_LINE = 1;
	
	private RCO mRCO;
	private SWI_SoftKeyBase mDownKey;
	private int mMouseState = MOUSE_NONE;
	private ArrayList<PointF> mSamplePoints;
	private ArrayList<PointF> mReplayPoints;
	private ArrayList<PointF> mIdealShapePoints;
	private Paint mTraceLinePt;
	private Paint mStandardLinePt;
	private long lastMovementTimestamp;
	private SWI_CommandStrokes mCommandStrokes = null;
	private Handler mHandler;
	private boolean mShowReplay = false;
	private int mReplayPointNum = 0;
	private boolean mShowIdealShape = true;
	private String mTopCandidate;
	
	public SWI_KeyboardViewTrace(Context context) {
		super(context);
		
		mSamplePoints = new ArrayList<PointF>();
		mReplayPoints = new ArrayList<PointF>();
		mIdealShapePoints = new ArrayList<PointF>();
		
		mTraceLinePt = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTraceLinePt.setARGB(255, 127, 127, 255);
		mTraceLinePt.setStrokeWidth(3.5f);
		
		mStandardLinePt = new Paint(Paint.ANTI_ALIAS_FLAG);
		mStandardLinePt.setARGB(255, 127, 127, 200);
		mStandardLinePt.setStrokeWidth(3.5f);
		
		mHandler = new Handler(){
			@Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case MSG_CLEAR_STANDARD_LINE:
	                	mIdealShapePoints.clear();
	        			onBufferDraw();
	        			invalidate();
	                break;
	            }
	        }
		};
	}

	@Override
	public void destroy() {
		mRCO = null;
		mCurKey = null;
		mDownKey = null;;
		mSamplePoints = null;
		mReplayPoints = null;
		mIdealShapePoints = null;
		mTraceLinePt = null;
		mStandardLinePt = null;
		mCommandStrokes = null;
		mHandler = null;
		mTopCandidate = null;
		super.destroy();
	}



	@Override
	public void setRCO(RCO rco){
		mRCO = rco;
	}
	
	@Override
	public void setCmdStrokes(SWI_CommandStrokes commandStrokes){
		mCommandStrokes = commandStrokes;
		mCommandStrokes.setHandler(mHandler);
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
        ArrayList<SWI_SoftKeyBase> keyList = mKeyboardBase.keyList;
        for(int i = 0; i < size; i++){
        	if(keyList.get(i).isPress){
        		canvas.drawBitmap(keyList.get(i).highLightImage, 
        				keyList.get(i).highLightLeft, keyList.get(i).highLightTop, null);
        		break;
        	}
        }

        canvas.drawBitmap(mKeyboardBase.foreground, 0, 0, null);
        
        if(mMiniKeyboardOnScreen){
            canvas.drawRect(0, 0, mKeyboardBase.width, mKeyboardBase.height, mMaskPaint);
            return;
        }
        
        if(mShowReplay){
        	for(int i = 0; i < mReplayPointNum; i++){
        		canvas.drawLine(mReplayPoints.get(i).x, mReplayPoints.get(i).y, 
        				mReplayPoints.get(i+1).x, mReplayPoints.get(i+1).y, mTraceLinePt);
        	}
        	return;
        }
        
        if(mShowIdealShape){
        	int alphaNum = mIdealShapePoints.size();
        	for(int i = 0; i < alphaNum - 1; i++){
        		canvas.drawLine(mIdealShapePoints.get(i).x, mIdealShapePoints.get(i).y, 
        				mIdealShapePoints.get(i+1).x, mIdealShapePoints.get(i+1).y, mStandardLinePt);
        	}
        }
        
        int pointsNum = mSamplePoints.size();
        for(int i = 0; i < pointsNum - 1; i++){
        	canvas.drawLine(mSamplePoints.get(i).x, mSamplePoints.get(i).y, 
        			mSamplePoints.get(i+1).x, mSamplePoints.get(i+1).y, mTraceLinePt);
        }
        
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if(mMiniKeyboardOnScreen){
			return true;
		}
		
		super.onTouchEvent(event);
		
		int action = event.getAction();	
		int x = (int)event.getX();
		int y = (int)event.getY();
		clearKeyState();	
		if (x < 0 || y < 0) { // Handle tracing line into AUI
			if (mPreviewPopup.isShowing()) {
				mPreviewPopup.dismiss();
			}
			mPreviewPopup.timer1and2Cancel();
			mPreviewPopup.timer3Cancel();
		}
		
		mCurKey = getKey(x, y);
		if(mCurKey == null){
			if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
				mSamplePoints.add(new PointF(x,y));
				mTopCandidate = null;
			}
			else if( action == MotionEvent.ACTION_UP && mMouseState == MOUSE_DRAG){
				mTopCandidate = handleRecognizeResult(recognize(mSamplePoints), true);
				mSamplePoints.clear();
			}
			else{
				mSamplePoints.clear();
			}
			clearKeyState();
			mPreviewPopup.dismiss();
			mPopkeyboard.dismiss();
			showStandardTraceLine(mTopCandidate);
			onBufferDraw();
			invalidate();
			return true;
		}
		
		
		mCurKey.isPress = true;
		
		/**
		 * Handle quick double click for some keys
		 */
		if (handleQuickDoubleClick(event, mCurKey, mMouseState)) {
			return true;
		}
		
		/**
		 * Check quick single click for above keys
		 */
		if (mCurKey != mDownKey && action == MotionEvent.ACTION_DOWN) {
			SWI_QuickDoubleClickHandle.instance().checkQuickSingleClick();
		}
		
		// if trace onto other key, clear quick double click status, and stop backspace timer.
		if (mDownKey!=null && mCurKey != mDownKey ) {
			SWI_QuickDoubleClickHandle.instance().clearStatus();
			mPageManager.StopBackspace();
		}

		
		if (action == MotionEvent.ACTION_DOWN ) {
			/**
			 * Show Pop-Up Window
			 */
			if( hasPopupWindow(mCurKey) ){
				mPreviewPopup.setProperty(this, mCurKey);
				if (mCurKey.type.equals(SWI_SoftKeyBase.TYPE_ALPHA)) {
					mPreviewPopup.timer1and2Start();
				}
				
				if(	! mPreviewPopup.isShowing() ){
					mPreviewPopup.show();
				}
				else{
					mPreviewPopup.update();
				}
			}			
			
			mMouseState = MOUSE_DOWN;
			mShowReplay = false;
			mDownKey = getKey(x, y);
			checkForDelete();
						
			mSamplePoints.add(new PointF(x,y));
			lastMovementTimestamp = System.nanoTime();
			mIdealShapePoints.clear();
			mHandler.removeMessages(MSG_CLEAR_STANDARD_LINE);
			
		}
		
		else if(action == MotionEvent.ACTION_MOVE){
			
			/**
			 * Show Pop-Up Window
			 */
			if (hasPopupWindow(mCurKey)) {
				if (!mPreviewPopup.isSameProperty(mCurKey)) { // new button down
					if (mCurKey.type.equals(SWI_SoftKeyBase.TYPE_ALPHA)) {
						mPreviewPopup.timer1and2Cancel();
						mPreviewPopup.timer3Cancel();
						mPreviewPopup.timer3Start();
					}
					if (mPreviewPopup.isShowing()) {
						mPreviewPopup.dismiss();
					}
					mPreviewPopup.setProperty(this, mCurKey);
				}
			} else {
				if (mPreviewPopup.isShowing()) {
					mPreviewPopup.dismiss();
				}
				mPreviewPopup.timer1and2Cancel();	
				mPreviewPopup.timer3Cancel();
			}
			
			
			mSamplePoints.add(new PointF(x,y));
			lastMovementTimestamp = System.nanoTime();
			
			if (mDownKey != null && mDownKey.label.equals(SWI_SoftKeyBase.LABEL_COMMAND)
					&& mCommandStrokes != null && (mDownKey != mCurKey)) {
				if (mCommandStrokes.isActivated() == false){ 
					mCommandStrokes.setActivated(true);
				}
			}
			else if (mCommandStrokes != null && mCommandStrokes.isActivated()){
				mCommandStrokes.setActivated(false);
			}
			
			/**
			 * Update Mouse State: Down or Drag
			 */

			if (mMouseState != MOUSE_DRAG) {
				if (mDownKey != mCurKey) {
					mMouseState = MOUSE_DRAG;
					mPageManager.StopBackspace();
				} 
			}			
		}
		
		/**
		 * action == MotionEvent.ACTION_UP
		 */
		else {
			mDownKey = null;
			mTopCandidate = null;
			
			/**
			 * Trace Mode
			 */
			if(mMouseState == MOUSE_DRAG){
				mIsPunctuation = false; // Declared in SWI_KeyboardView, added by andy.
				/**
				 * Command Stroke Recognition
				 */
				if(mCommandStrokes != null && mCommandStrokes.isActivated()) {
					mCommandStrokes.finalCommandStrokesRecognition();
				}
				
				/**
				 * Trace Recognition
				 */
				else{
					mTopCandidate = handleRecognizeResult(recognize(mSamplePoints), true);
					mReplayPoints.clear();
					int size = mSamplePoints.size();
					for(int i = 0; i < size; i++){
						mReplayPoints.add(new PointF(mSamplePoints.get(i).x, mSamplePoints.get(i).y));
					}
				}
				if (mPreviewPopup.isShowing()) {
					mPreviewPopup.dismiss();
				}
			}
			
			/**
			 * Tap Mode
			 */
			else if(mMouseState == MOUSE_DOWN){
				if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_COMMAND)) {
					mPageManager.launchCommandPage();
				}
				else if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)){
				}
				else if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_PERIOD) ||
						mCurKey.label.equals(SWI_SoftKeyBase.LABEL_AT) ||
						mCurKey.label.equals(SWI_SoftKeyBase.LABEL_SMILE)){
					mIsPunctuation = true; // Declared in SWI_KeyboardView, added by andy.
					mPageManager.handleKey(mCurKey);
				}
				else{
					mPageManager.handleKey(mCurKey.label, mPreviewPopup.getShowText());
				}
			}
			
			mPreviewPopup.dismiss();
			mPreviewPopup.timer1and2Cancel();
			mPreviewPopup.timer3Cancel();
			clearKeyState();
			mSamplePoints.clear();
			showStandardTraceLine(mTopCandidate);
		}
	
		
		
		onBufferDraw();
		invalidate();
		return true;
	}
	
	@Override
	protected void checkForDelete() {
		if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)){
			if(!mIsPunctuation){
				mPageManager.receiveKeyboardText(null, false);
			}
			else{
				mPageManager.receiveKeyboardText("", false);
				mPageManager.handleKey(mCurKey.label, null);
				mIsPunctuation = false;
			}
			super.checkForDelete();
		}
	}
	
	private static String getStringFromRCOResult(char[] ca) {
		for (int i = 0, n = ca.length; i < n; i++) {
			char c = ca[i];
			if (c == '\0') {
				return String.valueOf(ca, 0, i);
			}
		}
		return String.valueOf(ca);
	}
	
	private ResultSet recognize(ArrayList<PointF> samplePoints){
		int n = samplePoints.size();
		if (n == 0 || n == 1) {
			return null;
		} 
		else {
			if (n > 500) n = 500;	
			InputSignal input_signal = new InputSignal();
			input_signal.iCount = n;
			input_signal.iSamplePoints = new SamplePoint[n];
			for (int i = 0; i < n; i++) {
				PointF pt = samplePoints.get(i);
				input_signal.iSamplePoints[i] = new SamplePoint();
				input_signal.iSamplePoints[i].x = pt.x;
				input_signal.iSamplePoints[i].y = pt.y;
				input_signal.iSamplePoints[i].t = 0.0f;
			}
			synchronized (mRCO) {
				return mRCO.Recognize(input_signal, null);
			}
		}
	}
	
	private String handleRecognizeResult(ResultSet rs, boolean bSend){
		if(rs == null || rs.result_count == 0)
			return null;
		mPageManager.keyboardResultPrepare();
		ArrayList<String> array = new ArrayList<String>();
		for (int i = 0; i < rs.result_count; ++i) {
			String text = getStringFromRCOResult(rs.results[i].str.toCharArray());
			array.add(text);
		}
		
		SWI_MisspellingTable.instance().fromErrorToCorrect(array);
		int size = array.size();
		String szTopResult = array.get(0);		
		for (int i = 0; i <size ; ++i) { 
			String word = array.get(i);
			double distance = rs.results[i].distance;
			int type = rs.results[i].result_type;
			//Log.e("distance:",""+distance);
			//Log.e("tpye:",""+type);
			mPageManager.addKeyboardResult(word,distance,type);
		}
		
		mPageManager.keyboardResultDone(bSend);
		mPageManager.addCachedCandidateWordList(szTopResult, array);
		return szTopResult;
	}
	
	@Override
	public void setShowIdealShape(boolean show){
		mShowIdealShape = show;
	}
	
	
	@Override
	public boolean isWordInRCOLexicon(String word) {
		return mRCO.IsWordExistInRCO(word);
	}
	
	@Override
	public void showCandidateWordListByResamplePoints(String str) {
		ArrayList<PointF> samplePoints = getStringSamplePoints(str);
		handleRecognizeResult(recognize(samplePoints), false);
	}
	
	private ArrayList<PointF> getStringSamplePoints(String str) {
		ArrayList<PointF> samplePoints = new ArrayList<PointF>();
		if (str == null || str.length() <= 0) {
			return samplePoints;
		}

		ArrayList<SWI_SoftKeyBase> keysContainer = mKeyboardBase.keyList;
		final int keysContainerSize = keysContainer.size();

		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);

			int keyX = -1;
			int keyY = -1;
			for (int j = 0; j < keysContainerSize; ++j) {
				SWI_SoftKeyBase key = keysContainer.get(j);
				if (Character.toUpperCase(key.valueList.get(0).charAt(0)) == Character
						.toUpperCase(c)) {
					keyX = key.left + (key.width / 2);
					keyY = key.top + (key.height / 2);
					break;
				}
			}

			boolean bCharFound = (keyX != -1 && keyY != -1);
			if (bCharFound) {
				samplePoints.add(new PointF(keyX, keyY));
			}
		}
		return samplePoints;
	}
	
	public List<PointF> getSamplePoints(){
		return mSamplePoints;
	}
	
	public long getLastMovementTimestamp() {
		return lastMovementTimestamp;
	}
	
	@Override
	public void replay(){
		mShowReplay = true;
		mReplayPointNum = 0;
		mHandler.post(mReplayAction);
	}
	
	private Runnable mReplayAction = new Runnable() {
		private final static int REPLAY_INTERVAL = 25;
		private final static int LAST_STAY_TIME = 200;
		@Override
		public void run() {
			if (mShowReplay && mReplayPointNum < mReplayPoints.size() - 1) {
				onBufferDraw();
				invalidate();
				mReplayPointNum++;
				mHandler.postDelayed(mReplayAction, REPLAY_INTERVAL);
			}
			else{
				mReplayPointNum = 0;
				mShowReplay = false;
				onBufferDraw();
				mHandler.postDelayed(mInvalidate, LAST_STAY_TIME);
			}
		}
	};
	
	private Runnable mInvalidate = new Runnable() {
		@Override
		public void run() {
			invalidate();
		}
	};

	private void showStandardTraceLine(String word){
		if(mShowIdealShape && word != null){
			mIdealShapePoints.clear();
			word = word.toLowerCase();
			int length = word.length();
			for(int i = 0; i < length; i++){
				char symbol = word.charAt(i);
				if(symbol >= 'a' && symbol <='z'){
					mIdealShapePoints.add(mKeyboardBase.getKeyCenter(symbol));
				}
			}
			onBufferDraw();
			invalidate();
			Message msg = mHandler.obtainMessage(MSG_CLEAR_STANDARD_LINE, null);
            mHandler.sendMessageDelayed(msg, 1000);
		}
	}
	//new add
	@Override
	public void showminikeyboard(){
		mMiniKeyboardOnScreen = true;
		mPopkeyboard.dismiss();
		mPreviewPopup.timer1and2Cancel();
		
		if (mCurKey != null) {
			mPopkeyboard.showPopKeyboard(mCurKey);
			onBufferDraw();
			invalidate();
		} else {
			mMiniKeyboardOnScreen = false;
		}
	}
	
	@Override
	 public void resetMiniKeyboard(){
		mMiniKeyboardOnScreen = false;
		clearKeyState();
		mSamplePoints.clear();
		onBufferDraw();
		invalidate();
	}
	
	@Override
	public void hidenMiniKeyboard(){
		mMiniKeyboardOnScreen = false;
		clearKeyState();
		mSamplePoints.clear();
		onBufferDraw();
		invalidate();
		mPopkeyboard.dismiss();
		
	}
	
	@Override
	protected void handleQuickDoubleClickInView(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mMouseState = MOUSE_DOWN;
			mDownKey = getKey((int) event.getX(), (int) event.getY());
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			clearKeyState();
			mMouseState = MOUSE_NONE;
			mSamplePoints.clear();
			onBufferDraw();
			invalidate();
		}
	};
}
