/**
 * Copyright (C) 2001 Maynard Demmon, maynard@organic.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
 
package com.organic.maynard.outliner;

import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.StringTools;

import org.xml.sax.*;

// WebFile
import com.yearahead.io.*;
import java.net.MalformedURLException;

public class OPMLFileFormat extends HandlerBase implements SaveFileFormat, OpenFileFormat {

	// Constants
	public static final String ELEMENT_OPML = "opml";
	public static final String ELEMENT_HEAD = "head";
	public static final String ELEMENT_TITLE = "title";
	public static final String ELEMENT_DATE_CREATED = "dateCreated";
	public static final String ELEMENT_DATE_MODIFIED = "dateModified";
	public static final String ELEMENT_OWNER_NAME = "ownerName";
	public static final String ELEMENT_OWNER_EMAIL = "ownerEmail";
	public static final String ELEMENT_EXPANSION_STATE = "expansionState";
	public static final String ELEMENT_VERTICAL_SCROLL_STATE = "vertScrollState";
	public static final String ELEMENT_WINDOW_TOP = "windowTop";
	public static final String ELEMENT_WINDOW_LEFT = "windowLeft";
	public static final String ELEMENT_WINDOW_BOTTOM = "windowBottom";
	public static final String ELEMENT_WINDOW_RIGHT = "windowRight";
	public static final String ELEMENT_BODY = "body";
	public static final String ELEMENT_OUTLINE = "outline";

	public static final String ATTRIBUTE_TEXT = "text";
	public static final String ATTRIBUTE_IS_COMMENT = "isComment";

	// Open File Settings
    private org.xml.sax.Parser parser = new com.jclark.xml.sax.Driver();
    private DocumentInfo docInfo = null;
    private TreeContext tree = null;
	
	// Constructors
	public OPMLFileFormat() {
		parser.setDocumentHandler(this);
		parser.setErrorHandler(this);
	}

	
	// SaveFileFormat Interface
	public boolean save(TreeContext tree, DocumentInfo docInfo) {

		String text = prepareFile(tree, docInfo);

		// WebFile
		if (Preferences.WEB_FILE_SYSTEM.cur)
		{
			try
			{
				return WebFile.save(Preferences.WEB_FILE_URL.cur,
									docInfo.getPath(), text);
			}
			catch(IOException x)
			{
				x.printStackTrace();
				return false;
			}
		}
		else
		{
			return FileFormatManager.writeFile(
			    docInfo.getPath(), 
			    docInfo.getEncodingType(),
				text
				);
		}
	}
	
	public boolean supportsComments() {return true;}

	private String prepareFile(TreeContext tree, DocumentInfo docInfo) {
		String lineEnding = Preferences.platformToLineEnding(docInfo.getLineEnding());
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<?xml version=\"1.0\" encoding=\"" + docInfo.getEncodingType() + "\" ?>" + lineEnding);
		buf.append("<" + ELEMENT_OPML + " version=\"1.0\">" + lineEnding);
		
		buf.append("<" + ELEMENT_HEAD + ">" + lineEnding);

		buf.append("<" + ELEMENT_TITLE +                 ">" + escapeXMLText(docInfo.getPath()) +                        "</" + ELEMENT_TITLE +                 ">" + lineEnding); // We'll use path for the title since that is how our outliner difines window titles.
		buf.append("<" + ELEMENT_DATE_CREATED +          ">" + escapeXMLText(docInfo.getDateCreated()) +                 "</" + ELEMENT_DATE_CREATED +          ">" + lineEnding);
		buf.append("<" + ELEMENT_DATE_MODIFIED +         ">" + escapeXMLText(docInfo.getDateModified()) +                "</" + ELEMENT_DATE_MODIFIED +         ">" + lineEnding);
		buf.append("<" + ELEMENT_OWNER_NAME +            ">" + escapeXMLText(docInfo.getOwnerName()) +                   "</" + ELEMENT_OWNER_NAME +            ">" + lineEnding);
		buf.append("<" + ELEMENT_OWNER_EMAIL +           ">" + escapeXMLText(docInfo.getOwnerEmail()) +                  "</" + ELEMENT_OWNER_EMAIL +           ">" + lineEnding);
		buf.append("<" + ELEMENT_EXPANSION_STATE +       ">" + escapeXMLText(docInfo.getExpandedNodesStringShifted(1)) + "</" + ELEMENT_EXPANSION_STATE +       ">" + lineEnding);
		buf.append("<" + ELEMENT_VERTICAL_SCROLL_STATE + ">" + escapeXMLText("" + docInfo.getVerticalScrollState()) +    "</" + ELEMENT_VERTICAL_SCROLL_STATE + ">" + lineEnding);
		buf.append("<" + ELEMENT_WINDOW_TOP +            ">" + escapeXMLText("" + docInfo.getWindowTop()) +              "</" + ELEMENT_WINDOW_TOP +            ">" + lineEnding);
		buf.append("<" + ELEMENT_WINDOW_LEFT +           ">" + escapeXMLText("" + docInfo.getWindowLeft()) +             "</" + ELEMENT_WINDOW_LEFT +           ">" + lineEnding);
		buf.append("<" + ELEMENT_WINDOW_BOTTOM +         ">" + escapeXMLText("" + docInfo.getWindowBottom()) +           "</" + ELEMENT_WINDOW_BOTTOM +         ">" + lineEnding);
		buf.append("<" + ELEMENT_WINDOW_RIGHT +          ">" + escapeXMLText("" + docInfo.getWindowRight()) +            "</" + ELEMENT_WINDOW_RIGHT +          ">" + lineEnding);

		buf.append("</" + ELEMENT_HEAD + ">" + lineEnding);

		buildOutliner(tree.getRootNode(),lineEnding, buf);
		
		buf.append("</" + ELEMENT_OPML + ">" + lineEnding);
		return buf.toString();
	}

	private void buildOutliner(Node node, String lineEnding, StringBuffer buf) {
		if (node.isRoot()) {
			buf.append("<" + ELEMENT_BODY + ">" + lineEnding);
			for (int i = 0; i < node.numOfChildren(); i++) {
				buildOutliner(node.getChild(i), lineEnding, buf);
			}
			buf.append("</" + ELEMENT_BODY + ">" + lineEnding);
		} else if (node.isLeaf()) {
			if (node.isComment()) {
				buf.append("<" + ELEMENT_OUTLINE + " " + ATTRIBUTE_IS_COMMENT + "=\"true\" " + ATTRIBUTE_TEXT + "=\"" + escapeXMLAttribute(node.getValue()) + "\"/>" + lineEnding);
			} else {
				buf.append("<" + ELEMENT_OUTLINE + " " + ATTRIBUTE_TEXT + "=\"" + escapeXMLAttribute(node.getValue()) + "\"/>" + lineEnding);
			}
		} else {
			if (node.isComment()) {
				buf.append("<" + ELEMENT_OUTLINE + " " + ATTRIBUTE_IS_COMMENT + "=\"true\" " + ATTRIBUTE_TEXT + "=\"" + escapeXMLAttribute(node.getValue()) + "\">" + lineEnding);
			} else {
				buf.append("<" + ELEMENT_OUTLINE + " " + ATTRIBUTE_TEXT + "=\"" + escapeXMLAttribute(node.getValue()) + "\">" + lineEnding);
			}
			for (int i = 0; i < node.numOfChildren(); i++) {
				buildOutliner(node.getChild(i), lineEnding, buf);
			}
			buf.append("</" + ELEMENT_OUTLINE + ">" + lineEnding);		
		}
	}
	
	private String escapeXMLAttribute(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "<", "&lt;");
		text = StringTools.replace(text, "\"", "&quot;");
		return text;
	}

	private String escapeXMLText(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "<", "&lt;");
		text = StringTools.replace(text, "]]>", "]]&gt;");
		return text;
	}

	
	// OpenFileFormat Interface
	private boolean errorOccurred = false;
	
	public int open(TreeContext tree, DocumentInfo docInfo) {
		// Set the objects we are going to populate.
		this.docInfo = docInfo;
		this.tree = tree;
		
		// Do the Parsing
		int success = OpenFileFormat.FAILURE;
		errorOccurred = false;
		
		try {

			// WebFile
			BufferedReader buffer;
			if (Preferences.WEB_FILE_SYSTEM.cur)
			{
				buffer = WebFile.open(Preferences.WEB_FILE_URL.cur,
									  docInfo.getPath());
			}
			else
			{
				FileInputStream fileInputStream = new FileInputStream(docInfo.getPath());
				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, docInfo.getEncodingType());
				buffer = new BufferedReader(inputStreamReader);
			}

			parser.parse(new InputSource(buffer));
			if (errorOccurred) {
				success = OpenFileFormat.FAILURE;
				return success;
			}
			success = OpenFileFormat.SUCCESS;
		} catch (SAXException e) {
			success = OpenFileFormat.FAILURE;
		} catch (IOException e) {
			success = OpenFileFormat.FAILURE;
		} catch (Exception e) {
			success = OpenFileFormat.FAILURE;
		}
				
		return success;
	}
	
	// Sax DocumentHandler Implementation
	public void startDocument () {
		this.currentParent = tree.getRootNode();
		
		// Clear out any existing children.
		while (currentParent.numOfChildren() > 0) {
			currentParent.removeChild(currentParent.getLastChild());
		}
	}
    
	public void endDocument () {}
	
	private Vector elementStack = new Vector();
	private Node currentParent = null;
	
	public void startElement (String name, AttributeList atts) {
		//System.out.println("Start element: " + name);
		elementStack.add(name);
		
		if (name.equals(ELEMENT_OUTLINE)) {
			String text = atts.getValue(ATTRIBUTE_TEXT);
			String isComment = atts.getValue(ATTRIBUTE_IS_COMMENT);
			NodeImpl node = new NodeImpl(tree, text);
			if (isComment != null && isComment.equals("true")) {
				node.setComment(true);
			} else {
				node.setComment(false);
			}
			currentParent.appendChild(node);
			currentParent = node;
		}
	}
	
	public void endElement (String name) throws SAXException {
		//System.out.println("End element: " + name);
		
		if (name.equals(ELEMENT_OUTLINE)) {
			Node parentNode = currentParent.getParent();
			currentParent = parentNode;
		}
		
		elementStack.removeElementAt(elementStack.size() - 1);
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		String elementName = (String) elementStack.lastElement();
		//System.out.println(text);
		
		if (elementName.equals(ELEMENT_TITLE)) {
			docInfo.setTitle(text);
		} else if (elementName.equals(ELEMENT_DATE_CREATED)) {
			docInfo.setDateCreated(text);
		} else if (elementName.equals(ELEMENT_DATE_MODIFIED)) {
			docInfo.setDateModified(text);
		} else if (elementName.equals(ELEMENT_OWNER_NAME)) {
			docInfo.setOwnerName(text);
		} else if (elementName.equals(ELEMENT_OWNER_EMAIL)) {
			docInfo.setOwnerEmail(text);
		} else if (elementName.equals(ELEMENT_EXPANSION_STATE)) {
			docInfo.setExpandedNodesStringShifted(text, -1);
		} else if (elementName.equals(ELEMENT_VERTICAL_SCROLL_STATE)) {
			try {
				docInfo.setVerticalScrollState(Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		} else if (elementName.equals(ELEMENT_WINDOW_TOP)) {
			try {
				docInfo.setWindowTop(Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		} else if (elementName.equals(ELEMENT_WINDOW_LEFT)) {
			try {
				docInfo.setWindowLeft(Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		} else if (elementName.equals(ELEMENT_WINDOW_BOTTOM)) {
			try {
				docInfo.setWindowBottom(Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		} else if (elementName.equals(ELEMENT_WINDOW_RIGHT)) {
			try {
				docInfo.setWindowRight(Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		}
	}
	
	
	// ErrorHandler Interface
	public void error(SAXParseException e) {
		System.out.println("SAXParserException Error: " + e);
		this.errorOccurred = true;
	}

	public void fatalError(SAXParseException e) {
		System.out.println("SAXParserException Fatal Error: " + e);
		this.errorOccurred = true;
	}

	public void warning(SAXParseException e) {
		System.out.println("SAXParserException Warning: " + e);
		this.errorOccurred = true;
	}
}
