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

public class PrimitiveUndoableCommentChange implements Undoable, PrimitiveUndoablePropertyChange {

	private Node node = null;
	private boolean oldState = false;
	private boolean newState = false;
	
	
	// The Constructors
	public PrimitiveUndoableCommentChange(Node node, boolean oldState, boolean newState) {
		this.node = node;
		this.oldState = oldState;
		this.newState = newState;
	}

	public void destroy() {
		node = null;
	}
	
	// Undoable Interface
	public void undo() {
		node.setComment(oldState);
	}
	
	public void redo() {
		node.setComment(newState);
	}
	
	public int getType() {return Undoable.PRIMITIVE_COMMENT_PROPERTY_CHANGE_TYPE;}
}