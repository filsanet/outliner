/** * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com * All rights reserved. *  * Redistribution and use in source and binary forms, with or  * without modification, are permitted provided that the  * following conditions are met: *  *  - Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  *  - Redistributions in binary form must reproduce the above  *    copyright notice, this list of conditions and the following  *    disclaimer in the documentation and/or other materials provided  *    with the distribution.  *  *  - Neither the names "Java Outline Editor", "JOE" nor the names of its  *    contributors may be used to endorse or promote products derived  *    from this software without specific prior written permission.  *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS  * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT  * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS  * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE  * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,  * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER  * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT  * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN  * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  * POSSIBILITY OF SUCH DAMAGE. */package com.organic.maynard.outliner;import java.awt.*;import java.awt.event.*;import java.util.*;import javax.swing.*;import javax.swing.table.*;import javax.swing.event.*;import javax.swing.tree.*;public class DocumentAttributesPanel extends AbstractAttributesPanel {	private DocumentAttributesView view = null;	// The Constructor	public DocumentAttributesPanel() {		super();	}	// Data Display	public void update(DocumentAttributesView view) {		this.view = view;		AttributeContainer node = view.tree;		model.keys.clear();		model.values.clear();		model.readOnly.clear();		clearSelection();		Iterator it = node.getAttributeKeys();		if (it != null) {			while (it.hasNext()) {				String key = (String) it.next();				Object value = node.getAttribute(key);				boolean readOnly = node.isReadOnly(key);				model.keys.add(key);				model.values.add(value);				model.readOnly.add(new Boolean(readOnly));			}		}		if (isEditing()) {			getCellEditor().cancelCellEditing();		}		model.fireTableDataChanged();	}	public void update() {}	// Data Modification	public void newAttribute(String key, Object value, boolean isReadOnly, AttributeTableModel model) { 		model.keys.add(key);		model.values.add(value);		model.readOnly.add(new Boolean(isReadOnly));		AttributeContainer node = view.tree;		node.setAttribute(key, value);		view.tree.getDocument().setFileModified(true);		model.fireTableDataChanged();	}	// Delete Attribute	public void deleteAttribute(int row, AttributeTableModel model) {		AttributeContainer node = view.tree;		String key = (String) model.keys.get(row);		node.removeAttribute(key);		model.keys.remove(row);		model.values.remove(row);		model.readOnly.remove(row);				view.tree.getDocument().setFileModified(true);		model.fireTableRowsDeleted(row, row);	}	// Toggle Editability	public void toggleEditability(int row, AttributeTableModel model) {		AttributeContainer node = view.tree;		String key = (String) model.keys.get(row);				boolean oldValue = true;		ImageIcon isReadOnly = (ImageIcon) model.getValueAt(row, 1);		if (isReadOnly == OutlineEditableIndicator.ICON_IS_NOT_PROPERTY) {			oldValue = true;		} else {			oldValue = false;		}						//boolean oldValue = ((Boolean) model.getValueAt(row, 1)).booleanValue();		boolean newValue = !oldValue;		model.readOnly.set(row, new Boolean(newValue));		node.setReadOnly(key, newValue);				view.tree.getDocument().setFileModified(true);				model.fireTableDataChanged();	}		// Set Value    public void setValueAt(Object value, int row, AttributeTableModel model) {		AttributeContainer node = view.tree;		String key = (String) model.keys.get(row);    	model.values.set(row, value);    	node.setAttribute(key, value);    	    	view.tree.getDocument().setFileModified(true);    	    	model.fireTableDataChanged();	}	// Misc    protected boolean isCellEditable() {    	return true;	}	protected boolean isCellEditable(int row) {		AttributeContainer node = view.tree;		String key = (String) model.keys.get(row);		if (node.isReadOnly(key)) {    		return false;    	}    	return true;	}}