package com.shapewriter.android.softkeyboard;


import java.util.ArrayList;
import java.util.HashMap;

import com.shapewriter.android.softkeyboard.SWI_SoftkeyboardService.Word;
import com.shapewriter.android.softkeyboard.recognizer.RCO;


import android.text.method.TextKeyListener;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputConnection;

public class SWI_PageEnglishTrace extends SWI_PageManager {
	
	
	private String mLastTracedWord = null;
	private SWI_SoftkeyboardService mService;
	private boolean mDisableAutoEdit = false;
	private int mPreCursorPos = -1;
	private HashMap<String, ArrayList<String>> mCachedCandidateWordList;
	private boolean mbIgnoreKeyboardRefresh = false;
	private boolean mbJustSendTraceText = false;
	private RCO mRCO;
	private SWI_RCOSet mRCOSet;
	
	public SWI_PageEnglishTrace(SWI_SoftkeyboardService service) {
		super(service);
		// TODO Auto-generated constructor stub
		mService = service;
		mCachedCandidateWordList = new HashMap<String, ArrayList<String>>();
	}
	
	@Override
	public void destroy(){
		mCachedCandidateWordList.clear();
		mCachedCandidateWordList = null;
		mLastTracedWord = null;
		mService = null;
		mRCO = null;
		mRCOSet = null;
		super.destroy();
	}
	
	@Override
	public void setRCO(SWI_RCOSet rcoSet, RCO rco){
		mRCOSet = rcoSet;
		mRCO = rco;
		mKeyboardView.setRCO(rco);
	}
	
	@Override
	public void setCmdStrokes(SWI_CommandStrokes commandStrokes){
		mKeyboardView.setCmdStrokes(commandStrokes);
	}
	
	@Override
	public void setDisableAutoEdit(boolean disable){
		mDisableAutoEdit = disable;
	}
	
	@Override
	public void clear(){
		mAuiViews[0].clearCandidate();
		mAuiViews[0].showLogo(true);
		mAuiViews[0].show();
	}
	
	@Override
	public void keyboardResultPrepare(){
		mAuiViews[0].clearCandidate();
	}

	@Override
	public void addKeyboardResult(String text,double distance,int type){
		mAuiViews[0].addCandidate(text);
		mAuiViews[0].addDistanceAndTpye(distance, type);
	}
		
	@Override
	public void keyboardResultDone(boolean bSend){
		if(bSend){			 
			mAuiViews[0].sendFirstCandidate();
			mAuiViews[0].initializeAuiLayout();
			mbJustSendTraceText = true;
		}
		mAuiViews[0].show();
	}
	
	@Override
	public void replay(){
		mKeyboardView.replay();
	}
	
	@Override
	public boolean autoEdit(){
		return ! mDisableAutoEdit;
	}
	
	@Override
	public void onUpdateSelection(int oldSelStart, int oldSelEnd,
			int newSelStart, int newSelEnd, int candidatesStart,
			int candidatesEnd) {
		
	
		
		super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
				candidatesStart, candidatesEnd);

		
				
		if (mbIgnoreKeyboardRefresh) {
			mbIgnoreKeyboardRefresh = false;
			return;
		}

		if(mbJustSendTraceText){
			mbJustSendTraceText = false;
			return;
		}
		

		ExtractedText extractedText = mService.getExtractedText();
		if (mKeyboardView == null || extractedText == null
				|| extractedText.text.length() == 0) {
			return;
		}

		String text = extractedText.text.toString();
		
		if (newSelStart == newSelEnd) {			
			int idx = newSelEnd;
			if (idx >= 0 && idx <= text.length()) {
				Word w = mService.getWordByPos(text, newSelStart);
				if (w.mEnd > w.mStart) {
					
					mKeyboardView.mIsPunctuation = false;
					
					String str = text.substring(w.mStart, w.mEnd == text
							.length() ? w.mEnd : w.mEnd + 1);
					
					if ( str == null || mLastTracedWord == null ){
							//str.compareToIgnoreCase(mLastTracedWord.trim()) == 0){ 
						return;
					}
					mAuiViews[0].clearCandidate();
					String tmp = str.toLowerCase();
					if (mKeyboardView.isWordInRCOLexicon(tmp) || mKeyboardView.isWordInRCOLexicon(str)) {
						if (mCachedCandidateWordList.containsKey(str.toLowerCase())) {
							mAuiViews[0].addCandidate(mCachedCandidateWordList.get(str.toLowerCase()));
						} else {						
							mKeyboardView.showCandidateWordListByResamplePoints(str);
						}
						mAuiViews[0].initializeAuiLayout();
					} else {
						if ((str.length() > 1) && (str.length() < 50)) {	
							informAddStringToLexicon(str);
						}
					}
				}
				else if(!mKeyboardView.mIsPunctuation){
					mAuiViews[0].clearCandidate();
				}
			}
			mAuiViews[0].show();	
		} else if (newSelStart != newSelEnd && mAuiViews[0] != null) {
			mAuiViews[0].show();
		}
	}
	
	private void informAddStringToLexicon(String word) {
		mAuiViews[0].clearCandidate();
		mAuiViews[0].showAddNewWord();
		mAuiViews[0].addCandidate("Add [" + word + "]");
		mAuiViews[0].initializeAuiLayout();
		mAuiViews[0].show();
	}
	
	@Override
	public void addNewWord(String word){
		mRCOSet.addWordToLexicon(mService, mRCO, word);
	}
	
	@Override
	public void addCachedCandidateWordList(String word, ArrayList<String> array) {
		if (mCachedCandidateWordList != null) {
			if (mCachedCandidateWordList.containsKey(word.toLowerCase())) return;
			if (mCachedCandidateWordList.size() >= 1000) {
				mCachedCandidateWordList.clear();
			}
			mCachedCandidateWordList.put(word.toLowerCase(), array);
		}
	}
	
	@Override
	public boolean handleKey(String label, String value){
		if (!super.handleKey(label, value)){

			if (label.equals(SWI_SoftKeyBase.LABEL_SPACE)) {
				mKeyboardView.mIsPunctuation = false;
				sendText(" ", true, true);
			} else if (label.equals(SWI_SoftKeyBase.LABEL_BACKSPACE)) {
				mAuiViews[0].clearCandidate();
				mAuiViews[0].show();
				mService.sendText(null, mLengthOfSentString);
				mLengthOfSentString = 0;
			} else {
				sendText(value, true, true);
			}
		}
		return true;
	}
	
	/**
	 * Handler the key with value list that should be sent to AUI
	 */
	@Override
	public void handleKey(SWI_SoftKeyBase key){
		mAuiViews[0].clearCandidate();
		mAuiViews[0].addCandidate(key.valueList);
		mAuiViews[0].setCurSelectIndex(0);
		mAuiViews[0].initializeAuiLayout();
		mAuiViews[0].show();
		mService.sendText(key.valueList.get(0), 0);
		mLengthOfSentString = key.valueList.get(0).length();
	}

	@Override
	public void receiveKeyboardText(String text, boolean bInsert){
		sendText(text, bInsert, true);
	}
	
	@Override
	public void receiveAuiText(String auiName, String text, boolean bInsert){
		if (mKeyboardView.mIsPunctuation) { // If in '.', '@' or ':)' punctuation set.
			mService.sendText(text,mLengthOfSentString);
			mLengthOfSentString = text.length();
		} else {
			sendText(text, bInsert, true);
			mRCOSet.addActiveWord(mService, mRCO, text);
		}
	}
	
		
	private int sendText(String str, boolean bInsert, boolean bInsertSpace) {	
		if (SWI_UtilSingleton.instance().isExpired()) {
			SWI_UtilSingleton.instance().toastTrialInfo();
			return 0;
		}
		
		int iResult = -1;
		ExtractedText extractedText = mService.getExtractedText();
		if (extractedText == null) {
			return -1;
		}

		int idxStart = extractedText.selectionStart;
		int idxEnd = extractedText.selectionEnd;

		if (str != null && bInsert && idxStart != idxEnd) {
			sendText(null, false, false);
			return sendText(str, bInsert, bInsertSpace);
		}

		if (str != null  && bInsertSpace) {
			mLastTracedWord = str;
		}

		// int oldlen = idxEnd - idxStart + 1;
		if (idxEnd == -1) {
			idxEnd = Math.max(0, extractedText.text.length() - 1);
		}

		if (idxStart == -1) {
			idxStart = Math.max(0, extractedText.text.length() - 1);
		}

		String currStr = extractedText.text.toString();
		InputConnection connection = mService.getCurrentInputConnection();

		if (bInsert) {
			boolean bIgnoreKeyboardRefresh = true;
			String szTrimStr = str.trim();

			if (!mDisableAutoEdit && szTrimStr.length() > 1 && idxEnd > -1
					&& bInsertSpace) {
				if (idxEnd == 1) {
					char prec = currStr.charAt(0);
					if (!Character.isWhitespace(prec) && prec != '\n'
							&& prec != '\t' && !isSymbolSet0(prec)) {
						str = " " + str;
					}
				} else if (idxEnd > 1) {
					char ccLast = currStr.charAt(idxEnd - 2);
					char cLast = currStr.charAt(idxEnd - 1);
					if (!Character.isWhitespace(cLast)
							&& !isSymbolSet1(cLast, ccLast)) {
						str = " " + str;
					}
				}

				char cCurr = idxEnd < currStr.length() ? currStr.charAt(idxEnd)
						: '_';

				if (Character.isLetter(cCurr)) {
					str += " ";
				}
			} else if (!mDisableAutoEdit && szTrimStr.compareTo("i") == 0) {

				if (idxEnd > 0) {
					char cLast = currStr.charAt(idxEnd - 1);

					if (idxEnd == mPreCursorPos || isSymbolSet2(cLast)) {
						str = " I";

					} else {
						if (cLast == ' ' && idxEnd > 1) {

							char ccLast = currStr.charAt(idxEnd - 2);
							if (isSymbolSet4(ccLast)) {

								str = "I";
							} else {

								str = "i";
							}
						} else {

							str = "i";
						}
					}
				}
			} else if (!mDisableAutoEdit && szTrimStr.compareTo("a") == 0) {
				
				if (idxEnd > 0) {
					char cLast = currStr.charAt(idxEnd - 1);
					if (idxEnd == mPreCursorPos || isSymbolSet4(cLast)) {
						str = " a";
					} else if (isSymbolSet3(cLast)) {
						str = " A";
					} else if (isBeforeWord(currStr, idxEnd)) {
						str = "a ";
					} else {
						str = "a";
					}
				}
			} else if (szTrimStr.length() == 1) {
				bIgnoreKeyboardRefresh = false;
			}

			if (bIgnoreKeyboardRefresh)
				connection.beginBatchEdit();

			connection.commitText(str, 1);

			if (bIgnoreKeyboardRefresh)
				connection.endBatchEdit();

			if (str.length() > 1 && str.charAt(str.length() - 1) == ' ') {
				int offset = idxEnd + str.length() - 1;

				connection.beginBatchEdit();
				connection.setSelection(offset, offset);
				connection.endBatchEdit();
			}

			szTrimStr = str.trim();
			if (szTrimStr.length() > 1) {
				char cfirst = str.charAt(0);
				char clast = str.charAt(str.length() - 1);
				int iTrimStrLen = szTrimStr.length();

				if (cfirst == ' ' && clast == ' ') {
					mPreCursorPos = idxEnd + iTrimStrLen + 1;
				} else if (cfirst != ' ' && clast == ' ') {
					mPreCursorPos = idxEnd + iTrimStrLen;
				} else if (cfirst == ' ' && clast != ' ') {
					mPreCursorPos = idxEnd + iTrimStrLen + 1;
				} else {
					mPreCursorPos = idxEnd + iTrimStrLen;
				}

			} else if (str.length() > 0
					&& (Character.isSpaceChar(str.charAt(0)) || str
							.equals("\n"))) {
				mPreCursorPos = -2;
			}
			toggleFirstCharacterCase(idxEnd, str);
		} else {			// bInsert = false
			Word currWord = mService.getWordByPos(currStr, idxEnd);
			String keyWord = currStr.substring(currWord.mStart,
					currWord.mEnd == currStr.length() ? currWord.mEnd
							: currWord.mEnd + 1);
			if (str != null) {
				if (!str.equals(keyWord)) {
					int delLeft = idxEnd - currWord.mStart;
					int delRight = currWord.mEnd >= extractedText.text.length() ? currWord.mEnd
							- idxEnd
							: currWord.mEnd - idxEnd + 1;

					connection.beginBatchEdit();
					if (idxEnd == idxStart && idxEnd > currWord.mStart
							&& idxEnd <= currWord.mEnd) {
						int selectionOffset = idxEnd
								+ extractedText.startOffset + delRight;
						selectionOffset = Math.min(selectionOffset, currStr
								.length()
								+ extractedText.startOffset);
						connection.setSelection(selectionOffset,
								selectionOffset);

						delLeft += delRight;
						delRight = 0;
					}

					connection.deleteSurroundingText(delLeft, delRight);

					if (mCachedCandidateWordList.containsKey(keyWord.toLowerCase())) {
						mCachedCandidateWordList.put(str.toLowerCase(),
								mCachedCandidateWordList.get(keyWord.toLowerCase()));
						mCachedCandidateWordList.remove(keyWord.toLowerCase());
					}

					connection.commitText(str, 1);

					if (str.length() != delLeft + delRight)
						mbIgnoreKeyboardRefresh = true;

					connection.endBatchEdit();

					toggleFirstCharacterCase(currWord.mStart, str);
				}
			} else if (idxEnd >= 0) {
				if (idxStart != idxEnd && idxStart > -1 && idxEnd > -1) {
					connection.beginBatchEdit();
					connection.commitText("", 0);
					connection.endBatchEdit();
				} else 	if (mLastTracedWord != null	            // Delete the whole word
					
						&& mLastTracedWord.toLowerCase().equals(keyWord.toLowerCase())
						&& idxEnd > currWord.mStart
						&& mCachedCandidateWordList.containsKey(keyWord.toLowerCase())
						) {
											
					int delLeft = idxEnd - currWord.mStart;
					int delRight = currWord.mEnd >= extractedText.text.length() ? currWord.mEnd
							- idxEnd
							: currWord.mEnd - idxEnd + 1;

					connection.beginBatchEdit();
					if (idxEnd == idxStart && idxEnd > currWord.mStart
							&& idxEnd <= currWord.mEnd) {
						int selectionOffset = idxEnd
								+ extractedText.startOffset + delRight;
						selectionOffset = Math.min(selectionOffset, currStr
								.length()
								+ extractedText.startOffset);
						connection.setSelection(selectionOffset,
								selectionOffset);

						delLeft += delRight;
						delRight = 0;
					}

					if (currWord.mStart > 1) {
						if (extractedText.text.charAt(currWord.mStart - 1) == ' ') {
							delLeft += 1;
						}
					}

					connection.deleteSurroundingText(delLeft, delRight);
					connection.endBatchEdit();

					mCachedCandidateWordList.remove(keyWord.toLowerCase());
					mAuiViews[0].clearCandidate();
					mAuiViews[0].show();
						
				} else {
					connection.deleteSurroundingText(1, 0);
				}
			}
		}

		return iResult;
	}
	
	private static boolean isSymbolSet0(char c) {
		return (c == '\'' || c == '"' || c == '{' || c == '(' || c == '[' || c == '<');
	}

	private static boolean isSymbolSet1(char c, char cc) {
		if (c == '\'' || c == '"') {
			if (cc == ' ' || cc == '\n' || cc == '\t')
				return true;
			return false;
		}
		return (c == '{' || c == '(' || c == '[' || c == '<');
	}

	private static boolean isSymbolSet2(char c) {
		return (c == '.' || c == '?' || c == '!' || c == '}' || c == ')'
				|| c == ']' || c == '>' || c == ',' || c == ';' || c == ':');
	}

	private static boolean isSymbolSet3(char c) {
		return (c == '.' || c == '?' || c == '!');
	}

	private static boolean isSymbolSet4(char c) {
		return (c == '}' || c == ')' || c == ']' || c == '>' || c == ','
				|| c == ';' || c == ':');
	}

	private static boolean isBeforeWord(String text, int idx) {
		String currStr = text;
		if (idx > 0 && idx < currStr.length()) {
			if ((Character.isWhitespace(currStr.charAt(idx - 1)) || isSymbolSet2(currStr
					.charAt(idx - 1)))
					&& Character.isLetter(currStr.charAt(idx))) {
				return true;
			}
		}
		return false;
	}
	
	private void toggleFirstCharacterCase(int off, String str) {
		if (mDisableAutoEdit) {
			return;
		}

		ExtractedText extractedText = mService.getExtractedText();
		if (extractedText == null || extractedText.text.length() == 0) {
			return;
		}

		String text = extractedText.text.toString();
		String szTrimStr = str.trim();

		boolean bVerifyCap = text.length() > 0 ? true : false;
		int numIdx = off;
		if (numIdx < 0 || numIdx >= text.length()) {
			return;
		}

		char cFirstChar = bVerifyCap ? text.charAt(numIdx) : ' ';
		while (bVerifyCap && !Character.isLetter(cFirstChar)) {
			++numIdx;
			if ((numIdx - off) >= str.length() || numIdx >= text.length()) {
				bVerifyCap = false;
				break;
			}
			cFirstChar = text.charAt(numIdx);
		}

		if (bVerifyCap
				&& szTrimStr.length() > 0
				&& TextKeyListener.shouldCap(
						TextKeyListener.Capitalize.SENTENCES, text, numIdx)
				&& !hasSpace(szTrimStr) && isFirstCharLowerCase(szTrimStr)) {
			mService.caseKeyPressed();
		}
	}
	
	private static boolean hasSpace(String str) {
		for (int i = 0; i < str.length(); ++i) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	private static boolean isFirstCharLowerCase(String str) {
		if (str.length() == 0)
			return false;

		return Character.isLowerCase(str.charAt(0));
	}

	
	
	@Override
	public void showMessage(String message, boolean fade){
		mAuiViews[0].showMessage(message, fade);
	}
	
	@Override
	public void hideMessage(){
		mAuiViews[0].hideMessage();
	}
	
}
