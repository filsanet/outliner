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

import com.organic.maynard.util.string.StringTools;

public class RecentFilesList extends JMenu implements ActionListener {
	
	private OutlinerDocument doc = null;
	
	public static Vector fileList = new Vector();
	public static Vector encodingList = new Vector(); // Stores the encoding used to save this document, so we can reopen it correctly.
	public static Vector formatList = new Vector(); // Stores the format used to save this document, so we can reopen it correctly.
	
	// The Constructors
	public RecentFilesList(String text, OutlinerDocument doc) {
		super(text);
		this.doc = doc;
		
		// Populate the Menu with the existing filenames
		loadMenuItemsFromLists();

	}
	
	private void loadMenuItemsFromLists() {
		for (int i = 0; i < fileList.size(); i++) {
			String filename = (String) fileList.elementAt(i);
			String encoding = (String) encodingList.elementAt(i);
			String fileFormat = (String) formatList.elementAt(i);
			addFileName(filename,encoding,fileFormat);
		}	
	}

	private void addFileName(String filename, String encoding, String fileFormat) {
		RecentFilesListItem item = new RecentFilesListItem(filename,filename,encoding,fileFormat);
		item.addActionListener(this);
		add(item);
	}
	
	// Static methods
	public static void addFileNameToList(String filename,String encoding,String fileFormat) {
		// Short Circuit if undo is disabled.
		if (Preferences.RECENT_FILES_LIST_SIZE.cur == 0) {return;}

		if (isFileNameUnique(filename)) {
			if (fileList.size() >= Preferences.RECENT_FILES_LIST_SIZE.cur) {
				// Remove from the lists
				fileList.removeElementAt(0);
				encodingList.removeElementAt(0);
				formatList.removeElementAt(0);
				
				// Remove from menus
				Outliner.menuBar.fileMenu.FILE_OPEN_RECENT_MENU.remove(0);
			}
			// Add to the lists
			fileList.addElement(filename);
			encodingList.addElement(encoding);
			formatList.addElement(fileFormat);

			// Add to menus
			Outliner.menuBar.fileMenu.FILE_OPEN_RECENT_MENU.addFileName(filename,encoding,fileFormat);
		}	
	}

	public static void trim() {
		while (fileList.size() > Preferences.RECENT_FILES_LIST_SIZE.cur) {
			// Trim lists
			fileList.removeElementAt(0);
			encodingList.removeElementAt(0);
			formatList.removeElementAt(0);

			// Trim menus
			Outliner.menuBar.fileMenu.FILE_OPEN_RECENT_MENU.remove(0);
		}	
	}

	public static boolean isFileNameUnique(String filename) {
		for (int i = 0; i < fileList.size(); i++) {
			String text = (String) fileList.elementAt(i);
			if (filename.equals(text)) {
				return false;
			}
		}
		return true;
	}
	
	public static void updateFileNameInList(String oldFilename, String filename, String encoding, String fileFormat) {
		removeFileNameFromList(oldFilename);
		addFileNameToList(filename,encoding,fileFormat);		
	}

	public static void removeFileNameFromList(String filename) {
		int index = -1;
		for (int i = 0; i < fileList.size(); i++) {
			String value = (String) fileList.elementAt(i);
			if (value.equals(filename)) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			return;
		}
		
		// Remove from lists
		fileList.removeElementAt(index);
		encodingList.removeElementAt(index);
		formatList.removeElementAt(index);
		
		// Remove from menus
		Outliner.menuBar.fileMenu.FILE_OPEN_RECENT_MENU.remove(index);
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
	
	private static String prepareConfigFile() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < fileList.size(); i++) {
			buffer.append(Outliner.COMMAND_SET);
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append("recent_file");
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(StringTools.escape((String) fileList.elementAt(i), '\\', null));
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(StringTools.escape((String) encodingList.elementAt(i), '\\', null));
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(StringTools.escape((String) formatList.elementAt(i), '\\', null));
			buffer.append(System.getProperty("line.separator"));
		}
		return buffer.toString();
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		String filename = ((RecentFilesListItem) e.getSource()).filename;
		String encoding = ((RecentFilesListItem) e.getSource()).encoding;
		String fileFormat = ((RecentFilesListItem) e.getSource()).fileFormat;
		if (!Outliner.isFileNameUnique(filename)) {
			JOptionPane.showMessageDialog(this.doc, "The file: " + filename + " is already open.");
			return;
		}
		FileMenu.openFile(filename,encoding,fileFormat);
	}
}