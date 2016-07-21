/* SceneObject.java
 * Created on May 26, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

//import java.awt.Graphics2D;
//import java.awt.geom.Rectangle2D;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 
 * @author pkriste
 */
interface SWI_SceneObject {
	
	void paint(Canvas canvas, Rect sceneBounds);

}
