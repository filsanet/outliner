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

public class ScriptsTable extends JTable {
	
	// The Constructor
	public ScriptsTable() {
		setModel(Outliner.scriptsManager.model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setShowVerticalLines(false);
		setIntercellSpacing(new Dimension(0,1));
		
		getTableHeader().setReorderingAllowed(false);

		ScriptsButtonCellEditor editor = new ScriptsButtonCellEditor();
		
		TableColumn runColumn = getColumnModel().getColumn(0);
		runColumn.setCellRenderer(editor);
		runColumn.setCellEditor(editor);
		runColumn.setMinWidth(60);
		runColumn.setMaxWidth(60);
		runColumn.setResizable(false);

		TableColumn editColumn = getColumnModel().getColumn(1);
		editColumn.setCellRenderer(editor);
		editColumn.setCellEditor(editor);
		editColumn.setMinWidth(60);
		editColumn.setMaxWidth(60);
		editColumn.setResizable(false);
	}


	// Static Methods
	protected static void runScript(int index) {
		Script script = Outliner.scriptsManager.model.get(index);
		
		if (script != null) {
			ScriptsManagerModel.runScript(script, ScriptsManagerModel.USER_SCRIPT);
		}
	}
	
	protected static void updateScript(int index) {
		Script script = Outliner.scriptsManager.model.get(index);
		
		if (script != null) {
			Outliner.scriptsManager.displayScriptEditor(script, ScriptEditor.BUTTON_MODE_UPDATE);
		}
	}
}