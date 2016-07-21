package com.shapewriter.android.softkeyboard;


import com.shapewriter.android.softkeyboard.SWI_PopKeyboard.SWI_PopKeyboardLayout;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class SWI_SoftKeyView extends View{
	private Context mContext;
	private SWI_SoftKeyBase mSoftKeyBase;
	private int mLocation;
	protected SWI_MiniKeyPopupWindow mPreviewPopup;
	private Bitmap mBackground;
	private Bitmap mCLose;
	private Canvas mCanvas;
	private Bitmap mPopKeyBitmap;
	private boolean mHighLight = false;
	private Paint mPainter;
	private Rect mRect;
	private  boolean mIsClose = false;
	private float mKeyX;
	private boolean mMove;
	public SWI_PopKeyboard popkeys;   
	private  int KEYWIDTH;  
	private View mParent;
	public SWI_PopKeyboardLayout layout;   
	public SWI_SoftKeyView(Context context, SWI_SoftKeyBase keyBase, int location, Bitmap background,Bitmap close,
			SWI_MiniKeyPopupWindow keyPopupWindow,View parent) {
		super(context);
		mSoftKeyBase = keyBase;
		mLocation = location;
		mContext = context;
		mBackground = background;
		mCLose = close;
		mPreviewPopup = keyPopupWindow;
		mPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
		mRect = new Rect();
		mParent = parent;
		KEYWIDTH = mSoftKeyBase.width;
		mPreviewPopup.setkeywidth(KEYWIDTH);		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mCanvas = canvas;
		mRect.set(0, 0, mSoftKeyBase.width, mSoftKeyBase.height);     
		mPainter.setTextAlign(Paint.Align.CENTER);
		mPainter.setColor(Color.WHITE);
		mPainter.setTextSize(20);
		canvas.drawRect(mRect, mPainter);
		if(mSoftKeyBase.valueList.size() != mLocation){
			canvas.drawBitmap(mBackground, 0,0, null);
		}
		if(true == mHighLight)
		{
			mPopKeyBitmap =  BitmapFactory.decodeResource(mContext.getResources(), R.raw.qwerty_2_hilite);
			mCanvas.drawBitmap(mPopKeyBitmap, 0,0, null);
			if (mPreviewPopup.isShowing()) {
				mPreviewPopup.dismiss();
			}
			if(	! mPreviewPopup.isShowing() ){
				mPreviewPopup.show();
				
			}
			else{
				mPreviewPopup.update();
			}
		}
		if(mSoftKeyBase.valueList.size() == mLocation){
			canvas.drawBitmap(mCLose, 0,0, null);
		}
		else{
			canvas.drawText(mSoftKeyBase.valueList.get(mLocation), mSoftKeyBase.width/2 ,  mSoftKeyBase.height/2, mPainter);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(mSoftKeyBase.valueList.size() == mLocation){
			setMeasuredDimension(mCLose.getWidth(), mCLose.getHeight());   //50*49
		}
		else{
			setMeasuredDimension(mBackground.getWidth(), mBackground.getHeight());   //32*49
		}
		
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Log.e("action","aaaa");
	//	super.onTouchEvent(event);
		int action = event.getAction();
		/*                                         */
		/*     Down    event                       */
		/*                                         */
		if (action == MotionEvent.ACTION_DOWN){
			mMove = false;
			mIsClose = false;
			if((mSoftKeyBase.valueList.size() != mLocation)){
				mHighLight  = true;
				invalidate();
				mPreviewPopup.setvalueposition(mLocation);
				float orignalX = event.getRawX() - KEYWIDTH * (mLocation-2) - event.getX();
				mPreviewPopup.setoriginalposition(orignalX);
				mPreviewPopup.setProperty(mParent,mSoftKeyBase);
				if(	! mPreviewPopup.isShowing() ){
					mPreviewPopup.show();
				}
				else{
					mPreviewPopup.update();
				}
			}
			else{
				mIsClose  =true;
			}
			
			
		}
		/*                                         */
		/*     up      event                       */
		/*                                         */
		else if(action == MotionEvent.ACTION_UP){
			mPreviewPopup.dismiss();
			float sendlocation= (mKeyX - KEYWIDTH*(mLocation - 2))/KEYWIDTH;
			/*     If the close button be activated    */
			/*     One situation is tap mode           */
			/*     The other situation is move mode    */
			if((mSoftKeyBase.valueList.size() == mLocation )){
				notifyupdate();
				return true;
			}
			else if((mSoftKeyBase.valueList.size() == (mLocation + (int)sendlocation)) && (mMove)){
				notifyupdate();
				return true;
			}
			/*     For send to the text box                          */
			/*     First situation is for front choose               */
			/*     Second situation is for back choose               */
			/*     Third  situation is for the exactly down word     */
			if((mKeyX > KEYWIDTH*(mLocation - 1) ) && (mMove)){
				if((mLocation + (int)sendlocation) > (mSoftKeyBase.valueList.size() - 1)){
					sendtext(mSoftKeyBase.label,mSoftKeyBase.valueList.get((mSoftKeyBase.valueList.size() - 1)));
					notifyupdate();
					return false;
				}
				sendtext(mSoftKeyBase.label,mSoftKeyBase.valueList.get(mLocation + (int)sendlocation));
			}
			else if((mKeyX < KEYWIDTH*(mLocation - 2))&& (mMove)){
				if(mKeyX < 0){
					sendtext(mSoftKeyBase.label,mSoftKeyBase.valueList.get(2));
					notifyupdate();
					return true;
				}
				sendtext(mSoftKeyBase.label,mSoftKeyBase.valueList.get(mLocation + (int)sendlocation- 1) );
				notifyupdate();
				return false;
				
			}
			else{
				sendtext(mSoftKeyBase.label,mSoftKeyBase.valueList.get(mLocation));
			}
			mHighLight  = false;
			mPreviewPopup.dismiss();
			invalidate();
			notifyupdate();
			
		}
		/*                                         */
		/*     Move    event                       */
		/*                                         */
		else if(action == MotionEvent.ACTION_MOVE){
			if(mIsClose == false){
				float x  = event.getX();       
			mKeyX = x + (mLocation - 2)*KEYWIDTH;
			mMove = true;
			float realposition = event.getRawX();
			float orignalX = event.getRawX() - KEYWIDTH * (mLocation-2) - event.getX();
			float endX = orignalX +( mSoftKeyBase.valueList.size() - 2) *KEYWIDTH;
			if((realposition < orignalX) ||(realposition >= endX)){
				return false;
			}
			changetouchevent((int)(mKeyX/KEYWIDTH+2));
			}
			
		}
		return true;
	}
	
	public void attach(SWI_PopKeyboard popkeyboard){
		popkeys = popkeyboard;
	}
	
	public void notifyupdate(){
		popkeys.update();
	}
	
	public void sendtext(String label,String value){
		popkeys.sendtext(label,value);
	}
	
	public void attach(SWI_PopKeyboardLayout poplayout){
		layout = poplayout;
		
	}
	
	public void changetouchevent(int currentposition){ 
		layout.changetouchevent(currentposition);
	}
	
	public void resethight(boolean highlight){
		mHighLight = highlight;
		invalidate();
	}
	
	public void setcurrentminikey(int location){
		mPreviewPopup.setvalueposition(location);
	}
	
	public void resetparthighlight(int position){
		mHighLight = false;
		invalidate();
	}
}
