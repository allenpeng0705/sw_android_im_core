package com.shapewriter.android.softkeyboard;




import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

public class SWI_AuiViewVer extends SWI_AuiView {

	private static int MAX_SHOW_TEXT_NUM = 4;
	private static float ARROW_SCALE = 1.4f;
	private static int MAX_ONE_WORD_LEGNTH;
	private static int EXTRA_MARGIN = 6; // Shrink the clip length
	private static String mTailText = "...";
	private static String mExtend = " ";
	
	private int mExtendLength;
	private int mTailLength;
	private int mFontHeight;
	private int mLastY;
	
	private Bitmap mClip;
	private Canvas mClipCanvas;
	
	public SWI_AuiViewVer(Context context, SWI_AuiBase auiBase) {
		super(context, auiBase);
		
		MAX_ONE_WORD_LEGNTH = auiBase.width * 9 / 10;
		
		mFontPaint.setTextAlign(Align.CENTER);
		mExtendLength = getTextWidth(mExtend);
		mTailLength = getTextWidth(mTailText);
		mFontHeight = auiBase.fontSize;
		
		// From SWI_AuiView
		mTop = auiBase.firstArrowTop + auiBase.firstArrowHeight +  EXTRA_MARGIN;
		mLeft = 0;
		mClipWidth = auiBase.width;
		mClipHeight = auiBase.secondArrowTop - mTop - EXTRA_MARGIN;
		
		mClip = Bitmap.createBitmap(mClipWidth, mClipHeight,
				Config.ARGB_8888);
		mClipCanvas = new Canvas(mClip);
		
		onBufferDraw();
	}
	
	@Override
	public void destroy(){
		if(mPage != null){
			mPage.recycle();
			mPage = null;
		}
		if(mClip != null){
			mClip.recycle();
			mClip = null;
		}
		mClipCanvas = null;
		super.destroy();
	}

	@Override
	public void show(){
		if (!mDragStatus && mDirection != 0) {
			int result = mCurClip + mDirection;
			result = result > 0 ? result : 0;
			result = result < mClipNum ? result : (mClipNum - 1);
			mCurClip = result;			
			mDirection = 0;
		}
		
		if (mCurClip == 0 || mCurClip == -1) {
			mCanGoFirst = false;
		} else {
			mCanGoFirst = true;
		}
		
		if (mCurClip == mClipNum - 1 || mCurClip ==  -1) {
			mCanGoSecond = false;
		} else {
			mCanGoSecond = true;
		}
		
		onBufferDraw();
		invalidate();
	}
	
		
	@Override
	protected void onBufferDraw() {
		super.onBufferDraw();
	
		mClip.eraseColor(0x00000000);

		if (mCandidatesNum != 0) {
			mCurScreenTextNum = mTextNumberForEachClip.get(mCurClip);
			calculateCurClipTextPos();
		}

		if (mDragStatus) {
			mClipCanvas.drawBitmap(mPage, 0, -1 * mClipHeight * mCurClip
					+ mOffset, null);
			mCanvas.drawBitmap(mClip, mLeft, mTop, null);
			return;
		}

		if (mCurSelectIndex != -1) {
			int top = mTop + mCurScreenCandidatePos.get(mCurSelectIndex) - mFontHeight
					- mExtendLength;

			mHighLightRect.set(mAuiBase.highLightBegin, top, mAuiBase
					.highLightEnd, top + mFontHeight * 3 / 2
					+ mExtendLength + mExtendLength);

			mCanvas.drawBitmap(mAuiBase.candidateHighLight, null,
					mHighLightRect, null);
		}

		if (mCandidatesNum != 0) {
			mClipCanvas.drawBitmap(mPage, 0, -1 * mClipHeight * mCurClip, null);
			mCanvas.drawBitmap(mClip, mLeft, mTop, null);
		}
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int y = (int)event.getY();
		int action = event.getAction();
		
		/**
		 * Press to Add New Word
		 */
		if(mShowAddNewWord){
			int start = 5;
			int end = mCandidateText.get(0).length() - 1;
			String text = mCandidateText.get(0).substring(start, end);
			mPageManager.addNewWord(text);
			clearCandidate();
			onBufferDraw();
			invalidate();
			return true;
		}
		
		if (action == MotionEvent.ACTION_DOWN) {
			mLastY = (int) event.getY();
		}
		
		if (action == MotionEvent.ACTION_MOVE) {
			if(y >= 0 && y <= mAuiBase.firstArrowHeight * ARROW_SCALE){
				mDragStatus = false;
				return true;
			}
			else if( y >= mAuiBase.height - 
					ARROW_SCALE * mAuiBase.secondArrowHeight){
				mDragStatus = false;
				return true;
			} 
			
			if (mCandidateText.size() != 0) {
				mDragStatus = true;
				handleDragEvent(event);
			}
		}
		
		if(action == MotionEvent.ACTION_UP){
			
			if (mCanGoFirst && mLastY >= 0
					&& mLastY <= mAuiBase.firstArrowHeight * ARROW_SCALE
					&& y >= 0
					&& y <= mAuiBase.firstArrowHeight * ARROW_SCALE) {
				goToBack();
				return true;
			} else if (mCanGoSecond
					&& mLastY >= mAuiBase.height - ARROW_SCALE
							* mAuiBase.secondArrowHeight
					&& y >= mAuiBase.height - ARROW_SCALE
							* mAuiBase.secondArrowHeight) {
				goToFront();
				
				return true;
			} else {
				mCurSelectIndex = -1;
				if (mDirection == 0) {
					for (int i = 0; i < mCurScreenTextNum; i++) {
						if (mLastY >= mTop + mCurScreenCandidatePos.get(i)
								- mFontHeight
								- mExtendLength
								&& mLastY <= mTop + mCurScreenCandidatePos.get(i)
										+ mExtendLength) {
							mCurSelectIndex = i;
							break;
						}
					}
				}
				
				if (mCurSelectIndex != -1) {
					mPageManager.receiveAuiText(mAuiBase.name, mCandidateText
							.get(mStartIndexForEachClip.get(mCurClip)
									+ mCurSelectIndex), false);
				}
	
				mDragStatus = false;
				show();
				}
			}
		return true;
	}
	
	@Override
	protected void handleDragEvent(MotionEvent event) {
		int y = (int) event.getY();
		mOffset = y - mLastY;

		if (mOffset > 0 && mOffset > mAuiBase.height / 4) { // drag
																// down
			mDirection = -1;
		} else if (mOffset < 0 && -mOffset > mAuiBase.height / 4) { // drag
																		// up
			mDirection = 1;
		}

		onBufferDraw();
		invalidate();
	}
	
	private int numberOfClips() {		
		mCandidateWidth.clear();
		mTextNumberForEachClip.clear();
		mPureTextLengthForEachClip.clear();
		mStartIndexForEachClip.clear();		
		
		int num = mCandidatesNum / MAX_SHOW_TEXT_NUM;
		for (int i = 0; i < num; i++) {
			mStartIndexForEachClip.add(i * MAX_SHOW_TEXT_NUM);
			mTextNumberForEachClip.add(MAX_SHOW_TEXT_NUM);
			mPureTextLengthForEachClip.add(MAX_SHOW_TEXT_NUM * mFontHeight);
		}

		int numberOfLastPage = mCandidatesNum % MAX_SHOW_TEXT_NUM;
		if (0 != numberOfLastPage) {
			num++;
			mStartIndexForEachClip.add((num - 1) * MAX_SHOW_TEXT_NUM);
			mTextNumberForEachClip.add(numberOfLastPage);
			mPureTextLengthForEachClip.add(numberOfLastPage * mFontHeight);
		}
		
		for (int i = 0; i < mCandidatesNum; i++) {
			mCandidateWidth.add(getTextWidth(mCandidateText.get(i)));
		}
		
		return num;
	}
	
	@Override
	protected void drawPage(Bitmap page) {
		Canvas canvas = new Canvas(page);
		int curClipBlank = 0;

		for (int i = 0; i < mClipNum; i++) {

			curClipBlank = (mClipHeight - mPureTextLengthForEachClip.get(i))
					/ (mTextNumberForEachClip.get(i) + 1);
			int offset = curClipBlank + mFontHeight;
			mCandidatePosForEachClip.clear();
			mCandidatePosForEachClip.add(offset);

			for (int j = 1; j < mTextNumberForEachClip.get(i); j++) {
				offset += mFontHeight + curClipBlank;
				mCandidatePosForEachClip.add(offset);
			}

			for (int k = 0; k < mTextNumberForEachClip.get(i); k++) {
				String text = mCandidateText.get(mStartIndexForEachClip
						.get(i)
						+ k);
				if (getTextWidth(text) > MAX_ONE_WORD_LEGNTH) {
					int end = getPositionInWidth(text,
							MAX_ONE_WORD_LEGNTH - mTailLength);
					text = text.substring(0, end) + mTailText;
				}
				if(mCandidateDistance.size()  > 1){
					if(k == 1 && i ==0 && ((mCandidateDistance.get(1)) / (mCandidateDistance.get(0))) > 1.2  &&  mCandidateType.get(1) == 2 ){
					mFontPaint.setColor(Color.GREEN);
					canvas.drawLine(mAuiBase.width / 2 -  getTextWidth(text) / 2 - 5, mCandidatePosForEachClip.get(k) + 5,mAuiBase.width / 2 +  getTextWidth(text) / 2 + 5,  mCandidatePosForEachClip.get(k) + 5, mFontPaint);
					canvas.drawText(text, mAuiBase.width / 2, mClipHeight * i
							+ mCandidatePosForEachClip.get(k), mFontPaint);
					  
					} 
					else{
						mFontPaint.setColor(Color.WHITE);
						canvas.drawText(text, mAuiBase.width / 2, mClipHeight * i
								+ mCandidatePosForEachClip.get(k), mFontPaint); 
					} 
				}
				
				else{
					mFontPaint.setColor(Color.WHITE);
					canvas.drawText(text, mAuiBase.width / 2, mClipHeight * i
							+ mCandidatePosForEachClip.get(k), mFontPaint);
				} 
			} 
		}
	}
	
	@Override
	public void initializeAuiLayout() {
		super.initializeAuiLayout();
		mCandidatesNum = mCandidateText.size();
		mCurClip = 0;
		mClipNum = numberOfClips();
		mPage = Bitmap.createBitmap(mClipWidth,
				mClipHeight * mClipNum, Bitmap.Config.ARGB_8888);
		drawPage(mPage);
		
		if (mClipNum > 1) {
			mCanGoSecond = true;
		}
	}
	
	private void calculateCurClipTextPos() {
		int curClipBlank = (mClipHeight - mPureTextLengthForEachClip
				.get(mCurClip))
				/ (mTextNumberForEachClip.get(mCurClip) + 1);
		int offset = curClipBlank + mFontHeight;
		mCurScreenCandidatePos.clear();
		mCurScreenCandidatePos.add(offset);

		for (int i = 1; i < mTextNumberForEachClip.get(mCurClip); i++) {
			offset += mFontHeight + curClipBlank;
			mCurScreenCandidatePos.add(offset);
		}
	}
}
