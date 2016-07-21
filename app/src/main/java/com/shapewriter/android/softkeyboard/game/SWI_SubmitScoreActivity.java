/* 
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shapewriter.android.softkeyboard.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shapewriter.android.softkeyboard.R;

/**
 * An activity that will edit the title of a note. Displays a floating window
 * with a text field.
 */
public class SWI_SubmitScoreActivity extends Activity {

	/**
	 * The EditText field from our UI. Keep track of this so we can extract the
	 * text when we are finished.
	 */
	private EditText mText;
	private int mCurrentScore = -1;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.submit_score);

		String initText = null;
		Bundle extras = getIntent().getExtras();
		String hintText = null;
		String title = null;
		if (extras != null) {
			title = extras.getString("title");
			initText = extras.getString("init_text");
			if (title != null) {
				setTitle(title);
			}

			if (extras.containsKey("score"))
				mCurrentScore = extras.getInt("score");

			if (extras.containsKey("hint"))
				hintText = extras.getString("hint");
		}

		mText = (EditText) this.findViewById(R.id.name);

		if (initText != null) {
			mText.setText(initText);
		}

		if (hintText != null) {
			mText.setHint(hintText);
		}

		// mText.setOnClickListener(this);
		mText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keycode, KeyEvent e) {
				if (e.getAction() == KeyEvent.ACTION_DOWN
						&& e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					submitScore();
					return true;
				}
				return false;
			}
		});

		Button b = (Button) findViewById(R.id.name_ok);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				submitScore();
			}
		});
	}

	private void submitScore() {
		if (mText.getText().toString().trim().length() == 0) {
			Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT);
			return;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(makeSubmitScoreUri());
		startActivity(intent);
		finish();
	}

	private Uri makeSubmitScoreUri() {
		final long dt = System.currentTimeMillis();
		String urlStr = "http://www.shapewriter.com/onlinesale/scoreboard.jsp?";
		urlStr += "sc=" + mCurrentScore;
		urlStr += "&dt=" + dt;
		urlStr += "&jc=" + getCorrectHashCode(String.valueOf(mCurrentScore));
		urlStr += "&md=" + getCorrectHashCode(String.valueOf(dt));
		urlStr += "&imei=" + getDeviceId();
		urlStr += "&sis=" + getCorrectHashCode(getDeviceId());
		urlStr += "&pl=" + "android_g1";
		urlStr += "&un=" + mText.getText().toString().trim();
		return Uri.parse(urlStr);
	}

	private static String mStrImei = null;

	private final String getDeviceId() {
		if (mStrImei == null) {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			mStrImei = mTelephonyMgr.getDeviceId();
		}
		return mStrImei;
	}

	private final long getCorrectHashCode(final String strImei) {
		String key = strImei;
		long active_code = 0;
		char arr[] = key.toCharArray();
		long val = 0;
		int i = 0;
		long num = key.length();
		long tmp = 0;
		char ptr_int;

		while (i < num) {
			ptr_int = arr[i];
			val = (val << 4) + ptr_int;
			tmp = val & 0xf0000000;
			if (tmp != 0) {
				val = val ^ (tmp >> 24);
				val = val ^ tmp;
			}
			i++;
		}

		active_code = val % 200663319;
		if (active_code < 0)
			active_code = 0 - active_code;

		return active_code;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mText.requestFocus();

		if (mCurrentScore >= 0) {
			SharedPreferences prefs = getPreferences(0);
			String restoredText = prefs.getString("name", null);
			if (restoredText != null)
				mText.setText(restoredText, TextView.BufferType.EDITABLE);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mCurrentScore >= 0) {
			SharedPreferences.Editor editor = getPreferences(0).edit();
			editor.putString("name", mText.getText().toString());
			editor.commit();
		}
	}
}
