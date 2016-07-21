package com.shapewriter.android.softkeyboard;

import android.provider.BaseColumns;

import com.shapewriter.android.softkeyboard.SWI_Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SWI_Database extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "ShapeWriter";
	private static final int DATABASE_VERSION = 2;

	/** Create a helper object for the Events database */
	public SWI_Database(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE " + SWI_Constants.TABLE_USER_LEXCION
					+ " (" + BaseColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ SWI_Constants.LANGUAGE + " Text," + SWI_Constants.WORD
					+ " TEXT);");

			db.execSQL("CREATE TABLE " + SWI_Constants.TABLE_ACTIVE_LEXCION
					+ " (" + BaseColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ SWI_Constants.LANGUAGE + " Text," + SWI_Constants.WORD
					+ " TEXT);");
		} catch (Exception e) {

		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if ((oldVersion == 1) && (newVersion == 2))
			SWI_UtilSingleton.setValidUser();
	}

}
