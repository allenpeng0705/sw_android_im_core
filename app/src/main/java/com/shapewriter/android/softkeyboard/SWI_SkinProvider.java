package com.shapewriter.android.softkeyboard;



import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;



public class SWI_SkinProvider extends ContentProvider{
	
	SkinDBHelper dbHelper;
	
	private static final UriMatcher sMatcher;
	static{
		sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sMatcher.addURI(SWI_SkinTable.AUTHORITY, "item", SWI_SkinTable.ITEM);
		sMatcher.addURI(SWI_SkinTable.AUTHORITY, "item/#", SWI_SkinTable.ITEM_ID);
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch(sMatcher.match(uri)){
		case SWI_SkinTable.ITEM:
			count = db.delete(SWI_SkinTable.TB_NAME, where, whereArgs);
			break;
		case SWI_SkinTable.ITEM_ID:
			String id = uri.getPathSegments().get(1);
			count = db.delete(SWI_SkinTable.TB_NAME, SWI_SkinTable.ID + "=" + id + 
					(!TextUtils.isEmpty(where)? " AND (" + where + ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknow URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch(sMatcher.match(uri)){
		case SWI_SkinTable.ITEM:
			return SWI_SkinTable.CONTENT_TYPE;
		case SWI_SkinTable.ITEM_ID:
			return SWI_SkinTable.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId;
		
		if(sMatcher.match(uri) != SWI_SkinTable.ITEM){
			throw new IllegalArgumentException("UnKnown URI " + uri);
		}
		rowId = db.insert(SWI_SkinTable.TB_NAME, SWI_SkinTable.ID, values);
		if(rowId > 0){
			Uri noteUri = ContentUris.withAppendedId(SWI_SkinTable.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}
		throw new SQLException("SWI_SkinProvider Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new SkinDBHelper(getContext(), SWI_SkinTable.DB_NAME, null, SWI_SkinTable.DB_VERSION);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c;
		switch(sMatcher.match(uri)){
		case SWI_SkinTable.ITEM:
			c = db.query(SWI_SkinTable.TB_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case SWI_SkinTable.ITEM_ID:
			String id = uri.getPathSegments().get(1);
			c = db.query(SWI_SkinTable.TB_NAME, projection, SWI_SkinTable.ID + "=" + id +
					(!TextUtils.isEmpty(selection)? " AND (" + selection + ')' : ""), selectionArgs, null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("UnKnow URI " + uri);
		}
		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
		
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch(sMatcher.match(uri)){
		case SWI_SkinTable.ITEM:
			count = db.update(SWI_SkinTable.TB_NAME, values, where, whereArgs);
			break;
		case SWI_SkinTable.ITEM_ID:
			String id = uri.getPathSegments().get(1);
			count = db.update(SWI_SkinTable.TB_NAME, values, SWI_SkinTable.ID + "=" + id +
					(!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	class SkinDBHelper extends SQLiteOpenHelper{

		public SkinDBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
				db.execSQL("CREATE TABLE IF NOT EXISTS " + SWI_SkinTable.TB_NAME + " ("
						+ SWI_SkinTable.ID +" INTEGER PRIMARY KEY,"
						+ SWI_SkinTable.NAME + " TEXT,"
						+ SWI_SkinTable.DIRECTORY + " TEXT,"
						+ SWI_SkinTable.VERSION + " INTEGER,"
						+ SWI_SkinTable.OTHER + " TEXT)");
			}catch(SQLException e){
				//Log.e("chen","SQLException in onCreate of SkinDBHelper");
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			//Log.e("chen","SkinDBHelper onUpgrade top");
		}
	}
}
