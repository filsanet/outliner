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

public class ScriptThread extends Thread {
	private Script script = null;
	private int threadID = -1;
	
	ScriptThread(Script script, int startedBy) {
		this.script = script;
		setName(script.getName());

		// Register the thread
		this.threadID = Outliner.scriptsManager.threadsTableModel.add(this, startedBy, DocumentInfo.getCurrentDateTimeString());

	}

	public void run() {
		try {
			// Process the Script
			script.process();
		} catch (InterruptedException ie) {
			System.out.println("interrupted.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		// Remove it
		Outliner.scriptsManager.threadsTableModel.removeThread(this.threadID);
	}
}