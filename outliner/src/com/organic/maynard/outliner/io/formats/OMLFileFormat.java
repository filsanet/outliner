/**
 * Copyright (C) 2003 Maynard Demmon, maynard@organic.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 * 
 *  - Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution. 
 * 
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
 
package com.organic.maynard.outliner.io.formats;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.io.*;
import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.StringTools;
import org.xml.sax.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class OMLFileFormat extends HandlerBase implements SaveFileFormat, OpenFileFormat, JoeReturnCodes {
	
	// Constants
		/** The threshold over which we use data elements rather than text atts. */
		public static final int TEXT_THRESHOLD = 20; // TBD: this should be a pref.
		
		// XML Structure
		public static final String ELEMENT_OML = "oml";
		public static final String ELEMENT_HEAD = "head";
		public static final String ELEMENT_METADATA = "metadata";
		public static final String ELEMENT_BODY = "body";
		public static final String ELEMENT_OUTLINE = "outline";
		public static final String ELEMENT_DATA = "data";
		public static final String ELEMENT_ITEM = "item";
		
		public static final String ATTRIBUTE_TEXT = "text";
		public static final String ATTRIBUTE_TYPE = "type";
		public static final String ATTRIBUTE_CREATED = "created";
		public static final String ATTRIBUTE_MODIFIED = "modified";
		public static final String ATTRIBUTE_URL = "url";
		public static final String ATTRIBUTE_NAME = "name";
		public static final String ATTRIBUTE_VERSION = "version";
		
		// Names
		public static final String TITLE = "title";
		public static final String DATE_CREATED = "dateCreated";
		public static final String DATE_MODIFIED = "dateModified";
		public static final String OWNER_NAME = "ownerName";
		public static final String OWNER_EMAIL = "ownerEmail";
		public static final String EXPANSION_STATE = "expansionState";
		public static final String VERTICAL_SCROLL_STATE = "vertScrollState";
		public static final String WINDOW_TOP = "windowTop";
		public static final String WINDOW_LEFT = "windowLeft";
		public static final String WINDOW_BOTTOM = "windowBottom";
		public static final String WINDOW_RIGHT = "windowRight";
		
		public static final String APPLY_FONT_STYLE_FOR_COMMENTS = "applyStyleForComments";
		public static final String APPLY_FONT_STYLE_FOR_EDITABILITY = "applyStyleForEditability";
		public static final String APPLY_FONT_STYLE_FOR_MOVEABILITY = "applyStyleForMoveability";
		
		public static final String IS_READ_ONLY_ATTS_LIST = "readOnlyAttsList";
		
		public static final String IS_EDITABLE = "isEditable";
		public static final String IS_MOVEABLE = "isMoveable";
		public static final String IS_COMMENT = "isComment";
	
	// Open File Settings
	private org.xml.sax.Parser parser = new com.jclark.xml.sax.Driver();
	
	// Constructors
	public OMLFileFormat() {
		parser.setDocumentHandler(this);
		parser.setErrorHandler(this);
	}
	
	private String name = null;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	// SaveFileFormat Interface
	public byte[] save(JoeTree tree, DocumentInfo docInfo) {
		StringBuffer buf = prepareFile(tree, docInfo);
		
		try {
			return buf.toString().getBytes(docInfo.getEncodingType());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return buf.toString().getBytes();
		}
	}
	
	public boolean supportsComments() {return true;}
	public boolean supportsEditability() {return true;}
	public boolean supportsMoveability() {return true;}	
	public boolean supportsAttributes() {return true;}
	public boolean supportsDocumentAttributes() {return true;}
	
	private StringBuffer prepareFile(JoeTree tree, DocumentInfo docInfo) {
		String lineEnding = PlatformCompatibility.platformToLineEnding(docInfo.getLineEnding());
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<?xml version=\"1.0\" encoding=\"").append(docInfo.getEncodingType()).append("\"?>").append(lineEnding);
		buf.append("<").append(ELEMENT_OML).append(" ").append(ATTRIBUTE_VERSION).append("=\"1.0\">").append(lineEnding);
		
		buf.append("<").append(ELEMENT_HEAD).append(">").append(lineEnding);
		
			appendMetadataElement(buf, TITLE, docInfo.getPath(), lineEnding);
			appendMetadataElement(buf, DATE_CREATED, docInfo.getDateCreated(), lineEnding);
			appendMetadataElement(buf, DATE_MODIFIED, docInfo.getDateModified(), lineEnding);
			appendMetadataElement(buf, OWNER_NAME, docInfo.getOwnerName(), lineEnding);
			appendMetadataElement(buf, OWNER_EMAIL, docInfo.getOwnerEmail(), lineEnding);
			appendMetadataElement(buf, EXPANSION_STATE, docInfo.getExpandedNodesStringShifted(1), lineEnding);
			appendMetadataElement(buf, VERTICAL_SCROLL_STATE, "" + docInfo.getVerticalScrollState(), lineEnding);
			appendMetadataElement(buf, WINDOW_TOP, "" + docInfo.getWindowTop(), lineEnding);
			appendMetadataElement(buf, WINDOW_LEFT, "" + docInfo.getWindowLeft(), lineEnding);
			appendMetadataElement(buf, WINDOW_BOTTOM, "" + docInfo.getWindowBottom(), lineEnding);
			appendMetadataElement(buf, WINDOW_RIGHT, "" + docInfo.getWindowRight(), lineEnding);
			
			appendMetadataElement(buf, APPLY_FONT_STYLE_FOR_COMMENTS, "" + docInfo.getApplyFontStyleForComments(), lineEnding);
			appendMetadataElement(buf, APPLY_FONT_STYLE_FOR_EDITABILITY, "" + docInfo.getApplyFontStyleForEditability(), lineEnding);
			appendMetadataElement(buf, APPLY_FONT_STYLE_FOR_MOVEABILITY, "" + docInfo.getApplyFontStyleForMoveability(), lineEnding);
			
			buildMetadataElements(tree, lineEnding, buf);
		
		buf.append("</").append(ELEMENT_HEAD).append(">").append(lineEnding);
		
		buf.append("<").append(ELEMENT_BODY).append(">").append(lineEnding);
			Node node = tree.getRootNode();
			for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
				buildOutlineElement(node.getChild(i), lineEnding, buf);
			}
		buf.append("</").append(ELEMENT_BODY).append(">").append(lineEnding);
		
		buf.append("</").append(ELEMENT_OML).append(">").append(lineEnding);
		return buf;
	}
	
	private void appendMetadataElement(StringBuffer buf, String name, String value, String line_ending) {
		if (name == null || name.length() == 0) {
			return;
		}
		buf.append("\t");
		buf.append("<").append(ELEMENT_METADATA).append(" ").append(ATTRIBUTE_NAME).append("=\"").append(escapeXMLAttribute(name)).append("\">");
		buf.append("<![CDATA[").append(escapeXMLText(value)).append("]]>");
		buf.append("</").append(ELEMENT_METADATA).append(">").append(line_ending);
	}
	
	private void buildMetadataElements(JoeTree tree, String line_ending, StringBuffer buf) {
		Iterator it = tree.getAttributeKeys();
		
		StringBuffer readOnlyAtts = new StringBuffer();
		if (it != null) {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = tree.getAttribute(key);
				
				if (isReservedMetadataName(key)) {
					continue;
				}
				
				boolean isReadOnly = tree.isReadOnly(key);
				if (isReadOnly) {
					readOnlyAtts.append(key).append(" ");
				}
				appendMetadataElement(buf, key, value.toString(), line_ending);
			}
		}
		
		if (readOnlyAtts.length() > 0) {
			appendMetadataElement(buf, IS_READ_ONLY_ATTS_LIST, readOnlyAtts.toString().trim(), line_ending);
		}
	}
	
	private boolean isReservedMetadataName(String name) {
		if (TITLE.equals(name)) {
			return true;
		} else if (DATE_CREATED.equals(name)) {
			return true;
		} else if (DATE_MODIFIED.equals(name)) {
			return true;
		} else if (OWNER_NAME.equals(name)) {
			return true;
		} else if (OWNER_EMAIL.equals(name)) {
			return true;
		} else if (EXPANSION_STATE.equals(name)) {
			return true;
		} else if (VERTICAL_SCROLL_STATE.equals(name)) {
			return true;
		} else if (WINDOW_TOP.equals(name)) {
			return true;
		} else if (WINDOW_LEFT.equals(name)) {
			return true;
		} else if (WINDOW_BOTTOM.equals(name)) {
			return true;
		} else if (WINDOW_RIGHT.equals(name)) {
			return true;
		} else if (APPLY_FONT_STYLE_FOR_COMMENTS.equals(name)) {
			return true;
		} else if (APPLY_FONT_STYLE_FOR_EDITABILITY.equals(name)) {
			return true;
		} else if (APPLY_FONT_STYLE_FOR_MOVEABILITY.equals(name)) {
			return true;
		} else if (IS_READ_ONLY_ATTS_LIST.equals(name)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void buildOutlineElement(Node node, String line_ending, StringBuffer buf) {
		indent(node, buf);
		buf.append("<").append(ELEMENT_OUTLINE);
		
		Object attribute = node.getAttribute(ATTRIBUTE_CREATED);
		if (attribute != null) {
			buf.append(" ").append(ATTRIBUTE_CREATED).append("=\"").append(escapeXMLAttribute(attribute.toString())).append("\"");
		}
		attribute = node.getAttribute(ATTRIBUTE_MODIFIED);
		if (attribute != null) {
			buf.append(" ").append(ATTRIBUTE_MODIFIED).append("=\"").append(escapeXMLAttribute(attribute.toString())).append("\"");
		}
		attribute = node.getAttribute(ATTRIBUTE_TYPE);
		if (attribute != null) {
			buf.append(" ").append(ATTRIBUTE_TYPE).append("=\"").append(escapeXMLAttribute(attribute.toString())).append("\"");
		}
		attribute = node.getAttribute(ATTRIBUTE_URL);
		if (attribute != null) {
			buf.append(" ").append(ATTRIBUTE_URL).append("=\"").append(escapeXMLAttribute(attribute.toString())).append("\"");
		}
		
		String node_value = node.getValue();
		if (node_value.length() <= TEXT_THRESHOLD) {
			buf.append(" ").append(ATTRIBUTE_TEXT).append("=\"").append(escapeXMLAttribute(node_value)).append("\"");
		}
		
		buf.append(">").append(line_ending);
			
			// Data Element
			appendDataElement(node, buf, node.getValue(), line_ending);
			
			// Item Elements
			if (node.getCommentState() == Node.COMMENT_TRUE) {
				appendItemElement(node, buf, IS_COMMENT, "true", line_ending);
			} else if (node.getCommentState() == Node.COMMENT_FALSE) {
				appendItemElement(node, buf, IS_COMMENT, "false", line_ending);
			}
			
			if (node.getEditableState() == Node.EDITABLE_TRUE) {
				appendItemElement(node, buf, IS_EDITABLE, "true", line_ending);
			} else if (node.getEditableState() == Node.EDITABLE_FALSE) {
				appendItemElement(node, buf, IS_EDITABLE, "false", line_ending);
			}
			
			if (node.getMoveableState() == Node.MOVEABLE_TRUE) {
				appendItemElement(node, buf, IS_MOVEABLE, "true", line_ending);
			} else if (node.getMoveableState() == Node.MOVEABLE_FALSE) {
				appendItemElement(node, buf, IS_MOVEABLE, "false", line_ending);
			}
			
			buildItemElements(node, buf, line_ending);
			
			// Child Outlines
			for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
				buildOutlineElement(node.getChild(i), line_ending, buf);
			}
		indent(node, buf);
		buf.append("</").append(ELEMENT_OUTLINE).append(">").append(line_ending);
	}
	
	private void indent(Node node, StringBuffer buf) {
		for (int i = 0, limit = node.getDepth(); i < limit; i++) {
			buf.append("\t");
		}
	}
	
	private boolean isReservedItemName(String name) {
		if (ATTRIBUTE_TEXT.equals(name)) {
			return true;
		} else if (ATTRIBUTE_CREATED.equals(name)) {
			return true;
		} else if (ATTRIBUTE_MODIFIED.equals(name)) {
			return true;
		} else if (ATTRIBUTE_TYPE.equals(name)) {
			return true;
		} else if (ATTRIBUTE_URL.equals(name)) {
			return true;
		} else if (IS_COMMENT.equals(name)) {
			return true;
		} else if (IS_EDITABLE.equals(name)) {
			return true;
		} else if (IS_MOVEABLE.equals(name)) {
			return true;
		} else if (IS_READ_ONLY_ATTS_LIST.equals(name)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void buildItemElements(Node node, StringBuffer buf, String line_ending) {
		Iterator it = node.getAttributeKeys();
		
		StringBuffer readOnlyAtts = new StringBuffer();
		if (it != null) {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = node.getAttribute(key);
				
				if (isReservedItemName(key)) {
					continue;
				}
				
				boolean isReadOnly = node.isReadOnly(key);
				if (isReadOnly) {
					readOnlyAtts.append(key).append(" ");
				}
				appendItemElement(node, buf, key, value.toString(), line_ending);
			}
		}
		
		if (readOnlyAtts.length() > 0) {
			appendItemElement(node, buf, IS_READ_ONLY_ATTS_LIST, readOnlyAtts.toString().trim(), line_ending);
		}
	}
	
	private void appendItemElement(Node node, StringBuffer buf, String name, String value, String line_ending) {
		if (name == null || name.length() == 0) {
			return;
		}
		indent(node, buf);
		buf.append("\t");
		buf.append("<").append(ELEMENT_ITEM).append(" ").append(ATTRIBUTE_NAME).append("=\"").append(escapeXMLAttribute(name)).append("\">");
		buf.append("<![CDATA[").append(escapeXMLText(value)).append("]]>");
		buf.append("</").append(ELEMENT_ITEM).append(">").append(line_ending);
	}
	
	private void appendDataElement(Node node, StringBuffer buf, String value, String line_ending) {
		if (value == null || value.length() <= TEXT_THRESHOLD) {
			return;
		}
		indent(node, buf);
		buf.append("\t");
		buf.append("<").append(ELEMENT_DATA).append(">");
		buf.append("<![CDATA[").append(escapeXMLText(value)).append("]]>");
		buf.append("</").append(ELEMENT_DATA).append(">").append(line_ending);
	}
	
	
	// Utility Methods
	private String escapeXMLAttribute(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "<", "&lt;");
		text = StringTools.replace(text, "\"", "&quot;");
		text = StringTools.replace(text, ">", "&gt;");
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
	
  private DocumentInfo docInfo = null;
  private JoeTree tree = null;
	private ArrayList elementStack = new ArrayList();
	private ArrayList attributesStack = new ArrayList();
	private Node currentParent = null;
	
	public int open(JoeTree tree, DocumentInfo docInfo, InputStream stream) {
		// Set the objects we are going to populate.
		this.docInfo = docInfo;
		this.tree = tree;
		
		// Do the Parsing
		int success = FAILURE;
		/*errorOccurred = false;
		
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(stream, docInfo.getEncodingType());
			BufferedReader buf = new BufferedReader(inputStreamReader);

			parser.parse(new InputSource(buf));
			if (errorOccurred) {
				System.out.println("Error Occurred in OPMLFileFormat");
				success = FAILURE;
				return success;
			}
			success = SUCCESS;
		} catch (SAXException e) {
			System.out.println("SAXException: " + e.getMessage());
			success = FAILURE;
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			success = FAILURE;
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			success = FAILURE;
		}
		
		// Cleanup
		this.tree = null;
		this.docInfo = null;
		this.elementStack.clear();
		this.attributesStack.clear();
		this.currentParent = null;
		*/
		return success;
	}
	/*
	// Sax DocumentHandler Implementation
	public void startDocument () {
		this.currentParent = tree.getRootNode();
		
		// Clear out any existing children.
		while (currentParent.numOfChildren() > 0) {
			currentParent.removeChild(currentParent.getLastChild());
		}
	}
    
	public void endDocument () {}
			
	public void startElement (String name, AttributeList atts) {
		//System.out.println("Start element: " + name);
		elementStack.add(name);
		attributesStack.add(atts);
		
		if (name.equals(ELEMENT_OUTLINE)) {
			NodeImpl node = new NodeImpl(tree, "");
			
			String readOnlyAttsList = new String("");
						
			for (int i = 0, limit = atts.getLength(); i < limit; i++) {
				String attName = atts.getName(i);
				String attValue = atts.getValue(i);
				
				if (attName.equals(ATTRIBUTE_TEXT)) {
					node.setValue(attValue);
					
				} else if (attName.equals(ATTRIBUTE_IS_READ_ONLY_ATTS_LIST)) {
					if (attValue != null) {
						readOnlyAttsList = attValue;
					}
					
				} else if (attName.equals(ATTRIBUTE_IS_MOVEABLE)) {
					if (attValue != null && attValue.equals("false")) {
						node.setMoveableState(Node.MOVEABLE_FALSE);
					} else if (attValue != null && attValue.equals("true")) {
						node.setMoveableState(Node.MOVEABLE_TRUE);
					}
					
				} else if (attName.equals(ATTRIBUTE_IS_EDITABLE)) {
					if (attValue != null && attValue.equals("false")) {
						node.setEditableState(Node.EDITABLE_FALSE);
					} else if (attValue != null && attValue.equals("true")) {
						node.setEditableState(Node.EDITABLE_TRUE);
					}
					
				} else if (attName.equals(ATTRIBUTE_IS_COMMENT)) {
					if (attValue != null && attValue.equals("false")) {
						node.setCommentState(Node.COMMENT_FALSE);
					} else if (attValue != null && attValue.equals("true")) {
						node.setCommentState(Node.COMMENT_TRUE);
					}

				} else if (attName.equals(ATTRIBUTE_CREATED)) {
					node.setAttribute(attName, attValue, true);

				} else if (attName.equals(ATTRIBUTE_MODIFIED)) {
					node.setAttribute(attName, attValue, true);
					
				} else {
					node.setAttribute(attName, attValue);
				}
			}
			
			// Set ReadOnly Property for Attributes
			StringTokenizer tok = new StringTokenizer(readOnlyAttsList);
			while (tok.hasMoreTokens()) {
				String key = tok.nextToken();
				node.setReadOnly(key, true);
			}
						
			currentParent.appendChild(node);
			currentParent = node;
			
		} else if (name.equals(ELEMENT_DOCUMENT_ATTRIBUTE)) {
			String key = atts.getValue(ATTRIBUTE_KEY);
			boolean isReadOnly = Boolean.valueOf(atts.getValue(ATTRIBUTE_IS_READ_ONLY)).booleanValue();
			
			tree.setAttribute(key, "", isReadOnly);
		}
	}
	
	public void endElement (String name) throws SAXException {
		//System.out.println("End element: " + name);
		
		if (name.equals(ELEMENT_OUTLINE)) {
			Node parentNode = currentParent.getParent();
			currentParent = parentNode;
		}
		
		elementStack.remove(elementStack.size() - 1);
		attributesStack.remove(attributesStack.size() - 1);
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		String elementName = (String) elementStack.get(elementStack.size() - 1);
		AttributeList atts = (AttributeList) attributesStack.get(attributesStack.size() - 1);
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
		
		} else if (elementName.equals(ELEMENT_APPLY_FONT_STYLE_FOR_COMMENTS)) {
			docInfo.setApplyFontStyleForComments(Boolean.valueOf(text).booleanValue());
		
		} else if (elementName.equals(ELEMENT_APPLY_FONT_STYLE_FOR_EDITABILITY)) {
			docInfo.setApplyFontStyleForEditability(Boolean.valueOf(text).booleanValue());
		
		} else if (elementName.equals(ELEMENT_APPLY_FONT_STYLE_FOR_MOVEABILITY)) {
			docInfo.setApplyFontStyleForMoveability(Boolean.valueOf(text).booleanValue());
		
		} else if (elementName.equals(ELEMENT_DOCUMENT_ATTRIBUTE)) {
			String key = atts.getValue(ATTRIBUTE_KEY);
			boolean isReadOnly = tree.isReadOnly(key);
			tree.setAttribute(key, text, isReadOnly);
		}
	}
	*/
	
	// ErrorHandler Interface
	public void error(SAXParseException e) {
		System.out.println("SAXParserException Error: " + e.getMessage());
		this.errorOccurred = true;
	}
	
	public void fatalError(SAXParseException e) {
		System.out.println("SAXParserException Fatal Error: " + e.getMessage());
		this.errorOccurred = true;
	}
	
	public void warning(SAXParseException e) {
		System.out.println("SAXParserException Warning: " + e.getMessage());
		this.errorOccurred = true;
	}
	
	
	// File Extensions
	private HashMap extensions = new HashMap();
	
	public void addExtension(String ext, boolean isDefault) {
		extensions.put(ext, new Boolean(isDefault));
	}
	
	public void removeExtension(String ext) {
		extensions.remove(ext);
	}
	
	public String getDefaultExtension() {
		Iterator i = getExtensions();
		while (i.hasNext()) {
			String key = (String) i.next();
			Boolean value = (Boolean) extensions.get(key);
			
			if (value.booleanValue()) {
				return key;
			}
		}
		
		return null;
	}
	
	public Iterator getExtensions() {
		return extensions.keySet().iterator();
	}

	public boolean extensionExists(String ext) {
		Iterator it = getExtensions();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (ext.equals(key)) {
				return true;
			}
		}
		
		return false;
	}
}
