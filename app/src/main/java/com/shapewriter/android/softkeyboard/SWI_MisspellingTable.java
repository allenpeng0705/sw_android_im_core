package com.shapewriter.android.softkeyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

import android.content.Context;

public class SWI_MisspellingTable {
	private static SWI_MisspellingTable _instance = null;
	private HashMap<String, String> mMisspellingTable; 
	
	private boolean mInitialized = false;
	private boolean mIsEnglishRunning = true;
	
	private SWI_MisspellingTable() {
		
	}

	public static SWI_MisspellingTable instance() {
		if (_instance == null) {
			_instance = new SWI_MisspellingTable();
		}
		return _instance;
	}
	
	public void init(Context context) {
		if ((mInitialized == false) && (mIsEnglishRunning == true)) {
			mMisspellingTable = new HashMap<String, String>();
			java.io.InputStream correct_stream = context.getResources()
			.openRawResource(R.raw.correct_lexicon);
			java.io.InputStream error_stream = context.getResources()
			.openRawResource(R.raw.error_lexicon);
	
			try {	
	            BufferedReader correct_file_reader = new BufferedReader(new InputStreamReader(correct_stream));
	            BufferedReader error_file_reader = new BufferedReader(new InputStreamReader(error_stream));
	            
	            String correct_word;
	            String error_word;		
	            while ((correct_word = correct_file_reader.readLine()) != null)   {
	            	error_word = error_file_reader.readLine();
	            	mMisspellingTable.put(error_word, correct_word);
	            }
	            correct_file_reader.close();
	            error_file_reader.close();
	            correct_stream.close();
	            error_stream.close();      
	            
			} catch (Exception e) {
				
			}			
			mInitialized = true;
		}
	}
	
	public static void destroy() {
		if (_instance.mInitialized == true) {
			_instance.mMisspellingTable.clear();
			_instance.mMisspellingTable = null;
		}
		_instance = null;
	}
	
	public String fromErrorToCorrect(String word) {
		if ((mIsEnglishRunning) && (mInitialized)) {
			String ret;
			if (mMisspellingTable.isEmpty()) return word;
			ret = mMisspellingTable.get(word);
			if (ret == null) return word;	
			return ret;	
		} else {
			return word;
		}
	}
	
	public void fromErrorToCorrect(ArrayList<String> arrayList) {
		if ((mIsEnglishRunning) && (mInitialized)) {
			ArrayList<String> candidates = new ArrayList<String>();
			int size = arrayList.size();
			for (int i = 0; i<size; i++) {
				String key = arrayList.get(i);
				String value = mMisspellingTable.get(key);
				if (value == null) {
					if (candidates.contains(key) == false) candidates.add(key);
				} else {
					if (candidates.contains(value) == false) candidates.add(value);
				}
			}
			
			arrayList.clear();
			size = candidates.size();
		
			for (int i = 0; i<size; i++) {
				String val = candidates.get(i);			
				arrayList.add(val);
			}
			
			candidates.clear();
			candidates = null;
		} 
	}
	
	public void setEnglishRunning(boolean aRunning) {
		mIsEnglishRunning = aRunning;
	}
	
}
