package com.shapewriter.android.softkeyboard;

import java.io.File;
import android.content.Context;

public class SWI_LanguageFileIO {
	
	public static final String LEXICON1 = "lex1.bin";
	public static final String LEXICON2 = "lex2.bin";
	public static final String LEXICON3 = "lex3.bin";
	public static final String LEXICON4 = "lex4.bin";
	public static final String LEXICON_CMD = "lex_cmd.bin";
	public static final String CORRECT_LEXICON = "correct_lexicon.txt";
	public static final String ERROR_LEXICON = "error_lexicon.txt";
	
	public static final String NUMBER_PAGE = "number_page.xml";
	public static final String LAND_PAGE = "land_page.xml";
	public static final String PORT_PAGE = "port_page.xml";
	
	private static final String LAND_AUI = "land_aui.xml";
	private static final String PORT_AUI = "port_aui.xml";
	private static final String PORT_KEYBOARD = "port_keyboard.xml";
	private static final String PORT_NUMBER_KEYBOARD = "port_number_keyboard.xml";
	
	
	private static final String [] SET_WESTERN ={ LAND_AUI, LAND_PAGE, PORT_AUI, PORT_KEYBOARD, PORT_PAGE,
		LEXICON1, LEXICON2, LEXICON3, LEXICON4, LEXICON_CMD, CORRECT_LEXICON, ERROR_LEXICON };
	private static final String [] SET_NUMBER = { NUMBER_PAGE, PORT_NUMBER_KEYBOARD };
	
	
	public static void writeAll(Context context, String language){
		if(language.equals(SWI_Language.ENGLISH))		writeAllEnglish(context);
		else if(language.equals(SWI_Language.CHINESE))	writeAllChinese(context);
		else if(language.equals(SWI_Language.FRENCH))	writeAllFrench(context);
		else if(language.equals(SWI_Language.GERMAN))	writeAllGerman(context);
		else if(language.equals(SWI_Language.ITALIAN))	writeAllItalian(context);
		else if(language.equals(SWI_Language.JAPANESE))	writeAllJapanese(context);
		else if(language.equals(SWI_Language.SPANISH))	writeAllSpanish(context);
		else if(language.equals(SWI_Language.SWEDISH))	writeAllSwedish(context);
		else if(language.equals(SWI_Language.FRENCHQWERTY))	writeAllFrenchQwerty(context);
		else if(language.equals(SWI_Language.DANISH))	writeAllDanish(context);
		else if(language.equals(SWI_Language.TURKISH))	writeAllTurkish(context);
		else if(language.equals(SWI_Language.NUMBER))	writeAllNumber(context);
	}
	
	/**
	 * Check files exist. Normally, when the language package application deleted, 
	 * the data item still exist in Language-Table. So need to check if the real 
	 * files exist.
	 */
	public static boolean existAll(String directory, String language){
		if(language.equals(SWI_Language.ENGLISH))		return existAllEnglish(directory);
		else if(language.equals(SWI_Language.CHINESE))	return existAllChinese(directory);
		else if(language.equals(SWI_Language.FRENCH))	return existAllFrench(directory);
		else if(language.equals(SWI_Language.GERMAN))	return existAllGerman(directory);
		else if(language.equals(SWI_Language.ITALIAN))	return existAllItalian(directory);
		else if(language.equals(SWI_Language.JAPANESE))	return existAllJapanese(directory);
		else if(language.equals(SWI_Language.SPANISH))	return existAllSpanish(directory);
		else if(language.equals(SWI_Language.SWEDISH))	return existAllSwedish(directory);
		else if(language.equals(SWI_Language.FRENCHQWERTY))	return existAllFrenchQwerty(directory);
		else if(language.equals(SWI_Language.DANISH))	return existAllDanish(directory);
		else if(language.equals(SWI_Language.TURKISH))	return existAllTurkish(directory);
		else if(language.equals(SWI_Language.NUMBER))	return existAllNumber(directory);
		return false;
	}
	
	
	private static void writeAllEnglish(Context context){
		writeAllWestern(context);
	}
	
	private static void writeAllFrench(Context context){
		writeAllWestern(context);
	}
	
	private static void writeAllGerman(Context context){
		writeAllWestern(context);
	}
	
	private static void writeAllItalian(Context context){
		writeAllWestern(context);
	}

	private static void writeAllSpanish(Context context){
		writeAllWestern(context);
	}
	
	private static void writeAllSwedish(Context context){
		writeAllWestern(context);
	}
	
	private static void writeAllFrenchQwerty(Context context){
		writeAllWestern(context);
	}
	
	private static void writeAllDanish(Context context){
		writeAllWestern(context);
	}

	private static void writeAllTurkish(Context context){
		writeAllWestern(context);
	}

	private static void writeAllJapanese(Context context){
		
	}

	private static void writeAllChinese(Context context){
		
	}
	
	private static void writeAllWestern(Context context){
		SWI_FileIO.write(context, LAND_AUI, R.raw.land_aui);
		SWI_FileIO.write(context, LAND_PAGE, R.raw.land_page);
		SWI_FileIO.write(context, PORT_AUI, R.raw.port_aui);
		SWI_FileIO.write(context, PORT_KEYBOARD, R.raw.port_keyboard);
		SWI_FileIO.write(context, PORT_PAGE, R.raw.port_page);
		
		SWI_FileIO.write(context, LEXICON1, R.raw.lex1);
		SWI_FileIO.write(context, LEXICON2, R.raw.lex2);
		SWI_FileIO.write(context, LEXICON3, R.raw.lex3);
		SWI_FileIO.write(context, LEXICON4, R.raw.lex4);
		
		SWI_FileIO.write(context, LEXICON_CMD, R.raw.lex_cmd);
		SWI_FileIO.write(context, CORRECT_LEXICON, R.raw.correct_lexicon);
		SWI_FileIO.write(context, ERROR_LEXICON, R.raw.error_lexicon);
	}
	
	private static void writeAllNumber(Context context){
		SWI_FileIO.write(context, NUMBER_PAGE, R.raw.number_page);
		SWI_FileIO.write(context, PORT_NUMBER_KEYBOARD, R.raw.port_number_keyboard);
	}
	
	
	private static boolean existAllEnglish(String directory){
		return existAllWestern(directory);
	}
	
	private static boolean existAllFrench(String directory){
		return existAllWestern(directory);
	}
	
	private static boolean existAllGerman(String directory){
		return existAllWestern(directory);
	}
	
	private static boolean existAllItalian(String directory){
		return existAllWestern(directory);
	}
	
	private static boolean existAllSwedish(String directory){
		return existAllWestern(directory);
	}
	
	private static boolean existAllFrenchQwerty(String directory){
		return existAllWestern(directory);
	}
	
	private static boolean existAllDanish(String directory){
		return existAllWestern(directory);
	}
	
	private static boolean existAllTurkish(String directory){
		return existAllWestern(directory);
	}
	
	private static boolean existAllSpanish(String directory){
		return existAllWestern(directory);
	}
	
	private static boolean existAllChinese(String directory){
		return false;
	}
	
	private static boolean existAllJapanese(String directory){
		return false;
	}
	
	private static boolean existAllNumber(String directory){
		int size = SET_NUMBER.length;
		File file;
		for(int i = 0; i < size; i++){
			file = new File(directory, SET_NUMBER[i]);
			if(!file.exists()){
				return false;
			}
		}
		return true;
	}
	
	private static boolean existAllWestern(String directory){
		int size = SET_WESTERN.length;
		File file;
		for(int i = 0; i < size; i++){
			file = new File(directory, SET_WESTERN[i]);
			if(!file.exists()){
				return false;
			}
		}
		return true;
	}
}
