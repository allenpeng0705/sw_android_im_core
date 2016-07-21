/* EriItem.java
 * Created on May 25, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.io.Serializable;

/**
 * This class encapsulates expanding rehearsal interval information on a word.
 * 
 * @author pkriste
 */
class SWI_EriItem implements Serializable {

	private int show_time = -1;
	private String word;
	private long timeStampMs;
	private int rehearsalIntervalS;
	private long traceTimeMillis;

	private final static long speed_rule = 3000L;

	int getRehearsalIntervalS() {
		return rehearsalIntervalS;
	}

	void setRehearsalIntervalS(int rehearsalIntervalS) {
		this.rehearsalIntervalS = rehearsalIntervalS;
	}

	long getTimeStampMs() {
		return timeStampMs;
	}

	void setTimeStampMs(long timeStampMs) {
		this.timeStampMs = timeStampMs;
	}

	String getWord() {
		return word;
	}

	void setWord(String word) {
		this.word = word;
	}

	void incShowTime() {
		show_time = (show_time + 1) % 4;
	}

	void setTraceTimeMillis(final long traceTime) {
		this.traceTimeMillis = traceTime;
	}

	long getTraceTimeMillis() {
		return traceTimeMillis;
	}

	boolean isValidSpeed() {
		return (traceTimeMillis / word.length()) < speed_rule;		
	}

	int getShowTime() {
		return show_time;
	}
}
