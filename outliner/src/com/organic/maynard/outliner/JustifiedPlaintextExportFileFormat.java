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

import javax.swing.*;
import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.StringTools;
import java.text.*;

public class JustifiedPlaintextExportFileFormat implements ExportFileFormat, JoeReturnCodes {

	// Constants
	private static final int COLS = 80;
	private static final int INDENT = 2;
	
	// Constructors
	public JustifiedPlaintextExportFileFormat() {}

	
	// ExportFileFormat Interface
	public boolean supportsComments() {return false;}
	public boolean supportsEditability() {return false;}
	public boolean supportsMoveability() {return false;}
	public boolean supportsAttributes() {return false;}
	public boolean supportsDocumentAttributes() {return false;}
	
	public byte[] save(TreeContext tree, DocumentInfo docInfo) {
		StringBuffer buf = prepareFile(tree, docInfo);
		
		try {
			return buf.toString().getBytes(docInfo.getEncodingType());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return buf.toString().getBytes();
		}
	}

	private StringBuffer prepareFile(TreeContext tree, DocumentInfo docInfo) {
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
		int spaceCount = (node.getDepth() + 1) * INDENT;
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
				indentHeirarchy(node, buf, FIRST_LINE);
			} else {
				indentHeirarchy(node, buf, MIDDLE_LINE);
			}
			buf.append((String) lines.get(i)).append(lineEnding);
		}
		
		indentHeirarchy(node, buf, AFTER_LINE);
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