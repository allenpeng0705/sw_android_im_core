package com.shapewriter.android.softkeyboard;





import android.content.Context;

public class SWI_Language {
	public static final String ENGLISH = "English";
	public static final String GERMAN = "German";
	public static final String SPANISH = "Spanish";
	public static final String FRENCH = "French";
	public static final String ITALIAN = "Italian";
	public static final String CHINESE = "Chinese";
	public static final String JAPANESE = "Japanese";
	public static final String SWEDISH = "Swedish";
	public static final String FRENCHQWERTY = "FrenchQwerty";
	public static final String DANISH = "Danish";
	public static final String TURKISH = "Turkish";
	public static final String NUMBER = "number";
	
	public static String NativeLanguage(Context context, String English){
		if(English.equals(GERMAN))	return context.getString(R.string.German);
		if(English.equals(SPANISH))	return context.getString(R.string.Spanish);
		if(English.equals(FRENCH))	return context.getString(R.string.French);
		if(English.equals(ITALIAN))	return context.getString(R.string.Italian);
		if(English.equals(CHINESE))	return context.getString(R.string.Chinese);
		if(English.equals(SWEDISH))	return context.getString(R.string.Swedish);
		if(English.equals(FRENCHQWERTY))	return context.getString(R.string.FrenchQwerty);
		if(English.equals(DANISH))	return context.getString(R.string.Danish);
		if(English.equals(TURKISH))	return context.getString(R.string.Turkish);
		return English;
	}
}


