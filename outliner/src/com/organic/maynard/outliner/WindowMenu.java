/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 * 
 *  - Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution. 
 * 
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
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