/* Services.java
 * Created on May 25, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Canvas;

/**
 * 
 * @author pkriste
 */
public class SWI_Services {

	// static Font scoreFont = new Font("SanSerif", Font.BOLD, 24);
	static final int scoreColor = Color.YELLOW;

	// Hidden constructor
	private SWI_Services() {
	}

	/**
	 * Displays an error message dialog box.
	 * 
	 * @param title
	 *            the title of the dialog box
	 * @param message
	 *            the message in the dialog box
	 * @param onTop
	 *            if <code>true</code> the dialog box will be top most in the
	 *            z-order on the desktop
	 */
	static void showError(String title, String message, boolean onTop) {
		// JOptionPane pane = new JOptionPane(message,
		// JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
		// JDialog dialog = pane.createDialog(null, title);
		// dialog.setModal(true);
		// dialog.setVisible(true);
		// dialog.setAlwaysOnTop(true);
		// dialog.toFront();
		return;
	}

	/**
	 * Createas a frequency sorted list of strings from a lexicon.
	 * 
	 * @param lexicon
	 *            the lexicon source
	 */
	static List getLexiconList(SWI_Lexicon lexicon) {
		List lexiconList = new ArrayList();

		lexicon.acquireLock();

		Iterator i = lexicon.wordsIteratorAsynch();
		while (i.hasNext()) {
			String w = (String) i.next();
			int f = lexicon.getFrequencyAsynch(w);
			lexiconList.add(new Object[] { w, new Integer(f) });
		}

		Collections.sort(lexiconList, new Comparator() {
			public int compare(Object o1, Object o2) {
				Object[] oa1 = (Object[]) o1;
				Object[] oa2 = (Object[]) o2;
				return ((Integer) oa1[1]).compareTo((Integer) oa2[1]) * -1;
			}
		});

		for (ListIterator j = lexiconList.listIterator(); j.hasNext();) {
			Object[] oa = (Object[]) j.next();
			j.set(oa[0]);
		}

		lexicon.releaseLock();
		return lexiconList;
	}

	static List getLexiconList(InputStream lexInputStream)
			throws SWI_LexiconException {
		List lexiconList = new ArrayList();

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = null;
		try {
			parser = factory.newSAXParser();
		} catch (SAXException saxe) {
			throw new SWI_LexiconException("SAXException", saxe);
		} catch (ParserConfigurationException pce) {
			throw new SWI_LexiconException("Cannot create parser", pce);
		}

		try {
			parser.parse(lexInputStream, new Handler(lexiconList));
		} catch (IllegalArgumentException iae) {
			throw new SWI_LexiconException("xmlStream is null", iae);
		} catch (SAXException saxe) {
			throw new SWI_LexiconException("SAXException", saxe);
		} catch (IOException ioe) {
			throw new SWI_LexiconException("I/O Error", ioe);
		}

		return lexiconList;
	}

	private static class Handler extends DefaultHandler {
		private List list;

		// private static final String ATTR_QNAME_FREQUENCY = "f";

		/**
		 * Creates a SAX XML stream handler that parses lexicon data.
		 */
		public Handler(List list) {
			this.list = list;
		}

		/*
		 * Tracks stacklevel: 1. lexicon element 2. word element or combination
		 * element 3. group element under a combination element 4. entity
		 * element under a group element
		 */
		private int stackLevel = 0;
		// Buffers current word at element
		private StringBuffer buffer = new StringBuffer();

		// private int freq;

		// Increases stack level
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			stackLevel++;
			// if (stackLevel == 2) {
			// if (attributes.getQName(0).equalsIgnoreCase(
			// ATTR_QNAME_FREQUENCY)) {
			// freq = Integer.parseInt(attributes.getValue(0));
			// } else {
			// freq = 0;
			// }
			// }
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (stackLevel == 2) {
				buffer.append(ch, start, length);
			}
		}

		// Decreases stack level
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (stackLevel == 2) {
				// Exited a word tag
				addWord(buffer.toString());
				buffer = new StringBuffer();
			}
			stackLevel--;
		}

		private void addWord(String word) {
			list.add(word);
		}
	}

	static Rect paintText(Canvas canvas, float x, float y, int c, String str,
			boolean centerH) {
		// TextLayout text = new TextLayout(str, scoreFont,
		// g2d.getFontRenderContext());
		// Rectangle2D rect = text.getBounds();
		// y += rect.getHeight() * 0.5d;
		// if (centerH) {
		// x -= rect.getWidth() * 0.5d;
		// }
		// g2d.setColor(c);
		// text.draw(g2d, (float)x, (float)y);
		// rect.setRect(x, y - rect.getHeight(), rect.getWidth(),
		// rect.getHeight());

		Rect rect = new Rect();

		Paint paint = new Paint();
		paint.setColor(c);

		canvas.drawText(str, x, y, paint);

		return rect;
	}

	/**
	 * Returns the point in the plane that if thought of as the upper left
	 * corner in a bounding box defined by <code>width</code> and
	 * <code>height</code> centers said bounding box in the rectangular
	 * coordinate system specified by <code>largeBox</code>.
	 * 
	 * @param largeBox
	 *            the rectangle that typically defines the scene
	 * @param width
	 *            the width of an imagined rectangle to be centered in the scene
	 * @param height
	 *            the height of an imagined rectangle to be centered in the
	 *            scene
	 */
	static Point getTopLeftCenteringCoordinate(Rect largeBox, int width,
			int height) {
		float x = (largeBox.left * 0.5f) + (largeBox.width() * 0.5f)
				- (width * 0.5f);
		float y = (largeBox.top * 0.5f) + (largeBox.height() * 0.5f)
				- (height * 0.5f);
		return new Point((int) x, (int) y);
	}

	/**
	 * Returns the visual (actual) descent of a text layout created with a
	 * specific string.
	 * 
	 * <p>
	 * The descent will be zero if the string that created the text layout has
	 * no characters hanging below the base line, otherwise the descent will be
	 * a positive number taken from the text layout line metrics.
	 * </p>
	 * 
	 * <p>
	 * This method only supports the US English a-Z, A-Z character range.
	 * </p>
	 * 
	 * @param str
	 *            the string that created the text layout
	 * @param tl
	 *            the text layout
	 */
	// static double getVisualDescent(String str, TextLayout tl) {
	// // char array must be sorted or binary search will fail
	// char[] descentChars = new char[]{'g', 'j', 'p', 'q', 'y'};
	// boolean hasDescent = false;
	// for (int i = 0, len = str.length(); i < len; i++) {
	// char c = str.charAt(i);
	// if (linearSearch(descentChars, c)) {
	// hasDescent = true;
	// }
	// }
	// return hasDescent ? (double)tl.getDescent() : 0.0d;
	// }
	private static boolean linearSearch(char[] array, char c) {
		for (int i = 0, len = array.length; i < len; i++) {
			if (array[i] == c) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Paints game play statistics.
	 * 
	 * @param g2d
	 *            the graphics context
	 */

	/**
	 * Returns the user's accuracy in hitting the balloons as a percentage of
	 * the number of balloons hit in relation to total number of words received.
	 * 
	 * @return the user's accuracy as a percentage
	 */
	static int getUsersAccuracy(int numWordsEntered, int numBalloonsHit) {
		if (numWordsEntered == 0) {
			return 0;
		} else {
			return (int) (((double) numBalloonsHit / (double) numWordsEntered) * 100.0d);
		}
	}

}
