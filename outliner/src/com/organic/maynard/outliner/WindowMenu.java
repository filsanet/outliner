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

public class WindowMenu extends AbstractOutlinerMenu implements ActionListener {

	public static final String WINDOW_STACK = "Stack";
	
	public JMenuItem windowStackItem = new JMenuItem(WINDOW_STACK);
	
	// The Constructors
	public WindowMenu() {
		super("Window");
		
		windowStackItem.addActionListener(this);
		add(windowStackItem);

		insertSeparator(1);
	}	

	public static void addWindow(OutlinerDocument doc) {
		WindowMenuItem item = new WindowMenuItem(doc.getTitle(),doc);
		item.addActionListener(Outliner.menuBar.windowMenu);
		Outliner.menuBar.windowMenu.add(item);
	}

	public static void removeWindow(OutlinerDocument doc) {
		int index = getIndexOfDocument(doc);
		Outliner.menuBar.windowMenu.remove(index);
	}
	
	public static void updateWindow(OutlinerDocument doc) {
		int index = getIndexOfDocument(doc);
		Outliner.menuBar.windowMenu.getItem(index).setText(doc.getTitle());
	}

	private static int indexOfOldSelection = -1;
	
	public static void selectWindow(OutlinerDocument doc) {
		// DeSelect Old Window
		if ((indexOfOldSelection >= 2) && (indexOfOldSelection < Outliner.menuBar.windowMenu.getItemCount())) {
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
		if (e.getActionCommand().equals(WINDOW_STACK)) {
			if (!Outliner.desktop.desktopManager.isMaximized()) {
				stack_windows();
			}
		} else {
			changeToWindow(((WindowMenuItem) e.getSource()).doc);
		}
	}

	public static void updateWindowMenu(OutlinerDocument doc) {
		if (doc == null) {
			Outliner.menuBar.windowMenu.setEnabled(false);
		} else {
			Outliner.menuBar.windowMenu.setEnabled(true);
		}
	}


	// Window Menu Methods
	public static void changeToWindow(OutlinerDocument doc) {
		try {
			OutlinerDocument prevDoc = Outliner.getMostRecentDocumentTouched();
			
			doc.setSelected(true);
			doc.moveToFront();
			
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
		} catch (java.beans.PropertyVetoException pve) {
			pve.printStackTrace();
		}
	}
	
	private static final int STACK_X_START = 5;
	private static final int STACK_Y_START = 5;
	private static final int STACK_X_STEP = 30;
	private static final int STACK_Y_STEP = 30;

	private static final int STACK_X_COLUMN_STEP = 45;
	
	protected static void stack_windows() {
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

	protected static int getUpperScreenBoundary() {
		return Outliner.desktop.getHeight() - STACK_Y_START;
	}
}