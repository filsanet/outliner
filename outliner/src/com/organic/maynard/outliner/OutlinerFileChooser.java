/**
 * Portions copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002  Stan Krute <Stan@StanKrute.com>
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

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

package com.organic.maynard.outliner;

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.organic.maynard.util.string.Replace;
import javax.swing.filechooser.*;
import com.organic.maynard.util.string.StanStringTools;

public class OutlinerFileChooser extends JFileChooser {

	private JPanel openAccessory = new JPanel();
	private JPanel importAccessory = new JPanel();
	private JPanel saveAccessory = new JPanel();
	private JPanel exportAccessory = new JPanel();

	private JComboBox saveLineEndComboBox = new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
	private JComboBox saveEncodingComboBox = new JComboBox();
	private JComboBox saveFormatComboBox = new JComboBox();

	private JComboBox openEncodingComboBox = new JComboBox();
	private JComboBox openFormatComboBox = new JComboBox();

	private JComboBox importEncodingComboBox = new JComboBox();
	private JComboBox importFormatComboBox = new JComboBox();

	private JComboBox exportLineEndComboBox = new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
	private JComboBox exportEncodingComboBox = new JComboBox();
	private JComboBox exportFormatComboBox = new JComboBox();

	private boolean isInitialized = false; 
	
	// dialogType is here so we know the type of dialog we are, since Swing forces 
	// us to be CUSTOM_DIALOG. Without this the approveSelection() method won't work right.
	private int dialogType = JFileChooser.CUSTOM_DIALOG;

	// The Constructor
	public OutlinerFileChooser(FileSystemView fsv) {
		super(fsv);
	}

	private void lazyInstantiate() {
		if (isInitialized) {
			return;
		}

		// TBD [srk] have different encoding prefs for each of these OPs
		for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
			String encoding = (String) Preferences.ENCODINGS.get(i);
			saveEncodingComboBox.addItem(encoding);
			exportEncodingComboBox.addItem(encoding);
			openEncodingComboBox.addItem(encoding);
			importEncodingComboBox.addItem(encoding);
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_OPEN.size(); i++) {
			openFormatComboBox.addItem((String) Preferences.FILE_FORMATS_OPEN.get(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_IMPORT.size(); i++) {
			importFormatComboBox.addItem((String) Preferences.FILE_FORMATS_IMPORT.get(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_SAVE.size(); i++) {
			saveFormatComboBox.addItem((String) Preferences.FILE_FORMATS_SAVE.get(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_EXPORT.size(); i++) {
			exportFormatComboBox.addItem((String) Preferences.FILE_FORMATS_EXPORT.get(i));
		}

		// Lay out save panel
		Box saveBox = Box.createVerticalBox();

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("line_terminator")), saveBox);
		addSingleItemCentered(saveLineEndComboBox, saveBox);

		saveBox.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_encoding")), saveBox);
		addSingleItemCentered(saveEncodingComboBox, saveBox);

		saveBox.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_format")), saveBox);
		addSingleItemCentered(saveFormatComboBox, saveBox);

		saveAccessory.add(saveBox,BorderLayout.CENTER);

		// Lay out export panel
		Box exportBox = Box.createVerticalBox();

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("line_terminator")), exportBox);
		addSingleItemCentered(exportLineEndComboBox, exportBox);

		exportBox.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_encoding")), exportBox);
		addSingleItemCentered(exportEncodingComboBox, exportBox);

		exportBox.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_format")), exportBox);
		addSingleItemCentered(exportFormatComboBox, exportBox);

		exportAccessory.add(exportBox,BorderLayout.CENTER);

		// Layout open panel
		Box openBox = Box.createVerticalBox();

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_encoding")), openBox);
		addSingleItemCentered(openEncodingComboBox, openBox);

		openBox.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_format")), openBox);
		addSingleItemCentered(openFormatComboBox, openBox);

		openAccessory.add(openBox,BorderLayout.CENTER);

		// Layout import panel
		Box importBox = Box.createVerticalBox();

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_encoding")), importBox);
		addSingleItemCentered(importEncodingComboBox, importBox);

		importBox.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_format")), importBox);
		addSingleItemCentered(importFormatComboBox, importBox);

		importAccessory.add(importBox,BorderLayout.CENTER);

		// Set the flag
		isInitialized = true;
	}

	private static void addSingleItemCentered(JComponent component, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		component.setMaximumSize(component.getPreferredSize());
		box.add(component);
		box.add(Box.createHorizontalGlue());
		container.add(box);
	}


	//----------------------------- Configure Methods -----------------------------------
	public void configureForExport(OutlinerDocument doc, String protocolName, String currentDirectory) {
		lazyInstantiate();

		// adjust title
		setDialogTitle("Export: " + protocolName);

		// adjust approve button
		setApproveButtonToolTipText ("Export file as named") ;
		// [srk] this next doesn't work right - so we do this adjustment in the file protocol's code
		// setApproveButtonText ("Export") ;

		// Set the Accessory state
		setAccessory(exportAccessory);

		// Set the Accessory GUI state.
		exportLineEndComboBox.setSelectedItem(doc.settings.getLineEnd().cur);
		exportEncodingComboBox.setSelectedItem(doc.settings.getSaveEncoding().cur);
		exportFormatComboBox.setSelectedItem(doc.settings.getSaveFormat().cur);

		// Set the current directory location or selected file.
		String currentFileName = doc.getFileName();
		if (!currentFileName.equals("")) {
			setSelectedFile(new File(currentFileName));
		} else {
			setCurrentDirectory(new File(currentDirectory));
			setSelectedFile(null);
		}
		
		this.dialogType = JFileChooser.SAVE_DIALOG;
	}

	public void configureForSave(OutlinerDocument doc, String protocolName, String currentDirectory) {
		lazyInstantiate();

		// adjust title
		setDialogTitle("Save: " + protocolName);

		// adjust approve button
		setApproveButtonToolTipText ("Save file as named") ;
		// [srk] this next doesn't work right - so we do this adjustment in the file protocol's code
		// setApproveButtonText ("Save") ;

		// Set the Accessory state
		setAccessory(saveAccessory);

		// Set the Accessory GUI state.
		saveLineEndComboBox.setSelectedItem(doc.settings.getLineEnd().cur);
		saveEncodingComboBox.setSelectedItem(doc.settings.getSaveEncoding().cur);
		saveFormatComboBox.setSelectedItem(doc.settings.getSaveFormat().cur);

		// Set the current directory location or selected file.
		// grab the file's name
		String currentFileName = doc.getFileName();
		
		// if it's an imported file ...
		if (doc.getDocumentInfo().isImported()) {
			// trim any extension off the file name
			String trimmedFileName = StanStringTools.trimFileExtension(currentFileName) ;

			// obtain the current default save format's extension
			String extension = 	(Outliner.fileFormatManager.getSaveFormat(doc.settings.getSaveFormat().cur)).getDefaultExtension() ;
			
			// addemup
			setSelectedFile(new File(trimmedFileName + "." + extension)) ;
		
		// else it's not an imported file
		} else {
			// if it has a name ... 
			if (!currentFileName.equals("")) {
				
				// set up using the filename
				setSelectedFile(new File(currentFileName));
				
			// else it has no name (it's a new file)
		} else {
				// use the current directory
				setCurrentDirectory(new File(currentDirectory));
				
				// start with the window title
				String title = doc.getTitle() ;
				
				// obtain the current default save format's extension
				String extension = 	(Outliner.fileFormatManager.getSaveFormat(doc.settings.getSaveFormat().cur)).getDefaultExtension() ;
			
				// addemup
				setSelectedFile(new File(title + "." + extension)) ;
				
			} // end else it has no name
			
		} // end else it's not imported
		
		this.dialogType = JFileChooser.SAVE_DIALOG;
		
	} // end method configureForSave

	public void configureForOpen(String protocolName, String currentDirectory) {
		lazyInstantiate();

		// adjust title
		setDialogTitle("Open: " + protocolName);

		// adjust approve button
		setApproveButtonToolTipText ("Open selected file") ;
		// [srk] this next doesn't work reliably - so we do this adjustment in the file protocol's code
		// setApproveButtonText ("Open") ;
		
		// Set the Accessory state.
		setAccessory(openAccessory);

		// Set the Accessory GUI state.
		openEncodingComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_ENCODING).cur);
		openFormatComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_FORMAT).cur);

		// Set the current directory location and selected file.
		setCurrentDirectory(new File(currentDirectory));
		setSelectedFile(null);
		
		this.dialogType = JFileChooser.OPEN_DIALOG;
	}


	public void configureForImport(String protocolName, String currentDirectory) {
		lazyInstantiate();

		// adjust title
		setDialogTitle("Import: " + protocolName);
		
		// adjust approve button
		setApproveButtonToolTipText ("Import selected file") ;
		// [srk] this next doesn't work right - so we do this adjustment in the file protocol's code
		// setApproveButtonText ("Import") ;

		// Set the Accessory state.
		setAccessory(importAccessory);

		// Set the Accessory GUI state.
		importEncodingComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.IMPORT_ENCODING).cur);
		importFormatComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.IMPORT_FORMAT).cur);

		// Set the current directory location and selected file.
		setCurrentDirectory(new File(currentDirectory));
		setSelectedFile(null);
		
		this.dialogType = JFileChooser.OPEN_DIALOG;
	}



	// Accessors
// public String getLineEnding() {return (String) lineEndComboBox.getSelectedItem();}

	public String getOpenEncoding() {return (String) openEncodingComboBox.getSelectedItem();}
	public String getOpenFileFormat() {return (String) openFormatComboBox.getSelectedItem();}

	public String getImportEncoding() {return (String) importEncodingComboBox.getSelectedItem();}
	public String getImportFileFormat() {return (String) importFormatComboBox.getSelectedItem();}

	public String getSaveLineEnding() {return (String) saveLineEndComboBox.getSelectedItem();}
	public String getSaveEncoding() {return (String) saveEncodingComboBox.getSelectedItem();}
	public String getSaveFileFormat() {return (String) saveFormatComboBox.getSelectedItem();}

	public String getExportLineEnding() {return (String) exportLineEndComboBox.getSelectedItem();}
	public String getExportEncoding() {return (String) exportEncodingComboBox.getSelectedItem();}
	public String getExportFileFormat() {return (String) exportFormatComboBox.getSelectedItem();}


	// Overriden Methods of JFileChooser
	public void approveSelection() {
		File file = getSelectedFile();
		
		if (this.dialogType == JFileChooser.OPEN_DIALOG) {
			// Alert if file does not exist.
			if (!file.exists()) {
				String msg = GUITreeLoader.reg.getText("error_file_not_found");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, file.getPath());

				JOptionPane.showMessageDialog(this, msg);
				return;
			}
		} else if (this.dialogType == JFileChooser.SAVE_DIALOG) {
			// Alert if file exists.
			if (file.exists()) {
				// Custom button text
				String yes = GUITreeLoader.reg.getText("yes");
				String no = GUITreeLoader.reg.getText("no");
				String confirm_replacement = GUITreeLoader.reg.getText("confirm_replacement");
				String msg = GUITreeLoader.reg.getText("confirmation_replace_file");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, file.getPath());

				Object[] options = {yes, no};
				int result = JOptionPane.showOptionDialog(this,
					msg,
					confirm_replacement,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]
				);
				if (result == JOptionPane.YES_OPTION) {
					// Proceed normally.
				} else if (result == JOptionPane.NO_OPTION) {
					return;
				} else {
					return;
				}
			}
		}

		super.approveSelection();
	}
}
