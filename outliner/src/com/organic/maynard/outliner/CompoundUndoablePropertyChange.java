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

public class CompoundUndoablePropertyChange implements Undoable {

	private Vector primitives = new Vector(1,25);
	private TreeContext tree = null;
	
	// The Constructors
	public CompoundUndoablePropertyChange(TreeContext tree) {
		this.tree = tree;
	}

	public void destroy() {
		for (int i = 0; i < primitives.size(); i++) {
			((Undoable) primitives.get(i)).destroy();
		}

		primitives = null;
		tree = null;
	}
	
	// Accessors
	public void addPrimitive(PrimitiveUndoablePropertyChange primitive) {
		primitives.addElement(primitive);
	}
	
	public boolean isEmpty() {
		if (primitives.size() > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	// Undoable Interface
	public void undo() {
		for (int i = primitives.size() - 1; i >= 0; i--) {
			((Undoable) primitives.elementAt(i)).undo();
		}
		tree.doc.panel.layout.draw();	
	}
	
	public void redo() {
		for (int i = 0; i < primitives.size(); i++) {
			((Undoable) primitives.elementAt(i)).redo();
		}
		tree.doc.panel.layout.draw();	
	}
	
	public int getType() {return Undoable.COMPOUND_PROPERTY_CHANGE_TYPE;}
}