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

import com.organic.maynard.xml.XMLTools;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class EnhancedTextMacro extends MacroImpl {

	// Constants
	private static final String E_PATTERN = "pattern";
	
	// Instance Fields
	private String replacementPattern = "";

	// Class Fields
	private static BSHMacroConfig macroConfig = new BSHMacroConfig();

	
	// The Constructors
	public EnhancedTextMacro() {
		this("");
	}

	public EnhancedTextMacro(String name) {
		super(name, true, Macro.COMPLEX_UNDOABLE);
	}


	// Accessors
	public String getReplacementPattern() {return replacementPattern;}
	public void setReplacementPattern(String replacementPattern) {this.replacementPattern = replacementPattern;}


	// Macro Interface	
	public MacroConfig getConfigurator() {return this.macroConfig;}
	public void setConfigurator(MacroConfig macroConfig) {}

	public NodeRangePair process(NodeRangePair nodeRangePair) {
		Node node = nodeRangePair.node;
		
		// Create the mini tree from the replacement pattern
		if (replacementPattern.equals("")) {
			return null;
		}
		Node replacementNode = PadSelection.pad(replacementPattern, node.getTree(), node.getDepth(), PlatformCompatibility.LINE_END_UNIX).getFirstChild();
		
		// Walk the tree backwards.
		boolean walking = true;
		Node walkNode = replacementNode.getLastDecendent();
		while (walking) {
			String value = walkNode.getValue();
			if (value.equals("{$value}")) {
				Node clonedNode = node.cloneClean();
				clonedNode.setDepthRecursively(walkNode.getDepth());
				
				int index = walkNode.currentIndex();
				Node parent = walkNode.getParent();
				parent.removeChild(walkNode,index);
				parent.insertChild(clonedNode,index);
				
				walkNode = clonedNode;
			}
			
			Node prevNode = walkNode.prev();
			if (walkNode == prevNode) {
				walking = false;
			}
			walkNode = prevNode;
		}
		
		nodeRangePair.node = replacementNode;
		nodeRangePair.startIndex = -1;
		nodeRangePair.endIndex = -1;
		return nodeRangePair;
	}

	
	// Saving the Macro
	protected void prepareFile (StringBuffer buf) {
		buf.append(XMLTools.getXmlDeclaration(null) + "\n");
		buf.append(XMLTools.getElementStart(E_PATTERN) + XMLTools.escapeXMLText(getReplacementPattern()) + XMLTools.getElementEnd(E_PATTERN)+ "\n");
	}


	// Sax DocumentHandler Implementation
	protected void handleCharacters(String elementName, String text) {
		if (elementName.equals(E_PATTERN)) {
			setReplacementPattern(text);
		}
	}
}