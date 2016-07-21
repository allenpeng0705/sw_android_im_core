/* EriScheduler.java
 * Created on May 25, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

// import com.shapewriter.android.language.Lexicon;
// import com.shapewriter.android.language.LexiconException;

/**
 * 
 * @author pkriste
 */
public class SWI_EriScheduler {

	private List lexiconList = new ArrayList();
	private ArrayList<SWI_EriItem> eriList;
	private int eriMultiplier = 2;
	private int startEriIntervalS = 30;

	/**
	 * Creates an ERI scheduler.
	 * 
	 * @param lexInputStream
	 *            an input stream to a lexicon in the ShapeWriter XML format
	 * @param eriFilename
	 *            a filename to a serialized <code>ArrayList</code> of
	 *            <code>EriItem</code>s, this parameter may be <code>null</code>
	 * @throws NullPointerException
	 *             if <code>lexInputStream</code> is <code>null</code>
	 */
	public SWI_EriScheduler(InputStream lexInputStream, String eriFilename) {
		// if (lexInputStream == null) {
		// throw new NullPointerException("lexInputStream cannot be null");
		// }
		createLexiconList(lexInputStream);
		initializeERI(eriFilename);
	}

	public void saveState(String eriFilename) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(eriFilename));
			oos.writeObject(eriList);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	private void createLexiconList(InputStream lexInputStream) {
		if (lexInputStream == null) {
			lexiconList.add("Hello");
			lexiconList.add("World");
			lexiconList.add("Android");
			lexiconList.add("Game");
			lexiconList.add("is");
			lexiconList.add("Good");
		} else {
			try {
				// Log.i("EriScheduler", " before Lexicon constructor " +
				// "lexInputStream = " + lexInputStream.toString());
				// SWI_Lexicon lexicon = new SWI_Lexicon(lexInputStream);
				// Log.i("EriScheduler", " after Lexicon constructor ");
				lexiconList = SWI_Services.getLexiconList(lexInputStream);
			} catch (SWI_LexiconException e) {
				e.printStackTrace();
			}
		}
	}

	private void initializeERI(String eriFilename) {
		if (lexiconList.size() == 0) {
			throw new IllegalStateException(
					"Lexicon list must be initialized before this method is called");
		}
		File f = new File(eriFilename);
		boolean succeeded = false;
		if (f.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(f));
				eriList = (ArrayList<SWI_EriItem>) ois.readObject();
				succeeded = true;
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
		if (!succeeded) {
			eriList = new ArrayList<SWI_EriItem>(0);
		}
	}

	/**
	 * Returns the next ERI item.
	 */
	public SWI_EriItem getNextEriItem() {
		if (eriList.size() == 0) {
			// If the ERI list is empty, trivially fetch first
			// word in lexicon and initialize the ERI list
			SWI_EriItem ei = makeEriItem();
			eriList.add(ei);
			return ei;
		} else {
			// Fetch the first word in the ERI list whose time elapased
			// has exceeded the time specified
			SWI_EriItem ei = getEriItemIfApplicable();
			if (ei == null) {
				ei = makeEriItem();
				eriList.add(ei);
				return ei;
			} else {
				ei.setTimeStampMs(System.currentTimeMillis());
				return ei;
			}
		}
	}

	public void updateEri(SWI_EriItem ei) {
		// Update ERI interval
		ei.setRehearsalIntervalS(ei.getRehearsalIntervalS() * eriMultiplier);
		// Update time stamp
		ei.setTimeStampMs(System.currentTimeMillis());
	}

	private SWI_EriItem getEriItemIfApplicable() {
		long now = System.currentTimeMillis();
		for (Iterator<SWI_EriItem> i = eriList.iterator(); i.hasNext();) {
			SWI_EriItem ei = i.next();
			long dt = now - ei.getTimeStampMs();
			dt /= 1000;
			if (dt > ei.getRehearsalIntervalS()) {
				return ei;
			}
		}
		return null;
	}

	private SWI_EriItem makeEriItem() {
		int size = eriList.size();
		if (size >= lexiconList.size()) {
			return null;
		}
		String s = (String) lexiconList.get(size);
		SWI_EriItem ei = new SWI_EriItem();
		ei.setWord(s);
		ei.setRehearsalIntervalS(startEriIntervalS);
		ei.setTimeStampMs(System.currentTimeMillis());
		return ei;
	}

	public void clearEriList() {
		eriList.clear();
	}

}
