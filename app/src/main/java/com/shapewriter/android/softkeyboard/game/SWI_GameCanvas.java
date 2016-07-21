/* GameCanvas.java
 * Created on Apr 18, 2005
 *
 * Copyright (c) 2005 Per-Ola Kristensson
 * Per-Ola Kristensson <perkr@ida.liu.se>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.io.InputStream;
import android.util.AttributeSet;
import android.view.View;
import android.content.Context;


// import com.shapewriter.android.keyboard.Keyboard;

/**
 * 
 * @author pkriste
 */
public abstract class SWI_GameCanvas extends View {

	private Callback callback = null;
	protected Context context = null;
	public int mHeight;

	public SWI_GameCanvas(Context context) {
		super(context);
		// setOpaque(true);
	}

	public SWI_GameCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public abstract void userInput(SWI_UserInput userInput);

	public abstract void activated(boolean enableAllFeatures);

	public abstract void deactivated();

	public InputStream getGameLexiconInputStream() {
		if (callback == null)
			return null;
		return callback.sendGameLexiconInputStream();
	}

	public final void setCallback(Callback callback) {
		this.callback = callback;
	}

	public final void sendDeactivateSignal() {
		callback.deactivateGameCanvas();
		//switch to normal text edit view		
//		((Activity)context).finish();
	}

	public static interface Callback {
		void deactivateGameCanvas();
		InputStream sendGameLexiconInputStream();
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mHeight);
	}
}
