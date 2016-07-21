/* LexiconException.java
 * Created on Mar 22, 2004
 *
 * (C) Per-Ola Kristensson. 2004. All rights reserved.
 *
 * @author Per-Ola Kristensson */
package com.shapewriter.android.softkeyboard.game;

public class SWI_LexiconException extends Exception {

	static String copyright() {
		return "(C) Copyright Per-Ola Kristensson. 2004. All rights reserved.";
	}

	public SWI_LexiconException(String message) {
		super(message);
	}

	public SWI_LexiconException(String message, Throwable cause) {
		super(message, cause);
	}
}
