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

public class CompoundUndoableEdit implements Undoable {

	private Vector primitives = new Vector();
	private TreeContext tree = null;
	
	// The Constructors
	public CompoundUndoableEdit(TreeContext tree) {
		this.tree = tree;
	}
	
	// Accessors
	public void addPrimitive(PrimitiveUndoableEdit primitive) {
		primitives.addElement(primitive);
	}
	
	// Undoable Interface
	public void undo() {
		for (int i = primitives.size() - 1; i >= 0; i--) {
			((PrimitiveUndoableEdit) primitives.elementAt(i)).undo();
		}
		tree.doc.panel.layout.draw();	
	}
	
	public void redo() {
		for (int i = 0; i < primitives.size(); i++) {
			((PrimitiveUndoableEdit) primitives.elementAt(i)).redo();
		}
		tree.doc.panel.layout.draw();	
	}
	
	public int getType() {return Undoable.COMPOUND_EDIT_TYPE;}
}