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
import helma.xmlrpc.*;
import com.organic.maynard.io.FileTools;
import com.organic.maynard.xml.XMLTools;

public class XMLRPCMacro extends HandlerBase implements Macro {

	// Constants
	private static final String E_XMLRPC = "xmlrpc";	
	private static final String E_URL = "url";
	private static final String E_CALL = "xmlrpc_call";
	private static final String E_REPLACE = "replace";
	
	// Instance Fields
	private String name = null;
	private boolean undoable = true;
	private int undoableType = Macro.COMPLEX_UNDOABLE;
		
	private boolean replace = false;
	private String url = "http://127.0.0.1/RPC2";
	private String xmlrpcCall = "";

	// Class Fields
	public static XMLRPCMacroConfig macroConfig = new XMLRPCMacroConfig();
	private static Parser parser = new com.jclark.xml.sax.Driver();
	private static boolean errorOccurred = false;
	private static ArrayList elementStack = new ArrayList();

	
	// The Constructors
	public XMLRPCMacro() {
		this("");
	}

	public XMLRPCMacro(String name) {
		this.name = name;
		parser.setDocumentHandler(this);
		parser.setErrorHandler(this);	
	}


	// Accessors
	public String getURL() {return url;}
	public void setURL(String url) {this.url = url;}

	public boolean isReplacing() {return this.replace;}
	public void setReplacing(boolean replace) {this.replace = replace;}

	public String getCall() {return xmlrpcCall;}
	public void setCall(String xmlrpcCall) {this.xmlrpcCall = xmlrpcCall;}


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
		// Get the selected text
		String requestXmlString = null;
		String firstChunk = "";
		String lastChunk = "";
		
		boolean textSelection = false;
		if ((nodeRangePair.startIndex != -1) && (nodeRangePair.endIndex != -1)) {
			textSelection = true;
			
			requestXmlString = nodeRangePair.node.getValue();
			firstChunk = requestXmlString.substring(0,nodeRangePair.startIndex);
			lastChunk = requestXmlString.substring(nodeRangePair.endIndex, requestXmlString.length());
			requestXmlString = requestXmlString.substring(nodeRangePair.startIndex, nodeRangePair.endIndex);
		} else {
			StringBuffer buf = new StringBuffer();
			nodeRangePair.node.getRecursiveValue(buf, Preferences.LINE_END_STRING, true);
			requestXmlString = buf.toString();		
		}

		if (!xmlrpcCall.equals("")) {
			// If xmlrpcCall is not empty then munge it
			requestXmlString = munge(requestXmlString);
		} else {
			// Trim leading crap before the XML declaration
			int startIndex = requestXmlString.indexOf("<");
			if (startIndex > 0) {
				requestXmlString = requestXmlString.substring(startIndex, requestXmlString.length());
			}
		}
		
		// Instantiate a Client and make the request
		try {
			XmlRpcClient client = new XmlRpcClient(url);
			Object obj = client.execute(requestXmlString);
			String text = obj.toString();
					
			Node replacementNode = null;
			
			// Do the right replacement for the selection type.
			if (textSelection) {
				text = Replace.replace(text, "\t", "");
				text = Replace.replace(text, "\r", "");
				text = Replace.replace(text, "\n", "");
				nodeRangePair.node.setValue(firstChunk + text + lastChunk);
				nodeRangePair.startIndex = firstChunk.length();
				nodeRangePair.endIndex = nodeRangePair.startIndex + text.length();
			} else {
				replacementNode = PadSelection.pad(text, nodeRangePair.node.getTree(), nodeRangePair.node.getDepth(), Preferences.LINE_END_UNIX).getFirstChild();
				nodeRangePair.node = replacementNode;
				nodeRangePair.startIndex = -1;
				nodeRangePair.endIndex = -1;
			}
		
			// Display the result
			if (isReplacing()) {
				return nodeRangePair;
			} else {
				System.out.println(obj.toString());
				return null;
			}

		} catch (Exception e) {
			System.out.println("Exception: " + e);
			return null;
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
		
		buf.append(XMLTools.getElementStart(E_XMLRPC) + "\n");
		
		buf.append(XMLTools.getElementStart(E_URL) + XMLTools.escapeXMLText(getURL()) + XMLTools.getElementEnd(E_URL)+ "\n");
		buf.append(XMLTools.getElementStart(E_CALL) + XMLTools.escapeXMLText(getCall()) + XMLTools.getElementEnd(E_CALL)+ "\n");
		buf.append(XMLTools.getElementStart(E_REPLACE) + isReplacing() + XMLTools.getElementEnd(E_REPLACE)+ "\n");

		buf.append(XMLTools.getElementEnd(E_XMLRPC) + "\n");
		
		FileTools.dumpStringToFile(file, buf.toString());
		
		return true;
	}

	private String munge(String text) {
		return Replace.replace(xmlrpcCall, "{$value}", text);
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
		
		if (elementName.equals(E_URL)) {
			setURL(text);
		} else if (elementName.equals(E_CALL)) {
			setCall(text);
		} else if (elementName.equals(E_REPLACE)) {
			setReplacing(Boolean.valueOf(text).booleanValue());
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