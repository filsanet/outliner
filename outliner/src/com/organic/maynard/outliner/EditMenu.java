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

public class EditMenu extends AbstractOutlinerMenu implements GUITreeComponent {

	// The Constructors
	public EditMenu() {
		super();
	}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		Outliner.menuBar.editMenu = this;
	}


	// Utility Methods
	protected static void fireKeyEvent(OutlinerDocument doc, int keyMask, int keyChar) {
		OutlinerCellRendererImpl textArea = doc.panel.layout.getUIComponent(doc.tree.getEditingNode());
		if (textArea == null) {return;}
		
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), keyMask, keyChar));
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), keyMask, keyChar));
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), keyMask, keyChar));
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), keyMask, keyChar));
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
	
	
	// Misc Methods
	public static void updateEditMenu(OutlinerDocument doc) {
		JMenuItem cutItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.CUT_MENU_ITEM);
		JMenuItem copyItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.COPY_MENU_ITEM);
		JMenuItem pasteItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.PASTE_MENU_ITEM);
		JMenuItem selectAllItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SELECT_ALL_MENU_ITEM);
		JMenuItem editDocumentSettingsItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.EDIT_DOCUMENT_SETTINGS_MENU_ITEM);

		UndoQueue.updateMenuBar(doc);
		if (doc == null) {
			cutItem.setEnabled(false);
			copyItem.setEnabled(false);
			pasteItem.setEnabled(false);
			selectAllItem.setEnabled(false);
			editDocumentSettingsItem.setEnabled(false);
		} else {
			cutItem.setEnabled(true);
			copyItem.setEnabled(true);
			pasteItem.setEnabled(true);
			selectAllItem.setEnabled(true);
			editDocumentSettingsItem.setEnabled(true);
		}
	}
}


public class UndoMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
		
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		Outliner.getMostRecentDocumentTouched().undoQueue.undo();
	}
}


public class RedoMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
		
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		Outliner.getMostRecentDocumentTouched().undoQueue.redo();
	}
}


public class UndoAllMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
		
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		Outliner.getMostRecentDocumentTouched().undoQueue.undoAll();
	}
}


public class RedoAllMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
		
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		Outliner.getMostRecentDocumentTouched().undoQueue.redoAll();
	}
}


public class CutMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		EditMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.CTRL_MASK, KeyEvent.VK_X);
	}
}


public class CopyMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		EditMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.CTRL_MASK, KeyEvent.VK_C);
	}
}


public class PasteMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		EditMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.CTRL_MASK, KeyEvent.VK_V);
	}
}


public class SelectAllMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		EditMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.CTRL_MASK, KeyEvent.VK_A);
	}
}


public class EditDocumentSettingsMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		Outliner.getMostRecentDocumentTouched().settings.show();
	}
}


public class PreferencesMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		((PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME)).setVisible(true);
		PreferencesFrame.BOTTOM_OK.requestFocus();
	}
}


public class MacrosMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		Outliner.macroManager.setVisible(true);
	}
}
