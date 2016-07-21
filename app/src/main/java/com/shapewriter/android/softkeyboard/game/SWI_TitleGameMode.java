/* TitleGameMode.java
 * Created on Jun 7, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import android.content.Context;

/**
 * 
 * @author pkriste
 */
class SWI_TitleGameMode implements SWI_GameMode {

	private SWI_SceneController sceneController;;
	private SWI_GameModeSwitcher gameModeSwitcher;
	private Context context;
	private SWI_GameMode gameMode = null;

	private SWI_TitleSceneListener titleSceneListener = new SWI_TitleSceneListener() {
		public void practiceGameSelected() {
			if (gameModeSwitcher != null) {
				if (gameMode == null) {
					gameMode = new SWI_PracticeBalloonGameMode(context,
							sceneController, gameModeSwitcher
									.getGameLexiconInputStream());
				}
				gameModeSwitcher.switchGameMode(gameMode, false);
			}
		}

		public void challengeGameSelected() {
		}

		public void exitGameSelected() {
			if (gameModeSwitcher != null) {
				gameModeSwitcher.sendDeactivateSignal();
			}
		}
	};

	SWI_TitleGameMode(Context context, SWI_SceneController sceneController) {
		// Log.e("TitleGameMode", "TitleGameMode constructor is calling");

		this.sceneController = sceneController;
		SWI_TitleScene titleScene = new SWI_TitleScene(context,
				this.sceneController.getPaintableBounds(), this.sceneController);
		titleScene.addTitleSceneListener(titleSceneListener);
		this.sceneController.addScene(titleScene);
		this.context = context;
	}

	public void setGameModeSwitcher(SWI_GameModeSwitcher gameModeSwitcher) {
		this.gameModeSwitcher = gameModeSwitcher;
	}

	public void handleUserInput(SWI_UserInput userInput) {
		// Empty implementation
	}
}
