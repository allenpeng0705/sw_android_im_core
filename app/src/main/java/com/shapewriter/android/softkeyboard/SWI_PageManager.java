package com.shapewriter.android.softkeyboard;

import java.util.ArrayList;


import com.shapewriter.android.softkeyboard.recognizer.RCO;
import com.shapewriter.android.softkeyboard.recognizer.Result;



import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

public class SWI_PageManager extends LinearLayout{
	
	private static final int INTERVAL_DELETE_TIME = 300; 
	public SWI_PageBase mPageBase;
	public SWI_KeyboardView mKeyboardView;
	public int mLengthOfSentString = 0; 
	protected String mLastTracedWord = null;
	
	protected SWI_SoftkeyboardService mService;
	protected SWI_AuiView [] mAuiViews;
	protected int mAuiNum = 0;
	
	private SWI_MargingroundView mBackgroundView1;
	private SWI_MargingroundView mBackgroundView2;
	private LinearLayout mInputMethodLayout;
	private boolean mIsHorizontal;
	
	private BackspaceTimer mBackspaceTimer;
	private boolean mIsBackSpaceStart = false;
	
	/**
	 * Sub Class Should Override the Following Methods
	 */
	public void setRCO(SWI_RCOSet rcoSet, RCO rco){};
	public void setResult(Result result){};
	public void setCmdStrokes(SWI_CommandStrokes commandStrokes){};
	public void setDisableAutoEdit(boolean disable){};
	
	public void keyboardResultPrepare(){};
	public void clear(){};
	public void addKeyboardResult(String text,double distance,int type){};
	public void keyboardResultDone(boolean bSend){};
	public void receiveAuiText(String auiName, String text, boolean bInsert){};
	public void receiveKeyboardText(String text, boolean bInsert){};
	public void onUpdateSelection(int oldSelStart, int oldSelEnd,
			int newSelStart, int newSelEnd, int candidatesStart,
			int candidatesEnd) {};
	
	public void addCachedCandidateWordList(String word, ArrayList<String> array) {};
	public void addNewWord(String word){};
	public void showMessage(String message, boolean fade){};
	public void hideMessage(){};
	public boolean autoEdit(){return false;};
	public void replay(){};
	public void handleKey(SWI_SoftKeyBase key){};
	
	
	public SWI_PageManager(SWI_SoftkeyboardService service) {
		super(service);
		// TODO Auto-generated constructor stub
		mService = service;
		setOrientation(LinearLayout.HORIZONTAL);
		mInputMethodLayout = new LinearLayout(mService);
		mBackspaceTimer = new BackspaceTimer(INTERVAL_DELETE_TIME,INTERVAL_DELETE_TIME);
	}
	
	public void destroy(){
		for(int i = 0; i < mAuiNum; i++){
			mAuiViews[i].destroy();
			mAuiViews[i] = null;
		}
		mBackgroundView1.destroy();
		mBackgroundView2.destroy();
		mPageBase = null;
		mKeyboardView = null;
		mService = null;
		mAuiViews = null;
		mBackgroundView1 = null;
		mBackgroundView2 = null;
		mInputMethodLayout = null;
		mBackspaceTimer = null;
	}
	
	public void setPageBase(SWI_PageBase pageBase){
		if(pageBase == mPageBase)
			return;
		mPageBase = pageBase;
		update();
		mBackgroundView1 = new SWI_MargingroundView(mService, pageBase );
		mBackgroundView2 = new SWI_MargingroundView(mService, pageBase );
	}
	
		
	public void compose(){
		mKeyboardView.setPageLayout(this);
		
		if(mIsHorizontal){
			mInputMethodLayout.setOrientation(LinearLayout.VERTICAL);
			for(int i = 0; i < mAuiNum; i++){
				mInputMethodLayout.addView(mAuiViews[i],i);
			}
			mInputMethodLayout.addView(mKeyboardView, mAuiNum);
			addView(mBackgroundView1);
			addView(mInputMethodLayout);
			addView(mBackgroundView2);
		}
		else{
			addView(mBackgroundView1,0);
			for(int i = 0; i < mAuiNum; i++){
				addView(mAuiViews[i],i + 1);
			}
			addView(mKeyboardView, mAuiNum + 1);
			addView(mBackgroundView2,mAuiNum + 2);
		}
	}
	

	private void update(){
		mAuiNum = mPageBase.auiList.size();
		if(mAuiNum != 0){
			if(mPageBase.auiList.get(0).mode.equals(SWI_AuiBase.HORIZONTAL) ){
				mIsHorizontal = true;
			}
			else{
				mIsHorizontal = false;
			}
			mAuiViews = new SWI_AuiView[mAuiNum];
			for(int i = 0; i < mAuiNum; i++){
				if(mIsHorizontal)
					mAuiViews[i] = new SWI_AuiViewHor(mService, mPageBase.auiList.get(i));
				else
					mAuiViews[i] = new SWI_AuiViewVer(mService, mPageBase.auiList.get(i));
				mAuiViews[i].setPageManager(this);
			}
		}
		
		mKeyboardView = mService.mKeyboardViewSet.getKeyboardView(mPageBase.keyboard, mPageBase.traceable);		
	}
	public boolean handleKey(String label, String value){
		if (label.equals(SWI_SoftKeyBase.LABEL_ALPHA)) {
			switchPage(SWI_PageBase.ALPHA);
		} else if (label.equals(SWI_SoftKeyBase.LABEL_LANGUAGE)) {
			switchPage(SWI_PageBase.LANGUAGE);
		} else if (label.equals(SWI_SoftKeyBase.LABEL_NUMBERS)) {
			switchPage(SWI_PageBase.NUMBER);
		} else if (label.equals(SWI_SoftKeyBase.LABEL_CASE)) {
			mService.handleCaseKey();
		} else if (label.equals(SWI_SoftKeyBase.LABEL_COMMAND)) {
			mService.handleCommandKey();
		} else if (label.equals(SWI_SoftKeyBase.LABEL_ENTER)) {
			//mService.sendText("\n", 0);
			if (mService != null){
				mService.sendKeyChar('\n');
			}
			
		} else { 
			return false;
		}
		return true; 
	}
	
	public void switchPage(int type){
		removeAllViews();
		mService.switchPage(type);
	}
	
	@Override
	public void removeAllViews(){
		super.removeAllViews();
		mInputMethodLayout.removeAllViews();
	}
	
	public void setShowIdealShape(boolean show){
		mKeyboardView.setShowIdealShape(show);
	}
	
	
	public void CopyCommand(boolean bCut) {
		if (mService != null) {
			mService.copyCommand(bCut);
		}
	}
	
	public void PasteCommand(){
		if (mService != null) {
			mService.pasteCommand();
		}
	}
	
	public void SelectAllCommand(){
		if (mService != null) {
			mService.selectAllCommand();
		}
	}
	
	public void launchSettings(){
		if (mService != null) {
			mService.launchSettings();
		}
	}
	
	public void launchGame(){
		if (mService != null) {
			mService.launchGame();
		}
	}
	
	public void launchHelp(){
		if (mService != null) {
			mService.launchHelp();
		}
	}
	
	public void launchVideo(){
		if (mService != null) {
			mService.launchVideo();
		}
	}
	
	public void handleLiteralMode(){
		if (mService != null) {
			mService.handleLiteralMode();
		}
	}
	
	public void handleClose() {
		if (mService != null) {
			mService.requestHideSelf(0);
		}
	}
	
	public void onPress(){
		mService.onPress();
	}
	public void hidenminikeyboard(){
		mKeyboardView.hidenMiniKeyboard();
	}
	
	public void launchCommandPage(){
		mService.launchCommondPage();
	}
	
	public void StartBackspace() {
		if (!mIsBackSpaceStart) {
			mBackspaceTimer.start();
			mIsBackSpaceStart = true;
		}
	}

	public void StopBackspace() {
		if (mIsBackSpaceStart) {
			mBackspaceTimer.cancel();
			mIsBackSpaceStart = false;
		}
	}
	
	public class BackspaceTimer extends CountDownTimer{

		public BackspaceTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			receiveKeyboardText(null, false);
			mIsBackSpaceStart = false;
			StartBackspace();
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}
}
