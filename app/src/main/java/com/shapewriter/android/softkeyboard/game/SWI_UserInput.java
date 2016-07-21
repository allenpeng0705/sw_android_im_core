/* UserInput.java
 * Created on Apr 18, 2005
 *
 * Copyright (c) 2005 Per-Ola Kristensson
 * Per-Ola Kristensson <perkr@ida.liu.se>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

/**
 * 
 * @author pkriste
 */
public class SWI_UserInput {
	
	private Object source;
	private String str;
	private Object data;
	private long traceTimeMillis;
	
	public SWI_UserInput(Object source, String str, Object data, long traceTime) {
		this.source = source;
		this.str = str;
		this.data = data;
		this.traceTimeMillis = traceTime;
	}
	
	public Object getSource() {
		return source;
	}
	
	public String getString() {
		return str;
	}
	
	public Object getData() {
		return data;
	}
	
	public long getTraceTime(){
		return traceTimeMillis;
	}

}
