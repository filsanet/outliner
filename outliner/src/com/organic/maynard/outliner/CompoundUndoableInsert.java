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

public class CompoundUndoableInsert extends AbstractCompoundUndoable {

	private Node parent = null;
	
	// The Constructors
	public CompoundUndoableInsert(Node parent) {
		super();
		this.parent = parent;
	}
	
	// Accessors
	public Node getParent() {return parent;}
	
	
	// Undoable Interface
	public void destroy() {
		super.destroy();
		parent = null;
	}
	
	public void undo() {
		// Find the node we will change focus too, note may end up null
		Node youngestNode = ((PrimitiveUndoableInsert) primitives.get(primitives.size() - 1)).getNode();
		Node newSelectedNode = youngestNode.prev();
		
		// Shorthand
		TreeContext tree = youngestNode.getTree();
		OutlineLayoutManager layout = tree.doc.panel.layout;
		
		// Delete Everything
		for (int i = 0; i < primitives.size(); i++) {
			((PrimitiveUndoableInsert) primitives.get(i)).undo();
		}

		if ((newSelectedNode == youngestNode.getParent()) && !newSelectedNode.isLeaf()) {
			newSelectedNode = newSelectedNode.getFirstChild();
		}
		
		// If the newSelectedNode is null, then select the first node in the tree
		if (newSelectedNode == null) {
			tree.setSelectedNodesParent(tree.rootNode);
			newSelectedNode = tree.rootNode.getFirstChild();
			tree.addNodeToSelection(newSelectedNode);
		} else {
			tree.setSelectedNodesParent(newSelectedNode.getParent());
			tree.addNodeToSelection(newSelectedNode);
		}

		// Record the EditingNode
		tree.setEditingNode(newSelectedNode);
		tree.setComponentFocus(OutlineLayoutManager.ICON);

		// Redraw and Set Focus
		// First make sure the node to draw from wasn't removed, it will be root since it is orphaned. 
		// If so, we need to set the new one before trying to redraw.
		if (layout.getNodeToDrawFrom().isRoot()) {
			layout.setNodeToDrawFrom(newSelectedNode, tree.visibleNodes.indexOf(newSelectedNode));
		}
		tree.insertNode(newSelectedNode); // Just to make it visible
		layout.draw(newSelectedNode,OutlineLayoutManager.ICON);	
	}
	
	public void redo() {
		Node youngestNode = ((PrimitiveUndoableInsert) primitives.get(primitives.size() - 1)).getNode();
		TreeContext tree = youngestNode.getTree();

		// Do all the Inserts
		tree.setSelectedNodesParent(parent);
		
		for (int i = primitives.size() - 1; i >= 0; i--) {
			((PrimitiveUndoableInsert) primitives.get(i)).redo();
		}

		// Record the EditingNode
		tree.setEditingNode(youngestNode);
		tree.setComponentFocus(OutlineLayoutManager.ICON);
		
		// Redraw and Set Focus
		tree.doc.panel.layout.draw(youngestNode,OutlineLayoutManager.ICON);		
	}
}