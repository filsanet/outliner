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

public class PrimitiveUndoableMove implements Undoable {

	private CompoundUndoableMove undoable = null;
	
	private Node node = null;
	private int index = 0;
	private int targetIndex = 0;
	
	
	// The Constructors
	public PrimitiveUndoableMove(CompoundUndoableMove undoable, Node node, int index, int targetIndex) {
		this.undoable = undoable;
		this.node = node;
		this.index = index;
		this.targetIndex = targetIndex;
	}

	public void destroy() {
		undoable = null;
		node = null;
	}
	
	// Accessors
	public void setNode(Node node) {this.node = node;}
	public Node getNode() {return this.node;}

	public int getIndex() {return this.index;}
	public int getTargetIndex() {return this.targetIndex;}
	
	public void undo() {
		// Remove the Node
		node.getTree().removeNode(node);
		undoable.getTargetParent().removeChild(node);

		// Insert the Node
		undoable.getParent().insertChild(node,index);
		node.getTree().insertNode(node);
		
		// Set depth if neccessary.
		if (undoable.getTargetParent().getDepth() != undoable.getParent().getDepth()) {
			node.setDepthRecursively(undoable.getParent().getDepth() + 1);
		}
		
		// Update selection
		node.getTree().addNodeToSelection(node);
	}
	
	// Undoable Interface
	public void redo() {
		// Remove the Node
		node.getTree().removeNode(node);
		undoable.getParent().removeChild(node);

		// Insert the Node
		undoable.getTargetParent().insertChild(node,targetIndex);
		node.getTree().insertNode(node);

		// Set depth if neccessary.
		if (undoable.getTargetParent().getDepth() != undoable.getParent().getDepth()) {
			node.setDepthRecursively(undoable.getTargetParent().getDepth() + 1);
		}
		
		// Update selection
		node.getTree().addNodeToSelection(node);
	}
}