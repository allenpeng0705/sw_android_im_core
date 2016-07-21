package com.shapewriter.android.softkeyboard;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SWI_IMESettings extends PreferenceActivity implements
	Preference.OnPreferenceChangeListener {
	
	
	private static final int MAX_SELECTED_NUM = 2;
	private static final int MIN_SELECTED_NUM = 1;
	
	private SWI_LanguageResolver mLanguageResolver;
	
	private boolean mEnglishInstalled = true;
	private boolean mGermanInstalled = false;
	private boolean mSpanishInstalled = false;
	private boolean mFrenchInstalled = false;
	private boolean mItalianInstalled = false;
	private boolean mSwedishInstalled = false;
	private boolean mFrenchQwertyInstalled = false;
//	private boolean mDanishInstalled = false;
//	private boolean mTurkishInstalled = false;
//	private boolean mChineseInstalled = false;
	
	private boolean mEnglishSelected = false;
	private boolean mGermanSelected = false;
	private boolean mSpanishSelected = false;
	private boolean mFrenchSelected = false;
	private boolean mItalianSelected = false;
	private boolean mSwedishSelected = false;
	private boolean mFrenchQwertySelected = false;
//	private boolean mDanishSelected = false;
//	private boolean mTurkishSelected = false;
//  boolean mChineseSelected = false;
		
	private int mSelectedNum = 0;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		addPreferencesFromResource(R.xml.prefs);
		
		mLanguageResolver = new SWI_LanguageResolver(this);
//		if(mLanguageResolver.exist(SWI_Language.CHINESE))	mChineseInstalled = true;
		if(mLanguageResolver.exist(SWI_Language.ENGLISH))	mEnglishInstalled = true;
		if(mLanguageResolver.exist(SWI_Language.FRENCH))	mFrenchInstalled = true;
		if(mLanguageResolver.exist(SWI_Language.GERMAN))	mGermanInstalled = true;
		if(mLanguageResolver.exist(SWI_Language.ITALIAN))	mItalianInstalled = true;
		if(mLanguageResolver.exist(SWI_Language.SPANISH))	mSpanishInstalled = true;
		if(mLanguageResolver.exist(SWI_Language.SWEDISH))	mSwedishInstalled = true;
		if(mLanguageResolver.exist(SWI_Language.FRENCHQWERTY))	mFrenchQwertyInstalled = true;
//		if(mLanguageResolver.exist(SWI_Language.DANISH))	mDanishInstalled = true;
//		if(mLanguageResolver.exist(SWI_Language.TURKISH))	mTurkishInstalled = true;
	
		updateIntalledLanguage();	
		updateSelectedLanguage();
			
//		((CheckBoxPreference)findPreference(SWI_Language.CHINESE)).setOnPreferenceChangeListener(this);
		((CheckBoxPreference)findPreference(SWI_Language.ENGLISH)).setOnPreferenceChangeListener(this);
		((CheckBoxPreference)findPreference(SWI_Language.FRENCH)).setOnPreferenceChangeListener(this);
		((CheckBoxPreference)findPreference(SWI_Language.GERMAN)).setOnPreferenceChangeListener(this);
		((CheckBoxPreference)findPreference(SWI_Language.ITALIAN)).setOnPreferenceChangeListener(this);
		((CheckBoxPreference)findPreference(SWI_Language.SPANISH)).setOnPreferenceChangeListener(this);
		((CheckBoxPreference)findPreference(SWI_Language.SWEDISH)).setOnPreferenceChangeListener(this);
		((CheckBoxPreference)findPreference(SWI_Language.FRENCHQWERTY)).setOnPreferenceChangeListener(this);
		//((CheckBoxPreference)findPreference(SWI_Language.DANISH)).setOnPreferenceChangeListener(this);
		//((CheckBoxPreference)findPreference(SWI_Language.TURKISH)).setOnPreferenceChangeListener(this);

		
	}

	private void updateIntalledLanguage(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		
		if(mGermanInstalled)	findPreference(SWI_Language.GERMAN).setSummary(getString(R.string.installed_tip));	
		else{
			editor.putBoolean(SWI_Language.GERMAN, false);
			((CheckBoxPreference)findPreference(SWI_Language.GERMAN)).setChecked(false);
		}
		
		if(mEnglishInstalled)	findPreference(SWI_Language.ENGLISH).setSummary(getString(R.string.installed_tip));	
		else{
			editor.putBoolean(SWI_Language.ENGLISH, false);
			((CheckBoxPreference)findPreference(SWI_Language.ENGLISH)).setChecked(false);
		}

		if(mSpanishInstalled)	findPreference(SWI_Language.SPANISH).setSummary(getString(R.string.installed_tip));	
		else{
			editor.putBoolean(SWI_Language.SPANISH, false);
			((CheckBoxPreference)findPreference(SWI_Language.SPANISH)).setChecked(false);
		}
		
		if(mFrenchInstalled)	findPreference(SWI_Language.FRENCH).setSummary(getString(R.string.installed_tip));	
		else{
			editor.putBoolean(SWI_Language.FRENCH, false);
			((CheckBoxPreference)findPreference(SWI_Language.FRENCH)).setChecked(false);
		}
		
		if(mItalianInstalled)	findPreference(SWI_Language.ITALIAN).setSummary(getString(R.string.installed_tip));	
		else{
			editor.putBoolean(SWI_Language.ITALIAN, false);
			((CheckBoxPreference)findPreference(SWI_Language.ITALIAN)).setChecked(false);
		}
		
		if(mSwedishInstalled)	findPreference(SWI_Language.SWEDISH).setSummary(getString(R.string.installed_tip));	
		else{
			editor.putBoolean(SWI_Language.SWEDISH, false);
			((CheckBoxPreference)findPreference(SWI_Language.SWEDISH)).setChecked(false);
		}

		if(mFrenchQwertyInstalled)	findPreference(SWI_Language.FRENCHQWERTY).setSummary(getString(R.string.installed_tip));	
		else{
			editor.putBoolean(SWI_Language.FRENCHQWERTY, false);
			((CheckBoxPreference)findPreference(SWI_Language.FRENCHQWERTY)).setChecked(false);
		}
		
/*		if(mDanishInstalled)	findPreference(SWI_Language.DANISH).setSummary(getString(R.string.installed_tip));	
		else{
			editor.putBoolean(SWI_Language.DANISH, false);
			((CheckBoxPreference)findPreference(SWI_Language.DANISH)).setChecked(false);
		}
		
		if(mTurkishInstalled)	findPreference(SWI_Language.TURKISH).setSummary(getString(R.string.installed_tip));	
		else{
			editor.putBoolean(SWI_Language.TURKISH, false);
			((CheckBoxPreference)findPreference(SWI_Language.TURKISH)).setChecked(false);
		}*/
		
/*		if(mChineseInstalled)	findPreference(SWI_Language.CHINESE).setSummary(getString(R.string.installed_tip));	
		else{
			editor.putBoolean(SWI_Language.CHINESE, false);
			((CheckBoxPreference)findPreference(SWI_Language.CHINESE)).setChecked(false);
		}*/
		
		editor.commit();
	}
	
	private void updateSelectedLanguage(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		mEnglishSelected = sp.getBoolean(SWI_Language.ENGLISH, false);
//		mChineseSelected = sp.getBoolean(SWI_Language.CHINESE, false);
		mFrenchSelected = sp.getBoolean(SWI_Language.FRENCH, false);
		mGermanSelected = sp.getBoolean(SWI_Language.GERMAN, false);
		mItalianSelected = sp.getBoolean(SWI_Language.ITALIAN, false);
		mSpanishSelected = sp.getBoolean(SWI_Language.SPANISH, false);
		mSwedishSelected = sp.getBoolean(SWI_Language.SWEDISH, false);
		mFrenchQwertySelected = sp.getBoolean(SWI_Language.FRENCHQWERTY, false);
//		mDanishSelected = sp.getBoolean(SWI_Language.DANISH, false);
//		mTurkishSelected = sp.getBoolean(SWI_Language.TURKISH, false);
		
		if(mEnglishSelected)	mSelectedNum++;
//		if(mChineseSelected)	mSelectedNum++;
		if(mFrenchSelected)		mSelectedNum++;
		if(mGermanSelected)		mSelectedNum++;
		if(mItalianSelected)	mSelectedNum++;
		if(mSpanishSelected)	mSelectedNum++;
		if(mSwedishSelected)	mSelectedNum++;
		if(mFrenchQwertySelected)	mSelectedNum++;
//		if(mDanishSelected)	    mSelectedNum++;
//		if(mTurkishSelected)	mSelectedNum++;
		
		if(mSelectedNum == 0){
			mEnglishSelected = true;
			mSelectedNum = 1;
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(SWI_Language.ENGLISH, true);
			editor.commit();
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object objValue) {
		String key = preference.getKey();
		
		if(!isInstalled(key)){
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClassName("com.android.vending", "com.android.vending.SearchAssetListActivity");
			if (key.equalsIgnoreCase("FrenchQwerty") == false) {
				intent.putExtra(SearchManager.QUERY, "ShapeWriter " + key + " Support");
		    } else {
		    	intent.putExtra(SearchManager.QUERY, "ShapeWriter FrenchQWERTY");
		    }
			try {
				startActivity(intent);
				finish();
			} catch (ActivityNotFoundException  e) {
				new AlertDialog.Builder(this)
				.setMessage("Application \"Android Market\" not found in your phone. " +
						"Please install Android Market first.")
				.setPositiveButton("ok", null)
				.show();
			}
			return false;
		}

		if((Boolean)objValue){
			if(mSelectedNum >= MAX_SELECTED_NUM){
				new AlertDialog.Builder(this).setTitle("Notice")
				.setMessage("Select a maximum of two languages. If you want to " +
						"change language, please deselect first.")
				.setPositiveButton("ok", null)
				.show();
				return false;
			}
			else{
				mSelectedNum++;
				return true;
			}
		}
		else{
			if(mSelectedNum <= MIN_SELECTED_NUM){
				new AlertDialog.Builder(this).setTitle("Notice")
				.setMessage("Select at least one language. If you want to " +
						"change language, please select other language first.")
				.setPositiveButton("ok", null)
				.show();
				return false;
			}
			else{
				mSelectedNum--;
				return true;
			}
		}
	}
	
	private boolean isInstalled(String key){
		if(mEnglishInstalled && key.equals(SWI_Language.ENGLISH))	return true;
		if(mGermanInstalled && key.equals(SWI_Language.GERMAN))		return true;
		if(mSpanishInstalled && key.equals(SWI_Language.SPANISH))	return true;
		if(mFrenchInstalled && key.equals(SWI_Language.FRENCH))		return true;
		if(mItalianInstalled && key.equals(SWI_Language.ITALIAN))	return true;
		if(mSwedishInstalled && key.equals(SWI_Language.SWEDISH))	return true;
		if(mFrenchQwertyInstalled && key.equals(SWI_Language.FRENCHQWERTY))		return true;
//		if(mDanishInstalled && key.equals(SWI_Language.DANISH))	return true;
//		if(mTurkishInstalled && key.equals(SWI_Language.TURKISH))		return true;
//		if(mChineseInstalled && key.equals(SWI_Language.CHINESE))	return true;
		return false;
	}
}
