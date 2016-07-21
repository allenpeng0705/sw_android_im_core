package com.shapewriter.android.softkeyboard;

import android.net.Uri;
import android.provider.BaseColumns;


public interface SWI_Constants extends BaseColumns {
	public static final String TABLE_USER_LEXCION = "User_lexion_table";
	public static final String TABLE_ACTIVE_LEXCION = "Active_lexion_table";
	public static final String AUTHORITY = "com.shapewriter.android.softkeyboard.swi_databasemanage";
	public static final Uri CONTENT_URI_FOR_USER_LEXCION = Uri.parse("content://"
	      + AUTHORITY + "/" + TABLE_USER_LEXCION);
	public static final Uri CONTENT_URI_FOR_ACTIVE_LEXCION = Uri.parse("content://"
		  + AUTHORITY + "/" + TABLE_ACTIVE_LEXCION);

	
	// Columns in the Events database
	public static final String 	LANGUAGE = "language";
	public static final String WORD = "word";
}

