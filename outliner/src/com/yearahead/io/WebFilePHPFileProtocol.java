/**
 * Portions copyright (C) 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002   Stan Krute <Stan@StanKrute.com>
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
package com.yearahead.io;

import com.organic.maynard.outliner.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import com.organic.maynard.util.string.Replace;
import javax.swing.filechooser.*;

public class WebFilePHPFileProtocol extends AbstractFileProtocol {

	// private instance vars
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

	
	// select a file to save or export
	public boolean selectFileToSave(OutlinerDocument document, int type) {
		lazyInstantiation();
		
		String hostname = null;
		try {
			URL url = new URL(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur);
			hostname = url.getHost();
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			return false;
		}
		// Setup the File Chooser to save or export
		if (type == FileProtocol.SAVE) {
			chooser.configureForSave(document, getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur);
		} else if (type == FileProtocol.EXPORT) {
			chooser.configureForExport(document, getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur);
		} else {
			System.out.println("ERROR: invalid save/export type used. (" + type +")");
			return false;
		}

		// run the File chooser
		int option = chooser.showSaveDialog(Outliner.outliner);
				
		// Handle User Input
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = chooser.getSelectedFile().getPath();
			
			String lineEnd ;
			String encoding ;
			String fileFormat ;

			if (!Outliner.isFileNameUnique(filename) && (!filename.equals(document.getFileName()))) {
				String msg = GUITreeLoader.reg.getText("message_cannot_save_file_already_open");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);

				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				// We might want to move this test into the approveSelection method of the file chooser.
				return false;
			}
			
			// Pull Preference Values from the file chooser		[srk] 1/1/02 3:02AM
			switch (type) {
				case FileProtocol.SAVE:
					lineEnd = chooser.getSaveLineEnding();
					encoding = chooser.getSaveEncoding();
					fileFormat = chooser.getSaveFileFormat();
					break ;
				case FileProtocol.EXPORT:
					lineEnd = chooser.getExportLineEnding();
					encoding = chooser.getExportEncoding();
					fileFormat = chooser.getExportFileFormat();
					break ;
				default:
					System.out.println("ERROR: invalid save type used. (" + type +")");
					return false;
				} // end switch
				

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
	
	// select a file to open or import
	public boolean selectFileToOpen(DocumentInfo docInfo, int type) {
		lazyInstantiation();
		
		String hostname = null;
		try {
			URL url = new URL(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur);
			hostname = url.getHost();
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			return false;
		}
		// Setup the File Chooser to open or import
		switch (type) {
			case FileProtocol.OPEN:
				chooser.configureForOpen(getName(), hostname);
				break ;
			case FileProtocol.IMPORT:
				chooser.configureForImport(getName(), hostname);
				break ;
			default:
				System.out.println("ERROR: invalid open/import type used. (" + type +")");
				return false;
			} // end switch

		// run the File Chooser
		int option = chooser.showOpenDialog(Outliner.outliner);

		// Handle User Input
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = chooser.getSelectedFile().getPath();
			
			String encoding ;
			String fileFormat ;
			
			// pull proper preference values from the file chooser
			switch (type) {
				case FileProtocol.OPEN:
					encoding = chooser.getOpenEncoding();
					fileFormat = chooser.getOpenFileFormat();
					break ;
				case FileProtocol.IMPORT:
					encoding = chooser.getImportEncoding();
					fileFormat = chooser.getImportFileFormat();
					break ;
				default:
					System.out.println("ERROR: invalid open/import type used. (" + type +")");
					return false;
				} // end switch

			// store data into docInfo structure
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