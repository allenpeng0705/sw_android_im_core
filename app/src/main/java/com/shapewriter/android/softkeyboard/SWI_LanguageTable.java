package com.shapewriter.android.softkeyboard;

import android.net.Uri;

public class SWI_LanguageTable {

	public static final String DB_NAME = "language.db";
	public static final String TB_NAME = "languageTable";
	public static final int DB_VERSION = 2;
	
	public static final String ID = "_id";
	public static final String LANGUAGE = "language";
	public static final String VERSION = "version";
	public static final String DIRECTORY = "directory";
	public static final String OTHER = "other";
	
	public static final String AUTHORITY = "com.shapewriter.android.softkeyboard.swi_languageprovider";
	public static final int ITEM = 1;
	public static final int ITEM_ID = 2;
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studio.android.countrycode";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studio.android.countrycode";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/item");
}