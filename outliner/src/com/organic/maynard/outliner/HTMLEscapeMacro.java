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

import com.organic.maynard.util.string.*;
import com.organic.maynard.xml.XMLTools;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class HTMLEscapeMacro extends MacroImpl {

	// Constants
	private static final String E_ESCAPE = "escape";
	
	// Instance Fields
	private boolean escape = true;

	// Class Fields
	private static HTMLEscapeMacroConfig macroConfig = new HTMLEscapeMacroConfig();
	
	
	// The Constructors
	public HTMLEscapeMacro() {
		this("");
	}

	public HTMLEscapeMacro(String name) {
		super(name, true, Macro.SIMPLE_UNDOABLE);
	}


	// Accessors
	public boolean isEscaping() {return this.escape;}
	public void setEscaping(boolean escape) {this.escape = escape;}


	// Macro Interface	
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
		text = transform(text);
		int lengthAfter = text.length();
		
		int difference = lengthAfter - lengthBefore;

		if (textSelection) {
			nodeRangePair.endIndex += difference;
			nodeRangePair.startIndex = nodeRangePair.endIndex;
		}
		
		node.setValue(firstChunk + text + lastChunk);
		return nodeRangePair;
	}
	
	private String transform(String text) {
		if (isEscaping()) {
			return escape(text);
		} else {
			try {
				return unescape(text);
			} catch (Exception e) {
				return text;
			}
		}
	}
	
	private String escape(String text) {
		text = Replace.replace(text,"&","&amp;");
		text = Replace.replace(text,"<","&lt;");
		text = Replace.replace(text,">","&gt;");
		text = Replace.replace(text,"\"","&quot;");
		return text;
	}

	private String unescape(String text) {
		text = Replace.replace(text,"&amp;","&");
		text = Replace.replace(text,"&lt;","<");
		text = Replace.replace(text,"&gt;",">");
		text = Replace.replace(text,"&quot;","\"");
		return text;
	}

	
	// Saving the Macro
	protected void prepareFile (StringBuffer buf) {
		buf.append(XMLTools.getXmlDeclaration(null) + "\n");
		buf.append(XMLTools.getElementStart(E_ESCAPE) + isEscaping() + XMLTools.getElementEnd(E_ESCAPE)+ "\n");
	}


	// Sax DocumentHandler Implementation
	protected void handleCharacters(String elementName, String text) {
		if (elementName.equals(E_ESCAPE)) {
			setEscaping(Boolean.valueOf(text).booleanValue());
		}
	}
}