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

public class PrimitiveUndoableAttributeChange implements Undoable, PrimitiveUndoablePropertyChange {

	private Node node = null;
	private String oldKey = null;
	private Object oldValue = null;
	private String newKey = null;
	private Object newValue = null;
	
	
	// The Constructors
	public PrimitiveUndoableAttributeChange(Node node, String oldKey, Object oldValue, String newKey, Object newValue) {
		this.node = node;
		this.oldKey = oldKey;
		this.oldValue = oldValue;
		this.newKey = newKey;
		this.newValue = newValue;
	}

	public void destroy() {
		node = null;
		oldValue = null;
		newValue = null;
	}


	// PrimitiveUndoablePropertyChangeInterface
	public Node getNode() {return node;}
	
		
	// Undoable Interface
	public void undo() {
		node.setAttribute(oldKey, oldValue);
		if (!oldKey.equals(newKey)) {
			node.removeAttribute(newKey);
		}
	}
	
	public void redo() {
		node.setAttribute(newKey, newValue);
		if (!oldKey.equals(newKey)) {
			node.removeAttribute(oldKey);
		}
	}
	
	public int getType() {return Undoable.PRIMITIVE_ATTRIBUTE_PROPERTY_CHANGE_TYPE;}
}