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

import java.util.*;
import java.awt.*;

public class CompoundUndoableReplace implements Undoable {

	private Vector primitives = new Vector();
	public Node parent = null;
	
	// The Constructors
	public CompoundUndoableReplace(Node parent) {
		this.parent = parent;
	}
	
	// Accessors
	public void addPrimitive(PrimitiveUndoableReplace primitive) {
		primitives.addElement(primitive);
	}
	
	// Undoable Interface
	public void undo() {		
		// Shorthand
		TreeContext tree = parent.getTree();
		outlineLayoutManager layout = tree.doc.panel.layout;

		tree.setSelectedNodesParent(parent);
		
		// Replace Everything
		for (int i = 0; i < primitives.size(); i++) {
			((PrimitiveUndoableReplace) primitives.elementAt(i)).undo();
		}

		// Record the EditingNode
		Node newSelectedNode = ((PrimitiveUndoableReplace) primitives.lastElement()).getOldNode();
		
		tree.setEditingNode(newSelectedNode);
		tree.setCursorPosition(0);
		tree.setCursorMarkPosition(0);
		tree.setComponentFocus(outlineLayoutManager.ICON);
		layout.draw(newSelectedNode,outlineLayoutManager.ICON);
	}
	
	public void redo() {
		// Shorthand
		TreeContext tree = parent.getTree();
		outlineLayoutManager layout = tree.doc.panel.layout;

		tree.setSelectedNodesParent(parent);

		// Find fallback node for drawing and editing
		Node fallbackNode = ((PrimitiveUndoableReplace) primitives.firstElement()).getOldNode().prev();
		
		// Replace Everything
		for (int i = primitives.size() - 1; i >= 0; i--) {
			((PrimitiveUndoableReplace) primitives.elementAt(i)).redo();
		}

		// Select a sibling if it exists, rather than the parent
		if ((fallbackNode == parent) && (parent.numOfChildren() > 0)) {
			fallbackNode = parent.getFirstChild();
		}
		
		if (fallbackNode.isRoot()) {
			fallbackNode = tree.rootNode.getFirstChild();
		}

		// Record the EditingNode
		Node newSelectedNode = ((PrimitiveUndoableReplace) primitives.firstElement()).getNewNode();
		if (newSelectedNode == null) {
			newSelectedNode = fallbackNode;
			tree.addNodeToSelection(newSelectedNode);
		}
		
		// Deal with selection if we ended up changing focus to a node deeper in the tree.
		if (newSelectedNode.getParent() != tree.getSelectedNodesParent()) {
			tree.setSelectedNodesParent(newSelectedNode.getParent());
			tree.addNodeToSelection(newSelectedNode);
		}
		
		tree.setEditingNode(newSelectedNode);
		tree.setCursorPosition(0);
		tree.setCursorMarkPosition(0);
		tree.setComponentFocus(outlineLayoutManager.ICON);
		layout.draw(newSelectedNode,outlineLayoutManager.ICON);
	}
	
	public int getType() {return Undoable.COMPOUND_REPLACE_TYPE;}
}