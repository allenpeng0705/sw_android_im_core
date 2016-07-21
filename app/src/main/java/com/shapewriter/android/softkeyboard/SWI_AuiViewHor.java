package com.shapewriter.android.softkeyboard;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

public class SWI_AuiViewHor extends SWI_AuiView {

	private static int MAX_SHOW_TEXT_NUM = 4;
	private static float ARROW_WIDTH_SCALE = 1.4f;
	private static int EXTRA_MARGIN = 6; // Shrink the clip length
	private static String mExtend = "    ";
	
	private int MAX_PURE_TEXT_LENGTH;
	private int MAX_ONE_WORD_LEGNTH;
	private int BASIC_LINE;
	private int mExtendLength;
	private int mLastX;
	
	private Bitmap mClip;
	private Canvas mClipCanvas;
	
	public SWI_AuiViewHor(Context context, SWI_AuiBase auiBase) {
		super(context, auiBase);
		MAX_PURE_TEXT_LENGTH = auiBase.width / 2;
		MAX_ONE_WORD_LEGNTH = auiBase.width - 2 * auiBase.firstArrowWidth
									       		 - 2 * auiBase.secondArrowWidth;
		
		mFontPaint.setTextAlign(Align.LEFT);
		//mFontPaint.setColor(Color.BLUE);
		BASIC_LINE = auiBase.basicLine;
		mExtendLength = getTextWidth(mExtend);
		
		// From SWI_AuiView
		mTop = 0;
		mLeft = auiBase.firstArrowWidth + EXTRA_MARGIN;
		mClipWidth = auiBase.width - auiBase.firstArrowWidth
				- auiBase.secondArrowWidth - EXTRA_MARGIN * 2;
		mClipHeight = auiBase.height;
		
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
		
		if (mCurClip == mClipNum - 1 || mCurClip == -1) {
			mCanGoSecond = false;
		} else {
			mCanGoSecond = true;
		}
		
		onBufferDraw();
		invalidate();
	}
	
	@Override
	public void clearCandidate(){
		super.clearCandidate();
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
			mClipCanvas.drawBitmap(mPage, -1 * mClipWidth * mCurClip + mOffset, 0,
					null);
			mCanvas.drawBitmap(mClip, mLeft, mTop, null);
			return;
		}
		

		if (mCurSelectIndex != -1) {
			int highLightExtend;
			int textLength = mCandidateText.get(
					mStartIndexForEachClip.get(mCurClip) + mCurSelectIndex)
					.length();
			highLightExtend = Math.max(6, 10 - textLength);

			mHighLightRect.set(mLeft + mCurScreenCandidatePos.get(mCurSelectIndex)
					- highLightExtend, mAuiBase.highLightBegin,
					mLeft + mCurScreenCandidatePos.get(mCurSelectIndex)
							+ mCurScreenCandidateWidth.get(mCurSelectIndex)
							+ highLightExtend, mAuiBase.highLightEnd);

			mCanvas.drawBitmap(mAuiBase.candidateHighLight, null,
					mHighLightRect, null);
		}
		
		if (mCandidatesNum != 0) {
			mClipCanvas.drawBitmap(mPage, -1 * mClipWidth * mCurClip, 0, null);
			mCanvas.drawBitmap(mClip, mLeft, mTop, null);
		}
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int)event.getX();
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
			mLastX = (int) event.getX();
			mOffset = 0;
		}

		if (action == MotionEvent.ACTION_MOVE) {
			if (x >= 0
					&& x <= mAuiBase.firstArrowWidth * ARROW_WIDTH_SCALE) {
				mDragStatus = false;
				return true;
			} else if (x >= mAuiBase.width - ARROW_WIDTH_SCALE
					* mAuiBase.secondArrowWidth) {
				mDragStatus = false;
				return true;
			}
			
			if (mCandidateText.size() != 0) {
				mDragStatus = true;
				handleDragEvent(event);
			}
		}

		if (action == MotionEvent.ACTION_UP) {

			if (mCanGoFirst
					&& mLastX >= 0
					&& mLastX <= mAuiBase.firstArrowWidth
							* ARROW_WIDTH_SCALE && x >= 0
					&& x <= mAuiBase.firstArrowWidth * ARROW_WIDTH_SCALE) {
				goToBack();
				return true;
			} else if (mCanGoSecond
					&& mLastX >= mAuiBase.width - ARROW_WIDTH_SCALE
							* mAuiBase.secondArrowWidth
					&& x >= mAuiBase.width - ARROW_WIDTH_SCALE
							* mAuiBase.secondArrowWidth) {
				goToFront();
				return true;
			} else {
				mCurSelectIndex = -1;
				if (mDirection == 0) {
					for (int i = 0; i < mCurScreenTextNum; i++) {
						if (mLastX >= mLeft + mCurScreenCandidatePos.get(i)
								- mExtendLength
								&& mLastX <= mLeft + mCurScreenCandidatePos.get(i)
										+ mCurScreenCandidateWidth.get(i)
										+ mExtendLength) {
							mCurSelectIndex = i;
							break;
						}
					}
				}
				
				if (mCurSelectIndex != -1) {
					mPageManager.receiveAuiText(mAuiBase.name,
							mCandidateText.get(mStartIndexForEachClip
									.get(mCurClip)
									+ mCurSelectIndex), false);
				}
			}

			mDragStatus = false;
			show();
		}

		return true;
	}
	
	@Override
	protected void handleDragEvent(MotionEvent event) {
		int x = (int) event.getX();
		mOffset = x - mLastX;

		if (mOffset > 0 && mOffset > mAuiBase.width / 4) { // drag
																// rightward
			mDirection = -1;
		} else if (mOffset < 0 && -mOffset > mAuiBase.width / 4) { // drag
																		// leftward
			mDirection = 1;
		}

		onBufferDraw();
		invalidate();
	}

	private int numberOfClips() {
		int num = 0;
		int curClipPureTextLength = 0;
		int curStartIndex = 0;
		mCandidateWidth.clear();
		mTextNumberForEachClip.clear();
		mPureTextLengthForEachClip.clear();
		mStartIndexForEachClip.clear();
		mStartIndexForEachClip.add(curStartIndex);		
		mCandidatesNum = mCandidateText.size();

		for (int i = 0; i < mCandidatesNum; i++) {
			int width = getTextWidth(mCandidateText.get(i));
			mCandidateWidth.add(width);
			curClipPureTextLength += width;

			if (curClipPureTextLength > MAX_PURE_TEXT_LENGTH
					|| (i - curStartIndex + 1) == MAX_SHOW_TEXT_NUM) {
				num++;

				if (i - curStartIndex == 0) { // one word one clip
					mPureTextLengthForEachClip.add(curClipPureTextLength);
					curClipPureTextLength = 0;
					mTextNumberForEachClip.add(1);
					curStartIndex = i + 1;
				} else if (i - curStartIndex == 1) {
					mPureTextLengthForEachClip.add(curClipPureTextLength
							- width);
					curClipPureTextLength = width;
					mTextNumberForEachClip.add(i - curStartIndex);
					curStartIndex = i;
				} else if (curClipPureTextLength > MAX_PURE_TEXT_LENGTH
						&& (i - curStartIndex + 1) <= MAX_SHOW_TEXT_NUM) { // 
					mPureTextLengthForEachClip.add(curClipPureTextLength
							- width);
					curClipPureTextLength = width;
					mTextNumberForEachClip.add(i - curStartIndex);
					curStartIndex = i;
				} else {
					mPureTextLengthForEachClip.add(curClipPureTextLength);
					curClipPureTextLength = 0;
					mTextNumberForEachClip.add(i - curStartIndex + 1);
					curStartIndex = i + 1;
				}

				mStartIndexForEachClip.add(curStartIndex);
			}
		}
		
		// The last page is littler than MAX_SHOW_TEXT_NUM
		if ((mCandidatesNum - curStartIndex) % MAX_SHOW_TEXT_NUM != 0) {
			num++;
			mTextNumberForEachClip.add(mCandidatesNum - curStartIndex);
			mPureTextLengthForEachClip.add(curClipPureTextLength);
		} else {
			mStartIndexForEachClip.remove(num);
		}
		
		return num;
	}
	
	@Override
	protected void drawPage(Bitmap page){
		Canvas canvas = new Canvas(page);
		int curClipBlank = 0;
		String tail = "...";
		int posX;
		for (int i = 0; i < mClipNum; i++) {
			curClipBlank = (mClipWidth - mPureTextLengthForEachClip.get(i))
					/ (mTextNumberForEachClip.get(i) + 1);
			int offset = curClipBlank;
			mCandidatePosForEachClip.clear();
			mCandidatePosForEachClip.add(offset);

			for(int j = 1; j < mTextNumberForEachClip.get(i); j++){
				offset += mCandidateWidth.get(mStartIndexForEachClip.get(i) + j
						- 1)
						+ curClipBlank;
				mCandidatePosForEachClip.add(offset);
			}
			
			for (int k = 0; k < mTextNumberForEachClip.get(i); k++) {
				String text = mCandidateText.get(mStartIndexForEachClip
						.get(i)
						+ k);
				
				if (getTextWidth(text) > MAX_ONE_WORD_LEGNTH) {
					int end = getPositionInWidth(text, MAX_ONE_WORD_LEGNTH
							- tail.length());
					text = text.substring(0, end) + tail;
					posX = (mClipWidth - getTextWidth(text))/2;
				} else {
					posX = mClipWidth * i + mCandidatePosForEachClip.get(k);
				}
			//	Log.e("distance:",""+mCandidateDistance.get(1));
			//	Log.e("tpye:",""+type);
				if(mCandidateDistance.size()  > 1){
					if(k == 1 && i ==0 && ((mCandidateDistance.get(1)) / (mCandidateDistance.get(0))) > 1.2  &&  mCandidateType.get(1) == 2 ){
					
					mFontPaint.setColor(Color.GREEN);
					canvas.drawLine(posX -10, mClipHeight - 5, posX + getTextWidth(text) + 10,  mClipHeight - 5, mFontPaint);
					canvas.drawText(text, posX, BASIC_LINE,
							mFontPaint);				
					} 
					else{
						mFontPaint.setColor(Color.WHITE); 
						canvas.drawText(text, posX, BASIC_LINE,
							mFontPaint);
					}
				}
								 
				else{
					mFontPaint.setColor(Color.WHITE); 
					canvas.drawText(text, posX, BASIC_LINE,
						mFontPaint);
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
		mPage = Bitmap.createBitmap(mClipWidth * mClipNum,
				mClipHeight, Bitmap.Config.ARGB_8888);
		drawPage(mPage);
		
		if (mClipNum > 1) {
			mCanGoSecond = true;
		}
	}
	
	private void calculateCurClipTextPos() {
		int curClipBlank = (mClipWidth - mPureTextLengthForEachClip
				.get(mCurClip))
				/ (mTextNumberForEachClip.get(mCurClip) + 1);
		int offset = curClipBlank;
		mCurScreenCandidatePos.clear();
		mCurScreenCandidatePos.add(offset);
		mCurScreenCandidateWidth.clear();

		int width;
		for (int i = 1; i < mTextNumberForEachClip.get(mCurClip); i++) {
			width = mCandidateWidth.get(mStartIndexForEachClip.get(mCurClip)
					+ i - 1);
			offset += width + curClipBlank;
			mCurScreenCandidatePos.add(offset);
			mCurScreenCandidateWidth.add(width);
		}
		width = mCandidateWidth.get(mStartIndexForEachClip.get(mCurClip)
				+ mTextNumberForEachClip.get(mCurClip) - 1);
		mCurScreenCandidateWidth.add(width);
	}
}
