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

public class AttributesPanel extends JTable {

	public OutlinerDocument doc = null;
	
	// GUI Fields
	AttributeTableModel model = null;

	// The Constructor
	public AttributesPanel(OutlinerDocument doc) {
		this.doc = doc;

		model = new AttributeTableModel(this);
		setModel(model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	// Data Display
	public void update() {
		if (doc.isShowingAttributes()) {
			//System.out.println("Updating Table Data");
			Node node = doc.tree.getEditingNode();
			
			model.keys.clear();
			model.values.clear();
			clearSelection();
			
			Iterator it = node.getAttributeKeys();
			if (it != null) {
				while (it.hasNext()) {
					String key = (String) it.next();
					Object value = node.getAttribute(key);
					model.keys.add(key);
					model.values.add(value);
				}
			}
			if (isEditing()) {
				getCellEditor().cancelCellEditing();
			}
			model.fireTableDataChanged();
		}
	}
}


class AttributeTableModel extends AbstractTableModel {
	
	public AttributesPanel panel = null;
	
	public Vector keys = new Vector();
	public Vector values = new Vector();
	
	public AttributeTableModel(AttributesPanel panel) {
		super();
		this.panel = panel;
	}

	public int getColumnCount() {
		return 2;
	}
	
	public int getRowCount() {
		return keys.size();
	}
	
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return keys.get(row);
		} else {
			return values.get(row);
		}
	}
	
	public String getColumnName(int col) {
		if (col == 0) {
			return "Attribute";
		} else {
			return "Value";
		}
	}

	// Editing Methods
    public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

    public boolean isCellEditable(int row, int col) {
		if (col == 1) { 
			return true;
		} else {
			return false;
		}
	}

    public void setValueAt(Object value, int row, int col) {
    	System.out.println("SetValue called.");
		Node node = panel.doc.tree.getEditingNode();
		String key = (String) keys.get(row);
		
		Object oldValue = node.getAttribute(key);
		node.setAttribute(key, value);
		values.set(row, value);
		
		if (oldValue.equals(value)) {
			return;
		}
		
		// undo
		CompoundUndoable undoable = new CompoundUndoablePropertyChange(panel.doc.tree);
		Undoable primitive = new PrimitiveUndoableAttributeChange(node, key, oldValue, key, value);
		undoable.addPrimitive(primitive);
		panel.doc.undoQueue.add(undoable);
		
		fireTableCellUpdated(row, col);
	}
}
