package com.shapewriter.android.softkeyboard;

import com.shapewriter.android.softkeyboard.SWI_SoftkeyboardService.Word;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputConnection;


public class SWI_QuickDoubleClickHandle {

	private static final int INTERVAL_QUICK_DOUBLE_CLICK_TIME = 300; 
	private static SWI_QuickDoubleClickHandle _instance = null;
	
	public boolean mQuickDoubleClickStart = false;  // indicate first click down event
	public boolean mFirstClickUp = false;           // indicate first click up event
	public boolean mSecondClickDown = false;        // indicate second click down event
	public boolean mSecondClickUp = false;          // indicate second click up event
	
	private QuickDoubleClickTimer mQuickDoubleClickTimer;
	private SWI_PageManager mPageManager;
	private SWI_SoftKeyBase mCurKey = null;
	private SWI_SoftKeyBase mLastDownKey = null;
	private int mMouseStatus = 3;
	private int mGoToSettingCount = 0;
	

	private SWI_QuickDoubleClickHandle() {
		mQuickDoubleClickTimer = new QuickDoubleClickTimer(
				INTERVAL_QUICK_DOUBLE_CLICK_TIME,
				INTERVAL_QUICK_DOUBLE_CLICK_TIME);
	}

	public static SWI_QuickDoubleClickHandle instance() {
		if (_instance == null) {
			_instance = new SWI_QuickDoubleClickHandle();
		}

		return _instance;
	}
	
	public void setPageManager(SWI_PageManager pageManager){
		mPageManager = pageManager;
	}
	
	public static void Destroy(){
		if (_instance != null) _instance.destroy();
		_instance = null;
	}

	private void destroy() {
		if (mQuickDoubleClickTimer != null) {
			mQuickDoubleClickTimer.cancel();
			mQuickDoubleClickTimer = null;
		}
		mCurKey = null;
	}
	
	public void checkQuickSingleClick(){
		if(null != mCurKey) {
			handleQuickSingleClick();
		}
	}
	
	public void handleQuickSingleClick() {
		mQuickDoubleClickTimer.cancel();
		if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_SPACE)
				|| mCurKey.label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)){
			handleNormalClick(mCurKey);
		}
	}

	public String getTextOnCursor() {
		ExtractedText extrText = mPageManager.mService.getExtractedText();
		String currStr = extrText.text.toString();
		Word currWord = mPageManager.mService.getWordByPos(currStr,
				extrText.selectionEnd);
		String keyWord = currStr.substring(currWord.mStart,
				currWord.mEnd == currStr.length() ? currWord.mEnd
						: currWord.mEnd + 1);
		return keyWord;
	}
	
	public void deleteTextOnCursor(){		
		ExtractedText extractedText = mPageManager.mService.getExtractedText();
		int idxStart = extractedText.selectionStart;
		int idxEnd = extractedText.selectionEnd;
		
		String currStr = extractedText.text.toString();
		InputConnection connection = mPageManager.mService
				.getCurrentInputConnection();
	
		Word currWord = mPageManager.mService.getWordByPos(currStr, idxEnd);	
		
		int delLeft = idxEnd - currWord.mStart;
		int delRight = currWord.mEnd >= extractedText.text.length() ? currWord.mEnd
				- idxEnd
				: currWord.mEnd - idxEnd + 1;
		
		connection.beginBatchEdit();
		if (idxEnd == idxStart && idxEnd > currWord.mStart
				&& idxEnd <= currWord.mEnd) {
			int selectionOffset = idxEnd
					+ extractedText.startOffset + delRight;
			selectionOffset = Math.min(selectionOffset, currStr
					.length()
					+ extractedText.startOffset);
			connection.setSelection(selectionOffset,
					selectionOffset);

			delLeft += delRight;
			delRight = 0;
		}
		
		if (currWord.mStart > 1) {
			if (extractedText.text.charAt(currWord.mStart - 1) == ' ') {
				delLeft += 1;
			}
		}
		
		if(delLeft == 0 && delRight == 0)
		{
			delLeft = 1;
			delRight = 0;
		}
		
		connection.deleteSurroundingText(delLeft, delRight);
		connection.endBatchEdit();
	}
	
	public boolean handleQuickDoubleClick(MotionEvent event,
			SWI_SoftKeyBase key, int mouseStatus) {
		int action = event.getAction();  
		mCurKey = key;
		mMouseStatus = mouseStatus;

		if (action == MotionEvent.ACTION_DOWN) {
			// eg: click space firstly, then click backspace
			if (mLastDownKey != null && mCurKey != mLastDownKey && mMouseStatus == 1) {
				mQuickDoubleClickTimer.cancel(); // cancel timer
				handleNormalClick(mLastDownKey); // single click
			}
/*			if (mCurKey.label.equals(SWI_SoftKeyBase.LABEL_LANGUAGE)) {
				mPageManager.mKeyboardView.mPreviewPopup.setProperty(mPageManager.mKeyboardView, mCurKey);
				mPageManager.mKeyboardView.mPreviewPopup.show();
			}*/
			if (!mQuickDoubleClickStart) {
				StartQuickDoubleClick();
				mQuickDoubleClickStart = true;
			} else {
				mSecondClickDown = true;
			}
			mLastDownKey = mCurKey;			
		} else if (action == MotionEvent.ACTION_MOVE) {
			return false;

		} else if (action == MotionEvent.ACTION_UP) {
			// when tracing line and click up on this key, don't care, return false.
			if (mMouseStatus == 2) {
				clearStatus();
				return false;
			}
			
			if (mCurKey.label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)) {
				mPageManager.StopBackspace();
			} /*else if (mCurKey.label.equals(SWI_SoftKeyBase.LABEL_LANGUAGE)) {
				if(	mPageManager.mKeyboardView.mPreviewPopup.isShowing() ){
					mPageManager.mKeyboardView.mPreviewPopup.dismiss();
				}
				if (3 < mGoToSettingCount) {
					clearStatus();
					mGoToSettingCount = 0;
					mPageManager.launchSettings();
				}
			}*/
			if (mQuickDoubleClickStart) {
				if (mMouseStatus == 1) {
					if (!mFirstClickUp) {
						mFirstClickUp = true;						
					} else if (mSecondClickDown) {
						mSecondClickUp = true;
						handleQuickDoubleClick();
					}
				}
			}
		}
		return true;
	}
	
	public void StartQuickDoubleClick() {
		if (!mQuickDoubleClickStart) {
			mQuickDoubleClickTimer.start();
			mQuickDoubleClickStart = true;
		}
	}

	public void StopQuickDoubleClick() {
		if (mQuickDoubleClickStart) {
			mQuickDoubleClickTimer.cancel();
			mQuickDoubleClickStart = false;
		}
	}
	
	public class QuickDoubleClickTimer extends CountDownTimer{

		public QuickDoubleClickTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			if (mQuickDoubleClickStart && mMouseStatus == 1) {
				if (!mFirstClickUp) {
					if (mCurKey.label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)) {
						clearStatus();
						mPageManager.StartBackspace();
					}/* else if (mCurKey.label
							.equals(SWI_SoftKeyBase.LABEL_LANGUAGE)) {
						if (mGoToSettingCount++ < 3) {
							mQuickDoubleClickTimer.start();
						} else {
							mPageManager.mKeyboardView.mPreviewPopup.dismiss();
							mPageManager.mKeyboardView.mPreviewPopup
									.setShowText("Setting");
							mPageManager.mKeyboardView.mPreviewPopup
									.setPopupWindowWidth(Math.max(
											mCurKey.width, 50) * 2 + 50);
							mPageManager.mKeyboardView.mPreviewPopup.show();
						}						
					} */else {
						mQuickDoubleClickTimer.start();
					}
				} else if (!mSecondClickDown) {
					handleNormalClick(mCurKey);
				} else if (!mSecondClickUp) {
					mQuickDoubleClickTimer.start();
				} else {
					handleNormalClick(mCurKey);
				}
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}
	
	public void handleNormalClick(SWI_SoftKeyBase key){
		if(key.label.equals(SWI_SoftKeyBase.LABEL_SPACE)){
			mPageManager.handleKey("space", "");
		} else if(key.label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)){
			if (!mPageManager.mKeyboardView.mIsPunctuation) {		
				mPageManager.receiveKeyboardText(null, false);
			} else {
				mPageManager.receiveKeyboardText("", false);
				mPageManager.handleKey(key.label, null);
				mPageManager.mKeyboardView.mIsPunctuation = false;
			}			
		} /*else if(key.label.equals(SWI_SoftKeyBase.LABEL_LANGUAGE)){
			mPageManager.switchPage(SWI_PageBase.LANGUAGE);
		}*/
		
		clearStatus();
	}
	
	public void clearStatus(){
		mQuickDoubleClickStart = false;
		mGoToSettingCount = 0;
		mFirstClickUp = false;
		mSecondClickDown = false;
		mSecondClickUp = false;
		mLastDownKey = null;
		mCurKey = null;
	}
	
	/*
	 * before timer finish, handle quick double click, then cancel timer.
	 */
	public void handleQuickDoubleClick(){
		if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_SPACE)){
			mPageManager.mLengthOfSentString = 1;					
			mPageManager.handleKey(".", ".");
			mPageManager.handleKey(" ", " ");
		} else if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)){
			deleteTextOnCursor();
			if(getTextOnCursor().equals("")){
				if (mPageManager.mAuiViews != null) {
					mPageManager.mAuiViews[0].clearCandidate();
					mPageManager.mAuiViews[0].show();
				}
			}
			
		} /*else if(mCurKey.label.equals(SWI_SoftKeyBase.LABEL_LANGUAGE)){
			SWI_UtilSingleton.instance().toastCurrentLanguage();
		}*/
		mQuickDoubleClickTimer.cancel();
		clearStatus();
	}
}
