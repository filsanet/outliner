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
 * Last Touched: 8/12/01 11:29AM
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
