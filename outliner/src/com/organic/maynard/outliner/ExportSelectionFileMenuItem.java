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

import java.awt.event.*;
import javax.swing.*;
import org.xml.sax.*;
import com.organic.maynard.util.string.Replace;

public class ExportSelectionFileMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	private FileProtocol protocol = null;

	// Constructors
	public ExportSelectionFileMenuItem(FileProtocol protocol) {
		setProtocol(protocol);
		addActionListener(this);
	}

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		//addActionListener(this);
		
		//setEnabled(false);
	}

	
	// Accessors
	public FileProtocol getProtocol() {
		return this.protocol;
	}
	
	public void setProtocol(FileProtocol protocol) {
		this.protocol = protocol;
		setText(protocol.getName());
	}
	

	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		exportOutlinerDocument(Outliner.getMostRecentDocumentTouched(), getProtocol());
	}

	protected static void exportOutlinerDocument(OutlinerDocument document, FileProtocol protocol) {
		// We need to swap in a new documentSettings object so that the changes don't carry over
		// to the open document, but are conveyed to the export. We'll put the real object back
		// when we're done.
		DocumentSettings oldSettings = document.settings;
		DocumentInfo oldDocInfo = document.getDocumentInfo();
		
		DocumentSettings newSettings = new DocumentSettings(document);
		DocumentInfo newDocInfo = new DocumentInfo();
		
		document.settings = newSettings;
		document.setDocumentInfo(newDocInfo);

		// We also need to swap out the tree with a new tree that just contains the current selection
		TreeContext newTree = new TreeContext();
		TreeContext oldTree = document.tree;

		if (document.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
			Node node = document.tree.getEditingNode();
			
			int cursor = document.tree.getCursorPosition();
			int mark = document.tree.getCursorMarkPosition();
			int startIndex = Math.min(cursor,mark);
			int endIndex = Math.max(cursor,mark);
			
			String text = node.getValue().substring(startIndex, endIndex);
			
			newTree.getRootNode().getFirstChild().setValue(text);
		} else {
			Node root = newTree.getRootNode();
			
			root.removeChild(root.getFirstChild());
			
			for (int i = 0; i < document.tree.selectedNodes.size(); i++) {
				Node node = document.tree.selectedNodes.get(i).cloneClean();
				newTree.getRootNode().appendChild(node);				
			}
		}

		document.tree = newTree;

		if (!protocol.selectFileToSave(document, FileProtocol.EXPORT)) {
			return;
		}
		
		FileMenu.exportFile(document.getDocumentInfo().getPath(), document, protocol);

		// Swap it back the settings
		document.settings = oldSettings;
		document.setDocumentInfo(oldDocInfo);
		document.tree = oldTree;
	}
}