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
import javax.swing.*;

public class DocumentSettings extends JDialog implements ActionListener {
	private OutlinerDocument doc = null;
	
	
	// Constants
	private static final String DOCUMENT_SETTINGS = "Document Preferences";

	private static final String OK = "OK";
	private static final String CANCEL = "Cancel";
	private static final String RESTORE_TO_GLOBAL = "Restore to Global";
	private static final String LINE_TERMINATOR = "Line Terminator";
	private static final String ENCODING_WHEN_SAVING = "Encoding when saving.";
	private static final String FORMAT_WHEN_SAVING = "Format when saving.";
	private static final String OWNER_NAME = "Owner Name";
	private static final String OWNER_EMAIL = "Owner Email";

	
	// Editable Settings
	private String sLineEnd = Preferences.getPreferenceLineEnding(Preferences.LINE_END).cur;
	private String sSaveEncoding = Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur;
	private String sSaveFormat = Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur;
	private String sOwnerName = Preferences.getPreferenceString(Preferences.OWNER_NAME).cur;
	private String sOwnerEmail = Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur;
	
	public PreferenceLineEnding lineEnd = new PreferenceLineEnding(sLineEnd, sLineEnd, "");
	public PreferenceString saveEncoding = new PreferenceString(sSaveEncoding, sSaveEncoding, "");
	public PreferenceString saveFormat = new PreferenceString(sSaveFormat, sSaveFormat, "");

	public PreferenceString ownerName = new PreferenceString(sOwnerName, sOwnerName, "");
	public PreferenceString ownerEmail = new PreferenceString(sOwnerEmail, sOwnerEmail, "");

	// Hidden Settings
	public String dateCreated = new String("");
	public String dateModified = new String("");

	// GUI Elements
	private JButton buttonOK = new JButton(OK);
	private JButton buttonCancel = new JButton(CANCEL);
	private JButton buttonRestoreToGlobal = new JButton(RESTORE_TO_GLOBAL);
	private JComboBox lineEndComboBox = new JComboBox(Preferences.PLATFORM_IDENTIFIERS);
	private JComboBox saveEncodingComboBox = new JComboBox();
	private JComboBox saveFormatComboBox = new JComboBox();
	private JTextField ownerNameField = new JTextField(10);
	private JTextField ownerEmailField = new JTextField(10);

	// The Constructors
	public DocumentSettings(OutlinerDocument document) {
		super(Outliner.outliner, DOCUMENT_SETTINGS, true);
		
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

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(LINE_TERMINATOR), box);
		AbstractPreferencesPanel.addSingleItemCentered(lineEndComboBox, box);

		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(ENCODING_WHEN_SAVING), box);
		AbstractPreferencesPanel.addSingleItemCentered(saveEncodingComboBox, box);

		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(FORMAT_WHEN_SAVING), box);
		AbstractPreferencesPanel.addSingleItemCentered(saveFormatComboBox, box);

		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(OWNER_NAME), box);
		AbstractPreferencesPanel.addSingleItemCentered(ownerNameField, box);

		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(OWNER_EMAIL), box);
		AbstractPreferencesPanel.addSingleItemCentered(ownerEmailField, box);

		box.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(buttonRestoreToGlobal, box);

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

		Rectangle r = Outliner.outliner.getBounds();
		setLocation((int) (r.getCenterX() - getWidth()/2), (int) (r.getCenterY() - getHeight()/2));

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


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(OK)) {
			ok();
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		} else if (e.getActionCommand().equals(RESTORE_TO_GLOBAL)) {
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
