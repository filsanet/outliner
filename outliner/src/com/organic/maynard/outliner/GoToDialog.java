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
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.xml.sax.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class GoToDialog extends AbstractGUITreeJDialog implements ActionListener, JoeReturnCodes {

	// Constants
	private static final int INITIAL_WIDTH = 300;
	private static final int INITIAL_HEIGHT = 150;
 	private static final int MINIMUM_WIDTH = 300;
	private static final int MINIMUM_HEIGHT = 150;
	
	private static final String GO = "Go";
	private static final String GOTO_LINE_AND_COLUMN = "Go to Line and Column";
	private static final String CANCEL = "Cancel";

	private static final String LINE_NUMBER = "Enter a line number.";
	private static final String COLUMN_NUMBER = "Enter a column number.";
	private static final String COUNT_DEPTH = "Count indents for column number.";
	
	private static JTextField lineNumberTextField = new JTextField(10);
	private static JTextField columnNumberTextField = new JTextField(10);
	private static JCheckBox countDepthCheckBox = new JCheckBox(COUNT_DEPTH);
	
	private static JButton goButton = new JButton(GO);
	private static JButton gotoLineAndColumnButton = new JButton(GOTO_LINE_AND_COLUMN);
	private static JButton cancelButton = new JButton(CANCEL);
	
	// Fields
	private static OutlinerDocument doc = null;
	private static GoToDialog dialog = null;
	
	// The Constructor
	public GoToDialog() {
		super(false, true, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}


	// GUITreeComponentInterface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		setResizable(false);
		
		// Create the layout
		this.getContentPane().setLayout(new BorderLayout());

		goButton.addActionListener(this);
		gotoLineAndColumnButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		Box vBox = Box.createVerticalBox();
		
		vBox.add(new JLabel(COLUMN_NUMBER));
		vBox.add(columnNumberTextField);
		vBox.add(countDepthCheckBox);
		
		vBox.add(Box.createVerticalStrut(5));
		
		vBox.add(new JLabel(LINE_NUMBER));
		vBox.add(lineNumberTextField);

		vBox.add(Box.createVerticalStrut(5));
		
		Box hBox = Box.createHorizontalBox();
		hBox.add(cancelButton);
		hBox.add(Box.createHorizontalStrut(5));
		hBox.add(gotoLineAndColumnButton);
		hBox.add(Box.createHorizontalStrut(5));
		hBox.add(goButton);

		// Put it all together
		this.getContentPane().add(vBox,BorderLayout.CENTER);
		this.getContentPane().add(hBox,BorderLayout.SOUTH);

		// Set the default button
		getRootPane().setDefaultButton(goButton);
		
		// Assign ourselves to the static field. Basically were like a singleton.
		dialog = this;

		// This let's us actually set the focus in our modal dialog.
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				lineNumberTextField.requestFocus();
			}
			
			public void windowOpened(WindowEvent e) {
				lineNumberTextField.requestFocus();
			}
		});

		dialog.pack();
	}


	public static void setStateAndShow(OutlinerDocument document) {
		doc = document;
		
		// Populate Column Number
		int currentColumnNumber = doc.tree.getCursorPosition();

		if (countDepthCheckBox.isSelected()) {
			currentColumnNumber += doc.tree.getEditingNode().getDepth();
		}

		String columnNumber = "" + currentColumnNumber;
		columnNumberTextField.setText(columnNumber);

		// Populate Line Number
		int currentLineNumber = doc.tree.getEditingNode().getLineNumber();
		String lineNumber = "" + currentLineNumber;
		lineNumberTextField.setText(lineNumber);

		lineNumberTextField.setCaretPosition(0);
		lineNumberTextField.moveCaretPosition(lineNumber.length());

		dialog.show();
	}
	

	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(GO)) {
			go(false);
		} else if (e.getActionCommand().equals(GOTO_LINE_AND_COLUMN)) {
			go(true);
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		}
	}
	
	private static void go(boolean gotoColumn) {
	
		// Get a valid line number
		int lineNumber = 1;
		String lineNumberString = lineNumberTextField.getText();
		
		if (lineNumberString == null) {
			return;
		}
		try {
			lineNumber = Integer.parseInt(lineNumberString);
			if (lineNumber < 1) {
				lineNumber = 1;
			}
		} catch (NumberFormatException nfe) {
			return;
		}
		
		// Find the nth node.
		Node currentNode = doc.tree.rootNode;
		Node nextNode;
		for (int i = 0; i < lineNumber; i++) {
			nextNode = currentNode.nextNode();
			if (nextNode.isRoot()) {
				break;
			} else {
				currentNode = nextNode;
			}
		}

		
		// Geta a valid column number
		int columnNumber = 0;
		String columnNumberString = columnNumberTextField.getText();
		
		if (columnNumberString == null) {
			return;
		}
		try {
			columnNumber = Integer.parseInt(columnNumberString);
			
			// Handle indents if neccessary
			if (countDepthCheckBox.isSelected()) {
				columnNumber -= currentNode.getDepth();
			}
			
			if (columnNumber < 0) {
				columnNumber = 0;
			}
		} catch (NumberFormatException nfe) {
			return;
		}
				
		// Insert the node into the visible nodes.
		doc.tree.insertNode(currentNode);

		doc.tree.setEditingNode(currentNode);
		
		if (gotoColumn) {
			// Select the node
			doc.tree.clearSelection();
			
			// Correct the columnNumber if it's larger than the nodes text
			if (columnNumber > currentNode.getValue().length()) {
				columnNumber = currentNode.getValue().length();
			}
			
			// Record the EditingNode and CursorPosition and ComponentFocus
			doc.tree.setCursorPosition(columnNumber);
			doc.setPreferredCaretPosition(columnNumber);
			doc.tree.setComponentFocus(OutlineLayoutManager.TEXT);
		
		} else {
			// Select the node
			doc.tree.setSelectedNodesParent(currentNode.getParent());
			doc.tree.addNodeToSelection(currentNode);
			
			// Record the EditingNode and CursorPosition and ComponentFocus
			doc.tree.setComponentFocus(OutlineLayoutManager.ICON);
		}
		

		// Redraw and Set Focus
		doc.panel.layout.redraw();		
		
		dialog.hide();
	}

	
	private static void cancel() {
		dialog.hide();
	}
}
