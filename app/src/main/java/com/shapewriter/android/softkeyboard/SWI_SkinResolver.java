package com.shapewriter.android.softkeyboard;



import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


public class SWI_SkinResolver {
	
	private ContentResolver mResolver;
	
	public SWI_SkinResolver(Context context){
		mResolver = context.getContentResolver();
	}
	
	public void destroy(){
		mResolver = null;
	}
	
	public boolean exist(){
		Cursor cur = mResolver.query(SWI_SkinTable.CONTENT_URI, new String[]{SWI_SkinTable.NAME}, 
				null, null, null);
		if(cur == null){
			return false;
		}
		else{
			cur.close();
			return true;
		}
	}

	public boolean exist(String name){
		Cursor cur = mResolver.query(SWI_SkinTable.CONTENT_URI, new String[]{SWI_SkinTable.NAME}, 
				SWI_SkinTable.NAME + "=" + "\"" + name + "\"", null, null);
		if(!cur.moveToFirst()){
			cur.close();
			return false;
		}
		else{
			cur.close();
			String directory = getDirectory(name);
			if(!SWI_SkinFileIO.existAll(directory)){
				mResolver.delete(SWI_SkinTable.CONTENT_URI, 
						SWI_SkinTable.NAME + "=" + "\"" + name + "\"", null);
				return false;
			}
			return true;
		}
	}
	
	
	/**
	 * Get the version of skin named by 'name'
	 * @param name: skin name
	 * @return If the name exist, return the version, otherwise return -1. 
	 */
	public int getVersion(String name){
		Cursor cur = mResolver.query(SWI_SkinTable.CONTENT_URI, new String[]{SWI_SkinTable.VERSION}, 
				SWI_SkinTable.NAME + "=" + "\"" + name + "\"", null, null);
		if(cur.moveToFirst()){
			int version = cur.getInt(cur.getColumnIndex(SWI_SkinTable.VERSION));
			cur.close();
			return version;
		}
		else{
			cur.close();
			return -1;
		}
	}
	
	public String getDirectory(String name){
		Cursor cur = mResolver.query(SWI_SkinTable.CONTENT_URI, new String[]{SWI_SkinTable.DIRECTORY}, 
				SWI_SkinTable.NAME + "=" + "\"" + name + "\"", null, null);
		if(cur.moveToFirst()){
			String directory = cur.getString(cur.getColumnIndex(SWI_SkinTable.DIRECTORY)); 
			cur.close();
			return directory;
		}
		else{
			cur.close();
			return null;
		}
	}
	
	public Uri insert(String name, String directory, int version, String other){
		ContentValues values = new ContentValues();
		values.put(SWI_SkinTable.NAME, name);
		values.put(SWI_SkinTable.DIRECTORY, directory);
		values.put(SWI_SkinTable.VERSION, version);
		values.put(SWI_SkinTable.OTHER, other);
		return mResolver.insert(SWI_SkinTable.CONTENT_URI, values);
	}
	
	public int update(String name, String directory, int version, String other){
		ContentValues values = new ContentValues();
		values.put(SWI_SkinTable.NAME, name);
		values.put(SWI_SkinTable.DIRECTORY, directory);
		values.put(SWI_SkinTable.VERSION, version);
		values.put(SWI_SkinTable.OTHER, other);
		return mResolver.update(SWI_SkinTable.CONTENT_URI, values, 
				SWI_SkinTable.NAME + "=" + "\"" + name + "\"", null);
	}
	
}
