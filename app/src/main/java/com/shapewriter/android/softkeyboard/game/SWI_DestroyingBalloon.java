/* SceneDestroyedBalloon.java
 * Created on Jun 1, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import android.content.Context;
import android.graphics.*;
import android.util.Log;

/**
 * 
 * @author pkriste
 */
class SWI_DestroyingBalloon implements SWI_SceneObjectProcess {

	// private static final int numStars = 8;
	// private static final double starSize = 3;
	// private static final int starColor = Color.YELLOW;
	// private static final int starEdgeColor = Color.BLACK;
	// private static final GeneralPath starShape = getStarShape();

	private SWI_Balloon balloon;
	private int step = 0;
	private int steps = 19;

	private static SWI_BalloonBitmapFactory bitmapFactory = null;

	public SWI_DestroyingBalloon(Context context, SWI_Balloon balloon) {
		this.balloon = balloon;

		if (bitmapFactory == null) {
			bitmapFactory = SWI_BalloonBitmapFactory.getInstance(context);
		}
	}

	private int bmPos = 0;

	private Bitmap getBitmap() {
		bmPos = step/5;
		Bitmap bm[] = bitmapFactory.getPopBitmaps();
		return bm[bmPos % bm.length];
	}

	public Bitmap getNextBitmap() {
		return getBitmap();
	}

	public void nextStep() {
		step++;			
	}

	public boolean isDone() {
		return step >= steps;
	}

	public void paint(Canvas canvas, Rect sceneBounds) {
		// double r = (double)step / (double)steps;
		// Composite comp = canvas.getComposite(); // Push composite
		// canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		// (float)(1.0d - r)));
		//balloon.paintBalloon(canvas);
		// canvas.setComposite(comp); // Pop composite
		paintBalloonBlast(canvas);
	}

	// private double getScale() {
	// double r = (double)step / (double)steps;
	// return 1.0d + (0.5d * r);
	// }

	private void paintBalloonBlast(Canvas canvas) {
//		Log.i("DestroyingBalloon", "paintBalloonBlast : " + balloon.getText());
		// Composite comp = g2d.getComposite(); // Push composite
		// Rect bounds = balloon.getBounds();
		// // Blast
		// double blastWidth = bounds.width() * 2.0d;
		// double blastHeight = bounds.height() * 2.0d;
		// // Compute stars' paths
		// double extent = 360.0d / numStars;
		// double theta = - 0.5d * extent;
		// double sx = bounds.left - blastWidth * 0.25d;
		// double sy = bounds.top - blastHeight * 0.25d;
		// // Center position
		// double cx = sx + blastWidth * 0.5d;
		// double cy = sy + blastHeight * 0.5d;
		// // Linear interpolation
		// double r = (double)step / (double)steps;
		// for (int j = 0; j < numStars; j++) {
		// Rect arc = new Rect((int)sx, (int)sy, (int)(blastWidth + sx),
		// (int)(blastHeight + sy));
		// theta+= extent;
		// double targetX = arc.centerX();
		// double targetY = arc.centerY();
		// double dx = r * (targetX - cx);
		// double dy = r * (targetY - cy);
		// canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		// (float)f(step, steps)));
		// paintStar(canvas, starColor, cx + dx, cy + dy, starSize);
		// }
		// g2d.setComposite(comp); // Pop composite

		balloon.setBitmap(getNextBitmap());
		balloon.paintBalloon(canvas, false);				
	}

	// private double f(double x, double range) {
	// if (x < (0.7d * range)) {
	// return x / range;
	// }
	// else {
	// return 1 - (x / range);
	// }
	// }
	//	
	// private void paintStar(Canvas canvas, int c, double x, double y, double
	// s) {
	// GeneralPath path = (GeneralPath)starShape.clone();
	// Rectangle2D bounds = path.getBounds2D();
	// // Center star vertically
	// AffineTransform tx = AffineTransform.getTranslateInstance(x, y - (0.5d *
	// bounds.getHeight()));
	// AffineTransform rx =
	// AffineTransform.getRotateInstance(balloon.getTheta());
	// AffineTransform sx = AffineTransform.getScaleInstance(s, s);
	// tx.concatenate(sx);
	// tx.concatenate(rx);
	// path.transform(tx);
	// g2d.setColor(c);
	// g2d.fill(path);
	// g2d.setColor(starEdgeColor);
	// g2d.draw(path);
	// }

	// private static GeneralPath getStarShape() {
	// GeneralPath path = new GeneralPath();
	// path.moveTo(0.0f,0.0f);
	// path.lineTo(1.0f,1.7f);
	// path.lineTo(3.0f,2.0f);
	// path.lineTo(1.5f,3.6f);
	// path.lineTo(2.0f,5.6f);
	// path.lineTo(0.0f,4.5f);
	// path.lineTo(-2.0f,5.6f);
	// path.lineTo(-1.5f,3.6f);
	// path.lineTo(-3.0f,2.0f);
	// path.lineTo(-1.0f,1.7f);
	// path.lineTo(0.0f,0.0f);
	// return path;
	// return null;
	// }

}
