/**
 * Portions copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002  Stan Krute <Stan@StanKrute.com>
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

import com.organic.maynard.outliner.io.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;
import com.organic.maynard.io.FileTools;
import com.organic.maynard.util.string.StringTools;
import com.organic.maynard.util.string.Replace;
import com.organic.maynard.util.string.StanStringTools ;
import com.organic.maynard.util.vector.StanVectorTools ;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class RecentFilesList extends AbstractOutlinerMenu implements ActionListener, GUITreeComponent, JoeReturnCodes, JoeXMLConstants {

	// Constants
	private static final String TRUNC_STRING = GUITreeLoader.reg.getText("trunc_string");
	
	// display mode constants [srk]
	// ordering
	private static final int CHRONO_ORDER = 0;
	private static final int ALFA_ORDER = 1;
	private static final int ASCII_ORDER = 2;
	// name form
	private static final int FULL_PATHNAME = 0;
	private static final int TRUNC_PATHNAME = 1;
	private static final int JUST_FILENAME = 2;
	// direction
	private static final int TOP_TO_BOTTOM = 0;
	private static final int BOTTOM_TO_TOP = 1;
	
	// presence in frameInfoList
	private static final int NOT_THERE = -1;


	// Static Fields
	private static RecentFilesList recentFilesList = null;
	private static Vector frameInfoList = null; // All objects stored herein should be DocumentInfo objects.
	
	/* TBD  
	 * [srk] move frameInfoList into its own class for cleanth/power/flexibility
	 * its job is state maintenance for ALL internal frames opened in JOE
	 * not just std. outline docs, which go up on recent files list,
	 * but modeless dialogs and help doc outlines while they're open
	 *	
	 * that will let it restore ALL were-open frames after a crash
	 * or at startup if desired
	 */
	
	private static TreeSet alfaAsciiTree = null ; // [srk] for alfa/ascii ordering, we store filename/pathname strings here
	
	private static int currentDisplayOrdering = -1 ; // [srk] we start with these values to force a menu population
	private static int currentDisplayNameForm = -1 ;
	private static int currentDisplayDirection = -1 ;
	private static int currentRecentFilesListSize = 0 ;


	// The Constructors
	public RecentFilesList() {
		recentFilesList = this;
	}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		// get the menu's title and set it
		setText(atts.getValue(A_TEXT));
		
		// we start out disabled
		setEnabled(false);

		// Add us to our parent menu.
		((JMenu) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 2)).add(this);
		
		// set size of Recent Files list
		currentRecentFilesListSize = Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur;

		// Try to load the frameInfoList from disk
		// TBD [srk] rename RECENT_FILES_FILE to FRAME_INFO_LIST_FILE
		Object obj = FileTools.ReadObjectFromFile(Outliner.RECENT_FILES_FILE);
		
		// we need to make sure we have a Vector
		// we used to use an ArrayList for frameInfoList,
		// but now we use a Vector 
		// anything else is just ignored
		
		// if we were able to read something ...
		if ((obj != null) && (obj instanceof Vector)) {
			frameInfoList = (Vector) obj;
			
			// Remove any duplicate entries. Saves youngest, removes oldest
			StanVectorTools.removeDupesHeadside(frameInfoList);
			
			int filSize = Preferences.getPreferenceInt(Preferences.FRAME_INFO_LIST_SIZE).cur;
			
			if (frameInfoList.size() > filSize) {
				// trim back
				StanVectorTools.trimSizeSaveTail(frameInfoList, filSize);
			}
		} else {
			frameInfoList = new Vector();
		}
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		DocumentInfo docInfo = ((RecentFilesListItem) e.getSource()).getDocumentInfo();

		// Get the protocol from the FileProtocolManager by using the protocolName
		String protocolName = docInfo.getProtocolName();
		
		FileProtocol protocol = null;
		if (protocolName == null || protocolName.equals("")) {
			protocol = Outliner.fileProtocolManager.getDefault();
		} else {
			protocol = Outliner.fileProtocolManager.getProtocol(protocolName);
		}

		// Open or Import the file, as is appropriate
		if (!docInfo.isImported()) {
			FileMenu.openFile(docInfo, protocol);
		} else {
			FileMenu.importFile(docInfo, protocol);
		}
	}
	

	// Static Accessors
	public static Vector getFrameInfoList() {
		return frameInfoList;
	}
	
	public static int getSizeOfFrameInfoList() {
		return frameInfoList.size();
	}
	
	public static void setFrameInfoList(Vector list) {
		frameInfoList = list;
	}
	
	public static void addDocumentInfo(DocumentInfo docInfo) {
		frameInfoList.add(docInfo);
	}


	// set display options -- adjust menu and treeset structures as necessary
	// called from PreferencesPanelRecentFiles.applyCurrentToApplication
	public void setDisplayOptions (int ordering, int nameForm, int direction) {
		
		// Only update if something has changed.
		if (
			(currentDisplayOrdering != ordering) || 
			(currentDisplayNameForm != nameForm) || 
			(currentDisplayDirection != direction)		
		) {
			// store the new values.
			currentDisplayOrdering = ordering;
			currentDisplayNameForm = nameForm;
			currentDisplayDirection = direction;
			
			// sync view to model.
			syncTreeSet();
			syncMenuItems();
		}
	}
	
	
	// ensure that the treeset contains the necessary info
	private void syncTreeSet() {
		
		// Lazy Instantiation
		if (alfaAsciiTree == null) {
			alfaAsciiTree = new TreeSet();
		}
		
		alfaAsciiTree.clear();
		
		// if this ordering does not use the tree ...
		if ((currentDisplayOrdering != ALFA_ORDER) && (currentDisplayOrdering != ASCII_ORDER)) {
			return;
		}
		
		// if frameInfoList is empty ...
		int frameInfoListSize = frameInfoList.size() ;
		if (frameInfoListSize == 0) {
			return;
		}
				
		// if Recent Files size is zero, leave
		int recentFilesListSize = Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur;
		if (recentFilesListSize == 0) {
			return;
		}

		// set up some local vars
		DocumentInfo docInfo = null ;
		StrungDocumentInfo strungDocInfo = null ;
		
		// let's go mine some data

		// until we get to the bottom of frameInfoList or hit RECENT_FILES_LIST_SIZE ...
		for (int i = frameInfoListSize - 1, j = 0; (i >= 0) && (j < recentFilesListSize); i--) {
			
			// grab docInfo from frameInfoList
			docInfo = (DocumentInfo) frameInfoList.get(i);
			
			// if we're a Help file, try the next frame
			if (docInfo.isHelpFile()) {
				continue;
			}
			
			// switch on name form
			switch (currentDisplayNameForm) {
				
				case FULL_PATHNAME: 
				
				default:
					// package the docInfo up with the full pathname
					strungDocInfo= new StrungDocumentInfo(docInfo.getPath(), docInfo) ;
					break;
					
				case TRUNC_PATHNAME:
					// package the docInfo up with a truncated pathname
					strungDocInfo = new StrungDocumentInfo(StanStringTools.getTruncatedPathName(docInfo.getPath(), TRUNC_STRING), docInfo); 
					break;
					
				case JUST_FILENAME:
					// package the docInfo up with the filename
					strungDocInfo = new StrungDocumentInfo(StanStringTools.getFileNameFromPathName(docInfo.getPath()), docInfo);
					break;
			}
			
			// set ascii/alfa switch
			// we ignore case for alphabetical order, heed it for ASCII order
			strungDocInfo.setIgnoreCase(currentDisplayOrdering == ALFA_ORDER);
			
			// add item to the alfaAscii tree
			alfaAsciiTree.add(strungDocInfo) ;
			
			// up the Recent Files list counter
			j++;
		}
	}

		
	// ensure that this menu contains the proper set of menu items
	private void syncMenuItems() {
		// start with a clean slate by removing all menu items
		removeAll();
		
		// [srk] this removal does NOT shrink the
		// displayed width of the menu back to 
		// a small number
		// perhaps this is a bug in Sun's JMenu code
		// looking for a workaround or fix
		
		// if frameInfoList is empty, leave
		int frameInfoListSize = frameInfoList.size() ;
		if (frameInfoListSize == 0) {
			return;
		}
		
		// if Recent Files size is zero, leave
		int recentFilesListSize = Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur;
		if (recentFilesListSize == 0) {
			return;
		}

		// switch on ordering
		switch (currentDisplayOrdering) {
			
			case CHRONO_ORDER:
		
			default:
				// we send these out in reverse order
				if (currentDisplayOrdering == TOP_TO_BOTTOM) {
					currentDisplayOrdering = BOTTOM_TO_TOP;
				} else {
					currentDisplayOrdering = TOP_TO_BOTTOM;
				}
				
				DocumentInfo docInfo = null;
				
				for (int j = 0, i = frameInfoListSize - 1; (i >= 0) && (j < recentFilesListSize); i--) {

					// grab docInfo from frameInfoList
					docInfo = (DocumentInfo) frameInfoList.get(i) ;
					
					if (docInfo == null) {
						continue;
					}
					
					// if we're a Help file, try the next frame
					if (docInfo.isHelpFile()) {
						continue;
					}
		
					// okay, we're to be shown -- do it
					addMenuItemForFileToMenu(docInfo);
		
					// up the Recent Files list counter
					j++;
				}

				// go back to our entry direction state
				if (currentDisplayOrdering == TOP_TO_BOTTOM) {
					currentDisplayOrdering = BOTTOM_TO_TOP;
				} else {
					currentDisplayOrdering = TOP_TO_BOTTOM;
				}
				
				break; // case CHRONO_ORDER, default
			
			case ALFA_ORDER:
		
			case ASCII_ORDER:
				// add each item in alfaAscii tree order to the menu
				// note that we don't check whether item's to be shown on menu
				// that's because only showing items get added to the tree 
				StrungDocumentInfo sdi = null;
				
				for (Iterator iter = alfaAsciiTree.iterator(); iter.hasNext();) {
					sdi = (StrungDocumentInfo) iter.next();
					addMenuItemForFileToMenu(sdi.getDocumentInfo());
				}
				
				break; // case ALFA_ORDER, ASCII_ORDER
		}
	}

	
	// add a menu item for a file to the menu
	private void addMenuItemForFileToMenu(DocumentInfo docInfo) {
		// local vars
		RecentFilesListItem menuItem = null;
		StrungDocumentInfo strungItem = null;
		
		// switch on nameform to create a menu item
		switch (currentDisplayNameForm) {
			
			case FULL_PATHNAME:
			
			default:
				// create a menu item
				menuItem = new RecentFilesListItem(docInfo.getPath(), docInfo);
				break;
				
			case TRUNC_PATHNAME:
				// create a menu item
				menuItem = new RecentFilesListItem(StanStringTools.getTruncatedPathName(docInfo.getPath(), TRUNC_STRING), docInfo);
				break;
			
			case JUST_FILENAME:
				// create a menu item
				menuItem = new RecentFilesListItem(StanStringTools.getFileNameFromPathName(docInfo.getPath()), docInfo);
				break;
		}
		
		// tell the menu item to listen to its menu
		menuItem.addActionListener(this);
		
		// switch out on menu's item orientation
		switch (currentDisplayDirection) {
			
			case TOP_TO_BOTTOM:
			
			default:
				// append the menu item to the menu
				add(menuItem);
				break ;
				
			case BOTTOM_TO_TOP:
				// prepend the menu item to the menu
				insert(menuItem,0);
				break ;	
		}
		
		// since we've got at least one item on our menu, we're enabled
		setEnabled(true);
	}


	// Static methods
	
	// this method is called by outsiders when a file is open or imported
	// they're asking RFL to add the doc to the Recent Files List
	
	// if the file is not in frameInfoList, it gets added to it
	// if the file is already in frameInfoList, it get moves to the tail of the list
	
	// we then sync up treeset and the menu
	static void addFileNameToList(DocumentInfo docInfo) {
		
		// check to see if this file is in frameInfoList 
		int position = NOT_THERE;
		String filename = docInfo.getPath();

		for (int i = 0, limit = frameInfoList.size(); i < limit; i++) {
			if (filename.equals(((DocumentInfo) frameInfoList.get(i)).getPath())) {
				position = i;
			}
		}
		
		// if it's not in frameInfoList ...
		if (position == NOT_THERE) {
			// if the frameInfoList is too long ...
			if (frameInfoList.size() >= Preferences.getPreferenceInt(Preferences.FRAME_INFO_LIST_SIZE).cur) {
				frameInfoList.remove(0);
			}
			
			// add the file's docInfo to frameInfoList
			frameInfoList.add(docInfo);
		} else {
			// update its doc info
			frameInfoList.set(position, docInfo) ;
			
			// move that to the top of the list
			StanVectorTools.moveElementToTail(frameInfoList, position) ;
		}

		// sync
		recentFilesList.syncTreeSet() ;
		recentFilesList.syncMenuItems() ;
	}


	// this method is called by outsiders when a file is to be removed from Recent Files List
	
	// if the file is not in frameInfoList, we do nothing
	// if the file is in frameInfoList, we remove it, then sync up the treeset and the menu items
	
	// we then sync up treeset and the menu
	public static void removeFileNameFromList(DocumentInfo docInfo) {
		
		// check to see if this file is in frameInfoList 
		int position = NOT_THERE;
		String filename = docInfo.getPath();

		for (int i = 0, limit = frameInfoList.size(); i < limit; i++) {
			if (filename.equals(((DocumentInfo) frameInfoList.get(i)).getPath())) {
				position = i;
			}
		}
		
		// if it's not in frameInfoList ...
		if (position == NOT_THERE) {
			return;
		}
		
		// move it to the tail of the list
		StanVectorTools.moveElementToTail(frameInfoList, position);
		
		// cut off the tail
		frameInfoList.setSize(frameInfoList.size() - 1);

		// sync 
		recentFilesList.syncTreeSet();
		recentFilesList.syncMenuItems();
	}


	// called by outsiders when the size of the recent files list may have been changed
	public static void syncSize() {
		// grab the size that's been set
		int sizeSet = Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur;
		
		// if it's different than our stored setting ...
		if (sizeSet != recentFilesList.currentRecentFilesListSize) {
			
			// store new setting
			recentFilesList.currentRecentFilesListSize = sizeSet;
			
			// sync
			recentFilesList.syncTreeSet();
			recentFilesList.syncMenuItems();
		}
	}


	// given a pathname, get docInfo from frameInfoList
	public static DocumentInfo getDocumentInfo(String pathname) {
		for (int i = 0, limit = frameInfoList.size(); i < limit; i++) {
			DocumentInfo docInfo = (DocumentInfo) frameInfoList.get(i);
			
			// if it's nothing. Why would this ever happen?
			if (docInfo == null) {
				continue;
			}
			
			if (pathname.equals(docInfo.getPath())) {
				return docInfo;
			}
		}
		return null;
	}

	
	// Config File
	public static void saveConfigFile(String filename) {
		FileTools.writeObjectToFile(frameInfoList, filename);
	}
}