/**
 * HelpDocumentsManager class
 * 
 * Manages sets of Help system documents
 *	tracks whether they're open or shut
 *	stores their pathnames
 *		pathnames can come in many forms
 *		we just store 'em
 *		examples
 *			local:  c:\someDir\someDoc.opml
 *			network: \\some_machine\some_sharepoint\someDir\someDoc.opml
 *			internet: http://someSite.dom/someDir/someDoc.opml
 *			internet: ftp://someSite.dom/someDir/someDoc.opml
 * 
 * extends DocumentManager
 * implements JoeReturnCodes
 *
 * Members
 *	constants
 *		class
 *			public
 *				int USER_GUIDE
 *				int DEVELOPER_GUIDE
 *				int BOOKMARKS
 *				int TUTORIALS
 *				int ABOUT
 *				int HELP_DOX_COUNT
 *
 *	methods
 * 		instance
 *			public
 *				HelpDocumentsManager ()
 *				void startSetup ()
 *				void someDocumentJustOpened (OutlinerDocument) 
 *				void someDocumentJustClosed (OutlinerDocument) 
 *
 *		
 * Copyright (C) 2001 Stan Krute, Stan@StanKrute.com
 * Last Touched: 8/12/01 7:58PM
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

// we're part of this
package com.organic.maynard.outliner;

// we manage a set of Help system documents
public class HelpDocumentsManager 

	extends DocumentManager {
	
	// public class constants
	public static final int USER_GUIDE = 0 ;
	public static final int DEVELOPER_GUIDE = 1 ;
	public static final int BOOKMARKS = 2 ;
	public static final int TUTORIALS = 3 ;
	public static final int ABOUT = 4 ;
	public static final int HELP_DOX_COUNT = 5 ;
	
	
	// constructor method
	public HelpDocumentsManager() {
		// call the ancestor's constructor
		super(HELP_DOX_COUNT);
		
		} // end constructor method HelpDocumentsManager

	// this next is called by a new Help menu's startSetup GUITreeComponent interface
	// we've got to wait for that call to do this, because it means the preference files have been read
	public void startSetup() {

		/* add our help document paths to the management set
		 * these live in the preferences file[s]
		 * 
		 * note: if these are relative rather than absolute, 
		 * 	they are relative to Outliner's root directory,
		 * 	and setDocPath makes 'em absolute
		 */	
		setDocPath(USER_GUIDE, 
			Preferences.getPreferenceString(Preferences.USER_GUIDE_PATH).def);
		setDocPath(DEVELOPER_GUIDE, 
			Preferences.getPreferenceString(Preferences.DEVELOPER_GUIDE_PATH).def);
		setDocPath(BOOKMARKS,
			Preferences.getPreferenceString(Preferences.BOOKMARKS_PATH).def);
		setDocPath(TUTORIALS,
			Preferences.getPreferenceString(Preferences.TUTORIALS_PATH).def);
		setDocPath(ABOUT,
			Preferences.getPreferenceString(Preferences.ABOUT_PATH).def);
		
		} // end method startSetup	


	// a document just opened
	// if it's one of ours, mark it so
	public void someDocumentJustOpened (OutlinerDocument document) {	
		
		// local vars
		int whichOne = isThisOneOfOurs(document.getTitle());
		
		// if it's one of ours ...
		if (whichOne != DOCUMENT_NOT_FOUND) {
			
			// mark it open
			docOpenStates[whichOne] = true ;
			} // END if
		
		} // end method someDocumentJustOpened

		
	// a document just closed
	// if it's one of ours, mark it so
	public void someDocumentJustClosed (OutlinerDocument document) {	
		
		// local vars
		int whichOne = isThisOneOfOurs(document.getTitle());
		
		// if it's one of ours ...
		if (whichOne != DOCUMENT_NOT_FOUND) {
			
			// mark it closed
			docOpenStates[whichOne] = false ;
			} // END if
		
		} // end method someDocumentJustClosed

		
	} // end class HelpDocumentsManager