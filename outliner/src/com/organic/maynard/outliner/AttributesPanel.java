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

	protected TableCellRenderer removeColRenderer = new RemoveColumnRenderer(this);
	protected TableCellEditor removeColEditor = new RemoveColumnRenderer(this);

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
		removeColumn.setCellRenderer(removeColRenderer);
		removeColumn.setCellEditor(removeColEditor);

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


public class AttributesPanel extends AbstractAttributesPanel {

	protected OutlinerDocument doc = null;

	// The Constructor
	public AttributesPanel(OutlinerDocument doc) {
		super();
		
		this.doc = doc;
	}
		
	// Data Display
	public void update() {
		if (doc.isShowingAttributes()) {
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

	// Data Modification
	public void newAttribute(String key, Object value, AttributeTableModel model) {
 		model.keys.add(key);
		model.values.add(value);

	   	Node node = doc.tree.getEditingNode();
		node.setAttribute(key, value);
				
		// undo
		CompoundUndoable undoable = new CompoundUndoablePropertyChange(doc.tree);
		Undoable primitive = new PrimitiveUndoableAttributeChange(node, null, null, key, value);
		undoable.addPrimitive(primitive);
		doc.undoQueue.add(undoable);

		model.fireTableDataChanged();
	}
	
	// Delete Attribute
	public void deleteAttribute(int row, AttributeTableModel model) {
	
    	Node node = doc.tree.getEditingNode();
		String key = (String) model.keys.get(row);
		
		Object oldValue = node.getAttribute(key);
		
		node.removeAttribute(key);
		model.keys.remove(row);
		model.values.remove(row);
				
		// undo
		CompoundUndoable undoable = new CompoundUndoablePropertyChange(doc.tree);
		Undoable primitive = new PrimitiveUndoableAttributeChange(node, key, oldValue, null, null);
		undoable.addPrimitive(primitive);
		doc.undoQueue.add(undoable);
		
		model.fireTableRowsDeleted(row, row);
	}

	// Set Value
    public void setValueAt(Object value, int row, AttributeTableModel model) {
    	Node node = doc.tree.getEditingNode();
		String key = (String) model.keys.get(row);
		
		Object oldValue = node.getAttribute(key);
		node.setAttribute(key, value);
		model.values.set(row, value);
		
		if (oldValue.equals(value)) {
			return;
		}
		
		// undo
		CompoundUndoable undoable = new CompoundUndoablePropertyChange(doc.tree);
		Undoable primitive = new PrimitiveUndoableAttributeChange(node, key, oldValue, key, value);
		undoable.addPrimitive(primitive);
		doc.undoQueue.add(undoable);
	}
		
	// Misc
    protected boolean isCellEditable() {
    	Node node = doc.tree.getEditingNode();
    	
    	if (!node.isEditable()) {
    		return false;
    	}
    	
    	return true;
	}
}



class AttributeTableModel extends AbstractTableModel implements MouseListener {

	private static NewAttributeDialog dialog = new NewAttributeDialog();
	
	public AbstractAttributesPanel panel = null;
	
	public Vector keys = new Vector();
	public Vector values = new Vector();
	
	public AttributeTableModel(AbstractAttributesPanel panel) {
		super();
		this.panel = panel;
	}

	public int getColumnCount() {
		return 3;
	}
	
	public int getRowCount() {
		return keys.size();
	}
	
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return "";
		} else if (col == 1) {
			return keys.get(row);
		} else {
			return values.get(row);
		}
	}
	
	public String getColumnName(int col) {
		if (col == 0) {
			return "";
		} else if (col == 1) {
			return "Attribute";
		} else {
			return "Value";
		}
	}

    public boolean isCellEditable(int row, int col) {
    
    	if (!panel.isCellEditable()) {
    		return false;
    	}
    	
		if (col == 0 || col == 2) { 
			return true;
		} else {
			return false;
		}
	}

    public void setValueAt(Object value, int row, int col) {
    	if (col == 2) {
    		panel.setValueAt(value, row, this);
			
			fireTableCellUpdated(row, col);
		}
	}

	
	// MouseListener Interface
	public void mouseClicked(MouseEvent e) {
		int col = panel.getTableHeader().columnAtPoint(e.getPoint());
		if (col == 0) {

	    	if (!panel.isCellEditable()) {
	    		return;
	    	}
		
			dialog.show(panel);
		} else if (col == 1) {
			//System.out.println("sort");
		}
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}



class RemoveColumnRenderer extends AbstractCellEditor implements ActionListener, TableCellRenderer, TableCellEditor {
	private JButton button = new JButton("Delete");
	private AbstractAttributesPanel panel = null;
	
	public RemoveColumnRenderer(AbstractAttributesPanel panel) {
		super();
		this.panel = panel;
		button.addActionListener(this);
	}
	
	public Component getTableCellRendererComponent(
		JTable table, 
		Object value, 
		boolean isSelected, 
		boolean hasFocus, 
		int row, 
		int column
	) {
		return button;
	}

	public Component getTableCellEditorComponent(
		JTable table, 
		Object value, 
		boolean isSelected, 
		int row, 
		int column
	) {
		return button;
	}
	
	public Object getCellEditorValue() {
		return "remove_value";
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		panel.deleteAttribute(panel.getEditingRow(), (AttributeTableModel) panel.getModel());
	}
}



class RemoveColumnHeaderRenderer extends JButton implements TableCellRenderer {
	public RemoveColumnHeaderRenderer() {
		super("New...");
	}
	
	public Component getTableCellRendererComponent(
		JTable table, 
		Object value, 
		boolean isSelected, 
		boolean hasFocus, 
		int row, 
		int column
	) {
		return this;
	}
}



class NewAttributeDialog extends JDialog implements ActionListener {

	// Constants
	private static final String OK = "OK";
	private static final String CANCEL = "Cancel";
	private static final String NEW_ATTRIBUTE = "New Attribute";
	private static final String ATTRIBUTE = "Attribute";
	private static final String VALUE = "Value";

	private static final String ERROR_EXISTANCE = "Error: key cannot be empty.";
	private static final String ERROR_UNIQUENESS = "Error: key must be unique.";
	private static final String ERROR_ALPHA_NUMERIC = "Error: key must be alpha-numeric.";

	// GUI Elements
	private JButton buttonOK = new JButton(OK);
	private JButton buttonCancel = new JButton(CANCEL);
	private JTextField attributeField = new JTextField(20);
	private JTextField valueField = new JTextField(20);
	private JLabel errorLabel = new JLabel(" ");

	// Context
	private AbstractAttributesPanel panel = null;

	// Constructors	
	public NewAttributeDialog() {
		super(Outliner.outliner, NEW_ATTRIBUTE, true);
		
		// Create the Layout
		setSize(250,180);
		setResizable(false);
		
		// Adding window adapter to fix problem where initial focus won't go to the textfield.
		// Solution found at: http://forums.java.sun.com/thread.jsp?forum=57&thread=124417&start=15&range=15;
		addWindowListener(
			new WindowAdapter() {
				public void windowOpened(WindowEvent e) {
					attributeField.requestFocus();
				}
			}
		);

		// Define the Bottom Panel
		JPanel bottomPanel = new JPanel();

		bottomPanel.setLayout(new FlowLayout());
		
		buttonOK.addActionListener(this);
		bottomPanel.add(buttonOK);

		buttonCancel.addActionListener(this);
		bottomPanel.add(buttonCancel);

		getContentPane().add(bottomPanel,BorderLayout.SOUTH);

		// Define the Center Panel
		Box box = Box.createVerticalBox();

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(ATTRIBUTE), box);
		AbstractPreferencesPanel.addSingleItemCentered(attributeField, box);

		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(VALUE), box);
		AbstractPreferencesPanel.addSingleItemCentered(valueField, box);

		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(errorLabel, box);

		getContentPane().add(box,BorderLayout.CENTER);

		// Set the default button
		getRootPane().setDefaultButton(buttonOK);
	}
	
	public void show(AbstractAttributesPanel panel) {
		this.panel = panel;
		
		attributeField.setText("");
		valueField.setText("");
		errorLabel.setText(" ");
		
		attributeField.requestFocus();

		Rectangle r = Outliner.outliner.getBounds();
		setLocation((int) (r.getCenterX() - getWidth()/2), (int) (r.getCenterY() - getHeight()/2));
		
		super.show();
	}
		
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(OK)) {
			ok();
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		}
	}

	private void ok() {
		String key = attributeField.getText();
		String value = valueField.getText();
		AttributeTableModel model = (AttributeTableModel) panel.getModel();
		
		// Validate Existence
		if ((key == null) || key.equals("")) {
			errorLabel.setText(ERROR_EXISTANCE);
			return;
		}

		// Validate alpha-numeric
		if (!isValidXMLAttributeName(key)) {
			errorLabel.setText(ERROR_ALPHA_NUMERIC);
			return;		
		}
				
		// Validate Uniqueness
		for (int i = 0; i < model.keys.size(); i++) {
			String existingKey = (String) model.keys.get(i);
			if (key.equals(existingKey)) {
				errorLabel.setText(ERROR_UNIQUENESS);
				return;
			}
		}
		
		// All is good so lets make the change
		panel.newAttribute(key, value, model);
		
		this.hide();
	}

	private void cancel() {
		hide();
	}
	
	private boolean isValidXMLAttributeName(String text) {
		// Must match (Letter | '_' | ':') (Letter | Digit | '.' | '-' | '_' | ':')*
		// XML allows CombiningChar | Extender but I'm gonna be more restrictive since it's easier.
		// at some point we should improve this or find some code that already does this.
		
		char[] chars = text.toCharArray();
		
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			
			if (i == 0) {
				if (Character.isLetter(c) || c == '_' || c == ':') {
					continue;
				} else {
					return false;
				}
			} else {
				if (Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == '_' || c == ':') {
					continue;
				} else {
					return false;
				}			
			}
		}
		
		return true;
	}
}