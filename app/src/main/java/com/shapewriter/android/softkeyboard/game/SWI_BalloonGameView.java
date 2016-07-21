/* BalloonGame.java
 * Created on 2005-feb-08
 *
 * Copyright (c) 2005 Per-Ola Kristensson
 * Copyright (c) 2005 ShapeWriter Inc.
 * 
 * Per-Ola Kristensson <perkr@ida.liu.se>
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.graphics.Bitmap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.content.Context;

import com.shapewriter.android.softkeyboard.R;

/**
 * A simple balloon game. The user must write the words that are in the balloons
 * before the balloons disappear from the window.
 * 
 * @author pkriste
 */
public class SWI_BalloonGameView extends SWI_GameCanvas implements
		SWI_GameModeSwitcher {

	protected static boolean enableAllFeatures = true;

	private long refreshIntervalMs = 30L;

	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			SWI_BalloonGameView.this.postInvalidate();
		}
	};
	private Timer timer = new Timer();
	private boolean noInit = true;

	private SWI_SceneController sceneController;
	private SWI_GameMode titleSceneGameMode;
	private SWI_GameMode practiceGameGameMode;

	/**
	 * Constructs a balloon game canvas.
	 */
	public SWI_BalloonGameView(Context context) {
		super(context);
		this.context = context;
	}

	private Bitmap mBackgroundBitmap = null;

	public SWI_BalloonGameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// instance = this;

		java.io.InputStream is;
		is = context.getResources().openRawResource(R.drawable.sky);

		BitmapFactory.Options opts = new BitmapFactory.Options();

		opts.inJustDecodeBounds = false;
		// opts.inSampleSize = 1;
		mBackgroundBitmap = BitmapFactory.decodeStream(is, null, opts);

		activated(true);
	}

	// public static MainLoop getInstance(){
	// return instance;
	// }

	public void switchGameMode(SWI_GameMode newGameMode,
			boolean isTitleSceneGameMode) {
		newGameMode.setGameModeSwitcher(this);
		if (isTitleSceneGameMode)
			titleSceneGameMode = newGameMode;
		else
			practiceGameGameMode = newGameMode;
	}

	protected void pause(boolean paused) {
		if (practiceGameGameMode != null) {
			((SWI_PracticeBalloonGameMode) practiceGameGameMode)
					.setPaused(paused);
		}
	}

	private void doInit() {
		switchGameMode(new SWI_TitleGameMode(context, sceneController), true);
		timer.schedule(task, refreshIntervalMs, refreshIntervalMs);
	}

	@Override
	public void activated(boolean enableAllFeatures) {
		SWI_BalloonGameView.enableAllFeatures = enableAllFeatures;
		sceneController = new SWI_SceneController(this, mBackgroundBitmap);
	}

	@Override
	protected void onAttachedToWindow() {
		// timer.schedule(task, refreshIntervalMs, refreshIntervalMs);
	}

	@Override
	protected void onDetachedFromWindow() {
		deactivated();
	}

	@Override
	public void deactivated() {
		timer.cancel();
		mBackgroundBitmap = null;

		if (practiceGameGameMode != null) {
			((SWI_PracticeBalloonGameMode) practiceGameGameMode).deinitGame();
		}
	}

	// public void setKeyboard(View keyboard){
	// this.keyboard = keyboard;
	// }
	//	
	// public View getKeyboard(){
	// return keyboard;
	// }

	/**
	 * Called when the balloon game receives user input.
	 * 
	 * @param userInput
	 *            a user input information object
	 */
	@Override
	public void userInput(SWI_UserInput userInput) {
		if (titleSceneGameMode != null)
			titleSceneGameMode.handleUserInput(userInput);
		if (practiceGameGameMode != null)
			practiceGameGameMode.handleUserInput(userInput);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		dispatch(e);
		return true;
	}

	public static interface TouchEventListener {
		void touchEvent(MotionEvent e);
	}

	private Vector<TouchEventListener> touchEventListeners = new Vector<TouchEventListener>();

	public void addTouchEventListener(TouchEventListener l) {
		touchEventListeners.add(l);
	}

	public void removeTouchEventListener(TouchEventListener l) {
		touchEventListeners.remove(l);
	}

	private void dispatch(MotionEvent e) {
		for (Iterator<TouchEventListener> i = touchEventListeners.iterator(); i
				.hasNext();) {
			TouchEventListener l = i.next();
			l.touchEvent(e);
		}
	}

	/**
	 * Paints the scene and the objects on the scene.
	 * 
	 * @param g
	 *            the graphics context
	 */

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Log.i("MainLoop", "UI thread id : " +
		// Thread.currentThread().toString());

		if (noInit) {
			doInit();
			noInit = false;
		}
		sceneController.paintScenes(canvas);
	}
}
