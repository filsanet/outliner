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

public class ThreadsTable extends JTable {
	
	// The Constructor
	public ThreadsTable() {
		setModel(Outliner.scriptsManager.threadsTableModel);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		getTableHeader().setReorderingAllowed(false);

		ThreadsButtonCellEditor editor = new ThreadsButtonCellEditor();
		
		TableColumn killColumn = getColumnModel().getColumn(0);
		killColumn.setCellRenderer(editor);
		killColumn.setCellEditor(editor);
		killColumn.setMinWidth(60);
		killColumn.setMaxWidth(60);
		killColumn.setResizable(false);

		TableColumn idColumn = getColumnModel().getColumn(1);
		idColumn.setMinWidth(25);
		idColumn.setPreferredWidth(25);
		idColumn.setMaxWidth(75);
	}


	// Static Methods
	protected static void killThread(int index) {
		Thread thread = Outliner.scriptsManager.threadsTableModel.get(index);
		if (thread != null) {
			thread.interrupt();
		}
	}
}