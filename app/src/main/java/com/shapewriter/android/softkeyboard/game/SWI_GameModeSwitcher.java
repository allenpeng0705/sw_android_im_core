/* GameModeSwitcher.java
 * Created on Jun 7, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.io.InputStream;

/**
 * 
 * @author pkriste
 */
interface SWI_GameModeSwitcher {	
	void switchGameMode(SWI_GameMode newGameMode, boolean titleSceneGameMode);	
	void sendDeactivateSignal();	
	InputStream getGameLexiconInputStream();
}
