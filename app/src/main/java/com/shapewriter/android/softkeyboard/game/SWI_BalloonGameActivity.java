package com.shapewriter.android.softkeyboard.game;

import java.io.InputStream;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.MenuItem;
import android.view.WindowManager;
import android.content.Context;


import com.shapewriter.android.softkeyboard.R;
import com.shapewriter.android.softkeyboard.SWI_PageEnglishTrace;


public class SWI_BalloonGameActivity extends Activity {

	private SWI_BalloonGameView mBallGame = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.balloon_game);
		setTitle(R.string.game_title);
		
		SWI_GameTextEditor textEditor = (SWI_GameTextEditor) findViewById(R.id.gameInputEditor);

		mBallGame = (SWI_BalloonGameView) findViewById(R.id.mainGameEntryBox);
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);   	 	
		if(480 == wm.getDefaultDisplay().getWidth()){
			mBallGame.mHeight = 390;
		} else if (320 == wm.getDefaultDisplay().getWidth()){
			mBallGame.mHeight = 213;
		} else {
			mBallGame.mHeight = 106;
		}
		SetGame();
		textEditor.setGameView(mBallGame);
		textEditor.setPressed(true);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	@Override
	protected void onPause() {
		if (mBallGame != null)
			mBallGame.pause(true);

		super.onPause();
	}

	@Override
	protected void onResume() {
		if (mBallGame != null)
			mBallGame.pause(false);

		super.onResume();
	}

	public void SetGame() {
		mBallGame.setCallback(new SWI_GameCanvas.Callback() {
			public void deactivateGameCanvas() {
				finish();
			}

			public InputStream sendGameLexiconInputStream() {
				java.io.InputStream stream = getResources().openRawResource(
						R.raw.gamelexicon);
				return stream;
			}
		});
	}

	public static final int STOP_GAME_ID = Menu.FIRST;
	public static final String STOP_GAME = "Exit Game";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add(0, STOP_GAME_ID, 0, STOP_GAME).setShortcut('0',
		// 's').setIcon(
		// R.drawable.quit_game);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case STOP_GAME_ID:
			finish();
			break;
		}

		return true;
	}

}