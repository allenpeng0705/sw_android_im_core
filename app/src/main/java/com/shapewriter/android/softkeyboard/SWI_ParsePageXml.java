package com.shapewriter.android.softkeyboard;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.BitmapFactory;

public class SWI_ParsePageXml {

	private SWI_PageBase mPageBase;

	public SWI_ParsePageXml(String xmlPath, String filename, String imagePath) {
		/* Get a SAXParser from the SAXPArserFactory. */
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();

			/* Create a new ContentHandler and apply it to the XML-Reader */
			ParsePageXmlHandler parsePageXmlHandler = new ParsePageXmlHandler(xmlPath, imagePath);
			xr.setContentHandler(parsePageXmlHandler);

			/* Parse the xml-data from our URL. */
			InputStream is = SWI_FileIO.readAsInputStream(xmlPath, filename);
			
			xr.parse(new InputSource(is));
			is.close();
			mPageBase = parsePageXmlHandler.getParsedData();
		} catch (Exception e) {
			//Log.e("chen", "in ParseLayoutXml" + e.toString());
		}
	}

	public SWI_PageBase getPageBase() {
		return mPageBase;
	}
}

/**
 * The classed below are inner class which are called only by ParsePageXml.java
 */
class ParsePageXmlHandler extends DefaultHandler {

	private boolean in_page = false;
	private boolean in_traceable = false;
	private boolean in_aui = false;
	private boolean in_keyboard = false;
	private boolean in_marginground = false;

	private SWI_PageBase pageBase;

	private int curAuiLeft = 0;
	private int curAuiTop = 0;
	private int curAuiWidth = 0;
	private int curAuiHeight = 0;

	private int kbdLeft = 0;
	private int kbdTop = 0;
	private int kbdWidth = 0;
	private int kbdHeight = 0;

	private String xmlPath;
	private String imagePath;

	public ParsePageXmlHandler(String xmlPath, String imagePath) {
		this.xmlPath = xmlPath;
		this.imagePath = imagePath;
	}

	@Override
	public void startDocument() throws SAXException {
		this.pageBase = new SWI_PageBase();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {

		if (localName.equals("page")) {
			this.in_page = true;
			pageBase.width = (Integer.parseInt(atts.getValue("width")));
			pageBase.height = (Integer.parseInt(atts.getValue("height")));
			pageBase.type = (atts.getValue("type"));
			pageBase.name = (atts.getValue("name"));
		} else if (localName.equals("traceable")) {
			this.in_traceable = true;
		} else if (localName.equals("AUI")) {
			this.in_aui = true;
			curAuiLeft = Integer.parseInt(atts.getValue("left"));
			curAuiTop = Integer.parseInt(atts.getValue("top"));
			curAuiWidth = Integer.parseInt(atts.getValue("width"));
			curAuiHeight = Integer.parseInt(atts.getValue("height"));
		} else if (localName.equals("keyboard")) {
			this.in_keyboard = true;
			kbdLeft = Integer.parseInt(atts.getValue("left"));
			kbdTop = Integer.parseInt(atts.getValue("top"));
			kbdWidth = Integer.parseInt(atts.getValue("width"));
			kbdHeight = Integer.parseInt(atts.getValue("height"));
		} else if (localName.equals("marginground")) {
			this.in_marginground = true;
		}
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("page")) {
			this.in_page = false;
		} else if (localName.equals("traceable")) {
			this.in_traceable = false;
		} else if (localName.equals("AUI")) {
			this.in_aui = false;
		} else if (localName.equals("keyboard")) {
			this.in_keyboard = false;
		} else if (localName.equals("marginground")) {
			this.in_marginground = false;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		String str = new String(ch, start, length);

		if (this.in_page) {
			if (this.in_traceable) {
				if (str.equals("true")) {
					pageBase.traceable = true;
				} else {
					pageBase.traceable = false;
				}
			} else if (this.in_aui) {
				// Toast.makeText(context, "in_aui", Toast.LENGTH_SHORT).show();
				ParseAuiXml pa = new ParseAuiXml(xmlPath, str, imagePath);

				SWI_AuiBase aui = pa.getAui();

				pageBase.auiList.add(aui);
				pageBase.getLastAui().left = curAuiLeft;
				pageBase.getLastAui().top = curAuiTop;
				pageBase.getLastAui().width = curAuiWidth;
				pageBase.getLastAui().height = curAuiHeight;
			} else if (this.in_keyboard) {
				// Toast.makeText(context, "in_keyboard",
				// Toast.LENGTH_SHORT).show();
				ParseKeyboardXml pkbd = new ParseKeyboardXml(xmlPath, str, imagePath);
				SWI_KeyboardBase kbd = pkbd.getKeyboard();
				kbd.top = kbdTop;
				kbd.left = kbdLeft;
				kbd.width = kbdWidth;
				kbd.height = kbdHeight;
				pageBase.keyboard = kbd;
			} else if (this.in_marginground) {
				pageBase.marginground = BitmapFactory.decodeFile(imagePath + "/" + str);
			}
		}
	}

	public SWI_PageBase getParsedData() {
		return pageBase;
	}
}

class ParseAuiXml {

	private SWI_AuiBase mAui;

	public ParseAuiXml(String xmlPath, String filename, String imagePath) {
		/* Get a SAXParser from the SAXPArserFactory. */
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();

			/* Create a new ContentHandler and apply it to the XML-Reader */
			ParseAuiXmlHandler parseAuiXmlHandler = new ParseAuiXmlHandler(xmlPath, imagePath);
			xr.setContentHandler(parseAuiXmlHandler);

			/* Parse the xml-data from our URL. */
			InputStream is = SWI_FileIO.readAsInputStream(xmlPath, filename);
			xr.parse(new InputSource(is));
			is.close();
			mAui = parseAuiXmlHandler.getParsedData();
		} catch (Exception e) {

		}
	}

	public SWI_AuiBase getAui() {
		return mAui;
	}
}

class ParseAuiXmlHandler extends DefaultHandler {

	private String imagePath;
	private boolean in_aui = false;
	private boolean in_background = false;
	private boolean in_firstArrow = false;
	private boolean in_secondArrow = false;
	private boolean in_candidateHighLight = false;
	private boolean in_unselectable = false;
	private boolean in_selectable = false;
	private boolean in_fontSize = false;
	private boolean in_basicLine = false;
	private boolean in_logo = false;

	private SWI_AuiBase aui;

	public ParseAuiXmlHandler(String xmlPath, String imagePath) {
		this.imagePath = imagePath;
		aui = new SWI_AuiBase();
	}

	@Override
	public void startDocument() throws SAXException {

	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (localName.equals("AUI")) {
			this.in_aui = true;
			aui.mode = atts.getValue("mode");
		} else if (localName.equals("background")) {
			this.in_background = true;
		} else if (localName.equals("firstArrow")) {
			this.in_firstArrow = true;
			aui.firstArrowLeft = (Integer.parseInt(atts.getValue("left")))*3;
			aui.firstArrowTop = (Integer.parseInt(atts.getValue("top")))*3;
			aui.firstArrowWidth = (Integer.parseInt(atts.getValue("width")))*3;
			aui.firstArrowHeight = (Integer.parseInt(atts.getValue("height")))*3;
		} else if (localName.equals("secondArrow")) {
			this.in_secondArrow = true;
			aui.secondArrowLeft = (Integer.parseInt(atts.getValue("left")))*3;
			aui.secondArrowTop = (Integer.parseInt(atts.getValue("top")))*3;
			aui.secondArrowWidth = (Integer.parseInt(atts.getValue("width")))*3;
			aui.secondArrowHeight = (Integer.parseInt(atts.getValue("height")))*3;
		} else if (localName.equals("candidateHighLight")) {
			this.in_candidateHighLight = true;
			aui.highLightBegin = (Integer.parseInt(atts.getValue("begin")))*3;
			aui.highLightEnd = (Integer.parseInt(atts.getValue("end")))*3;
		} else if (localName.equals("unselectable")) {
			this.in_unselectable = true;
		} else if (localName.equals("selectable")) {
			this.in_selectable = true;
		} else if (localName.equals("fontSize")) {
			this.in_fontSize = true;
		} else if (localName.equals("basicLine")) {
			this.in_basicLine = true;
		} else if (localName.equals("logo")) {
			this.in_logo = true;
			aui.logoTop = (Integer.parseInt(atts.getValue("top")))*3;
			aui.logoLeft = (Integer.parseInt(atts.getValue("left")))*3;
		}

	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("AUI")) {
			this.in_aui = false;
		} else if (localName.equals("background")) {
			this.in_background = false;
		} else if (localName.equals("firstArrow")) {
			this.in_firstArrow = false;
		} else if (localName.equals("secondArrow")) {
			this.in_secondArrow = false;
		} else if (localName.equals("candidateHighLight")) {
			this.in_candidateHighLight = false;
		} else if (localName.equals("unselectable")) {
			this.in_unselectable = false;
		} else if (localName.equals("selectable")) {
			this.in_selectable = false;
		} else if (localName.equals("fontSize")) {
			this.in_fontSize = false;
		} else if (localName.equals("basicLine")) {
			this.in_basicLine = false;
		} else if (localName.equals("logo")) {
			this.in_logo = false;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.in_aui) {
			if (this.in_background) {
				aui.background = BitmapFactory.decodeFile(imagePath + "/" + new String(ch, start, length));
			} else if (this.in_firstArrow) {
				if (this.in_unselectable) {
					aui.firstArrowUnSelect = BitmapFactory.decodeFile(imagePath + "/" + new String(ch, start, length));
				} else if (this.in_selectable) {
					aui.firstArrowSelect = BitmapFactory.decodeFile(imagePath + "/" + new String(ch, start, length));
				}
			} else if (this.in_secondArrow) {
				if (this.in_unselectable) {
					aui.secondArrowUnSelect = BitmapFactory.decodeFile(imagePath + "/" + new String(ch, start, length));
				} else if (this.in_selectable) {
					aui.secondArrowSelect = BitmapFactory.decodeFile(imagePath + "/" + new String(ch, start, length));
				}
			} else if (this.in_candidateHighLight) {
				aui.candidateHighLight = BitmapFactory.decodeFile(imagePath + "/" + new String(ch, start, length));
			} else if (this.in_fontSize) {
				aui.fontSize = (Integer.parseInt(new String(ch, start, length)))*3;
			} else if (this.in_basicLine) {
				aui.basicLine = (Integer.parseInt(new String(ch, start, length)))*3;
			} else if (this.in_logo) {
				aui.logo = BitmapFactory.decodeFile(imagePath + "/" + new String(ch, start, length));
			}
		}
	}

	public SWI_AuiBase getParsedData() {
		return aui;
	}
}

class ParseKeyboardXml {

	private SWI_KeyboardBase keyboard;

	public ParseKeyboardXml(String xmlPath, String filename, String imagePath) {
		/* Get a SAXParser from the SAXPArserFactory. */
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();

			/* Create a new ContentHandler and apply it to the XML-Reader */
			ParseKeyboardXmlHandler parseKeyboardXmlHandler = new ParseKeyboardXmlHandler(xmlPath, imagePath);
			xr.setContentHandler(parseKeyboardXmlHandler);

			/* Parse the xml-data from our URL. */
			InputStream is = SWI_FileIO.readAsInputStream(xmlPath, filename);

			xr.parse(new InputSource(is));
			is.close();
			keyboard = parseKeyboardXmlHandler.getParsedData();
		} catch (Exception e) {

		}
	}

	public SWI_KeyboardBase getKeyboard() {
		return keyboard;
	}
}

class ParseKeyboardXmlHandler extends DefaultHandler {

	private String imagePath;
	private boolean in_keyboard = false;
	private boolean in_key = false;
	private boolean in_label = false;
	private boolean in_value = false;
	private boolean in_highLightImage = false;
	private boolean in_anchor = false;
	private boolean in_map = false;
	private int scale = 1;

	private SWI_KeyboardBase keyboard;

	public ParseKeyboardXmlHandler(String xmlPath, String imagePath) {
		this.imagePath = imagePath;
		keyboard = new SWI_KeyboardBase();
	}

	@Override
	public void startDocument() throws SAXException {

	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) {

		if (localName.equals("keyboard")) {
			this.in_keyboard = true;
			keyboard.name = new String(atts.getValue("name"));
			keyboard.background = BitmapFactory.decodeFile(imagePath + "/" +  atts.getValue("background"));
			keyboard.foreground = BitmapFactory.decodeFile(imagePath + "/" + atts.getValue("foreground"));
//			String zoomrate = new String(atts.getValue("scale"));
//			if (zoomrate.length() > 0) {
//				this.scale = Integer.parseInt(zoomrate);
//			}
		} else if (localName.equals("key")) {
			this.in_key = true;

			SWI_SoftKeyBase key = new SWI_SoftKeyBase();
			key.left = (Integer.parseInt(atts.getValue("left")))*3;
			key.top = (Integer.parseInt(atts.getValue("top")))*3;
			key.width = (Integer.parseInt(atts.getValue("width")))*3;
			key.height = (Integer.parseInt(atts.getValue("height")))*3;
			key.type = (atts.getValue("type"));
			keyboard.keyList.add(key);
		} else if (localName.equals("label")) {
			this.in_label = true;
		} else if (localName.equals("value")) {
			this.in_value = true;
		} else if (localName.equals("map")) {
			this.in_map = true;
		} else if (localName.equals("highLightImage")) {
			this.in_highLightImage = true;
			keyboard.getLastKey().highLightTop = (
					Integer.parseInt(atts.getValue("top")))*3;
			keyboard.getLastKey().highLightLeft = (
					Integer.parseInt(atts.getValue("left")))*3;
		} else if (localName.equals("anchor")) {
			this.in_anchor = true;
		}
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("keyboard")) {
			this.in_keyboard = false;
		} else if (localName.equals("key")) {
			this.in_key = false;
		} else if (localName.equals("label")) {
			this.in_label = false;
		} else if (localName.equals("value")) {
			this.in_value = false;
		} else if (localName.equals("map")) {
			this.in_map = false;
		} else if (localName.equals("highLightImage")) {
			this.in_highLightImage = false;
		} else if (localName.equals("anchor")) {
			this.in_anchor = false;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.in_keyboard) {
			if (this.in_key) {
				if (this.in_label) {
					keyboard.getLastKey().label = (new String(ch, start, length));
				} else if (this.in_value) {
					keyboard.getLastKey().valueList.add(new String(ch, start, length));
				} else if (this.in_map) {
					keyboard.getLastKey().mapList.add(new String(ch, start, length).charAt(0));
				} else if (this.in_highLightImage) {
					keyboard.getLastKey().highLightImage = BitmapFactory.decodeFile(imagePath
							+ "/" + new String(ch, start, length));
				} else if (this.in_anchor) {
					keyboard.getLastKey().anchor = (new String(ch, start, length));
				}
			}
		}
	}

	public SWI_KeyboardBase getParsedData() {
		return keyboard;
	}
}

class Layout {

	public List<SWI_PageBase> pageList;

	public Layout() {
		this.pageList = new ArrayList<SWI_PageBase>();
	}

	
	public SWI_PageBase getLastPage(){
		int num = pageList.size();
		return pageList.get(num - 1);
	}

	public void destroy(){
		for(int i = 0; i < pageList.size(); i++){
			pageList.get(i).destroy();
		}
		pageList.clear();
		pageList = null;
	}
}
