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

public class CompoundUndoableReplace extends AbstractCompoundUndoable {

	private Node parent = null;
	
	// The Constructors
	public CompoundUndoableReplace(Node parent) {
		super();
		this.parent = parent;
	}


	// Accessors
	public Node getParent() {
		return this.parent;
	}


	// Undoable Interface
	public void destroy() {
		super.destroy();
		parent = null;
	}
	
	public void undo() {		
		// Shorthand
		TreeContext tree = parent.getTree();
		outlineLayoutManager layout = tree.doc.panel.layout;

		tree.setSelectedNodesParent(parent);
		
		// Replace Everything
		for (int i = 0; i < primitives.size(); i++) {
			((PrimitiveUndoableReplace) primitives.elementAt(i)).undo();
		}

		// Find fallback node for drawing and editing
		if (tree.selectedNodes.size() <= 0) {
			Node fallbackNode = ((PrimitiveUndoableReplace) primitives.lastElement()).getNewNode();
			if (fallbackNode != null) {
				fallbackNode = fallbackNode.next();
				if (fallbackNode.isRoot()) {
					fallbackNode = fallbackNode.prevUnSelectedNode();
					if (fallbackNode.isRoot()) {
						tree.setSelectedNodesParent(tree.getRootNode());
					} else {
						layout.setNodeToDrawFrom(fallbackNode, tree.visibleNodes.indexOf(fallbackNode));
						tree.setSelectedNodesParent(fallbackNode.getParent());
						tree.addNodeToSelection(fallbackNode);
					}
				} else {
					tree.setSelectedNodesParent(fallbackNode.getParent());
					tree.addNodeToSelection(fallbackNode);
				}
			}
		}
		
		// Record the EditingNode
		Node firstNewSelectedNode = tree.getYoungestInSelection();
		int ioFirstNewSelectedNode = tree.visibleNodes.indexOf(firstNewSelectedNode);
		Node lastNewSelectedNode = tree.getOldestInSelection();
		int ioLastNewSelectedNode = tree.visibleNodes.indexOf(lastNewSelectedNode);

		Node newSelectedNode = null;
		if (ioFirstNewSelectedNode == 0) {
			layout.setNodeToDrawFrom(firstNewSelectedNode, ioFirstNewSelectedNode);
			newSelectedNode = firstNewSelectedNode;
		} else if (ioLastNewSelectedNode == (tree.visibleNodes.size() - 1)) {
			layout.setNodeToDrawFrom(lastNewSelectedNode, ioLastNewSelectedNode);
			newSelectedNode = lastNewSelectedNode;
		} else {
			newSelectedNode = firstNewSelectedNode;
		}
		
		tree.setEditingNode(newSelectedNode);
		tree.setCursorPosition(0);
		tree.setCursorMarkPosition(0);
		tree.setComponentFocus(outlineLayoutManager.ICON);
		
		layout.draw(newSelectedNode, outlineLayoutManager.ICON);
	}
	
	public void redo() {
		// Shorthand
		TreeContext tree = parent.getTree();
		outlineLayoutManager layout = tree.doc.panel.layout;

		// Find fallback node for drawing and editing
		boolean allWillBeDeleted = false;
		Node fallbackNode = ((PrimitiveUndoableReplace) primitives.firstElement()).getOldNode().prev();
		if (fallbackNode.isRoot()) {
			fallbackNode = ((PrimitiveUndoableReplace) primitives.firstElement()).getOldNode().nextUnSelectedNode();
			if (fallbackNode.isRoot()) {
				System.out.println("B");
				allWillBeDeleted = true;
			}
		}
		
		tree.setSelectedNodesParent(parent);
		
		// Replace Everything
		for (int i = primitives.size() - 1; i >= 0; i--) {
			((PrimitiveUndoableReplace) primitives.elementAt(i)).redo();
		}
		
		if (tree.selectedNodes.size() <= 0) {
			// Find fallback node for drawing and editing
			if (fallbackNode.isRoot()) {
				if (allWillBeDeleted) {
					tree.setSelectedNodesParent(tree.getRootNode());
				} else {
					layout.setNodeToDrawFrom(fallbackNode, tree.visibleNodes.indexOf(fallbackNode));
					tree.setSelectedNodesParent(fallbackNode.getParent());
					tree.addNodeToSelection(fallbackNode);
				}
			} else {
				tree.setSelectedNodesParent(fallbackNode.getParent());
				tree.addNodeToSelection(fallbackNode);
			}
		}

		Node firstNewSelectedNode = tree.getYoungestInSelection();
		int ioFirstNewSelectedNode = tree.visibleNodes.indexOf(firstNewSelectedNode);
		Node lastNewSelectedNode = tree.getOldestInSelection();
		int ioLastNewSelectedNode = tree.visibleNodes.indexOf(lastNewSelectedNode);

		Node newSelectedNode = null;
		if (ioFirstNewSelectedNode == 0) {
			layout.setNodeToDrawFrom(firstNewSelectedNode, ioFirstNewSelectedNode);
			newSelectedNode = firstNewSelectedNode;
		} else if (ioLastNewSelectedNode == (tree.visibleNodes.size() - 1)) {
			newSelectedNode = lastNewSelectedNode;
		} else {
			newSelectedNode = firstNewSelectedNode;
		}

		//System.out.println("node: " + newSelectedNode.getValue());
		tree.setEditingNode(newSelectedNode);
		tree.setCursorPosition(0);
		tree.setCursorMarkPosition(0);
		tree.setComponentFocus(outlineLayoutManager.ICON);

		layout.draw(newSelectedNode, outlineLayoutManager.ICON);
	}
	
	public int getType() {return Undoable.COMPOUND_REPLACE_TYPE;}
}