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
 
package com.organic.maynard.outliner;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.organic.maynard.util.string.Replace;
import javax.swing.filechooser.*;

public class OutlinerFileChooser extends JFileChooser {

	private JPanel openAccessory = new JPanel();
	private JPanel importAccessory = new JPanel();
	private JPanel saveAccessory = new JPanel();
	private JPanel exportAccessory = new JPanel();
	
	private JComboBox saveLineEndComboBox = new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
	private JComboBox saveEncodingComboBox = new JComboBox();
	private JComboBox saveFormatComboBox = new JComboBox();

	private JComboBox openLineEndComboBox = new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
	private JComboBox openEncodingComboBox = new JComboBox();
	private JComboBox openFormatComboBox = new JComboBox();

	private JComboBox importLineEndComboBox = new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
	private JComboBox importEncodingComboBox = new JComboBox();
	private JComboBox importFormatComboBox = new JComboBox();

	private JComboBox exportLineEndComboBox = new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
	private JComboBox exportEncodingComboBox = new JComboBox();
	private JComboBox exportFormatComboBox = new JComboBox();

	private boolean isInitialized = false;
	
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
			String encoding = (String) Preferences.ENCODINGS.elementAt(i);
			saveEncodingComboBox.addItem(encoding);
			exportEncodingComboBox.addItem(encoding);
			openEncodingComboBox.addItem(encoding);
			importEncodingComboBox.addItem(encoding);
		}
		
		for (int i = 0; i < Preferences.FILE_FORMATS_OPEN.size(); i++) {
			openFormatComboBox.addItem((String) Preferences.FILE_FORMATS_OPEN.elementAt(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_IMPORT.size(); i++) {
			importFormatComboBox.addItem((String) Preferences.FILE_FORMATS_IMPORT.elementAt(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_SAVE.size(); i++) {
			saveFormatComboBox.addItem((String) Preferences.FILE_FORMATS_SAVE.elementAt(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_EXPORT.size(); i++) {
			exportFormatComboBox.addItem((String) Preferences.FILE_FORMATS_EXPORT.elementAt(i));
		}
		
		// Layout save panel
		Box box = Box.createVerticalBox();

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("line_terminator")), box);
		addSingleItemCentered(saveLineEndComboBox, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_encoding")), box);
		addSingleItemCentered(saveEncodingComboBox, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_format")), box);
		addSingleItemCentered(saveFormatComboBox, box);

		saveAccessory.add(box,BorderLayout.CENTER);

		// Layout export panel
		Box box3 = Box.createVerticalBox();

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("line_terminator")), box3);
		addSingleItemCentered(exportLineEndComboBox, box3);

		box3.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_encoding")), box3);
		addSingleItemCentered(exportEncodingComboBox, box3);

		box3.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_format")), box3);
		addSingleItemCentered(exportFormatComboBox, box3);

		exportAccessory.add(box3,BorderLayout.CENTER);
		
		// Layout open panel
		Box box2 = Box.createVerticalBox();

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("line_terminator")), box2);
		addSingleItemCentered(openLineEndComboBox, box2);

		box2.add(Box.createVerticalStrut(5));
		
		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_encoding")), box2);
		addSingleItemCentered(openEncodingComboBox, box2);

		box2.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_format")), box2);
		addSingleItemCentered(openFormatComboBox, box2);

		openAccessory.add(box2,BorderLayout.CENTER);
		
		// Layout import panel
		Box box4 = Box.createVerticalBox();

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("line_terminator")), box4);
		addSingleItemCentered(importLineEndComboBox, box4);

		box4.add(Box.createVerticalStrut(5));
		
		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_encoding")), box4);
		addSingleItemCentered(importEncodingComboBox, box4);

		box4.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_format")), box4);
		addSingleItemCentered(importFormatComboBox, box4);

		importAccessory.add(box4,BorderLayout.CENTER);
		
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


	// Configure Methods
	public void configureForExport(OutlinerDocument doc, String protocolName, String currentDirectory) {
		lazyInstantiate();
		
		setDialogTitle("Export: " + protocolName);
		
		// Set the Accessory state
		setAccessory(exportAccessory);
		
		// Set the Accessory GUI state.
		exportLineEndComboBox.setSelectedItem(doc.settings.lineEnd.cur);
		exportEncodingComboBox.setSelectedItem(doc.settings.saveEncoding.cur);
		exportFormatComboBox.setSelectedItem(doc.settings.saveFormat.cur);

		// Set the current directory location or selected file.
		String currentFileName = doc.getFileName();
		if (!currentFileName.equals("")) {
			setSelectedFile(new File(currentFileName));
		} else {
			setCurrentDirectory(new File(currentDirectory));
			setSelectedFile(null);
		}
	}
		
	public void configureForSave(OutlinerDocument doc, String protocolName, String currentDirectory) {
		lazyInstantiate();

		setDialogTitle("Save: " + protocolName);
		
		// Set the Accessory state
		setAccessory(saveAccessory);
		
		// Set the Accessory GUI state.
		saveLineEndComboBox.setSelectedItem(doc.settings.lineEnd.cur);
		saveEncodingComboBox.setSelectedItem(doc.settings.saveEncoding.cur);
		saveFormatComboBox.setSelectedItem(doc.settings.saveFormat.cur);

		// Set the current directory location or selected file.
		String currentFileName = doc.getFileName();
		if (!currentFileName.equals("")) {
			setSelectedFile(new File(currentFileName));
		} else {
			setCurrentDirectory(new File(currentDirectory));
			setSelectedFile(null);
		}
	}

	public void configureForOpen(String protocolName, String currentDirectory) {
		lazyInstantiate();

		setDialogTitle("Open: " + protocolName);

		// Set the Accessory state.
		setAccessory(openAccessory);
		
		// Set the Accessory GUI state.
		//openLineEndComboBox.setSelectedItem(doc.settings.lineEnd.cur);
		openEncodingComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_ENCODING).cur);
		openFormatComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_FORMAT).cur);

		// Set the current directory location and selected file.
		setCurrentDirectory(new File(currentDirectory));
		setSelectedFile(null);
	}


	public void configureForImport(String protocolName, String currentDirectory) {
		lazyInstantiate();

		setDialogTitle("Import: " + protocolName);

		// Set the Accessory state.
		setAccessory(openAccessory);
		
		// Set the Accessory GUI state.
//		importLineEndComboBox.setSelectedItem(doc.settings.lineEnd.cur);
		importEncodingComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.IMPORT_ENCODING).cur);
		importFormatComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.IMPORT_FORMAT).cur);

		// Set the current directory location and selected file.
		setCurrentDirectory(new File(currentDirectory));
		setSelectedFile(null);
	}


	
	// Accessors
//	public String getLineEnding() {return (String) lineEndComboBox.getSelectedItem();}

	public String getOpenLineEnding() {return (String) openLineEndComboBox.getSelectedItem();}
	public String getOpenEncoding() {return (String) openEncodingComboBox.getSelectedItem();}
	public String getOpenFileFormat() {return (String) openFormatComboBox.getSelectedItem();}

	public String getImportLineEnding() {return (String) importLineEndComboBox.getSelectedItem();}
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
    	
		if (getDialogType() == JFileChooser.OPEN_DIALOG) {
			// Alert if file does not exist.
			if (!file.exists()) {
				String msg = GUITreeLoader.reg.getText("error_file_not_found");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, file.getPath());

				JOptionPane.showMessageDialog(this, msg);
				return;
			}
		} else if (getDialogType() == JFileChooser.SAVE_DIALOG) {
			// Alert if file exists.
			if (file.exists()) {
				//Custom button text
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
