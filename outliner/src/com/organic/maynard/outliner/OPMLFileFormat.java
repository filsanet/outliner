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

public class OPMLFileFormat 

	extends HandlerBase 
	implements SaveFileFormat, OpenFileFormat, JoeReturnCodes {

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
	
	public static final String ATTRIBUTE_IS_EDITABLE = "isEditable";
	public static final String ATTRIBUTE_IS_EDITABLE_INHERITED = "isEditableInherited";

	public static final String ATTRIBUTE_IS_MOVEABLE = "isMoveable";
	public static final String ATTRIBUTE_IS_MOVEABLE_INHERITED = "isMoveableInherited";

	public static final String ATTRIBUTE_IS_COMMENT = "isComment";
	public static final String ATTRIBUTE_IS_COMMENT_INHERITED = "isCommentInherited";

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
	public byte[] save(TreeContext tree, DocumentInfo docInfo) {
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

	private StringBuffer prepareFile(TreeContext tree, DocumentInfo docInfo) {
		String lineEnding = Preferences.platformToLineEnding(docInfo.getLineEnding());
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<?xml version=\"1.0\" encoding=\"").append(docInfo.getEncodingType()).append("\"?>").append(lineEnding);
		buf.append("<").append(ELEMENT_OPML).append(" version=\"1.0\">").append(lineEnding);
		
		buf.append("<").append(ELEMENT_HEAD).append(">").append(lineEnding);

		buf.append("<").append(ELEMENT_TITLE).append(">").append(escapeXMLText(docInfo.getPath())).append("</").append(ELEMENT_TITLE).append(">").append(lineEnding); // We'll use path for the title since that is how our outliner difines window titles.
		buf.append("<").append(ELEMENT_DATE_CREATED).append(">").append(escapeXMLText(docInfo.getDateCreated())).append("</").append(ELEMENT_DATE_CREATED).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_DATE_MODIFIED).append(">").append(escapeXMLText(docInfo.getDateModified())).append("</").append(ELEMENT_DATE_MODIFIED).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_OWNER_NAME).append(">").append(escapeXMLText(docInfo.getOwnerName())).append("</").append(ELEMENT_OWNER_NAME).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_OWNER_EMAIL).append(">").append(escapeXMLText(docInfo.getOwnerEmail())).append("</").append(ELEMENT_OWNER_EMAIL).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_EXPANSION_STATE).append(">").append(escapeXMLText(docInfo.getExpandedNodesStringShifted(1))).append("</").append(ELEMENT_EXPANSION_STATE).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_VERTICAL_SCROLL_STATE).append(">").append(escapeXMLText("" + docInfo.getVerticalScrollState())).append("</").append(ELEMENT_VERTICAL_SCROLL_STATE).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_WINDOW_TOP).append(">").append(escapeXMLText("" + docInfo.getWindowTop())).append("</").append(ELEMENT_WINDOW_TOP).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_WINDOW_LEFT).append(">").append(escapeXMLText("" + docInfo.getWindowLeft())).append("</").append(ELEMENT_WINDOW_LEFT).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_WINDOW_BOTTOM).append(">").append(escapeXMLText("" + docInfo.getWindowBottom())).append("</").append(ELEMENT_WINDOW_BOTTOM).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_WINDOW_RIGHT).append(">").append(escapeXMLText("" + docInfo.getWindowRight())).append("</").append(ELEMENT_WINDOW_RIGHT).append(">").append(lineEnding);

		buf.append("</").append(ELEMENT_HEAD).append(">").append(lineEnding);

		buf.append("<").append(ELEMENT_BODY).append(">").append(lineEnding);
		Node node = tree.getRootNode();
		for (int i = 0; i < node.numOfChildren(); i++) {
			buildOutlineElement(node.getChild(i), lineEnding, buf);
		}
		buf.append("</").append(ELEMENT_BODY).append(">").append(lineEnding);
		
		buf.append("</").append(ELEMENT_OPML).append(">").append(lineEnding);
		return buf;
	}
	
	private void buildOutlineElement(Node node, String lineEnding, StringBuffer buf) {
		buf.append("<").append(ELEMENT_OUTLINE).append(" ");
		
		if (node.getCommentState() == Node.COMMENT_TRUE) {
			buf.append(ATTRIBUTE_IS_COMMENT).append("=\"true\" ");
			buf.append(ATTRIBUTE_IS_COMMENT_INHERITED).append("=\"true\" ");
			
		} else if (node.getCommentState() == Node.COMMENT_FALSE) {
			buf.append(ATTRIBUTE_IS_COMMENT).append("=\"false\" ");
			buf.append(ATTRIBUTE_IS_COMMENT_INHERITED).append("=\"true\" ");
			
		} else {
			if (node.isComment()) {
				buf.append(ATTRIBUTE_IS_COMMENT).append("=\"true\" ");
			}
		}

		if (node.getEditableState() == Node.EDITABLE_TRUE) {
			buf.append(ATTRIBUTE_IS_EDITABLE).append("=\"true\" ");
			buf.append(ATTRIBUTE_IS_EDITABLE_INHERITED).append("=\"true\" ");
			
		} else if (node.getEditableState() == Node.COMMENT_FALSE) {
			buf.append(ATTRIBUTE_IS_EDITABLE).append("=\"false\" ");
			buf.append(ATTRIBUTE_IS_EDITABLE_INHERITED).append("=\"true\" ");
			
		} else {
			if (node.isEditable()) {
				buf.append(ATTRIBUTE_IS_EDITABLE).append("=\"true\" ");
			}
		}

		if (node.getMoveableState() == Node.MOVEABLE_TRUE) {
			buf.append(ATTRIBUTE_IS_MOVEABLE).append("=\"true\" ");
			buf.append(ATTRIBUTE_IS_MOVEABLE_INHERITED).append("=\"true\" ");
			
		} else if (node.getMoveableState() == Node.MOVEABLE_FALSE) {
			buf.append(ATTRIBUTE_IS_MOVEABLE).append("=\"false\" ");
			buf.append(ATTRIBUTE_IS_MOVEABLE_INHERITED).append("=\"true\" ");
			
		} else {
			if (node.isMoveable()) {
				buf.append(ATTRIBUTE_IS_MOVEABLE).append("=\"true\" ");
			}
		}
				
		buf.append(ATTRIBUTE_TEXT).append("=\"").append(escapeXMLAttribute(node.getValue())).append("\"");
		buildAttributes(node, buf);
		
		if (node.isLeaf()) {
			buf.append("/>").append(lineEnding);
		} else {
			buf.append(">").append(lineEnding);
			
			for (int i = 0; i < node.numOfChildren(); i++) {
				buildOutlineElement(node.getChild(i), lineEnding, buf);
			}
			
			buf.append("</").append(ELEMENT_OUTLINE).append(">").append(lineEnding);		
		}	
	}
	
	private void buildAttributes(Node node, StringBuffer buf) {
		Iterator it = node.getAttributeKeys();
		if (it != null) {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = node.getAttribute(key);
				buf.append(" ").append(key).append("=\"").append(escapeXMLAttribute(value.toString())).append("\"");
			}
		}
	}
	
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
	
	public int open(TreeContext tree, DocumentInfo docInfo, InputStream stream) {
		// Set the objects we are going to populate.
		this.docInfo = docInfo;
		this.tree = tree;
		
		// Do the Parsing
		int success = FAILURE;
		errorOccurred = false;
		
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(stream, docInfo.getEncodingType());
			BufferedReader buf = new BufferedReader(inputStreamReader);

			parser.parse(new InputSource(buf));
			if (errorOccurred) {
				success = FAILURE;
				return success;
			}
			success = SUCCESS;
		} catch (SAXException e) {
			success = FAILURE;
		} catch (IOException e) {
			success = FAILURE;
		} catch (Exception e) {
			success = FAILURE;
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
	
	private boolean anyIsCommentInheritedAttributesFound = false; // used to provide for better interop with outliners that don't support isCommentInherited.
	private boolean anyIsEditableInheritedAttributesFound = false; // used to provide for better interop with outliners that don't support isEditableInherited.
	private boolean anyIsMoveableInheritedAttributesFound = false; // used to provide for better interop with outliners that don't support isMoveableInherited.
	
	public void startElement (String name, AttributeList atts) {
		//System.out.println("Start element: " + name);
		elementStack.add(name);
		
		if (name.equals(ELEMENT_OUTLINE)) {
			NodeImpl node = new NodeImpl(tree, "");
			
			boolean isEditable = true;
			boolean isEditableInherited = false;
			boolean isMoveable = true;
			boolean isMoveableInherited = false;
			boolean isComment = false;
			boolean isCommentInherited = false;
			
			for (int i = 0; i < atts.getLength(); i++) {
				String attName = atts.getName(i);
				String attValue = atts.getValue(i);
				
				if (attName.equals(ATTRIBUTE_TEXT)) {
					node.setValue(attValue);
					
				} else if (attName.equals(ATTRIBUTE_IS_EDITABLE)) {
					if (attValue != null && attValue.equals("false")) {
						isEditable = false;;
					}
				} else if (attName.equals(ATTRIBUTE_IS_EDITABLE_INHERITED)) {
					if (attValue != null && attValue.equals("true")) {
						isEditableInherited = true;;
					}
					anyIsEditableInheritedAttributesFound = true;
					
				} else if (attName.equals(ATTRIBUTE_IS_MOVEABLE)) {
					if (attValue != null && attValue.equals("false")) {
						isMoveable = false;;
					}
				} else if (attName.equals(ATTRIBUTE_IS_MOVEABLE_INHERITED)) {
					if (attValue != null && attValue.equals("true")) {
						isMoveableInherited = true;;
					}
					anyIsMoveableInheritedAttributesFound = true;

				} else if (attName.equals(ATTRIBUTE_IS_COMMENT)) {
					if (attValue != null && attValue.equals("true")) {
						isComment = true;;
					}
				} else if (attName.equals(ATTRIBUTE_IS_COMMENT_INHERITED)) {
					if (attValue != null && attValue.equals("true")) {
						isCommentInherited = true;;
					}
					anyIsCommentInheritedAttributesFound = true;
					
				} else {
					node.setAttribute(attName, attValue);
				}
			}
			
			if (anyIsCommentInheritedAttributesFound) {
				if (isCommentInherited) {
					if (isComment) {
						node.setCommentState(Node.COMMENT_TRUE);
					} else {
						node.setCommentState(Node.COMMENT_FALSE);
					}
				}
			} else {
				if (isComment) {
					node.setCommentState(Node.COMMENT_TRUE);
				}
			}

			if (anyIsEditableInheritedAttributesFound) {
				if (isEditableInherited) {
					if (isEditable) {
						node.setEditableState(Node.EDITABLE_TRUE);
					} else {
						node.setEditableState(Node.EDITABLE_FALSE);
					}
				}
			} else {
				if (!isEditable) {
					node.setEditableState(Node.EDITABLE_FALSE);
				}
			}

			if (anyIsMoveableInheritedAttributesFound) {
				if (isMoveableInherited) {
					if (isMoveable) {
						node.setMoveableState(Node.MOVEABLE_TRUE);
					} else {
						node.setMoveableState(Node.MOVEABLE_FALSE);
					}
				}
			} else {
				if (!isMoveable) {
					node.setMoveableState(Node.MOVEABLE_FALSE);
				}
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
