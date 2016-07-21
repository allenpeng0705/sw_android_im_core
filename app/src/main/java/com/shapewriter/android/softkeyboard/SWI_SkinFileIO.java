package com.shapewriter.android.softkeyboard;


import java.io.File;





import android.content.Context;


public class SWI_SkinFileIO {
	private static final String LAND_QWERTY_AUI = "land_qwerty_aui.png";
	private static final String LAND_QWERTY_AUI_ARROW_LEFT = "land_qwerty_aui_arrow_left.png";
	private static final String LAND_QWERTY_AUI_ARROW_LEFT_DK = "land_qwerty_aui_arrow_left_dk.png";
	private static final String LAND_QWERTY_AUI_ARROW_RT = "land_qwerty_aui_arrow_rt.png";
	private static final String LAND_QWERTY_AUI_ARROW_RT_DK = "land_qwerty_aui_arrow_rt_dk.png";
	private static final String LAND_QWERTY_AUI_HILITE = "land_qwerty_aui_hilite.png";
	private static final String LAND_QWERTY_AUI_LOGO = "land_qwerty_aui_logo.png";
	private static final String LAND_QWERTY_MARGIN = "land_qwerty_margin.png";
	private static final String PORT_QWERTY_AUI = "port_qwerty_aui.png";
	private static final String PORT_QWERTY_AUI_ARROW_LEFT = "port_qwerty_aui_arrow_left.png";
	private static final String PORT_QWERTY_AUI_ARROW_LEFT_DK = "port_qwerty_aui_arrow_left_dk.png";
	private static final String PORT_QWERTY_AUI_ARROW_RT = "port_qwerty_aui_arrow_rt.png";
	private static final String PORT_QWERTY_AUI_ARROW_RT_DK = "port_qwerty_aui_arrow_rt_dk.png";
	private static final String PORT_QWERTY_AUI_HILITE = "port_qwerty_aui_hilite.png";
	private static final String PORT_QWERTY_AUI_LOGO = "port_qwerty_aui_logo.png";
	private static final String PORT_QWERTY_MARGIN = "port_qwerty_margin.png";
	private static final String QWERTY_1_HILITE = "qwerty_1_hilite.png";
	private static final String QWERTY_2_HILITE = "qwerty_2_hilite.png";
	private static final String QWERTY_3_HILITE = "qwerty_3_hilite.png";
	private static final String QWERTY_4_HILITE = "qwerty_4_hilite.png";
	private static final String QWERTY_5_HILITE = "qwerty_5_hilite.png";
	private static final String QWERTY_KEYS = "qwerty_keys.png";
	
	private static final String ENG_QWERTY_SYMBOLS = "eng_qwerty_symbols.png";
	private static final String FRE_QWERTY_SYMBOLS = "fre_qwerty_symbols.png";
	private static final String GER_QWERTY_SYMBOLS = "ger_qwerty_symbols.png";

	private static final String NUMBER_1_HILITE = "number_1_hilite.png";
	private static final String NUMBER_2_HILITE = "number_2_hilite.png";
	private static final String NUMBER_KEYBOARD = "number_keyboard.png";
	private static final String NUMBER_MARGIN = "number_margin.png";
	private static final String NUMBER_SYMBOLS = "number_symbols.png";
	
	private static final String [] ALL ={
		LAND_QWERTY_AUI, LAND_QWERTY_AUI_ARROW_LEFT, LAND_QWERTY_AUI_ARROW_LEFT_DK,
		LAND_QWERTY_AUI_ARROW_RT, LAND_QWERTY_AUI_ARROW_RT_DK, LAND_QWERTY_AUI_HILITE, 
		LAND_QWERTY_AUI_LOGO, LAND_QWERTY_MARGIN, PORT_QWERTY_AUI,
		PORT_QWERTY_AUI_ARROW_LEFT, PORT_QWERTY_AUI_ARROW_LEFT_DK, PORT_QWERTY_AUI_ARROW_RT,
		PORT_QWERTY_AUI_ARROW_RT_DK, PORT_QWERTY_AUI_HILITE, PORT_QWERTY_AUI_LOGO,
		PORT_QWERTY_MARGIN, QWERTY_1_HILITE, QWERTY_2_HILITE,
		QWERTY_3_HILITE, QWERTY_4_HILITE, QWERTY_5_HILITE, 
		QWERTY_KEYS, 
		
		ENG_QWERTY_SYMBOLS, FRE_QWERTY_SYMBOLS, GER_QWERTY_SYMBOLS,
		
		NUMBER_1_HILITE, NUMBER_2_HILITE, NUMBER_KEYBOARD,
		NUMBER_MARGIN, NUMBER_SYMBOLS
	}; 
	
	public static void writeAll(Context context){

		SWI_FileIO.write(context, NUMBER_1_HILITE, R.raw.number_1_hilite);
		SWI_FileIO.write(context, NUMBER_2_HILITE, R.raw.number_2_hilite);
		SWI_FileIO.write(context, NUMBER_KEYBOARD, R.raw.number_keyboard);
		SWI_FileIO.write(context, NUMBER_MARGIN, R.raw.number_margin);
		SWI_FileIO.write(context, NUMBER_SYMBOLS, R.raw.number_symbols);
		
		SWI_FileIO.write(context, LAND_QWERTY_AUI, R.raw.land_qwerty_aui);
		SWI_FileIO.write(context, LAND_QWERTY_AUI_ARROW_LEFT, R.raw.land_qwerty_aui_arrow_left);
		SWI_FileIO.write(context, LAND_QWERTY_AUI_ARROW_LEFT_DK, R.raw.land_qwerty_aui_arrow_left_dk);
		SWI_FileIO.write(context, LAND_QWERTY_AUI_ARROW_RT, R.raw.land_qwerty_aui_arrow_rt);
		SWI_FileIO.write(context, LAND_QWERTY_AUI_ARROW_RT_DK, R.raw.land_qwerty_aui_arrow_rt_dk);
		SWI_FileIO.write(context, LAND_QWERTY_AUI_HILITE, R.raw.land_qwerty_aui_hilite);
		SWI_FileIO.write(context, LAND_QWERTY_AUI_LOGO, R.raw.land_qwerty_aui_logo);
		SWI_FileIO.write(context, LAND_QWERTY_MARGIN, R.raw.land_qwerty_margin);
		SWI_FileIO.write(context, PORT_QWERTY_AUI, R.raw.port_qwerty_aui);
		SWI_FileIO.write(context, PORT_QWERTY_AUI_ARROW_LEFT, R.raw.port_qwerty_aui_arrow_left);
		SWI_FileIO.write(context, PORT_QWERTY_AUI_ARROW_LEFT_DK, R.raw.port_qwerty_aui_arrow_left_dk);
		SWI_FileIO.write(context, PORT_QWERTY_AUI_ARROW_RT, R.raw.port_qwerty_aui_arrow_rt);
		SWI_FileIO.write(context, PORT_QWERTY_AUI_ARROW_RT_DK, R.raw.port_qwerty_aui_arrow_rt_dk);
		SWI_FileIO.write(context, PORT_QWERTY_AUI_HILITE, R.raw.port_qwerty_aui_hilite);
		SWI_FileIO.write(context, PORT_QWERTY_AUI_LOGO, R.raw.port_qwerty_aui_logo);
		SWI_FileIO.write(context, PORT_QWERTY_MARGIN, R.raw.port_qwerty_margin);
		SWI_FileIO.write(context, QWERTY_1_HILITE, R.raw.qwerty_1_hilite);
		SWI_FileIO.write(context, QWERTY_2_HILITE, R.raw.qwerty_2_hilite);
		SWI_FileIO.write(context, QWERTY_3_HILITE, R.raw.qwerty_3_hilite);
		SWI_FileIO.write(context, QWERTY_4_HILITE, R.raw.qwerty_4_hilite);
		SWI_FileIO.write(context, QWERTY_5_HILITE, R.raw.qwerty_5_hilite);
		SWI_FileIO.write(context, QWERTY_KEYS, R.raw.qwerty_keys);
		
		SWI_FileIO.write(context, FRE_QWERTY_SYMBOLS, R.raw.fre_qwerty_symbols);
		SWI_FileIO.write(context, ENG_QWERTY_SYMBOLS, R.raw.eng_qwerty_symbols);
		SWI_FileIO.write(context, GER_QWERTY_SYMBOLS, R.raw.ger_qwerty_symbols);
	}
	
	public static boolean existAll(String directory){
		File file;
		int size = ALL.length;
		for(int i = 0; i < size; i++){
			file = new File(directory, ALL[i]);
			if(!file.exists()){
				return false;
			}
		}
		return true;
	}
}
