package com.shapewriter.android.softkeyboard.recognizer;

public class RCO {
	
	public static final int LANGUAGE_ENGLISH = 1;
	public static final int LANGUAGE_CHINESE = 2;
	
	public RCO() {
		mRCO = 0;
	}
	
	// Create one native RCO object, and set the pointer to mRCO. After Initialize, you need to call Create
	public native void SetParameters(int aP1, int aP2, int aP3, int aP4, int aP5, int aP6);
	public native void SetRCOMode(int aRecognitionMode);
	public native boolean Remap(KeySet aKeySet, KeyboardInfo aKbdInfo);
	public native void Store(String aFilePath);
	public native void LoadFromFile(String aFilePath);
	public native void LoadFromBuffer(byte[] aBuffer);
	public native void CreateFromTextLexicon(String aFilePath, KeySet aKeyMappingSet, KeyboardInfo aKbdInfo);
	public native void CreateFromTextLexicon(byte[] aBuffer, KeySet aKeyMappingSet, KeyboardInfo aKbdInfo);
	public native ResultSet Recognize(InputSignal aInputSignal, Object aReserved);
	public native boolean AddWordToRCO(String aWord);
	public native boolean AddWordsToRCO(String[] aWords, int count);
	public native boolean IsWordExistInRCO(String aWord);
	public native boolean RemoveWordFromLexicon(String aWord);
	public native boolean SetWordActive(String aWord, boolean aActive);
	public native boolean IsWordActive(String aWord);
	public native void Destroy();
	
	public long mRCO;
}
