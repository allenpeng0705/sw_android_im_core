package com.shapewriter.android.softkeyboard;



public class SWI_DataIntegration {
	
	/**
	 * Integrate resource from Skin-Table and Language-Table. 
	 * 
	 * @param languagePath: The directory saved .XML file and RCO data, "/data/data/[language page name]/files".
	 * @param skinPath: The directory saved skin data (.PNG file), "/data/data/[skin page name]/files". 
	 * @param language: Language name, such as English, French or Chinese.
	 * @param mode: In portrait mode, mode is true. Otherwise, it's false.
	 * @return: A SWI_PageBase instance. 
	 */
	public static SWI_PageBase getPageBase(String languagePath, String skinPath, String language, boolean mode){
		if(language.equals(SWI_Language.ENGLISH))		return getPageBaseEnglish(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.CHINESE)) 	return getPageBaseChinese(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.FRENCH))	return getPageBaseFrench(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.GERMAN))	return getPageBaseGerman(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.ITALIAN))	return getPageBaseItalian(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.JAPANESE))	return getPageBaseJapanese(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.SPANISH))	return getPageBaseSpanish(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.SWEDISH))	return getPageBaseSwedish(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.FRENCHQWERTY))	return getPageBaseFrenchQwerty(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.DANISH))	return getPageBaseDanish(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.TURKISH))	return getPageBaseTurkish(languagePath, skinPath, mode);
		else if(language.equals(SWI_Language.NUMBER))	return getPageBaseNumber(languagePath, skinPath, mode);
		return null;
	}
	
	
	public static byte[] getLexicon(String languagePath){
		return SWI_FileIO.readAsByteArray(languagePath, SWI_LanguageFileIO.LEXICON1, SWI_LanguageFileIO.LEXICON2,
				SWI_LanguageFileIO.LEXICON3, SWI_LanguageFileIO.LEXICON4);
	}
	
	public static byte[] getCmdLexicon(String languagePath){
		return SWI_FileIO.readAsByteArray(languagePath, SWI_LanguageFileIO.LEXICON_CMD);
	}
	
	public static byte[] getCorrectLexicon(String languagePath){
		return SWI_FileIO.readAsByteArray(languagePath, SWI_LanguageFileIO.CORRECT_LEXICON);
	}
	
	public static byte[] getErrorLexicon(String languagePath){
		return SWI_FileIO.readAsByteArray(languagePath, SWI_LanguageFileIO.ERROR_LEXICON);
	}
	
	private static SWI_PageBase getPageBaseNumber(String languagePath, String skinPath, boolean mode){
		SWI_ParsePageXml parsePageXml = new SWI_ParsePageXml(languagePath, SWI_LanguageFileIO.NUMBER_PAGE, skinPath);		
		return parsePageXml.getPageBase();
	}
	
	private static SWI_PageBase getPageBaseEnglish(String languagePath, String skinPath, boolean mode){
		return getPageBaseWestern(languagePath, skinPath, mode);
	}
	
	private static SWI_PageBase getPageBaseFrench(String languagePath, String skinPath, boolean mode){
		return getPageBaseWestern(languagePath, skinPath, mode);
	}
	
	private static SWI_PageBase getPageBaseGerman(String languagePath, String skinPath, boolean mode){
		return getPageBaseWestern(languagePath, skinPath, mode);
	}
	
	private static SWI_PageBase getPageBaseItalian(String languagePath, String skinPath, boolean mode){
		return getPageBaseWestern(languagePath, skinPath, mode);
	}
	
	private static SWI_PageBase getPageBaseSpanish(String languagePath, String skinPath, boolean mode){
		return getPageBaseWestern(languagePath, skinPath, mode);
	}
	
	private static SWI_PageBase getPageBaseSwedish(String languagePath, String skinPath, boolean mode){
		return getPageBaseWestern(languagePath, skinPath, mode);
	}
	
	private static SWI_PageBase getPageBaseFrenchQwerty(String languagePath, String skinPath, boolean mode){
		return getPageBaseWestern(languagePath, skinPath, mode);
	}
	
	private static SWI_PageBase getPageBaseDanish(String languagePath, String skinPath, boolean mode){
		return getPageBaseWestern(languagePath, skinPath, mode);
	}
	
	private static SWI_PageBase getPageBaseTurkish(String languagePath, String skinPath, boolean mode){
		return getPageBaseWestern(languagePath, skinPath, mode);
	}

	private static SWI_PageBase getPageBaseChinese(String languagePath, String skinPath, boolean mode){
		return null;
	}
	
	private static SWI_PageBase getPageBaseJapanese(String languagePath, String skinPath, boolean mode){
		return null;
	}

	private static SWI_PageBase getPageBaseWestern(String languagePath, String skinPath, boolean mode){
		SWI_ParsePageXml parsePageXml;
		if(mode){		
			parsePageXml = new SWI_ParsePageXml(languagePath, SWI_LanguageFileIO.PORT_PAGE, skinPath);
		}
		else{
			parsePageXml = new SWI_ParsePageXml(languagePath, SWI_LanguageFileIO.LAND_PAGE, skinPath);
		}
		return parsePageXml.getPageBase();
	}
}
