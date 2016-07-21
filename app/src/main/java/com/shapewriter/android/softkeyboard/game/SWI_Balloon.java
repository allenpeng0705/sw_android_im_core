/* SceneBalloon.java
 * Created on May 27, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.util.Random;

import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 
 * @author pkriste
 */
class SWI_Balloon implements SWI_SceneObject,
		SWI_BalloonGameView.TouchEventListener {

	// static final Font BALLOON_FONT = new Font("SanSerif", Font.PLAIN, 10);
	private int width = 0;
	private int height = 0;

	public static int DEFAULT_BALLOON_HEIGHT = 94;
	public static int DEFAULT_BALLOON_WIDTH = 50;

	// private static final int[] balloonColors = {Color.RED, Color.GREEN,
	// Color.BLUE, Color.YELLOW};
	// private static final int[] balloonTextColors = { Color.WHITE,
	// Color.BLACK,
	// Color.WHITE, Color.BLACK };

	private float x;
	private float y;
	private float theta;
	// private int fillColor;
	// private int edgeColor;
	// private int textColor;
	private String text;
	// private CubicCurve2D movementPath;
	// private PathIterator movementPathIterator;
	// private double scale = 1.0d;
	// private double r = 0;
	// private boolean rPos = false;
	// private boolean animated = true;

	private Bitmap bitmap = null;

	private boolean touched = false;

	private int steps = 30;
	private int step = 0;
	private boolean maxAxis = false;
	private static Paint mPaint = new Paint();
	private static Random random = new Random(System.currentTimeMillis());
	private float currAxis;

	private final static int TEXT_SIZE = 12;

	static {
		mPaint.setAntiAlias(true);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setTextSize(TEXT_SIZE);
		mPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
	}

	public SWI_Balloon(Bitmap bm, float x, float y, float theta, String text) {
		this.x = x;
		this.y = y;
		// this.theta = theta;
		this.theta = random.nextInt() % 28;
		this.text = text;
		this.bitmap = bm;

		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}

	public void setBitmap(Bitmap bm) {
		this.bitmap = bm;
	}

	// void setScale(double scale) {
	// this.scale = scale;
	// }

	// void setAnimated(boolean animated) {
	// this.animated = animated;
	// }

	public void paint(Canvas canvas, Rect sceneBounds) {
		paintBalloon(canvas, true);
	}

	// public PathIterator getMovementPathIterator() {
	// return movementPathIterator;
	// }

	// public void setMovementPath(CubicCurve2D.Double movementPath) {
	// this.movementPath = movementPath;
	// }

	// public void setMovementPathIterator(PathIterator movementPathIterator) {
	// this.movementPathIterator = movementPathIterator;
	// }

	void paintBalloon(Canvas canvas, boolean rotate) {
		// canvas.drawText("Hello, Balloon Game", 0, 0, new Paint());
		// drawBalloonBitmap...
		// Shape balloon = new Ellipse2D.Double(0, 0, BALLOON_WIDTH,
		// BALLOON_HEIGHT);
		// Rectangle2D balloonBounds = balloon.getBounds2D();
		// int tailX = (int)(balloonBounds.getWidth() * 0.5d);
		// int tailY = (int)(balloonBounds.getHeight());
		// Shape spline = getSpline(tailX, tailY, BALLOON_WIDTH * 0.5d,
		// BALLOON_HEIGHT, animated ? getR() : Math.PI * 0.5d);
		// AffineTransform tx = AffineTransform.getTranslateInstance(x, y);
		// tx.concatenate(AffineTransform.getRotateInstance(theta));
		// tx.concatenate(AffineTransform.getScaleInstance(scale, scale));
		// balloon = tx.createTransformedShape(balloon);
		// spline = tx.createTransformedShape(spline);
		// // FIXME: Temporary
		// // if (movementPath != null) {
		// // g2d.setColor(Color.BLACK);
		// // g2d.draw(movementPath);
		// // }
		// // FIXME: End temporary
		// cachedBounds = balloon.getBounds2D();
		// Paint balloonPaint = new GradientPaint((float)cachedBounds.getX(),
		// (float)cachedBounds.getY(), Color.WHITE, (float)(cachedBounds.getX()
		// + cachedBounds.getWidth()), (float)(cachedBounds.getY() +
		// cachedBounds.getHeight()), fillColor);
		// g2d.setPaint(balloonPaint);
		// g2d.fill(balloon);
		// g2d.setColor(edgeColor);
		// g2d.draw(balloon);
		// g2d.setColor(edgeColor);
		// g2d.draw(spline);
		// TextLayout tl = new TextLayout(text, BALLOON_FONT,
		// g2d.getFontRenderContext());
		// Rectangle2D r2d = tl.getBounds();
		// double txx = x + (cachedBounds.getWidth() * 0.25d) - (r2d.getWidth()
		// * 0.5d);
		// double txy = y + (cachedBounds.getHeight() * 0.5d);
		// tx = AffineTransform.getTranslateInstance(txx, txy);
		// tx.concatenate(AffineTransform.getRotateInstance(theta));
		// tx.concatenate(AffineTransform.getScaleInstance(scale, scale));
		// g2d.setColor(textColor);
		// g2d.fill(tl.getOutline(tx));

		canvas.save();
		if (rotate)
			currAxis = (step * (theta / steps));
		canvas.rotate(currAxis, x + width / 2, y);
		canvas.drawBitmap(bitmap, x, y, null);

		float w = mPaint.measureText(text, 0, text.length());

		if (w > width * 0.8f) {
			mPaint.setTextSize(TEXT_SIZE * 0.8f);
			w = mPaint.measureText(text, 0, text.length());
		}

		float xPos = x + width / 2 - w / 2;

		canvas.drawText(text, xPos, y + 35, mPaint);
		canvas.restore();

		if (mPaint.getTextSize() != TEXT_SIZE) {
			mPaint.setTextSize(TEXT_SIZE);
		}

		if (rotate) {
			if (maxAxis) {
				step--;
			} else {
				step++;
			}

			if (!maxAxis && step == steps) {
				maxAxis = true;
				step--;
			} else if (maxAxis && step == -1) {
				maxAxis = false;
				step++;
				theta = theta * -1;
			}
		}
	}

	// private double getR() {
	// if (rPos) {
	// if (r < Math.PI * 0.5d) {
	// r+= 0.025d * Math.PI;
	// }
	// else {
	// rPos = false;
	// }
	// }
	// else {
	// if (r > -Math.PI * 0.5d) {
	// r-= 0.025d * Math.PI;
	// }
	// else {
	// rPos = true;
	// }
	// }
	// return r;
	// }

	// private CubicCurve2D getSpline(double x, double y, double w, double h,
	// double r) {
	// CubicCurve2D.Double s = new CubicCurve2D.Double();
	// s.x1 = x;
	// s.y1 = y;
	// s.ctrlx1 = x + (w * Math.sin(r));
	// s.ctrly1 = y + (0.4d * h);
	// s.ctrlx2 = x + (w * Math.sin(r + Math.PI));
	// s.ctrly2 = y + (0.8d * h);
	// s.x2 = x;
	// s.y2 = y + h;
	// return s;
	// }

	void move(float x, float y) {
		this.x = x;
		this.y = y;
	}

	void nextPos(int level) {
		float xPos = x;
		// xPos += random.nextInt() % 2;
		xPos += theta * 0.04f;

		if (xPos < 0) {
			xPos = 0;
			theta = theta * -1;
		}

		if (xPos + width > 320) {
			xPos = 320 - width;
			theta = theta * -1;
		}

		float yPos = y;
		yPos -= level;
		// yPos -= Math.abs(random.nextInt()) % 1;

		x = xPos;
		y = yPos;
	}

	String getText() {
		return text;
	}

	private Rect cachedBounds = null;

	Rect getBounds() {
		if (cachedBounds == null) {
			cachedBounds = new Rect(0, 0, width, height);
			return cachedBounds;
		} else {
			return cachedBounds;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	float getTheta() {
		return theta;
	}

	private boolean isHit(float xPos, float yPos) {
		// Rect rc = new Rect((int)x, (int)y, width, height);
		return xPos >= x && xPos <= (x + width) && yPos >= y
				&& yPos <= (y + height);
		// return rc.contains((int)xPos, (int)yPos);
	}

	public boolean isTouched() {
		return touched;
	}

	public void resetTouchState() {
		touched = false;
	}

	public void touchEvent(MotionEvent e) {
		// Log.i(text, "TouchEvent is activated");
		if (e.getAction() == MotionEvent.ACTION_UP) {
			if (isHit(e.getX(), e.getY())) {
				// Log.e("Balloon", "isHit");
				touched = true;
			} else {
				// Log.i("Balloon", "notHit");
				touched = false;
			}
		}
	}

	// static int getBalloonColor(int numBalloon) {
	// int len = balloonColors.length;
	// int index = numBalloon < len ? numBalloon : numBalloon % len;
	// return balloonColors[index];
	// }

	// static int getBalloonTextColor(int numBalloon) {
	// int len = balloonTextColors.length;
	// int index = numBalloon < len ? numBalloon : numBalloon % len;
	// return balloonTextColors[index];
	// }
}
