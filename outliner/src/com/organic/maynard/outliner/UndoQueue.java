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
	
	private Vector queue = new Vector(Preferences.UNDO_QUEUE_SIZE.cur);
	private int cursor = -1;
	
	// The Constructors
	public UndoQueue(OutlinerDocument doc) {
		this.doc = doc;
	}
	
	public void destroy() {
		//for (int i = 0; i < queue.size(); i++) {
		//	((Undoable) queue.get(i)).destroy();
		//}
		
		doc = null;
		queue = null;
	}
	
	public static void updateMenuBar(OutlinerDocument doc) {
		// First make sure the document still exists
		if (doc == null) {
			Outliner.menuBar.editMenu.EDIT_UNDO_ITEM.setEnabled(false);
			Outliner.menuBar.editMenu.EDIT_UNDO_ALL_ITEM.setEnabled(false);
			Outliner.menuBar.editMenu.EDIT_REDO_ITEM.setEnabled(false);
			Outliner.menuBar.editMenu.EDIT_REDO_ALL_ITEM.setEnabled(false);
			return;
		} else {
			if(doc.undoQueue.isUndoable()) {
				Outliner.menuBar.editMenu.EDIT_UNDO_ITEM.setEnabled(true);
				Outliner.menuBar.editMenu.EDIT_UNDO_ALL_ITEM.setEnabled(true);
			} else {
				Outliner.menuBar.editMenu.EDIT_UNDO_ITEM.setEnabled(false);
				Outliner.menuBar.editMenu.EDIT_UNDO_ALL_ITEM.setEnabled(false);
			}
	
			if(doc.undoQueue.isRedoable()) {
				Outliner.menuBar.editMenu.EDIT_REDO_ITEM.setEnabled(true);
				Outliner.menuBar.editMenu.EDIT_REDO_ALL_ITEM.setEnabled(true);
			} else {
				Outliner.menuBar.editMenu.EDIT_REDO_ITEM.setEnabled(false);
				Outliner.menuBar.editMenu.EDIT_REDO_ALL_ITEM.setEnabled(false);
			}
		}
	}
	
	public void add(Undoable undoable) {
		// Short Circuit if undo is disabled.
		if (Preferences.UNDO_QUEUE_SIZE.cur == 0) {
			return;
		}
		
		trim();
		
		if (queue.size() < Preferences.UNDO_QUEUE_SIZE.cur) {
			cursor++;
			queue.addElement(undoable);
		} else {
			queue.removeElementAt(0);
			queue.addElement(undoable);
		}
		updateMenuBar(doc);
		doc.setFileModified(true);
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
		while (queue.size() > Preferences.UNDO_QUEUE_SIZE.cur) {
			queue.removeElementAt(0);
			cursor--;
		}
		
		updateMenuBar(doc);
	}

	public void clear() {
		//System.out.println("Clear");
		cursor = -1;
		queue.setSize(0);
		updateMenuBar(doc);
	}
	
	public void undo() {
		//System.out.println("Undo");
		if (isUndoable()) {
			((Undoable) queue.elementAt(cursor)).undo();
			cursor--;
			updateMenuBar(doc);
			doc.setFileModified(true);
		}
	}

	public void undoAll() {
		while (isUndoable()) {
			undo();
		}
	}
		
	public void redo() {
		//System.out.println("Redo");
		if (isRedoable()) {
			cursor++;
			((Undoable) queue.elementAt(cursor)).redo();
			updateMenuBar(doc);
			doc.setFileModified(true);
		}	
	}
	
	public void redoAll() {
		while (isRedoable()) {
			redo();
		}	
	}
	
	public boolean isUndoable() {
		if (cursor >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isRedoable() {
		if (cursor < queue.size() - 1) {
			return true;
		} else {
			return false;
		}
	}
}