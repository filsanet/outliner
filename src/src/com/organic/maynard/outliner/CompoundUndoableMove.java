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

public class CompoundUndoableMove implements Undoable {

	private Vector primitives = new Vector();
	public Node parent = null;
	public Node targetParent = null;
	
	// The Constructors
	public CompoundUndoableMove(Node parent, Node targetParent) {
		this.parent = parent;
		this.targetParent = targetParent;
	}
	
	// Accessors
	public void addPrimitive(PrimitiveUndoableMove primitive) {
		primitives.addElement(primitive);
	}
	
	// Undoable Interface
	public void undo() {
		Node youngestNode = ((PrimitiveUndoableMove) primitives.elementAt(0)).getNode();
		TreeContext tree = youngestNode.getTree();

		// Store nodeToDrawFrom if neccessary. Used when the selection is dissconnected.
		outlineLayoutManager layout = tree.doc.panel.layout;
		Node nodeToDrawFromTmp = layout.getNodeToDrawFrom().nextUnSelectedNode();

		// Do all the Inserts
		tree.setSelectedNodesParent(parent);

		for (int i = primitives.size() - 1; i >= 0; i--) {
			((PrimitiveUndoableMove) primitives.elementAt(i)).undo();
		}

		// Record the EditingNode
		tree.setEditingNode(youngestNode);
		tree.setComponentFocus(outlineLayoutManager.ICON);

		// Redraw and Set Focus
		if (layout.getNodeToDrawFrom().isAncestorSelected()) { // Makes sure we dont' stick at the top when multiple nodes are selected.
			Node visNode = layout.getNodeToDrawFrom().prev();
			int ioVisNode = tree.visibleNodes.indexOf(visNode);
			int ioNodeToDrawFromTmp = tree.visibleNodes.indexOf(nodeToDrawFromTmp);
			if (ioVisNode < ioNodeToDrawFromTmp) {
				layout.setNodeToDrawFrom(visNode, ioVisNode);
			} else {
				layout.setNodeToDrawFrom(nodeToDrawFromTmp, ioNodeToDrawFromTmp);
			}
		}
		
		layout.draw(tree.getYoungestInSelection(), outlineLayoutManager.ICON);
	}
	
	public void redo() {
		Node youngestNode = ((PrimitiveUndoableMove) primitives.elementAt(0)).getNode();
		TreeContext tree = youngestNode.getTree();

		// Store nodeToDrawFrom if neccessary. Used when the selection is dissconnected.
		outlineLayoutManager layout = tree.doc.panel.layout;
		Node nodeToDrawFromTmp = layout.getNodeToDrawFrom().nextUnSelectedNode();

		// Do all the Inserts
		tree.setSelectedNodesParent(targetParent);
		
		for (int i = 0; i < primitives.size(); i++) {
			((PrimitiveUndoableMove) primitives.elementAt(i)).redo();
		}

		// Record the EditingNode
		tree.setEditingNode(youngestNode);
		tree.setComponentFocus(outlineLayoutManager.ICON);

		// Redraw and Set Focus
		if (layout.getNodeToDrawFrom().isAncestorSelected()) { // Makes sure we dont' stick at the top when multiple nodes are selected.
			Node visNode = layout.getNodeToDrawFrom().prev();
			int ioVisNode = tree.visibleNodes.indexOf(visNode);
			int ioNodeToDrawFromTmp = tree.visibleNodes.indexOf(nodeToDrawFromTmp);
			if (ioVisNode < ioNodeToDrawFromTmp) {
				layout.setNodeToDrawFrom(visNode, ioVisNode);
			} else {
				layout.setNodeToDrawFrom(nodeToDrawFromTmp, ioNodeToDrawFromTmp);
			}
		}
		
		layout.draw(tree.getYoungestInSelection(), outlineLayoutManager.ICON);
	}
	
	public int getType() {return Undoable.COMPOUND_MOVE_TYPE;}
}