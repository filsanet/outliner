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

public abstract class AbstractCompoundUndoable implements CompoundUndoable {

	protected ArrayList primitives = new ArrayList(5);
	
	// The Constructors
	public AbstractCompoundUndoable() {}
	
	// Accessors
	public void addPrimitive(Undoable primitive) {
		primitives.add(primitive);
	}
	
	public boolean isEmpty() {
		if (primitives.size() > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	// Undoable Interface
	public void destroy() {
		for (int i = 0; i < primitives.size(); i++) {
			((Undoable) primitives.get(i)).destroy();
		}

		primitives = null;
	}

	public abstract void undo();
	public abstract void redo();
}