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

public class SaveAsFileMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
		
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		saveAsOutlinerDocument(Outliner.getMostRecentDocumentTouched());
	}

	protected static void saveAsOutlinerDocument(OutlinerDocument document) {
		// Setup the File Chooser
		Outliner.chooser.configureForSave(document);

		int option = Outliner.chooser.showSaveDialog(Outliner.outliner);
		
		// Update the most recent save dir preference
		Preferences.MOST_RECENT_SAVE_DIR.cur = Outliner.chooser.getCurrentDirectory().getPath();
		Preferences.MOST_RECENT_SAVE_DIR.restoreTemporaryToCurrent();
				
		// Handle User Input
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = Outliner.chooser.getSelectedFile().getPath();
			if (!Outliner.isFileNameUnique(filename) && (!filename.equals(document.getFileName()))) {
				JOptionPane.showMessageDialog(Outliner.outliner, "Cannot save to file: " + filename + " it is currently open.");
				// We might want to move this test into the approveSelection method of the file chooser.
				return;
			}
			
			// Pull Preference Values from the file chooser
			String lineEnd = Outliner.chooser.getLineEnding();
			String encoding = Outliner.chooser.getSaveEncoding();
			String fileFormat = Outliner.chooser.getSaveFileFormat();

			// Update the document settings
			document.settings.lineEnd.def = lineEnd;
			document.settings.lineEnd.cur = lineEnd;
			document.settings.lineEnd.tmp = lineEnd;
			document.settings.saveEncoding.def = encoding;
			document.settings.saveEncoding.cur = encoding;
			document.settings.saveEncoding.tmp = encoding;
			document.settings.saveFormat.def = fileFormat;
			document.settings.saveFormat.cur = fileFormat;
			document.settings.saveFormat.tmp = fileFormat;
			
			FileMenu.saveFile(filename,document,true);
		}
	}
}