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
import java.awt.Window;
import java.awt.datatransfer.*;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;

public class FindReplaceFrame extends JInternalFrame implements ActionListener, KeyListener {
	
	static final int MIN_WIDTH = 350;
	static final int MIN_HEIGHT = 300;

	static final int INITIAL_WIDTH = 350;
	static final int INITIAL_HEIGHT = 300;
        	
	// Button Text and Other Copy
	public static final String FIND = "Find";
	public static final String REPLACE = "Replace";
	public static final String REPLACE_ALL = "Replace All";

	public static final String START_AT_TOP = "Start at top";
	public static final String WRAP_ARROUND = "Wrap around";
	public static final String SELECTION_ONLY = "Selection only";
	public static final String IGNORE_CASE = "Ignore case";

	// Define Fields and Buttons
	public static final JCheckBox CHECKBOX_START_AT_TOP = new JCheckBox(START_AT_TOP);
	public static final JCheckBox CHECKBOX_WRAP_AROUND = new JCheckBox(WRAP_ARROUND);
	public static final JCheckBox CHECKBOX_SELECTION_ONLY = new JCheckBox(SELECTION_ONLY);
	public static final JCheckBox CHECKBOX_IGNORE_CASE = new JCheckBox(IGNORE_CASE);
	
	public static final JButton BUTTON_FIND = new JButton(FIND);
	public static final JButton BUTTON_REPLACE = new JButton(REPLACE);
	public static final JButton BUTTON_REPLACE_ALL = new JButton(REPLACE_ALL);

	public static final JLabel LABEL_FIND = new JLabel(FIND);
	public static final JTextArea TEXTAREA_FIND = new JTextArea();

	public static final JLabel LABEL_REPLACE = new JLabel(REPLACE);
	public static final JTextArea TEXTAREA_REPLACE = new JTextArea();
	
	static {
		TEXTAREA_FIND.setName(FIND);
		TEXTAREA_FIND.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		TEXTAREA_FIND.setLineWrap(true);
		TEXTAREA_FIND.setMargin(new Insets(1,3,1,3));
	
		TEXTAREA_REPLACE.setName(REPLACE);
		TEXTAREA_REPLACE.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		TEXTAREA_REPLACE.setLineWrap(true);
		TEXTAREA_REPLACE.setMargin(new Insets(1,3,1,3));
		
		disableButtons();
		
		CHECKBOX_START_AT_TOP.setContentAreaFilled(false);
		CHECKBOX_WRAP_AROUND.setContentAreaFilled(false);
		CHECKBOX_SELECTION_ONLY.setContentAreaFilled(false);
		CHECKBOX_IGNORE_CASE.setContentAreaFilled(false);
	}
	
	// Static Methods
	public static void enableButtons() {
		BUTTON_FIND.setEnabled(true);
		BUTTON_REPLACE.setEnabled(true);
		BUTTON_REPLACE_ALL.setEnabled(true);
	}

	public static void disableButtons() {
		BUTTON_FIND.setEnabled(false);
		BUTTON_REPLACE.setEnabled(false);
		BUTTON_REPLACE_ALL.setEnabled(false);
	}
		
	// The Constructor
	public FindReplaceFrame() {
		super("Find/Replace",true,true,false,false);
		
		Outliner.desktop.add(this, JLayeredPane.PALETTE_LAYER);

		// Set the Component & Window Listeners
		addInternalFrameListener(new FindReplaceFrameWindowMonitor());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			
		// Create the Layout
		restoreWindowToInitialSize();
		setLocation(5,5);
		setBackground(new Color(198,198,198));
		
		
		// Try to get rid of the icon in the frame header.
		setFrameIcon(null);

		setVisible(false);

		
		// Define the options Box
		Box optionsBox = Box.createHorizontalBox();
			
			// Scope Options
			Box scopeOptionsBox = Box.createVerticalBox();
			scopeOptionsBox.add(CHECKBOX_START_AT_TOP);
			scopeOptionsBox.add(CHECKBOX_WRAP_AROUND);
			scopeOptionsBox.add(CHECKBOX_SELECTION_ONLY);
			
			// Match Options
			Box matchOptionsBox = Box.createVerticalBox();
			matchOptionsBox.add(CHECKBOX_IGNORE_CASE);
			
			// Put it all together
			optionsBox.add(scopeOptionsBox);
			optionsBox.add(matchOptionsBox);

		// Set the default button.
		getRootPane().setDefaultButton(BUTTON_FIND);

		// Define FindReplace Box
		TEXTAREA_FIND.addKeyListener(this);
		TEXTAREA_REPLACE.addKeyListener(this);
		
		Box findReplaceBox = Box.createVerticalBox();
		findReplaceBox.add(LABEL_FIND);
		JScrollPane findScrollPane = new JScrollPane(TEXTAREA_FIND);
		findReplaceBox.add(findScrollPane);
		findReplaceBox.add(Box.createVerticalStrut(10));
		findReplaceBox.add(LABEL_REPLACE);
		JScrollPane replaceScrollPane = new JScrollPane(TEXTAREA_REPLACE);
		findReplaceBox.add(replaceScrollPane);
		findReplaceBox.add(Box.createVerticalStrut(10));
		findReplaceBox.add(optionsBox);
		
		// Define Button Box
		BUTTON_FIND.addActionListener(this);
		BUTTON_REPLACE.addActionListener(this);
		BUTTON_REPLACE_ALL.addActionListener(this);

		Box buttonBox = Box.createVerticalBox();
		buttonBox.add(new JLabel(" "));
		buttonBox.add(BUTTON_FIND);
		buttonBox.add(Box.createVerticalStrut(5));
		buttonBox.add(BUTTON_REPLACE);
		buttonBox.add(Box.createVerticalStrut(5));
		buttonBox.add(BUTTON_REPLACE_ALL);
		
		// Put it all together
		Box mainBox = Box.createHorizontalBox();
		mainBox.add(Box.createHorizontalStrut(5));
		mainBox.add(findReplaceBox);
		mainBox.add(Box.createHorizontalStrut(5));
		mainBox.add(buttonBox);
		mainBox.add(Box.createHorizontalStrut(5));
		
		getContentPane().add(mainBox, BorderLayout.CENTER);

		//getContentPane().add(findReplaceBox, BorderLayout.CENTER);
		//getContentPane().add(buttonBox, BorderLayout.EAST);
		//getContentPane().add(optionsBox, BorderLayout.SOUTH);
	}

	public void restoreWindowToInitialSize() {
		setSize(INITIAL_WIDTH,INITIAL_HEIGHT);
	}
	
	// KeyListener Interface
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			JTextArea text = (JTextArea) e.getSource();
			
			BUTTON_FIND.doClick(100);
			
			e.consume();
			return;
		}
		
		if (e.getKeyChar() == KeyEvent.VK_TAB) {
			JTextArea text = (JTextArea) e.getSource();
			
			if (text.getName().equals(FIND)) {
				TEXTAREA_REPLACE.requestFocus();
			} else if (text.getName().equals(REPLACE)) {
				TEXTAREA_FIND.requestFocus();
			}
			
			e.consume();
			return;
		}
	}

	public void keyTyped(KeyEvent e) {}
	
	public void keyReleased(KeyEvent e) {}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(FIND)) {
			find(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(REPLACE)) {
			replace(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(REPLACE_ALL)) {
			replace_all(Outliner.getMostRecentDocumentTouched());
		}
	}
	
	private void find(OutlinerDocument doc) {
		NodeRangePair location = findLocation(doc);
		
		if (location != null) {
			// Shorthand
			TreeContext tree = doc.tree;
			OutlineLayoutManager layout = doc.panel.layout;

			// Insert the node into the visible nodes and clear the selection.
			tree.insertNode(location.node);
			tree.clearSelection();
			
			// Record the EditingNode and CursorPosition
			tree.setEditingNode(location.node);
			tree.setCursorPosition(location.endIndex);
			tree.setCursorMarkPosition(location.startIndex);
			tree.setComponentFocus(OutlineLayoutManager.TEXT);
			
			// Update Preferred Caret Position
			doc.setPreferredCaretPosition(location.endIndex);
			
			// Freeze Undo Editing
			UndoableEdit.freezeUndoEdit(location.node);
			
			// Bring the window to the front
			try {
				doc.setSelected(true);
			} catch (java.beans.PropertyVetoException pve) {
				pve.printStackTrace();
			}

			// Redraw and Set Focus
			layout.draw(location.node,OutlineLayoutManager.TEXT);
		}
	}

	private void replace(OutlinerDocument doc) {
		NodeRangePair location = findLocation(doc);
		
		if (location != null) {
			// Shorthand
			TreeContext tree = doc.tree;
			OutlineLayoutManager layout = doc.panel.layout;

			// Create the undoable
			int difference = TEXTAREA_REPLACE.getText().length() - TEXTAREA_FIND.getText().length();

			String oldText = location.node.getValue();
			String newText = oldText.substring(0,location.startIndex) + TEXTAREA_REPLACE.getText() + oldText.substring(location.endIndex,oldText.length()); //
			int oldPosition = location.endIndex;
			int newPosition = location.endIndex + difference;
			doc.undoQueue.add(new UndoableEdit(location.node,oldText,newText,oldPosition,newPosition,oldPosition,location.startIndex));

			// Update the model
			location.node.setValue(newText);
			
			// Insert the node into the visible nodes and clear the selection.
			tree.insertNode(location.node);
			tree.clearSelection();
			
			// Record the EditingNode and CursorPosition
			tree.setEditingNode(location.node);
			tree.setCursorPosition(location.endIndex + difference);
			tree.setCursorMarkPosition(location.startIndex);
			tree.setComponentFocus(OutlineLayoutManager.TEXT);
			
			// Update Preferred Caret Position
			doc.setPreferredCaretPosition(location.endIndex);
			
			// Freeze Undo Editing
			UndoableEdit.freezeUndoEdit(location.node);

			// Bring the window to the front
			try {
				doc.setSelected(true);
			} catch (java.beans.PropertyVetoException pve) {
				pve.printStackTrace();
			}
			
			// Redraw and Set Focus
			layout.draw(location.node,OutlineLayoutManager.TEXT);
		}
	}

	private void replace_all(OutlinerDocument doc) {
		int count = 0;

		String textToMatch = TEXTAREA_FIND.getText();
		if (textToMatch.equals("")) {return;}
		
		CompoundUndoableEdit undoable = new CompoundUndoableEdit(doc.tree);
		boolean undoableAdded = false;
		
		if (CHECKBOX_SELECTION_ONLY.isSelected()) {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				if (doc.tree.getCursorPosition() == doc.tree.getCursorMarkPosition()) {
					// No selection, so return.
					return;
				} else {
					int cursor = doc.tree.getCursorPosition();
					int mark = doc.tree.getCursorMarkPosition();

					Node nodeStart = doc.tree.getEditingNode();
					int cursorStart = Math.min(cursor,mark);
					Node nodeEnd = doc.tree.getEditingNode();
					int cursorEnd = Math.max(cursor,mark);			
		
					while (true) {
						//System.out.println("range: " + cursorStart + " : " + cursorEnd);
						if ((nodeStart == nodeEnd) && (cursorStart == cursorEnd)) {break;}
						NodeRangePair location = findText(nodeStart,cursorStart,nodeEnd,cursorEnd,textToMatch,false,true);
						
						if (location == null) {
							if (count == 0) {
								return;
							} else {
								break;
							}
						}
						if (location.loopedOver) {break;}
						
						if (!undoableAdded) {
							doc.undoQueue.add(undoable);
							undoableAdded = true;
						}
						
						// Replace the Text
						String oldText = location.node.getValue();
						String newText = oldText.substring(0,location.startIndex) + TEXTAREA_REPLACE.getText() + oldText.substring(location.endIndex,oldText.length()); //
						location.node.setValue(newText);
						
						// Add the primitive undoable
						undoable.addPrimitive(new PrimitiveUndoableEdit(location.node,oldText,newText));
						
						// Setup for next replacement
						int difference = TEXTAREA_REPLACE.getText().length() - TEXTAREA_FIND.getText().length();
		
						if (nodeEnd == location.node) {
							cursorEnd += difference;
						}
						nodeStart = location.node;
						cursorStart = location.endIndex + difference;
						
						count++;
					}
					
					// Adjust cursor and mark for new selection.
					doc.tree.setCursorPosition(cursorEnd);
					doc.tree.setCursorMarkPosition(Math.min(cursor,mark));
						
				}
			} else {
				for (int i = 0; i < doc.tree.selectedNodes.size(); i++) {					
					Node nodeStart = (Node) doc.tree.selectedNodes.get(i);
					int cursorStart = 0;
					Node nodeEnd = nodeStart.getLastDecendent();
					int cursorEnd = nodeEnd.getValue().length();
		
					while (true) {
						//System.out.println("range: " + cursorStart + " : " + cursorEnd);
						if ((nodeStart == nodeEnd) && (cursorStart == cursorEnd)) {break;}
						NodeRangePair location = findText(nodeStart,cursorStart,nodeEnd,cursorEnd,textToMatch,false,false);
						
						if (location == null) {
							break;
						}
						if (location.loopedOver) {break;}
						
						if (!undoableAdded) {
							doc.undoQueue.add(undoable);
							undoableAdded = true;
						}
						
						// Replace the Text
						String oldText = location.node.getValue();
						String newText = oldText.substring(0,location.startIndex) + TEXTAREA_REPLACE.getText() + oldText.substring(location.endIndex,oldText.length()); //
						location.node.setValue(newText);
						
						// Add the primitive undoable
						undoable.addPrimitive(new PrimitiveUndoableEdit(location.node,oldText,newText));
						
						// Setup for next replacement
						int difference = TEXTAREA_REPLACE.getText().length() - TEXTAREA_FIND.getText().length();
		
						if (nodeEnd == location.node) {
							cursorEnd += difference;
						}
						nodeStart = location.node;
						cursorStart = location.endIndex + difference;
						
						count++;
					}					
					
				}
			}	
		} else {
			Node nodeStart = doc.tree.rootNode.getFirstChild();
			int cursorStart = 0;
			Node nodeEnd = doc.tree.rootNode.getLastDecendent();
			int cursorEnd = nodeEnd.getValue().length();			

			while (true) {
				//System.out.println("range: " + cursorStart + " : " + cursorEnd);
				NodeRangePair location = findText(nodeStart,cursorStart,nodeEnd,cursorEnd,textToMatch,false,false);
				
				if (location == null) {
					if (count == 0) {
						return;
					} else {
						break;
					}
				}
				if (location.loopedOver) {break;}
				
				if (!undoableAdded) {
					doc.undoQueue.add(undoable);
					undoableAdded = true;
				}
				
				// Replace the Text
				String oldText = location.node.getValue();
				String newText = oldText.substring(0,location.startIndex) + TEXTAREA_REPLACE.getText() + oldText.substring(location.endIndex,oldText.length()); //
				location.node.setValue(newText);
				
				// Add the primitive undoable
				undoable.addPrimitive(new PrimitiveUndoableEdit(location.node,oldText,newText));
				
				// Setup for next replacement
				int difference = TEXTAREA_REPLACE.getText().length() - TEXTAREA_FIND.getText().length();

				if (nodeEnd == location.node) {
					cursorEnd += difference;
				}
				nodeStart = location.node;
				cursorStart = location.endIndex + difference;
				
				count++;
			}
		}
		
		if (count == 0) {return;}

		// Bring the window to the front
		try {
			doc.setSelected(true);
		} catch (java.beans.PropertyVetoException pve) {
			pve.printStackTrace();
		}
		
		// Redraw and Set Focus
		doc.panel.layout.draw(doc.tree.getEditingNode(),doc.tree.getComponentFocus());
		
		// Popup a dialogue with the replacement count.
		String replacementText = "replacements";
		if (count == 1) {
			replacementText = "replacement";
		}
		JOptionPane.showMessageDialog(doc, "" + count + " " + replacementText + " made.");
	}
	
	private NodeRangePair findLocation (OutlinerDocument doc) {
		NodeRangePair location = null;
		
		// Match Value
		String textToMatch = TEXTAREA_FIND.getText();
		
		if (textToMatch.equals("")) {
			return null;
		}

		if (CHECKBOX_SELECTION_ONLY.isSelected()) {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				if (doc.tree.getCursorPosition() == doc.tree.getCursorMarkPosition()) {
					// No selection, so return.
					return null;
				} else {
					Node node = doc.tree.getEditingNode();
					int cursor = doc.tree.getCursorPosition();
					int mark = doc.tree.getCursorMarkPosition();
					
					location = findText(node,Math.min(cursor,mark),node,Math.max(cursor,mark),textToMatch,false,true);
				}
			} else {
				for (int i = 0; i < doc.tree.selectedNodes.size(); i++) {
					// Record the Insert in the undoable
					Node nodeStart = (Node) doc.tree.selectedNodes.get(i);
					int cursorStart = 0;
					Node nodeEnd = nodeStart.getLastDecendent();
					int cursorEnd = nodeEnd.getValue().length();
					
					location = findText(nodeStart,cursorStart,nodeEnd,cursorEnd,textToMatch,false,false);
					
					if (location != null) {
						break;
					}
				}
			}
		} else {
			// End Values
			Node nodeEnd = doc.tree.getEditingNode();
			int cursorEnd = doc.tree.getCursorPosition();

			// Start Values
			Node nodeStart = null;
			int cursorStart = 0;
	
			if (CHECKBOX_START_AT_TOP.isSelected()) {
				nodeStart = doc.tree.rootNode.getFirstChild();
				nodeEnd = doc.tree.rootNode.getLastChild();
				cursorStart = 0;
			} else {
				nodeStart = doc.tree.getEditingNode();
				cursorStart = doc.tree.getCursorPosition();
			}
			
			if (nodeStart.isSelected()) {
				cursorStart = 0;
				cursorEnd = 0;
			}
			
			location = findText(nodeStart,cursorStart,nodeEnd,cursorEnd,textToMatch,false,false);
		}
		return location;
	}
	
	private NodeRangePair findText(Node startNode, int start, Node endNode, int end, String match, boolean loopedOver, boolean done) {
		String text = startNode.getValue();
		
		// Find the match
		int matchStart = -1;
		if (startNode == endNode) {
			if (end > start) {
				matchStart = matchText(text.substring(start,end),match);
				done = true;
			} else {
				matchStart = matchText(text.substring(start,text.length()),match);
			}
		} else {
			matchStart = matchText(text.substring(start,text.length()),match);
		}
		
		if (matchStart != -1) {
			matchStart += start;
			int matchEnd = matchStart + match.length();
			return new NodeRangePair(startNode,matchStart,matchEnd,loopedOver);
		}
		
		if (done) {
			return null;
		}
		
		// No match found, so move on to the next node.
		Node nextNodeToSearch = startNode.nextNode();
		if (nextNodeToSearch.isRoot()) {
			if (!CHECKBOX_WRAP_AROUND.isSelected()) {
				return null;
			}
			nextNodeToSearch = nextNodeToSearch.nextNode();
			loopedOver = true;
		}
		if (endNode == nextNodeToSearch) {
			return findText(nextNodeToSearch,0,endNode,end,match,loopedOver,true);
		} else {
			return findText(nextNodeToSearch,0,endNode,end,match,loopedOver,false);
		}
	}
	
	private int matchText(String text, String match) {
		if (CHECKBOX_IGNORE_CASE.isSelected()) {
			text = text.toLowerCase();
			match = match.toLowerCase();
			return text.indexOf(match);
		} else {
			return text.indexOf(match);
		}
	}
}