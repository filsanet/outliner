/**
 * Copyright (C) 2000 Maynard Demmon, maynard@organic.com
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
import java.io.*;
import java.awt.event.*;
import org.xml.sax.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class QuitMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		quit();
	}


	// Static Methods
	public static void quit() {
		if (!CloseAllFileMenuItem.closeAllOutlinerDocuments()) {
			return;
		}
		
		// Store current window position
		Dimension size = Outliner.outliner.getSize();
		Point location = Outliner.outliner.getLocation();
		
		Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_W).cur = size.width;
		Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_H).cur = size.height;
		Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_X).cur = location.x;
		Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_Y).cur = location.y;

		// Hide Desktop
		Outliner.outliner.setVisible(false);
		//Outliner.outliner.dispose();
		
		// Save config and quit
		Preferences.saveConfigFile(Outliner.CONFIG_FILE);
		RecentFilesList.saveConfigFile(Outliner.RECENT_FILES_FILE);
		Outliner.findReplace.model.saveConfigFile();
		LoadScriptCommand.saveConfigFile(new File(Outliner.SCRIPTS_FILE));

		// Run shutdown scripts. This is the last thing we do before quitting.
		ScriptsManagerModel.runShutdownScripts();

		System.exit(0);
	}
}