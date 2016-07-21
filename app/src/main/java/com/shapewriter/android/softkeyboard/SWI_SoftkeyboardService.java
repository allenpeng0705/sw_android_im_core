package com.shapewriter.android.softkeyboard;

import java.util.HashMap;

import com.shapewriter.android.softkeyboard.game.SWI_BalloonGameActivity;
import com.shapewriter.android.softkeyboard.SWI_MisspellingTable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.util.Log;

public class SWI_SoftkeyboardService extends InputMethodService {
	public static final String APP_FILE_PATH = "/data/data/com.shapewriter.android.softkeyboard/files";

	public static final int DEFAULT_SKIN_VERSION = 10;
	public static final int DEFAULT_LANGUAGE_VERSION = 11;
	public static final String DEFAULT_SKIN_NAME = "default_skin";

	private static final String PREF_VIBRATE_ON = "vibrate_on";
	private static final String PREF_SOUND_ON = "sound_on";
	private static final String PREF_AUTO_EDIT = "auto_cap";
	private static final String PREF_IDEAL_SHAPE = "ideal_shape";

	private static final long VIBRATE_DURATION = 40;
	private static final float FX_VOLUME = 1.0f;
	private static final int VERSION_CODE = 36;

	private Vibrator mVibrator;
	private AudioManager mAudioManager;

	private boolean mSilentMode;
	private boolean mVibrateOn;
	private boolean mSoundOn;
	private boolean mDisableAutoEdit;
	private boolean mShowIdealShape = true;
	private boolean mRCOHasMap1 = false;
	private boolean mRCOHasMap2 = false;
	private boolean mJustPaste = false;	

	private int mPageNum = 0;
	private String mFirstLanguage;
	private String mSecondLanguage;

	public SWI_KeyboardViewSet mKeyboardViewSet;
	private SWI_PageManager[] mPage;
	private SWI_PageManager mPageCur;
	private SWI_PageManager mPageLast;
	private Layout mLayoutPort;
	private Layout mLayoutLand;
	private Layout mLayoutCur;

	private SWI_LanguageResolver mLanguageResolver;
	private SWI_SkinResolver mSkinResolver;
	private SWI_LanguageSetting mLanguageSetting;
	// private ContactsContent mContactObserver;
	// private ContactsContent mUserDictObserver;

	static {
		System.loadLibrary("shapewriter");
	}

	@Override
	public void onCreate() {
		try {
			/*
			 * if (DEBUG) Log.e("SWI_SoftkeyboardService", "onCreate");
			 */
	
			super.onCreate();
			
			// Handler contact_handle = new Handler();
			// Handler user_dict_handle = new Handler();
			// mContactObserver = new ContactsContent(contact_handle);
			// mUserDictObserver = new ContactsContent(user_dict_handle);
	
			// getApplicationContext().getContentResolver().registerContentObserver(People.CONTENT_URI,true,
			// mContactObserver);
			// getApplicationContext().getContentResolver().registerContentObserver(UserDictionary.Words.CONTENT_URI,true,
			// mUserDictObserver);
	
			IntentFilter ringerFilter = new IntentFilter(
					AudioManager.RINGER_MODE_CHANGED_ACTION);
			registerReceiver(mRingerModeReceiver, ringerFilter);
			SWI_UtilSingleton.instance().setContext(this);
			SWI_UtilSingleton.instance().logCurrentVersion(VERSION_CODE);
	
			IntentFilter languageUpdateFilter = new IntentFilter("language update");
			registerReceiver(mLanguageUpdateReceiver, languageUpdateFilter);
	
			mLanguageResolver = new SWI_LanguageResolver(this);
			mSkinResolver = new SWI_SkinResolver(this);
	
			/**
			 * Update Default Skin Data
			 */
			if (!mSkinResolver.exist(DEFAULT_SKIN_NAME)) {
				SWI_SkinFileIO.writeAll(this);
				mSkinResolver.insert(DEFAULT_SKIN_NAME, APP_FILE_PATH,
						DEFAULT_SKIN_VERSION, null);
			} else if (mSkinResolver.getVersion(DEFAULT_SKIN_NAME) < DEFAULT_SKIN_VERSION) {
				SWI_SkinFileIO.writeAll(this);
				mSkinResolver.update(DEFAULT_SKIN_NAME, APP_FILE_PATH,
						DEFAULT_SKIN_VERSION, null);
			}
	
			/**
			 * Update Default Language (English and Number) Data
			 */
			if (!mLanguageResolver.exist(SWI_Language.ENGLISH)) {
				SWI_LanguageFileIO.writeAll(this, SWI_Language.ENGLISH);
				mLanguageResolver.insert(SWI_Language.ENGLISH, APP_FILE_PATH,
						DEFAULT_LANGUAGE_VERSION, null);
			} else if (mLanguageResolver.getVersion(SWI_Language.ENGLISH) < DEFAULT_LANGUAGE_VERSION) {
				SWI_LanguageFileIO.writeAll(this, SWI_Language.ENGLISH);
				mLanguageResolver.update(SWI_Language.ENGLISH, APP_FILE_PATH,
						DEFAULT_LANGUAGE_VERSION, null);
			}
	
			if (!mLanguageResolver.exist(SWI_Language.NUMBER)) {
				SWI_LanguageFileIO.writeAll(this, SWI_Language.NUMBER);
				mLanguageResolver.insert(SWI_Language.NUMBER, APP_FILE_PATH,
						DEFAULT_LANGUAGE_VERSION, null);
			} else if (mLanguageResolver.getVersion(SWI_Language.NUMBER) < DEFAULT_LANGUAGE_VERSION) {
				SWI_LanguageFileIO.writeAll(this, SWI_Language.NUMBER);
				mLanguageResolver.update(SWI_Language.NUMBER, APP_FILE_PATH,
						DEFAULT_LANGUAGE_VERSION, null);
			}
			mLanguageSetting = new SWI_LanguageSetting(this, mLanguageResolver);
			mFirstLanguage = mLanguageSetting.getFirstLanguage();
			mSecondLanguage = mLanguageSetting.getSecondLanguage();
			SWI_UtilSingleton.instance().logCurrentLanguage(mFirstLanguage);
			if ((mFirstLanguage != null) && mFirstLanguage.equalsIgnoreCase("English")) {
				SWI_MisspellingTable.instance().setEnglishRunning(true);
				SWI_MisspellingTable.instance().init(this);
			} else if ((mSecondLanguage != null) && mSecondLanguage.equalsIgnoreCase("English")) {
				SWI_MisspellingTable.instance().setEnglishRunning(false);
				SWI_MisspellingTable.instance().init(this);
			} else {
				SWI_MisspellingTable.instance().setEnglishRunning(false);
			}
			mKeyboardViewSet = new SWI_KeyboardViewSet(this);
			SWI_RCOSet.instance().init(SWI_SoftkeyboardService.this, 
					mLanguageResolver, mFirstLanguage, mSecondLanguage);
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "onCreate Exception");
			destroy();
		}
	}

	@Override
	public void onDestroy() {
		destroy();
	}

	@Override
	public void onInitializeInterface() {
		try {
			/*
			 * if (DEBUG) Log.e("SWI_SoftkeyboardService", "onInitializeInterface");
			 */
	
			/**
			 * Initialize Layout
			 */
			if (isPortrait()) {
				
				if (mLayoutPort == null) {
					mLayoutPort = new Layout();
	
					mLayoutPort.pageList.add(SWI_DataIntegration.getPageBase(
							mLanguageResolver.getDirectory(mFirstLanguage),
							mSkinResolver.getDirectory(DEFAULT_SKIN_NAME),
							mFirstLanguage, true));
	
					if (mSecondLanguage != null) {
						mLayoutPort.pageList.add(SWI_DataIntegration.getPageBase(
								mLanguageResolver.getDirectory(mSecondLanguage),
								mSkinResolver.getDirectory(DEFAULT_SKIN_NAME),
								mSecondLanguage, true));
					}
	
					mLayoutPort.pageList.add(SWI_DataIntegration.getPageBase(
							mLanguageResolver.getDirectory(SWI_Language.NUMBER),
							mSkinResolver.getDirectory(DEFAULT_SKIN_NAME),
							SWI_Language.NUMBER, true));
	
					mLayoutCur = mLayoutPort;
					if (!mRCOHasMap1 && mFirstLanguage != null) {
						SWI_RCOSet.instance().map(mFirstLanguage,
								mLayoutPort.pageList.get(0).keyboard);
						mRCOHasMap1 = true;
					}
					if (!mRCOHasMap2 && mSecondLanguage != null) {
						SWI_RCOSet.instance().map(mSecondLanguage,
								mLayoutPort.pageList.get(1).keyboard);
						mRCOHasMap2 = true;
					}
				}
	
				if (mLayoutCur == mLayoutLand) {
					mLayoutCur = mLayoutPort;
				}
			} else {
				if (mLayoutLand == null) {
					mLayoutLand = new Layout();
					mLayoutLand.pageList.add(SWI_DataIntegration.getPageBase(
							mLanguageResolver.getDirectory(mFirstLanguage),
							mSkinResolver.getDirectory(DEFAULT_SKIN_NAME),
							mFirstLanguage, false));
	
					if (mSecondLanguage != null) {
						mLayoutLand.pageList.add(SWI_DataIntegration.getPageBase(
								mLanguageResolver.getDirectory(mSecondLanguage),
								mSkinResolver.getDirectory(DEFAULT_SKIN_NAME),
								mSecondLanguage, false));
					}
	
					mLayoutLand.pageList.add(SWI_DataIntegration.getPageBase(
							mLanguageResolver.getDirectory(SWI_Language.NUMBER),
							mSkinResolver.getDirectory(DEFAULT_SKIN_NAME),
							SWI_Language.NUMBER, false));
	
					mLayoutCur = mLayoutLand;
					if (!mRCOHasMap1 && mFirstLanguage != null) {
						SWI_RCOSet.instance().map(mFirstLanguage,
								mLayoutLand.pageList.get(0).keyboard);
						mRCOHasMap1 = true;
					}
					if (!mRCOHasMap2 && mSecondLanguage != null) {
						SWI_RCOSet.instance().map(mSecondLanguage,
								mLayoutLand.pageList.get(1).keyboard);
						mRCOHasMap2 = true;
					}
				}
	
				if (mLayoutCur == mLayoutPort) {
					mLayoutCur = mLayoutLand;
				}
			}
	
			/**
			 * Initialize Page Manager
			 */
			if (mPage == null) {
				mPageNum = mLayoutCur.pageList.size();
				mPage = new SWI_PageManager[mPageNum];
			}
			for (int i = 0; i < mPageNum; i++) {
				if (mPage[i] != null) {
					mPage[i].removeAllViews();
				}
				SWI_PageBase pageBase = mLayoutCur.pageList.get(i);
	
				if (pageBase.name.equals(SWI_PageBase.NAME_CHN_QWERTY_LAND)
						|| pageBase.name.equals(SWI_PageBase.NAME_CHN_QWERTY_PORT)) {
					mPage[i] = new SWI_PageChineseTrace(this);
					mPage[i].setPageBase(pageBase);
					if (pageBase.traceable) {
						// mPage[i].setRCO(SWI_RCOSet.instance(RCO.LANGUAGE_CHINESE).getRCO());
					}
				} else if (pageBase.name.equals(SWI_PageBase.NAME_ENG_QWERTY_LAND)
						|| pageBase.name.equals(SWI_PageBase.NAME_ENG_QWERTY_PORT)) {
					mPage[i] = new SWI_PageEnglishTrace(this);
					mPage[i].setPageBase(pageBase);
					if (pageBase.traceable) {
						if (i == 0) {
							mPage[i].setRCO(SWI_RCOSet.instance(), SWI_RCOSet.instance().getRCO(mFirstLanguage));
							SWI_RCOSet.instance().setPageManager(mFirstLanguage, mPage[i]);
							mPage[i].setCmdStrokes(SWI_RCOSet.instance()
									.getCommandStrokes(mFirstLanguage));
						} else if (mSecondLanguage != null && i == 1) {
							mPage[i].setRCO(SWI_RCOSet.instance(), SWI_RCOSet.instance()
									.getRCO(mSecondLanguage));
							SWI_RCOSet.instance().setPageManager(mSecondLanguage, mPage[i]);
							mPage[i].setCmdStrokes(SWI_RCOSet.instance()
									.getCommandStrokes(mSecondLanguage));
						}
					}
				} else if (pageBase.name.equals(SWI_PageBase.NAME_NUMBER_LAND)
						|| pageBase.name.equals(SWI_PageBase.NAME_NUMBER_PORT)) {
					mPage[i] = new SWI_PageTap(this);
					mPage[i].setPageBase(pageBase);
				}
			}
			mPageCur = mPage[0];
			SWI_QuickDoubleClickHandle.instance().setPageManager(mPageCur);	
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "onInitializeInterface Exception");
			destroy();
		}
	}

	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		try {
			// Log.e("SWI_SoftkeyboardService", "onStartInput");
	
			if (!mLanguageResolver.exist()) return;
	
			mLanguageSetting.updateSelectedLanguage();
			String firstLanguage = mLanguageSetting.getFirstLanguage();
			String secondLanguage = mLanguageSetting.getSecondLanguage();
			
			boolean reboot = false;
			
			if (mFirstLanguage.equalsIgnoreCase(firstLanguage) == false) {
				reboot = true;
			} else {
				if (mSecondLanguage == null) {
					if (secondLanguage != null) reboot = true;			
				} else {
					if (secondLanguage == null) {
						reboot = true;
					} else {
						if (mSecondLanguage.equalsIgnoreCase(secondLanguage) == false) reboot = true;
					}
				}	
			}
			
			if (reboot == true) {
				destroy();
				return;
			}
	
			loadSettings();
	
			switch (attribute.inputType & EditorInfo.TYPE_MASK_CLASS) {
			case EditorInfo.TYPE_CLASS_NUMBER:
			case EditorInfo.TYPE_CLASS_DATETIME:
			case EditorInfo.TYPE_CLASS_PHONE:
				if (!mPageCur.mPageBase.name.equals(SWI_PageBase.NAME_NUMBER_LAND)
						&& !mPageCur.mPageBase.name
								.equals(SWI_PageBase.NAME_NUMBER_PORT)) {
					mPageCur.switchPage(SWI_PageBase.NUMBER);
				}
				break;
	
			case EditorInfo.TYPE_CLASS_TEXT:
				if (mPageCur.mPageBase.name.equals(SWI_PageBase.NAME_NUMBER_LAND)
						|| mPageCur.mPageBase.name
								.equals(SWI_PageBase.NAME_NUMBER_PORT)) {
					mPageCur.switchPage(SWI_PageBase.ALPHA);
				}
	
				int variation = attribute.inputType
						& EditorInfo.TYPE_MASK_VARIATION;
				if (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
						|| variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
						|| variation == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
						// || variation == EditorInfo.TYPE_TEXT_VARIATION_FILTER
						|| variation == EditorInfo.TYPE_TEXT_VARIATION_URI) {
					if (mDisableAutoEdit == false) {
						mDisableAutoEdit = true;
						mPageCur.setDisableAutoEdit(mDisableAutoEdit);
					}
				}
				break;
			}
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "onStartInput Exception");
			destroy();
		}
	}

	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		try {
			/*
			 * if (DEBUG) Log.e("SWI_SoftkeyboardService", "onStartInputView");
			 */
			
			if (SWI_UtilSingleton.instance().shouldShowExpiredInfo()) {
				SWI_UtilSingleton.instance().toastTrialInfo();
			}
			
			super.onStartInputView(info, restarting);
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "onStartInputView Exception");
			destroy();
		}
	}

	@Override
	public View onCreateInputView() {
		try {
			/*
			 * if (DEBUG) Log.e("SWI_SoftkeyboardService", "onCreateInputView");
			 */
	
			mPageCur.compose();
	
			return mPageCur;
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "onCreateInputView Exception");
			destroy();
			return null;
		}
	}
	
	@Override
	public View onCreateCandidatesView() {
		/*
		 * if (DEBUG) Log.e("SWI_SoftkeyboardService",
		 * "onCreateCandidatesView");
		 */

		return null;
	}

	@Override
	public void onFinishInput() {
		try {
			/*
			 * if (DEBUG) Log.e("SWI_SoftkeyboardService", "onFinishInput");
			 */
	
			super.onFinishInput();
			mPageCur.clear();
		} catch (Exception e){
			Log.e("SWI_SoftkeyboardService", "onFinishInput Exception");
			destroy();
		}
	}

	@Override
	public void onFinishInputView(boolean finishingInput) {
		try {
			/*
			 * if (DEBUG) Log.e("SWI_SoftkeyboardService", "onFinishInputView");
			 */
			SWI_UtilSingleton.instance().toastCancel();
			mPageCur.hidenminikeyboard();
			super.onFinishInputView(finishingInput);
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "onFinishInputView Exception");
			destroy();
		}
	}

//	@Override
//	public boolean onEvaluateInputViewShown() {
//		return true;
//	}

	@Override
	public void onUpdateSelection(int oldSelStart, int oldSelEnd,
			int newSelStart, int newSelEnd, int candidatesStart,
			int candidatesEnd) {
		try {
			super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
					candidatesStart, candidatesEnd);
	
			if (mJustPaste) {
				mJustPaste = false;
				return;
			}
			if (mPageCur.mKeyboardView.mIsPunctuation == true) return;
			mPageCur.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart,
					newSelEnd, candidatesStart, candidatesEnd);
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "onUpdateSelection Exception");
			destroy();
		}
	}

	private void destroy() {
		try {
			if (mRingerModeReceiver != null)
				unregisterReceiver(mRingerModeReceiver);
			if (mLanguageUpdateReceiver != null)
				unregisterReceiver(mLanguageUpdateReceiver);
			
			// if (mContactObserver != null) {
			// getApplicationContext().getContentResolver().unregisterContentObserver(mContactObserver);
			// }
			// if (mUserDictObserver != null) {
			// getApplicationContext().getContentResolver().unregisterContentObserver(mUserDictObserver);
			// }
			
	
			if (mKeyboardViewSet != null) {
				mKeyboardViewSet.destroy();
				mKeyboardViewSet = null;
			}
	
			if (mPage != null) {
				for (int i = 0; i < mPage.length; i++) {
					if (mPage[i] != null) {
						mPage[i].destroy();
						mPage[i] = null;
					}
				}
				mPage = null;
			}
	
			if (mLayoutPort != null) {
				mLayoutPort.destroy();
				mLayoutPort = null;
			}
			if (mLayoutLand != null) {
				mLayoutLand.destroy();
				mLayoutLand = null;
			}
	
			SWI_RCOSet.destroy();
			
			SWI_MisspellingTable.destroy();
			
			if (mLanguageResolver != null) {
				mLanguageResolver.destroy();
				mLanguageResolver = null;
			}
			if (mSkinResolver != null) {
				mSkinResolver.destroy();
				mSkinResolver = null;
			}
			if (mLanguageSetting != null) {
				mLanguageSetting.destroy();
				mLanguageSetting = null;
			}
			
			mPageCur = null;
			mPageLast = null;
			mLayoutCur = null;
	
			super.onDestroy();
			System.runFinalization();
			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "destroy Exception");
			System.runFinalization();
			android.os.Process.killProcess(android.os.Process.myPid());			
		}
	}

	private boolean isPortrait() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			return true;
		else
			return false;
	}

	public void switchPage(int type) {
		try {
			SWI_UtilSingleton.instance().toastCancel();
	
			if (type == SWI_PageBase.LANGUAGE) {
				if (mPageCur == mPage[mPageNum - 2]) {
					mPageCur = mPage[0];
					SWI_UtilSingleton.instance().toastMessageInstant(mFirstLanguage);
					SWI_UtilSingleton.instance().logCurrentLanguage(mFirstLanguage);
				} else {
					for (int i = 0; i < mPageNum - 2; i++) {
						if (mPageCur == mPage[i]) {
							mPageCur = mPage[i + 1];
							SWI_UtilSingleton.instance().toastMessageInstant(mSecondLanguage);
							SWI_UtilSingleton.instance().logCurrentLanguage(
									mSecondLanguage);
							break;
						}
					}
				}
				mPageCur.clear();
			} else if (type == SWI_PageBase.NUMBER) {
				mPageLast = mPageCur;
				mPageCur = mPage[mPageNum - 1];
			} else if (type == SWI_PageBase.ALPHA) {
				mPageCur = mPageLast;
				if (mPageCur == mPage[0]) {
					SWI_UtilSingleton.instance().toastMessageInstant(mFirstLanguage);
				} else if (mSecondLanguage != null) {
					SWI_UtilSingleton.instance().toastMessageInstant(mSecondLanguage);
				}
				mPageCur.clear();
			}
			mPageCur.compose();
			setInputView(mPageCur);
			
			if (SWI_UtilSingleton.instance().getCurrentLanguage().equalsIgnoreCase("English")) {
				SWI_MisspellingTable.instance().setEnglishRunning(true);
			} else {
				SWI_MisspellingTable.instance().setEnglishRunning(false);
			}
			
			SWI_QuickDoubleClickHandle.instance().setPageManager(mPageCur);
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "switchPage Exception");
			destroy();
		}
	}

	public void sendText(String str, int replaceNum) {
		try {
			ExtractedText extractedText = getExtractedText();
			if (extractedText == null) {
				return;
			}
			int idxEnd = extractedText.selectionEnd;
	
			InputConnection connection = getCurrentInputConnection();
			if (connection == null) {
				return;
			}
	
			if (str != null) {
				connection.setSelection(idxEnd - replaceNum, idxEnd);
				connection.commitText(str, 1);
			} else {
				connection.commitText("", 1);
				connection.deleteSurroundingText(replaceNum, 0);
			}
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "sendText Exception");
			destroy();
		}
	}

	public void handleCommandKey() {

	}

	public void caseKeyPressed() {
		try {
			ExtractedText extractedText = getExtractedText();
			if (extractedText == null || extractedText.text.length() == 0) {
				return;
			}
	
			String text = extractedText.text.toString();
	
			int sel_st = Math.min(extractedText.selectionStart,
					extractedText.selectionEnd);
			int sel_en = Math.max(extractedText.selectionStart,
					extractedText.selectionEnd);
	
			if (sel_st < 0)
				sel_st = 0;
			if (sel_en < 0)
				sel_en = 0;
	
			InputConnection connection = getCurrentInputConnection();
			Word w = getWordByPos(text, sel_st);
	
			if (sel_st == sel_en) {
				if (sel_en > 0 && w.mStart < sel_en && w.mEnd >= sel_en
						&& w.mEnd != text.length()) {
					char prechar = text.charAt(sel_en - 1
							+ extractedText.startOffset);
	
					connection.beginBatchEdit();
					connection.deleteSurroundingText(1, 0);
					connection.commitText(String.valueOf(Character
							.isUpperCase(prechar) ? Character.toLowerCase(prechar)
							: Character.toUpperCase(prechar)), 1);
					connection.endBatchEdit();
					return;
				} else {
					sel_st = w.mStart;
					sel_en = w.mEnd;
					sel_en += (sel_en == text.length() ? 0 : 1);
				}
			}
	
			String newStr = toggleCase(text, sel_st, sel_en);
	
			int delLeft = sel_en - sel_st;
			int delRight = 0;
			int selectionOffset = sel_en + extractedText.startOffset;
	
			connection.beginBatchEdit();
			connection.setSelection(selectionOffset, selectionOffset);
			connection.deleteSurroundingText(delLeft, delRight);
			connection.commitText(newStr, 1);
			connection.endBatchEdit();
		} catch (Exception e) {
			Log.e("SWI_SoftkeyboardService", "caseKeyPressed Exception");
			destroy();
		}
	}

	public void onPress() {
		vibrate();
		playKeyClick();
	}

	public void onRelease() {
		vibrate();
	}

	private void vibrate() {
		if (!mVibrateOn) {
			return;
		}
		if (mVibrator == null) {
			mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		}
		mVibrator.vibrate(VIBRATE_DURATION);
	}

	private BroadcastReceiver mRingerModeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateRingerMode();
		}
	};

	private void updateRingerMode() {
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		}
		if (mAudioManager != null) {
			mSilentMode = (mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL);
		}
	}

	private void playKeyClick() {
		if (mAudioManager == null) {
			updateRingerMode();
		}
		if (mSoundOn && !mSilentMode) {
			int sound = AudioManager.FX_KEYPRESS_STANDARD;
			mAudioManager.playSoundEffect(sound, FX_VOLUME);
		}
	}

	private BroadcastReceiver mLanguageUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			destroy();
		}
	};

	public void launchSettings() {
		Intent intent = new Intent();
		intent.setClass(SWI_SoftkeyboardService.this, SWI_IMESettings.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	public void launchHelp() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent
				.setData(Uri
						.parse("http://www.shapewriter.com/download/help/android_help/and_kb_help.html"));
		startActivity(intent);
	}

	public void launchVideo() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse("http://www.youtube.com/watch?v=MeTb7nPYlOA"));
		startActivity(intent);
	}

	public void launchGame() {
		if (isPortrait() && 
				(mPageCur.mKeyboardView.getWidth() > 300)
				&& (SWI_UtilSingleton.instance().
						getCurrentLanguage().equalsIgnoreCase("English"))) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, SWI_BalloonGameActivity.class);
			startActivity(intent);
		}	
	}

	public void launchCommondPage() {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(this, SWI_CommandPage.class);
		startActivity(intent);

	}

	public void handleLiteralMode() {
		mDisableAutoEdit = !mDisableAutoEdit;
		mPageCur.setDisableAutoEdit(mDisableAutoEdit);
		setAutoEditDiabled(mDisableAutoEdit);
	}

	private int mExtractedToken = 0;

	public ExtractedText getExtractedText() {
		InputConnection connection = getCurrentInputConnection();
		if (connection == null)
			return null;

		mExtractedToken++;
		ExtractedTextRequest req = new ExtractedTextRequest();
		// req.token = mExtractedToken;
		req.hintMaxLines = 10;
		req.hintMaxChars = 10000;
		ExtractedText extractedText = connection.getExtractedText(req, 0);

		return extractedText;
	}

	public class Word {
		public int mStart;
		public int mEnd;
	}

	public Word getWordByPos(String text, int pos) {
		Word result = new Word();

		String currStr = text;
		result.mStart = result.mEnd = pos;

		char c;

		for (int i = pos; i < currStr.length(); ++i) {
			c = currStr.charAt(i);

			if (!Character.isLetter(c)) {
				if (c == '\''
						&& (i > 0 && Character.isLetter(currStr.charAt(i - 1)))
						&& (i < currStr.length() - 1 && Character
								.isLetter(currStr.charAt(i + 1)))) {
				} else {
					break;
				}
			}
			result.mEnd = i;
		}

		if (pos > -1 && pos < currStr.length()) {
			c = currStr.charAt(pos);
			if (!Character.isLetter(c)) {
				if (c == '\''
						&& (pos > 0 && Character.isLetter(currStr
								.charAt(pos - 1)))
						&& (pos < currStr.length() - 1 && Character
								.isLetter(currStr.charAt(pos + 1)))) {

				} else {
					result.mEnd--;
				}
			}
		}

		for (int i = pos - 1; i >= 0; --i) {
			if (i == currStr.length()) {
				continue;
			}

			c = currStr.charAt(i);
			if (!Character.isLetter(c)) {
				if (c == '\''
						&& (i > 0 && Character.isLetter(currStr.charAt(i - 1)))
						&& (i < currStr.length() - 1 && Character
								.isLetter(currStr.charAt(i + 1)))) {
				} else {
					break;
				}
			}
			result.mStart = i;
		}
		// result.mEnd += (result.mEnd == currStr.length() ? 0 : 1);
		return result;
	}

	private class StringCase {
		private final static int eCaseDefault = 0;
		private final static int eCaseLower = 1;
		private final static int eCaseFirstUpper = 2;
		private final static int eCaseUpper = 3;

		private String word;
		private int currentCase;

		public StringCase(String word) {
			this.word = word;
			currentCase = eCaseDefault;
		}

		public void setCaseState(final String str) {
			String tmpStr = str;
			if (tmpStr.equals(word)) {
				if (tmpStr.equals(word.substring(0, 1).toUpperCase()
						+ word.substring(1).toLowerCase())) {
					if (currentCase == eCaseFirstUpper)
						currentCase = eCaseFirstUpper;
					else
						currentCase = eCaseDefault;
				} else {
					currentCase = eCaseDefault;
				}
			} else if (tmpStr.equals(word.toLowerCase())) {
				currentCase = eCaseLower;
			} else if (tmpStr.equals(word.toUpperCase())) {
				currentCase = eCaseUpper;
			} else if (tmpStr.equals(word.substring(0, 1).toUpperCase()
					+ word.substring(1).toLowerCase())) {
				currentCase = eCaseFirstUpper;
			}
		}

		public String nextCase() {
			currentCase = (currentCase + 1) % 4;
			String currStr = word;
			if (currentCase == eCaseFirstUpper) {
				return currStr.length() <= 0 ? "" : currStr.substring(0, 1)
						.toUpperCase()
						+ currStr.substring(1).toLowerCase();
			} else if (currentCase == eCaseUpper) {
				return currStr.toUpperCase();
			} else if (currentCase == eCaseLower) {
				return currStr.toLowerCase();
			} else {
				return currStr;
			}
		}
	};

	public void handleCaseKey() {
		ExtractedText extractedText = getExtractedText();
		if (extractedText == null || extractedText.text.length() == 0) {
			return;
		}

		String text = extractedText.text.toString();

		int sel_st = Math.min(extractedText.selectionStart,
				extractedText.selectionEnd);
		int sel_en = Math.max(extractedText.selectionStart,
				extractedText.selectionEnd);

		if (sel_st < 0)
			sel_st = 0;
		if (sel_en < 0)
			sel_en = 0;

		InputConnection connection = getCurrentInputConnection();
		Word w = getWordByPos(text, sel_st);

		if (sel_st == sel_en) {
			if (sel_en > 0 && w.mStart < sel_en && w.mEnd >= sel_en
					&& w.mEnd != text.length()) {
				char prechar = text.charAt(sel_en - 1
						+ extractedText.startOffset);

				connection.beginBatchEdit();
				connection.deleteSurroundingText(1, 0);
				connection.commitText(String.valueOf(Character
						.isUpperCase(prechar) ? Character.toLowerCase(prechar)
						: Character.toUpperCase(prechar)), 1);
				connection.endBatchEdit();
				return;
			} else {
				sel_st = w.mStart;
				sel_en = w.mEnd;
				sel_en += (sel_en == text.length() ? 0 : 1);
			}
		}

		String newStr = toggleCase(text, sel_st, sel_en);

		int delLeft = sel_en - sel_st;
		int delRight = 0;
		int selectionOffset = sel_en + extractedText.startOffset;

		connection.beginBatchEdit();
		connection.setSelection(selectionOffset, selectionOffset);
		connection.deleteSurroundingText(delLeft, delRight);
		connection.commitText(newStr, 1);
		connection.endBatchEdit();
	}

	private HashMap<String, StringCase> mCaseMap = new HashMap<String, StringCase>();

	private String toggleCase(final String text, final int st, final int en) {
		int s_st = Math.min(st, en);
		int s_en = Math.max(st, en);

		String currStr = text.substring(s_st, s_en);
		if (currStr.equals(""))
			return currStr;

		StringCase strCase = mCaseMap.get(currStr.toLowerCase());
		String nextCaseStr = null;
		if (strCase == null) {
			strCase = new StringCase(currStr);
			mCaseMap.put(currStr.toLowerCase(), strCase);
		} else {
			strCase.setCaseState(currStr);
		}

		nextCaseStr = strCase.nextCase();
		if (nextCaseStr.equals(currStr)) {
			nextCaseStr = strCase.nextCase();
			if (nextCaseStr.equals(currStr)) {
				nextCaseStr = strCase.nextCase();
			}
		}
		return nextCaseStr;
	}

	private void loadSettings() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		mVibrateOn = sp.getBoolean(PREF_VIBRATE_ON, false);
		mSoundOn = sp.getBoolean(PREF_SOUND_ON, false);
		mDisableAutoEdit = sp.getBoolean(PREF_AUTO_EDIT, false);
		mShowIdealShape = sp.getBoolean(PREF_IDEAL_SHAPE, true);

		mPageCur.setDisableAutoEdit(mDisableAutoEdit);
		mPageCur.setShowIdealShape(mShowIdealShape);

		// mKbdLayout = Integer.parseInt((String) sp.getString(
		// PREF_KEYBOARD_LAYOUT, String.valueOf(KeyboardLayout.eQWERTY)));

		// mShowSuggestions = sp.getBoolean(PREF_SHOW_SUGGESTIONS, true);
	}

	private void setAutoEditDiabled(boolean disable) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(PREF_AUTO_EDIT, disable);
		editor.commit();
	}

	public void copyCommand(boolean bCut) {
		ExtractedText extractedText = getExtractedText();
		if (extractedText == null || extractedText.text.length() == 0) {
			return;
		}

		String text = extractedText.text.toString();

		int min;
		int max;
		min = Math
				.min(extractedText.selectionStart, extractedText.selectionEnd);
		max = Math
				.max(extractedText.selectionStart, extractedText.selectionEnd);
		if (min < 0)
			min = 0;
		if (max < 0)
			max = 0;

		if (min == max)
			return;

		ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

		clip.setText(text.subSequence(min, max));
		if (bCut) {
			// SendText(null, false, false);
			sendText(null, 0);
		}
	}

	public boolean pasteCommand() {
		ExtractedText extractedText = getExtractedText();
		if (extractedText == null) {
			return false;
		}

		ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		String paste = null;
		paste = clip.getText().toString();

		if (paste != null) {
			// SendText(paste, true, false);
			sendText(paste, 0);
			mJustPaste = true;
			return true;
		}
		return false;
	}

	public boolean selectAllCommand() {
		InputConnection connection = getCurrentInputConnection();
		if (connection == null)
			return false;

		ExtractedText extractedText = getExtractedText();
		if (extractedText == null || extractedText.text.length() == 0) {
			return false;
		}

		return connection.setSelection(0, extractedText.text.length());
	}

	public void ResetOperationCommand() {
	}
}
