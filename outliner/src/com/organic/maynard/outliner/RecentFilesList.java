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
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;
import com.organic.maynard.util.string.StringTools;
import com.organic.maynard.util.string.Replace;

public class RecentFilesList extends JMenu implements ActionListener, GUITreeComponent, JoeReturnCodes {

	// Constants
	private static final String A_TEXT = "text";
	
	
	// Static Fields
	private static ArrayList docInfoList = null;
	
	
	// The Constructors
	public RecentFilesList() {}
	
	
	// Static Accessors
	public static ArrayList getDocInfoList() {
		return docInfoList;
	}
	
	public static int getSizeOfDocInfoList() {
		return docInfoList.size();
	}
	
	public static void setDocInfoList(ArrayList list) {
		docInfoList = list;
	}
	
	public static void addDocumentInfo(DocumentInfo docInfo) {
		docInfoList.add(docInfo);
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
		
		// Load recent files from disk.
		docInfoList = (ArrayList) ReadObjectFromFile(Outliner.RECENT_FILES_FILE);
		if (docInfoList == null) {
			docInfoList = new ArrayList();
		}
		
		// Populate the Menu with the existing filenames
		for (int i = 0; i < docInfoList.size(); i++) {
			addFileName((DocumentInfo) docInfoList.get(i));
		}
	}
	
	public void endSetup(AttributeList atts) {}

	private void addFileName(DocumentInfo docInfo) {
		RecentFilesListItem item = new RecentFilesListItem(docInfo.getPath(), docInfo);
		item.addActionListener(this);
		add(item);
		setEnabled(true);
	}


	// Static methods
	public static void addFileNameToList(DocumentInfo docInfo) {
		String filename = docInfo.getPath();
		
		// Short Circuit if undo is disabled.
		if (Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur == 0) {
			return;
		}
		
		// if it's a Help system file ... 		[srk] 8/12/01 12:26AM
		if (Outliner.helpDoxMgr.isThisOneOfOurs(filename) != DOCUMENT_NOT_FOUND) {
			// fuhgedabowdit
			return;
		}
		 
		if (isFileNameUnique(filename)) {
			RecentFilesList menu = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);
			
			if (docInfoList.size() >= Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur) {
				// Remove from the lists
				docInfoList.remove(0);
				
				// Remove from menus
				menu.remove(0);
			}
			// Add to the lists
			docInfoList.add(docInfo);

			// Add to menus
			menu.addFileName(docInfo);
		}	
	}

	public static void trim() {
		RecentFilesList menu = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);
		
		while (docInfoList.size() > Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur) {
			// Trim lists
			docInfoList.remove(0);

			// Trim menus
			menu.remove(0);
		}
		
		if (menu.getItemCount() <= 0) {
			menu.setEnabled(false);
		}		
	}

	public static boolean isFileNameUnique(String filename) {
		for (int i = 0; i < docInfoList.size(); i++) {
			String text = ((DocumentInfo) docInfoList.get(i)).getPath();
			if (filename.equals(text)) {
				return false;
			}
		}
		return true;
	}
	
	public static DocumentInfo getDocumentInfo(String filename) {
		for (int i = 0; i < docInfoList.size(); i++) {
			DocumentInfo docInfo = (DocumentInfo) docInfoList.get(i);
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
			String text = ((DocumentInfo) docInfoList.get(i)).getPath();
			if (text.equals(filename)) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			return;
		}
		
		// Remove from lists
		docInfoList.remove(index);
		
		// Remove from menus
		RecentFilesList menu = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);
		menu.remove(index);
		if (menu.getItemCount() <= 0) {
			menu.setEnabled(false);
		}
	}

	
	// Config File
	public static void saveConfigFile(String filename) {
		writeObjectToFile(docInfoList, filename);
	}

	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		DocumentInfo docInfo = ((RecentFilesListItem) e.getSource()).getDocumentInfo();
		String filename = docInfo.getPath();
		if (!Outliner.isFileNameUnique(filename)) {
			String msg = GUITreeLoader.reg.getText("message_file_already_open");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);
			
			JOptionPane.showMessageDialog(Outliner.outliner, msg);
			return;
		}

		// TEMP: get protocol from protocolName
		String protocolName = docInfo.getProtocolName();
		FileProtocol protocol = null;
		if (protocolName == null || protocolName.equals("")) {
			protocol = Outliner.fileProtocolManager.getDefault();
		} else {
			protocol = Outliner.fileProtocolManager.getProtocol(protocolName);
		}

		FileMenu.openFile(docInfo, protocol);
	}
	
	
	// Utility Functions
	// TODO: these should be added to io in com.organic.maynard.jar
	public static boolean writeObjectToFile(Object obj, String filename) {
		try {
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename));
			stream.writeObject(obj);
			stream.close();
			return true;
		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
			return false;
		}
	}

	public static Object ReadObjectFromFile(String filename) {
		Object obj = null;
		
		try {
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(filename));
			obj = stream.readObject();
			stream.close();
			
		} catch (OptionalDataException ode) {
			System.out.println("Exception: " + ode);
			
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + cnfe);
			
		} catch (FileNotFoundException fnfe) {
			System.out.println("Exception: " + fnfe);
			
		} catch (StreamCorruptedException sce) {
			System.out.println("Exception: " + sce);
					
		} catch (IOException ioe) {
			System.out.println("Exception: " + ioe);
					
		}
		
		return obj;
	}
}