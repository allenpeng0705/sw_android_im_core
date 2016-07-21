package com.shapewriter.android.softkeyboard;



import java.util.ArrayList;
import java.util.Stack;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class SWI_AuiView extends View{
	
	protected static final int FADE_UPDATE_INTERVAL = 50;
	
	protected boolean mDragStatus = false;
	protected boolean mCanGoFirst;
	protected boolean mCanGoSecond;
	protected boolean mShowAddNewWord = false;
	protected boolean mMessageDisplayMode = false;
	protected boolean mFadeMessage = false;
	protected boolean mShowLogo = true;
	protected int mCurClip = -1;
	protected int mClipNum;
	protected int mCandidatesNum = 0;
	protected int mClipWidth;
	protected int mClipHeight = 0;
	protected int mOffset;
	protected int mDirection = 0;
	protected int mCurSelectIndex = -1;
	protected int mCurIndexStart = 0;
	protected int mCurIndexEnd;
	protected int mCurScreenTextNum;
	protected int mTop;
	protected int mLeft;
	protected float mCurrentMessageAlpha = 1.0f;
	
	protected SWI_AuiBase mAuiBase;
	protected SWI_PageManager mPageManager;
	protected Paint mFontPaint;
	protected Rect mHighLightRect;
	protected String mMessage = null;
	protected Handler mHandler;
	protected Bitmap mPage;
	
	protected Canvas mCanvas;
	protected ArrayList <String> mCandidateText;
	protected Stack<Integer> mStartIndexStack;
	protected ArrayList<Integer> mCurScreenCandidateWidth;
	protected ArrayList<Integer> mCurScreenCandidatePos;
	protected ArrayList<Integer> mCandidateWidth;
	protected ArrayList<Integer> mTextNumberForEachClip;
	protected ArrayList<Integer> mPureTextLengthForEachClip;
	protected ArrayList<Integer> mCandidatePosForEachClip;
	protected ArrayList<Integer> mStartIndexForEachClip;
	
	protected ArrayList<Double> mCandidateDistance;
	protected ArrayList<Integer> mCandidateType;
	private Bitmap mBuffer;
	
	/**
	 * Sub Class Should Override the Following Methods
	 */
	public void show(){};	
	public void initializeAuiLayout() {
		mMessageDisplayMode = false;
		mFadeMessage = false;
	};
	
	protected void handleDragEvent(MotionEvent event){};
	protected void drawPage(Bitmap page) {};
	
	public SWI_AuiView(Context context, SWI_AuiBase auiBase) {
		super(context);
	
		mAuiBase = auiBase;
		mBuffer = Bitmap.createBitmap(auiBase.width, auiBase.height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBuffer);
        mStartIndexStack = new Stack<Integer>();
        mHandler = new Handler();
        mCandidateText = new ArrayList<String>();
        
        mCurScreenCandidatePos = new ArrayList<Integer>();		
		mCandidateWidth = new ArrayList<Integer>();
		mTextNumberForEachClip = new ArrayList<Integer>();
		mPureTextLengthForEachClip = new ArrayList<Integer>();
		mCandidatePosForEachClip = new ArrayList<Integer>();
		mStartIndexForEachClip = new ArrayList<Integer>();
		mCurScreenCandidateWidth = new ArrayList<Integer>();
		mCandidateDistance = new ArrayList<Double>();
		mCandidateType = new ArrayList<Integer>();
        mFontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFontPaint.setARGB(255, 255, 255, 255);
		mFontPaint.setTextSize(auiBase.fontSize);
		
		mHighLightRect = new Rect();
	}
	
	public void destroy(){
		if(mBuffer != null)		mBuffer.recycle();
		
		if(mCandidateText != null)				mCandidateText.clear();
		if(mStartIndexStack != null)			mStartIndexStack.clear();
		if(mCurScreenCandidateWidth != null)	mCurScreenCandidateWidth.clear();
		if(mCurScreenCandidatePos != null)		mCurScreenCandidatePos.clear();
		if(mCandidateWidth != null)				mCandidateWidth.clear();
		if(mTextNumberForEachClip != null)		mTextNumberForEachClip.clear();
		if(mPureTextLengthForEachClip != null)	mPureTextLengthForEachClip.clear();
		if(mCandidatePosForEachClip != null)	mCandidatePosForEachClip.clear();
		if(mStartIndexForEachClip != null)		mStartIndexForEachClip.clear();
		if(mCandidateDistance != null)          mCandidateDistance.clear();
		if(mCandidateType != null)              mCandidateType.clear();
		mAuiBase = null;
		mPageManager = null;
		mFontPaint = null;
		mMessage = null;
		mHandler = null;
		mCanvas = null;
		mCandidateText = null;
		mStartIndexStack = null;
		mCurScreenCandidateWidth = null;
		mCurScreenCandidatePos = null;
		mCandidateWidth = null;
		mTextNumberForEachClip = null;
		mPureTextLengthForEachClip = null;
		mCandidatePosForEachClip = null;
		mStartIndexForEachClip = null;
		mCandidateDistance = null;
		mCandidateType = null;
		mBuffer = null;
		mHighLightRect = null;
	}
	
	public void setPageManager(SWI_PageManager pageManager){
		mPageManager = pageManager;
	}
	
	public void addCandidate(String candidate){
		String ret = SWI_MisspellingTable.instance().fromErrorToCorrect(candidate);
		mCandidateText.add(ret);
	}
	
	public void addDistanceAndTpye(double distance,int type){
		mCandidateDistance.add(distance);
		mCandidateType.add(type);
	}
	
	public void addCandidate(ArrayList<String> arrayList) {
		if (arrayList == null || arrayList.size() == 0)
			return;

		for (int i = 0; i < arrayList.size(); i++) {
			mCandidateText.add(arrayList.get(i));
		}
	}
	
	public void setCurSelectIndex(int index){
		mCurSelectIndex = index;
	}
	
	public void showLogo(boolean show){
		mShowLogo = show;
	}
	
	public void clearCandidate(){
		mCandidatesNum = 0;
		mCandidateText.clear();
		mCanGoFirst = false;
		mCanGoSecond = false;
		mCurSelectIndex = -1;
		mCurIndexStart = 0;
		mCurScreenTextNum = 0;
		mStartIndexStack.clear();
		mShowAddNewWord = false;
		clearAuiLayout();
	}
	
	public void clearAuiLayout() {
		mPage = null;
		mCurClip = -1;
		mDirection = 0;
		if (mPage != null)
			mPage.eraseColor(0x00000000);
		mCurScreenCandidateWidth.clear();
		mCurScreenCandidatePos.clear();
		mCandidateWidth.clear();
		mTextNumberForEachClip.clear();
		mPureTextLengthForEachClip.clear();
		mCandidatePosForEachClip.clear();
		mStartIndexForEachClip.clear();
		mCandidateDistance.clear();
		mCandidateType.clear();
	}
	
	protected void goToFront(){
		mCurSelectIndex = -1;
		mCurClip++;
		show();
	}
	
	protected void goToBack(){
		mCurSelectIndex = -1;
		mCurClip--;
		show();
	}
	
	
	public void sendFirstCandidate(){
		mPageManager.receiveAuiText(mAuiBase.name, mCandidateText.get(0), true);
		mCurIndexStart = 0;
		mCurSelectIndex = 0;
	}
	
	
	public void showAddNewWord(){
		mShowAddNewWord = true;
	}
	
	public void showMessage(String message, boolean fade) {
		if (message == null) {
			return;
		}
		clearCandidate();
		mMessageDisplayMode = true;
		mMessage = message;
		mCurrentMessageAlpha = 1.0f;
		mFadeMessage = fade;
		if (fade) {
			mHandler.postDelayed(mFadeAwayMessageAction, FADE_UPDATE_INTERVAL);
		}
		onBufferDraw();
		invalidate();
	}
	
	public void hideMessage(){
		mMessageDisplayMode = false;
		mFadeMessage = false;
		onBufferDraw();
		postInvalidate();
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mAuiBase.width, mAuiBase.height);
	}
	
	protected void onBufferDraw(){
		mCanvas.drawBitmap(mAuiBase.background, 0, 0, null);
		
		if(mCanGoFirst)
			mCanvas.drawBitmap(mAuiBase.firstArrowSelect,mAuiBase.firstArrowLeft, 
					mAuiBase.firstArrowTop, null);
		else
			mCanvas.drawBitmap(mAuiBase.firstArrowUnSelect, mAuiBase.firstArrowLeft, 
					mAuiBase.firstArrowTop, null);
		
		if(mCanGoSecond)
			mCanvas.drawBitmap(mAuiBase.secondArrowSelect,mAuiBase.secondArrowLeft, 
					mAuiBase.secondArrowTop, null);
		else
			mCanvas.drawBitmap(mAuiBase.secondArrowUnSelect,mAuiBase.secondArrowLeft, 
					mAuiBase.secondArrowTop, null);
		
		if (mMessageDisplayMode) {
			renderMessage(mCanvas);
			return;
		}
		
		if(mShowLogo && mCandidateText.size() == 0){
			mCanvas.drawBitmap(mAuiBase.logo, mAuiBase.logoLeft, mAuiBase.logoTop, null);
			mShowLogo = false;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBuffer, 0, 0, null);
	}
	
	
	
	protected int getTextWidth(String text) {

		float[] width = new float[text.length()];
		
		mFontPaint.getTextWidths(text, width);
		int selectionWidth = 0;
		for (int i = 0; i < text.length(); ++i) {
			selectionWidth += (int) width[i];
		}
		return selectionWidth;
	}
	
	protected int getPositionInWidth(String text, int maxLength){
		float[] width = new float[text.length()];
		mFontPaint.getTextWidths(text, width);
		int selectionWidth = 0;
		for (int i = 0; i < text.length(); ++i) {
			selectionWidth += (int) width[i];
			if(selectionWidth > maxLength)
				return i - 1;
		}
		return text.length() - 1;
	}
		
	private Runnable mFadeAwayMessageAction = new Runnable() {
		@Override
		public void run() {
			if (!mFadeMessage) {
				return;
			}
			if (mCurrentMessageAlpha > 0.05f) {
				mCurrentMessageAlpha *= 0.90f;
				onBufferDraw();
				invalidate();
				mHandler.postDelayed(mFadeAwayMessageAction, FADE_UPDATE_INTERVAL);
			}
		}
	};
	
	private void renderMessage(Canvas canvas) {
		if (!mMessageDisplayMode ) {
			return;
		}
		
		int w = mAuiBase.width;
		int h = mAuiBase.height;
		Paint cmdPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		cmdPaint.setARGB((int) (mCurrentMessageAlpha * 255), 255, 255,
				mFadeMessage ? 0 : 255);
		cmdPaint.setTextSize(mAuiBase.fontSize);
		cmdPaint.setTextAlign(Paint.Align.CENTER);
		Rect textBounds = new Rect();
		cmdPaint.getTextBounds("W", 0, 1, textBounds);
		canvas.drawText(mMessage, w / 2 , h / 2	+ (int) (textBounds.height() * 0.5d), cmdPaint);		
	}
	
}
