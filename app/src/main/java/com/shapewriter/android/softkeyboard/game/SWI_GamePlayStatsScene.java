/* GamePlayStatistics.java
 * Created on Jun 3, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

// import java.awt.Color;
// import java.awt.geom.Rectangle2D;
import java.util.Vector;
import java.util.Iterator;
import android.content.Context;
import android.graphics.*;

/**
 * 
 * @author pkriste
 */
public class SWI_GamePlayStatsScene extends SWI_Scene {

	// private Balloon balloon;
	private SWI_Text scoreBoard;
	private SWI_Crosshair crosshair;
	// private SWI_Text accuracy;
	private Vector<SWI_GamePlayStatsSceneListener> gamePlayStatsSceneListeners = new Vector<SWI_GamePlayStatsSceneListener>();
	private SWI_Text level;
	private SWI_Text exit;
	// private boolean levelGesture = false;
	private SWI_TextListener levelTextListener = new SWI_TextListener() {
		public void textClicked() {
			for (Iterator<SWI_GamePlayStatsSceneListener> i = gamePlayStatsSceneListeners
					.iterator(); i.hasNext();) {
				i.next().setOption();
			}
		}
	};
	private SWI_TextListener exitTextListener = new SWI_TextListener() {
		public void textClicked() {
			setFadeOut(false);
			// removeMouseListener(exit);
			// removeMouseMotionListener(exit);
			for (Iterator<SWI_GamePlayStatsSceneListener> i = gamePlayStatsSceneListeners
					.iterator(); i.hasNext();) {
				i.next().gamePlayStatsSceneDismissed();
			}
		}
	};

	SWI_GamePlayStatsScene(Context context, Rect bounds,
			SWI_SceneControllerView sceneControllerView) {
		super(context, bounds, sceneControllerView);
		setupSceneObjects();
		setFadeIn();
	}

	void addGamePlayStatsSceneListener(
			SWI_GamePlayStatsSceneListener gamePlayStatsSceneListener) {
		gamePlayStatsSceneListeners.add(gamePlayStatsSceneListener);
	}

	void removeGamePlayStatsSceneListener(
			SWI_GamePlayStatsSceneListener gamePlayStatsSceneListener) {
		gamePlayStatsSceneListeners.remove(gamePlayStatsSceneListener);
	}

	private void setupSceneObjects() {
		clearAllSceneObjects();

		float leftX = bounds.width() * 0.65f;
		float topY = bounds.height() - SWI_Balloon.DEFAULT_BALLOON_HEIGHT
				* 2.3f;
		float rightX = bounds.width() * 0.8f;

		// balloon = new Balloon(bitmapFactory.getBitmaps()[0], leftX, topY,
		// (float)Math.PI * 0.08f, " ");
		// balloon.setScale(0.5d);
		// balloon.setAnimated(false);
		// addSceneObject(balloon);

		float bottomY = topY + SWI_Balloon.DEFAULT_BALLOON_HEIGHT * 1.0f;
		crosshair = new SWI_Crosshair(leftX, bottomY,
				SWI_Balloon.DEFAULT_BALLOON_WIDTH * 0.25f);
		addSceneObject(crosshair);

		scoreBoard = new SWI_Text("0", rightX, topY, SWI_Text.BALLOON_NUM,
				false, true);
		float h = scoreBoard.getTextBounds().height();
		Rect crosshairBounds = crosshair.getBounds();
		// scoreBoard.setY(topY + SWI_Balloon.DEFAULT_BALLOON_HEIGHT * 0.75f - h
		// * 0.5f);
		scoreBoard.setY(crosshairBounds.top + crosshairBounds.height() * 0.75f);
		addSceneObject(scoreBoard);

		// accuracy = new SWI_Text("0%", rightX, bottomY, SWI_Text.PERCENT,
		// false,
		// true);
		// accuracy.setY(crosshairBounds.top + crosshairBounds.height() *
		// 0.75f);
		// addSceneObject(accuracy);

		float textBoundsHeight = 80.0f;
		float textY = bounds.height() - textBoundsHeight - 15.0f;
		float textBoundsWidth = bounds.width() * 0.95f - leftX;
		Rect r2d = new Rect((int) leftX, (int) textY,
				(int) (leftX + textBoundsWidth),
				(int) (textY + textBoundsHeight));

		SWI_DialogBackground dialogBackground = new SWI_DialogBackground(r2d,
				SWI_DialogBackground.SMALL_BACKGROUND);
		addSceneObject(dialogBackground);

		textY += textBoundsHeight * 0.5f;
		level = new SWI_Text("Level", leftX + textBoundsWidth * 0.5f, textY,
				SWI_Text.OPTION, true, true);
		// level.setDynamic(true);
		level.addTextListener(levelTextListener);
		addTouchEventListener(level);
		// addMouseListener(level);
		// addMouseMotionListener(level);
		addSceneObject(level);
		// checkMark = new CheckMark(leftX + 3.0d, textY + textBoundsHeight *
		// 0.0625, 6.0d, Color.RED);
		// addSceneObject(checkMark);
		textY += textBoundsHeight * 0.5f;
		exit = new SWI_Text("Exit", leftX + textBoundsWidth * 0.5f, textY,
				SWI_Text.EXIT, true, true);
		// exit.setDynamic(true);
		exit.addTextListener(exitTextListener);
		addTouchEventListener(exit);
		// addMouseListener(exit);
		// addMouseMotionListener(exit);
		addSceneObject(exit);
	}

	void setNumBalloonHit(int numBalloonHit) {
		scoreBoard.setText(Integer.toString(numBalloonHit));
	}

	// void setAccuracy(int accuracy) {
	// this.accuracy.setText(Integer.toString(accuracy) + "%");
	// }

}
