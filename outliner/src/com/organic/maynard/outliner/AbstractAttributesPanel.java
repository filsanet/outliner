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