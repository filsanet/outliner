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
import bsh.Interpreter;
import bsh.NameSpace;
import java.util.*;
import javax.swing.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class SortMacro extends MacroImpl implements RawMacro {

	// Constants
	private static final String E_COMPARATOR = "comparator";
	
	// These modes are intended to be added together so should go 1,2,4,8,...
	// Don't forget that 0 is basically the default for each independant property.
	public static final int MODE_SHALLOW = 0;
	public static final int MODE_DEEP = 1;

	public static final int MODE_ASCENDING = 0;
	public static final int MODE_DECENDING = 2;
	
	// Instance Fields
	private String comparator = ""; // BSH will turn this into a Comparator

	// Class Fields
	private static SortMacroConfig macroConfig = new SortMacroConfig();

	
	// The Constructors
	public SortMacro() {
		this("");
	}

	public SortMacro(String name) {
		super(name, true, Macro.RAW_MACRO_UNDOABLE);
	}


	// Accessors
	public String getComparator() {return comparator;}
	public void setComparator(String comparator) {this.comparator = comparator;}


	// Macro Interface	
	public MacroConfig getConfigurator() {return this.macroConfig;}
	public void setConfigurator(MacroConfig macroConfig) {}

	public NodeRangePair process(NodeRangePair nodeRangePair) {
		// This method should never get called.
		return nodeRangePair;
	}
	
	public void process() {
		// not supported
	}
	
	public void process(int mode) {
		// Shorthand
		OutlinerDocument document = Outliner.getMostRecentDocumentTouched();
		TreeContext tree = document.tree;

		// Abort if nodes aren't selected.
		if (tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
			return;
		}

		// Instantiate the Undoable
		CompoundUndoableImpl undoable = new CompoundUndoableImpl(true);

		// Create the Comparator using BSH
		Comparator comparator = null; 

		try {
			Interpreter bsh = new Interpreter();
			NameSpace nameSpace = new NameSpace("outliner");
			nameSpace.importPackage("com.organic.maynard.outliner");
			bsh.setNameSpace(nameSpace);

			StringBuffer textForEval = new StringBuffer();
			textForEval.append("Comparator c = new Comparator() {");
			textForEval.append("	int compare(Object objA, Object objB) {");
			textForEval.append("		Node nodeA = (Node) objA;");
			textForEval.append("		Node nodeB = (Node) objB;");
			textForEval.append(getComparator());
			if ((mode & MODE_DECENDING) == MODE_DECENDING) {
				textForEval.append("		return -retVal;"); // this can be changed to make ascending/decending.	
			} else {
				textForEval.append("		return retVal;"); // this can be changed to make ascending/decending.	
			}
			textForEval.append("	}");
			textForEval.append("};");
			textForEval.append("return this.c;");

			comparator = (Comparator) bsh.eval(textForEval.toString());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(Outliner.outliner, "BSH Exception: " + e.getMessage());
			return;
		}

		ArrayList parentNodes = new ArrayList();
		parentNodes.add(tree.getEditingNode().getParent());
		
		for (int i = 0; i < parentNodes.size(); i++) {
			// Instantiate the Undoable
			Node parent = (Node) parentNodes.get(i);
			CompoundUndoableMove undoableMove;
			if (i == 0) {
				undoableMove = new CompoundUndoableMove(true, parent, parent);
			} else {
				undoableMove = new CompoundUndoableMove(false, parent, parent);
			}

			// Store the Before State
			Object[] sortedNodes;
			int[] indeces;

			if (i == 0) {
				sortedNodes = tree.selectedNodes.toArray();
				indeces = new int[tree.selectedNodes.size()];
			} else {
				sortedNodes = ((NodeImpl) parent).children.toArray();
				indeces = new int[parent.numOfChildren()];
			}
	
			for (int j = 0; j < sortedNodes.length; j++) {
				Node node = (Node) sortedNodes[j];
				indeces[j] = node.currentIndex();
				
				// If the node has children then throw it on the list of things to sort
				if (!node.isLeaf() && (mode & MODE_DEEP) == MODE_DEEP) {
					parentNodes.add(node);
				}
			}
			
			// Sort them
			try {
				Arrays.sort(sortedNodes, comparator);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(Outliner.outliner, "Exception: " + e.getMessage());
				return;
			}
			
			if (i == 0) { // Only do this for the top level nodeset
				tree.clearSelection();
			}
			
			// Add the primitives to the undoable
			for (int j = sortedNodes.length - 1; j >= 0; j--) {
				Node sortedNode = (Node) sortedNodes[j];
				int oldIndex = sortedNode.currentIndex();
				int newIndex = indeces[j];
				PrimitiveUndoableMove primitive = new PrimitiveUndoableMove(undoableMove, sortedNode, oldIndex, newIndex);
				
				undoableMove.addPrimitive(primitive);

				if (i == 0) { // Only do this for the top level nodeset
					tree.addNodeToSelection(sortedNode);
				}
			}
			
			undoable.addPrimitive(undoableMove);
		}
		
		// Put the undoable onto the queue
		if (isUndoable()) {
			if (!undoable.isEmpty()) {
				document.undoQueue.add(undoable);
				undoable.redo();
			}
		} else {
			document.undoQueue.clear();
		}	
	}

	
	// Saving the Macro
	protected void prepareFile (StringBuffer buf) {
		buf.append(XMLTools.getXmlDeclaration(null) + "\n");
		buf.append(XMLTools.getElementStart(E_COMPARATOR) + XMLTools.escapeXMLText(getComparator()) + XMLTools.getElementEnd(E_COMPARATOR)+ "\n");
	}


	// Sax DocumentHandler Implementation
	protected void handleCharacters(String elementName, String text) {
		if (elementName.equals(E_COMPARATOR)) {
			setComparator(text);
		}
	}
}