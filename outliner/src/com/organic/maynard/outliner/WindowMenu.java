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
	
	// Class Fields
	protected static int WINDOW_LIST_START = -1;
	protected static int indexOfOldSelection = -1;


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
	
	public void endSetup(AttributeList atts) {
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