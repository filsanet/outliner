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

		model = new AttributeTableModel();
		setModel(model);
	}	
	
	// Data Display
	public void update() {
		Node node = doc.tree.getEditingNode();
		
		model.keys.clear();
		model.values.clear();
		
		Iterator it = node.getAttributeKeys();
		if (it != null) {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = node.getAttribute(key);
				model.keys.add(key);
				model.values.add(value);
			}
		}
		
		model.fireTableDataChanged();
	}
}


class AttributeTableModel extends AbstractTableModel {
	
	public Vector keys = new Vector();
	public Vector values = new Vector();
	
	public AttributeTableModel() {
		super();
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
}
