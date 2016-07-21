/* BalloonGame.java
 * Created on Jun 7, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.shapewriter.android.softkeyboard.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * 
 * @author pkriste
 */
public class SWI_PracticeBalloonGameMode implements SWI_GameMode {

	private SWI_PracticeBalloonScene balloonScene;
	private SWI_GamePlayStatsScene gamePlayStatsScene;
	private int currentScore = 0;
	private int numWordsEntered = 0;
	private SWI_SceneController sceneController;
	private SWI_EriScheduler eriScheduler;
	private SWI_PracticeGameState practiceGameState;
	private String eriDataFilename = "/sdcard/tmp/eridata.bin";
	private String gameDataFilename = "/sdcard/tmp/practicegamedata.bin";
	private boolean stateSaved = false;

	private Context context;

	private SWI_PracticeBalloonSceneListener balloonSceneListener = new SWI_PracticeBalloonSceneListener() {
		public void balloonHit(SWI_EriItem eriItem) {
			updateGamePlayStatistics(eriItem, true, false);
		}

		public void missedWord(SWI_EriItem eriItem) {
			updateGamePlayStatistics(eriItem, false, false);
		}

		public void outScreen(SWI_EriItem eriItem) {
			updateGamePlayStatistics(eriItem, false, true);
		}
	};

	private SWI_GamePlayStatsSceneListener gamePlayStatsSceneListener = new SWI_GamePlayStatsSceneListener() {
		public void gamePlayStatsSceneDismissed() {
			showExitOptions();
		}

		public void setOption() {
			balloonScene.setOption();
		}
	};

	protected void setPaused(boolean pause) {
		if (balloonScene != null)
			balloonScene.setPause(pause);
	}

	private void showExitOptions() {
		balloonScene.setPause(true);

		new AlertDialog.Builder(context).setTitle(
				"Current Score: " + currentScore + " pts.").setItems(
				R.array.game_exit_options,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (whichButton == 0) {
							balloonScene.setPause(false);
						} else if (whichButton == 1) {
							// submit this score
							submitCurrentScore();
						} else if (whichButton == 2) {
							// check score board
							getScoreBoardList();
						} else if (whichButton == 3) {
							// discard and restart game
							resetGameState();
							balloonScene.setPause(false);
						} else if (whichButton == 4) {
							// exit game
							exitGame();
						}
					}
				}).setCancelable(false).create().show();
	}

	private void submitCurrentScore() {
		exitGame();

		Intent it = new Intent(context, SWI_SubmitScoreActivity.class);
		it.putExtra("title", "Current Score: " + currentScore + " pts.");
		it.putExtra("hint", "Please enter your name");
		it.putExtra("score", currentScore);
		((Activity) context).startActivity(it);
	}

	private void getScoreBoardList() {
		exitGame();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(makeGetScoreboardUri());
		((Activity) context).startActivity(intent);
	}

	private void exitGame() {
		saveEriState();
		balloonScene.setFadeOut(false);
		if (gameModeSwitcher != null) {
			gameModeSwitcher.switchGameMode(new SWI_TitleGameMode(context,
					sceneController), true);
		}
	}

	private Uri makeGetScoreboardUri() {
		return Uri.parse("http://www.shapewriter.com/onlinesale/scorelist.php");
	}

	private SWI_GameModeSwitcher gameModeSwitcher;
	private InputStream gameLexiconStream = null;

	public SWI_PracticeBalloonGameMode(Context context,
			SWI_SceneController sceneController, InputStream gameLexiconStream) {
		this.sceneController = sceneController;
		this.gameLexiconStream = gameLexiconStream;
		this.context = context;
		init();
	}

	public void saveEriState() {
		if (eriScheduler == null || stateSaved)
			return;

		balloonScene.removeMe();
		gamePlayStatsScene.setFadeOut(true);

		/* Save the ERI state */
		eriScheduler.saveState(eriDataFilename);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(gameDataFilename));
			oos.writeObject(practiceGameState);
			oos.flush();
			oos.close();
			stateSaved = true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public void handleUserInput(SWI_UserInput userInput) {
		if (balloonScene != null) {
			balloonScene.handleUserInput(userInput.getString(), userInput
					.getTraceTime());
		}
	}

	public void setGameModeSwitcher(SWI_GameModeSwitcher gameModeSwitcher) {
		this.gameModeSwitcher = gameModeSwitcher;
	}

	private void init() {
		if (new File(gameDataFilename).exists()
				&& new File(eriDataFilename).exists()) {
			new AlertDialog.Builder(context).setIcon(R.drawable.icon24)
					.setTitle("Balloon Game").setMessage(
							"Continue from last time?").setPositiveButton(
							"Yes", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									initGame(true);
								}
							}).setNeutralButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									initGame(false);
								}
							}).setCancelable(false).show();
		} else {
			initGame(false);
		}
	}

	private void initGame(boolean bContinueFromLast) {
		boolean succeeded = false;
		if (bContinueFromLast) {
			try {
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(gameDataFilename));
				practiceGameState = (SWI_PracticeGameState) ois.readObject();
				ois.close();
				succeeded = true;
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}

			if (succeeded) {
				currentScore = practiceGameState.getCurrentScore();
				numWordsEntered = practiceGameState.getNumWordsEntered();
				try {
					eriScheduler = new SWI_EriScheduler(gameLexiconStream,
							eriDataFilename);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				practiceGameState = new SWI_PracticeGameState();
				eriScheduler = new SWI_EriScheduler(gameLexiconStream, "");
			}
		} else {
			practiceGameState = new SWI_PracticeGameState();
			eriScheduler = new SWI_EriScheduler(gameLexiconStream, "");
		}

		balloonScene = new SWI_PracticeBalloonScene(context, sceneController
				.getPaintableBounds(), sceneController, eriScheduler);
		balloonScene.addBalloonSceneListener(balloonSceneListener);
		sceneController.addScene(balloonScene);

		gamePlayStatsScene = new SWI_GamePlayStatsScene(context,
				sceneController.getPaintableBounds(), sceneController);
		if (gamePlayStatsScene != null && succeeded) {
			gamePlayStatsScene.setNumBalloonHit(currentScore);
			// double accuracy = (double) currentScore
			// / (double) numWordsEntered;
			// accuracy *= 100;
			// gamePlayStatsScene.setAccuracy((int) accuracy);
		}

		gamePlayStatsScene
				.addGamePlayStatsSceneListener(gamePlayStatsSceneListener);
		sceneController.addScene(gamePlayStatsScene);
	}

	public void deinitGame() {
		saveEriState();
	}

	public void resetGameState() {
		balloonScene.clearBalloons();
		currentScore = 0;
		numWordsEntered = 0;
		eriScheduler.clearEriList();
	}

	private void updateGamePlayStatistics(SWI_EriItem ei, boolean balloonIsHit,
			boolean outScreen) {
		if (balloonIsHit) {
			int show_time = ei.getShowTime();
			boolean validSpeed = ei.isValidSpeed();

			if (show_time == 0) {
				if (validSpeed)
					currentScore++;
			} else if (show_time == 1) {
			} else if (show_time == 2) {
				if (!validSpeed)
					currentScore--;
			} else if (show_time == 3) {
				if (validSpeed)
					currentScore++;
			}
		} else if (outScreen) {
			int show_time = ei.getShowTime();
			if (show_time == 2) {
				currentScore--;
			}
		} else {
			// missed word
		}

		if (currentScore < 0)
			currentScore = 0;

		numWordsEntered++;
		practiceGameState.setCurrentScore(currentScore);
		practiceGameState.setNumWordsEntered(numWordsEntered);
		if (gamePlayStatsScene != null) {
			gamePlayStatsScene.setNumBalloonHit(currentScore);
			// double accuracy = (double) currentScore
			// / (double) numWordsEntered;
			// accuracy *= 100;
			// gamePlayStatsScene.setAccuracy((int) accuracy);
		}
	}
}
