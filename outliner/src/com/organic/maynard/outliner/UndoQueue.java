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
 
package com.organic.maynard.outliner;

import com.organic.maynard.outliner.dom.*;
import com.organic.maynard.outliner.event.*;

import com.organic.maynard.outliner.util.undo.*;

import com.organic.maynard.outliner.dom.*;
import java.util.*;
import javax.swing.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class UndoQueue {
	private OutlinerDocument doc = null;
	
	private UndoableList queue = new UndoableList(Preferences.getPreferenceInt(Preferences.UNDO_QUEUE_SIZE).cur);
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
			queue.add(undoable);
		} else {
			queue.remove(0);
			queue.add(undoable);
		}
		
		// Fire Event
		Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.ADD);
	}
	
	public Undoable get() {
		try {
			return queue.get(cursor);
		} catch (IndexOutOfBoundsException aiobe) {
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
		queue.trim(cursor + 1);
		
		// Next, trim undoables from oldest to newest until the size matches the UNDO_QUEUE_SIZE preference.
		// This could be optimized with System.arraycopy.
		while (queue.size() > Preferences.getPreferenceInt(Preferences.UNDO_QUEUE_SIZE).cur) {
			queue.remove(0);
			cursor--;
		}

		// Fire Event
		Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.TRIM);
	}

	public void clear() {
		//System.out.println("Clear");
		cursor = -1;
		queue.clear();

		// Fire Event
		Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.CLEAR);
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
			doc.setFileModified(true);

			// Fire Event
			Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.UNDO);
		}
	}

	public void undoAll() {
		while (isUndoable()) {
			primitiveUndo();
		}
		doc.setFileModified(true);

		// Fire Event
		Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.UNDO_ALL);
	}

	private void primitiveUndo() {
		queue.get(cursor).undo();
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
			doc.setFileModified(true);

			// Fire Event
			Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.REDO);
		}	
	}

	public void redoAll() {
		while (isRedoable()) {
			primitiveRedo();
		}
		doc.setFileModified(true);

		// Fire Event
		Outliner.documents.fireUndoQueueEvent(doc, UndoQueueEvent.REDO_ALL);
	}
	
	private void primitiveRedo() {
		cursor++;
		queue.get(cursor).redo();
	}

	public boolean isRedoable() {
		if (cursor < queue.size() - 1) {
			return true;
		} else {
			return false;
		}
	}
}