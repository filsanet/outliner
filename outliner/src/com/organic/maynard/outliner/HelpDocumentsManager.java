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

import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.outliner.dom.Document;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

// we manage a set of Help system documents
public class HelpDocumentsManager extends DocumentManager implements JoeReturnCodes {
	
	// Constants
	public static final int USER_GUIDE = 0 ;
	public static final int DEVELOPER_GUIDE = 1 ;
	public static final int BOOKMARKS = 2 ;
	public static final int TUTORIALS = 3 ;
	public static final int ABOUT = 4 ;
	public static final int HELP_DOX_COUNT = 5 ;
	
	// Constructors
	public HelpDocumentsManager() {
		super(HELP_DOX_COUNT);
	}

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
		setDocPath(USER_GUIDE,      Preferences.getPreferenceString(Preferences.USER_GUIDE_PATH).def);
		setDocPath(DEVELOPER_GUIDE, Preferences.getPreferenceString(Preferences.DEVELOPER_GUIDE_PATH).def);
		setDocPath(BOOKMARKS,       Preferences.getPreferenceString(Preferences.BOOKMARKS_PATH).def);
		setDocPath(TUTORIALS,       Preferences.getPreferenceString(Preferences.TUTORIALS_PATH).def);
		setDocPath(ABOUT,           Preferences.getPreferenceString(Preferences.ABOUT_PATH).def);
	}

	protected void docClosingChores(Document document) {
		RecentFilesList.removeFileNameFromList(document.getDocumentInfo());
	}		
}