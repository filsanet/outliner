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
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.xml.sax.*;

import com.organic.maynard.util.string.StringTools;

public class RecentFilesList extends JMenu implements ActionListener, GUITreeComponent {

	// Constants
	public static final String A_TEXT = "text";
	
	//private OutlinerDocument doc = null;
		
	public static Vector docInfoList = new Vector();
	
	
	// The Constructors
	public RecentFilesList() {
		//super(text);
		//this.doc = doc;
	}

	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}
	
	public void startSetup(AttributeList atts) {
		String title = atts.getValue(A_TEXT);
		setText(title);
		
		setEnabled(false);

		// Add this menuItem to the parent menu.
		JMenu menu = (JMenu) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 2);
		menu.add(this);
		
		// Populate the Menu with the existing filenames
		for (int i = 0; i < docInfoList.size(); i++) {
			addFileName((DocumentInfo) docInfoList.elementAt(i));
		}
	}
	
	public void endSetup() {}

	private void addFileName(DocumentInfo docInfo) {
		RecentFilesListItem item = new RecentFilesListItem(docInfo.getPath(), docInfo);
		item.addActionListener(this);
		add(item);
		setEnabled(true);
	}
	
	// Static methods
	public static void addFileNameToList(DocumentInfo docInfo) {
		// WebFile
		// Let's turn off the recent file list for remote files since it isn't designed to handle remote files yet.
		if (Preferences.WEB_FILE_SYSTEM.cur) {
			return;
		}
	
		String filename = docInfo.getPath();
		
		// Short Circuit if undo is disabled.
		if (Preferences.RECENT_FILES_LIST_SIZE.cur == 0) {return;}

		if (isFileNameUnique(filename)) {
			RecentFilesList menu = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);
			if (docInfoList.size() >= Preferences.RECENT_FILES_LIST_SIZE.cur) {
				// Remove from the lists
				docInfoList.removeElementAt(0);
				
				// Remove from menus
				menu.remove(0);
			}
			// Add to the lists
			docInfoList.addElement(docInfo);

			// Add to menus
			menu.addFileName(docInfo);
		}	
	}

	public static void trim() {
		RecentFilesList menu = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);
		
		while (docInfoList.size() > Preferences.RECENT_FILES_LIST_SIZE.cur) {
			// Trim lists
			docInfoList.removeElementAt(0);

			// Trim menus
			menu.remove(0);
		}
		
		if (menu.getItemCount() <= 0) {
			menu.setEnabled(false);
		}		
	}

	public static boolean isFileNameUnique(String filename) {
		for (int i = 0; i < docInfoList.size(); i++) {
			String text = ((DocumentInfo) docInfoList.elementAt(i)).getPath();
			if (filename.equals(text)) {
				return false;
			}
		}
		return true;
	}
	
	public static DocumentInfo getDocumentInfo(String filename) {
		for (int i = 0; i < docInfoList.size(); i++) {
			DocumentInfo docInfo = (DocumentInfo) docInfoList.elementAt(i);
			if (filename.equals(docInfo.getPath())) {
				return docInfo;
			}
		}
		return null;
	}
	
	public static void updateFileNameInList(String oldFilename, DocumentInfo docInfo) {
		removeFileNameFromList(oldFilename);
		addFileNameToList(docInfo);		
	}

	public static void removeFileNameFromList(String filename) {
		int index = -1;
		for (int i = 0; i < docInfoList.size(); i++) {
			String text = ((DocumentInfo) docInfoList.elementAt(i)).getPath();
			if (text.equals(filename)) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			return;
		}
		
		// Remove from lists
		docInfoList.removeElementAt(index);
		
		// Remove from menus
		RecentFilesList menu = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);
		menu.remove(index);
		if (menu.getItemCount() <= 0) {
			menu.setEnabled(false);
		}
	}

	
	// Config File
	public static void saveConfigFile(String filename) {
		try {
			FileWriter fw = new FileWriter(filename);
			fw.write(prepareConfigFile());
			fw.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not save recent files config file because of: " + e);
		}
	}
	
	// Need to fix
	private static String prepareConfigFile() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < docInfoList.size(); i++) {
			buffer.append(Outliner.COMMAND_SET);
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append("recent_file");
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			
			DocumentInfo docInfo = (DocumentInfo) docInfoList.elementAt(i);
			buffer.append(docInfo.toEncodedString(Outliner.COMMAND_PARSER_SEPARATOR, '\\'));
			
			buffer.append(System.getProperty("line.separator"));
		}
		return buffer.toString();
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// WebFile
		// Let's turn off the recent file list for remote files since it isn't designed to handle remote files yet.
		if (Preferences.WEB_FILE_SYSTEM.cur) {
			return;
		}

		DocumentInfo docInfo = ((RecentFilesListItem) e.getSource()).getDocumentInfo();
		String filename = docInfo.getPath();
		if (!Outliner.isFileNameUnique(filename)) {
			JOptionPane.showMessageDialog(Outliner.outliner, "The file: " + filename + " is already open.");
			return;
		}

		FileMenu.openFile(docInfo);
	}
}