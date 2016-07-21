/* Scene.java
 * Created on May 26, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.util.Iterator;
import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import android.content.Context;

/**
 * 
 * @author pkriste
 */
class SWI_Scene implements SWI_BalloonGameView.TouchEventListener {
	protected Context context;
	protected SWI_BalloonBitmapFactory bitmapFactory;;

	protected Rect bounds;
	protected SWI_SceneControllerView sceneControllerView;

	protected Vector<SWI_SceneObject> sceneObjects = new Vector<SWI_SceneObject>();
	protected Vector<SWI_SceneObjectProcess> sceneObjectProcesses = new Vector<SWI_SceneObjectProcess>();
	private Vector<SWI_BalloonGameView.TouchEventListener> touchEventListeners = new Vector<SWI_BalloonGameView.TouchEventListener>();

	private boolean fadeIn = false;
	private int fadeInStep = 0;
	private int fadeInSteps = 30;
	private boolean fadeOut = false;
	// private boolean removeScene = false;

	SWI_Scene(Context context, Rect bounds,
			SWI_SceneControllerView sceneControllerView) {
		this.bounds = bounds;
		this.sceneControllerView = sceneControllerView;
		this.context = context;

		bitmapFactory = SWI_BalloonBitmapFactory.getInstance(context);
	}

	void paintScene(Canvas canvas) {
		setFade();
		paintSceneObject(canvas);
		paintSceneObjectProcess(canvas);
	}

	void setFade() {
		if (fadeIn) {
			if (fadeInStep < fadeInSteps) {
				fadeInStep++;
			} else {
				fadeIn = false;
			}
		} else if (fadeOut) {
			// if (fadeOutStep < fadeOutSteps) {
			// fadeOutStep++;
			// } else {
			// if(removeScene)
			// sceneControllerView.flagRemoveScene(this);
			// }
		}
	}

	void paintSceneObject(Canvas canvas) {
		for (Iterator<SWI_SceneObject> i = sceneObjects.iterator(); i.hasNext();) {
			SWI_SceneObject sObj = i.next();
			sObj.paint(canvas, bounds);
		}
	}

	void paintSceneObjectProcess(Canvas canvas) {
		for (Iterator<SWI_SceneObjectProcess> i = sceneObjectProcesses
				.iterator(); i.hasNext();) {
			SWI_SceneObjectProcess sObjP = i.next();
			if (sObjP.isDone()) {
				i.remove();
			} else {
				sObjP.paint(canvas, bounds);
				sObjP.nextStep();
			}
		}
	}

	void setFadeIn() {
		fadeIn = true;
	}

	void removeMe() {
		sceneControllerView.flagRemoveScene(this);
	}

	void setFadeOut(final boolean removeScene) {
		fadeOut = true;
		// this.removeScene = removeScene;

		if (removeScene)
			sceneControllerView.flagRemoveScene(this);
	}

	void addTouchEventListener(SWI_BalloonGameView.TouchEventListener l) {
		touchEventListeners.add(l);
	}

	void removeTouchEventListener(SWI_BalloonGameView.TouchEventListener l) {
		touchEventListeners.remove(l);
	}

	// void addMouseListener(MouseListener mouseListener) {
	// mouseListeners.add(mouseListener);
	// }
	//	
	// void removeMouseListener(MouseListener mouseListener) {
	// flaggedRemoveMouseListeners.add(mouseListener);
	// }
	//	
	// void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
	// mouseMotionListeners.add(mouseMotionListener);
	// }
	//	
	// void removeMouseMotionListener(MouseMotionListener mouseMotionListener) {
	// flaggedRemoveMouseMotionListeners.add(mouseMotionListener);
	// }

	void addSceneObject(SWI_SceneObject sObj) {
		sceneObjects.add(sObj);
	}

	void removeSceneObject(SWI_SceneObject sObj) {
		sceneObjects.remove(sObj);
	}

	void addSceneObjectProcess(SWI_SceneObjectProcess sObjP) {
		sceneObjectProcesses.add(sObjP);
	}

	void removeSceneObjectProcess(SWI_SceneObjectProcess sObjP) {
		sceneObjectProcesses.remove(sObjP);
	}

	void clearAllSceneObjects() {
		sceneObjects.clear();
		sceneObjectProcesses.clear();
	}

	void setBounds(Rect bounds) {
		this.bounds = bounds;
	}

	int numSceneObjects() {
		return sceneObjects.size() + sceneObjectProcesses.size();
	}

	@Override
	public void touchEvent(MotionEvent e) {
		dispatchTouchEvent(e);
	}

	private void dispatchTouchEvent(MotionEvent e) {
		for (Iterator<SWI_BalloonGameView.TouchEventListener> i = touchEventListeners
				.iterator(); i.hasNext();) {
			// Log.i("Scene", "dispatchTouchEvent is activated");
			SWI_BalloonGameView.TouchEventListener l = i.next();
			l.touchEvent(e);
		}
	}

	// public void mouseClicked(MouseEvent e) {
	// dispatchMouseEvent(e);
	// }
	// public void mouseEntered(MouseEvent e) {
	// dispatchMouseEvent(e);
	// }
	// public void mouseExited(MouseEvent e) {
	// dispatchMouseEvent(e);
	// }
	// public void mousePressed(MouseEvent e) {
	// dispatchMouseEvent(e);
	// }
	// public void mouseReleased(MouseEvent e) {
	// dispatchMouseEvent(e);
	// }
	// public void mouseDragged(MouseEvent e) {
	// dispatchMouseMotionEvent(e);
	// }
	// public void mouseMoved(MouseEvent e) {
	// dispatchMouseMotionEvent(e);
	// }

	// private void dispatchMouseEvent(MouseEvent e) {
	// for (Iterator i = mouseListeners.iterator(); i.hasNext(); ) {
	// MouseListener l = (MouseListener)i.next();
	// switch (e.getID()) {
	// case MouseEvent.MOUSE_CLICKED:
	// l.mouseClicked(e);
	// break;
	// case MouseEvent.MOUSE_ENTERED:
	// l.mouseEntered(e);
	// break;
	// case MouseEvent.MOUSE_EXITED:
	// l.mouseExited(e);
	// break;
	// case MouseEvent.MOUSE_PRESSED:
	// l.mousePressed(e);
	// break;
	// case MouseEvent.MOUSE_RELEASED:
	// l.mouseReleased(e);
	// break;
	// }
	// }
	// for (Iterator i = flaggedRemoveMouseListeners.iterator(); i.hasNext(); )
	// {
	// mouseListeners.remove((MouseListener)i.next());
	// }
	// flaggedRemoveMouseListeners.clear();
	// }

	// private void dispatchMouseMotionEvent(MouseEvent e) {
	// for (Iterator i = mouseMotionListeners.iterator(); i.hasNext(); ) {
	// MouseMotionListener l = (MouseMotionListener)i.next();
	// switch (e.getID()) {
	// case MouseEvent.MOUSE_DRAGGED:
	// l.mouseDragged(e);
	// break;
	// case MouseEvent.MOUSE_MOVED:
	// l.mouseMoved(e);
	// break;
	// }
	// }
	// for (Iterator i = flaggedRemoveMouseMotionListeners.iterator();
	// i.hasNext(); ) {
	// mouseMotionListeners.remove((MouseMotionListener)i.next());
	// }
	// flaggedRemoveMouseMotionListeners.clear();
	// }

}
