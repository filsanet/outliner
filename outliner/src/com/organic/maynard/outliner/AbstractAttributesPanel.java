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

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public abstract class AbstractAttributesPanel extends JTable {

	protected RemoveColumnHeaderRenderer removeColumnHeaderRenderer = new RemoveColumnHeaderRenderer();
	
	// GUI Fields
	protected AttributeTableModel model = null;

	// The Constructor
	public AbstractAttributesPanel() {
		model = new AttributeTableModel(this);
		setModel(model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		TableColumn removeColumn = getColumnModel().getColumn(0);
		removeColumn.setMinWidth(80);
		removeColumn.setMaxWidth(80);
		removeColumn.setResizable(false);
		
		AttributesButtonCellEditor editor = new AttributesButtonCellEditor(this);
		
		removeColumn.setCellRenderer(editor);
		removeColumn.setCellEditor(editor);

		removeColumn.setHeaderRenderer(removeColumnHeaderRenderer);
		getTableHeader().addMouseListener(model);   
		getTableHeader().setReorderingAllowed(false); 
	}
	
	// Data Display
	public abstract void update();

	// Data Modification
	public abstract void newAttribute(String key, Object value, AttributeTableModel model);
	
	// Delete Attribute
	public abstract void deleteAttribute(int row, AttributeTableModel model);

	// Set Value
    public abstract void setValueAt(Object value, int row, AttributeTableModel model);
		
	// Misc
    protected abstract boolean isCellEditable();
}