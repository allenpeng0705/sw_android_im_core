/* Text.java
 * Created on Jun 3, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.util.Log;

/**
 * 
 * @author pkriste
 */
public class SWI_Text implements SWI_SceneObject, SWI_BalloonGameView.TouchEventListener {

	private String text;
	private float x, y;
//	private boolean centerH, centerV;
	// private Font font = new Font("SanSerif", Font.BOLD, 22);
	private Rect textBounds = new Rect();
//	private boolean dynamic = false;
	private List<SWI_TextListener> textListeners = new LinkedList<SWI_TextListener>();
	// private boolean entered = false;
	// private boolean exited = false;
	private int animationStep = 0;
	private int animationSteps = 5;
	private boolean touched = false;

	public static final int PRACTICE_GAME = 0x00010000;
	public static final int EXIT_GAME = 0x00010001;
	public static final int OPTION = 0x00010002;
	public static final int EXIT = 0x00010003;
	public static final int BALLOON_NUM = 0x00010004;
	public static final int PERCENT = 0x00010005;

	private int initialFontSize = 25;

	private int type;

	public SWI_Text(String text, float x, float y, int type, boolean centerH,
			boolean centerV) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.type = type;
//		this.centerH = centerH;
//		this.centerV = centerV;

		mPaint.setColor(Color.argb(150, 50, 50, 200));

		if (type == EXIT || type == OPTION) {
			initialFontSize = 25;
		} else if (type == PRACTICE_GAME || type == EXIT_GAME) {
			initialFontSize = 30;
		}

		mPaint.setAntiAlias(true);
		// mPaint.setStrokeWidth(5);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setTextSize(initialFontSize);
		mPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));
	}

	void addTextListener(SWI_TextListener textListener) {
		textListeners.add(textListener);
	}

	void removeTextListener(SWI_TextListener textListener) {
		textListeners.remove(textListener);
	}

	// void setFont(Font font) {
	// this.font = font;
	// }

	private Paint mPaint = new Paint();

	private void re_init(){
		touched = false;
		animationStep = 0;
		mPaint.setTextSize(initialFontSize);
	}
	
	public void paint(Canvas canvas, Rect sceneBounds) {
		if (touched && (animationStep < animationSteps)) {
			animationStep++;
			mPaint.setTextSize(initialFontSize + animationStep * 2);
		} else if (touched /*&& type != OPTION*/) {
			for (Iterator<SWI_TextListener> i = textListeners.iterator(); i
					.hasNext();) {
				i.next().textClicked();
			}
			re_init();
		}

		mPaint.getTextBounds(text, 0, text.length(), textBounds);
		float w = mPaint.measureText(text, 0, text.length());

		float xPos = x - w / 2;
		float yPos = y - textBounds.height() / 2;

		canvas.drawText(text, xPos, yPos, mPaint);

		textBounds.offset((int) xPos, (int) yPos);
	}

	Rect getTextBounds() {
		return textBounds;
	}

	void setText(String text) {
		this.text = text;
	}

//	void setDynamic(boolean dynamic) {
//		this.dynamic = dynamic;
//	}

	void setX(float x) {
		this.x = x;
	}

	void setY(float y) {
		this.y = y;
	}

	private boolean isHit(int x, int y) {
		int xPos = textBounds.left - 10;
		int yPos = textBounds.top - 5;
		int width = textBounds.width() + 20;
		int height = textBounds.height() + 10;
		
		return x >= xPos && x <= xPos + width && y >= yPos && y <= yPos + height;		
	}

	public void touchEvent(MotionEvent e) {
//		Log.i(text, "TouchEvent is activated");

		if (isHit((int) e.getX(), (int) e.getY())) {
			touched = true;
		} else {
			re_init();
		}
	}

	// public void mouseClicked(MouseEvent e) {
	// if (isHit(e.getX(), e.getY())) {
	// paintColor = DEFAULT_ACTIVATION_COLOR;
	// for (Iterator i = textListeners.iterator(); i.hasNext(); ) {
	// ((TextListener)i.next()).textClicked();
	// }
	// }
	// }
	// public void mouseDragged(MouseEvent e) {
	// }
	// public void mouseEntered(MouseEvent e) {
	// }
	// public void mouseExited(MouseEvent e) {
	// }
	// public void mouseMoved(MouseEvent e) {
	// if (isHit(e.getX(), e.getY())) {
	// touched = true;
	// if (!entered) {
	// entered = true;
	// animationStep = 0;
	// paintColor = DEFAULT_SELECTION_COLOR;
	// }
	// exited = false;
	// }
	// else {
	// if (!exited) {
	// exited = true;
	// if (animationStep < animationSteps) {
	// animationStep = animationSteps - animationStep;
	// }
	// else {
	// animationStep = 0;
	// }
	// paintColor = color;
	// }
	// entered = false;
	// }
	// }
	// public void mousePressed(MouseEvent e) {
	// }
	// public void mouseReleased(MouseEvent e) {
	// }

}
