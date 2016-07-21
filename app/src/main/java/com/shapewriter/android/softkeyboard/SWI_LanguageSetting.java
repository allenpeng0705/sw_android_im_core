package com.shapewriter.android.softkeyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SWI_LanguageSetting {
	private Context mContext;
	private String mFirstLanguage;
	private String mSecondLanguage;
	private SWI_LanguageResolver mLanguageResolver;
	
	public SWI_LanguageSetting(Context context, SWI_LanguageResolver languageResolver){
		mContext = context;
		mLanguageResolver = languageResolver;
		updateSelectedLanguage();
	}
	
	public void destroy(){
		mContext = null;
		mLanguageResolver = null;
	}

	public String getFirstLanguage(){
		return mFirstLanguage;
	}
	
	public String getSecondLanguage(){
		return mSecondLanguage;
	}
	
	public void updateSelectedLanguage(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		if(true == SWI_UtilSingleton.instance().getIsInstalledLanguage()){
			
			SharedPreferences.Editor editor = sp.edit();
			if(sp.getBoolean(SWI_Language.ENGLISH, false) && mLanguageResolver.exist(SWI_Language.ENGLISH)){
				editor.putBoolean(SWI_Language.ENGLISH, false);
			}
			if(sp.getBoolean(SWI_Language.FRENCH, false) && mLanguageResolver.exist(SWI_Language.FRENCH)){
				editor.putBoolean(SWI_Language.FRENCH, false);
			}
			if(sp.getBoolean(SWI_Language.GERMAN, false) && mLanguageResolver.exist(SWI_Language.GERMAN)){
				editor.putBoolean(SWI_Language.GERMAN, false);
			}
			if(sp.getBoolean(SWI_Language.ITALIAN, false) && mLanguageResolver.exist(SWI_Language.ITALIAN)){
				editor.putBoolean(SWI_Language.ITALIAN, false);
			}
			if(sp.getBoolean(SWI_Language.SPANISH, false) && mLanguageResolver.exist(SWI_Language.SPANISH)){
				editor.putBoolean(SWI_Language.SPANISH, false);
			}
			if(sp.getBoolean(SWI_Language.SWEDISH, false) && mLanguageResolver.exist(SWI_Language.SWEDISH)){
				editor.putBoolean(SWI_Language.SWEDISH, false);
			}
			if(sp.getBoolean(SWI_Language.FRENCHQWERTY, false) && mLanguageResolver.exist(SWI_Language.FRENCHQWERTY)){
				editor.putBoolean(SWI_Language.FRENCHQWERTY, false);
			}
			if(sp.getBoolean(SWI_Language.DANISH, false) && mLanguageResolver.exist(SWI_Language.DANISH)){
				editor.putBoolean(SWI_Language.DANISH, false);
			}
			if(sp.getBoolean(SWI_Language.TURKISH, false) && mLanguageResolver.exist(SWI_Language.TURKISH)){
				editor.putBoolean(SWI_Language.TURKISH, false);
			}
			String language = SWI_UtilSingleton.instance().getInstalledLanguage();						
			mFirstLanguage = language;
			editor.putBoolean(language, true);
			editor.commit();
			
			SWI_UtilSingleton.instance().logIsInstalledLanguage(false);
		} 
		   
		else{
			if(sp.getBoolean(SWI_Language.CHINESE, false) && mLanguageResolver.exist(SWI_Language.CHINESE)){
				mFirstLanguage = SWI_Language.CHINESE;
			} 
			else if(sp.getBoolean(SWI_Language.ENGLISH, false) && mLanguageResolver.exist(SWI_Language.ENGLISH)){
				mFirstLanguage = SWI_Language.ENGLISH;
			}
			else if(sp.getBoolean(SWI_Language.FRENCH, false) && mLanguageResolver.exist(SWI_Language.FRENCH)){
				mFirstLanguage = SWI_Language.FRENCH;
			}
			else if(sp.getBoolean(SWI_Language.GERMAN, false) && mLanguageResolver.exist(SWI_Language.GERMAN)){
				mFirstLanguage = SWI_Language.GERMAN;
			}
			else if(sp.getBoolean(SWI_Language.ITALIAN, false) && mLanguageResolver.exist(SWI_Language.ITALIAN)){
				mFirstLanguage = SWI_Language.ITALIAN;
			}
			else if(sp.getBoolean(SWI_Language.JAPANESE, false) && mLanguageResolver.exist(SWI_Language.JAPANESE)){
				mFirstLanguage = SWI_Language.JAPANESE;
			}
			else if(sp.getBoolean(SWI_Language.SPANISH, false) && mLanguageResolver.exist(SWI_Language.SPANISH)){
				mFirstLanguage = SWI_Language.SPANISH;
			}
			else if(sp.getBoolean(SWI_Language.SWEDISH, false) && mLanguageResolver.exist(SWI_Language.SWEDISH)){
				mFirstLanguage = SWI_Language.SWEDISH;
			}
			else if(sp.getBoolean(SWI_Language.FRENCHQWERTY, false) && mLanguageResolver.exist(SWI_Language.FRENCHQWERTY)){
				mFirstLanguage = SWI_Language.FRENCHQWERTY;
			}
			else if(sp.getBoolean(SWI_Language.DANISH, false) && mLanguageResolver.exist(SWI_Language.DANISH)){
				mFirstLanguage = SWI_Language.DANISH;
			}
			else if(sp.getBoolean(SWI_Language.TURKISH, false) && mLanguageResolver.exist(SWI_Language.TURKISH)){
				mFirstLanguage = SWI_Language.TURKISH;
			}
			else{
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(SWI_Language.ENGLISH, true);
				editor.commit();
				mFirstLanguage = SWI_Language.ENGLISH;
			}
			
			if(sp.getBoolean(SWI_Language.CHINESE, false) && !mFirstLanguage.equals(SWI_Language.CHINESE)
					 && mLanguageResolver.exist(SWI_Language.CHINESE)){
				mSecondLanguage = SWI_Language.CHINESE;
			}
			else if(sp.getBoolean(SWI_Language.ENGLISH, false) && !mFirstLanguage.equals(SWI_Language.ENGLISH)
					 && mLanguageResolver.exist(SWI_Language.ENGLISH)){
				mSecondLanguage = SWI_Language.ENGLISH;
			}
			else if(sp.getBoolean(SWI_Language.FRENCH, false) && !mFirstLanguage.equals(SWI_Language.FRENCH)
					 && mLanguageResolver.exist(SWI_Language.FRENCH)){
				mSecondLanguage = SWI_Language.FRENCH;
			}
			else if(sp.getBoolean(SWI_Language.GERMAN, false) && !mFirstLanguage.equals(SWI_Language.GERMAN)
					 && mLanguageResolver.exist(SWI_Language.GERMAN)){
				mSecondLanguage = SWI_Language.GERMAN;
			}
			else if(sp.getBoolean(SWI_Language.ITALIAN, false) && !mFirstLanguage.equals(SWI_Language.ITALIAN)
					 && mLanguageResolver.exist(SWI_Language.ITALIAN)){
				mSecondLanguage = SWI_Language.ITALIAN;
			}
			else if(sp.getBoolean(SWI_Language.JAPANESE, false) && !mFirstLanguage.equals(SWI_Language.JAPANESE)
					 && mLanguageResolver.exist(SWI_Language.JAPANESE)){
				mSecondLanguage = SWI_Language.JAPANESE;
			}
			else if(sp.getBoolean(SWI_Language.SPANISH, false) && !mFirstLanguage.equals(SWI_Language.SPANISH)
					 && mLanguageResolver.exist(SWI_Language.SPANISH)){
				mSecondLanguage = SWI_Language.SPANISH;
			}
			else if(sp.getBoolean(SWI_Language.SWEDISH, false) && !mFirstLanguage.equals(SWI_Language.SWEDISH)
					 && mLanguageResolver.exist(SWI_Language.SWEDISH)){
				mSecondLanguage = SWI_Language.SWEDISH;
			}
			else if(sp.getBoolean(SWI_Language.FRENCHQWERTY, false) && !mFirstLanguage.equals(SWI_Language.FRENCHQWERTY)
					 && mLanguageResolver.exist(SWI_Language.FRENCHQWERTY)){
				mSecondLanguage = SWI_Language.FRENCHQWERTY;
			}
			else if(sp.getBoolean(SWI_Language.DANISH, false) && !mFirstLanguage.equals(SWI_Language.DANISH)
					 && mLanguageResolver.exist(SWI_Language.DANISH)){
				mSecondLanguage = SWI_Language.DANISH;
			}
			else if(sp.getBoolean(SWI_Language.TURKISH, false) && !mFirstLanguage.equals(SWI_Language.TURKISH)
					 && mLanguageResolver.exist(SWI_Language.TURKISH)){
				mSecondLanguage = SWI_Language.TURKISH;
			}
			else{
				mSecondLanguage = null;
			}
		}
	
	}
}
