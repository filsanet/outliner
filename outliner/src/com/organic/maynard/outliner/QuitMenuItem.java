/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
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