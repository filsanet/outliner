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

public class DocumentAttributesPanel extends AbstractAttributesPanel {

	private DocumentAttributesView view = null;
	
	// The Constructor
	public DocumentAttributesPanel() {
		super();
	}
	
	// Data Display
	public void update(DocumentAttributesView view) {
		this.view = view;
		
		AttributeContainer node = view.tree;
		
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
	
	public void update() {

	}

	// Data Modification
	public void newAttribute(String key, Object value, AttributeTableModel model) {
 		model.keys.add(key);
		model.values.add(value);

		model.fireTableDataChanged();
	}
	
	// Delete Attribute
	public void deleteAttribute(int row, AttributeTableModel model) {
		model.keys.remove(row);
		model.values.remove(row);
		
		model.fireTableRowsDeleted(row, row);
	}

	// Set Value
    public void setValueAt(Object value, int row, AttributeTableModel model) {
    	model.values.set(row, value);
	}
		
	// Misc
    protected boolean isCellEditable() {
    	return true;
	}
}