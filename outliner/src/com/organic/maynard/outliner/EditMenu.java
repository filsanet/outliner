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

public class EditMenu extends AbstractOutlinerMenu implements ActionListener {

	public static final String EDIT_UNDO = "Undo";
	public static final String EDIT_REDO = "Redo";
	public static final String EDIT_UNDO_ALL = "Undo All";
	public static final String EDIT_REDO_ALL = "Redo All";
	public static final String EDIT_CUT = "Cut";
	public static final String EDIT_COPY = "Copy";
	public static final String EDIT_PASTE = "Paste";
	public static final String EDIT_SELECT_ALL = "Select All";
	public static final String EDIT_DOCUMENT_SETTINGS = "Document Settings...";
	public static final String EDIT_PREFERENCES = "Preferences...";
	public static final String EDIT_MACROS = "Macros...";

	public JMenuItem EDIT_UNDO_ITEM = new JMenuItem(EDIT_UNDO);
	public JMenuItem EDIT_REDO_ITEM = new JMenuItem(EDIT_REDO);
	// Seperator	
	public JMenuItem EDIT_UNDO_ALL_ITEM = new JMenuItem(EDIT_UNDO_ALL);
	public JMenuItem EDIT_REDO_ALL_ITEM = new JMenuItem(EDIT_REDO_ALL);
	// Seperator	
	public JMenuItem EDIT_CUT_ITEM = new JMenuItem(EDIT_CUT);
	public JMenuItem EDIT_COPY_ITEM = new JMenuItem(EDIT_COPY);	
	public JMenuItem EDIT_PASTE_ITEM = new JMenuItem(EDIT_PASTE);
	// Seperator
	public JMenuItem EDIT_SELECT_ALL_ITEM = new JMenuItem(EDIT_SELECT_ALL);
	// Seperator
	public JMenuItem EDIT_DOCUMENT_SETTINGS_ITEM = new JMenuItem(EDIT_DOCUMENT_SETTINGS);
	public JMenuItem EDIT_PREFERENCES_ITEM = new JMenuItem(EDIT_PREFERENCES);
	public JMenuItem EDIT_MACROS_ITEM = new JMenuItem(EDIT_MACROS);
	
	// The Constructors
	public EditMenu() {
		super("Edit");
			
		EDIT_UNDO_ITEM.setAccelerator(KeyStroke.getKeyStroke('Z', Event.CTRL_MASK, false));
		EDIT_UNDO_ITEM.addActionListener(this);
		EDIT_UNDO_ITEM.setEnabled(false);
		add(EDIT_UNDO_ITEM);

		EDIT_REDO_ITEM.setAccelerator(KeyStroke.getKeyStroke('Z', Event.CTRL_MASK + Event.SHIFT_MASK, false));
		EDIT_REDO_ITEM.addActionListener(this);
		EDIT_REDO_ITEM.setEnabled(false);
		add(EDIT_REDO_ITEM);

		insertSeparator(2);

		EDIT_UNDO_ALL_ITEM.addActionListener(this);
		EDIT_UNDO_ALL_ITEM.setEnabled(false);
		add(EDIT_UNDO_ALL_ITEM);

		EDIT_REDO_ALL_ITEM.addActionListener(this);
		EDIT_REDO_ALL_ITEM.setEnabled(false);
		add(EDIT_REDO_ALL_ITEM);

		insertSeparator(5);

		EDIT_CUT_ITEM.setAccelerator(KeyStroke.getKeyStroke('X', Event.CTRL_MASK, false));
		EDIT_CUT_ITEM.addActionListener(this);
		EDIT_CUT_ITEM.setEnabled(false);
		EDIT_CUT_ITEM.setEnabled(false);
		add(EDIT_CUT_ITEM);

		EDIT_COPY_ITEM.setAccelerator(KeyStroke.getKeyStroke('C', Event.CTRL_MASK, false));
		EDIT_COPY_ITEM.addActionListener(this);
		EDIT_COPY_ITEM.setEnabled(false);
		EDIT_COPY_ITEM.setEnabled(false);
		add(EDIT_COPY_ITEM);

		EDIT_PASTE_ITEM.setAccelerator(KeyStroke.getKeyStroke('V', Event.CTRL_MASK, false));
		EDIT_PASTE_ITEM.addActionListener(this);
		EDIT_PASTE_ITEM.setEnabled(false);
		add(EDIT_PASTE_ITEM);

		insertSeparator(9);

		EDIT_SELECT_ALL_ITEM.setAccelerator(KeyStroke.getKeyStroke('A', Event.CTRL_MASK, false));
		EDIT_SELECT_ALL_ITEM.addActionListener(this);
		EDIT_SELECT_ALL_ITEM.setEnabled(false);
		add(EDIT_SELECT_ALL_ITEM);

		insertSeparator(11);

		EDIT_DOCUMENT_SETTINGS_ITEM.addActionListener(this);
		EDIT_DOCUMENT_SETTINGS_ITEM.setEnabled(false);
		add(EDIT_DOCUMENT_SETTINGS_ITEM);

		EDIT_PREFERENCES_ITEM.addActionListener(this);
		add(EDIT_PREFERENCES_ITEM);

		EDIT_MACROS_ITEM.addActionListener(this);
		add(EDIT_MACROS_ITEM);
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(EDIT_UNDO)) {
			Outliner.getMostRecentDocumentTouched().undoQueue.undo();
		} else if (e.getActionCommand().equals(EDIT_REDO)) {
			Outliner.getMostRecentDocumentTouched().undoQueue.redo();
		} else if (e.getActionCommand().equals(EDIT_UNDO_ALL)) {
			Outliner.getMostRecentDocumentTouched().undoQueue.undoAll();
		} else if (e.getActionCommand().equals(EDIT_REDO_ALL)) {
			Outliner.getMostRecentDocumentTouched().undoQueue.redoAll();
		} else if (e.getActionCommand().equals(EDIT_CUT)) {
			cut(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(EDIT_COPY)) {
			copy(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(EDIT_PASTE)) {
			paste(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(EDIT_SELECT_ALL)) {
			selectAll(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(EDIT_DOCUMENT_SETTINGS)) {
			document_settings(Outliner.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(EDIT_PREFERENCES)) {
			preferences();
		} else if (e.getActionCommand().equals(EDIT_MACROS)) {
			macros();
		}
	}


	public static void updateEditMenu(OutlinerDocument doc) {
		UndoQueue.updateMenuBar(doc);
		if (doc == null) {
			Outliner.menuBar.editMenu.EDIT_CUT_ITEM.setEnabled(false);
			Outliner.menuBar.editMenu.EDIT_COPY_ITEM.setEnabled(false);
			Outliner.menuBar.editMenu.EDIT_PASTE_ITEM.setEnabled(false);
			Outliner.menuBar.editMenu.EDIT_SELECT_ALL_ITEM.setEnabled(false);
			Outliner.menuBar.editMenu.EDIT_DOCUMENT_SETTINGS_ITEM.setEnabled(false);
		} else {
			Outliner.menuBar.editMenu.EDIT_CUT_ITEM.setEnabled(true);
			Outliner.menuBar.editMenu.EDIT_COPY_ITEM.setEnabled(true);
			Outliner.menuBar.editMenu.EDIT_PASTE_ITEM.setEnabled(true);
			Outliner.menuBar.editMenu.EDIT_SELECT_ALL_ITEM.setEnabled(true);
			Outliner.menuBar.editMenu.EDIT_DOCUMENT_SETTINGS_ITEM.setEnabled(true);
		}
	}

	// Edit Menu Methods
	protected static void cut(OutlinerDocument doc) {
		OutlinerCellRendererImpl textArea = doc.panel.layout.getUIComponent(doc.tree.getEditingNode());
		if (textArea == null) {return;}
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_X));
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_X));
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_X));
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_X));
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	protected static void copy(OutlinerDocument doc) {
		OutlinerCellRendererImpl textArea = doc.panel.layout.getUIComponent(doc.tree.getEditingNode());
		if (textArea == null) {return;}
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_C));
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_C));
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_C));
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_C));
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	protected static void paste(OutlinerDocument doc) {
		OutlinerCellRendererImpl textArea = doc.panel.layout.getUIComponent(doc.tree.getEditingNode());
		if (textArea == null) {return;}
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_V));
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_V));
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_V));
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_V));
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	protected static void selectAll(OutlinerDocument doc) {
		OutlinerCellRendererImpl textArea = doc.panel.layout.getUIComponent(doc.tree.getEditingNode());
		if (textArea == null) {return;}
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_A));
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_A));
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_A));
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis() , Event.CTRL_MASK, KeyEvent.VK_A));
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	protected static void preferences() {
		// Make the preferences window visible and switch focus to it.
		Outliner.prefs.setVisible(true);
		PreferencesFrame.BOTTOM_OK.requestFocus();
	}

	protected static void macros() {
		// Make the preferences window visible and switch focus to it.
		Outliner.macroManager.setVisible(true);
	}

	protected static void document_settings(OutlinerDocument document) {
		document.settings.show();
	}
}