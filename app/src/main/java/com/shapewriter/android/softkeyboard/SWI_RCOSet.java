package com.shapewriter.android.softkeyboard;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.shapewriter.android.softkeyboard.SWI_Constants;
import com.shapewriter.android.softkeyboard.SWI_CommandStrokes.Command;
import com.shapewriter.android.softkeyboard.recognizer.Key;
import com.shapewriter.android.softkeyboard.recognizer.KeySet;
import com.shapewriter.android.softkeyboard.recognizer.KeyboardInfo;
import com.shapewriter.android.softkeyboard.recognizer.RCO;

public class SWI_RCOSet {	
	private static final long MESSAGE_DISPLAY_INTERVAL = 2000L; // ms
	private RCO mRecognizer1 = null;
	private RCO mCmdRecognizer1 = null;
	private SWI_CommandStrokes mCommandStrokes1 = null;
	private SWI_PageManager mPageManager1 = null;
	
	
	private RCO mRecognizer2 = null;
	private RCO mCmdRecognizer2 = null;
	private SWI_CommandStrokes mCommandStrokes2 = null;
	private SWI_PageManager mPageManager2 = null;
	
    private Handler mHandler;
    private String mFirstLanguage;
    private String mSecondLanguage;
	private Context mContext;
	
	private static SWI_RCOSet mInstance = null;
	
	private SWI_RCOSet() {
	}
	
	public static SWI_RCOSet instance() {
		if (mInstance == null) {
			mInstance = new SWI_RCOSet();
		}
		return mInstance;
	}
	
	public static void destroy() {
		if (mInstance != null) {
			mInstance.deInit();
			mInstance = null;
		}
	}
	
	public void init(Context context, SWI_LanguageResolver languageResolver, 
			String firstLanguage, String secondLanguage) {
		if (mHandler == null) {
			mHandler = new Handler();		
			mFirstLanguage = firstLanguage;
			mSecondLanguage = secondLanguage;
			initalize(context, languageResolver);	
		}
	}
	
	public void deInit(){
		if (mRecognizer1 != null) {
			mRecognizer1.Destroy();
			mRecognizer1 = null;
		}
		
		if (mCmdRecognizer1 != null) {
			mCmdRecognizer1.Destroy();
			mCmdRecognizer1 = null;
		}
		
		if(mCommandStrokes1 != null){
			mCommandStrokes1.destroy();
			mCommandStrokes1 = null;
		}
		
		if (mRecognizer2 != null) {
			mRecognizer2.Destroy();
			mRecognizer2 = null;
		}
		
		if (mCmdRecognizer2 != null) {
			mCmdRecognizer2.Destroy();
			mCmdRecognizer2 = null;
		}
		
		if(mCommandStrokes2 != null){
			mCommandStrokes2.destroy();
			mCommandStrokes2 = null;
		}
		
		mPageManager1 = null;
		mPageManager2 = null;
		mHandler = null;
	}



	public SWI_CommandStrokes getCommandStrokes(String language) {
		if(language.equals(mFirstLanguage)){
			return mCommandStrokes1;
		}
		else if(language.equals(mSecondLanguage)){
			return mCommandStrokes2;
		}
		else{
			return null;
		}
	}

	public void setPageManager(String language, SWI_PageManager pageManager){
		if(language.equals(mFirstLanguage)){
			mPageManager1 = pageManager;
			if(mCommandStrokes1 == null){
				createCommandStrokes(language);
			}
		}
		else if(language.equals(mSecondLanguage)){
			mPageManager2 = pageManager;
			if(mCommandStrokes2 == null){
				createCommandStrokes(language);
			}
		}
	}
	
	private void initalize(Context context, SWI_LanguageResolver languageResolver) {
		try {
			if(mFirstLanguage != null){
				mRecognizer1 = new RCO();
				mCmdRecognizer1 = new RCO();
				mRecognizer1.LoadFromBuffer(SWI_DataIntegration.getLexicon(languageResolver.getDirectory(mFirstLanguage)));
				mCmdRecognizer1.LoadFromBuffer(SWI_DataIntegration.getCmdLexicon(languageResolver.getDirectory(mFirstLanguage)));
			}
			
			if(mSecondLanguage != null){
				mRecognizer2 = new RCO();
				mCmdRecognizer2 = new RCO();
				mRecognizer2.LoadFromBuffer(SWI_DataIntegration.getLexicon(languageResolver.getDirectory(mSecondLanguage)));
				mCmdRecognizer2.LoadFromBuffer(SWI_DataIntegration.getCmdLexicon(languageResolver.getDirectory(mSecondLanguage)));
			}
			
			mContext = context;
			
//			SWI_UtilSingleton.instance().loadDataFromUserDict();
//			SWI_UtilSingleton.instance().loadDataFromContacts();
			
			TransferUserWordsToRCO();
			TransferActiveWordsToRCO();
			
		} catch (Exception ex) {}
		
	}
	
	private void TransferActiveWordsToRCO() {
		Cursor active_words_cursor = getActiveEvents();	
		if (active_words_cursor == null) return;
		if (active_words_cursor.moveToFirst()){
			do {
				String language = active_words_cursor.getString(1);
				String word = active_words_cursor.getString(2);
				if(mRecognizer1 != null && mFirstLanguage.equals(language)){
					mRecognizer1.SetWordActive(word, true);
				}
				if(mRecognizer2 != null && mSecondLanguage.equals(language)){
					mRecognizer2.SetWordActive(word, true);
				}
			} while (active_words_cursor.moveToNext());
		}
		active_words_cursor.close();		
	}
	
	private void TransferUserWordsToRCO() {
		Cursor user_words_cursor = getUserEvents();
		if (user_words_cursor == null) return;
		ArrayList<String> user_words1 = new ArrayList<String>();
		ArrayList<String> user_words2 = new ArrayList<String>();
		
		if(user_words_cursor.moveToFirst()){
			do{
				String language = user_words_cursor.getString(1);
				String word = user_words_cursor.getString(2);
				//Log.e("the language is::",""+language);
				//Log.e("the word is::",""+word);
				if(mRecognizer1 != null && !mRecognizer1.IsWordExistInRCO(word)){
					user_words1.add(word);
				}
				if(mRecognizer2 != null && !mRecognizer2.IsWordExistInRCO(word)){
					user_words2.add(word);
				}
				
			}while (user_words_cursor.moveToNext());
		}
		
		user_words_cursor.close();

		int size = user_words1.size();
		String[] words = new String[size];
		words = user_words1.toArray(words);
	
		if (mRecognizer1 != null) {
			mRecognizer1.AddWordsToRCO(words, size);
		}
		words = null;
		
		size = user_words2.size();
		words = new String[size];
		words = user_words2.toArray(words);
		
		if (mRecognizer2 != null) {
			mRecognizer2.AddWordsToRCO(words, size);
		}
		user_words1.clear();
		user_words1 = null;
		user_words2.clear();
		user_words2 = null;	
		words = null;
	}

	
	public void map(String language, SWI_KeyboardBase keyboardBase ){
		if(language.equals(mFirstLanguage)){
			if ((mRecognizer1 == null) || (mCmdRecognizer1 == null)){
				return;
			}
		}
		else if(language.equals(mSecondLanguage)){
			if ((mRecognizer2 == null) || (mCmdRecognizer2 == null)){
				return;
			}
		}
		
		
		KeySet key_mapping_set = new KeySet();
		
		initRcoKeyMappingSet(keyboardBase, key_mapping_set);
	
		KeyboardInfo kbd_info = new KeyboardInfo();
			
		initKbdInfo(keyboardBase, kbd_info, key_mapping_set);
		
		if(language.equals(mFirstLanguage)){
			mRecognizer1.Remap(key_mapping_set, kbd_info);
			mCmdRecognizer1.Remap(key_mapping_set, kbd_info);	
			mRecognizer1.SetParameters(6, 6, 80, 30, (int)kbd_info.letter_key_width, (int)kbd_info.letter_key_height);
			mCmdRecognizer1.SetParameters(6, 6, 80, 30, (int)kbd_info.letter_key_width, (int)kbd_info.letter_key_height);
		}
		
		else{
			mRecognizer2.Remap(key_mapping_set, kbd_info);
			mCmdRecognizer2.Remap(key_mapping_set, kbd_info);	
			mRecognizer2.SetParameters(6, 6, 80, 30, (int)kbd_info.letter_key_width, (int)kbd_info.letter_key_height);
			mCmdRecognizer2.SetParameters(6, 6, 80, 30, (int)kbd_info.letter_key_width, (int)kbd_info.letter_key_height);
		}
		
		/*
		else if (this.mGLKeyboard.GetCurrKbdLayout() == -2) {
			mRecognizer.SetParameters(5, 5, 80, 30, (int)kbd_info.letter_key_width, (int)kbd_info.letter_key_height);
			mCmdRecognizer.SetParameters(5, 5, 80, 30, (int)kbd_info.letter_key_width, (int)kbd_info.letter_key_height);
		}
		*/
		
		key_mapping_set = null;
		kbd_info = null;
	}
		
	public RCO getRCO(String language) {
		if(language.equals(mFirstLanguage)){
			return mRecognizer1;
		}
		else if(language.equals(mSecondLanguage)){
			return mRecognizer2;
		}
		else{
			return null;
		}
	}
		
	public boolean addWordToLexicon(Context context, RCO rco, String word) {	
		ContentValues values = new ContentValues();
		if(mRecognizer1 == rco){
			values.put(SWI_Constants.LANGUAGE, mFirstLanguage);
			values.put(SWI_Constants.WORD, word);
			mRecognizer1.AddWordToRCO(word);
			if(mRecognizer2 != null && mRecognizer2.IsWordExistInRCO(word)){
				mRecognizer2.AddWordToRCO(word);
			}
		}
		else if(mRecognizer2 == rco){
			values.put(SWI_Constants.LANGUAGE, mSecondLanguage);
			values.put(SWI_Constants.WORD, word);
			mRecognizer2.AddWordToRCO(word);
			if(mRecognizer1 != null && mRecognizer1.IsWordExistInRCO(word)){
				mRecognizer1.AddWordToRCO(word);
			}
		}
		
		Cursor cur = mContext.getContentResolver().query(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION, 
				null, SWI_Constants.WORD + "=" + "\"" + word + "\"", 
				null, null);
		if(cur.getCount() == 0){
			context.getContentResolver().insert(SWI_Constants.CONTENT_URI_FOR_USER_LEXCION,
					values);
		}
		
		return true;
	}
		
	public boolean addActiveWord(Context context, RCO rco, String aWord) {
		if (rco.IsWordActive(aWord)) return true;
		ContentValues values = new ContentValues();
		if(mRecognizer1 == rco){
			values.put(SWI_Constants.LANGUAGE, mFirstLanguage);
			values.put(SWI_Constants.WORD, aWord);
		}
		else if(mRecognizer2 == rco){
			values.put(SWI_Constants.LANGUAGE, mSecondLanguage);
			values.put(SWI_Constants.WORD, aWord);
		}
		
		
		context.getContentResolver().insert(SWI_Constants.CONTENT_URI_FOR_ACTIVE_LEXCION,
				values);		
		return rco.SetWordActive(aWord, true);
	}
	
	private Cursor getUserEvents() {
		try {
			return mContext.getContentResolver().query(
					SWI_Constants.CONTENT_URI_FOR_USER_LEXCION, null, null, null,
					null);
		} catch (Exception e) {
			return null;
		}
	}
	


	private Cursor getActiveEvents() {
		try {
			return mContext.getContentResolver().query(
					SWI_Constants.CONTENT_URI_FOR_USER_LEXCION, null, null, null,
					null);
		} catch (Exception e) {
			return null;
		}
		
	}
	
	/**
	 * Private Methods
	 */
	
	private static int initRcoKeyMappingSet(SWI_KeyboardBase keyboardBase, KeySet key_mapping_set) {
		int key_width = -1;
		int rco_size = 0;

		int key_size = keyboardBase.keyList.size();

		for (int i = 0; i < key_size; ++i) {
			if(keyboardBase.keyList.get(i).isRCOKey()){
				rco_size += keyboardBase.keyList.get(i).mapList.size();
			}
		}
		rco_size *= 2;

		key_mapping_set.key_mapping_count = rco_size;
		key_mapping_set.keys = new Key[rco_size];
		key_mapping_set.n_ignore_chars = 4;
		key_mapping_set.ignore_chars = "'_-.";

		for (int i = 0, curr = 0; i < key_size; ++i) {
			SWI_SoftKeyBase key = keyboardBase.keyList.get(i);
			if (!key.isRCOKey()) {
				continue;
			}
			if (key_width < 0)
				key_width = key.width;

			final double centerX = key.left + (key.width / 2);
			final double centerY = key.top + (key.height / 2);

			for(int j = 0 ; j < key.mapList.size(); j++){
				key_mapping_set.keys[curr] = new Key();
				key_mapping_set.keys[curr].key_label = Character
						.toString(Character.toLowerCase(key.mapList.get(j).charValue()));
				
				
				key_mapping_set.keys[curr].x = centerX;
				key_mapping_set.keys[curr].y = centerY;
				key_mapping_set.keys[rco_size / 2 + curr] = new Key();
				key_mapping_set.keys[rco_size / 2 + curr].key_label = Character
						.toString(Character.toUpperCase(key.mapList.get(j).charValue()));
				key_mapping_set.keys[rco_size / 2 + curr].x = centerX;
				key_mapping_set.keys[rco_size / 2 + curr].y = centerY;
	
				++curr;
			}
		}
		return key_width;
	}

	private static void initKbdInfo(SWI_KeyboardBase keyboardBase, KeyboardInfo kbd_info, KeySet key_mapping_set) {
		int i;
		Key key;
		float y_r = 0f, y_g = 0f, y_b = 0f;	
		int key_size = keyboardBase.keyList.size();		
	
		for (i = 0; i<key_size; ++i) {
			SWI_SoftKeyBase key_item = keyboardBase.keyList.get(i);
			if (key_item.valueList.get(0).charAt(0) == 'q') {
				kbd_info.letter_key_width = key_item.width;
				kbd_info.letter_key_height = key_item.height;
				break;
			}
		}
		
	
		for (i = 0; i<key_mapping_set.key_mapping_count; i++) {
			key = key_mapping_set.keys[i];
			if ((key.key_label).equalsIgnoreCase("r")) {
				y_r = (float)key.y;
			} else if ((key.key_label).equalsIgnoreCase("g")) {
				y_g = (float)key.y;				
			} else if ((key.key_label).equalsIgnoreCase("b")) {
				y_b = (float)key.y;				
			}
		}
		
		kbd_info.row_break1 = (y_r + y_g)/2;
		kbd_info.row_break2 = (y_g + y_b)/2;

		kbd_info.kbd_type = 0; // 0 stands for QWERTY		
		 
		/*
		else if (this.mGLKeyboard.GetCurrKbdLayout() == -2) {
			kbd_info.row_break1 = -1;
			kbd_info.row_break2 = -1;	
			kbd_info.kbd_type = 1; // 1 stands for other layout	
		}*/
	}
	
	
	
	
	
	
	private void createCommandStrokes(String language) {
		
		if(language.equals(mFirstLanguage)){
		
			List<SWI_CommandStrokes.Command> commands = new ArrayList<SWI_CommandStrokes.Command>();
			commands.add(new SWI_CommandStrokes.Command("#copy", "Copy",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager1 != null) {
								mPageManager1.CopyCommand(false);
							}
						}
					}));
			
			commands.add(new SWI_CommandStrokes.Command("#cut", "Cut",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager1 != null) {
								mPageManager1.CopyCommand(true);
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#paste", "Paste",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager1 != null) {
								mPageManager1.PasteCommand();
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#all", "Select All",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager1 != null) {
								mPageManager1.SelectAllCommand();
							}
						}
					}));
	
				
			commands.add(new SWI_CommandStrokes.Command("#replay", "Replay",
					new Runnable() {
						@Override
						public void run() {
							if(mPageManager1 != null){
								mPageManager1.replay();	
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#hide", "Hide",
					new Runnable() {
						@Override
						public void run() {
							if(mPageManager1 != null){
								mPageManager1.handleClose();
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#close", "Close",
					new Runnable() {
						@Override
						public void run() {
							if(mPageManager1 != null){
								mPageManager1.handleClose();
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#settings", "Settings",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager1 != null) {
								mPageManager1.launchSettings();
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#help", "Help",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager1 != null) {
								mPageManager1.launchHelp();
							}
						}
					}));
			commands.add(new SWI_CommandStrokes.Command("#video", "Video",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager1 != null) {
								mPageManager1.launchVideo();
							}
						}
					}));
			commands.add(new SWI_CommandStrokes.Command("#literal",
					" ", new Runnable() {
						@Override
						public void run() {
							if (mPageManager1 != null) {
								mPageManager1.handleLiteralMode();
							}
						}
					}));

			commands.add(new SWI_CommandStrokes.Command("#game", "Balloon Game",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager1 != null) {
								mPageManager1.launchGame();
							}
						}
					}));	
			
			
			mCommandStrokes1 = new SWI_CommandStrokes(commands, 
					(SWI_KeyboardViewTrace)mPageManager1.mKeyboardView, mCmdRecognizer1);
					
			mCommandStrokes1.setCallback(mCommandStrokesCallback1);
			mCommandStrokes1.setActivated(false);
		}
		
		
		else{
			
			List<SWI_CommandStrokes.Command> commands = new ArrayList<SWI_CommandStrokes.Command>();
			commands.add(new SWI_CommandStrokes.Command("#copy", "Copy",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager2 != null) {
								mPageManager2.CopyCommand(false);
							}
						}
					}));
			
			commands.add(new SWI_CommandStrokes.Command("#cut", "Cut",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager2 != null) {
								mPageManager2.CopyCommand(true);
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#paste", "Paste",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager2 != null) {
								mPageManager2.PasteCommand();
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#all", "Select All",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager2 != null) {
								mPageManager2.SelectAllCommand();
							}
						}
					}));
	
				
			commands.add(new SWI_CommandStrokes.Command("#replay", "Replay",
					new Runnable() {
						@Override
						public void run() {
							if(mPageManager2 != null){
								mPageManager2.replay();
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#hide", "Hide",
					new Runnable() {
						@Override
						public void run() {
							if(mPageManager2 != null){
								mPageManager2.handleClose();
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#close", "Close",
					new Runnable() {
						@Override
						public void run() {
							if(mPageManager2 != null){
								mPageManager2.handleClose();
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#settings", "Settings",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager2 != null) {
								mPageManager2.launchSettings();
							}
						}
					}));
	
			commands.add(new SWI_CommandStrokes.Command("#help", "Help",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager2 != null) {
								mPageManager2.launchHelp();
							}
						}
					}));
			commands.add(new SWI_CommandStrokes.Command("#video", "Video",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager2 != null) {
								mPageManager2.launchVideo();
							}
						}
					}));
			commands.add(new SWI_CommandStrokes.Command("#literal",
					" ", new Runnable() {
						@Override
						public void run() {
							if (mPageManager2 != null) {
								mPageManager2.handleLiteralMode();
							}
						}
					}));
	/*
			commands.add(new SWI_CommandStrokes.Command("#game", "Balloon Game",
					new Runnable() {
						@Override
						public void run() {
							if (mPageManager != null) {
								mPageManager.launchGame();
							}
						}
					}));
		*/	
			
			
				mCommandStrokes2 = new SWI_CommandStrokes(commands, 
						(SWI_KeyboardViewTrace)mPageManager2.mKeyboardView, mCmdRecognizer2);
						
				mCommandStrokes2.setCallback(mCommandStrokesCallback2);
				mCommandStrokes2.setActivated(false);
			
		}
	}

	
	private SWI_CommandStrokes.Callback mCommandStrokesCallback1 = new SWI_CommandStrokes.Callback() {
		@Override
		public void commandRecognized(Command command, boolean complete) {
			if (complete) {
				// Trigger command...
				if (command.command.equals("#literal")) {
					if (mPageManager1 != null) {
						if (mPageManager1.autoEdit()) {
							command.caption = "Literal mode on";
						} else {
							command.caption = "Literal mode off";
						}
					}
				}	
				mPageManager1.showMessage(command.caption, true);
				mHandler.postDelayed(removeMessageAction1, MESSAGE_DISPLAY_INTERVAL);
				command.action.run();
			} else {
				if (command.command.equals("#literal")) {
					if (mPageManager1 != null) {
						if (mPageManager1.autoEdit()) {
							command.caption = "Literal mode on";
						} else {
							command.caption = "Literal mode off";
						}
					}
				}	
				mPageManager1.showMessage(command.caption, false);
			}
		}

		@Override
		public void commandCancel() {
			mPageManager1.hideMessage();
		} 
	};
	
	private SWI_CommandStrokes.Callback mCommandStrokesCallback2 = new SWI_CommandStrokes.Callback() {
		@Override
		public void commandRecognized(Command command, boolean complete) {
			if (complete) {
				// Trigger command...
				if (command.command.equals("#literal")) {
					if (mPageManager2 != null) {
						if (mPageManager2.autoEdit()) {
							command.caption = "Literal mode on";
						} else {
							command.caption = "Literal mode off";
						}
					}
				}	
				mPageManager2.showMessage(command.caption, true);
				mHandler.postDelayed(removeMessageAction2, MESSAGE_DISPLAY_INTERVAL);
				command.action.run();
			} else {
				if (command.command.equals("#literal")) {
					if (mPageManager2 != null) {
						if (mPageManager2.autoEdit()) {
							command.caption = "Literal mode on";
						} else {
							command.caption = "Literal mode off";
						}
					}
				}	
				mPageManager2.showMessage(command.caption, false);
			}
		}

		@Override
		public void commandCancel() {
			mPageManager2.hideMessage();
		} 
	};
	
	private Runnable removeMessageAction1 = new Runnable() {
		@Override
		public void run() {
			if (mCommandStrokes1 != null) {
				mCommandStrokes1.setActivated(false);
				mPageManager1.hideMessage();	
			}
		}
	};
	
	private Runnable removeMessageAction2 = new Runnable() {
		@Override
		public void run() {
			if (mCommandStrokes2 != null) {
				mCommandStrokes2.setActivated(false);
				mPageManager2.hideMessage();	
			}
		}
	};
	
}
