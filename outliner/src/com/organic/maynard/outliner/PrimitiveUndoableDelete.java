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

public class PrimitiveUndoableDelete implements Undoable {

	private Node parent = null;
	private Node node = null;
	private int index = 0;
	
	// The Constructors
	public PrimitiveUndoableDelete(Node parent, Node node, int index) {
		this.parent = parent;
		this.node = node;
		this.index = index;
	}

	public void destroy() {
		parent = null;
		node = null;
	}
	
	// Accessors
	public void setNode(Node node) {this.node = node;}
	public Node getNode() {return this.node;}
	
	// Undoable Interface
	public void undo() {
		// Insert the Node
		parent.insertChild(node,index);
		node.getTree().insertNode(node);
		node.getTree().addNodeToSelection(node);
	}
	
	public void redo() {
		// Remove the Node
		node.getTree().removeNode(node);
		parent.removeChild(node);
	}
	
	public int getType() {return Undoable.PRIMITIVE_DELETE_TYPE;}
}