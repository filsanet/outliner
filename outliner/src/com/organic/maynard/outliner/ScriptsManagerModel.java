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

import java.util.*;
import java.io.*;
import org.xml.sax.*;
import javax.swing.*;
import javax.swing.table.*;
import com.organic.maynard.xml.XMLTools;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class ScriptsManagerModel extends AbstractTableModel {
	// Constants
	public static final int STARTUP_SCRIPT = 0;
	public static final int SHUTDOWN_SCRIPT = 1;
	public static final int USER_SCRIPT = 2;

	public static final String STARTUP_SCRIPT_TEXT = "Startup";
	public static final String SHUTDOWN_SCRIPT_TEXT = "Shutdown";
	public static final String USER_SCRIPT_TEXT = "User";
	public static final String UNKNOWN_SCRIPT_TEXT = "Unknown";
	

	// Fields
	private ArrayList scripts = new ArrayList(); // Strings

	private static final int SCRIPT_EVENT_COUNT = 2; // Should be equal to the number of boolean isX ArrayLists.	

	// Constructors
	public ScriptsManagerModel() {

	}


	// Static Methods
	public static void runStartupScripts() {
		for (int i = 0; i < Outliner.scriptsManager.model.getSize(); i++) {
			Script script = Outliner.scriptsManager.model.get(i);
			
			if (script.isStartupScript()) {
				runScript(script, STARTUP_SCRIPT);
			}
		}
	}

	public static void runShutdownScripts() {
		for (int i = 0; i < Outliner.scriptsManager.model.getSize(); i++) {
			Script script = Outliner.scriptsManager.model.get(i);
			
			if (script.isShutdownScript()) {
				runScript(script, SHUTDOWN_SCRIPT);
			}
		}
	}
	
	public static void runScript(Script script, int scriptType) {
		if (scriptType == SHUTDOWN_SCRIPT) {
			try {
				script.process();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} else {
			// Create a new Thread
			ScriptThread scriptThread = new ScriptThread(script, scriptType);
			
			// Run the Thread
			scriptThread.start();
		}
	}
	
	
	// Misc Accessors
	public int getSize() {return scripts.size();}

	public boolean isNameUnique(String name) {
		for (int i = 0; i < scripts.size(); i++) {
			if (name.equals(get(i).getName())) {
				return false;
			}
		}
		return true;	
	}
	
	public int indexOf(String name) {
		for (int i = 0; i < scripts.size(); i++) {
			Script script = get(i);
			if (script.getName().equals(name)) {
				return i;
			}
		}
		
		return -1;
	}

	// Getters
	public Script get(int i) {
		return (Script) scripts.get(i);	
	}
	
	public Script get(String name) {
		int i = indexOf(name);
		if (i != -1) {
			return get(i);
		} else {
			return null;
		}
	}
	
	// Add/Insert
	public int add(Script script) {
		// Find the correct spot to add it alphabetically
		int i;
		for (i = 0; i < scripts.size(); i++) {
			Script scriptTemp = (Script) scripts.get(i);
			if (scriptTemp.getName().compareTo(script.getName()) >= 0) {
				break;
			}
		}
		
		scripts.add(i, script);
		
		// Update the table
		fireTableRowsInserted(i, i);
		
		return i;		
	}

	// Remove
	public void remove(int i) {
		scripts.remove(i);
		
		fireTableRowsDeleted(i, i);
	}
	
	public int remove(String name) {
		int i = indexOf(name);
		if (i != -1) {
			remove(i);
		}
		
		return i;
	}

	// Boolean Accessors
	public String getName(int i) {return get(i).getName();}
	
	public boolean getIsStartup(int i) {
		return get(i).isStartupScript();
	}
	
	public void setIsStartup(int i, Boolean b) {
		get(i).setStartupScript(b.booleanValue());
	}
	
	public void setIsStartup(int i, boolean b) {
		get(i).setStartupScript(b);
	}
	
	public void setIsStartup(int i, String b) {
		get(i).setStartupScript((new Boolean(b)).booleanValue());
	}


	public boolean getIsShutdown(int i) {
		return get(i).isShutdownScript();
	}
	
	public void setIsShutdown(int i, Boolean b) {
		get(i).setShutdownScript(b.booleanValue());
	}
	
	public void setIsShutdown(int i, boolean b) {
		get(i).setShutdownScript(b);
	}
	
	public void setIsShutdown(int i, String b) {
		get(i).setShutdownScript((new Boolean(b)).booleanValue());
	}
	
	
	// misc
	public static boolean validateUniqueness(String name) {
		if (Outliner.scriptsManager.model.isNameUnique(name)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	// TableModel Interface
	public String getColumnName(int col) {
		if (col == 0 || col == 1) {
			return "";
		} else if (col == 2) {
			return "Script";
		} else if (col == 3) {
			return "Startup";
		} else if (col == 4) {
			return "Shutdown";
		} else {
			return "error";
		}
	}


    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    	
	public int getColumnCount() {
		return 3 + SCRIPT_EVENT_COUNT;
	}
	
	public int getRowCount() {
		return getSize();
	}
	
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return "run";
		} else if (col == 1) {
			return "edit";
		} else if (col == 2) {
			return getName(row);
		} else if (col == 3) {
			return new Boolean(getIsStartup(row));
		} else if (col == 4) {
			return new Boolean(getIsShutdown(row));
		} else {
			return "error";
		}
	}
	
	public boolean isCellEditable(int row, int col) {
		if (col == 2) {
			return false;
		} else {
			return true;
		}
	}
	
	public void setValueAt(Object value, int row, int col) {
		if (col == 0 || col == 1 || col == 2) {
			// Do nothing, should not be editable.
		} else if (col == 3) {
			setIsStartup(row, (Boolean) value);
			
		} else if (col == 4) {
			setIsShutdown(row, (Boolean) value);
			
		} else {
			// Shouldn't happen.
		}	
	}
}
