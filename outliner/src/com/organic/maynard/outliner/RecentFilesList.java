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
	private static final int JUST_FILENAME = 1 ;
	// direction
	private static final int TOP_TO_BOTTOM = 0 ;
	private static final int BOTTOM_TO_TOP = 1 ;
	
	// Static Fields
	private static ArrayList docInfoList = null;
	/* [srk] docInfoList holds DocumentInfo data for a number of documents
		currently that's the number of items shown in the recent files list
		2B decoupled, so it'll have it's own value
		that way, one can slide from lotsa files shown to few files shown 
		and back to lotsa again without losing everybody   */
	
	private static TreeSet filenameTree = null ; // [srk] for alfa/ascii ordering, we store filename strings here
	private static TreeSet pathnameTree = null ; // [srk] for alfa/ascii ordering, we store pathname strings here
	/* these two treesets are used for alfa/ascii ordering
		one stores ordered pathnames
		one stores ordered filenames 	*/	
	
	private static int currentDisplayOrdering = -1 ; // [srk] we start with these values to force a menu population
	private static int currentDisplayNameForm = -1 ;
	private static int currentDisplayDirection = -1 ;
	
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
		
		// Try to load the list of recent files from disk.
		docInfoList = (ArrayList) ReadObjectFromFile(Outliner.RECENT_FILES_FILE);
		
		// if nothing was read from disk
		if (docInfoList == null) {
			// start a new list
			docInfoList = new ArrayList();
		} // end if
		
		// apply our display settings
		// this will also populate the menu
		applyDisplaySettings() ;
		
		
	} // end methor startSetup
	
	// call on our UI panel to apply the latest display options
	private void applyDisplaySettings () {
		
		PreferencesPanelRecentFiles prefsPanel = (PreferencesPanelRecentFiles) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_PANEL_RECENT_FILES);
		prefsPanel.applyCurrentToApplication() ;
		
	} // end method applyDisplaySettings
	
	// set display options -- rebuild menu and alfaAscii structures if necessary
	// called from PreferencesPanelRecentFiles
	void setDisplayOptions (int ordering, int nameForm, int direction) {
		// has anything changed ?
		boolean change = false ;
		
		// see if we've got a change
		change = (currentDisplayOrdering != ordering)
			|| (currentDisplayNameForm !=nameForm)
			|| (currentDisplayDirection != direction) ;
		
		// if we do have a change ...
		if (change) {
			// apply the new values
			currentDisplayOrdering = ordering ;
			currentDisplayNameForm = nameForm ;
			currentDisplayDirection = direction ;
			
			// populate the menu
			populateMenu() ;
			
		} // end if we have a change
		
	} // end method setDisplayOptions
	
	// populate the menu with file designators
	private void populateMenu () {
		StrungDocumentInfo sdi = null ;
		
		// clear out any existing menu entries
		removeAll() ;
		
		// if docInfoList is empty, do nothing
		if (docInfoList.size() == 0) {
			return ;
		} // end if
		
		// switch on ordering
		switch (currentDisplayOrdering) {
			
		case CHRONO_ORDER:
		default:
			// add each item in doc list order to the menu
			for (int i = 0, size=docInfoList.size(); i < size; i++) {
				addFileName((DocumentInfo) docInfoList.get(i));
			} // end for

			break ; // case CHRONO_ORDER
			
		case ASCII_ORDER:
		case ALFA_ORDER:
			// switch on nameform
			switch (currentDisplayNameForm) {
				
			case FULL_PATHNAME: 
			default:
				// add each item in pathname tree order to the menu 
				for (Iterator i = pathnameTree.iterator(); i.hasNext();) {
					sdi = (StrungDocumentInfo) i.next();
					addFileName(sdi.getDocumentInfo());
				} // end for
			
				break ;
				
			case JUST_FILENAME:
				// add each item in filename tree order to the menu 
				for (Iterator i = filenameTree.iterator(); i.hasNext();) {
					sdi = (StrungDocumentInfo) i.next();
					addFileName(sdi.getDocumentInfo());
				} // end for
			
				break ;
		
			} // end switch on nameform
			
			
			break ;
			
		} // end switch on currentDisplayOrdering	
		
	} // end method populateMenu
		

	public void endSetup(AttributeList atts) {}

	// add a file to the menu list
	private void addFileName(DocumentInfo docInfo) {
		
		RecentFilesListItem item = null ;
		StrungDocumentInfo strungItem = null ;
		
		// switch on nameform to create item
		switch (currentDisplayNameForm) {
			
		case FULL_PATHNAME: 
		default:
			// create a list item
			item = new RecentFilesListItem(docInfo.getPath(), docInfo);
			break ;
			
		case JUST_FILENAME:
			// create a list item
			item = new RecentFilesListItem(
				StanStringTools.getFileNameFromPathName(docInfo.getPath()), 
				docInfo);
			break ;
		
		} // end switch on nameform
		
		// we want it to listen to this
		item.addActionListener(this);
		
		// switch on ordering
		switch (currentDisplayOrdering) {
			
		case ALFA_ORDER:
		case ASCII_ORDER:
			// switch on nameform
			switch (currentDisplayNameForm) {
				
			case FULL_PATHNAME: 
			default:
				// if we've got a pathname tree 
				if (ensurePathnameTree()) {
					// create and add an item to it
					strungItem = new StrungDocumentInfo(docInfo.getPath(), docInfo) ;
					pathnameTree.add(strungItem) ;
				} // end if
				break ;
				
			case JUST_FILENAME:
				// if we've got a filename tree 
				if (ensureFilenameTree()) {
					// create and add an item to it
					strungItem = new StrungDocumentInfo(
						StanStringTools.getFileNameFromPathName(docInfo.getPath()), 
						docInfo) ;
					filenameTree.add(strungItem) ;
				} // end if
				break ;
			} // end switch on nameform
			
			// easiest to repopulate the menu
			//;
			
			break ;
				
		case CHRONO_ORDER:
		default:
			// switch out on direction
			switch (currentDisplayDirection) {
				
			case TOP_TO_BOTTOM:
			default:
				// append that item to the menu
				add(item);
				break ;
				
			case BOTTOM_TO_TOP:
				// prepend that item to the menu
				insert(item,0);
				break ;
				
			}  // end switch on direction
		
			break ;
		} // end switch on ordering

		// since we've got at least one item on our menu, we're enabled
		setEnabled(true);

	} // end method addFileName

	private static boolean ensureFilenameTree () {
		
		// if we're pointin' to a treeset, we're cool
		if ( (filenameTree != null) && (filenameTree.getClass().getName() == "TreeSet") ) {
			return true ;
		} // end if
		
		// if not, let's try to create one
		filenameTree = new TreeSet() ;
		
		// return a result
		return ( (filenameTree != null) && (filenameTree.getClass().getName() == "TreeSet") ) ;
		
	} // end method ensureFilenameTree

	private static boolean ensurePathnameTree () {
		
		// if we're pointin' to a treeset, we're cool
		if ( (pathnameTree != null) && (pathnameTree.getClass().getName() == "TreeSet") ) {
			return true ;
		} // end if
		
		// if not, let's try to create one
		pathnameTree = new TreeSet() ;
		
		// return a result
		return ( (pathnameTree != null) && (pathnameTree.getClass().getName() == "TreeSet") ) ;
		
	} // end method ensurePathnameTree

	// Static methods
	
	// this method is called when a file is open or imported
	static void addFileNameToList(DocumentInfo docInfo) {
		String filename = docInfo.getPath();
		
		// Short Circuit if user-set size of list is zero
		if (Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur == 0) {
			return;
		}
		
		// if it's a Help system file ... 		[srk] 8/12/01 12:26AM
		if (Outliner.helpDoxMgr.isThisOneOfOurs(filename) != DOCUMENT_NOT_FOUND) {
			// fuhgedabowdit
			return;
		}
		// if this item is not yet in the list
		if (isFileNameUnique(filename)) {
			
			RecentFilesList menu = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);
			
			// if the list is too long ...
			if (docInfoList.size() >= Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur) {
				// remove oldest item
				removeOldestItem(menu) ;
			} // end if the list is too long
			
			// Add it to the lists
			docInfoList.add(docInfo);

			// Add to menus
			menu.addFileName(docInfo);
			
		} // end if this item is not yet in the list	
		
	} // end method addFileNameToList

	private static void removeOldestItem (RecentFilesList menu) {
		
		switch (currentDisplayOrdering) {
			
		case ALFA_ORDER:
		case ASCII_ORDER:
/*			// if we've got an alfa-ascii ordering tree 
			if (ensureAlfaAsciiTree()) {
			
				// remove this item's entry from that tree
				alfaAsciiTree.remove(docInfoList.get(0)) ;
				
			} // end if we're got an alfaAscii tree
*/			
			break ;
							
		case CHRONO_ORDER:
		default:
			break ;
		} // end switch on ordering

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
		
	} // end method 	


	static void trim() {
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

	public static void removeFileNameFromList(String filename) {
		int index = -1;
		
		// is this item in the docInfoList ?
		for (int i = 0; i < docInfoList.size(); i++) {
			String text = ((DocumentInfo) docInfoList.get(i)).getPath();
			if (text.equals(filename)) {
				index = i;
				break;
			} // end if
		} // end for
		
		// if it wasn't there
		if (index == -1) {
			return;
		} // end if
		
		// it's there
		
		// need to remove any alfaAscii tree entries
		// and find our location if we're alfa/ascii-ordered
		switch (currentDisplayOrdering) {
			
		case ALFA_ORDER:
		case ASCII_ORDER:
			// if we've got an alfa-ascii ordering tree 
/*			if (ensureAlfaAsciiTree()) {
			
				// find our position in the menu
				// TBD
				
				// remove this item's entry from the tree
				// alfaAsciiTree.remove(docInfoList.get(0)) ;
				
			} // end if we're got an alfaAscii tree
*/			
			break ;
				
		case CHRONO_ORDER:
		default:
			break ;
		} // end switch on ordering

		// Remove from docInfoList
		docInfoList.remove(index);

		// grab a ref to the menu
		RecentFilesList menu = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);

		// adjust our index if running bottom to top
		// switch out on direction
		switch (currentDisplayDirection) {
			
		case TOP_TO_BOTTOM:
		default:
			break ;
			
		case BOTTOM_TO_TOP:
			// oldest item is at the bottom
			index = menu.getItemCount() - 1 - index ;
			break ;
			
		}  // end switch on direction
		
		// leave the menu
		menu.remove(index);
		
		// if the menu's empty, disable it
		if (menu.getItemCount() <= 0) {
			menu.setEnabled(false);
		} // end if
		
	} // end method
	

	
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