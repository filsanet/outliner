/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 * 
 *  - Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution. 
 * 
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
 
package com.organic.maynard.outliner.util.undo;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.dom.*;
import com.organic.maynard.outliner.event.*;

import java.util.*;
import javax.swing.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class UndoQueue implements com.organic.maynard.outliner.util.Destructible {

	// Instance Fields
	private Document doc = null;
	private UndoableList queue = new UndoableList(Preferences.getPreferenceInt(Preferences.UNDO_QUEUE_SIZE).cur);
	private int cursor = -1;


	/**
	 * Creates a new <code>UndoQueue</code> for the provided <code>Document</code>.
	 * an internal cursor is maintained that points to the current location. As
	 * actions are undone/redone this cursor is updated.
	 *
	 * @param doc the document that uses this <code>UndoQueue</code>.
	 */	
	public UndoQueue(Document doc) {
		this.doc = doc;
	}


	// Destructible Interface
	public void destroy() {
		doc = null;
		queue = null;
	}

	
	/**
	 * Adds the <code>Undoable</code> to this <code>UndoQueue</code>. The
	 * queue is trimmed as neccessary to accomodate the new undoable. If 
	 * successful, an <code>UndoQueueEvent</code> is fired.
	 *
	 * @param undoable the <code>Undoable</code> that is being added to
	 *                 this <code>UndoQueue</code>.
	 */	
	public void add(Undoable undoable) {
		doc.setModified(true);
		
		int queueSize = Preferences.getPreferenceInt(Preferences.UNDO_QUEUE_SIZE).cur;
		
		// Short Circuit if undo is disabled.
		if (queueSize == 0) {
			return;
		}
		
		// Trim Redoables
		trim();
		
		if (queue.size() < queueSize) {
			cursor++;
			queue.add(undoable);
		} else {
			queue.remove(0);
			queue.add(undoable);
		}
		
		// Fire Event
		Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.ADD);
	}

	/**
	 * Gets the current <code>Undoable</code> pointed to by the internal
	 * cursor.
	 *
	 * @return the current <code>Undoable</code> or <code>null</code> if
	 *         the <code>UndoQueue</code> is empty.
	 */
	public Undoable get() {
		try {
			return queue.get(cursor);
		} catch (IndexOutOfBoundsException aiobe) {
			return null;
		}
	}

	/**
	 * Gets the current <code>Undoable</code> pointed to by the internal
	 * cursor if it is an <code>UndoableEdit</code> object.
	 *
	 * @return the current <code>Undoable</code> or <code>null</code> if
	 *         the <code>UndoQueue</code> is empty, or the current <code>Undoable</code>
	 *         is not an instance of <code>UndoableEdit</code>.
	 */
	public UndoableEdit getIfEdit() {
		Undoable undoable = get();
		
		if (undoable instanceof UndoableEdit) {
			return (UndoableEdit) undoable;
		} else {
			return null;
		}
	}

	/**
	 * Trims the undo queue to the current size of the UNDO_QUEUE_SIZE Preference.
	 * Starts by trimming redoables then trims undoables if neccessary. An
	 * UndoQueueEvent is fired.
	 */	
	public void trim() {
		// First trim off any redoables
		queue.trim(cursor + 1);
		
		// Next, trim undoables.
		int range = queue.size() - Preferences.getPreferenceInt(Preferences.UNDO_QUEUE_SIZE).cur;
		if (range > 0) {
			queue.removeRange(0, range - 1);
			cursor -= range;
		}

		// Fire Event
		Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.TRIM);
	}

	/**
	 * Clears this <code>UndoQueue</code> resetting it's size to zero. An
	 * UndoQueueEvent is fired.
	 */	
	public void clear() {
		//System.out.println("Clear");
		cursor = -1;
		queue.clear();

		// Fire Event
		Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.CLEAR);
	}

	/**
	 * Indicates if this <code>UndoQueue</code> is currently empty.
	 *
	 * @return <code>true</code> if empty, otherwise returns <code>false</code>.
	 */
	public boolean isEmpty() {
		if (queue.size() <= 0) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Calls the undo() method on the current <code>Undoable</code> if the
	 * <code>UndoQueue</code> is currently undoable. An UndoQueueEvent is fired.
	 */	
	public void undo() {
		if (isUndoable()) {
			queue.get(cursor).undo();
			cursor--;
			doc.setModified(true);

			// Fire Event
			Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.UNDO);
		}
	}

	/**
	 * Calls the undo() method on each <code>Undoable</code> in the <code>UndoQueue</code>
	 * starting at the current <code>Undoable</code>. An UndoQueueEvent is fired.
	 */	
	public void undoAll() {
		while (isUndoable()) {
			queue.get(cursor).undo();
			cursor--;
		}
		doc.setModified(true);

		// Fire Event
		Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.UNDO_ALL);
	}

	/**
	 * Indicates if this <code>UndoQueue</code> is currently undoable.
	 *
	 * @return <code>true</code> if undoable, otherwise returns <code>false</code>.
	 */
	public boolean isUndoable() {
		if (cursor >= 0) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Calls the redo() method on the current <code>Undoable</code> if the
	 * <code>UndoQueue</code> is currently redoable. An UndoQueueEvent is fired.
	 */	
	public void redo() {
		if (isRedoable()) {
			cursor++;
			queue.get(cursor).redo();
			doc.setModified(true);

			// Fire Event
			Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.REDO);
		}	
	}

	/**
	 * Calls the redo() method on each <code>Undoable</code> in the <code>UndoQueue</code>
	 * starting at the current <code>Undoable</code>. An UndoQueueEvent is fired.
	 */	
	public void redoAll() {
		while (isRedoable()) {
			cursor++;
			queue.get(cursor).redo();
		}
		doc.setModified(true);

		// Fire Event
		Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.REDO_ALL);
	}

	/**
	 * Indicates if this <code>UndoQueue</code> is currently redoable.
	 *
	 * @return <code>true</code> if redoable, otherwise returns <code>false</code>.
	 */
	public boolean isRedoable() {
		if (cursor < queue.size() - 1) {
			return true;
		} else {
			return false;
		}
	}
}