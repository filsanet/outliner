/**
 * Copyright (C) 2001 Maynard Demmon, maynard@organic.com
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

package com.yearahead.io;

import com.organic.maynard.outliner.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import com.organic.maynard.util.string.Replace;
import javax.swing.filechooser.*;

public class WebFilePHPFileProtocol extends AbstractFileProtocol {

	private OutlinerFileChooser chooser = null;
	
	private boolean isInitialized = false;
	
	// Constructors
	public WebFilePHPFileProtocol() {}
	
	private void lazyInstantiation() {
		if (isInitialized) {
			return;
		}
		
		FileSystemView fsv = new WebFileSystemView(
			Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur,
			Preferences.getPreferenceString(Preferences.WEB_FILE_USER).cur,
			Preferences.getPreferenceString(Preferences.WEB_FILE_PASSWORD).cur
		);
		
		chooser = new OutlinerFileChooser(fsv);
		
		isInitialized = true;
	}

	
	// FileProtocol Interface
	public boolean selectFileToSave(OutlinerDocument document, int type) {
		lazyInstantiation();
		
		// Setup the File Chooser
		String hostname = null;
		try {
			URL url = new URL(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur);
			hostname = url.getHost();
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			return false;
		}
		
		if (type == FileProtocol.SAVE) {
			chooser.configureForSave(document, getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur);
		} else if (type == FileProtocol.EXPORT) {
			chooser.configureForExport(document, getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur);
		} else {
			System.out.println("ERROR: invalid save type used. (" + type +")");
			return false;
		}

		int option = chooser.showSaveDialog(Outliner.outliner);
				
		// Handle User Input
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = chooser.getSelectedFile().getPath();
			
			if (!Outliner.isFileNameUnique(filename) && (!filename.equals(document.getFileName()))) {
				String msg = GUITreeLoader.reg.getText("message_cannot_save_file_already_open");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);

				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				// We might want to move this test into the approveSelection method of the file chooser.
				return false;
			}
			
			// Pull Preference Values from the file chooser
			String lineEnd = chooser.getLineEnding();
			String encoding = chooser.getSaveEncoding();
			String fileFormat = chooser.getSaveFileFormat();

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
			
			// Update Document Info
			DocumentInfo docInfo = document.getDocumentInfo();
			docInfo.setPath(filename);
			docInfo.setLineEnding(lineEnd);
			docInfo.setEncodingType(encoding);
			docInfo.setFileFormat(fileFormat);
						
			return true;
		} else {
			return false;
		}
	}
	
	public boolean selectFileToOpen(DocumentInfo docInfo) {
		lazyInstantiation();
		
		// Setup the File Chooser
		String hostname = null;
		try {
			URL url = new URL(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur);
			hostname = url.getHost();
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			return false;
		}
		chooser.configureForOpen(getName(), hostname);
		
		int option = chooser.showOpenDialog(Outliner.outliner);

		// Handle User Input
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = chooser.getSelectedFile().getPath();
			String encoding = chooser.getOpenEncoding();
			String fileFormat = chooser.getOpenFileFormat();
			
			docInfo.setPath(filename);
			docInfo.setEncodingType(encoding);
			docInfo.setFileFormat(fileFormat);
			
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean saveFile(DocumentInfo docInfo) {
		try {
			return WebFile.save(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur, docInfo.getPath(), docInfo.getOutputBytes());
		} catch(IOException x) {
			x.printStackTrace();
			return false;
		}
	}
	
	public boolean openFile(DocumentInfo docInfo) {
		String msg = null;
		
		try {
			docInfo.setInputStream(WebFile.open(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur, docInfo.getPath()));
			return true;
						
		} catch (FileNotFoundException fnfe) {
			msg = GUITreeLoader.reg.getText("error_file_not_found");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, docInfo.getPath());

			JOptionPane.showMessageDialog(Outliner.outliner, msg);
			RecentFilesList.removeFileNameFromList(docInfo.getPath());
			return false;
			
		} catch (Exception e) {
			msg = GUITreeLoader.reg.getText("error_could_not_open_file");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, docInfo.getPath());

			JOptionPane.showMessageDialog(Outliner.outliner, msg);
			RecentFilesList.removeFileNameFromList(docInfo.getPath());
			return false;
		}	
	}
}