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
import com.organic.maynard.xml.XMLTools;
import bsh.Interpreter;
import bsh.NameSpace;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class BSHMacro extends MacroImpl {

	// Constants
	private static final String E_SCRIPT = "script";

	// Instance Fields
	protected String script = "";

	// Class Fields
	private static BSHMacroConfig macroConfig = new BSHMacroConfig();
 
	
	// The Constructors
	public BSHMacro() {
		this("");
	}

	public BSHMacro(String name) {
		super(name, true, Macro.COMPLEX_UNDOABLE);
	}


	// Accessors
	public String getScript() {return script;}
	public void setScript(String script) {this.script = script;}


	// Macro Interface	
	public MacroConfig getConfigurator() {return this.macroConfig;}
	public void setConfigurator(MacroConfig macroConfig) {}
		
	public NodeRangePair process(NodeRangePair nodeRangePair) {
		Node node = nodeRangePair.node;

		// Create the mini tree from the replacement pattern
		if (script.equals("")) {
			return null;
		}
		
		Node replacementNode = node.cloneClean();
		
		try {
			Interpreter bsh = new Interpreter();
			NameSpace nameSpace = new NameSpace("outliner");
			nameSpace.importPackage("com.organic.maynard.outliner");
			
			bsh.setNameSpace(nameSpace);
			bsh.set("node", replacementNode);
			bsh.set("nodeRangePair", nodeRangePair);
			bsh.eval(script);
		} catch (Exception e) {
			System.out.println("BSH Exception: " + e.getMessage());
			
			JOptionPane.showMessageDialog(node.getTree().doc, "BSH Exception: " + e.getMessage());
			
			return null;
		}

		nodeRangePair.node = replacementNode;
		nodeRangePair.startIndex = -1;
		nodeRangePair.endIndex = -1;
		
		return nodeRangePair;
	}

	
	// Saving the Macro
	protected void prepareFile (StringBuffer buf) {
		buf.append(XMLTools.getXmlDeclaration(null) + "\n");
		buf.append(XMLTools.getElementStart(E_SCRIPT) + XMLTools.escapeXMLText(getScript()) + XMLTools.getElementEnd(E_SCRIPT)+ "\n");
	}


	// Sax DocumentHandler Implementation
	protected void handleCharacters(String elementName, String text) {
		if (elementName.equals(E_SCRIPT)) {
			setScript(text);
		}
	}
}