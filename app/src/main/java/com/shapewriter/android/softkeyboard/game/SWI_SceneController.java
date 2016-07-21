/* SceneController.java
 * Created on Jun 7, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.util.Vector;


import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.util.Log;


/**
 * 
 * @author pkriste
 */
class SWI_SceneController implements SWI_SceneControllerView {
	
	private Vector<SWI_Scene> scenes = new Vector<SWI_Scene>();
	private Vector<SWI_Scene> flaggedRemoveScenes = new Vector<SWI_Scene>();
	private SWI_BalloonGameView host;
	
//	private ComponentListener cListener = new ComponentAdapter() {
//		public void componentResized(ComponentEvent e) {
//			handleComponentResized();
//		}
//	};
	
	private Bitmap backgroundImage = null;
	
	SWI_SceneController(SWI_BalloonGameView host, Bitmap backgroundImage) {
		this.host = host;
//		this.host.addComponentListener(cListener);
		this.backgroundImage = backgroundImage;
	}
	
	public void paintScenes(Canvas canvas) {
		paintBackgroundImage(canvas);
		setRenderingHints(canvas);
		
		for(int i = 0; i < flaggedRemoveScenes.size(); i++) {		
			SWI_Scene s = flaggedRemoveScenes.get(i);
			scenes.remove(s);
		}
		
		flaggedRemoveScenes.clear();
		
		for(int i = 0; i < scenes.size(); i++) {
			SWI_Scene s = scenes.get(i);	
			s.paintScene(canvas);
		}		
	}

	public void addScene(SWI_Scene scene) {
//		Log.i("SceneController", "Adding scene " + scene.toString());
		host.addTouchEventListener(scene);
//		host.addMouseListener(scene);
//		host.addMouseMotionListener(scene);
		scenes.add(scene);
	}
	
	public void synchronizedremoveScene(SWI_Scene scene) {
//		host.removeMouseListener(scene);
//		host.removeMouseMotionListener(scene);
		scenes.remove(scene);		
	}
	
	public synchronized void flagRemoveScene(SWI_Scene scene) {
		flaggedRemoveScenes.add(scene);
	}
	
	private void paintBackgroundImage(Canvas canvas) {
		// Paints background image (if it could be loaded)
		if (backgroundImage != null) {			
			canvas.drawBitmap(backgroundImage, 0, 0, null);
		}
	}
	
	private void setRenderingHints(Canvas canvas) {
//		canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
	
//	private void handleComponentResized() {
//		for (Iterator<Scene> i = scenes.iterator(); i.hasNext(); ) {
//			Scene s = i.next();
//			s.setBounds(getPaintableBounds());
//		}
//	}
	
	private int getPaintableX() {
		Rect rect = new Rect();
		//host.getHitRect(rect);
		host.getDrawingRect(rect);
		return rect.left;
	}
	
	private int getPaintableY() {
		Rect rect = new Rect();
		//host.getHitRect(rect);
		host.getDrawingRect(rect);
		return rect.top;
	}
	
	private int getPaintableWidth() {
		Rect rect = new Rect();
		host.getDrawingRect(rect);
		return rect.width();
	}
	
	private int getPaintableHeight() {
		Rect rect = new Rect();
		host.getDrawingRect(rect);
		return rect.height();
	}
	
	Rect getPaintableBounds() {
		int x = getPaintableX();
		int y = getPaintableY();
		int w = getPaintableWidth();
		int h = getPaintableHeight();
		return new Rect(x, y, x + w, y + h);
	}
	
	Vector<SWI_Scene> getScenes() {
		return scenes;
	}
}
