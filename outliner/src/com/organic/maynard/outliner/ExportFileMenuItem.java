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

import java.awt.event.*;
import javax.swing.*;
import org.xml.sax.*;
import com.organic.maynard.util.string.Replace;

public class ExportFileMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
		
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		exportOutlinerDocument(Outliner.getMostRecentDocumentTouched());
	}

	protected static void exportOutlinerDocument(OutlinerDocument document) {
		// Setup the File Chooser
		Outliner.chooser.configureForExport(document);

		int option = Outliner.chooser.showSaveDialog(Outliner.outliner);
		
		// Update the most recent save dir preference
		Preferences.getPreferenceString(Preferences.MOST_RECENT_SAVE_DIR).cur = Outliner.chooser.getCurrentDirectory().getPath();
		Preferences.getPreferenceString(Preferences.MOST_RECENT_SAVE_DIR).restoreTemporaryToCurrent();
				
		// Handle User Input
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = Outliner.chooser.getSelectedFile().getPath();
			
			if (!Outliner.isFileNameUnique(filename) && (!filename.equals(document.getFileName()))) {
				String msg = GUITreeLoader.reg.getText("message_cannot_save_file_already_open");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);

				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				// We might want to move this test into the approveSelection method of the file chooser.
				return;
			}
			
			// Pull Preference Values from the file chooser
			String lineEnd = Outliner.chooser.getExportLineEnding();
			String encoding = Outliner.chooser.getExportEncoding();
			String fileFormat = Outliner.chooser.getExportFileFormat();

			// We need to swap in a new documentSettings object so that the changes don't carry over
			// to the open document, but are conveyed to the export. We'll put the real object back
			// when we're done.
			
			DocumentSettings oldSettings = document.settings;
			
			document.settings = new DocumentSettings(document);
			
			document.settings.lineEnd.def = lineEnd;
			document.settings.lineEnd.cur = lineEnd;
			document.settings.lineEnd.tmp = lineEnd;
			document.settings.saveEncoding.def = encoding;
			document.settings.saveEncoding.cur = encoding;
			document.settings.saveEncoding.tmp = encoding;
			document.settings.saveFormat.def = fileFormat;
			document.settings.saveFormat.cur = fileFormat;
			document.settings.saveFormat.tmp = fileFormat;
			
			FileMenu.exportFile(filename,document);
			
			// Swap it back the settings
			document.settings = oldSettings;
		}
	}
}