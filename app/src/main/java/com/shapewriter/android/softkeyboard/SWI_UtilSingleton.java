package com.shapewriter.android.softkeyboard;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.UserDictionary;
import android.provider.Contacts.People;
import android.provider.UserDictionary.Words;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SWI_UtilSingleton {
	private static boolean mIsExpired = false;
	private static boolean mValidUser = false;
	private final int TRAIL_DAYS = 60;
	private final long ONE_DAY_MILLIS = 1 * 24 * 60 * 60 * 1000L;
	private static SWI_UtilSingleton _instance = null;
	private final static String APP_INSTALL_DATETIME = "shapewriter_install_datetime";
	private final static String APP_VERSION_CODE = "shapewriter_version_number";
	private final static String APP_IS_SHAPEWRITER_VALID_USER = "shapewriter_is_valid_user";
	private final static String APP_CURRENT_LANGUAGE = "shapewriter_current_language";
	
	public static final String PREF_CONTACTS = "has_read_contacts";
	public static final String PREF_DICTIONAY = "has_read_user_dict";
	//
	private final static String APP_INSTALL_NEW_LANGUAGE = "shapewriter_current_installed_language";
	private final static String APP_IS_NEW_INSTALLED = "shapewriter_is_new_installed";
	//
	private Context mContext;
	private Toast mToast = null;
	
	private SWI_UtilSingleton() {
		
	}

	public static SWI_UtilSingleton instance() {
		if (_instance == null) {
			_instance = new SWI_UtilSingleton();
		}

		return _instance;
	}
	
	public static void setValidUser() {
		mValidUser = true;
	}
	
	public void setContext(Context aContext) {
		mContext = aContext;
		mToast = Toast.makeText(mContext, "This is a free version of ShapeWriter, valid for only one month. If you have paid the previous full version, this is a free update." +
				"Feel free to contact [android@shapewriter.com], when you have any problem.", Toast.LENGTH_LONG);
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public void toastTrialInfo() {
		if (mToast == null) return;
		mToast.cancel();
		mToast.setDuration(Toast.LENGTH_LONG);
		mToast.setText("This is a free version of ShapeWriter, valid for only one month. If you have paid the previous full version, this is a free update." +
			"Feel free to contact [android@shapewriter.com], when you have any problem.");
		mToast.show();
	}
	
	public void toastCurrentLanguage() {
		if (mToast == null) return;
		mToast.cancel();
		mToast.setDuration(Toast.LENGTH_LONG);
		mToast.setText(_instance.getCurrentLanguage());
		mToast.show();
	}
	
	public void toastMessageInstant(String aMsg) {
		if (mToast == null) return;
		mToast.cancel();
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.setText(aMsg);
		mToast.show();
	}
	
	public void toastMessageSlow(String aMsg) {
		if (mToast == null) return;
		mToast.cancel();
		mToast.setDuration(Toast.LENGTH_LONG);
		mToast.setText(aMsg);
		mToast.show();
	}
	
	public void toastCancel() {
		if (mToast == null) return;
		mToast.cancel();
	}
	
	private void logInstallationTime() {
		long appInstallMillis = getAppInstallDateTime();
		if (appInstallMillis == 0L) {
			appInstallMillis = System.currentTimeMillis();
			Settings.System.putString(mContext.getContentResolver(),
					APP_INSTALL_DATETIME, Long.toString(appInstallMillis));
		}
	}
	
	public long getAppInstallDateTime() {
		String strDate = Settings.System.getString(mContext
				.getContentResolver(), APP_INSTALL_DATETIME);
		if (strDate == null) {
			return 0L;
		}

		return Long.parseLong(strDate);
	}
	
	public void logValidUser(boolean aValidUser) {
		Settings.System.putString(mContext.getContentResolver(),
				APP_IS_SHAPEWRITER_VALID_USER, Boolean.toString(aValidUser));
	}
	
	public void logReadWordsFromContacts(boolean aRead) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(PREF_CONTACTS, aRead);
		editor.commit();
	}
	
	public void logCurrentLanguage(String aCurrentLanguage) {
		Settings.System.putString(mContext.getContentResolver(),
				APP_CURRENT_LANGUAGE, aCurrentLanguage);
	}
	
	public String getCurrentLanguage() {
		String language = Settings.System.getString(mContext
				.getContentResolver(), APP_CURRENT_LANGUAGE);
		return language;
	}
	
	public boolean getHasReadContact() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean has_read_contact = sp.getBoolean(PREF_CONTACTS, false);
		return has_read_contact;
	}
	 
	public void logReadWordsFromUserDict(boolean aRead) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(PREF_DICTIONAY, aRead);
		editor.commit();
	}
	
	public boolean getHasReadUserDict() {
		SharedPreferences spe = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean has_read_user_dict = spe.getBoolean(PREF_DICTIONAY, false);
		return has_read_user_dict;
	}
	
	public boolean getIsValidUser() {
		String is_valid_user = Settings.System.getString(mContext
				.getContentResolver(), APP_IS_SHAPEWRITER_VALID_USER);
		if (is_valid_user == null) {
			return false;
		} 
		return Boolean.parseBoolean(is_valid_user);
	}
	
	public void logCurrentVersion(int aVersion) {
		int version = getAppVersionNumber();
		if (aVersion > version) {
			Settings.System.putString(mContext.getContentResolver(),
					APP_VERSION_CODE, Integer.toString(aVersion));
			
			// Update installation time
			long appInstallMillis = 0L;
			Settings.System.putString(mContext.getContentResolver(),
						APP_INSTALL_DATETIME, Long.toString(appInstallMillis));
		}
		logInstallationTime();
		if (mValidUser ==true) {
			logValidUser(true);
		}
	}
		
	public int getAppVersionNumber() {
		String strVersion = Settings.System.getString(mContext
				.getContentResolver(), APP_VERSION_CODE);
		if (strVersion == null) {
			return 0;
		}
		return Integer.parseInt(strVersion);
	}
	
	public boolean shouldShowExpiredInfo() {
		if (getIsValidUser() == true) return false;
		if (getTrialDays() <= TRAIL_DAYS) return false;
		mIsExpired = true;
		return true;
	}
	
	
	public boolean shouldShowTrialInfo() {
		if (getIsValidUser() == true) return false;
		if (getTrialDays() > TRAIL_DAYS) return false;
		return true;
	}
	
	public boolean isExpired() {
		return mIsExpired;
	}
	
	private int getTrialDays() {
		if (getIsValidUser()) return 0;

		long appInstallMillis = getAppInstallDateTime();
		if (appInstallMillis == 0L) return 0;

		long currMills = System.currentTimeMillis();
		int trial_days = (int) ((currMills - appInstallMillis) / ONE_DAY_MILLIS);
		return trial_days;
	}

	public void logIsInstalledLanguage(boolean aNewInstall) {
		Settings.System.putString(mContext.getContentResolver(),
				APP_IS_NEW_INSTALLED, Boolean.toString(aNewInstall));
	}
	
	public boolean getIsInstalledLanguage() {
		String is_new_installed = Settings.System.getString(mContext
				.getContentResolver(), APP_IS_NEW_INSTALLED);
		if (is_new_installed == null) {
			return false;
		} 
		return Boolean.parseBoolean(is_new_installed);
	}
	public void logInstalledLanguage(String language) {
		Settings.System.putString(mContext.getContentResolver(),
				APP_INSTALL_NEW_LANGUAGE,language);
	}
	
	public String getInstalledLanguage(){
		String language = Settings.System.getString(mContext.getContentResolver(), APP_INSTALL_NEW_LANGUAGE);
		return language;
	}

	public boolean isLegalWord(Context context, String word){
		String legalSet = context.getString(R.string.legal_symbol_set);
		int length = word.length();
		for (int i = 0; i < length; i++){
			if (!legalSet.contains(Character.toString(Character.toLowerCase(word.charAt(i))))) return false;
		}
		return true;
	}
	
	public void addToUserDatabase(ArrayList<String> words,String key) {
		try {
			ContentValues record = new ContentValues();
			
			for (int i = 0; i < words.size(); i++) {
				record.put(SWI_Constants.LANGUAGE, key);
				record.put(SWI_Constants.WORD, words.get(i));
				
				Cursor cursor = mContext.getContentResolver().query(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION, 
						null, SWI_Constants.WORD + "=" + "\"" + words.get(i) + "\"", 
						null, null);
				if (cursor != null) {
					if(cursor.getCount() == 0){
						mContext.getContentResolver().insert(
								SWI_Constants.CONTENT_URI_FOR_USER_LEXCION, record);
					} 
					cursor.close();
				}				
			}
			record = null;
		} catch (Exception e) {}
	}
	
	public void loadDataFromContacts() {
		try {
			if (getHasReadContact() == false) {
				ArrayList<String> contact_array = new ArrayList<String>();
				Cursor contacts_cursor = null;
				try {
					contacts_cursor = mContext.getContentResolver().query(
							People.CONTENT_URI, null, null, null,null);
				} catch (Exception e) {
				}
				
				if (contacts_cursor == null) {
					contact_array = null;
					return;
				}
				 
				int nameIdx = contacts_cursor.getColumnIndexOrThrow(People.NAME);
				if (contacts_cursor.moveToFirst()) {
					do {
						String name = contacts_cursor.getString(nameIdx);
						//include space
						if(name != null && !name.equals("")){
							String[] result = name.split(" ");
							if(result.length > 1){
								for(int i = 0;i < result.length;i++){
									if(false == contact_array.contains(result[i]) && isLegalWord(mContext, result[i]) &&(result[i].length() > 1)){
										contact_array.add(result[i]);
									}	 
								}
							}
							else if(false == contact_array.contains(name) && isLegalWord(mContext, name)&&(name.length() > 1)){
								contact_array.add(name);
							}
						}
					} while(contacts_cursor.moveToNext()); 
				}
				addToUserDatabase(contact_array, "Contacts"); 
				contact_array.clear();
				contact_array = null;
				contacts_cursor.close();
				logReadWordsFromContacts(true);
			}  
		} catch (Exception e) {}
	}
	
	public void loadDataFromUserDict() {
		try {
			/* add all userlexcionwords to database (not done)*/				  				
			if (getHasReadUserDict() == false) {
				ArrayList<String> user_dict_array = new ArrayList<String>();
				Cursor user_dict_cursor = null;
				try {
					user_dict_cursor = mContext.getContentResolver().query(
							UserDictionary.Words.CONTENT_URI, null, null, null,null);
				} catch (Exception e) {
				}
				
				if (user_dict_cursor == null) {
					user_dict_array = null;
					return;
				}
				
				int wordIdex = user_dict_cursor.getColumnIndexOrThrow(Words.WORD);
				if (user_dict_cursor.moveToFirst()){
					do {
						String name = user_dict_cursor.getString(wordIdex);
						if(name != null && !name.equals("")){
							String[] result = name.split(" ");
							if(result.length > 1){
								for(int i = 0;i < result.length;i++){
									if(false == user_dict_array.contains(result[i]) && isLegalWord(mContext, result[i]) &&(result[i].length() > 1)){
										user_dict_array.add(result[i]);
									}	 
								}
							}
							else if(false == user_dict_array.contains(name) && isLegalWord(mContext, name)&&(name.length() > 1)){
								user_dict_array.add(name);
							}
						}  
					} while(user_dict_cursor.moveToNext());
				}
				addToUserDatabase(user_dict_array, "UserDict"); 
				user_dict_array.clear();
				user_dict_array = null;
				user_dict_cursor.close();
				logReadWordsFromUserDict(true);
			}
		} catch (Exception e) {}
	}
	
	public void removeContactsWordsFromLexcion(String key){
		try {
			mContext.getContentResolver().delete(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION, SWI_Constants.LANGUAGE + "=" + "\"" + key + "\"" ,null);
		} catch (Exception e) {}
	}
	
	public void addWordToLexcion(String aWord,String key){
		try {
			ContentValues values = new ContentValues();
			values.put(SWI_Constants.LANGUAGE, key);
			values.put(SWI_Constants.WORD, aWord);
			mContext.getContentResolver().insert(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION,
					values);
		} catch (Exception e) {}
	}
	
	public String getIMEI() {
		String ret = null;
		TelephonyManager telephonyMgr = (TelephonyManager) mContext
		.getSystemService(Context.TELEPHONY_SERVICE);
		ret = telephonyMgr.getDeviceId();	
		return ret;
	}

	public static void destroy() {
		_instance = null;
	}
	
}
