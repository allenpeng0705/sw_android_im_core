package com.shapewriter.android.softkeyboard;





import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.HashMap;


public class SWI_PopKeyboard extends PopupWindow {
	private SWI_PopKeyboardLayout  mPopKeyboardLayout;
	private View mParent;
	private Context mContext;
	private SWI_SoftKeyBase mCurKey;
	private SWI_MiniKeyPopupWindow mKeyPopupWindow;
	private HashMap<String, SWI_PopKeyboardLayout> mMultiKeyList;
	public SWI_KeyboardView keyviews;   
	public SWI_PopKeyboard(Context context, View parent){
		super(context);
		mParent = parent;
		mContext = context;
		mKeyPopupWindow = new SWI_MiniKeyPopupWindow(context);
		mMultiKeyList = new HashMap<String, SWI_PopKeyboardLayout>();	
		setBackgroundDrawable(null);	
	}
	
	
	public void showPopKeyboard(SWI_SoftKeyBase key){
		mCurKey = key;
		if(mMultiKeyList.containsKey(mCurKey.label)){
			mPopKeyboardLayout = mMultiKeyList.get(mCurKey.label);
			int length = mCurKey.valueList.size();
			mPopKeyboardLayout.resethighlight(false, length);
		}
		else{
			mPopKeyboardLayout = new SWI_PopKeyboardLayout(mContext, mCurKey,mParent);
			mKeyPopupWindow.setContentView(mPopKeyboardLayout);
			mMultiKeyList.put(mCurKey.label,mPopKeyboardLayout);
		
		}
		
		setContentView(mPopKeyboardLayout);
		setWidth(key.width * key.valueList.size());
		setHeight(key.height);
		int width = Math.max(key.width, 50);
		
		int x = keyviews.getLeft() + key.left - (width - key.width) / 2;
		int y = keyviews.getHeight() - key.top;
		showAtLocation(mParent, Gravity.LEFT | Gravity.BOTTOM, x, y - 20);
	}
	
	@Override
	public void update(){
		dismiss();
		notifyupdate();
	}
	public void attach(SWI_KeyboardView keyboardview){
		keyviews = keyboardview;
	}
	public void notifyupdate(){
		
		keyviews.resetMiniKeyboard();

	}
	public void sendtext(String label,String value){
		keyviews.sendtext(label, value);
	}
	
	
class SWI_PopKeyboardLayout extends LinearLayout {
	private SWI_SoftKeyView [] mViews;
	private Context mContext;
	private Bitmap mPopKeyBitmap;
	private Bitmap mCloseKeyBitmap;
	private int mPrePositon;
	
	public SWI_PopKeyboardLayout(Context context, SWI_SoftKeyBase softKey,View parent) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		setOrientation(LinearLayout.HORIZONTAL);
		mPopKeyBitmap =  BitmapFactory.decodeResource(context.getResources(), R.drawable.popkey);
		mCloseKeyBitmap =  BitmapFactory.decodeResource(context.getResources(), R.drawable.close);
		int size = softKey.valueList.size();
		mViews = new SWI_SoftKeyView[size - 1];
		for(int i = 0; i < size - 1;i++){
			 mViews[i]	= new SWI_SoftKeyView(mContext, softKey, i + 2, mPopKeyBitmap, mCloseKeyBitmap,mKeyPopupWindow,parent);
			 addView(mViews[i]);
			 mViews[i].attach(SWI_PopKeyboard.this);
			 mViews[i].attach(this);
		}
		
	}
	public void changetouchevent(int currentposition){
		if(currentposition != mPrePositon){
			int length = mCurKey.valueList.size();
			mViews[currentposition - 2].resethight(true);
			resetparthighlight(currentposition - 2,length);
			mViews[currentposition - 2].setcurrentminikey(currentposition);
			mPrePositon = currentposition;
		}
		
	}
	public void resethighlight(boolean hightlight,int keylength){
		for(int i = 0;i < keylength - 1;i++){
			mViews[i].resethight(false);
		}
	}
	public void resetparthighlight(int position,int keylength){
		for(int i = 0;i < keylength - 1;i++){
			if(i == position){
				continue;
			}
			mViews[i].resethight(false);
		}
	}

}
}