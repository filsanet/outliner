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

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.xml.sax.*;

// WebFile
import com.yearahead.io.*;

public class OpenFileMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		openOutlinerDocument();
	}

	protected static void openOutlinerDocument() {
		// Setup the File Chooser
		Outliner.chooser.configureForOpen(null, Preferences.OPEN_ENCODING.cur, Preferences.OPEN_FORMAT.cur);
		
		int option = Outliner.chooser.showOpenDialog(Outliner.outliner);

		// Update the most recent save dir preference
		Preferences.MOST_RECENT_OPEN_DIR.cur = Outliner.chooser.getCurrentDirectory().getPath();
		Preferences.MOST_RECENT_OPEN_DIR.restoreTemporaryToCurrent();

		// Handle User Input
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = Outliner.chooser.getSelectedFile().getPath();
			if (!Outliner.isFileNameUnique(filename)) {
				JOptionPane.showMessageDialog(Outliner.outliner, "The file: " + filename + " is already open.");
				
				// Change to the open window.
				Outliner.menuBar.windowMenu.changeToWindow(Outliner.getDocument(filename));
				return;
			}
			
			// Pull Preference Values from the file chooser
			String encoding = Outliner.chooser.getOpenEncoding();
			String fileFormat = Outliner.chooser.getOpenFileFormat();

			DocumentInfo docInfo = new DocumentInfo();
			docInfo.setPath(filename);
			docInfo.setEncodingType(encoding);
			docInfo.setFileFormat(fileFormat);
			
			FileMenu.openFile(docInfo);
		}
	}
}