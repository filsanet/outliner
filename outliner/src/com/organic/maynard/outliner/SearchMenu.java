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

import javax.swing.*;

public class SearchMenu extends AbstractOutlinerMenu implements ActionListener {

	// Copy Used
	private static final String MENU_TITLE = "Search";
	
	private static final String SEARCH_GOTO_LINE = "Go to Line...";
	private static final String SEARCH_FIND = "Find/Replace...";


	// The MenuItems.
	public JMenuItem SEARCH_FIND_ITEM = new JMenuItem(SEARCH_FIND);
	// Seperator	
	public JMenuItem SEARCH_GOTO_LINE_ITEM = new JMenuItem(SEARCH_GOTO_LINE);


	// The Constructors
	public SearchMenu() {
		super(MENU_TITLE);

		SEARCH_FIND_ITEM.setAccelerator(KeyStroke.getKeyStroke('F', Event.CTRL_MASK, false));
		SEARCH_FIND_ITEM.addActionListener(this);
		add(SEARCH_FIND_ITEM);

		insertSeparator(1);

		SEARCH_GOTO_LINE_ITEM.setAccelerator(KeyStroke.getKeyStroke('G', Event.CTRL_MASK, false));
		SEARCH_GOTO_LINE_ITEM.addActionListener(this);
		SEARCH_GOTO_LINE_ITEM.setEnabled(false);
		add(SEARCH_GOTO_LINE_ITEM);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(SEARCH_FIND)) {
			find();
			
		} else if (e.getActionCommand().equals(SEARCH_GOTO_LINE)) {
			goto_line(Outliner.getMostRecentDocumentTouched());
			
		}
	}


	// Search Menu Methods
	private static void find() {
		// Make the preferences window visible and switch focus to it.
		Outliner.findReplace.setVisible(true);
		FindReplaceFrame.TEXTAREA_FIND.requestFocus();
	}

	private static void goto_line(OutlinerDocument doc) {
		// Abort if there is no open document.
		if (Outliner.getMostRecentDocumentTouched() == null) {
			return;
		}
		
		int lineNumber = 1;
		while (true) {
			String lineNumberString = JOptionPane.showInputDialog("Enter a line number.");
			if (lineNumberString == null) {
				return;
			}
			try {
				lineNumber = Integer.parseInt(lineNumberString);
				if (lineNumber < 1) {
					lineNumber = 1;
				}
				break;
			} catch (NumberFormatException nfe) {}
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
		
		// Insert the node into the visible nodes.
		doc.tree.insertNode(currentNode);
		
		// Select the node
		doc.tree.setSelectedNodesParent(currentNode.getParent());
		doc.tree.addNodeToSelection(currentNode);
		
		// Record the EditingNode and CursorPosition and ComponentFocus
		doc.tree.setEditingNode(currentNode);
		doc.tree.setComponentFocus(outlineLayoutManager.ICON);

		// Redraw and Set Focus
		doc.panel.layout.draw(currentNode, outlineLayoutManager.ICON);
	}


	// Misc Methods
	public static void updateSearchMenu(OutlinerDocument doc) {
		if (doc == null) {
			Outliner.menuBar.searchMenu.SEARCH_GOTO_LINE_ITEM.setEnabled(false);
		} else {
			Outliner.menuBar.searchMenu.SEARCH_GOTO_LINE_ITEM.setEnabled(true);
		}
	}
}