/**
 * HelpBookmarksMenuItem class
 * 
 * Implements the Help:Bookmarks command
 *	if the Help Bookmarks document is not open, opens it
 *	if the Help Bookmarks document is open, makes sure it's foremost
 * 
 * extends AbstractOutlinerMenuItem 
 * implements ActionListener, GUITreeComponent {
.*
 *
 *  Members
 *	methods
 * 		instance
 * 			public
 * 				void startSetup(AttributeList)
 * 				void actionPerformed(ActionEvent)
 * 		class
 * 			protected
 * 				int openHelpBookmarksDocument()
 *		
 * Portions copyright (C) 2000-2001 Maynard Demmon <maynard@organic.com>
 * Portions copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
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

import java.awt.event.*;
import org.xml.sax.*;

public class HelpBookmarksMenuItem 

	extends AbstractOutlinerMenuItem 
	implements ActionListener, GUITreeComponent, JoeReturnCodes {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		// have the ancestors handle their setup
		super.startSetup(atts);
		
		// let's listen up for action events
		addActionListener(this);
		
		// we start out live
		setEnabled(true);
		} // end startSetup


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// if the Help Bookmarks document is open ...
		if (Outliner.helpDoxMgr.documentIsOpen(Outliner.helpDoxMgr.BOOKMARKS)) {
			
			// make sure it's frontmost
			Outliner.menuBar.windowMenu.changeToWindow
				(Outliner.getDocument(Outliner.helpDoxMgr.getDocPath 
				(Outliner.helpDoxMgr.BOOKMARKS)));
			
			} // end if
		else {
			// it's not open; try to open it
			int result = openHelpBookmarksDocument() ;
			
			} // end else
		
		} // end actionPerformed


	// try to open up the Help system's Bookmarks document
	protected static int openHelpBookmarksDocument() {
		// set up 
		String encoding = "ISO-8859-1";
		String fileFormat = "OPML";
		
		DocumentInfo docInfo = new DocumentInfo();
		
		docInfo.setPath(Outliner.helpDoxMgr.getDocPath (Outliner.helpDoxMgr.BOOKMARKS));
		docInfo.setEncodingType(encoding);
		docInfo.setFileFormat(fileFormat);

		// we are a help file
		docInfo.setHelpFile(true) ;
		
		// TODO fix this once FileMenu returns a jrc code
		// return (FileMenu.openFile(docInfo);
		// we be tres fakey for now
		FileMenu.openFile(docInfo, Outliner.fileProtocolManager.getDefault());
		
		// done
		return SUCCESS ;

		} // end openHelpBookmarksDocument

	} // end HelpBookmarksMenuItem