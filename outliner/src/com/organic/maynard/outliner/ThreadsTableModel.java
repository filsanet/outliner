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

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class ThreadsTableModel extends AbstractTableModel {

	// Fields
	private ArrayList threads = new ArrayList(); // Threads
	private ArrayList threadIDs = new ArrayList(); // Threads
	private ArrayList startedBy = new ArrayList(); // Threads
	private ArrayList startedAt = new ArrayList(); // Threads
	
	private int threadIDCount = 0;

	// Constructors
	public ThreadsTableModel() {

	}
	
	// Misc Accessors
	public int getSize() {return threads.size();}
	
	public int indexOf(String name) {
		for (int i = 0; i < threads.size(); i++) {
			Thread thread = get(i);
			if (thread.getName().equals(name)) {
				return i;
			}
		}
		
		return -1;
	}

	public int indexOfThreadID(int threadID) {
		for (int i = 0; i < threadIDs.size(); i++) {
			Integer currentThreadID = getThreadID(i);
			if (currentThreadID.intValue() == threadID) {
				return i;
			}
		}
		
		return -1;
	}
	
	// Getters
	public Thread get(int i) {
		return (Thread) threads.get(i);	
	}

	public Integer getThreadID(int i) {
		return (Integer) threadIDs.get(i);	
	}	
	public Thread get(String name) {
		int i = indexOf(name);
		if (i != -1) {
			return get(i);
		} else {
			return null;
		}
	}
	
	// Add/Insert
	public int add(Thread thread, int startedBy, String startedAt) {
		int i = getSize();
		threads.add(i, thread);
		
		int threadID = ++threadIDCount;
		threadIDs.add(i, new Integer(threadID));
		
		String startedByString = null;
		switch(startedBy) {
			case ScriptsManagerModel.STARTUP_SCRIPT:
				startedByString = ScriptsManagerModel.STARTUP_SCRIPT_TEXT;
				break;
			case ScriptsManagerModel.SHUTDOWN_SCRIPT:
				startedByString = ScriptsManagerModel.SHUTDOWN_SCRIPT_TEXT;
				break;
			case ScriptsManagerModel.USER_SCRIPT:
				startedByString = ScriptsManagerModel.USER_SCRIPT_TEXT;
				break;
			default:
				startedByString = ScriptsManagerModel.UNKNOWN_SCRIPT_TEXT;
		}
		
		this.startedBy.add(i, startedByString); 

		this.startedAt.add(i, startedAt); 
		
		// Update the table
		fireTableRowsInserted(i, i);
		
		return threadID;		
	}

	// Remove
	public void remove(int i) {
		threads.remove(i);
		threadIDs.remove(i);
		startedBy.remove(i);
		startedAt.remove(i);
		
		fireTableRowsDeleted(i, i);
	}
	
	public int remove(String name) {
		int i = indexOf(name);
		if (i != -1) {
			remove(i);
		}
		
		return i;
	}

	public int removeThread(int threadID) {
		int i = indexOfThreadID(threadID);
		if (i != -1) {
			remove(i);
		}
		
		return i;
	}
	
	public String getName(int i) {return get(i).getName();}

	public String getStartedBy(int i) {return (String) startedBy.get(i);}

	public String getStartedAt(int i) {return (String) startedAt.get(i);}
	
	
	// TableModel Interface
	public String getColumnName(int col) {
		if (col == 0) {
			return "";
		} else if (col == 1) {
			return "ID";
		} else if (col == 2) {
			return "Script Name";
		} else if (col == 3) {
			return "Started By";
		} else if (col == 4) {
			return "Start Time";
		} else {
			return "error";
		}
	}


    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    	
	public int getColumnCount() {
		return 5;
	}
	
	public int getRowCount() {
		return getSize();
	}
	
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return "kill";
		} else if (col == 1) {
			return getThreadID(row).toString();
		} else if (col == 2) {
			return getName(row);
		} else if (col == 3) {
			return getStartedBy(row);
		} else if (col == 4) {
			return getStartedAt(row);
		} else {
			return "error";
		}
	}
	
	public boolean isCellEditable(int row, int col) {
		if (col == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setValueAt(Object value, int row, int col) {
		// Nothing is editable.
	}
}
