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

import com.organic.maynard.outliner.util.preferences.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.StringTools;
import java.text.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class JustifiedPlaintextExportFileFormat implements ExportFileFormat, JoeReturnCodes {

	// Constants
	private static int COLS = 80;
	private static final int INDENT = 2;
	private static boolean DRAW_LINES = true;
	
	// Constructors
	public JustifiedPlaintextExportFileFormat() {}

	
	// ExportFileFormat Interface
	public boolean supportsComments() {return false;}
	public boolean supportsEditability() {return false;}
	public boolean supportsMoveability() {return false;}
	public boolean supportsAttributes() {return false;}
	public boolean supportsDocumentAttributes() {return false;}
	
	public byte[] save(JoeTree tree, DocumentInfo docInfo) {
		COLS = Preferences.getPreferenceInt(Preferences.JUSTIFIED_PLAINTEXT_COL_WIDTH).cur;
		DRAW_LINES = Preferences.getPreferenceBoolean(Preferences.JUSTIFIED_PLAINTEXT_DRAW_LINES).cur;
	
		StringBuffer buf = prepareFile(tree, docInfo);
		
		try {
			return buf.toString().getBytes(docInfo.getEncodingType());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return buf.toString().getBytes();
		}
	}

	private StringBuffer prepareFile(JoeTree tree, DocumentInfo docInfo) {
		String lineEnding = PlatformCompatibility.platformToLineEnding(docInfo.getLineEnding());
		
		StringBuffer buf = new StringBuffer();

		Node node = tree.getRootNode();
		for (int i = 0; i < node.numOfChildren(); i++) {
			buildOutlineElement(node.getChild(i), lineEnding, buf);
		}

		return buf;
	}

	private void buildOutlineElement(Node node, String lineEnding, StringBuffer buf) {
		splitNode(node, lineEnding, buf);
		
		if (!node.isLeaf()) {
			for (int i = 0; i < node.numOfChildren(); i++) {
				buildOutlineElement(node.getChild(i), lineEnding, buf);
			}	
		}
	}
	
	private void splitNode(Node node, String lineEnding, StringBuffer buf) {
		int spaceCount = -1;
		if (DRAW_LINES) {
			spaceCount = (node.getDepth() + 1) * INDENT;
		} else {
			spaceCount = node.getDepth() * INDENT;
		}
		
		int textCount = COLS - spaceCount;
		
		// Catch situation where indenting exceeds the available columns
		if (textCount <= 0) {
			buf.append("### Maximum depth exceeded, line lost. ###").append(lineEnding);
			return;
		}
		
		ArrayList lines = split(textCount, node.getValue());
		
		for (int i = 0; i < lines.size(); i++) {
			//indent(spaceCount, buf);
			if (i == 0) {
				if (DRAW_LINES) {
					indentHeirarchy(node, buf, FIRST_LINE);
				} else {
					indent(spaceCount, buf);
				}
			} else {
				if (DRAW_LINES) {
					indentHeirarchy(node, buf, MIDDLE_LINE);
				} else {
					indent(spaceCount, buf);
				}
			}
			buf.append((String) lines.get(i)).append(lineEnding);
		}

		if (DRAW_LINES) {
			indentHeirarchy(node, buf, AFTER_LINE);
		} else {
			indent(spaceCount, buf);
		}
		buf.append(lineEnding);
	}
	
	private ArrayList split(int textCount, String text) {
		ArrayList strings = new ArrayList();
		
		BreakIterator it = BreakIterator.getLineInstance();
		it.setText(text);

		int prevBreak = -1;
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			if (it.isBoundary(i)) {
				prevBreak = i;
			}
		
			if ((buf.length() == textCount) && (i != 0)) {
				if (prevBreak == -1) {
					strings.add(buf.toString());
					buf.setLength(0);
				} else {
					buf.setLength(textCount - (i - prevBreak));
					strings.add(buf.toString());
					buf.setLength(0);
					i = prevBreak;
					prevBreak = -1;
				}
			}
			
			buf.append(text.charAt(i));
		}
		
		strings.add(buf.toString());
		
		return strings;
	}
	
	private void indent(int spaceCount, StringBuffer buf) {
		for (int i = 0; i < spaceCount; i++) {
			buf.append(" ");
		}
	}

	private static final String LEAF = "+-";
	private static final String BRANCH = "| ";
	private static final String EMPTY = "  ";
	
	private static final int FIRST_LINE = 0;
	private static final int MIDDLE_LINE = 1;
	private static final int AFTER_LINE = 2;
	
	private void indentHeirarchy(Node node, StringBuffer buf, int lineType) {
		Node parent = node.getParent();

		int offset = buf.length();
		
		if (parent != null) {
			if (lineType == FIRST_LINE) {
				buf.append(LEAF);
				
			} else if (lineType == MIDDLE_LINE) {
				if(node.isLastChild()) {
					buf.append(EMPTY);
				} else {
					buf.append(BRANCH);
				}
				
			} else {
				if(node.isLastChild()) {
					buf.append(EMPTY);
				} else {
					buf.append(BRANCH);
				}
				
				if (node.isLeaf()) {
					buf.append(EMPTY);
				} else {
					buf.append(BRANCH);
				}			
			}
			
			//parent = parent.getParent();
		}
				
		while (!parent.isRoot()) {
			if(parent.isLastChild()) {
				buf.insert(offset, EMPTY);
			} else {
				buf.insert(offset, BRANCH);
			}
			
			parent = parent.getParent();
		}
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