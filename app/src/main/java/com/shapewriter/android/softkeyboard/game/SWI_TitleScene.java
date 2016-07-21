/* TitleScreen.java
 * Created on Jun 6, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.util.Iterator;
import java.util.Vector;

import android.content.Context;
import android.graphics.Rect;

/**
 * 
 * @author pkriste
 */
class SWI_TitleScene extends SWI_Scene {

	private Vector<SWI_TitleSceneListener> titleSceneListeners = new Vector<SWI_TitleSceneListener>();
	private SWI_Text practiceGame;
	private SWI_Text exitGame;

	private SWI_TextListener practiceGameTextListener = new SWI_TextListener() {
		public void textClicked() {
			deregister(SWI_Text.PRACTICE_GAME);

			for (int i = 0; i < titleSceneListeners.size(); i++) {
				SWI_TitleSceneListener tl = titleSceneListeners.get(i);
				tl.practiceGameSelected();
			}
		}
	};

	private SWI_TextListener exitGameTextListener = new SWI_TextListener() {
		public void textClicked() {
			deregister(SWI_Text.EXIT_GAME);
			for (Iterator<SWI_TitleSceneListener> i = titleSceneListeners
					.iterator(); i.hasNext();) {
				i.next().exitGameSelected();
			}
		}
	};

	SWI_TitleScene(Context context, Rect bounds,
			SWI_SceneControllerView sceneControllerView) {
		super(context, bounds, sceneControllerView);
		setupScreen();
		setFadeIn();
	}

	private void deregister(int type) {
		if (type == SWI_Text.PRACTICE_GAME) {
			setFadeOut(true);
			// removeTouchEventListener(practiceGame);
		} else {
			setFadeOut(true);
			// removeTouchEventListener(exitGame);
		}
	}

	void addTitleSceneListener(SWI_TitleSceneListener titleSceneListener) {
		titleSceneListeners.add(titleSceneListener);
	}

	void removeTitleSceneListener(SWI_TitleSceneListener titleSceneListener) {
		titleSceneListeners.remove(titleSceneListener);
	}

	private void setupScreen() {
		float x = bounds.left + bounds.width() * 0.05f;
		float y = bounds.left + bounds.height() * 0.15f;
		float width = bounds.width() * 0.9f;
		float height = bounds.height() * 0.8f;

		SWI_DialogBackground db = new SWI_DialogBackground(bounds, (int) width,
				(int) height, SWI_DialogBackground.MAIN_BACKGROUND);
		addSceneObject(db);

		float xOffset = x + width * 0.5f;
		float intervalV = height * 0.30f;
		float yOffset = y + intervalV;

		practiceGame = new SWI_Text("Practice Game", xOffset, yOffset,
				SWI_Text.PRACTICE_GAME, true, true);
		yOffset += intervalV;
		// practiceGame.setDynamic(true);

		addTouchEventListener(practiceGame);

		// challengeGame = new Text("Challenge Contest", xOffset, yOffset,
		// Color.WHITE, true, true);
		yOffset += intervalV * 0.8f;
		// challengeGame.setDynamic(true);

		exitGame = new SWI_Text("Exit Game", xOffset, yOffset,
				SWI_Text.EXIT_GAME, true, true);
		// exitGame.setDynamic(true);

		// addMouseListener(practiceGame);
		// addMouseMotionListener(practiceGame);
		// addMouseListener(challengeGame);
		// addMouseMotionListener(challengeGame);
		// addMouseListener(exitGame);
		// addMouseMotionListener(exitGame);
		practiceGame.addTextListener(practiceGameTextListener);
		// challengeGame.addTextListener(challengeGameTextListener);
		exitGame.addTextListener(exitGameTextListener);

		addTouchEventListener(exitGame);

		addSceneObject(practiceGame);
		// addSceneObject(challengeGame);
		addSceneObject(exitGame);
	}

}
