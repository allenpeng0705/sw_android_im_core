package com.shapewriter.android.softkeyboard;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AlphabetIndexer;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter; 

public class SWI_EditDatabase extends ListActivity{
	private static final String[] PROJECTION = new String[] {
			SWI_Constants._ID, SWI_Constants.LANGUAGE, SWI_Constants.WORD };
	private static final int DIALOG1 = 1;
	private static final int DIALOG2 = 2;
	private static final int DIALOG3 = 3;
	private static final int DIALOG4 = 4;
	private static final int DIALOG5 = 5;
	private static final int DIALOG6 = 6;

	public static final int MENU_ITEM_INSERT = Menu.FIRST;
	public static final int MENU_ITEM_DELETE = Menu.FIRST +1;	
	public static final int MENU_ITEM_SYNCHRONIZE = Menu.FIRST + 2;	
	private static boolean mIsCatch = false;
	public int mCurrentWordIndex = 0;
	private Cursor mUserLexCursor = null;
	private boolean mIsWordsModified = false; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_list);
	    
		mUserLexCursor = getUserEvents();
		ListView listView = getListView(); 
		listView.setFastScrollEnabled(true);    
		setListAdapter(new MyCursorAdapter(getApplicationContext(), 
				R.layout.layout_row, mUserLexCursor,
				new String[] { SWI_Constants.WORD }, new int[] { R.id.created }));

	}
	 
	@Override
	protected void onStop() {
		super.onStop();
		if (!mUserLexCursor.isClosed()){
			mUserLexCursor.close();
		}
		if (mIsWordsModified == true) android.os.Process.killProcess(android.os.Process.myPid());
	}
	@Override
	protected void onResume(){
		super.onResume();
		if(mUserLexCursor.isClosed()) {
			mUserLexCursor = getUserEvents();
		}
		render();
	}  
	
	@Override
	public void onPause() { 
		super.onPause();
		if (!mUserLexCursor.isClosed()){
			mUserLexCursor.close();		
		}

		if (mIsWordsModified == true) android.os.Process.killProcess(android.os.Process.myPid());
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ITEM_INSERT, 0, "Add").setIcon(R.drawable.ic_menu_add);;
		menu.add(0, MENU_ITEM_DELETE, 0, "Delete").setIcon(R.drawable.ic_menu_delete);;
		menu.add(0, MENU_ITEM_SYNCHRONIZE, 0, "Synchronize").setIcon(R.drawable.ic_menu_refresh);;
		
		return true;
	}

	protected Dialog onCreateDialog(int id) { 
		switch (id) {
		case DIALOG1:
			return buildDialog1(SWI_EditDatabase.this);
		case DIALOG2:
			return buildDialog2(SWI_EditDatabase.this);
		case DIALOG3:
			return buildDialog3(SWI_EditDatabase.this);
		case DIALOG4:
			return buildDialog4(SWI_EditDatabase.this);
		case DIALOG5:
			return buildDialog5(SWI_EditDatabase.this);
		case DIALOG6:
			return buildDialog6(SWI_EditDatabase.this);
		}
		return null;
	}

	private Cursor getUserEvents() {
		try {
			return managedQuery(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION,
					PROJECTION, null, null,"UPPER("+SWI_Constants.WORD+")");
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_INSERT:
			showInputNewWordsDialog();
			return true;
		case MENU_ITEM_DELETE:
			searchWord();
			return true;
		case MENU_ITEM_SYNCHRONIZE:
			synchronize();
			return true;
		
		}
		return super.onOptionsItemSelected(item);
	}


  
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);		
		mCurrentWordIndex = position;
		showDialog(DIALOG1);
	}
 
	private void removeWordFromLexcion() {
		try {
			if (!mUserLexCursor.isClosed()){
				mUserLexCursor.moveToPosition(mCurrentWordIndex); 
			}
			String word = mUserLexCursor.getString(2);
			getContentResolver()
			.delete(
					SWI_Constants.CONTENT_URI_FOR_USER_LEXCION,
					SWI_Constants.WORD + "=" + "\"" + word + "\"", null);
			render();
			mIsWordsModified = true;   
		} catch (Exception e) {}
	}
	 
	private int removeSearchWordFromLexcion(String word){
		try {		
			int num = getContentResolver()
			.delete(
					SWI_Constants.CONTENT_URI_FOR_USER_LEXCION,
					SWI_Constants.WORD + "=" + "\"" + word + "\"", null);
			render();
			mIsWordsModified = true;
			return num;
		} catch (Exception e) {}
		return 0;
		
	}
	
	private void addWordToLexcion(String aWord) {
		try {
			ContentValues values = new ContentValues();
			values.put(SWI_Constants.LANGUAGE,  SWI_Language.ENGLISH);
			values.put(SWI_Constants.WORD, aWord);
			getContentResolver().insert(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION,
					values); 
			render();
			mIsWordsModified = true;
		} catch (Exception e) {}
	}

	private void render() {
		 setListAdapter( new MyCursorAdapter(getApplicationContext(), 
					R.layout.layout_row, mUserLexCursor,
					new String[] { SWI_Constants.WORD }, new int[] { R.id.created }));
	}

	// dialog 
	private Dialog buildDialog1(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Do you want to delete it?");
		builder.setMessage("If you choose \"OK\",the word will be deleted and can not be recognized again");
		builder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						removeWordFromLexcion();

					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// do nothing
					}
				});
		return builder.create();
	}

	private Dialog buildDialog2(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Please Input a word");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		return builder.create();
	}

	private Dialog buildDialog3(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Failed");
		builder.setMessage("This word existed the ShapeWriter dictionary!");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		return builder.create();
	}

	private Dialog buildDialog4(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Failed");
		builder.setMessage("This word contains illegal character!");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		return builder.create();
	}

	private Dialog buildDialog5(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Sorry");
		builder.setMessage("This word is not in ShapeWriter dictionary!");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		return builder.create();
	}
	
	private Dialog buildDialog6(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Sorry");
		builder.setMessage("You Should Enable ShapeWriter Keyboard First!");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		return builder.create();
	}

	// show input new word dialog
	private void showInputNewWordsDialog() {
		final EditText edittext = new EditText(this);
		AlertDialog dlg = new AlertDialog.Builder(SWI_EditDatabase.this)
				.setTitle("Add New Word").setView(edittext).setPositiveButton(
						"OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								CharSequence edit_text_value = edittext
										.getText();
								String word = edit_text_value.toString();
								String[] result = word.split(" ");
								int word_length = result.length;
								if ((word != null)
										&& (word.equals("") == false)
										&& (word_length == 1) &&(word.length() > 1)) {
									boolean isIn = isInsertToDataBase(word);
									if (true == isIn) {
										if (SWI_UtilSingleton.instance()
												.isLegalWord(
														SWI_EditDatabase.this,
														word)) {
											addWordToLexcion(word);
										} else {
											showDialog(DIALOG4);
										} 

									} else { 
										showDialog(DIALOG3); 
									}
								} else {  
									showDialog(DIALOG2);       
								}
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) { 
							}
						}).create();
		dlg.show();

	}
	// search word dialog
	private void searchWord(){  
		final EditText edittext = new EditText(this); 
		AlertDialog dlg = new AlertDialog.Builder(SWI_EditDatabase.this)
				.setTitle("Delete Word").setView(edittext).setPositiveButton(
						"OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								CharSequence edit_text_value = edittext
										.getText();
								String word = edit_text_value.toString();
								int num = removeSearchWordFromLexcion(word);
								if(num == 0){
									showDialog(DIALOG5); 
								}

								
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();
		dlg.show();
	}
	
	private void synchronize() {
		final ProgressDialog progress_dlg = ProgressDialog.show(
				this, "Synchronizing",
				"Synchronizing with contacts and user dictionary.", true);	
		
		new Thread() {
			public void run() {
				try {
					SWI_UtilSingleton.instance()
							.removeContactsWordsFromLexcion("Contacts");
					SWI_UtilSingleton.instance() 
							.removeContactsWordsFromLexcion("UserDict");
					SWI_UtilSingleton.instance().logReadWordsFromContacts(false);
					SWI_UtilSingleton.instance().logReadWordsFromUserDict(false);
					SWI_UtilSingleton.instance().loadDataFromContacts();
					SWI_UtilSingleton.instance().loadDataFromUserDict();								
					progress_dlg.dismiss();
					mIsWordsModified = true; 
				} catch (Exception e) { 
					mIsCatch = true;
					e.printStackTrace(); 
					
				} finally { 
					progress_dlg.dismiss();  
				}
			}
		}.start();
		if(true == mIsCatch){
			showDialog(DIALOG6);   
			mIsCatch = false;
		}
		render();	  
	}  

	private boolean isInsertToDataBase(String aWord) {
		try { 
			boolean ret = false;
			Intent intent = getIntent();
			if (intent.getData() == null) {
				intent.setData(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION);
			}
			Cursor cursor = managedQuery(getIntent().getData(), PROJECTION,
					SWI_Constants.WORD + "=" + "\"" + aWord + "\"", null,
					SWI_Constants.WORD);
			
			if (cursor != null) {			
				if (cursor.getCount() == 0) {
					ret = true;
				} 
				cursor.close();
			}
			return ret;
		} catch (Exception e) {
			return false;
		}
	}

	  class MyCursorAdapter extends SimpleCursorAdapter implements SectionIndexer{

	    	AlphabetIndexer alphaIndexer;
			public MyCursorAdapter(Context context, int layout, Cursor c,
					String[] from, int[] to) {
				super(context, layout, c, from, to);		
				alphaIndexer=new AlphabetIndexer(c, mUserLexCursor.getColumnIndex(SWI_Constants.WORD), " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
			}

			@Override
			public int getPositionForSection(int section) {
				// TODO Auto-generated method stub
				return alphaIndexer.getPositionForSection(section);
			}

			@Override
			public int getSectionForPosition(int position) {
				// TODO Auto-generated method stub
				return alphaIndexer.getSectionForPosition(position);
			}

			@Override
			public Object[] getSections() {
				// TODO Auto-generated method stub
				return alphaIndexer.getSections();
			}
	    	
	    }


}
