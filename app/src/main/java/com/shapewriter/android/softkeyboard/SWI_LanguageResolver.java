package com.shapewriter.android.softkeyboard;





import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SWI_LanguageResolver {

	private ContentResolver mResolver;

	
	public SWI_LanguageResolver(Context context){
		mResolver = context.getContentResolver();
	}
	
	public void destroy(){
		mResolver = null;
	}
	
	public boolean exist(){
		Cursor cur = mResolver.query(SWI_LanguageTable.CONTENT_URI, new String[]{SWI_LanguageTable.LANGUAGE}, 
				null, null, null);
		if(cur == null){
			return false;
		}
		else{
			cur.close();
			return true;
		}
	}

	public boolean exist(String language){
		Cursor cur = mResolver.query(SWI_LanguageTable.CONTENT_URI, new String[]{SWI_LanguageTable.LANGUAGE}, 
				SWI_LanguageTable.LANGUAGE + "=" + "\"" + language + "\"", null, null);
		if(!cur.moveToFirst()){
			cur.close();
			return false;
		}
		else{
			cur.close();
			String directory = getDirectory(language);
			if(!SWI_LanguageFileIO.existAll(directory, language)){
				mResolver.delete(SWI_LanguageTable.CONTENT_URI, 
						SWI_LanguageTable.LANGUAGE + "=" + "\"" + language + "\"", null);
				return false;
			}
			return true;
		}
	}
	
	public int getVersion(String language){
		Cursor cur = mResolver.query(SWI_LanguageTable.CONTENT_URI, new String[]{SWI_LanguageTable.VERSION}, 
				SWI_LanguageTable.LANGUAGE + "=" + "\"" + language + "\"", null, null);
		if(cur.moveToFirst()){
			int version = cur.getInt(cur.getColumnIndex(SWI_LanguageTable.VERSION));
			cur.close();
			return version;
		}
		else{
			cur.close();
			return -1;
		}
	}
	
	public String getDirectory(String language){
		Cursor cur = mResolver.query(SWI_LanguageTable.CONTENT_URI, new String[]{SWI_LanguageTable.DIRECTORY}, 
				SWI_LanguageTable.LANGUAGE + "=" + "\"" + language + "\"", null, null);
		if(cur.moveToFirst()){
			String directory = cur.getString(cur.getColumnIndex(SWI_LanguageTable.DIRECTORY));
			cur.close();
			return directory;
		}
		else{
			cur.close();
			return null;
		}
	}
	
	public Uri insert(String language, String directory, int version, String other){
		ContentValues values = new ContentValues();
		values.put(SWI_LanguageTable.LANGUAGE, language);
		values.put(SWI_LanguageTable.DIRECTORY, directory);
		values.put(SWI_LanguageTable.VERSION, version);
		values.put(SWI_LanguageTable.OTHER, other);
		return mResolver.insert(SWI_LanguageTable.CONTENT_URI, values);
	}
	
	public int update(String language, String directory, int version, String other){
		ContentValues values = new ContentValues();
		values.put(SWI_LanguageTable.LANGUAGE, language);
		values.put(SWI_LanguageTable.DIRECTORY, directory);
		values.put(SWI_LanguageTable.VERSION, version);
		values.put(SWI_LanguageTable.OTHER, other);
		return mResolver.update(SWI_LanguageTable.CONTENT_URI, values, 
				SWI_LanguageTable.LANGUAGE + "=" + "\"" + language + "\"", null);
	}
}
