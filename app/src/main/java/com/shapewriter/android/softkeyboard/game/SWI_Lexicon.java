/* Lexicon.java
 * Created on Mar 22, 2004
 *
 * (C) Per-Ola Kristensson. 2004, 2005. All rights reserved.
 * (C) Copyright ShapeWriter Inc. 2004. All rights reserved.
 *
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class represents a lexicon and provides associated access methods.
 */
public class SWI_Lexicon implements Serializable {

	/** The OP code for sorting the lexicon by frequency. */
	public static int OP_SORT_MASK = 1;

	private static final String ENCODING = "UTF-8";
	private static final String HEADER = "<?xml version='1.0' encoding='UTF-8'?>";
	private static final String ROOT = "lexicon";
	private static final String WORD = "w";
	private static final String FREQUENCY = "f";
	private static final String NEW_LINE = System.getProperty("line.separator");

	private Map words = new HashMap();

	// Lock status
	private volatile boolean locked = false;

	/**
	 * Creates an empty lexicon with no tokens.
	 */
	public SWI_Lexicon() {
	}

	/**
	 * Releases all resources held by this lexicon.
	 */
	public void release() {
		acquireLock();
		words.clear();
		releaseLock();
	}

	/**
	 * Constructs a lexicon by parsing the supplied XML <code>InputStream</code>.
	 * 
	 * @param xmlStream
	 *            an input stream of XML lexicon data
	 * @throws SWI_LexiconException
	 *             if an error occured when parsing the input stream, either due
	 *             to I/O errors or semantic errors in the XML data
	 */
	public SWI_Lexicon(InputStream xmlStream) throws SWI_LexiconException {
		makeLexicon(xmlStream);
	}

	/**
	 * Acquires the lock on the lexicon.
	 */
	public synchronized void acquireLock() {
		while (locked) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		locked = true;
	}

	/**
	 * Releases the lock from the lexicon.
	 */
	public synchronized void releaseLock() {
		locked = false;
		notifyAll();
	}

	/*
	 * Parses the input stream assumed to contain XML data for a lexicon, this
	 * method needs external synchronization
	 */
	private void makeLexicon(InputStream xmlStream) throws SWI_LexiconException {
		acquireLock();
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
			parser.parse(xmlStream, new Handler());
		} catch (IllegalArgumentException iae) {
			throw new SWI_LexiconException("xmlStream is null", iae);
		} catch (SAXException saxe) {
			throw new SWI_LexiconException("SAXException at entry no. "
					+ words.size(), saxe);
		} catch (IOException ioe) {
			throw new SWI_LexiconException("I/O Error", ioe);
		}
		releaseLock();
	}

	/**
	 * Writes this lexicon as an XML stream.
	 * 
	 * @param stream
	 *            the output stream to write to
	 * @throws IOException
	 *             if an I/O error occured
	 */
	public void writeLexicon(OutputStream stream) throws IOException {
		acquireLock();
		OutputStreamWriter osw = new OutputStreamWriter(stream, Charset
				.forName(ENCODING));
		emitStr(osw, HEADER);
		emitStr(osw, NEW_LINE);
		emitStr(osw, "<" + ROOT + ">");
		emitStr(osw, NEW_LINE);
		for (Iterator i = words.keySet().iterator(); i.hasNext();) {
			String word = (String) i.next();
			emitStr(osw, "<" + WORD + " " + FREQUENCY + "=\"" + words.get(word)
					+ "\">");
			emitStr(osw, normalize(word));
			emitStr(osw, "</" + WORD + ">");
			emitStr(osw, NEW_LINE);
		}
		emitStr(osw, "</" + ROOT + ">");
		emitStr(osw, NEW_LINE);
		osw.flush();
		osw.close();
		releaseLock();
	}

	/**
	 * Writes this lexicon as an XML stream applying the operation specified in
	 * the op mask.
	 * 
	 * @param stream
	 *            the output stream to write to
	 * @param opMask
	 *            the op mask
	 * @throws IOException
	 *             if an I/O error occured
	 */
	public void writeLexiconOpMask(OutputStream stream, int opMask)
			throws IOException {
		if (opMask == OP_SORT_MASK) {
			acquireLock();
			List list = new ArrayList(words.size());
			for (Iterator i = words.keySet().iterator(); i.hasNext();) {
				String word = (String) i.next();
				Integer freq = (Integer) words.get(word);
				list.add(new Object[] { word, freq });
			}
			Collections.sort(list, new Comparator() {
				public int compare(Object o1, Object o2) {
					Object[] oa1 = (Object[]) o1;
					Object[] oa2 = (Object[]) o2;
					return -1 * ((Integer) oa1[1]).compareTo((Integer) oa2[1]);
				}
			});
			OutputStreamWriter osw = new OutputStreamWriter(stream, Charset
					.forName(ENCODING));
			emitStr(osw, HEADER);
			emitStr(osw, NEW_LINE);
			emitStr(osw, "<" + ROOT + ">");
			emitStr(osw, NEW_LINE);
			for (Iterator i = list.iterator(); i.hasNext();) {
				Object[] oa = (Object[]) i.next();
				String word = (String) oa[0];
				emitStr(osw, "<" + WORD + " " + FREQUENCY + "=\"" + oa[1]
						+ "\">");
				emitStr(osw, normalize(word));
				emitStr(osw, "</" + WORD + ">");
				emitStr(osw, NEW_LINE);
			}
			emitStr(osw, "</" + ROOT + ">");
			emitStr(osw, NEW_LINE);
			osw.flush();
			osw.close();
			releaseLock();
		}
	}

	/**
	 * Exports the lexicon into two-column tabular text, where the first column
	 * contains the words and the second column their corresponding frequencies.
	 * 
	 * <p>
	 * Note that only export to tabular text is supported, the lexicon will not
	 * attempt to parse tabular text into a lexicon representation.
	 * </p>
	 * 
	 * @param stream
	 *            an output stream to write to
	 * @param columnDelimiter
	 *            the character or string that should delimit the columns
	 * @param freqThreshold
	 *            any word having a frequency count below this frequency cutoff
	 *            threshold is removed
	 */
	public void writeLexiconTwoColumnText(OutputStream stream,
			String columnDelimiter, int freqThreshold) throws IOException {
		acquireLock();
		pruneLexiconAsynch(freqThreshold);
		OutputStreamWriter osw = new OutputStreamWriter(stream, Charset
				.forName(ENCODING));
		emitStr(osw, WORD);
		emitStr(osw, columnDelimiter);
		emitStr(osw, FREQUENCY);
		emitStr(osw, NEW_LINE);
		for (Iterator i = words.keySet().iterator(); i.hasNext();) {
			String word = (String) i.next();
			int freq = ((Integer) words.get(word)).intValue();
			emitStr(osw, word);
			emitStr(osw, columnDelimiter);
			emitStr(osw, Integer.toString(freq));
			emitStr(osw, NEW_LINE);
		}
		osw.flush();
		osw.close();
		releaseLock();
	}

	public void writeLexiconToBinaryStream(String filename) throws IOException {
		acquireLock();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				filename));
		oos.writeInt(words.size());
		for (Iterator i = words.keySet().iterator(); i.hasNext();) {
			String word = (String) i.next();
			int freq = ((Integer) words.get(word)).intValue();
			oos.writeObject(code(word));
			oos.writeInt(freq);
		}
		oos.flush();
		oos.close();
		releaseLock();
	}

	public static SWI_Lexicon createLexiconFromBinaryStream(InputStream inputStream)
			throws IOException {
		SWI_Lexicon lexicon = new SWI_Lexicon();
		ObjectInputStream ois = new ObjectInputStream(inputStream);
		int size = ois.readInt();
		for (int i = 0; i < size; i++) {
			String word = null;
			try {
				word = (String) ois.readObject();
				int freq = ois.readInt();
				lexicon.add(code(word), freq);
			} catch (ClassNotFoundException e) {
				throw new IOException(
						"Cannot find class, binary stream is invalid");
			}
		}
		return lexicon;
	}

	private static String code(String str) {
		return str;
		// char[] ca = new char[str.length()];
		// for (int j = 0, len = str.length(); j < len; j++) {
		// char c = str.charAt(j);
		// ca[j] = (char)~c;
		// }
		// return new String(ca);
	}

	/*
	 * Performs normalization of a string to make it processable in XML,
	 * currently only translates illegal characters into the proper encoding.
	 */
	private String normalize(String str) {
		StringBuffer sb = new StringBuffer(str.length());
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			switch (c) {
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	// Emits a string to an output stream writer
	private static void emitStr(OutputStreamWriter osw, String str)
			throws IOException {
		osw.write(str, 0, str.length());
	}

	/**
	 * Adds a word asynchronously (un-synchronized) to the lexicon.
	 * 
	 * <p>
	 * This method is intended for package-private functions and services that
	 * need to manipulate the lexicon while holding the global lock.
	 * </p>
	 * 
	 * @param word
	 *            the word
	 */
	void addWordAsynch(String word) {
		if (word.length() == 1) {
			return;
		}
		Object o = words.get(word);
		if (o == null) {
			words.put(word, new Integer(1));
		} else {
			int freq = ((Integer) o).intValue();
			words.put(word, new Integer(++freq));
		}
	}

	/**
	 * Prunes the lexicon asynchronously (un-synchronized).
	 * 
	 * <p>
	 * This method is intended for package-private functions and services that
	 * need to manipulate the lexicon while holding the global lock.
	 * </p>
	 * 
	 * @param freqThreshold
	 *            any word having a frequency count below this frequency cutoff
	 *            threshold is removed
	 */
	void pruneLexiconAsynch(int freqThreshold) {
		for (Iterator i = words.keySet().iterator(); i.hasNext();) {
			String word = (String) i.next();
			int f = ((Integer) words.get(word)).intValue();
			if (f < freqThreshold) {
				i.remove();
			}
		}
	}

	/**
	 * Returns an asynchronous iterator over the words in the lexicon.
	 * 
	 * <p>
	 * The iterator is not thread-safe, neither is call to this method. A call
	 * should be preceded by call to <code>acquireLock</code> and superceded
	 * by a call to <code>releaseLock</code>. When the lock is held (during
	 * iteration) no thread-safe methods (all methods not ending with an
	 * "Asynch" suffix) may be called, as this will result in a dead-lcok.
	 * </p>
	 * 
	 * <p>
	 * The iterator will return <code>String</code>s which represent the
	 * words in the lexicon. To access the frequency of the word call
	 * <code>getFrequencyAsynch</code>. Any structural modifications via the
	 * iterator (remove, etc.) results in undefined behavior.
	 * </p>
	 * 
	 * @return an iterator over the words in the lexicon
	 */
	public Iterator wordsIteratorAsynch() {
		return words.keySet().iterator();
	}

	/**
	 * Adds a new word to the lexicon.
	 * 
	 * @param word
	 *            the word to add
	 * @param freq
	 *            the frequency count of the word\
	 * @throws IllegalStateException
	 *             if the word already exists in the lexicon
	 */
	public void add(String word, int freq) {
		if (word.length() == 1) {
			return;
		}
		boolean failed = false;
		acquireLock();
		Object o = words.get(word);
		if (o == null) {
			words.put(word, new Integer(freq));
		} else {
			failed = true;
		}
		releaseLock();
		if (failed) {
			throw new IllegalStateException("Word " + word
					+ " already exist in lexicon");
		}
	}

	/**
	 * Adds a new word to the lexicon asynchronously.
	 * 
	 * <p>
	 * This method is not synchronized.
	 * </p>
	 * 
	 * @param word
	 *            the word to add
	 * @param freq
	 *            the frequency count of the word
	 * @throws IllegalStateException
	 *             if the word alrady exists in the lexicon
	 */
	public void addAsynch(String word, int freq) {
		if (word.length() == 1) {
			return;
		}
		Object o = words.get(word);
		if (o == null) {
			words.put(word, new Integer(freq));
		} else {
			throw new IllegalStateException("Word " + word
					+ " already exist in lexicon");
		}
	}

	/**
	 * Removes a word from the lexicon.
	 * 
	 * @param word
	 *            the word to remove
	 */
	public void remove(String word) {
		acquireLock();
		words.remove(word);
		releaseLock();
	}

	/**
	 * Tests if the given word is contained in the lexicon.
	 * 
	 * @param word
	 *            the word to test
	 * @return <code>true</code> if the supplied word exists in the lexicon,
	 *         <code>false</code> otherwise
	 */
	public boolean contains(String word) {
		acquireLock();
		boolean b = words.get(word) != null;
		releaseLock();
		return b;
	}

	/**
	 * Returns the frequency count of a given word.
	 * 
	 * @param word
	 *            the word
	 * @return the frequency of the word, or <code>-1</code> if the word does
	 *         not exist in the lexicon
	 */
	public int getFrequency(String word) {
		acquireLock();
		Object o = words.get(word);
		int f = o == null ? -1 : ((Integer) o).intValue();
		releaseLock();
		return f;
	}

	/**
	 * Sets the frequency count of a given word.
	 * 
	 * @param word
	 *            the word
	 * @param freq
	 *            the frequency of the word, or <code>-1</code> if the word
	 *            does not exist in the lexicon
	 */
	public void setFrequency(String word, int freq) {
		acquireLock();
		words.put(word, new Integer(freq));
		releaseLock();
	}

	/**
	 * Sets the frequency count of a given word asynchronously.
	 * 
	 * <p>
	 * This method is not synchronized.
	 * </p>
	 * 
	 * @param word
	 *            the word
	 * @param freq
	 *            the frequency of the word, or <code>-1</code> if the word
	 *            does not exist in the lexicon
	 */
	public void setFrequencyAsynch(String word, int freq) {
		words.put(word, new Integer(freq));
	}

	/**
	 * Tests if the given word is contained in the lexicon asynchronously.
	 * 
	 * <p>
	 * This method is not synchronized.
	 * </p>
	 * 
	 * @param word
	 *            the word to test
	 * @return <code>true</code> if the supplied word exists in the lexicon,
	 *         <code>false</code> otherwise
	 */
	public boolean containsAsynch(String word) {
		return words.get(word) != null;
	}

	/**
	 * Returns the frequency count of a given word asynchronously.
	 * 
	 * <p>
	 * This method is not synchronized.
	 * </p>
	 * 
	 * @param word
	 *            the word
	 * @return the frequency of the word, or <code>-1</code> if the word does
	 *         not exist in the lexicon
	 */
	public int getFrequencyAsynch(String word) {
		Object o = words.get(word);
		int f;
		if (o == null) {
			f = -1;
		} else {
			f = ((Integer) o).intValue();
		}
		return f;
	}

	/**
	 * Returns the cardinality of the lexicon asynchronously.
	 * 
	 * <p>
	 * This method is not synchronized.
	 * </p>
	 * 
	 * @return the number of words in the lexicon
	 */
	public int sizeAsynch() {
		return words.size();
	}

	/**
	 * Returns the cardinality of the lexicon.
	 * 
	 * @return the number of words in the lexicon
	 */
	public int size() {
		acquireLock();
		int size = words.size();
		releaseLock();
		return size;
	}

	/**
	 * Returns a <em>safe clone</em> of this lexicon.
	 * 
	 * @return a <em>safe clone</em> of this lexicon
	 */
	@Override
	public Object clone() {
		acquireLock();
		Object obj = ((HashMap) words).clone();
		releaseLock();
		return obj;
	}

	/**
	 * Convenience main method to enable lexicon write operations from a command
	 * shell.
	 * 
	 * @param argv
	 *            argument array, first argument is interpreted as a lexicon
	 *            source file, second argument is a target file that output will
	 *            be written to, third argument is the operation number which
	 *            must be a valid integer that is equal to one of the op mask
	 *            constants defined in this class
	 */
	public static void main(String[] argv) throws Exception {
		SWI_Lexicon lexicon = new SWI_Lexicon(new FileInputStream(argv[0]));
		lexicon.writeLexiconOpMask(new FileOutputStream(argv[1]), Integer
				.parseInt(argv[2]));
	}

	/**
	 * A SAX XML stream handler that parses lexicon data.
	 * 
	 * <p>
	 * All lexcions are enclosed within a <code>lexicon</code> tag.
	 * </p>
	 * 
	 * <p>
	 * The lexicon can be either <em>linear</em> or <em>combinatoric</em>.
	 * </p>
	 * 
	 * <p>
	 * A linear lexicon consists of a series of <code>word</code> tags. The
	 * only permissible text within this tag (as of now) is a string designating
	 * a word to be included in the lexicon.
	 * </p>
	 * 
	 * <p>
	 * A combinatoric lexicon consists of a zero or more <code>word</code>
	 * tags, which (on the same level in the XML tree) can be interleaved with
	 * <code>combination</code> tags. A <code>combination</code> tag
	 * <em>must</em> have one or more <code>group</code> tags. A group tag
	 * <em>must</em> contain one or more <code>entity</code> tags. A
	 * <code>entity</code> tag consists of a string designating a word
	 * (interpreted in the liberal sense). A group decides the permissible
	 * combinations. None of the entities within a group may combine, however
	 * all entities within a group will be combined with all other entities in
	 * all other groups, in all combinations possible.
	 * </p>
	 * 
	 * @author pkriste
	 */
	private class Handler extends DefaultHandler {

		private static final String ATTR_QNAME_FREQUENCY = "f";

		/**
		 * Creates a SAX XML stream handler that parses lexicon data.
		 */
		public Handler() {
		}

		/*
		 * Tracks stacklevel: 1. lexicon element 2. word element or combination
		 * element 3. group element under a combination element 4. entity
		 * element under a group element
		 */
		private int stackLevel = 0;
		// Buffers current word at element
		private StringBuffer buffer = new StringBuffer();
		private int freq;

		// Increases stack level
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			stackLevel++;
			if (stackLevel == 2) {
				if (attributes.getQName(0).equalsIgnoreCase(
						ATTR_QNAME_FREQUENCY)) {
					freq = Integer.parseInt(attributes.getValue(0));
				} else {
					freq = 0;
				}
			}
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
			words.put(word, new Integer(freq));
		}

	}

}
