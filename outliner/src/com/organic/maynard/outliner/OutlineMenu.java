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

public class OutlineMenu extends AbstractOutlinerMenu implements ActionListener, GUITreeComponent {

	// Copy Used.
	private static final String OUTLINE_TOGGLE_COMMENT = "Toggle Comment";
	private static final String OUTLINE_TOGGLE_EXPANSION = "Toggle Expansion";
	private static final String OUTLINE_EXPAND_ALL_SUBHEADS = "Expand All Subheads";
	private static final String OUTLINE_EXPAND_EVERYTHING = "Expand Everything";
	private static final String OUTLINE_COLLAPSE_TO_PARENT = "Collapse To Parent";
	private static final String OUTLINE_COLLAPSE_EVERYTHING = "Collape Everything";
	private static final String OUTLINE_MOVE_UP = "Move Up";
	private static final String OUTLINE_MOVE_DOWN = "Move Down";
	private static final String OUTLINE_MOVE_RIGHT = "Move Right";
	private static final String OUTLINE_MOVE_LEFT = "Move Left";
	private static final String OUTLINE_PROMOTE = "Promote";
	private static final String OUTLINE_DEMOTE = "Demote";
	private static final String OUTLINE_MERGE = "Merge";
	private static final String OUTLINE_MERGE_WITH_SPACES = "Merge with Spaces";
	public static final String OUTLINE_HOIST = "Hoist";
	private static final String OUTLINE_DEHOIST = "De-Hoist";
	private static final String OUTLINE_DEHOIST_ALL = "De-Hoist All";


	// The MenuItems.
	public JMenuItem OUTLINE_TOGGLE_COMMENT_ITEM = new JMenuItem(OUTLINE_TOGGLE_COMMENT);
	// Seperator
	public JMenuItem OUTLINE_TOGGLE_EXPANSION_ITEM = new JMenuItem(OUTLINE_TOGGLE_EXPANSION);
	// Seperator
	public JMenuItem OUTLINE_EXPAND_ALL_SUBHEADS_ITEM = new JMenuItem(OUTLINE_EXPAND_ALL_SUBHEADS);
	public JMenuItem OUTLINE_EXPAND_EVERYTHING_ITEM = new JMenuItem(OUTLINE_EXPAND_EVERYTHING);
	// Seperator	
	public JMenuItem OUTLINE_COLLAPSE_TO_PARENT_ITEM = new JMenuItem(OUTLINE_COLLAPSE_TO_PARENT);
	public JMenuItem OUTLINE_COLLAPSE_EVERYTHING_ITEM = new JMenuItem(OUTLINE_COLLAPSE_EVERYTHING);
	// Seperator	
	public JMenuItem OUTLINE_MOVE_UP_ITEM = new JMenuItem(OUTLINE_MOVE_UP);
	public JMenuItem OUTLINE_MOVE_DOWN_ITEM = new JMenuItem(OUTLINE_MOVE_DOWN);
	public JMenuItem OUTLINE_MOVE_LEFT_ITEM = new JMenuItem(OUTLINE_MOVE_LEFT);
	public JMenuItem OUTLINE_MOVE_RIGHT_ITEM = new JMenuItem(OUTLINE_MOVE_RIGHT);
	// Seperator	
	public JMenuItem OUTLINE_PROMOTE_ITEM = new JMenuItem(OUTLINE_PROMOTE);
	public JMenuItem OUTLINE_DEMOTE_ITEM = new JMenuItem(OUTLINE_DEMOTE);
	// Seperator	
	public JMenuItem OUTLINE_MERGE_ITEM = new JMenuItem(OUTLINE_MERGE);
	public JMenuItem OUTLINE_MERGE_WITH_SPACES_ITEM = new JMenuItem(OUTLINE_MERGE_WITH_SPACES);
	// Seperator	
	public JMenuItem OUTLINE_HOIST_ITEM = new JMenuItem(OUTLINE_HOIST);
	public JMenuItem OUTLINE_DEHOIST_ITEM = new JMenuItem(OUTLINE_DEHOIST);
	public JMenuItem OUTLINE_DEHOIST_ALL_ITEM = new JMenuItem(OUTLINE_DEHOIST_ALL);
	
	// The Constructors
	public OutlineMenu() {
		super();

		OUTLINE_TOGGLE_COMMENT_ITEM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0, false));
		OUTLINE_TOGGLE_COMMENT_ITEM.addActionListener(this);
		add(OUTLINE_TOGGLE_COMMENT_ITEM);
			
		insertSeparator(1);

		OUTLINE_TOGGLE_EXPANSION_ITEM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0, false));
		OUTLINE_TOGGLE_EXPANSION_ITEM.addActionListener(this);
		add(OUTLINE_TOGGLE_EXPANSION_ITEM);
			
		insertSeparator(3);

		OUTLINE_EXPAND_ALL_SUBHEADS_ITEM.addActionListener(this);
		add(OUTLINE_EXPAND_ALL_SUBHEADS_ITEM);

		OUTLINE_EXPAND_EVERYTHING_ITEM.addActionListener(this);
		add(OUTLINE_EXPAND_EVERYTHING_ITEM);

		insertSeparator(6);

		OUTLINE_COLLAPSE_TO_PARENT_ITEM.addActionListener(this);
		add(OUTLINE_COLLAPSE_TO_PARENT_ITEM);

		OUTLINE_COLLAPSE_EVERYTHING_ITEM.addActionListener(this);
		add(OUTLINE_COLLAPSE_EVERYTHING_ITEM);

		insertSeparator(9);

		OUTLINE_MOVE_UP_ITEM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.SHIFT_MASK, false));
		OUTLINE_MOVE_UP_ITEM.addActionListener(this);
		add(OUTLINE_MOVE_UP_ITEM);

		OUTLINE_MOVE_DOWN_ITEM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.SHIFT_MASK, false));
		OUTLINE_MOVE_DOWN_ITEM.addActionListener(this);
		add(OUTLINE_MOVE_DOWN_ITEM);

		OUTLINE_MOVE_LEFT_ITEM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.SHIFT_MASK, false));
		OUTLINE_MOVE_LEFT_ITEM.addActionListener(this);
		add(OUTLINE_MOVE_LEFT_ITEM);

		OUTLINE_MOVE_RIGHT_ITEM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.SHIFT_MASK, false));
		OUTLINE_MOVE_RIGHT_ITEM.addActionListener(this);
		add(OUTLINE_MOVE_RIGHT_ITEM);

		insertSeparator(14);

		OUTLINE_PROMOTE_ITEM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Event.SHIFT_MASK, false));
		OUTLINE_PROMOTE_ITEM.addActionListener(this);
		add(OUTLINE_PROMOTE_ITEM);

		OUTLINE_DEMOTE_ITEM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false));
		OUTLINE_DEMOTE_ITEM.addActionListener(this);
		add(OUTLINE_DEMOTE_ITEM);

		insertSeparator(17);

		OUTLINE_MERGE_ITEM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK, false));
		OUTLINE_MERGE_ITEM.addActionListener(this);
		add(OUTLINE_MERGE_ITEM);

		OUTLINE_MERGE_WITH_SPACES_ITEM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK + Event.SHIFT_MASK, false));
		OUTLINE_MERGE_WITH_SPACES_ITEM.addActionListener(this);
		add(OUTLINE_MERGE_WITH_SPACES_ITEM);

		insertSeparator(20);

		OUTLINE_HOIST_ITEM.addActionListener(this);
		add(OUTLINE_HOIST_ITEM);

		OUTLINE_DEHOIST_ITEM.addActionListener(this);
		add(OUTLINE_DEHOIST_ITEM);
		OUTLINE_DEHOIST_ITEM.setEnabled(false);

		OUTLINE_DEHOIST_ALL_ITEM.addActionListener(this);
		add(OUTLINE_DEHOIST_ALL_ITEM);
		OUTLINE_DEHOIST_ALL_ITEM.setEnabled(false);
		
		setEnabled(false);
	}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		Outliner.menuBar.outlineMenu = this;
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(OUTLINE_TOGGLE_EXPANSION)) {
			fireKeyEvent(Outliner.getMostRecentDocumentTouched(), 0, KeyEvent.VK_PAGE_DOWN, false);

		} else if (e.getActionCommand().equals(OUTLINE_TOGGLE_COMMENT)) {
			fireKeyEvent(Outliner.getMostRecentDocumentTouched(), 0, KeyEvent.VK_PAGE_UP, false);
			
		} else if (e.getActionCommand().equals(OUTLINE_EXPAND_ALL_SUBHEADS)) {
			expandAllSubheads(Outliner.getMostRecentDocumentTouched());
			
		} else if (e.getActionCommand().equals(OUTLINE_EXPAND_EVERYTHING)) {
			expandEverything(Outliner.getMostRecentDocumentTouched());
			
		} else if (e.getActionCommand().equals(OUTLINE_COLLAPSE_TO_PARENT)) {
			collapseToParent(Outliner.getMostRecentDocumentTouched());
			
		} else if (e.getActionCommand().equals(OUTLINE_COLLAPSE_EVERYTHING)) {
			collapseEverything(Outliner.getMostRecentDocumentTouched());
			
		} else if (e.getActionCommand().equals(OUTLINE_MOVE_UP)) {
			fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.SHIFT_MASK, KeyEvent.VK_UP, true);
			
		} else if (e.getActionCommand().equals(OUTLINE_MOVE_DOWN)) {
			fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.SHIFT_MASK, KeyEvent.VK_DOWN, true);
			
		} else if (e.getActionCommand().equals(OUTLINE_MOVE_LEFT)) {
			fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.SHIFT_MASK, KeyEvent.VK_LEFT, false);
			
		} else if (e.getActionCommand().equals(OUTLINE_MOVE_RIGHT)) {
			fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.SHIFT_MASK, KeyEvent.VK_RIGHT, false);
			
		} else if (e.getActionCommand().equals(OUTLINE_PROMOTE)) {
			fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.SHIFT_MASK, KeyEvent.VK_TAB, false);
			
		} else if (e.getActionCommand().equals(OUTLINE_DEMOTE)) {
			fireKeyEvent(Outliner.getMostRecentDocumentTouched(), 0, KeyEvent.VK_TAB, true);
			
		} else if (e.getActionCommand().equals(OUTLINE_MERGE)) {
			fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.CTRL_MASK, KeyEvent.VK_M, false);

		} else if (e.getActionCommand().equals(OUTLINE_MERGE_WITH_SPACES)) {
			fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.CTRL_MASK + Event.SHIFT_MASK, KeyEvent.VK_M, false);
			
		} else if (e.getActionCommand().startsWith(OUTLINE_HOIST)) {
			hoist(Outliner.getMostRecentDocumentTouched());
			
		} else if (e.getActionCommand().equals(OUTLINE_DEHOIST)) {
			dehoist(Outliner.getMostRecentDocumentTouched());
						
		} else if (e.getActionCommand().equals(OUTLINE_DEHOIST_ALL)) {
			dehoist_all(Outliner.getMostRecentDocumentTouched());
						
		}
	}

	
	// Outline Menu Methods
	private static void hoist(OutlinerDocument doc) {
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				TextKeyListener.hoist(doc.tree.getEditingNode());
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				IconKeyListener.hoist(doc.tree);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	private static void dehoist(OutlinerDocument doc) {
		try {
			TextKeyListener.dehoist(doc.tree.getEditingNode());
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	private static void dehoist_all(OutlinerDocument doc) {
		try {
			TextKeyListener.dehoist_all(doc.tree.getEditingNode());
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	private static void expandAllSubheads(OutlinerDocument doc) {
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				TextKeyListener.expandAllSubheads(doc.tree.getEditingNode());
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				IconKeyListener.expandAllSubheads(doc.tree);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	private static void expandEverything(OutlinerDocument doc) {
		OutlinerCellRendererImpl textArea = doc.panel.layout.getUIComponent(doc.tree.getEditingNode());
		if (textArea == null) {return;}
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				TextKeyListener.expandEverything(doc.tree);
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				IconKeyListener.expandEverything(doc.tree);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	private static void collapseToParent(OutlinerDocument doc) {
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				TextKeyListener.collapseToParent(doc.tree.getEditingNode());
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				IconKeyListener.collapseToParent(doc.tree);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	private static void collapseEverything(OutlinerDocument doc) {
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				TextKeyListener.collapseEverything(doc.tree);
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				IconKeyListener.collapseEverything(doc.tree);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}


	// Utility Methods
	private static void fireKeyEvent(OutlinerDocument doc, int keyMask, int keyChar, boolean pressedOnly) {
		OutlinerCellRendererImpl textArea = doc.panel.layout.getUIComponent(doc.tree.getEditingNode());
		if (textArea == null) {return;}
		
		try {
			if (doc.tree.getComponentFocus() == outlineLayoutManager.TEXT) {
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), keyMask, keyChar));
				if (!pressedOnly) {
					textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), keyMask, keyChar));
				}
			} else if (doc.tree.getComponentFocus() == outlineLayoutManager.ICON) {
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), keyMask, keyChar));
				if (!pressedOnly) {	
					textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), keyMask, keyChar));
				}
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}


	// Misc Methods
	public static void updateOutlineMenu(OutlinerDocument doc) {
		if (doc == null) {
			Outliner.menuBar.outlineMenu.setEnabled(false);
		} else {
			Outliner.menuBar.outlineMenu.setEnabled(true);
		}
	}
}