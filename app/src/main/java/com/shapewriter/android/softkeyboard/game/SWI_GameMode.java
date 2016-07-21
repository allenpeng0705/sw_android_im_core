/* GameMode.java
 * Created on Jun 7, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;


/**
 * 
 * @author pkriste
 */
interface SWI_GameMode {
	
	void handleUserInput(SWI_UserInput userInput);
	
	void setGameModeSwitcher(SWI_GameModeSwitcher gameModeSwitcher);

}
