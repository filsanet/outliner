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

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;

import com.organic.maynard.util.string.*;

public class DocumentSettings extends JDialog implements ActionListener {
	OutlinerDocument doc = null;
	
	// Editable Settings
	String sLineEnd = Preferences.getPreferenceLineEnding(Preferences.LINE_END).cur;
	String sSaveEncoding = Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur;
	String sSaveFormat = Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur;
	String sOwnerName = Preferences.getPreferenceString(Preferences.OWNER_NAME).cur;
	String sOwnerEmail = Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur;
	
	public PreferenceLineEnding lineEnd = new PreferenceLineEnding(sLineEnd, sLineEnd, "");
	public PreferenceString saveEncoding = new PreferenceString(sSaveEncoding, sSaveEncoding, "");
	public PreferenceString saveFormat = new PreferenceString(sSaveFormat, sSaveFormat, "");

	public PreferenceString ownerName = new PreferenceString(sOwnerName, sOwnerName, "");
	public PreferenceString ownerEmail = new PreferenceString(sOwnerEmail, sOwnerEmail, "");

	// Hidden Settings
	public String dateCreated = new String("");
	public String dateModified = new String("");
	//public PreferenceString title = new PreferenceString(Preferences.SAVE_FORMAT.cur,Preferences.SAVE_FORMAT.cur,"");

	// GUI Elements
	public JButton buttonOK = new JButton("OK");
	public JButton buttonCancel = new JButton("Cancel");
	public JButton buttonRestoreToGlobal = new JButton("Restore to Global");
	public JComboBox lineEndComboBox = new JComboBox(Preferences.PLATFORM_IDENTIFIERS);
	public JComboBox saveEncodingComboBox = new JComboBox();
	public JComboBox saveFormatComboBox = new JComboBox();
	public JTextField ownerNameField = new JTextField(10);
	public JTextField ownerEmailField = new JTextField(10);

	// The Constructors
	public DocumentSettings(OutlinerDocument document) {
		//super(document,"Document Settings",true);
		
		this.doc = document;
		
		// Create the Layout
		setSize(250,325);
		setResizable(false);
		
		// Define the Bottom Panel
		JPanel bottomPanel = new JPanel();

		bottomPanel.setLayout(new FlowLayout());
		
		buttonOK.addActionListener(this);
		bottomPanel.add(buttonOK);

		buttonCancel.addActionListener(this);
		bottomPanel.add(buttonCancel);

		getContentPane().add(bottomPanel,BorderLayout.SOUTH);
		
		// Setup ComboBoxes
		for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
			saveEncodingComboBox.addItem((String) Preferences.ENCODINGS.elementAt(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_SAVE.size(); i++) {
			saveFormatComboBox.addItem((String) Preferences.FILE_FORMATS_SAVE.elementAt(i));
		}

		lineEndComboBox.addItemListener(new ComboBoxListener(lineEndComboBox, lineEnd));
		saveEncodingComboBox.addItemListener(new ComboBoxListener(saveEncodingComboBox, saveEncoding));		
		saveFormatComboBox.addItemListener(new ComboBoxListener(saveFormatComboBox, saveFormat));		

		ownerNameField.addFocusListener(new TextFieldListener(ownerNameField, ownerName));
		ownerEmailField.addFocusListener(new TextFieldListener(ownerEmailField, ownerEmail));
			
		// Define the Center Panel
		buttonRestoreToGlobal.addActionListener(this);
		
		Box box = Box.createVerticalBox();

		addSingleItemCentered(new JLabel("Line Terminator"), box);
		addSingleItemCentered(lineEndComboBox, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("Encoding when saving."), box);
		addSingleItemCentered(saveEncodingComboBox, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("Format when saving."), box);
		addSingleItemCentered(saveFormatComboBox, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("Owner Name"), box);
		addSingleItemCentered(ownerNameField, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("Owner Email"), box);
		addSingleItemCentered(ownerEmailField, box);

		box.add(Box.createVerticalStrut(10));

		addSingleItemCentered(buttonRestoreToGlobal, box);

		getContentPane().add(box,BorderLayout.CENTER);
	
		// Set the default button
		getRootPane().setDefaultButton(buttonOK);
	}
	
	public void destroy() {
		doc = null;
		lineEnd = null;
		saveEncoding = null;
		saveFormat = null;
		ownerName = null;
		ownerEmail = null;
		dateCreated = null;
		dateModified = null;
		buttonOK = null;
		buttonCancel = null;
		buttonRestoreToGlobal = null;
		lineEndComboBox = null;
		saveEncodingComboBox = null;
		saveFormatComboBox = null;
		ownerNameField = null;
		ownerEmailField = null;
		
		removeNotify();
		removeAll();
	}

	public boolean useDocumentSettings = false;

	public void show() {
		syncPrefs();
		useDocumentSettings = true;
		super.show();
	}
		
	public void syncPrefs() {		
		lineEnd.restoreTemporaryToCurrent();
		lineEndComboBox.setSelectedItem(lineEnd.tmp);

		saveEncoding.restoreTemporaryToCurrent();
		saveEncodingComboBox.setSelectedItem(saveEncoding.tmp);

		saveFormat.restoreTemporaryToCurrent();
		saveFormatComboBox.setSelectedItem(saveFormat.tmp);

		ownerName.restoreTemporaryToCurrent();
		ownerNameField.setText(ownerName.tmp);

		ownerEmail.restoreTemporaryToCurrent();
		ownerEmailField.setText(ownerEmail.tmp);
	}
	
	// Misc methods
	private void addSingleItemCentered(JComponent component, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		component.setMaximumSize(component.getPreferredSize());
		box.add(component);
		box.add(Box.createHorizontalGlue());
		container.add(box);
	}

	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			ok();
		} else if (e.getActionCommand().equals("Cancel")) {
			cancel();
		} else if (e.getActionCommand().equals("Restore to Global")) {
			restoreToGlobal();
		}
	}
	
	private void ok() {
		if (!lineEnd.cur.equals(lineEnd.tmp) || !saveEncoding.cur.equals(saveEncoding.tmp) || !saveFormat.cur.equals(saveFormat.tmp)) {
			doc.setFileModified(true);
		}
		
		lineEnd.cur = lineEnd.tmp;
		saveEncoding.cur = saveEncoding.tmp;
		saveFormat.cur = saveFormat.tmp;
		ownerName.cur = ownerName.tmp;
		ownerEmail.cur = ownerEmail.tmp;
		this.hide();
	}

	private void cancel() {
		this.hide();
	}

	private void restoreToGlobal() {
		lineEnd.tmp = Preferences.getPreferenceLineEnding(Preferences.LINE_END).cur;
		saveEncoding.tmp = Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur;
		saveFormat.tmp = Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur;
		ownerName.tmp = Preferences.getPreferenceString(Preferences.OWNER_NAME).cur;
		ownerEmail.tmp = Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur;
		
		lineEndComboBox.setSelectedItem(lineEnd.tmp);
		saveEncodingComboBox.setSelectedItem(saveEncoding.tmp);
		saveFormatComboBox.setSelectedItem(saveFormat.tmp);
		ownerNameField.setText(ownerName.tmp);
		ownerEmailField.setText(ownerEmail.tmp);
	}
}
