/**
 * Copyright (C) 2001 Maynard Demmon, maynard@organic.com
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

/**
 * Most of the code for this class, ButtonCellEditor, 
 * came from http://www2.gol.com/users/tame/swing/examples/JTableExamples2.html.
 */
 
public class ButtonCellEditor extends DefaultCellEditor implements TableCellRenderer {
	protected JButton button;
	protected JButton rendererButton;
	
	private String    label;
	private boolean   isPushed;
	
	protected int row = -1;
	protected int col = -1;
	
	public ButtonCellEditor(JCheckBox checkBox) {
		super(checkBox);
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			}
		);

		rendererButton = new JButton();
		rendererButton.setOpaque(true);

	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (isSelected) {
			rendererButton.setForeground(table.getSelectionForeground());
			rendererButton.setBackground(table.getSelectionBackground());
		} else{
			rendererButton.setForeground(table.getForeground());
			rendererButton.setBackground(UIManager.getColor("Button.background"));
		}

		label = (value == null) ? "" : value.toString();
		rendererButton.setText(label);
				
		return rendererButton;
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else{
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}
		label = (value == null) ? "" : value.toString();
		button.setText(label);
		isPushed = true;
		this.row = row;
		this.col = column;
		return button;
	}

	public Object getCellEditorValue() {
		if (isPushed)  {
			doEditing();
		}
		isPushed = false;
		return new String(label) ;
	}

	protected void doEditing() {}
	
	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}
	
	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
}