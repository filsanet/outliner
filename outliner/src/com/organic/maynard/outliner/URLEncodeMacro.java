/**
 * Copyright (C) 2000 Maynard Demmon, maynard@organic.com
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

import javax.swing.*;
import com.organic.maynard.util.string.*;
import java.io.*;
import java.net.*;
import org.xml.sax.*;
import java.util.*;
import com.organic.maynard.io.FileTools;
import com.organic.maynard.xml.XMLTools;

public class URLEncodeMacro extends HandlerBase implements Macro {

	// Constants
	public static final String E_ENCODE = "encode";
	
	// Instance Fields
	private String name = null;
	private boolean undoable = true;
	private int undoableType = Macro.SIMPLE_UNDOABLE;
		
	protected boolean encode = true;

	// Class Fields
	public static URLEncodeMacroConfig macroConfig = new URLEncodeMacroConfig();
    private static Parser parser = new com.jclark.xml.sax.Driver();
	private static boolean errorOccurred = false;
	private static ArrayList elementStack = new ArrayList();

	
	// The Constructors
	public URLEncodeMacro() {
		this("");
	}

	public URLEncodeMacro(String name) {
		this.name = name;
		parser.setDocumentHandler(this);
		parser.setErrorHandler(this);	
	}


	// Accessors
	public boolean isEncoding() {return this.encode;}
	public void setEncoding(boolean encode) {this.encode = encode;}


	// Macro Interface	
	public String getName() {return this.name;}
	public void setName(String name) {this.name = name;}

	public String getFileName() {return getName() + ".txt";}
	
	public boolean isUndoable() {return undoable;}
	protected void setUndoable(boolean undoable) {this.undoable = undoable;}

	public int getUndoableType() {return undoableType;}
	protected void setUndoableType(int undoableType) {this.undoableType = undoableType;}
	
	public MacroConfig getConfigurator() {return this.macroConfig;}
	public void setConfigurator(MacroConfig macroConfig) {}
		
	public NodeRangePair process(NodeRangePair nodeRangePair) {
		Node node = nodeRangePair.node;
		
		boolean textSelection = false;
		if ((nodeRangePair.startIndex != -1) && (nodeRangePair.endIndex != -1)) {
			textSelection = true;
		}
		
		String text = node.getValue();
		String firstChunk = "";
		String lastChunk = "";
		if (textSelection) {
			firstChunk = text.substring(0,nodeRangePair.startIndex);
			lastChunk = text.substring(nodeRangePair.endIndex,text.length());
			text = text.substring(nodeRangePair.startIndex,nodeRangePair.endIndex);
		}
		
		int lengthBefore = text.length();
		text = encode(text);
		int lengthAfter = text.length();
		
		int difference = lengthAfter - lengthBefore;

		if (textSelection) {
			nodeRangePair.endIndex += difference;
			nodeRangePair.startIndex = nodeRangePair.endIndex;
		}
		
		node.setValue(firstChunk + text + lastChunk);
		return nodeRangePair;
	}
	
	protected String encode(String text) {
		if (isEncoding()) {
			return URLEncoder.encode(text);
		} else {
			try {
				return URLDecoder.decode(text);
			} catch (Exception e) {
				return text;
			}
		}
	}
	
	public boolean init(File file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			parser.parse(new InputSource(fileInputStream));
			if (errorOccurred) {
				return false;
			}
			return true;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	public boolean save(File file) {
		StringBuffer buf = new StringBuffer();
		buf.append(XMLTools.getXmlDeclaration(null) + "\n");
		buf.append(XMLTools.getElementStart(E_ENCODE) + isEncoding() + XMLTools.getElementEnd(E_ENCODE)+ "\n");
		
		FileTools.dumpStringToFile(file, buf.toString());
		
		return true;
	}


	// Sax DocumentHandler Implementation
	public void startDocument () {}

	public void endDocument () {}

	public void startElement (String name, AttributeList atts) {
		elementStack.add(name);
	}
	
	public void endElement (String name) throws SAXException {
		elementStack.remove(elementStack.size() - 1);
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		String elementName = (String) elementStack.get(elementStack.size() - 1);
		
		if (elementName.equals(E_ENCODE)) {
			setEncoding(Boolean.valueOf(text).booleanValue());
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