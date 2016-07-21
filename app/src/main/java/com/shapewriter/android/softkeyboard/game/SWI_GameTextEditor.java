package com.shapewriter.android.softkeyboard.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import android.widget.EditText;

public class SWI_GameTextEditor extends EditText {
	private SWI_BalloonGameView mGameView = null;

	public SWI_GameTextEditor(Context context) {
		super(context);
	}

	public SWI_GameTextEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Draw the view content
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	public void setGameView(SWI_BalloonGameView gameView) {
		this.mGameView = gameView;
	}
	
	@Override
	public void onTextChanged(CharSequence text, int start, int before, int after) {
		super.onTextChanged(text, start, before, after);
		SendText(text.toString(), false, 0);
		selectAll();
	}

	public int SendText(String str, boolean bInsert, long traceTimeMillis) {
		if (str == null) {
			return -1;
		}

		String word = str.trim().toLowerCase();
		SWI_UserInput input = new SWI_UserInput(null, word, null,
				traceTimeMillis);

		if (mGameView != null)
			mGameView.userInput(input);
		return -1;
	}
}