/* Crosshair.java
 * Created on Jun 3, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import android.graphics.*;

/**
 * 
 * @author pkriste
 */
public class SWI_Crosshair implements SWI_SceneObject {

	private float x, y;
	private int color;
	private float radius;
	private Rect cachedBounds = new Rect(0, 0, 0, 0);
	private Paint mPaint = new Paint();

	SWI_Crosshair(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		// this.color = color;
		float r2 = radius * 2;
		cachedBounds.set((int) (x - getPaddingH(r2)),
				(int) (y - getPaddingV(r2)), (int) (x + r2 + getPaddingH(r2)),
				(int) (y + r2 + getPaddingV(r2)));

		mPaint.setColor(Color.argb(30, 50, 100, 50));
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(2);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	public void paint(Canvas canvas, Rect sceneBounds) {
		paintCrosshair(canvas);
	}

	private void paintCrosshair(Canvas canvas) {
		// Paint cross hair
		// Ellipse2D.Double circle = new Ellipse2D.Double(x, y, radius * 2.0d,
		// radius * 2.0d);
		// Rect circleBounds = new Rect((int) x, (int) y, (int) (x + radius *
		// 2),
		// (int) (y + radius * 2));
		//
		// float paddingH = getPaddingH(circleBounds.width());
		// float paddingV = getPaddingV(circleBounds.height());
		//		
		// Line2D.Double horizontal = new Line2D.Double(circleBounds.getMinX()
		// - paddingH, circleBounds.getCenterY(), circleBounds.getMaxX()
		// + paddingH, circleBounds.getCenterY());
		// Line2D.Double vertical = new Line2D.Double(circleBounds.getCenterX(),
		// circleBounds.getMinY() - paddingV, circleBounds.getCenterX(),
		// circleBounds.getMaxY() + paddingV);
		// g2d.setColor(color);
		// g2d.draw(circle);
		// g2d.draw(horizontal);
		// g2d.draw(vertical);

		float cx = cachedBounds.left + cachedBounds.width() / 2;
		float cy = cachedBounds.top + cachedBounds.height() / 2;

		canvas.drawCircle(cx, cy, radius, mPaint);
		canvas.drawLine(cx - radius - getPaddingH(radius), cy, cx + radius
				+ getPaddingH(radius), cy, mPaint);
		canvas.drawLine(cx, cy - radius - getPaddingV(radius), cx, cy + radius
				+ getPaddingH(radius), mPaint);
	}

	private float getPaddingH(float width) {
		return 0.3f * width;
	}

	private float getPaddingV(float height) {
		return 0.3f * height;
	}

	Rect getBounds() {
		return cachedBounds;
	}

}
