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

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.xml.sax.*;

// WebFile
import com.yearahead.io.*;

public class FileMenu extends AbstractOutlinerMenu implements GUITreeComponent {
	
	// The Constructors
	public FileMenu() {
		super();
	}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		Outliner.menuBar.fileMenu = this;
	}

	
	// Utility Methods
	protected static void saveFile(String filename, OutlinerDocument document, boolean saveAs) {
		// Get the file format object
		String fileFormatName = document.settings.saveFormat.cur;
		SaveFileFormat saveFileFormat = Outliner.fileFormatManager.getSaveFormat(fileFormatName);
		if (saveFileFormat == null) {
			JOptionPane.showMessageDialog(document, "An error occurred. Could not save file: " + Outliner.chooser.getSelectedFile().getPath() + " because I couldn't retrieve the file format: " + fileFormatName);
			return;
		}
		
		// Save the file
		DocumentInfo docInfo = new DocumentInfo();
		docInfo.setPath(filename);
		docInfo.setEncodingType(document.settings.saveEncoding.cur);
		docInfo.setLineEnding(document.settings.lineEnd.cur);
		docInfo.setFileFormat(document.settings.saveFormat.cur);
		Rectangle r = document.getNormalBounds();
		docInfo.setWindowTop(r.y);
		docInfo.setWindowLeft(r.x);
		docInfo.setWindowBottom(r.y + r.height);
		docInfo.setWindowRight(r.x + r.width);
		int index = document.tree.visibleNodes.indexOf(document.panel.layout.getNodeToDrawFrom()) + 1;
		docInfo.setVerticalScrollState(index);
		docInfo.getExpandedNodes().clear();
		for (int i = 0; i < document.tree.visibleNodes.size(); i++) {
			Node node = (Node) document.tree.visibleNodes.get(i);
			if (node.isExpanded()) {
				docInfo.addExpandedNodeNum(i);
			}
		}
		
		docInfo.getCommentedNodes().clear();
		boolean commentExists = false;
		
		Node node = document.tree.getRootNode();
		int lineCount = -1;
		while (true) {
			node = node.nextNode();
			lineCount++;
			
			if (node.isRoot()) {
				break;
			}
			
			if (node.isComment()) {
				docInfo.addCommentedNodeNum(lineCount);
				commentExists = true;
			}
		}
		
		if (commentExists && !saveFileFormat.supportsComments()) {
			Object[] options = {"Yes","No"};
			int result = JOptionPane.showOptionDialog(Outliner.outliner,
				"The file format you are saving with: " + fileFormatName + " does not support comments.\nThe document contains commented nodes whose commented status will be lost.\nDo you want to save it anyway?",
				"Confirm Open",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]
			);
			
			if (result == JOptionPane.YES_OPTION) {
				// Do Nothing
			} else if (result == JOptionPane.NO_OPTION) {
				return;
			}
		}
		
		docInfo.setOwnerName(document.settings.ownerName.cur);
		docInfo.setOwnerEmail(document.settings.ownerEmail.cur);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss z");
		if (!Preferences.TIME_ZONE_FOR_SAVING_DATES.cur.equals("")) {
			dateFormat.setTimeZone(TimeZone.getTimeZone(Preferences.TIME_ZONE_FOR_SAVING_DATES.cur));
		}
		String currentDateString = dateFormat.format(new Date());
		if(saveAs) {
			docInfo.setDateCreated(currentDateString);
			docInfo.setDateModified(currentDateString);
		} else {
			docInfo.setDateCreated(document.settings.dateCreated);
			docInfo.setDateModified(currentDateString);
		}
		
		// Save the File
		if (document.hoistStack.isHoisted()) {
			document.hoistStack.temporaryDehoistAll();
			
			// WebFile
			boolean success = false;
			byte[] bytes = saveFileFormat.save(document.tree, docInfo);
			if (Preferences.WEB_FILE_SYSTEM.cur) {
				try {
					success = WebFile.save(Preferences.WEB_FILE_URL.cur, docInfo.getPath(), bytes);
				} catch(IOException x) {
					x.printStackTrace();
					success = false;
				}
			} else {
				success = FileFormatManager.writeFile(docInfo.getPath(), bytes);
			}

			
			
			//boolean success = saveFileFormat.save(document.tree, docInfo);
			if (!success) {
				JOptionPane.showMessageDialog(document, "An error occurred. Could not save file: " + Outliner.chooser.getSelectedFile().getPath());
				return;
			}
			document.hoistStack.temporaryHoistAll();
		} else {

			// WebFile
			boolean success = false;
			byte[] bytes = saveFileFormat.save(document.tree, docInfo);
			if (Preferences.WEB_FILE_SYSTEM.cur) {
				try {
					success = WebFile.save(Preferences.WEB_FILE_URL.cur, docInfo.getPath(), bytes);
				} catch(IOException x) {
					x.printStackTrace();
					success = false;
				}
			} else {
				success = FileFormatManager.writeFile(docInfo.getPath(), bytes);
			}

			//boolean success = saveFileFormat.save(document.tree, docInfo);
			if (!success) {
				JOptionPane.showMessageDialog(document, "An error occurred. Could not save file: " + Outliner.chooser.getSelectedFile().getPath());
				return;
			}
		}					

		// Stop collecting text edits into the current undoable.
		UndoableEdit.freezeUndoEdit(document.tree.getEditingNode());
		
		// Update the Recent File List
		if (saveAs && !document.getFileName().equals(filename)) {
			RecentFilesList.addFileNameToList(docInfo);
		} else {
			RecentFilesList.updateFileNameInList(filename, docInfo);
		}

		document.setFileName(filename);
		document.setTitle(filename);
		document.setFileModified(false);

		// Update the Window Menu
		WindowMenu.updateWindow(document);
	}
		
	protected static void openFile(DocumentInfo docInfo) {
		String filename = docInfo.getPath();
		String encoding = docInfo.getEncodingType();
		String fileFormat = docInfo.getFileFormat();
		
		// Get the file format object
		OpenFileFormat openFileFormat = Outliner.fileFormatManager.getOpenFormat(fileFormat);
		if (openFileFormat == null) {
			JOptionPane.showMessageDialog(Outliner.outliner, "An error occurred. Could not open file: " + filename + " because I couldn't retrieve the file format: " + fileFormat);
			return;
		}
		
		// Load the file
		TreeContext tree = new TreeContext();
		InputStream stream = null;
		if (Preferences.WEB_FILE_SYSTEM.cur) {
			try {
				stream = WebFile.open(Preferences.WEB_FILE_URL.cur, filename);
			} catch(IOException e) {
				JOptionPane.showMessageDialog(Outliner.outliner, "An error occurred. Could not open file: " + filename);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			}
		} else {
			try {
				stream = new FileInputStream(filename);
			} catch (FileNotFoundException fnfe) {
				JOptionPane.showMessageDialog(Outliner.outliner, "An error occurred. File not found: " + filename);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(Outliner.outliner, "An error occurred. Could not open file: " + filename);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			}
		}

		int success = openFileFormat.open(tree, docInfo, stream);
		if (success == OpenFileFormat.FAILURE) {
			JOptionPane.showMessageDialog(Outliner.outliner, "An error occurred. Could not open file: " + filename);
			RecentFilesList.removeFileNameFromList(filename);
			return;
		} else if (success == OpenFileFormat.FAILURE_USER_ABORTED) {
			return;
		}
		
		// Deal with a childless RootNode or an Empty or Null Tree
		if ((tree != null) && (tree.getRootNode() != null) && (tree.getRootNode().numOfChildren() > 0)) {
			// Pass on through, we're OK.
		} else {
			tree = new TreeContext();
		}
		
		
		// Create a new document
		OutlinerDocument newDoc = new OutlinerDocument(filename);
		tree.doc = newDoc;
		newDoc.tree = tree;
		
		// Set bounds
		if (newDoc.isMaximum()) {
			newDoc.setNormalBounds(new Rectangle(docInfo.getWindowLeft(), docInfo.getWindowTop(), docInfo.getWidth(), docInfo.getHeight()));
		} else {
			newDoc.setBounds(docInfo.getWindowLeft(), docInfo.getWindowTop(), docInfo.getWidth(), docInfo.getHeight());
		}
		
		// Update DocumentSettings
		newDoc.settings.syncPrefs();
		newDoc.settings.saveEncoding.def = encoding;
		newDoc.settings.saveEncoding.cur = encoding;
		newDoc.settings.saveEncoding.tmp = encoding;
		newDoc.settings.saveFormat.def = fileFormat;
		newDoc.settings.saveFormat.cur = fileFormat;
		newDoc.settings.saveFormat.tmp = fileFormat;
		newDoc.settings.useDocumentSettings = true;
		
		newDoc.settings.dateCreated = docInfo.getDateCreated();
		newDoc.settings.dateModified = docInfo.getDateModified();

		// Move it to the bottom of the recent files list
		RecentFilesList.updateFileNameInList(filename, docInfo);
		
		setupAndDraw(docInfo, newDoc);

		// Set document as modified if something happened on open
		if (success == OpenFileFormat.SUCCESS_MODIFIED) {
			newDoc.setFileModified(true);
		}
	}

	protected static void revertFile(String filename, OutlinerDocument document) {
		String fileFormat = document.settings.saveFormat.cur;

		// Get the file format object
		OpenFileFormat openFileFormat = Outliner.fileFormatManager.getOpenFormat(fileFormat);
		if (openFileFormat == null) {
			JOptionPane.showMessageDialog(document, "An error occurred. Could not revert file: " + filename + " because I couldn't retrieve the file format: " + fileFormat);
			return;
		}
		
		// Load the file
		TreeContext tree = new TreeContext();
		
		DocumentInfo docInfo = new DocumentInfo();
		docInfo.setPath(filename);
		docInfo.setEncodingType(document.settings.saveEncoding.cur);

		InputStream stream = null;
		if (Preferences.WEB_FILE_SYSTEM.cur) {
			try {
				stream = WebFile.open(Preferences.WEB_FILE_URL.cur, filename);
			} catch(IOException e) {
				JOptionPane.showMessageDialog(Outliner.outliner, "An error occurred. Could not open file: " + filename);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			}
		} else {
			try {
				stream = new FileInputStream(filename);
			} catch (FileNotFoundException fnfe) {
				JOptionPane.showMessageDialog(Outliner.outliner, "An error occurred. File not found: " + filename);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(Outliner.outliner, "An error occurred. Could not open file: " + filename);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			}
		}


		int success = openFileFormat.open(tree, docInfo, stream);
		if (success == OpenFileFormat.FAILURE) {
			RecentFilesList.removeFileNameFromList(filename); // Not really sure this is appropriate.
			return;
		} else if (success == OpenFileFormat.FAILURE_USER_ABORTED) {
			return;
		}

		// Deal with a childless RootNode or an Empty or Null Tree
		if ((tree != null) && (tree.getRootNode() != null) && (tree.getRootNode().numOfChildren() > 0)) {
			// Pass on through, we're OK.
		} else {
			tree = new TreeContext();
		}
		
		// Swap in the new tree
		tree.doc = document;
		document.tree = tree;
		
		// Clear the UndoQueue
		document.undoQueue.clear();
		
		// Clear the HoistStack
		document.hoistStack.clear();

		setupAndDraw(docInfo, document);

		// Set document as modified if something happened on open
		if (success == OpenFileFormat.SUCCESS_MODIFIED) {
			document.setFileModified(true);
		}
	}
	
	private static void setupAndDraw(DocumentInfo docInfo, OutlinerDocument doc) {
		TreeContext tree = doc.tree;
		String filename = docInfo.getPath();
		
		// Clear current selection
		tree.clearSelection();
		
		// Clear the VisibleNodeCache
		tree.visibleNodes.clear();
		
		// Insert nodes into the VisibleNodes Cache
		for (int i = 0; i < tree.rootNode.numOfChildren(); i++) {
			tree.addNode(tree.rootNode.getChild(i));
		}
		
		// Update the menuBar
		doc.setFileName(filename);
		doc.setFileModified(false);
		doc.setTitle(filename);

		// Expand Nodes
		Vector expandedNodes = docInfo.getExpandedNodes();
		for (int i = 0; i < expandedNodes.size(); i++) {
			int nodeNum = ((Integer) expandedNodes.elementAt(i)).intValue();
			try {
				Node node = (Node) doc.tree.visibleNodes.get(nodeNum);
				node.setExpanded(true);
			} catch (Exception e) {
				break;
			}
		}
		
		// Comment Nodes
		Vector commentedNodes = docInfo.getCommentedNodes();
		
		Node node = doc.tree.getRootNode();
		int lineCount = -1;
		int vectorCount = 0;
		try {
			int vectorValue = ((Integer) commentedNodes.get(vectorCount)).intValue();
			while (true) {
				node = node.nextNode();
				lineCount++;
				
				if (node.isRoot()) {
					break;
				}
				
				if (lineCount == vectorValue) {
					node.setComment(true);
					vectorCount++;
					vectorValue = ((Integer) commentedNodes.get(vectorCount)).intValue();
				}
			}
		} catch (Exception e) {
		
		}
		
		// Record the current location
		Node firstVisibleNode;
		int index = -1;
		try {
			index = docInfo.getVerticalScrollState() - 1;
			firstVisibleNode = (Node) tree.visibleNodes.get(index);
		} catch (IndexOutOfBoundsException e) {
			index = 0;
			firstVisibleNode = (Node) tree.visibleNodes.get(0);
		}
		
		// Record Document Settings
		doc.settings.ownerName.cur = docInfo.getOwnerName();
		doc.settings.ownerEmail.cur = docInfo.getOwnerEmail();
		
		tree.setEditingNode(firstVisibleNode);
		tree.setCursorPosition(0);
		tree.setComponentFocus(OutlineLayoutManager.TEXT);
		
		// Redraw
		OutlineLayoutManager layout = doc.panel.layout;
		layout.setNodeToDrawFrom(firstVisibleNode,index);
		layout.draw();
		layout.setFocus(firstVisibleNode, OutlineLayoutManager.TEXT);
	}
}