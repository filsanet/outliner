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
import javax.swing.*;
import org.xml.sax.*;
import com.organic.maynard.util.string.Replace;

// WebFile
import com.yearahead.io.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class FileMenu extends AbstractOutlinerMenu implements GUITreeComponent, JoeReturnCodes {
	
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
		String msg = null;
		if (saveFileFormat == null) {
			msg = GUITreeLoader.reg.getText("error_could_not_save_no_file_format");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, Outliner.chooser.getSelectedFile().getPath());
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_2, fileFormatName);

			JOptionPane.showMessageDialog(document, msg);
			return;
		}
		
		// Initialize DocumentInfo with current document state, prefs and document settings.
		document.setFileName(filename);
		DocumentInfo docInfo = new DocumentInfo();
		docInfo.updateDocumentInfoForDocument(document, saveAs);
		
		document.settings.useDocumentSettings = true;
		
		// Check File Format Support
		boolean commentExists = false;
		boolean editableExists = false;
		boolean moveableExists = false;
		boolean attributesExist = false;
		boolean documentAttributesExist = false;
		
		if (document.tree.getAttributeCount() > 0) {
			documentAttributesExist = true;
		}
		
		Node node = document.tree.getRootNode();
		int lineCount = -1;
		while (true) {
			node = node.nextNode();
			lineCount++;
			
			if (node.isRoot()) {
				break;
			}
			
			if (node.isComment()) {
				commentExists = true;
			}

			if (!node.isEditable()) {
				editableExists = true;
			}

			if (!node.isMoveable()) {
				moveableExists = true;
			}
			
			if (!attributesExist && node.getAttributeCount() > 0) {
				attributesExist = true;
			}
		}

		
		if (commentExists && !saveFileFormat.supportsComments()) {
			msg = GUITreeLoader.reg.getText("error_file_format_does_not_support_comments");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, fileFormatName);
			if (USER_ABORTED == promptUser(msg)) {
				return;
			}
		}

		if (editableExists && !saveFileFormat.supportsEditability()) {
			msg = GUITreeLoader.reg.getText("error_file_format_does_not_support_editability");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, fileFormatName);
			if (USER_ABORTED == promptUser(msg)) {
				return;
			}
		}

		if (moveableExists && !saveFileFormat.supportsMoveability()) {
			msg = GUITreeLoader.reg.getText("error_file_format_does_not_support_moveability");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, fileFormatName);
			if (USER_ABORTED == promptUser(msg)) {
				return;
			}
		}

		if (attributesExist && !saveFileFormat.supportsAttributes()) {
			msg = GUITreeLoader.reg.getText("error_file_format_does_not_support_attributes");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, fileFormatName);
			if (USER_ABORTED == promptUser(msg)) {
				return;
			}
		}

		if (documentAttributesExist && !saveFileFormat.supportsDocumentAttributes()) {
			msg = GUITreeLoader.reg.getText("error_file_format_does_not_support_document_attributes");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, fileFormatName);
			if (USER_ABORTED == promptUser(msg)) {
				return;
			}
		}

		
		// Save the File
		if (document.hoistStack.isHoisted()) {
			document.hoistStack.temporaryDehoistAll(); // So that a hoisted doc will be completely saved.
		}
		
		// WebFile
		byte[] bytes = saveFileFormat.save(document.tree, docInfo);

		boolean success = false;
		if (Preferences.getPreferenceBoolean(Preferences.WEB_FILE_SYSTEM).cur) {
			try {
				success = WebFile.save(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur, docInfo.getPath(), bytes);
			} catch(IOException x) {
				x.printStackTrace();
				success = false;
			}
		} else {
			success = FileFormatManager.writeFile(docInfo.getPath(), bytes);
		}

		if (!success) {
			msg = GUITreeLoader.reg.getText("error_could_not_save_file");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, Outliner.chooser.getSelectedFile().getPath());

			JOptionPane.showMessageDialog(document, msg);
			return;
		}

		if (document.hoistStack.isHoisted()) {
			document.hoistStack.temporaryHoistAll(); // Now that the whole doc was saved, let's put things back the way they were.
		}
							

		// Stop collecting text edits into the current undoable.
		UndoableEdit.freezeUndoEdit(document.tree.getEditingNode());
		
		// Update the Recent File List
		if (saveAs && !document.getFileName().equals(filename)) {
			RecentFilesList.addFileNameToList(docInfo);
		} else {
			RecentFilesList.updateFileNameInList(filename, docInfo);
		}

		//document.setFileName(filename);
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
		
		String msg = null;
		if (openFileFormat == null) {
			msg = GUITreeLoader.reg.getText("error_could_not_open_no_file_format");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_2, fileFormat);

			JOptionPane.showMessageDialog(Outliner.outliner, msg);
			return;
		}
		
		// Load the file
		TreeContext tree = new TreeContext();
		InputStream stream = null;
		if (Preferences.getPreferenceBoolean(Preferences.WEB_FILE_SYSTEM).cur) {
			try {
				stream = WebFile.open(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur, filename);
			} catch(IOException e) {
				msg = GUITreeLoader.reg.getText("error_could_not_open_file");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);

				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			}
		} else {
			try {
				stream = new FileInputStream(filename);
			} catch (FileNotFoundException fnfe) {
				msg = GUITreeLoader.reg.getText("error_file_not_found");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);

				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			} catch (Exception e) {
				msg = GUITreeLoader.reg.getText("error_could_not_open_file");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);

				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			}
		}

		int success = openFileFormat.open(tree, docInfo, stream);
		if (success == FAILURE) {
			msg = GUITreeLoader.reg.getText("error_could_not_open_file");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);

			JOptionPane.showMessageDialog(Outliner.outliner, msg);
			RecentFilesList.removeFileNameFromList(filename);
			return;
		} else if (success == FAILURE_USER_ABORTED) {
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
		//newDoc.settings.syncPrefs();
		newDoc.settings.saveEncoding.def = encoding;
		newDoc.settings.saveEncoding.restoreCurrentToDefault();
		newDoc.settings.saveEncoding.restoreTemporaryToDefault();
		
		newDoc.settings.saveFormat.def = fileFormat;
		newDoc.settings.saveFormat.restoreCurrentToDefault();
		newDoc.settings.saveFormat.restoreTemporaryToDefault();
		
		newDoc.settings.applyFontStyleForComments.def = docInfo.getApplyFontStyleForComments();
		newDoc.settings.applyFontStyleForComments.restoreCurrentToDefault();
		newDoc.settings.applyFontStyleForComments.restoreTemporaryToDefault();

		newDoc.settings.applyFontStyleForEditability.def = docInfo.getApplyFontStyleForEditability();		
		newDoc.settings.applyFontStyleForEditability.restoreCurrentToDefault();
		newDoc.settings.applyFontStyleForEditability.restoreTemporaryToDefault();

		newDoc.settings.applyFontStyleForMoveability.def = docInfo.getApplyFontStyleForMoveability();
		newDoc.settings.applyFontStyleForMoveability.restoreCurrentToDefault();
		newDoc.settings.applyFontStyleForMoveability.restoreTemporaryToDefault();
		
		newDoc.settings.dateCreated = docInfo.getDateCreated();
		newDoc.settings.dateModified = docInfo.getDateModified();

		newDoc.settings.useDocumentSettings = true;

		// Move it to the bottom of the recent files list
		RecentFilesList.updateFileNameInList(filename, docInfo);
		
		setupAndDraw(docInfo, newDoc);

		// Set document as modified if something happened on open
		if (success == SUCCESS_MODIFIED) {
			newDoc.setFileModified(true);
		}
	}

	protected static void revertFile(String filename, OutlinerDocument document) {
		String fileFormat = document.settings.saveFormat.cur;

		// Get the file format object
		OpenFileFormat openFileFormat = Outliner.fileFormatManager.getOpenFormat(fileFormat);
		
		String msg = null;
		if (openFileFormat == null) {
			msg = GUITreeLoader.reg.getText("error_could_not_revert_no_file_format");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_2, fileFormat);

			JOptionPane.showMessageDialog(document, msg);
			return;
		}
		
		// Load the file
		TreeContext tree = new TreeContext();
		
		DocumentInfo docInfo = new DocumentInfo();
		docInfo.setPath(filename);
		docInfo.setEncodingType(document.settings.saveEncoding.cur);

		InputStream stream = null;
		if (Preferences.getPreferenceBoolean(Preferences.WEB_FILE_SYSTEM).cur) {
			try {
				stream = WebFile.open(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur, filename);
			} catch(IOException e) {
				msg = GUITreeLoader.reg.getText("error_could_not_open_file");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);

				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			}
		} else {
			try {
				stream = new FileInputStream(filename);
			} catch (FileNotFoundException fnfe) {
				msg = GUITreeLoader.reg.getText("error_file_not_found");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);

				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			} catch (Exception e) {
				msg = GUITreeLoader.reg.getText("error_could_not_open_file");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);

				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				RecentFilesList.removeFileNameFromList(filename);
				return;
			}
		}


		int success = openFileFormat.open(tree, docInfo, stream);
		if (success == FAILURE) {
			RecentFilesList.removeFileNameFromList(filename); // Not really sure this is appropriate.
			return;
		} else if (success == FAILURE_USER_ABORTED) {
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
		if (success == SUCCESS_MODIFIED) {
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
		ArrayList expandedNodes = docInfo.getExpandedNodes();
		for (int i = 0; i < expandedNodes.size(); i++) {
			int nodeNum = ((Integer) expandedNodes.get(i)).intValue();
			try {
				Node node = doc.tree.visibleNodes.get(nodeNum);
				node.setExpanded(true);
			} catch (Exception e) {
				break;
			}
		}
		
		// Record the current location
		Node firstVisibleNode;
		int index = -1;
		try {
			index = docInfo.getVerticalScrollState() - 1;
			firstVisibleNode = tree.visibleNodes.get(index);
		} catch (IndexOutOfBoundsException e) {
			index = 0;
			firstVisibleNode = tree.visibleNodes.get(0);
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


	// Utility Methods
	private static int promptUser(String msg) {
		String yes = GUITreeLoader.reg.getText("yes");
		String no = GUITreeLoader.reg.getText("no");
		String confirm_save = GUITreeLoader.reg.getText("confirm_save");


		Object[] options = {yes, no};
		int result = JOptionPane.showOptionDialog(Outliner.outliner,
			msg,
			confirm_save,
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]
		);
		
		if (result == JOptionPane.NO_OPTION) {
			return USER_ABORTED;
		} else {
			return SUCCESS;
		}
	}
}