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
import helma.xmlrpc.*;
import java.net.*;

public class XMLRPCMacro extends MacroImpl {

	static final long serialVersionUID = 624838780473359965L;

	public static XMLRPCMacroConfig macroConfig = new XMLRPCMacroConfig();
		
	protected String serverName = "127.0.0.1";
	protected int port = 8088;
	protected boolean replace = false;
		
	// The Constructors
	public XMLRPCMacro() {
		this("");
	}

	public XMLRPCMacro(String name) {
		super(name);
		setUndoable(true);
		setUndoableType(Macro.COMPLEX_UNDOABLE);
	}

	public MacroConfig getConfigurator() {return XMLRPCMacro.macroConfig;}
	public void setConfigurator(MacroConfig configurator) {}

	public String getServerName() {return serverName;}
	public void setServerName(String serverName) {this.serverName = serverName;}

	public int getPort() {return port;}
	public void setPort(int port) {this.port = port;}

	public boolean isReplacing() {return this.replace;}
	public void setReplacing(boolean replace) {this.replace = replace;}
		
	public NodeRangePair process(NodeRangePair nodeRangePair) {
		// Create the XMLRPC Request String
		String requestXmlString = nodeRangePair.node.depthPaddedValue(Preferences.LINE_END.cur);
		
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
}