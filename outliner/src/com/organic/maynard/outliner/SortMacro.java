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
		// Shorthand
		OutlinerDocument document = Outliner.getMostRecentDocumentTouched();
		TreeContext tree = document.tree;

		// Abort if nodes aren't selected.
		if (tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
			return;
		}

		// Instantiate the Undoable
		Node parent = tree.getEditingNode().getParent();
		CompoundUndoableMove undoable = new CompoundUndoableMove(parent, parent);
		
		// Create the Comparator using BSH
		BSHComparator comparator = new BSHComparator(this);

		/*Comparator comparator = null;

		try {
			Interpreter bsh = new Interpreter();
			NameSpace nameSpace = new NameSpace("outliner");
			nameSpace.importPackage("com.organic.maynard.outliner");
			
			bsh.setNameSpace(nameSpace);
			//bsh.set("comparator", comparator);
			comparator = (Comparator) bsh.eval(getComparator());
		} catch (Exception e) {
			System.out.println("BSH Exception: " + e.getMessage());
			JOptionPane.showMessageDialog(document, "BSH Exception: " + e.getMessage());
			return;
		}*/
		
		// Store the Before State
		//Object[] nodes = tree.selectedNodes.toArray();
		Object[] sortedNodes = tree.selectedNodes.toArray();
		int[] indeces = new int[tree.selectedNodes.size()];

		for (int i = 0; i < sortedNodes.length; i++) {
			Node node = (Node) sortedNodes[i];
			indeces[i] = node.currentIndex();
		}
		
		// Sort them
		Arrays.sort(sortedNodes, comparator);
		
		tree.clearSelection();
		
		// Add the primitives to the undoable
		for (int i = sortedNodes.length - 1; i >= 0; i--) {
			Node sortedNode = (Node) sortedNodes[i];
			int oldIndex = sortedNode.currentIndex();
			int newIndex = indeces[i];
			PrimitiveUndoableMove primitive = new PrimitiveUndoableMove(undoable, sortedNode, oldIndex, newIndex);
			
			undoable.addPrimitive(primitive);
			
			tree.addNodeToSelection(sortedNode);
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