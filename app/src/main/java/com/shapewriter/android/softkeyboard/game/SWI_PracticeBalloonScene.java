/* BalloonScene.java
 * Created on May 26, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.*;

import com.shapewriter.android.softkeyboard.R;

/**
 * 
 * @author pkriste
 */
class SWI_PracticeBalloonScene extends SWI_Scene {
	private int level = 1;
	private boolean paused = false;
	private int maxBalloons = 10;
	private int numTicksUntilNextBalloon = 280;
	private Vector<SWI_Balloon> balloons = new Vector<SWI_Balloon>();
	private Map<String, SWI_Balloon> balloonsMap = new HashMap<String, SWI_Balloon>();
	// private int numBalloonsHit = 0;
	private int numWordsEntered = 0;
	private SWI_EriScheduler eriScheduler;
	private int ticksUntilNextBalloonCounter = 0;
	private int producedBalloons = 0;
	private Map<String, SWI_EriItem> eriMap = new HashMap<String, SWI_EriItem>();
	private Vector<SWI_PracticeBalloonSceneListener> balloonSceneListeners = new Vector<SWI_PracticeBalloonSceneListener>();
	private String mLastTracedWord = null;

	SWI_PracticeBalloonScene(Context context, Rect bounds,
			SWI_SceneControllerView sceneControllerView,
			SWI_EriScheduler eriScheduler) {
		super(context, bounds, sceneControllerView);
		this.eriScheduler = eriScheduler;
		// numTicksUntilNextBalloon = 100;
		// maxBalloons = 10;
		setFadeIn();
	}

	public void setPause(boolean pause) {
		this.paused = pause;
	}

	public void setOption() {
		// Log.i("PracticeBalloonScene", "option setting clicked");
		final int currentLevel = level;

		// Pause, when select game level
		paused = true;

		new AlertDialog.Builder(context).setTitle(R.string.game_option_title)
				.setSingleChoiceItems(R.array.game_option_level,
						currentLevel - 1,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								/* User clicked on a radio button do some stuff */
								setLevel(whichButton + 1);
							}
						}).setPositiveButton(R.string.game_option_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								/* User clicked Yes so do some stuff */
								paused = false;
							}
						}).setNegativeButton(R.string.game_option_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								/* User clicked No so do some stuff */
								setLevel(currentLevel);
								paused = false;
							}
						}).setCancelable(false).create().show();
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFlyingPositionVertical() {
		return level * 2;
	}

	public void clearBalloons() {
		for (Iterator<SWI_Balloon> i = balloons.iterator(); i.hasNext();) {
			SWI_Balloon b = i.next();
			if (true) {
				removeTouchEventListener(b);
				i.remove();
				balloonsMap.remove(b.getText());
				eriMap.remove(b.getText());
				removeSceneObject(b);
			}
		}
	}

	@Override
	void paintScene(Canvas canvas) {
		// super.paintScene(canvas);
		setFade();
		if (sceneObjects.size() == 0 && paused == true) {
			paused = false;
		}

		for (Iterator<SWI_SceneObject> i = sceneObjects.iterator(); i.hasNext();) {
			SWI_Balloon sObj = (SWI_Balloon) i.next();
			if (sObj.isTouched() && paused == false) {
				sObj.resetTouchState();
				paused = true;
			} else if (sObj.isTouched() && paused == true) {
				sObj.resetTouchState();
				paused = false;				
				doCheckString(mLastTracedWord, -1);
			}
			sObj.paint(canvas, bounds);
		}

		paintSceneObjectProcess(canvas);
		doTick();
	}

	void addBalloonSceneListener(SWI_PracticeBalloonSceneListener bsl) {
		balloonSceneListeners.add(bsl);
	}

	void removeBalloonSceneListener(SWI_PracticeBalloonSceneListener bsl) {
		balloonSceneListeners.remove(bsl);
	}

	private void notifyBalloonSceneListeners(SWI_EriItem eriItem,
			boolean balloonIsHit, boolean balloonOutOfScreen) {
		for (Iterator<SWI_PracticeBalloonSceneListener> i = balloonSceneListeners
				.iterator(); i.hasNext();) {
			SWI_PracticeBalloonSceneListener bsl = i.next();
			if (balloonIsHit) {
				bsl.balloonHit(eriItem);
			} else if (balloonOutOfScreen) {
				bsl.outScreen(eriItem);
			} else {
				bsl.missedWord(eriItem);
			}
		}
	}

	void handleUserInput(String str, final long traceTime) {
		if (paused == true) {
			mLastTracedWord = str;
		} else {
			doCheckString(str, traceTime);
		}
	}

	private void doTick() {
		if (paused == true) {
		} else {
			doCheckIfAddBalloon();
			doUpdateBalloonPositions();
		}
	}

	private int getNumTicksUntilNextBalloon() {
		return numTicksUntilNextBalloon - level * 40;
	}

	private void doCheckIfAddBalloon() {
		int numBalloons = balloons.size();
		// Can we add a new balloon to the scene?
		if (numBalloons < maxBalloons) {
			if (numBalloons > 0) {
				if (ticksUntilNextBalloonCounter < getNumTicksUntilNextBalloon()) {
					ticksUntilNextBalloonCounter++;
				} else {
					// marked temporarily to make sure only one balloon on the
					// screen
					// doAddBalloon();
					ticksUntilNextBalloonCounter = 0;
				}
			} else {
				doAddBalloon();
				ticksUntilNextBalloonCounter = 0;
			}
		}
	}

	private int getNextRandomX(SWI_Balloon b) {
		int xPos = (int) b.getX();
		xPos += random.nextInt() % 2;

		if (xPos < 0)
			xPos = 0;
		if (xPos + b.getWidth() > bounds.right) {
			xPos = bounds.right - b.getWidth();
		}

		return xPos;
		// return (int) b.getX();
	}

	private int getNextRandomY(SWI_Balloon b) {
		int yPos = (int) b.getY();
		yPos -= getFlyingPositionVertical();
		yPos -= Math.abs(random.nextInt()) % 5;

		return yPos;

		// return (int) b.getY() - getFlyingPositionVertical();
	}

	private void doUpdateBalloonPositions() {
		for (Iterator<SWI_Balloon> i = balloons.iterator(); i.hasNext();) {
			SWI_Balloon b = i.next();
			// int x = getNextRandomX(b);
			// int y = getNextRandomY(b);
			// b.move(x, y);
			b.nextPos(level);
			if (isBalloonOutsideScene(b)) {
				removeTouchEventListener(b);
				i.remove();
				// Log.i("PracticeBalloonScene", "Ball is beyond scene");
				// Log.i("PracticeBalloonScene", "Removing Map<String, Balloon>:
				// <" + b.getText() + ", " + b.toString() + ">");
				balloonsMap.remove(b.getText());
				doHandleBalloonOutOfScene(b);
				removeSceneObject(b);
			}
		}
	}

	private boolean isBalloonOutsideScene(SWI_Balloon b) {
		Rect bounds = b.getBounds();
		return b.getY() + bounds.height() <= 0;
	}

	// private AutoGesture autoGestureImpl;

	private int bmPos = 0;

	private Bitmap getBitmap() {
		Bitmap bm[] = bitmapFactory.getBitmaps();
		return bm[bmPos++ % bm.length];
	}

	private Random random = new Random(System.currentTimeMillis());

	private int getRandomX() {
		int xPos = 0;

		int max = bounds.width() - SWI_Balloon.DEFAULT_BALLOON_WIDTH;
		xPos = Math.abs(random.nextInt()) % max;
		return xPos;
	}

	private int getShowupY() {
		int yPos = bounds.height();
		return yPos;
	}

	private void doAddBalloon() {
		// Log.i("PracticeBallScene", "eriScheduler = " + (eriScheduler ==
		// null));

		SWI_EriItem ei = eriScheduler.getNextEriItem();
		ei.incShowTime();
		String str = ei.getWord();
		eriMap.put(str, ei);

		SWI_Balloon b = new SWI_Balloon(getBitmap(), getRandomX(),
				getShowupY(), 360 * 0.06f, str);
		// CubicCurve2D.Double movementPath = getBalloonPath();
		// b.setMovementPath((CubicCurve2D.Double) movementPath.clone());
		// b.setMovementPathIterator(movementPath.getPathIterator(null,
		// 0.0025d));
		producedBalloons++;
		balloons.add(b);
		addSceneObject(b);
		addTouchEventListener(b);
		// Associate a string with a balloon for lookup
		// Log.i("PracticeBalloonScene", "Adding Map<String, Balloon>: <" + str
		// + ", " + b.toString() + ">");
		balloonsMap.put(str, b);
		// if (autoGesture) {
		// Attach "auto-pilot" drawing code to the keyboard
		// autoGestureImpl = new AutoGesture(str, 2500);
		// autoGestureImpl.addListener(new AutoGesture.Listener() {
		// public void isDone(String str) {
		// handleUserInput(str);
		// }
		// });
		// keyboard.addPainter(autoGestureImpl);
		// }
	}

	// private CubicCurve2D.Double getBalloonPath() {
	// return getRandomBalloonPath();
	// }
	//	
	// private CubicCurve2D.Double getRandomBalloonPath() {
	// double width = bounds.getWidth();
	// double height = bounds.getHeight();
	// CubicCurve2D.Double spline = new CubicCurve2D.Double();
	// double x = Math.random() * width * 0.3d;
	// double y = height;
	// double span = width * 0.2;
	// spline.x1 = x;
	// spline.y1 = y;
	// spline.ctrlx1 = (Math.random() * span) + x;
	// spline.ctrly1 = height * 0.3d;
	// spline.ctrlx2 = (Math.random() * span) + spline.ctrlx1;
	// spline.ctrly2 = height * 0.7d;
	// double max = Math.max(x, width * 0.8d);
	// double rx = max + ((width - max) * Math.random());
	// spline.x2 = rx;
	// spline.y2 = -240;
	// return spline;
	// }

	private void doCheckString(String str, final long traceTime) {
		// Log.i("PracticeBalloonScene", "doCheckString:" + str);
		numWordsEntered++;
		SWI_Balloon b = balloonsMap.get(str);
		SWI_EriItem eriItem = null;
		if (b == null) {
			// Log.i("PracticeBalloonScene", "Word missed:" + str);
			// User word doesn't match a balloon
			SWI_MissedWord mw = new SWI_MissedWord(str);
			addSceneObjectProcess(mw);
		} else {
			// A balloon is hit
			// Log.i("PracticeBalloonScene", "Matched word:" + str);
			eriItem = eriMap.get(b.getText());
			eriItem.setTraceTimeMillis(traceTime);
			// numBalloonsHit++;
			balloonsMap.remove(b.getText());
			removeSceneObject(b);
			balloons.remove(b);
			SWI_DestroyingBalloon db = new SWI_DestroyingBalloon(context, b);
			addSceneObjectProcess(db);
			eriScheduler.updateEri(eriItem);
			eriMap.remove(b.getText());
		}

		if (traceTime >= 0)
			notifyBalloonSceneListeners(eriItem, b != null, false);
	}

	private void doHandleBalloonOutOfScene(SWI_Balloon b) {
		SWI_EriItem ei = eriMap.get(b.getText());
		if (ei == null) {
			throw new IllegalStateException("Cannot find ERI item");
		} else {
			ei.setTimeStampMs(System.currentTimeMillis());
			notifyBalloonSceneListeners(ei, false, true);
		}
		eriMap.remove(b.getText());
	}

	int getNumBalloonsInScene() {
		return balloons.size();
	}

	Iterator<SWI_Balloon> balloonsIterator() {
		return balloons.iterator();
	}

}
