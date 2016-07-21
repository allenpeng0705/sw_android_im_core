package com.shapewriter.android.softkeyboard;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.PointF;
import android.os.Handler;

import com.shapewriter.android.softkeyboard.recognizer.*;

/**
 * This class implements the Command Strokes mechanism.
 * 
 * To use this class:
 * 1. Call its public constructor with the
 * required initialization parameters.
 * 
 * 2. Attach an implemention of this class's <code>Callback</code>
 * interface by calling the <code>setCallback</code> method.
 * 
 * 3. Activate the Command Strokes by calling <code>setActivated(true)</code>. 
 * 
 * @author Per Ola Kristensson
 *
 */
public class SWI_CommandStrokes {
	
	public static final long POLL_INTERVAL = 100; // ms
	
	private RCO rco;
	private Map<String,Command> cmdMap;
	private SWI_KeyboardViewTrace keyboard;
//	private Handler handler = new Handler();
	private Handler handler;
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	private boolean activated = false;
	private Callback callback;
	private float key_width;

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			executeRecognition();
			if (activated && keyboard != null) {
				handler.postDelayed(runnable, POLL_INTERVAL);
			}
		}
	};
	private void executeRecognition() {
		
		if (!activated || keyboard == null) {
			return;
		}
		List<PointF> samplePoints = keyboard.getSamplePoints();
		if (samplePoints == null) {
			return;
		}
		int n = samplePoints.size();
		if (n < 2) {
			return;
		}
		PointF pt1 = samplePoints.get(n - 1);
		PointF pt0 = samplePoints.get(n - 2);		
		double d = distance(pt0.x, pt0.y, pt1.x, pt1.y);
		double t = Math.max(key_width * 0.05d, 1);
		long dt = (System.nanoTime() - keyboard.getLastMovementTimestamp()) / 1000000; // 1,000,000
		
		if (d < t || dt > 50) {
			recognize(samplePoints, false);
		}
	}
	
	/**
	 * Creates a Command Strokes manager for the supplied keyboard.
	 *
	 * @param commands a list of commands (e.g. "copy")
	 * @param keyboard the keyboard that will use Command Strokes
	 * @param key_mapping_set the key mapping set used
	 * @param key_width the letter key width
	 */	
	public SWI_CommandStrokes(List<Command> commands, SWI_KeyboardViewTrace keyboard, RCO aRCO) {
		if (keyboard == null) {
			throw new NullPointerException("keyboard cannot be null");
		}
		
		this.keyboard = keyboard;
		cmdMap = new HashMap<String,Command>();
		rco = aRCO;
//		handler.postDelayed(runnable, POLL_INTERVAL);
		
		addCommands(commands);
	}
	
	public void SetKeyboard(SWI_KeyboardViewTrace keyboard){
		this.keyboard = keyboard;
	}
	
	/**
	 * Activates or deactivates Command Strokes.
	 * 
	 * @param activated <true> if Command Strokes should be
	 * activated, <code>false</code> otherwise
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
		if (activated) {
			handler.postDelayed(runnable, POLL_INTERVAL);
		}
	}

	/**
	 * Returns whether Command Strokes are activated or not.
	 * 
	 * @return <code>true</code> if Command Strokes are activated,
	 * <code>false</code> otherwise
	 */
	public boolean isActivated() {
		return activated;
	}
	
	/**
	 * Attaches a <code>Callback</code> implemention to be
	 * notified when a command was recognized.
	 * 
	 * @param callback the callback
	 */
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
	
	public void finalCommandStrokesRecognition() {
		recognize(keyboard.getSamplePoints(), true);
	}
	
	/**
	 * Adds a command to the command strokes recognizer. 
	 * 
	 * @param cmd the command to add (e.g. "copy")
	 */
	public void addCommand(Command command) {
		if (command == null) {
			throw new NullPointerException("command cannot be null");
		}
		if (command.command.length() < 2) {
			throw new IllegalArgumentException("String length of command must be > 2");
		}
		String[] tokens = getRecognizerTokens(command.command);
		for (int i = 0, n = tokens.length; i < n; i++) {
			//if (rco.IsWordExistInRCO(tokens[i]) == false) rco.AddWordToRCO(tokens[i]);
			cmdMap.put(tokens[i], command);
		}
	}
	
	private static double distance(float x1, float y1, float x2, float y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/*
	 * Performs recognition and calls the attached callback (if not null)
	 * if there is matching command.
	 */
	private void recognize(List<PointF> samplePoints, boolean complete) {
		int n = samplePoints.size();
		InputSignal input_signal = new InputSignal();
		input_signal.iCount = n;
		input_signal.iSamplePoints = new SamplePoint[n];
		for (int i = 0; i < n; i++) {
			PointF pt = samplePoints.get(i);
			input_signal.iSamplePoints[i] = new SamplePoint();
			input_signal.iSamplePoints[i].x = pt.x;
			input_signal.iSamplePoints[i].y = pt.y;
			input_signal.iSamplePoints[i].t = 0.0f;
		}
		Command command = getRecognizedCommand(input_signal);
		if (callback != null && activated) {
			if (command == null) {
				callback.commandCancel();
			}
			else {
				callback.commandRecognized(command, complete);
			}
		}
	}

	/*
	 * Performs recognition and returns the recognized command (e.g. "copy"),
	 * or null if no command is installed, or if no
	 * command matches the input signal.
	 */
	private Command getRecognizedCommand(InputSignal input_signal) {
		String str = null;    			
	    ResultSet rs = rco.Recognize(input_signal, null);
		if (rs != null) { 
    		if (rs.result_count > 0) {
    			str = getStringFromZeroTerminatedCharArray(rs.results[0].str.toCharArray());    			
    		}
    		
    		for (int i = 0; i<rs.result_count; i++) {
    			rs.results[i] = null;
    		}
    		rs = null;
		}
		
		
		
		Command command = cmdMap.get(str);
		return command;
	}
	
	/*
	 * Creates an input stream to be fed to the recognizer from the
	 * given list of commands.
	 */
	private void addCommands(List<Command> commands) {
		for (Iterator<Command> i = commands.iterator(); i.hasNext(); ) {
			Command command = i.next();
			addCommand(command);
		}
		commands.clear();
		commands = null;
	}


	/*
	 * Converts a raw recognizer string into a standard Java string.
	 */
	private static String getStringFromZeroTerminatedCharArray(char[] ca) {
		for (int i = 0, n = ca.length; i < n; i++) {
			char c = ca[i];
			if (c == '\0') {
				return String.valueOf(ca, 0, i);
			}
		}
		return String.valueOf(ca);
	}
	
	/*
	 * Tokenizes a command into recognizer tokens.
	 */
	private String[] getRecognizerTokens(String cmd) {
		int len = cmd.length();
		String[] tokens = new String[len];
		for (int i = 0; i < len; i++) {
			tokens[i] = cmd.substring(0, i + 1);
		}
		return tokens;
	}
	
	public static class Command {
		
		public String command;
		public String caption;
		public Runnable action;
		
		public Command(String command, String caption, Runnable action) {
			this.command = command;
			this.caption = caption;
			this.action = action;
		}
		
		@Override
		public int hashCode() {
			return command.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			return command.equals(o);
		}
		
	}

	/**
	 * This <code>Callback</code> interface will be called when
	 * a command is detected.
	 * 
	 * Use <code>setCallback</code> to configure a callback.
	 * 
	 * @author Per Ola Kristensson
	 *
	 */
	public static interface Callback {
		
		/**
		 * Called when a command is recognized.
		 * 
		 * <p>
		 * This method will always be called from the View's
		 * UI thread.
		 * </p>
		 * 
		 * @param cmd the command recognized (e.g. "copy")
		 * @param complete this parameter is <code>true</code> if the
		 * Command Stroke is complete, e.g. not a preview
		 */
		void commandRecognized(Command command, boolean complete);
		
		/**
		 * Called when the current command was cancelled.
		 */
		void commandCancel();
		
	}
	
	public void destroy() {
		activated = false;
		if (handler != null) handler = null;
		if (keyboard != null) keyboard = null;
		if (callback != null) callback = null;
		key_width = 0.0f;
		if (cmdMap != null) {
			cmdMap.clear();
			cmdMap = null;
		}
		
	}
}
