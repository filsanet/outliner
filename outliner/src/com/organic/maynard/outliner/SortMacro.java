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
	
	private static final int MODE_SHALLOW = 1;
	private static final int MODE_DEEP = 2;
	
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
	
	public void processShallow() {
		process(MODE_SHALLOW);
	}
	
	public void processDeep() {
		process(MODE_DEEP);
	}
	
	private void process(int mode) {
		// Shorthand
		OutlinerDocument document = Outliner.getMostRecentDocumentTouched();
		TreeContext tree = document.tree;

		// Abort if nodes aren't selected.
		if (tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
			return;
		}

		// Instantiate the Undoable
		CompoundUndoableImpl undoable = new CompoundUndoableImpl();

		// Create the Comparator using BSH
		BSHComparator comparator = new BSHComparator(this);

		ArrayList parentNodes = new ArrayList();
		parentNodes.add(tree.getEditingNode().getParent());
		
		for (int i = 0; i < parentNodes.size(); i++) {
			// Instantiate the Undoable
			Node parent = (Node) parentNodes.get(i);
			CompoundUndoableMove undoableMove = new CompoundUndoableMove(parent, parent);

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
				if (!node.isLeaf() && mode == MODE_DEEP) {
					parentNodes.add(node);
				}
			}
			
			// Sort them
			Arrays.sort(sortedNodes, comparator);
			
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

// This is not ideal, we should be able to implement the Comparator in BSH and only have to eval once, but for now we
// have this.
class BSHComparator implements Comparator {
	private SortMacro macro = null;
	
	public BSHComparator(SortMacro macro) {
		this.macro = macro;
	}
	
	public int compare(Object objA, Object objB) throws ClassCastException {
		try {
			Node nodeA = (Node) objA;
			Node nodeB = (Node) objB;
			
			Interpreter bsh = new Interpreter();
			NameSpace nameSpace = new NameSpace("outliner");
			nameSpace.importPackage("com.organic.maynard.outliner");
			bsh.setNameSpace(nameSpace);
			bsh.set("nodeA", nodeA);
			bsh.set("nodeB", nodeB);
			return ((Integer) bsh.eval(macro.getComparator())).intValue();
		} catch (Exception e) {
			System.out.println("BSH Exception: " + e.getMessage());
			//JOptionPane.showMessageDialog(Outliner.outliner, "BSH Exception: " + e.getMessage());
			return 0;
		}
	}
}