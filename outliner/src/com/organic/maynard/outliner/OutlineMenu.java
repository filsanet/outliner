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

public class OutlineMenu extends AbstractOutlinerMenu implements GUITreeComponent {

	public static String OUTLINE_HOIST = "";

	// The Constructors
	public OutlineMenu() {
		super();
	}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		Outliner.menuBar.outlineMenu = this;
		setEnabled(false);
	}
	
	public void endSetup() {
		super.endSetup();
		JMenuItem hoistItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.OUTLINE_HOIST_MENU_ITEM);
		OUTLINE_HOIST = hoistItem.getText();
	}


	// Utility Methods
	protected static void fireKeyEvent(OutlinerDocument doc, int keyMask, int keyChar, boolean pressedOnly) {
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


public class ToggleCommentMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlineMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), 0, KeyEvent.VK_PAGE_UP, false);
	}
}


public class ToggleExpansionMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlineMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), 0, KeyEvent.VK_PAGE_DOWN, false);
	}
}


public class ExpandAllSubheadsMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		expandAllSubheads(Outliner.getMostRecentDocumentTouched());
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
}


public class ExpandEverythingMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		expandEverything(Outliner.getMostRecentDocumentTouched());
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
}


public class CollapseToParentMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		collapseToParent(Outliner.getMostRecentDocumentTouched());
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
}


public class CollapseEverythingMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		collapseEverything(Outliner.getMostRecentDocumentTouched());
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
}


public class MoveUpMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlineMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.SHIFT_MASK, KeyEvent.VK_UP, true);
	}
}


public class MoveDownMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlineMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.SHIFT_MASK, KeyEvent.VK_DOWN, true);
	}
}


public class MoveLeftMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlineMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.SHIFT_MASK, KeyEvent.VK_LEFT, false);
	}
}


public class MoveRightMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlineMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.SHIFT_MASK, KeyEvent.VK_RIGHT, false);
	}
}


public class PromoteMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlineMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.SHIFT_MASK, KeyEvent.VK_TAB, false);
	}
}


public class DemoteMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlineMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), 0, KeyEvent.VK_TAB, true);
	}
}


public class MergeMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlineMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.CTRL_MASK, KeyEvent.VK_M, false);
	}
}


public class MergeWithSpacesMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlineMenu.fireKeyEvent(Outliner.getMostRecentDocumentTouched(), Event.CTRL_MASK + Event.SHIFT_MASK, KeyEvent.VK_M, false);
	}
}


public class HoistMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		hoist(Outliner.getMostRecentDocumentTouched());
	}

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
}


public class DehoistMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		dehoist(Outliner.getMostRecentDocumentTouched());
	}

	private static void dehoist(OutlinerDocument doc) {
		try {
			TextKeyListener.dehoist(doc.tree.getEditingNode());
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}


public class DehoistAllMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		dehoist_all(Outliner.getMostRecentDocumentTouched());
	}

	private static void dehoist_all(OutlinerDocument doc) {
		try {
			TextKeyListener.dehoist_all(doc.tree.getEditingNode());
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}


