/**
 * HelpUserGuideMenuItem class
 * 
 * Runs the Help:UserGuide command
 *	if the Help UserGuide is not open, opens it
 *	if the Help UserGuide is open, makes sure it's foremost
 * 	
 * 	
 * extends AbstractOutlinerMenuItem 
 * implements ActionListener, GUITreeComponent {

 *
 * Members
 *	methods
 * 		instance
 * 			public
 * 				void startSetup(AttributeList)
 * 				void actionPerformed(ActionEvent)
 * 		class
 * 			protected
 * 				int openHelpUserGuideDocument()
 *		
 *		
 * Portions copyright (C) 2000-2001 Maynard Demmon <maynard@organic.com>
 * Portions copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 8/11/01 9:31PM
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

// we need these modules
import java.awt.event.*;
import org.xml.sax.*;

// The Help:UserGuide menu item
public class HelpUserGuideMenuItem 

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
		// if the Help UserGuide document is open ...
		if (Outliner.helpDoxMgr.documentIsOpen(Outliner.helpDoxMgr.USER_GUIDE)) {

			// make sure it's frontmost
			Outliner.menuBar.windowMenu.changeToWindow
				(Outliner.getDocument(Outliner.helpDoxMgr.getDocPath 
				(Outliner.helpDoxMgr.USER_GUIDE)));
			
			} // end if
		else {
			// it's not open; try to open it
			int result = openHelpUserGuideDocument() ;
			
			} // end else
		
		} // end actionPerformed


	// try to open up the Help system's User Guide document
	protected static int openHelpUserGuideDocument() {
		// get set up 
		String encoding = "ISO-8859-1";
		String fileFormat = "OPML";
		
		DocumentInfo docInfo = new DocumentInfo();
		
		docInfo.setPath(Outliner.helpDoxMgr.getDocPath (Outliner.helpDoxMgr.USER_GUIDE));
		docInfo.setEncodingType(encoding);
		docInfo.setFileFormat(fileFormat);
		
		// TODO fix this once FileMenu returns a jrc code
		// return (FileMenu.openFile(docInfo);
		// we be tres fakey for now
		FileMenu.openFile(docInfo);
		return SUCCESS ;

		} // end openHelpUserGuideDocument

	} // end HelpUserGuideMenuItem