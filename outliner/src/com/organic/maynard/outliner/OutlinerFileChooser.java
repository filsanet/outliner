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
	private JPanel saveAccessory = new JPanel();
	private JPanel exportAccessory = new JPanel();
	
	private JComboBox lineEndComboBox = new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
	private JComboBox saveEncodingComboBox = new JComboBox();
	private JComboBox saveFormatComboBox = new JComboBox();

	private JComboBox openEncodingComboBox = new JComboBox();
	private JComboBox openFormatComboBox = new JComboBox();

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
		
		for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
			String encoding = (String) Preferences.ENCODINGS.elementAt(i);
			saveEncodingComboBox.addItem(encoding);
			exportEncodingComboBox.addItem(encoding);
			openEncodingComboBox.addItem(encoding);
		}
		
		for (int i = 0; i < Preferences.FILE_FORMATS_OPEN.size(); i++) {
			openFormatComboBox.addItem((String) Preferences.FILE_FORMATS_OPEN.elementAt(i));
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
		addSingleItemCentered(lineEndComboBox, box);

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

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_encoding")), box2);
		addSingleItemCentered(openEncodingComboBox, box2);

		box2.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel(GUITreeLoader.reg.getText("file_format")), box2);
		addSingleItemCentered(openFormatComboBox, box2);

		openAccessory.add(box2,BorderLayout.CENTER);
		
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
		lineEndComboBox.setSelectedItem(doc.settings.lineEnd.cur);
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
		openEncodingComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_ENCODING).cur);
		openFormatComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_FORMAT).cur);

		// Set the current directory location and selected file.
		setCurrentDirectory(new File(currentDirectory));
		setSelectedFile(null);
	}

	
	// Accessors
	public String getLineEnding() {return (String) lineEndComboBox.getSelectedItem();}
	public String getOpenEncoding() {return (String) openEncodingComboBox.getSelectedItem();}
	public String getSaveEncoding() {return (String) saveEncodingComboBox.getSelectedItem();}
	public String getOpenFileFormat() {return (String) openFormatComboBox.getSelectedItem();}
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
