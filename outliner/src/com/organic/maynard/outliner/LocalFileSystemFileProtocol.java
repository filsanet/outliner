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

import javax.swing.*;
import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.Replace;

public class LocalFileSystemFileProtocol extends AbstractFileProtocol {

	private OutlinerFileChooser chooser = null;
	
	private boolean isInitialized = false;
	
	// Constructors
	public LocalFileSystemFileProtocol() {}

	private void lazyInstantiation() {
		if (isInitialized) {
			return;
		}
		
		chooser = new OutlinerFileChooser(null);
		
		isInitialized = true;	
	}
	
	// FileProtocol Interface
	public boolean selectFileToSave(OutlinerDocument document, int type) {
		lazyInstantiation();
		
		// Setup the File Chooser
		if (type == FileProtocol.SAVE) {
			chooser.configureForSave(document, getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur);
		} else if (type == FileProtocol.EXPORT) {
			chooser.configureForExport(document, getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur);
		} else {
			System.out.println("ERROR: invalid save type used. (" + type +")");
			return false;
		}

		int option = chooser.showSaveDialog(Outliner.outliner);
		
		// Update the most recent save dir preference
		Preferences.getPreferenceString(Preferences.MOST_RECENT_SAVE_DIR).cur = chooser.getCurrentDirectory().getPath();
		Preferences.getPreferenceString(Preferences.MOST_RECENT_SAVE_DIR).restoreTemporaryToCurrent();
				
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
			String lineEnd = chooser.getExportLineEnding();
			String encoding = chooser.getExportEncoding();
			String fileFormat = chooser.getExportFileFormat();

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
		chooser.configureForOpen(getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur);
		
		int option = chooser.showOpenDialog(Outliner.outliner);

		// Update the most recent save dir preference
		Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur = chooser.getCurrentDirectory().getPath();
		Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).restoreTemporaryToCurrent();

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
		return FileFormatManager.writeFile(docInfo.getPath(), docInfo.getOutputBytes());
	}
	
	public boolean openFile(DocumentInfo docInfo) {		
		String msg = null;
		
		try {
			docInfo.setInputStream(new FileInputStream(docInfo.getPath()));
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