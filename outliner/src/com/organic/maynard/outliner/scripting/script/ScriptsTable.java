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
 
package com.organic.maynard.outliner.scripting.script;

import com.organic.maynard.outliner.*;
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