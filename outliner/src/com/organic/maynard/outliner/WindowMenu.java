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

public class WindowMenu extends AbstractOutlinerMenu implements ActionListener, GUITreeComponent {
	
	public static int WINDOW_LIST_START = -1;

	public static int indexOfOldSelection = -1;


	// The Constructors
	public WindowMenu() {
		super();
	}	


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		Outliner.menuBar.windowMenu = this;

		setEnabled(false);
	}
	
	public void endSetup() {
		WINDOW_LIST_START = getItemCount();
	}
	

	// Accessors
	public static void addWindow(OutlinerDocument doc) {
		WindowMenuItem item = new WindowMenuItem(doc.getTitle(),doc);
		item.addActionListener(Outliner.menuBar.windowMenu);
		Outliner.menuBar.windowMenu.add(item);
	}

	public static void removeWindow(OutlinerDocument doc) {
		int index = getIndexOfDocument(doc);
		WindowMenuItem item = (WindowMenuItem) Outliner.menuBar.windowMenu.getItem(index);
		Outliner.menuBar.windowMenu.remove(index);
		item.destroy();
	}
	
	public static void updateWindow(OutlinerDocument doc) {
		int index = getIndexOfDocument(doc);
		Outliner.menuBar.windowMenu.getItem(index).setText(doc.getTitle());
	}
	
	public static void selectWindow(OutlinerDocument doc) {
		// DeSelect Old Window
		if ((indexOfOldSelection >= WINDOW_LIST_START) && (indexOfOldSelection < Outliner.menuBar.windowMenu.getItemCount())) {
			Outliner.menuBar.windowMenu.getItem(indexOfOldSelection).setSelected(false);
		}

		// Select New Window
		indexOfOldSelection = getIndexOfDocument(doc);
		Outliner.menuBar.windowMenu.getItem(indexOfOldSelection).setSelected(true);
	}
		
	private static int getIndexOfDocument(OutlinerDocument doc) {
		WindowMenu menu = Outliner.menuBar.windowMenu;
		for (int i = 0; i < menu.getItemCount(); i++) {
			JMenuItem item = menu.getItem(i);
			if (item instanceof WindowMenuItem) {
				WindowMenuItem wmItem = (WindowMenuItem) item;
				if (doc == wmItem.doc) {
					return i;
				}
			}
		}
		return -1;
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		changeToWindow(((WindowMenuItem) e.getSource()).doc);
	}


	// Window Menu Methods	
	public static void changeToWindow(OutlinerDocument doc) {
		if (doc == null) {return;}
		
		try {
			OutlinerDocument prevDoc = Outliner.getMostRecentDocumentTouched();
			
			// DeIconify if neccessary
			if (doc.isIcon()) {
				doc.setIcon(false);
			}
			
			if (Outliner.desktop.desktopManager.isMaximized()) {
				if (prevDoc != null) {
					//System.out.println("About to try and minimize");
					OutlinerDesktopManager.activationBlock = true;
					prevDoc.setMaximum(false);
					prevDoc.setSelected(false);
					OutlinerDesktopManager.activationBlock = false;
				}
				//System.out.println("About to try and maximize");
				doc.setMaximum(true);
			}

			doc.setSelected(true);
			doc.moveToFront();

		} catch (java.beans.PropertyVetoException pve) {
			pve.printStackTrace();
		}
	}
	


	// Misc Methods
	public static void updateWindowMenu() {
	
		if (Outliner.openDocumentCount() > 0) {
			Outliner.menuBar.windowMenu.setEnabled(true);
		} else {
			Outliner.menuBar.windowMenu.setEnabled(false);
		}
	}
}


public class StatisticsMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		Outliner.statistics.show();
	}
}


public class StackMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// Constants
	private static final int STACK_X_START = 5;
	private static final int STACK_Y_START = 5;
	private static final int STACK_X_STEP = 30;
	private static final int STACK_Y_STEP = 30;

	private static final int STACK_X_COLUMN_STEP = 45;


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (!Outliner.desktop.desktopManager.isMaximized()) {
			Point p = new Point(STACK_X_START,STACK_Y_START);
			int rowCount = 1;
			int columnCount = 1;
			
			int upperBound = getUpperScreenBoundary();
			
			OutlinerDocument mostRecentDocumentTouched = Outliner.getMostRecentDocumentTouched();
			
			for (int i = 0; i < Outliner.openDocumentCount(); i++) {
				OutlinerDocument doc = Outliner.getDocument(i);
				
				// Restore Size
				doc.restoreWindowToInitialSize();
				
				// Set Location
				doc.setLocation(p);
				p.x += STACK_X_STEP;
				p.y += STACK_Y_STEP;
				
				// Set Z Order
				doc.toFront();
				
				// If we've gone down to far, then reset for a new column
				if ((p.y + OutlinerDocument.INITIAL_HEIGHT) >= upperBound ) {
					p.y = STACK_Y_START;
					p.x = STACK_X_START + STACK_X_COLUMN_STEP * columnCount;
					columnCount++;
					rowCount = 0;
				}
				
				rowCount++;
			}
			
			if (mostRecentDocumentTouched != null) {
				mostRecentDocumentTouched.toFront();
			}		
		}
	}

	private static int getUpperScreenBoundary() {
		return Outliner.desktop.getHeight() - STACK_Y_START;
	}
}


public class NextWindowMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		changeToNextWindow();
	}

	private static void changeToNextWindow() {
		WindowMenu menu = Outliner.menuBar.windowMenu;
	
		if (WindowMenu.indexOfOldSelection != -1) {
			int indexOfNewSelection = WindowMenu.indexOfOldSelection + 1;
			if (indexOfNewSelection >= menu.getItemCount()) {
				indexOfNewSelection = WindowMenu.WINDOW_LIST_START;
			}
			
			WindowMenu.changeToWindow(((WindowMenuItem) menu.getItem(indexOfNewSelection)).doc);
		}
	}
}


public class PrevWindowMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		changeToPrevWindow();
	}

	private static void changeToPrevWindow() {
		WindowMenu menu = Outliner.menuBar.windowMenu;
	
		if (WindowMenu.indexOfOldSelection != -1) {
			int indexOfNewSelection = WindowMenu.indexOfOldSelection - 1;
			if (indexOfNewSelection < WindowMenu.WINDOW_LIST_START) {
				indexOfNewSelection = menu.getItemCount() - 1;
			}
			
			WindowMenu.changeToWindow(((WindowMenuItem) menu.getItem(indexOfNewSelection)).doc);
		}	
	}

}

