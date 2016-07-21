/* SceneGraphicsText.java
 * Created on Jun 3, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

// import java.awt.Color;
// import java.awt.Font;
// import java.awt.Graphics2D;
// import java.awt.Shape;
// import java.awt.font.TextLayout;
// import java.awt.geom.AffineTransform;
// import java.awt.geom.Rectangle2D;

import android.graphics.*;

/**
 * 
 * @author pkriste
 */
public class SWI_MissedWord implements SWI_SceneObjectProcess {

	private String word;
	private int step = 0;
	private int steps = 100;
	private float x = -1.0f, y = -1.0f;

	// private Font font = new Font("SanSerif", Font.BOLD, 22);

	private Paint mPaint = new Paint();
	private int fontSize = 25;

	public SWI_MissedWord(String word) {
		this.word = word;

		mPaint.setAntiAlias(true);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setTextSize(fontSize);
		mPaint.setColor(Color.argb(150, 50, 50, 200));
		mPaint.setTypeface(Typeface
				.create(Typeface.SANS_SERIF, Typeface.NORMAL));
	}

	public boolean isDone() {
		return step >= steps;
	}

	public void nextStep() {
		step++;
	}

	private void resizeFontSize() {
		fontSize -= step / 25;

		if (fontSize < 0)
			fontSize = 0;

		mPaint.setTextSize(fontSize);
	}

	public void paint(Canvas canvas, Rect sceneBounds) {
		if (fontSize <= 1 || word == null)
			return;
		
		// TextLayout tl = new TextLayout(word, font,
		// g2d.getFontRenderContext());
		float theta = (step * (360f / steps)); // One full rotation
		float s = (float) (1.0f - Math.pow((((double) step / (double) steps)),
				2)); // Quadratic zoom from 1:1 to 0:1
		if (step == 0 || x == -1.0f && y == -1.0f) {
			x = (float) (Math.random() * 0.8f * sceneBounds.width());
			y = (float) ((sceneBounds.height() / 4) + Math.random() * 0.5d
					* sceneBounds.height());
		}
		canvas.save();

		canvas.rotate(theta, x, y);
		resizeFontSize();
		canvas.drawText(word, x, y, mPaint);

		canvas.restore();
	}
}
