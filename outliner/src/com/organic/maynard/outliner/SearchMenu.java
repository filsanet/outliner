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

import org.xml.sax.*;

public class SearchMenu extends AbstractOutlinerMenu implements GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		Outliner.menuBar.searchMenu = this;
	}


	// Misc Methods
	public static void updateSearchMenu(OutlinerDocument doc) {
		JMenuItem item = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.GOTO_MENU_ITEM);
		if (doc == null) {
			item.setEnabled(false);
		} else {
			item.setEnabled(true);
		}
	}
}


public class FindMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// Make the preferences window visible and switch focus to it.
		Outliner.findReplace.setVisible(true);
		FindReplaceFrame.TEXTAREA_FIND.requestFocus();
	}
}


public class GoToMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface	
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);

		setEnabled(false);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlinerDocument doc = Outliner.getMostRecentDocumentTouched();
		
		// Abort if there is no open document.
		if (doc == null) {
			return;
		}
		
		int currentLineNumber = doc.tree.getEditingNode().getLineNumber();
		
		int lineNumber = 1;
		while (true) {
			String lineNumberString = (String) JOptionPane.showInputDialog(
				Outliner.outliner, 
				"Enter a line number.", 
				"Goto Line", 
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				null, 
				"" + currentLineNumber
			);
			
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
}
