/**
 * HelpMenu class
 * 
 * Runs the Help menu
 *	routes menu commands
 * 
 * extends AbstractOutlinerMenu
 * implements GUITreeComponent 
 * 
 *
 * Members
 *	methods
 * 		instance
 * 			public
 * 				HelpMenu ()
 * 				void startSetup(AttributeList)
 * 		class
 * 			public
 * 				void updateHelpMenu()
 *
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
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
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
import javax.swing.*;
import org.xml.sax.*;

public class HelpMenu 
	extends AbstractOutlinerMenu 
	implements GUITreeComponent {

	// constructors
	public HelpMenu() {
		super();
	
		} // end constructor HelpMenu


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {

		// let the ancestors set up first
		super.startSetup(atts);

		// let ourselves be known
		Outliner.menuBar.helpMenu = this;

		// we're live
		setEnabled(true) ;
		
		// call startSetup on the Help documents manager
		Outliner.helpDoxMgr.startSetup() ;

		} // end method startSetup


	// Misc Methods
	public static void updateHelpMenu() {

		// route the call to the particular menu item
		JMenuItem item = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.GOTO_MENU_ITEM);
		
		}  // end method updateHelpMenu
	
	}  // end class HelpMenu
