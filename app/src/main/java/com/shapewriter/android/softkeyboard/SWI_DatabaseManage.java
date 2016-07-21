package com.shapewriter.android.softkeyboard;

import android.provider.BaseColumns;

import com.shapewriter.android.softkeyboard.SWI_Constants;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class SWI_DatabaseManage extends ContentProvider {
	private static final int EVENTS = 1;
	private static final int EVENTS_ID = 2;

	/** The MIME type of a directory of events */
	private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.example.event";

	/** The MIME type of a single event */
	private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.example.event";

	private UriMatcher mUriMatcher;
	private SWI_Database mSWIDatabase;
	private SQLiteDatabase mSQLDatabase;

	@Override
	public boolean onCreate() {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(SWI_Constants.AUTHORITY, "User_lexion_table", EVENTS);
		mUriMatcher.addURI(SWI_Constants.AUTHORITY, "User_lexion_table/#", EVENTS_ID);
		mUriMatcher.addURI(SWI_Constants.AUTHORITY, "Active_lexion_table", EVENTS);
		mUriMatcher.addURI(SWI_Constants.AUTHORITY, "Active_lexion_table/#", EVENTS_ID);
		mSWIDatabase = new SWI_Database(getContext());
		mSQLDatabase = mSWIDatabase.getWritableDatabase();
		return true;
	}

	public void close() {
		mSQLDatabase.close();
		mSWIDatabase = null;
		mUriMatcher = null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String orderBy) {
		if (mUriMatcher.match(uri) == EVENTS_ID) {
			long id = Long.parseLong(uri.getPathSegments().get(1));
			selection = appendRowId(selection, id);
		}

		if (uri.equals(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION)) {
			// Get the database and run the query
			try {
				Cursor cursor = mSQLDatabase.query(
						SWI_Constants.TABLE_USER_LEXCION, projection, selection,
						selectionArgs, null, null, orderBy);
				// Tell the cursor what uri to watch, so it knows when its
				// source data changes
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				return cursor;
			} catch (Exception e) {
				return null;
			}
		} else {
			// Get the database and run the query
			try {
				Cursor cursor2 = mSQLDatabase.query(
						SWI_Constants.TABLE_ACTIVE_LEXCION, projection, selection,
						selectionArgs, null, null, orderBy);
				// Tell the cursor what uri to watch, so it knows when its
				// source data changes
				cursor2.setNotificationUri(getContext().getContentResolver(), uri);
				return cursor2;
			} catch (Exception e) {
				return null;
			}
		}

	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case EVENTS:
			return CONTENT_TYPE;
		case EVENTS_ID:
			return CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Validate the requested uri
		try {
			if (mUriMatcher.match(uri) != EVENTS) {
				throw new IllegalArgumentException("Unknown URI " + uri);
			}
			if (uri.equals(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION)) {
				// Insert into database
				long id = mSQLDatabase.insertOrThrow(
						SWI_Constants.TABLE_USER_LEXCION, null, values);
	
				// Notify any watchers of the change
				Uri newUri = ContentUris.withAppendedId(
						SWI_Constants.CONTENT_URI_FOR_USER_LEXCION, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			} else {
				// Insert into database
				long id = mSQLDatabase.insertOrThrow(
						SWI_Constants.TABLE_ACTIVE_LEXCION, null, values);
	
				// Notify any watchers of the change
				Uri newUri = ContentUris.withAppendedId(
						SWI_Constants.CONTENT_URI_FOR_ACTIVE_LEXCION, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		try {
			if (uri.equals(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION)) {
				switch (mUriMatcher.match(uri)) {
				case EVENTS:
					count = mSQLDatabase.delete(SWI_Constants.TABLE_USER_LEXCION,
							selection, selectionArgs);
					break;
				case EVENTS_ID:
					long id = Long.parseLong(uri.getPathSegments().get(1));
					count = mSQLDatabase.delete(SWI_Constants.TABLE_USER_LEXCION,
							appendRowId(selection, id), selectionArgs);
					break;
				default:
					throw new IllegalArgumentException("Unknown URI " + uri);
				}
			} else {
				switch (mUriMatcher.match(uri)) {
				case EVENTS:
					count = mSQLDatabase.delete(SWI_Constants.TABLE_ACTIVE_LEXCION,
							selection, selectionArgs);
					break;
				case EVENTS_ID:
					long id = Long.parseLong(uri.getPathSegments().get(1));
					count = mSQLDatabase.delete(SWI_Constants.TABLE_ACTIVE_LEXCION,
							appendRowId(selection, id), selectionArgs);
					break;
				default:
					throw new IllegalArgumentException("Unknown URI " + uri);
				}
			}
	
			// Notify any watchers of the change
			getContext().getContentResolver().notifyChange(uri, null);
		} catch (Exception e) {}
		
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		try {
			if (uri.equals(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION)) {
				switch (mUriMatcher.match(uri)) {
				case EVENTS:
					count = mSQLDatabase.update(SWI_Constants.TABLE_USER_LEXCION,
							values, selection, selectionArgs);
					break;
				case EVENTS_ID:
					long id = Long.parseLong(uri.getPathSegments().get(1));
					count = mSQLDatabase.update(SWI_Constants.TABLE_USER_LEXCION,
							values, appendRowId(selection, id), selectionArgs);
					break;
				default:
					throw new IllegalArgumentException("Unknown URI " + uri);
				}
			} else {
				switch (mUriMatcher.match(uri)) {
				case EVENTS:
					count = mSQLDatabase.update(SWI_Constants.TABLE_ACTIVE_LEXCION,
							values, selection, selectionArgs);
					break;
				case EVENTS_ID:
					long id = Long.parseLong(uri.getPathSegments().get(1));
					count = mSQLDatabase.update(SWI_Constants.TABLE_ACTIVE_LEXCION,
							values, appendRowId(selection, id), selectionArgs);
					break;
				default:
					throw new IllegalArgumentException("Unknown URI " + uri);
				}
			}
	
			// Notify any watchers of the change
			getContext().getContentResolver().notifyChange(uri, null);
		} catch (Exception e) {}
		
		return count;
	}

	/** Append an id test to a SQL selection expression */
	private String appendRowId(String selection, long id) {
		return BaseColumns._ID
				+ "="
				+ id
				+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')'
						: "");
	}

}
