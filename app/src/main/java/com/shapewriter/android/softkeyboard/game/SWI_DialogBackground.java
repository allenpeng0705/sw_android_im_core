/* DialogBackground.java
 * Created on Jun 6, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

// import java.awt.AlphaComposite;
// import java.awt.Color;
// import java.awt.Composite;
// import java.awt.Graphics2D;
// import java.awt.geom.Point2D;
// import java.awt.geom.Rectangle2D;
// import java.awt.geom.RoundRectangle2D;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * 
 * @author pkriste
 */
class SWI_DialogBackground implements SWI_SceneObject {
	private int widgetArcW = 10;
	private int widgetArcH = 10;
	private Rect bounds;
	
	public final static int MAIN_BACKGROUND = 0x01;
	public final static int SMALL_BACKGROUND = 0x02;
	
	private int type;

	private Paint mPaint;

	SWI_DialogBackground(Rect sceneBounds, int width, int height, int type) {
		Point ulc = SWI_Services.getTopLeftCenteringCoordinate(sceneBounds, width,
				height);
		bounds = new Rect(ulc.x, ulc.y, ulc.x + width, ulc.y + height);

		this.type = type;		
		setPaint();
	}
	
	private void setPaint(){
		mPaint = new Paint();
		
		if(type == MAIN_BACKGROUND){
			mPaint.setStrokeWidth(5);
			mPaint.setColor(Color.argb(50, 50, 50, 100));	
		}else{
			mPaint.setStrokeWidth(2);
			mPaint.setColor(Color.argb(20, 50, 50, 100));
		}
		
		mPaint.setAntiAlias(true);		
		mPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	SWI_DialogBackground(Rect bounds, int type) {
		this.bounds = bounds;
		this.type = type;
		
		setPaint();
	}

	public void paint(Canvas canvas, Rect sceneBounds) {
		paintDialogBoxBackground(canvas, bounds);
	}

	private void paintDialogBoxBackground(Canvas canvas, Rect rect) {
		// Composite comp = g2d.getComposite(); // Push composite
		// The alpha composite code here is to accomodate the fact that the
		// scene might
		// have already set an alpha composite, in that case we extract the
		// alpha channel
		// and take it into account when compositing the dialog background
		// float alpha = -1.0f;
		// if (comp instanceof AlphaComposite) {
		// alpha = ((AlphaComposite)comp).getAlpha();
		// }
		// g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		// alpha == -1.0f ? widgetTranslucency : Math.min(alpha,
		// widgetTranslucency)));
		// RoundRectangle2D rr2d = new RoundRectangle2D.Double(0, 0, 1, 1,
		// widgetArcW, widgetArcH);
		// rr2d.setFrame(r2d);
		// g2d.setColor(widgetColor);
		// g2d.fill(rr2d);
		// g2d.setComposite(comp); // Pop composite
		// g2d.setColor(widgetEdgeColor);
		// g2d.draw(rr2d);

		RectF r = new RectF(bounds);
		
//		Log.i("DialogBackground", " r: " + r.left + " " + r.top + " "
//				+ r.width() + " " + r.height());

//		mPaint.setColor(Color.TRANSPARENT);
		canvas.drawRoundRect(r, widgetArcW, widgetArcH, mPaint);
//		canvas.drawRect(bounds, mPaint);
	}

}
