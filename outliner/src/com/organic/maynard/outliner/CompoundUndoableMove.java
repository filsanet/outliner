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

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class CompoundUndoableMove extends AbstractCompoundUndoable {

	private Node parent = null;
	private Node targetParent = null;
	
	// The Constructors
	public CompoundUndoableMove(Node parent, Node targetParent) {
		super();
		this.parent = parent;
		this.targetParent = targetParent;
	}

		
	// Accessors
	public Node getParent() {return parent;}
	public Node getTargetParent() {return targetParent;}
	
	
	// Undoable Interface
	public void destroy() {
		super.destroy();
		parent = null;
		targetParent = null;
	}

	public void undo() {
		Node youngestNode = ((PrimitiveUndoableMove) primitives.get(0)).getNode();
		TreeContext tree = youngestNode.getTree();

		// Store nodeToDrawFrom if neccessary. Used when the selection is dissconnected.
		OutlineLayoutManager layout = tree.doc.panel.layout;
		Node nodeToDrawFromTmp = layout.getNodeToDrawFrom().nextUnSelectedNode();

		// Do all the Inserts
		tree.setSelectedNodesParent(parent);

		for (int i = primitives.size() - 1; i >= 0; i--) {
			primitiveUndo((PrimitiveUndoableMove) primitives.get(i));
		}

		// Record the EditingNode
		tree.setEditingNode(youngestNode);
		tree.setComponentFocus(OutlineLayoutManager.ICON);

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
		
		layout.draw(tree.getYoungestInSelection(), OutlineLayoutManager.ICON);
	}
	
	private void primitiveUndo(PrimitiveUndoableMove primitive) {
		// ShortHand
		Node node = primitive.getNode();
		TreeContext tree = node.getTree();
		
		// Remove the Node
		tree.removeNode(node);
		targetParent.removeChild(node);

		// Insert the Node
		parent.insertChild(node, primitive.getIndex());
		tree.insertNode(node);
		
		// Set depth if neccessary.
		if (targetParent.getDepth() != parent.getDepth()) {
			node.setDepthRecursively(parent.getDepth() + 1);
		}

		// Update selection
		tree.addNodeToSelection(node);
	}
	
	public void redo() {
		Node youngestNode = ((PrimitiveUndoableMove) primitives.get(0)).getNode();
		TreeContext tree = youngestNode.getTree();

		// Store nodeToDrawFrom if neccessary. Used when the selection is dissconnected.
		OutlineLayoutManager layout = tree.doc.panel.layout;
		Node nodeToDrawFromTmp = layout.getNodeToDrawFrom().nextUnSelectedNode();

		// Do all the Inserts
		tree.setSelectedNodesParent(targetParent);
		
		for (int i = 0; i < primitives.size(); i++) {
			primitiveRedo((PrimitiveUndoableMove) primitives.get(i));
		}

		// Record the EditingNode
		tree.setEditingNode(youngestNode);
		tree.setComponentFocus(OutlineLayoutManager.ICON);

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
		
		layout.draw(tree.getYoungestInSelection(), OutlineLayoutManager.ICON);
	}
	
	private void primitiveRedo(PrimitiveUndoableMove primitive) {
		// ShortHand
		Node node = primitive.getNode();
		TreeContext tree = node.getTree();

		// Remove the Node
		tree.removeNode(node);
		parent.removeChild(node);

		// Insert the Node
		targetParent.insertChild(node, primitive.getTargetIndex());
		tree.insertNode(node);

		// Set depth if neccessary.
		if (targetParent.getDepth() != parent.getDepth()) {
			node.setDepthRecursively(targetParent.getDepth() + 1);
		}
		
		// Update selection
		tree.addNodeToSelection(node);
	}
}