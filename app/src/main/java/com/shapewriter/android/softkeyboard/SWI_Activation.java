package com.shapewriter.android.softkeyboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.provider.Settings;

public class SWI_Activation {
	private Context mContext;
	private Context mAppContext;
	private String mStrActivationCode = null;

	private Handler mDialogHandler = new Handler();
	
	private static SWI_Activation instance = null;

	public static SWI_Activation getInstance(Context context) {
		if (instance == null) {
			instance = new SWI_Activation(context);
		} else {
			instance.mContext = context;
		}

		if (context instanceof Activity) {
			instance.mAppContext = context;
		}

		return instance;
	}

	private SWI_Activation(Context context) {
		
	}

	public void register() {
/*
		long installMillis = getAppInstallDateTime();
		if (installMillis == 0) {
			// error
		}

		boolean bIsRegisteredUserValid = isRegisteredUserValid();

		if (!bIsRegisteredUserValid) {
			mDialogHandler.post(new Runnable() {
				public void run() {
					checkActivationCode();
				}
			});
		}

		mbExpired = System.currentTimeMillis() - installMillis > TRAIL_DURATION;
		if (!bIsRegisteredUserValid && mbExpired) {
			mDialogHandler.post(new Runnable() {
				public void run() {
					expired();
				}
			});
		}
*/
	}

	private void checkActivationCode() {
/*
		LayoutInflater factory = LayoutInflater.from(mContext);
		final View textEntryView = factory.inflate(
				R.layout.alert_dialog_input_activation_code, null);

		final TextView textDeviceId = (TextView) textEntryView
				.findViewById(R.id.device_id_edit);
		final TextView textActivationCode = (TextView) textEntryView
				.findViewById(R.id.activation_code_edit);

		textDeviceId.setText(mStrImei);
		textDeviceId.setEnabled(false);
		textActivationCode.requestFocus();

		new AlertDialog.Builder(mAppContext == null ? mContext : mAppContext)
				.setIcon(R.drawable.icon24).setTitle("ShapeWriter").setView(
						textEntryView).setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								boolean validCode = false;
								int code = 0;
								mStrActivationCode = textActivationCode
										.getText().toString().trim();
								try {
									code = Integer.parseInt(mStrActivationCode);
									validCode = true;
								} catch (NumberFormatException e) {
								} finally {
									if (validCode
											&& code == getCorrectActivationCode(mStrImei)) {
										activated();
										updateRegisteredUserValid(true);
									} else {
										invalidActivation();
									}
								}
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (mbExpired) {
									// mService.requestHideSelf(0);
								}
							}
						}).show();
*/
	}

	private void activated() {
/*
		if (mAppContext != null && mAppContext instanceof SWI_IMESettings) {
			mDialogHandler.post(new Runnable() {
				public void run() {
					((SWI_IMESettings) mAppContext).activated();
				}
			});
		}

		new AlertDialog.Builder(mAppContext == null ? mContext : mAppContext)
				.setIcon(R.drawable.icon24).setTitle("ShapeWriter").setMessage(
						"Thanks for choosing ShapeWriter!").setPositiveButton(
						"OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create().show();
*/
	}

	private void invalidActivation() {
/*
		new AlertDialog.Builder(mAppContext == null ? mContext : mAppContext)
				.setIcon(R.drawable.icon24).setTitle("ShapeWriter").setMessage(
						"Invalid activation code, do you want to retry?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								register();
							}
						}).setNeutralButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (mbExpired) {
									expired();
								}
							}
						}).create().show();
*/
	}

	
	private final int getCorrectActivationCode(final String strImei) {
/*
		String key = strImei;
		int active_code = 0;
		char arr[] = key.toCharArray();
		int val = 0;
		int i = 0;
		int num = key.length();
		int tmp = 0;
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

		active_code = val % 100663319;
		if (active_code < 0)
			active_code = 0 - active_code;

		return active_code;
*/
		return 1;
	}

}
