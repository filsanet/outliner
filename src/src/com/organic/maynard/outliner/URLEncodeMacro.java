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
import java.net.*;

public class URLEncodeMacro extends MacroImpl {

	static final long serialVersionUID = 624838780473359965L;

	public static URLEncodeMacroConfig macroConfig = new URLEncodeMacroConfig();
		
	protected boolean encode = true;
	
	// The Constructors
	public URLEncodeMacro() {
		this("");
	}

	public URLEncodeMacro(String name) {
		super(name);
		setUndoable(true);
		setUndoableType(Macro.SIMPLE_UNDOABLE);
	}

	public MacroConfig getConfigurator() {return URLEncodeMacro.macroConfig;}
	public void setConfigurator(MacroConfig configurator) {}
	
	public boolean isEncoding() {return this.encode;}
	public void setEncoding(boolean encode) {this.encode = encode;}
	
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
}