/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
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
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
 
package com.organic.maynard.outliner;

import java.net.*;
import com.organic.maynard.xml.XMLTools;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class URLEncodeMacro extends MacroImpl {

	// Constants
	private static final String E_ENCODE = "encode";
	
	// Instance Fields
	private boolean encode = true;

	// Class Fields
	private static URLEncodeMacroConfig macroConfig = new URLEncodeMacroConfig();
	
	
	// The Constructors
	public URLEncodeMacro() {
		this("");
	}

	public URLEncodeMacro(String name) {
		super(name, true, Macro.SIMPLE_UNDOABLE);
	}


	// Accessors
	public boolean isEncoding() {return this.encode;}
	public void setEncoding(boolean encode) {this.encode = encode;}


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

	
	// Saving the Macro
	protected void prepareFile (StringBuffer buf) {
		buf.append(XMLTools.getXmlDeclaration(null) + "\n");
		buf.append(XMLTools.getElementStart(E_ENCODE) + isEncoding() + XMLTools.getElementEnd(E_ENCODE)+ "\n");
	}


	// Sax DocumentHandler Implementation
	protected void handleCharacters(String elementName, String text) {
		if (elementName.equals(E_ENCODE)) {
			setEncoding(Boolean.valueOf(text).booleanValue());
		}
	}
}