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

public class CompoundUndoableEdit extends AbstractCompoundUndoable {

	private TreeContext tree = null;
	
	// The Constructors
	public CompoundUndoableEdit(TreeContext tree) {
		this(true, tree);
	}

	public CompoundUndoableEdit(boolean isUpdatingGui, TreeContext tree) {
		super(isUpdatingGui);
		this.tree = tree;
	}


	// Undoable Interface
	public void destroy() {
		super.destroy();
		tree = null;
	}
	
	public void undo() {
		for (int i = primitives.size() - 1; i >= 0; i--) {
			((PrimitiveUndoableEdit) primitives.get(i)).undo();
		}
		tree.doc.panel.layout.draw();	
	}
	
	public void redo() {
		int size = primitives.size(); // In theory it should be faster to do this out here so we don't check the size for each loop.
		for (int i = 0; i < size; i++) {
			((PrimitiveUndoableEdit) primitives.get(i)).redo();
		}
		tree.doc.panel.layout.draw();	
	}
}