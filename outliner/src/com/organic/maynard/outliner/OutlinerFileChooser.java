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

public class OutlinerFileChooser extends JFileChooser {
	
	// Constants
	//public static final String MODE_SAVE = "save";
	//public static final String MODE_OPEN = "open";

	// These 3 prefs seem useless. I think they only deal with setting initial state.
	//private PreferenceString chooserLineEnd = null;
	//private PreferenceString chooserEncoding = null;
	//private PreferenceString chooserFileFormat = null;

	private JPanel openAccessory = new JPanel();
	private JPanel saveAccessory = new JPanel();
	
	private JComboBox lineEndComboBox = new JComboBox(Preferences.PLATFORM_IDENTIFIERS);
	private JComboBox saveEncodingComboBox = new JComboBox();
	private JComboBox openEncodingComboBox = new JComboBox();
	private JComboBox openFormatComboBox = new JComboBox();
	private JComboBox saveFormatComboBox = new JComboBox();

	
	// The Constructor
	public OutlinerFileChooser() {
		//chooserLineEnd = new PreferenceString(Preferences.LINE_END.cur,Preferences.LINE_END.cur,"");
		//chooserEncoding = new PreferenceString(Preferences.SAVE_ENCODING.cur,Preferences.SAVE_ENCODING.cur,"");
		//chooserFileFormat = new PreferenceString(Preferences.SAVE_FORMAT.cur,Preferences.SAVE_FORMAT.cur,"");

		//lineEndComboBox.addItemListener(new ComboBoxListener(lineEndComboBox, chooserLineEnd));
		//encodingComboBox.addItemListener(new ComboBoxListener(encodingComboBox, chooserEncoding));
		//openEncodingComboBox.addItemListener(new ComboBoxListener(openEncodingComboBox, chooserEncoding));
		//openFormatComboBox.addItemListener(new ComboBoxListener(openFormatComboBox, chooserFileFormat));
		//saveFormatComboBox.addItemListener(new ComboBoxListener(saveFormatComboBox, chooserFileFormat));
		
		for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
			String encoding = (String) Preferences.ENCODINGS.elementAt(i);
			saveEncodingComboBox.addItem(encoding);
			openEncodingComboBox.addItem(encoding);
		}
		
		for (int i = 0; i < Preferences.FILE_FORMATS_OPEN.size(); i++) {
			openFormatComboBox.addItem((String) Preferences.FILE_FORMATS_OPEN.elementAt(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_SAVE.size(); i++) {
			saveFormatComboBox.addItem((String) Preferences.FILE_FORMATS_SAVE.elementAt(i));
		}
		
		// Layout save panel
		Box box = Box.createVerticalBox();

		addSingleItemCentered(new JLabel("Line Terminator"), box);
		addSingleItemCentered(lineEndComboBox, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("File Encoding"), box);
		addSingleItemCentered(saveEncodingComboBox, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("File Format"), box);
		addSingleItemCentered(saveFormatComboBox, box);

		saveAccessory.add(box,BorderLayout.CENTER);
		
		// Layout open panel
		Box box2 = Box.createVerticalBox();

		addSingleItemCentered(new JLabel("File Encoding"), box2);
		addSingleItemCentered(openEncodingComboBox, box2);

		box2.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("File Format"), box2);
		addSingleItemCentered(openFormatComboBox, box2);

		openAccessory.add(box2,BorderLayout.CENTER);
	}

	private static void addSingleItemCentered(JComponent component, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		component.setMaximumSize(component.getPreferredSize());
		box.add(component);
		box.add(Box.createHorizontalGlue());
		container.add(box);
	}
	
	public void configureForSave(OutlinerDocument doc) {
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
			setCurrentDirectory(new File(Preferences.MOST_RECENT_SAVE_DIR.cur));
			setSelectedFile(null);
		}
	}

	public void configureForOpen(String lineEnding, String encoding, String format) {
		// Set the Accessory state.
		setAccessory(openAccessory);
		
		// Set the Accessory GUI state.
		openEncodingComboBox.setSelectedItem(encoding);
		openFormatComboBox.setSelectedItem(format);

		// Set the current directory location and selected file.
		setCurrentDirectory(new File(Preferences.MOST_RECENT_OPEN_DIR.cur));
		setSelectedFile(null);
	}

	
	// Accessors
	public String getLineEnding() {return (String) lineEndComboBox.getSelectedItem();}
	public String getOpenEncoding() {return (String) openEncodingComboBox.getSelectedItem();}
	public String getSaveEncoding() {return (String) saveEncodingComboBox.getSelectedItem();}
	public String getOpenFileFormat() {return (String) openFormatComboBox.getSelectedItem();}
	public String getSaveFileFormat() {return (String) saveFormatComboBox.getSelectedItem();}
}