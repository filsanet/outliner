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

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

// we're part of this
package com.organic.maynard.outliner;

// we use these
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;
import com.organic.maynard.util.string.StringTools;
import com.organic.maynard.util.string.Replace;
import com.organic.maynard.util.string.StanStringTools ;

/* NOTICE  [srk] Currently undergoing as-gentle-as-possible minor decoupling and reconstructive surgery
 * while implementing alfa/ascii menu item orderings -- due to complete  * 1-23-02
*/

public class RecentFilesList extends JMenu implements ActionListener, GUITreeComponent, JoeReturnCodes {

	// Constants
	private static final String A_TEXT = "text";
	
	// display mode constants [srk]
	// ordering
	private static final int CHRONO_ORDER = 0 ;
	private static final int ALFA_ORDER = 1 ;
	private static final int ASCII_ORDER = 2 ;
	// name form
	private static final int FULL_PATHNAME = 0 ;
	private static final int TRUNC_PATHNAME = 1 ;
	private static final int JUST_FILENAME = 2 ;
	// direction
	private static final int TOP_TO_BOTTOM = 0 ;
	private static final int BOTTOM_TO_TOP = 1 ;
	
	// Static Fields
	private static ArrayList docInfoList = null;
	/* [srk] docInfoList holds DocumentInfo data for a number of documents
		currently that's the number of items shown in the recent files list
		2B decoupled, so it'll have it's own value, >= # recent files
		that way, one can slide from lotsa files shown to few files shown 
		and back to lotsa again without losing everybody   */
	
	private static TreeSet alfaAsciiTree = null ; // [srk] for alfa/ascii ordering, we store filename strings here
//	private static TreeSet pathnameTree = null ; // [srk] for alfa/ascii ordering, we store pathname strings here
	
	private static int currentDisplayOrdering = -1 ; // [srk] we start with these values to force a menu population
	private static int currentDisplayNameForm = -1 ;
	private static int currentDisplayDirection = -1 ;
	
	// The Constructors
	public RecentFilesList() {}
	
	// Static Accessors
	static ArrayList getDocInfoList() {
		return docInfoList;
	}
	
	static int getSizeOfDocInfoList() {
		return docInfoList.size();
	}
	
	static void setDocInfoList(ArrayList list) {
		docInfoList = list;
	}
	
	static void addDocumentInfo(DocumentInfo docInfo) {
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

		// Add us to our parent menu.
		JMenu menu = (JMenu) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 2);
		menu.add(this);
		
		// Try to load the docInfoList from disk.
		docInfoList = (ArrayList) ReadObjectFromFile(Outliner.RECENT_FILES_FILE);
		
		// if nothing was read from disk
		if (docInfoList == null) {
			// start a new docInfoList
			docInfoList = new ArrayList();
		} // end if
		
		// apply our display settings
		// this also populates the menu correctly
		applyDisplaySettings() ;
		
		
	} // end methor startSetup
	
	// call on our UI panel to apply the latest display options
	private void applyDisplaySettings () {
		
		// grab ahold of our prefs panel
		PreferencesPanelRecentFiles prefsPanel = (PreferencesPanelRecentFiles) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_PANEL_RECENT_FILES);

		// have it apply its current settings
		prefsPanel.applyCurrentToApplication() ;
		
	} // end method applyDisplaySettings
	
	// set display options -- adjust menu and treeset structures as necessary
	// called from PreferencesPanelRecentFiles
	void setDisplayOptions (int ordering, int nameForm, int direction) {
		// we are lazy, only work on change
		boolean change = (currentDisplayOrdering != ordering)
			|| (currentDisplayNameForm !=nameForm)
			|| (currentDisplayDirection != direction) ;
		
		// if there's change ...
		if (change) {
			// apply the new values
			currentDisplayOrdering = ordering ;
			currentDisplayNameForm = nameForm ;
			currentDisplayDirection = direction ;
			
			// sync up the tree set
			syncTreeSet() ;
						
			// sync up the menu items
			syncMenuItems() ;
			
		} // end if we have a change
		
	} // end method setDisplayOptions
	
	// ensure that the treeset contains the necessary info
	private void syncTreeSet () {
		
		// if we don't have tree, leave
		if ( ! ensureAlfaAsciiTree() ) {
			return ;
		} // end if
		
		// empty it out
		alfaAsciiTree.clear() ;
		
		// if this ordering does not use the tree, leave
		if ( (currentDisplayOrdering != ALFA_ORDER)  
			&&  (currentDisplayOrdering != ASCII_ORDER) ) {
			return ;
		} // end if
		
		// if docInfoList is empty, leave
		int numEntries = docInfoList.size() ;
		if (numEntries == 0) {
			return ;
		} // end if
				
		// okay, we have some docInfoList entries
		// let's go mine their data
		DocumentInfo docInfo = null ;
		StrungDocumentInfo strungDocInfo = null ;
		
		// for each item in docInfoList
		for (int i = 0; i < numEntries; i++) {
			// grab the docInfo
			docInfo = (DocumentInfo) docInfoList.get(i) ;
			
			// switch on name form
			switch (currentDisplayNameForm) {
				
			case FULL_PATHNAME: 
			default:
				// package the docInfo up with the full pathname
				strungDocInfo= new StrungDocumentInfo(docInfo.getPath(), docInfo) ;
				break ;
				
			case TRUNC_PATHNAME:
				// package the docInfo up with a truncated pathname
				strungDocInfo= new StrungDocumentInfo(
					StanStringTools.getTruncatedPathName(docInfo.getPath()),
					docInfo) ; 
				break ;
				
			case JUST_FILENAME:
				// package the docInfo up with the filename
				strungDocInfo= new StrungDocumentInfo(
					StanStringTools.getFileNameFromPathName(docInfo.getPath()),
					docInfo) ;
				break ;
			
			} // end switch on name form	
			
			// set ascii/alfa switch
			// we ignore case for alphabetical order, heed it for ASCII order
			strungDocInfo.setIgnoreCase(currentDisplayOrdering == ALFA_ORDER) ;
			
			// add item to the alfaAscii tree
			alfaAsciiTree.add(strungDocInfo) ;
			
		} // end for
		
	} // end method syncTreeSet

		
	// ensure that this menu contains the proper set of menu items
	private void syncMenuItems () {
		// local vars
		StrungDocumentInfo sdi = null ;
		
		// start with a clean slate
		removeAll() ;
		
		// [srk] this removal does NOT shrink the
		// displayed width of the menu back to 
		// a small number
		// perhaps this is a bug in Sun's jMenu code
		// looking for a workaround or fix
		
		// if docInfoList is empty, leave
		if (docInfoList.size() == 0) {
			return ;
		} // end if
		
		// switch on ordering
		switch (currentDisplayOrdering) {
			
		case CHRONO_ORDER:
		default:
			// add each item in doc list order to the menu
			for (int i = 0, size=docInfoList.size(); i < size; i++) {
				addMenuItemForFileToMenu((DocumentInfo) docInfoList.get(i));
			} // end for

			break ; // case CHRONO_ORDER, default
			
		case ALFA_ORDER:
		case ASCII_ORDER:
			// add each item in alfaAscii tree order to the menu 
			for (Iterator i = alfaAsciiTree.iterator(); i.hasNext();) {
				sdi = (StrungDocumentInfo) i.next();
				addMenuItemForFileToMenu(sdi.getDocumentInfo());
			} // end for
			
			break ; // case ALFA_ORDER, ASCII_ORDER
			
		} // end switch on currentDisplayOrdering	
		
	} // end method syncMenuItems
		

	public void endSetup(AttributeList atts) {}

	// add a menu item for a file to the menu
	private void addMenuItemForFileToMenu(DocumentInfo docInfo) {
		// local vars
		RecentFilesListItem menuItem = null ;
		StrungDocumentInfo strungItem = null ;
		
		// switch on nameform to create a menu item
		switch (currentDisplayNameForm) {
			
		case FULL_PATHNAME: 
		default:
			// create a menu item
			menuItem = new RecentFilesListItem(docInfo.getPath(), docInfo);
			break ;
			
		case TRUNC_PATHNAME:
			// create a menu item
			menuItem = new RecentFilesListItem(
				StanStringTools.getTruncatedPathName(docInfo.getPath()), 
				docInfo);
			break ;
		
		case JUST_FILENAME:
			// create a menu item
			menuItem = new RecentFilesListItem(
				StanStringTools.getFileNameFromPathName(docInfo.getPath()), 
				docInfo);
			break ;
		
		} // end switch on nameform
		
		// we want this menu to listen to the menu item
		menuItem.addActionListener(this);
		
		// switch out on direction
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
			
		}  // end switch on direction
		
		// since we've got at least one item on our menu, we're enabled
		setEnabled(true);

	} // end method addMenuItemForFileToMenu


	private static boolean ensureAlfaAsciiTree () {
		// local var
		TreeSet test = new TreeSet() ;
		
		// if we're pointin' to a treeset, we're cool
		if ( (alfaAsciiTree != null)  &&  (test.getClass().isInstance(alfaAsciiTree)) ) {
			return true ;
		} // end if
		
		// if not, let's try to create one
		alfaAsciiTree = new TreeSet() ;
		
		// return a result
		return ( (alfaAsciiTree != null)  &&  (test.getClass().isInstance(alfaAsciiTree)) ) ;
		
	} // end method ensureAlfaAsciiTree

	// Static methods
	
	// this method is called by outsiders when a file is open or imported
	// the file gets added to the docInfoList, the menu, and, if necessary, the alfaAscii tree
	static void addFileNameToList(DocumentInfo docInfo) {
		// local vars
		String filename = docInfo.getPath();
		StrungDocumentInfo strungDocInfo = null ;
		
		// Short Circuit if user-set size of list is zero
		if (Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur == 0) {
			return;
		} // end if
		
		// TBD [srk] maintain a list of non-document windows, nonDocInfoList
		// and add help windows, dialogs, all other non-doc windows to that list
		// for complete startup state regeneration
		// to be done elsewhere in the code, by the way
		
		// if it's a Help system file ... 		[srk] 8/12/01 12:26AM
		if (Outliner.helpDoxMgr.isThisOneOfOurs(filename) != DOCUMENT_NOT_FOUND) {
			// fuhgedabowdit
			return;
		} // end if
		
		// if this item is not yet in the list
		if (isFileNameUnique(filename)) {
			
			// grab the menu
			RecentFilesList menu = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);
			
			// if the docInfoList is too long ...
			if (docInfoList.size() >= Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur) {
				// remove the oldest item
				removeOldestFileNameFromList(menu) ;
			} // end if the list is too long
			
			// Add the item's docInfo to the docInfoList
			docInfoList.add(docInfo);
	
			// switch on ordering
			switch (currentDisplayOrdering) {
				
			case CHRONO_ORDER:
			default:
				// Add item to menus
				menu.addMenuItemForFileToMenu(docInfo);
				break ; // case CHRONO_ORDER
				
			case ASCII_ORDER:
			case ALFA_ORDER:
				// switch on name form
				switch (currentDisplayNameForm) {
					
				case FULL_PATHNAME: 
				default:
				
					// package the docInfo up with the full pathname
					strungDocInfo= new StrungDocumentInfo(docInfo.getPath(), docInfo) ;
					break ;
					
				case TRUNC_PATHNAME:
					// package the docInfo up with a truncated pathname
					strungDocInfo= new StrungDocumentInfo(
						StanStringTools.getTruncatedPathName(docInfo.getPath()),
						docInfo) ; 
					break ;
					
				case JUST_FILENAME:
					// package the docInfo up with the filename
					strungDocInfo= new StrungDocumentInfo(
						StanStringTools.getFileNameFromPathName(docInfo.getPath()),
						docInfo) ;
					break ;
				
				} // end switch on name form	
				
				// set ascii/alfa switch
				strungDocInfo.setIgnoreCase(currentDisplayOrdering==ALFA_ORDER) ;
				
				// add it to the alfaAscii tree
				alfaAsciiTree.add(strungDocInfo) ;
				
				// adjust the menu items to reflect the new alfaAscii ordering
				menu.syncMenuItems() ;
				
				break ;
				
			} // end switch on ordering
			
		} // end if this item is not yet in the list	
		
	} // end method addFileNameToList

	// remove the oldest item from docInfoList, the menu, and, if necessary, alfaAscii tree
	private static void removeOldestFileNameFromList (RecentFilesList menu) {
		// local vars
		DocumentInfo docInfo = null ;
		StrungDocumentInfo strungDocInfo = null ;
		boolean removeResult = false ;
		
		// switch on our ordering
		switch (currentDisplayOrdering) {
			
		case ALFA_ORDER:
		case ASCII_ORDER:
			// if we've got an alfa-ascii ordering tree 
			if (ensureAlfaAsciiTree()) {
			
				// grab the oldest files's docInfo
				docInfo = (DocumentInfo) docInfoList.get(0) ;
				
				// we're going to package that up
				
				// switch on name form
				switch (currentDisplayNameForm) {
					
				case FULL_PATHNAME: 
				default:
				
					// package the docInfo up with the full pathname
					strungDocInfo= new StrungDocumentInfo(docInfo.getPath(), docInfo) ;
					break ;
					
				case TRUNC_PATHNAME:
					// package the docInfo up with a truncated pathname
					strungDocInfo= new StrungDocumentInfo(
						StanStringTools.getTruncatedPathName(docInfo.getPath()),
						docInfo) ; 
					break ;
					
				case JUST_FILENAME:
					// package the docInfo up with the filename
					strungDocInfo= new StrungDocumentInfo(
						StanStringTools.getFileNameFromPathName(docInfo.getPath()),
						docInfo) ;
					break ;
				
				} // end switch on name form	
				
				// remove the oldest item's entry from the alfaAscii tree
				removeResult = alfaAsciiTree.remove(strungDocInfo) ;
				
			} // end if we're got an alfaAscii tree
			
			// remove the oldest item's entry from the docInfoList
			(docInfoList.remove(0)) ;

			// adjust the menu items to reflect the new alfaAscii ordering
			menu.syncMenuItems() ;
			
			break ;
							
		case CHRONO_ORDER:
		default:
			// depending on our ordering, remove it from menu
			// switch out on direction
			switch (currentDisplayDirection) {
				
			case TOP_TO_BOTTOM:
			default:
				// oldest item is at the top
				menu.remove(0);
				break ;
				
			case BOTTOM_TO_TOP:
				// oldest item is at the bottom
				menu.remove(menu.getItemCount() - 1) ;
				break ;
				
			}  // end switch on direction
			
			// Remove the oldest item in the list
			docInfoList.remove(0);
			
			break ;
		} // end switch on ordering
		
	} // end method 	

	// called by outsiders when the size of the recent files list may have been changed
	static void trim() {
		// grab a handle to this menu
		RecentFilesList menu = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);
		
		// grab the size that's been set
		int sizeGoal = Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur ;
		
		// while we're too big ...
		while (docInfoList.size() > sizeGoal) {
			
			// remove the oldest
			removeOldestFileNameFromList (menu) ;
		} // end while
		
		// if the menu's empty ...
		if (menu.getItemCount() <= 0) {
			// disable it
			menu.setEnabled(false);
		} // end if		
	} // end method trim

	private static boolean isFileNameUnique(String filename) {
		
		for (int i = 0; i < docInfoList.size(); i++) {
			if (filename.equals(((DocumentInfo) docInfoList.get(i)).getPath())) {
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

	// remove an item from the docInfoList, menu, and, if necessary, alfaAscii tree
	public static void removeFileNameFromList(String pathname) {
		// local vars
		int position = -1;
		DocumentInfo docInfo = null ;
		StrungDocumentInfo strungDocInfo = null ;
		RecentFilesList menu = null ;
		
		// is this item in the docInfoList ?
		for (int i = 0; i < docInfoList.size(); i++) {
			docInfo = (DocumentInfo) docInfoList.get(i) ;
			String text = docInfo.getPath();
			if (text.equals(pathname)) {
				position = i;
				break;
			} // end if
		} // end for
		
		// if it wasn't there
		if (position == -1) {
			return;
		} // end if
		
		// it's there

		// grab a ref to this menu
		menu = (RecentFilesList) GUITreeLoader.reg.get
			(GUITreeComponentRegistry.RECENT_FILE_MENU);
		
		// we need to handle this based on ordering
		switch (currentDisplayOrdering) {
			
		case ALFA_ORDER:
		case ASCII_ORDER:
			// if we've got an alfa-ascii ordering tree 
			if (ensureAlfaAsciiTree()) {
			
				// we've got to remove the file's entry there
				
				// we've got the docInfo
				// we're going to package that up
				
				// switch on name form
				switch (currentDisplayNameForm) {
					
				case FULL_PATHNAME: 
				default:
				
					// package the docInfo up with the full pathname
					strungDocInfo= new StrungDocumentInfo(docInfo.getPath(), docInfo) ;
					break ;
					
				case TRUNC_PATHNAME:
					// package the docInfo up with a truncated pathname
					strungDocInfo= new StrungDocumentInfo(
						StanStringTools.getTruncatedPathName(docInfo.getPath()),
						docInfo) ; 
					break ;
					
				case JUST_FILENAME:
					// package the docInfo up with the filename
					strungDocInfo= new StrungDocumentInfo(
						StanStringTools.getFileNameFromPathName(docInfo.getPath()),
						docInfo) ;
					break ;
				
				} // end switch on name form	
				
				// remove this item's entry from the alfaAscii tree
				alfaAsciiTree.remove(strungDocInfo) ;
				
			} // end if we're got an alfaAscii tree

			// Remove item from docInfoList
			docInfoList.remove(position);
			
			// adjust the menu items to reflect the new alfaAscii ordering
			menu.syncMenuItems() ;
			
			break ;
				
		case CHRONO_ORDER:
		default:

			// Remove from docInfoList
			docInfoList.remove(position);

			// now to nail down our position in the menu
			// switch out on direction
			switch (currentDisplayDirection) {
				
			case TOP_TO_BOTTOM:
			default:
				break ;
				
			case BOTTOM_TO_TOP:
				// oldest item is at the bottom
				position = menu.getItemCount() - 1 - position ;
				break ;
				
			}  // end switch on direction
			
			// remove item from menu
			menu.remove(position);

			break ;

		} // end switch on ordering

		// if the menu's empty, disable it
		if (menu.getItemCount() <= 0) {
			menu.setEnabled(false);
		} // end if
		
	} // end method
	

	
	// Config File
	public static void saveConfigFile(String filename) {
		writeObjectToFile(docInfoList, filename);
	} // end method saveConfigFile

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

		// Open or Import the file, as is appropriate
		if (! docInfo.isImported()) {
			FileMenu.openFile(docInfo, protocol);
		} else {
			FileMenu.importFile(docInfo,protocol);
		} // end if-else
		
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