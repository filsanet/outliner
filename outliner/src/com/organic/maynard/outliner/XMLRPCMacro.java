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
	public static final String E_XMLRPC = "xmlrpc";
	public static final String E_SERVER_NAME = "server_name";
	public static final String E_PORT = "port";
	public static final String E_REPLACE = "replace";
	
	// Instance Fields
	private String name = null;
	private boolean undoable = true;
	private int undoableType = Macro.COMPLEX_UNDOABLE;
		
	protected String serverName = "127.0.0.1";
	protected int port = 8088;
	protected boolean replace = false;

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
	public String getServerName() {return serverName;}
	public void setServerName(String serverName) {this.serverName = serverName;}

	public int getPort() {return port;}
	public void setPort(int port) {this.port = port;}

	public boolean isReplacing() {return this.replace;}
	public void setReplacing(boolean replace) {this.replace = replace;}


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
		// Create the XMLRPC Request String
		StringBuffer buf = new StringBuffer();
		nodeRangePair.node.depthPaddedValue(buf, Preferences.LINE_END.cur);
		String requestXmlString = buf.toString();
		
		// Trim leading crap before the XML declaration
		requestXmlString = requestXmlString.substring(requestXmlString.indexOf("<"),requestXmlString.length());
		
		// Instantiate a Client and make the request
		try {
			MyXmlRpcClient client = new MyXmlRpcClient(serverName,port);
			Object obj = client.execute(requestXmlString);
			Node replacementNode = PadSelection.pad(obj.toString(), nodeRangePair.node.getTree(), nodeRangePair.node.getDepth(), Preferences.LINE_END_UNIX).getFirstChild();
			nodeRangePair.node = replacementNode;
			nodeRangePair.startIndex = -1;
			nodeRangePair.endIndex = -1;
		
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
		
		buf.append(XMLTools.getElementStart(E_SERVER_NAME) + XMLTools.escapeXMLText(getServerName()) + XMLTools.getElementEnd(E_SERVER_NAME)+ "\n");
		buf.append(XMLTools.getElementStart(E_PORT) + getPort() + XMLTools.getElementEnd(E_PORT)+ "\n");
		buf.append(XMLTools.getElementStart(E_REPLACE) + isReplacing() + XMLTools.getElementEnd(E_REPLACE)+ "\n");

		buf.append(XMLTools.getElementEnd(E_XMLRPC) + "\n");
		
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
		
		if (elementName.equals(E_SERVER_NAME)) {
			setServerName(text);
		} else if (elementName.equals(E_PORT)) {
			setPort(Integer.parseInt(text));
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