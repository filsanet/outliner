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

public class PrimitiveUndoableReplace implements Undoable {

	private Node parent = null;
	private Node oldNode = null;
	private Node newNode = null;
	
	private int index = -1;
	
	// The Constructors
	public PrimitiveUndoableReplace(Node parent, Node oldNode, Node newNode) {
		this.parent = parent;
		this.oldNode = oldNode;
		this.newNode = newNode;
		this.index = oldNode.currentIndex();
	}

	// Accessors
	public Node getOldNode() {return oldNode;}
	public Node getNewNode() {return newNode;}
			
	public void undo() {
		TreeContext tree = parent.getTree();
		
		if (newNode != null) {
			// Remove node from visible nodes cache
			tree.removeNode(newNode);
			
			// Swap the nodes
			parent.removeChild(newNode);
			parent.insertChild(oldNode,index);
			
			// Insert the node into the visible nodes cache
			tree.insertNode(oldNode);
			
			// Handle Selection
			tree.addNodeToSelection(oldNode);
	
			// Handle updating the Node to Draw From
			outlineLayoutManager layout = tree.doc.panel.layout;
			if (layout.getNodeToDrawFrom() == newNode) {
				layout.setNodeToDrawFrom(oldNode, tree.visibleNodes.indexOf(oldNode));
			}		
		} else {
			// Swap the nodes
			parent.insertChild(oldNode,index);
			
			// Insert the node into the visible nodes cache
			tree.insertNode(oldNode);
			
			// Handle Selection
			tree.addNodeToSelection(oldNode);		
		}
	}
	
	// Undoable Interface
	public void redo() {
		TreeContext tree = parent.getTree();

		if (newNode != null) {
			// Remove node from visible nodes cache
			tree.removeNode(oldNode);
			
			// Swap the nodes
			parent.removeChild(oldNode);
			parent.insertChild(newNode,index);
			
			// Insert the node into the visible nodes cache
			tree.insertNode(newNode);
	
			// Handle Selection
			tree.addNodeToSelection(newNode);
	
			// Handle updating the Node to Draw From
			outlineLayoutManager layout = tree.doc.panel.layout;
			if (layout.getNodeToDrawFrom().isDecendantOf(oldNode)) {
				layout.setNodeToDrawFrom(newNode, tree.visibleNodes.indexOf(newNode));
			}
		} else {
			// Store a node to draw from just in case
			Node nodeToDrawFrom = getNodeToDrawFrom();
			
			// Remove node from visible nodes cache
			tree.removeNode(oldNode);
			
			// Swap the nodes
			parent.removeChild(oldNode);
	
			// Handle updating the Node to Draw From
			outlineLayoutManager layout = tree.doc.panel.layout;
			if (layout.getNodeToDrawFrom().isDecendantOf(oldNode)) {
				layout.setNodeToDrawFrom(nodeToDrawFrom, tree.visibleNodes.indexOf(nodeToDrawFrom));
			}		
		}
	}
	
	public Node getNodeToDrawFrom() {
		Node nodeToDrawFrom = oldNode.prev();
		return nodeToDrawFrom;
	}
	
	public int getType() {return Undoable.PRIMITIVE_REPLACE_TYPE;}
}