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
import javax.swing.*;

public class UndoQueue {
	private OutlinerDocument doc = null;
	
	private Vector queue = new Vector(Preferences.getPreferenceInt(Preferences.UNDO_QUEUE_SIZE).cur);
	private int cursor = -1;
	
	// The Constructors
	public UndoQueue(OutlinerDocument doc) {
		this.doc = doc;
	}
	
	public void destroy() {
		doc = null;
		queue = null;
	}
	
	public void add(Undoable undoable) {
		doc.setFileModified(true);
		
		int queueSize = Preferences.getPreferenceInt(Preferences.UNDO_QUEUE_SIZE).cur;
		
		// Short Circuit if undo is disabled.
		if (queueSize == 0) {
			return;
		}
		
		trim();
		
		if (queue.size() < queueSize) {
			cursor++;
			queue.addElement(undoable);
		} else {
			queue.removeElementAt(0);
			queue.addElement(undoable);
		}
		
		updateMenuBar(doc);
	}
	
	public Undoable get() {
		try {
			return (Undoable) queue.elementAt(cursor);
		} catch (ArrayIndexOutOfBoundsException aiobe) {
			return null;
		}
	}

	public UndoableEdit getIfEdit() {
		try {
			return (UndoableEdit) get();
		} catch (ClassCastException cce) {
			return null;
		}
	}

	public void trim() {
		// First trim off any redoables
		queue.setSize(cursor + 1);
		
		// Next, trim undoables from oldest to newest until the size matches the UNDO_QUEUE_SIZE preference.
		// This could be optimized with System.arraycopy.
		while (queue.size() > Preferences.getPreferenceInt(Preferences.UNDO_QUEUE_SIZE).cur) {
			queue.removeElementAt(0);
			cursor--;
		}
	}

	public void clear() {
		//System.out.println("Clear");
		cursor = -1;
		queue.setSize(0);
		updateMenuBar(doc);
	}
	
	public boolean isEmpty() {
		if (queue.size() <= 0) {
			return true;
		} else {
			return false;
		}
	}


	// Undo Methods
	public void undo() {
		if (isUndoable()) {
			primitiveUndo();
			updateMenuBar(doc);
			doc.setFileModified(true);
		}
	}

	public void undoAll() {
		while (isUndoable()) {
			primitiveUndo();
		}
		updateMenuBar(doc);
		doc.setFileModified(true);
	}

	private void primitiveUndo() {
		((Undoable) queue.elementAt(cursor)).undo();
		cursor--;
	}
	
	public boolean isUndoable() {
		if (cursor >= 0) {
			return true;
		} else {
			return false;
		}
	}


	// Redo Methods	
	public void redo() {
		if (isRedoable()) {
			primitiveRedo();
			updateMenuBar(doc);
			doc.setFileModified(true);	
		}	
	}

	public void redoAll() {
		while (isRedoable()) {
			primitiveRedo();
		}
		updateMenuBar(doc);
		doc.setFileModified(true);	
	}
	
	private void primitiveRedo() {
		cursor++;
		((Undoable) queue.elementAt(cursor)).redo();
	}

	public boolean isRedoable() {
		if (cursor < queue.size() - 1) {
			return true;
		} else {
			return false;
		}
	}


	// Static Methods
	private static JMenuItem undoItem = null;
	private static JMenuItem redoItem = null;
	private static JMenuItem undoAllItem = null;
	private static JMenuItem redoAllItem = null;
	
	public static void updateMenuBar(OutlinerDocument doc) {
		// Lazy instantiation of references to JMenuItems.
		if (undoItem == null) {
			undoItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.UNDO_MENU_ITEM);
			redoItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.REDO_MENU_ITEM);
			undoAllItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.UNDO_ALL_MENU_ITEM);
			redoAllItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.REDO_ALL_MENU_ITEM);
		}
		
		if (doc == null) {
			undoItem.setEnabled(false);
			undoAllItem.setEnabled(false);
			redoItem.setEnabled(false);
			redoAllItem.setEnabled(false);
		} else {
			if(doc.undoQueue.isUndoable()) {
				undoItem.setEnabled(true);
				undoAllItem.setEnabled(true);
			} else {
				undoItem.setEnabled(false);
				undoAllItem.setEnabled(false);
			}
	
			if(doc.undoQueue.isRedoable()) {
				redoItem.setEnabled(true);
				redoAllItem.setEnabled(true);
			} else {
				redoItem.setEnabled(false);
				redoAllItem.setEnabled(false);
			}
		}
	}
}