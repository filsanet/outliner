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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class FileMenu extends AbstractOutlinerMenu implements ActionListener {

	public static final String FILE_NEW = "New";
	public static final String FILE_OPEN = "Open...";
	public static final String FILE_OPEN_RECENT = "Open Recent";
	public static final String FILE_SAVE = "Save";
	public static final String FILE_SAVE_ALL = "Save All";
	public static final String FILE_SAVE_AS = "Save As...";
	public static final String FILE_REVERT = "Revert";
	public static final String FILE_CLOSE = "Close";
	public static final String FILE_CLOSE_ALL = "Close All";
	public static final String FILE_QUIT = "Quit";

	public JMenuItem FILE_NEW_ITEM = new JMenuItem(FILE_NEW);
	public JMenuItem FILE_OPEN_ITEM = new JMenuItem(FILE_OPEN);
	public RecentFilesList FILE_OPEN_RECENT_MENU;
	// Seperator	
	public JMenuItem FILE_SAVE_ITEM = new JMenuItem(FILE_SAVE);
	public JMenuItem FILE_SAVE_ALL_ITEM = new JMenuItem(FILE_SAVE_ALL);
	public JMenuItem FILE_SAVE_AS_ITEM = new JMenuItem(FILE_SAVE_AS);
	public JMenuItem FILE_REVERT_ITEM = new JMenuItem(FILE_REVERT);
	// Seperator	
	public JMenuItem FILE_CLOSE_ITEM = new JMenuItem(FILE_CLOSE);
	public JMenuItem FILE_CLOSE_ALL_ITEM = new JMenuItem(FILE_CLOSE_ALL);
	// Seperator	
	public JMenuItem FILE_QUIT_ITEM = new JMenuItem(FILE_QUIT);
	
	// The Constructors
	public FileMenu() {
		super("File");

		FILE_NEW_ITEM.setAccelerator(KeyStroke.getKeyStroke('N', Event.CTRL_MASK, false));
		FILE_NEW_ITEM.addActionListener(this);
		add(FILE_NEW_ITEM);

		FILE_OPEN_ITEM.setAccelerator(KeyStroke.getKeyStroke('O', Event.CTRL_MASK, false));
		FILE_OPEN_ITEM.addActionListener(this);
		add(FILE_OPEN_ITEM);

		FILE_OPEN_RECENT_MENU = new RecentFilesList(FILE_OPEN_RECENT,Outliner.getMostRecentDocumentTouched());
		add(FILE_OPEN_RECENT_MENU);

		insertSeparator(3);

		FILE_SAVE_ITEM.setAccelerator(KeyStroke.getKeyStroke('S', Event.CTRL_MASK, false));
		FILE_SAVE_ITEM.addActionListener(this);
		add(FILE_SAVE_ITEM);

		FILE_SAVE_ALL_ITEM.addActionListener(this);
		add(FILE_SAVE_ALL_ITEM);

		FILE_SAVE_AS_ITEM.addActionListener(this);
		add(FILE_SAVE_AS_ITEM);

		FILE_REVERT_ITEM.addActionListener(this);
		add(FILE_REVERT_ITEM);

		insertSeparator(8);

		FILE_CLOSE_ITEM.setAccelerator(KeyStroke.getKeyStroke('W', Event.CTRL_MASK, false));
		FILE_CLOSE_ITEM.addActionListener(this);
		add(FILE_CLOSE_ITEM);

		FILE_CLOSE_ALL_ITEM.addActionListener(this);
		add(FILE_CLOSE_ALL_ITEM);

		insertSeparator(11);

		FILE_QUIT_ITEM.setAccelerator(KeyStroke.getKeyStroke('Q', Event.CTRL_MASK, false));
		FILE_QUIT_ITEM.addActionListener(this);
		add(FILE_QUIT_ITEM);
	}

	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(FILE_NEW)) {
			newOutlinerDocument();
		} else if (e.getActionCommand().equals(FILE_SAVE)) {
			saveOutlinerDocument(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(FILE_SAVE_ALL)) {
			saveAllOutlinerDocuments();
		} else if (e.getActionCommand().equals(FILE_SAVE_AS)) {
			saveAsOutlinerDocument(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(FILE_REVERT)) {
			revertOutlinerDocument(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(FILE_OPEN)) {
			openOutlinerDocument(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(FILE_CLOSE)) {
			closeOutlinerDocument(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(FILE_CLOSE_ALL)) {
			closeAllOutlinerDocuments();
		} else if (e.getActionCommand().equals(FILE_QUIT)) {
			quit();
		}
	}

	// File Menu Methods
	public static void quit() {
		closeAllOutlinerDocuments();

		// Hide Desktop
		Outliner.outliner.setVisible(false);
		Outliner.outliner.dispose();
		
		// Save config and quit
		Preferences.saveConfigFile(Outliner.CONFIG_FILE);
		RecentFilesList.saveConfigFile(Outliner.RECENT_FILES_FILE);
		System.exit(0);
		
	}
	
	protected static void closeOutlinerDocument(OutlinerDocument document) {
		OutlinerWindowMonitor.closeInternalFrame(document);
	}

	protected static void closeAllOutlinerDocuments() {
		for (int i = Outliner.openDocumentCount() - 1; i >= 0; i--) {
			OutlinerWindowMonitor.closeInternalFrame(Outliner.getDocument(i));
		}
	}

	protected static void newOutlinerDocument() {
		new OutlinerDocument("");
	}

	protected static void saveOutlinerDocument(OutlinerDocument document) {
		if (!document.getFileName().equals("")) {
			saveFile(document.getFileName(),document,false);
		} else {
			saveAsOutlinerDocument(document);
		}
	}

	protected static void saveAllOutlinerDocuments() {
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			OutlinerDocument doc = Outliner.getDocument(i);
			if (doc.isFileModified()) {
				saveOutlinerDocument(doc);
			}
		}
	}

	protected static void saveAsOutlinerDocument(OutlinerDocument document) {
		// Setup comboBoxes from settings
		Outliner.chooser.setAccessory(Outliner.saveAccessory);
		Outliner.lineEndComboBox.setSelectedItem(document.settings.lineEnd.cur);
		Outliner.encodingComboBox.setSelectedItem(document.settings.saveEncoding.cur);

		int option = Outliner.chooser.showSaveDialog(document);
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = Outliner.chooser.getSelectedFile().getPath();
			if (!Outliner.isFileNameUnique(filename) && (!filename.equals(document.getFileName()))) {
				JOptionPane.showMessageDialog(document, "The file: " + filename + " is already in use.");
				return;
			}
			
			// Pull Preference Values from the file chooser
			String lineEnd = (String) Outliner.lineEndComboBox.getSelectedItem();
			String encoding = (String) Outliner.encodingComboBox.getSelectedItem();

			// Update the document settings
			document.settings.lineEnd.def = lineEnd;
			document.settings.lineEnd.cur = lineEnd;
			document.settings.lineEnd.tmp = lineEnd;
			document.settings.saveEncoding.def = encoding;
			document.settings.saveEncoding.cur = encoding;
			document.settings.saveEncoding.tmp = encoding;
			
			saveFile(filename,document,true);
		}
	}
	
	protected static void openOutlinerDocument(OutlinerDocument document) {
		// Setup comboBoxes from global settings
		Outliner.chooser.setAccessory(Outliner.openAccessory);
		Outliner.lineEndComboBox.setSelectedItem(Preferences.LINE_END.cur);
		Outliner.openEncodingComboBox.setSelectedItem(Preferences.OPEN_ENCODING.cur);
		
		int option = Outliner.chooser.showOpenDialog(document);
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = Outliner.chooser.getSelectedFile().getPath();
			if (!Outliner.isFileNameUnique(filename)) {
				JOptionPane.showMessageDialog(document, "The file: " + filename + " is already open.");
				return;
			}
			openFile(filename,(String) Outliner.openEncodingComboBox.getSelectedItem());
		}
	}

	protected static void revertOutlinerDocument(OutlinerDocument document) {
		int result = JOptionPane.showConfirmDialog(document, "Revert File? All Changes will be lost.","",JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			revertFile(document.getFileName(),document);
		} else if (result == JOptionPane.NO_OPTION) {
			return;
		}
	}

	protected static void saveFile(String filename, OutlinerDocument document, boolean saveAs) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filename);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream,document.settings.saveEncoding.cur);
			
			outputStreamWriter.write(document.tree.rootNode.depthPaddedValue(Preferences.platformToLineEnding(document.settings.lineEnd.cur)));
			outputStreamWriter.flush();
			outputStreamWriter.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(document, "Could not save file: " + Outliner.chooser.getSelectedFile().getPath() + " because of: " + e);
			return;
		}

		// Stop collecting text edits into the current undoable.
		UndoableEdit.freezeUndoEdit(document.tree.getEditingNode());
		
		// Update the Recent File List
		if (saveAs && !document.getFileName().equals(filename)) {
			RecentFilesList.addFileNameToList(filename,document.settings.saveEncoding.cur);
		}

		document.setFileName(filename);
		document.setTitle(filename);
		document.setFileModified(false);

		// Update the Window Menu
		WindowMenu.updateWindow(document);
	}
		
	protected static void openFile(String filename, String encoding) {
		String text = loadFile(filename,encoding);		
		if (text == null) {return;}

		// Create a new document
		OutlinerDocument newDoc = new OutlinerDocument(filename);
		
		newDoc.settings.syncPrefs();
		newDoc.settings.saveEncoding.def = encoding;
		newDoc.settings.saveEncoding.cur = encoding;
		newDoc.settings.saveEncoding.tmp = encoding;
		newDoc.settings.useDocumentSettings = true;

		// Shorthand
		TreeContext tree = newDoc.tree;
		
		// Clear current selection
		tree.clearSelection();
		
		// Clear the VisibleNodeCache
		tree.visibleNodes.clear();
		
		// Swap in the new tree
		tree.rootNode = PadSelection.pad(text, tree, 0,Preferences.LINE_END_UNIX);
		tree.rootNode.setExpandedClean(true);
		
		// Insert nodes into the VisibleNodes Cache
		for (int i = 0; i < tree.rootNode.numOfChildren(); i++) {
			tree.addNode(tree.rootNode.getChild(i));
		}
		
		// Update the menuBar
		newDoc.setFileName(filename);
		newDoc.setFileModified(false);
		newDoc.setTitle(filename);

		// Move it to the bottom of the recent files list
		RecentFilesList.updateFileNameInList(filename, filename, encoding);

		// Record the current location
		tree.setEditingNode((Node) tree.visibleNodes.get(0));
		tree.setCursorPosition(0);
		tree.setComponentFocus(outlineLayoutManager.TEXT);
		
		// Redraw
		outlineLayoutManager layout = newDoc.panel.layout;
		layout.setNodeToDrawFrom((Node) tree.visibleNodes.get(0),0);
		layout.draw((Node) tree.visibleNodes.get(0), outlineLayoutManager.TEXT);
	}

	protected static void revertFile(String filename, OutlinerDocument document) {
		String text = loadFile(filename,document.settings.saveEncoding.cur);
		if (text == null) {return;}

		// Shorthand
		TreeContext tree = document.tree;
		
		// Clear current selection
		tree.clearSelection();
		
		// Clear the VisibleNodeCache
		tree.visibleNodes.clear();
		
		// Clear the UndoQueue
		document.undoQueue.clear();
		
		// Swap in the new tree
		tree.rootNode = PadSelection.pad(text, tree, 0,Preferences.LINE_END_UNIX);
		tree.rootNode.setExpandedClean(true);
		
		// Insert nodes into the VisibleNodes Cache
		for (int i = 0; i < tree.rootNode.numOfChildren(); i++) {
			tree.addNode(tree.rootNode.getChild(i));
		}
		
		// Update the menuBar
		document.setFileName(filename);
		document.setFileModified(false);
		
		// Record the current location
		tree.setEditingNode((Node) tree.visibleNodes.get(0));
		tree.setCursorPosition(0);
		tree.setComponentFocus(outlineLayoutManager.TEXT);
		
		// Redraw
		outlineLayoutManager layout = document.panel.layout;
		layout.setNodeToDrawFrom((Node) tree.visibleNodes.get(0), 0);
		layout.draw((Node) tree.visibleNodes.get(0), outlineLayoutManager.TEXT);
	}

	protected static String loadFile(String filename, String encoding) {
		StringBuffer text = new StringBuffer("");
		try {
			FileInputStream fileInputStream = new FileInputStream(filename);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,encoding);
			BufferedReader buffer = new BufferedReader(inputStreamReader);
			
			boolean eof = false;
			while (!eof) {
				String theLine = buffer.readLine();
				if (theLine == null) {
					eof = true;
				} else {
					text.append(theLine + Preferences.LINE_END_UNIX);
				}
			}
			
			fileInputStream.close();
		} catch (FileNotFoundException fnfe) {
			JOptionPane.showMessageDialog(null, "File Not Found: " + filename);
			
			// Now remove it from the recent file list.
			RecentFilesList.removeFileNameFromList(filename);

			return null;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not create FileReader: " + filename);
			return null;
		}
		return text.toString();
	}
}