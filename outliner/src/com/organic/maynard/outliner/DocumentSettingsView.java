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
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;

public class DocumentSettingsView extends JDialog implements ActionListener, GUITreeComponent, JoeXMLConstants {
	
	// Constants
	private static final String DOCUMENT_SETTINGS = "Document Preferences";

	protected static final String OK = "OK";
	protected static final String CANCEL = "Cancel";
	protected static final String RESTORE_TO_GLOBAL = "Restore to Global";
	
	private static final String LINE_TERMINATOR = "Line Terminator";
	private static final String ENCODING_WHEN_SAVING = "Encoding when saving.";
	private static final String FORMAT_WHEN_SAVING = "Format when saving.";
	private static final String OWNER_NAME = "Owner Name";
	private static final String OWNER_EMAIL = "Owner Email";


	// GUI Elements
	protected Box box = Box.createVerticalBox();

	protected JButton buttonOK = new JButton(OK);
	protected JButton buttonCancel = new JButton(CANCEL);
	protected JButton buttonRestoreToGlobal = new JButton(RESTORE_TO_GLOBAL);
	
	protected JComboBox lineEndComboBox = new JComboBox(Preferences.PLATFORM_IDENTIFIERS);
	protected JComboBox saveEncodingComboBox = new JComboBox();
	protected JComboBox saveFormatComboBox = new JComboBox();
	protected JTextField ownerNameField = new JTextField(10);
	protected JTextField ownerEmailField = new JTextField(10);
	
	protected ComboBoxListener lineEndComboBoxListener = new ComboBoxListener(lineEndComboBox, null);
	protected ComboBoxListener saveEncodingComboBoxListener = new ComboBoxListener(saveEncodingComboBox, null);
	protected ComboBoxListener saveFormatComboBoxListener = new ComboBoxListener(saveFormatComboBox, null);	
	protected TextFieldListener ownerNameTextFieldListener = new TextFieldListener(ownerNameField, null);	
	protected TextFieldListener ownerEmailTextFieldListener = new TextFieldListener(ownerEmailField, null);	


	// The Constructors
	public DocumentSettingsView() {
		super(Outliner.outliner, "", true);
	}

	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}

	public void startSetup(AttributeList atts) {
		setTitle(atts.getValue(A_TITLE));
		
		setVisible(false);
				
		// Create the Layout
		setSize(250,325);
		
		// Define the Bottom Panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		bottomPanel.add(buttonOK);
		bottomPanel.add(buttonCancel);
		getContentPane().add(bottomPanel,BorderLayout.SOUTH);
		
		// Setup ComboBoxes
		for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
			saveEncodingComboBox.addItem((String) Preferences.ENCODINGS.elementAt(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_SAVE.size(); i++) {
			saveFormatComboBox.addItem((String) Preferences.FILE_FORMATS_SAVE.elementAt(i));
		}

		// Add Listeners
		buttonOK.addActionListener(this);
		buttonCancel.addActionListener(this);
		buttonRestoreToGlobal.addActionListener(this);

		lineEndComboBox.addItemListener(lineEndComboBoxListener);
		saveEncodingComboBox.addItemListener(saveEncodingComboBoxListener);		
		saveFormatComboBox.addItemListener(saveFormatComboBoxListener);		
		ownerNameField.addFocusListener(ownerNameTextFieldListener);
		ownerEmailField.addFocusListener(ownerEmailTextFieldListener);
			
		// Define the Center Panel
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

		JScrollPane jsp = new JScrollPane(box);
		Box outterBox = Box.createVerticalBox();

		outterBox.add(jsp);
		
		outterBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(buttonRestoreToGlobal, outterBox);

		getContentPane().add(outterBox,BorderLayout.CENTER);
	
		// Set the default button
		getRootPane().setDefaultButton(buttonOK);
	}
	
	public void endSetup(AttributeList atts) {}
	

	// Configuration 
	private DocumentSettings docSettings = null;
	
	public void configureAndShow(DocumentSettings docSettings) {
		this.docSettings = docSettings;
		
		syncPrefs();
		docSettings.useDocumentSettings = true;

		Rectangle r = Outliner.outliner.getBounds();
		setLocation((int) (r.getCenterX() - getWidth()/2), (int) (r.getCenterY() - getHeight()/2));

		super.show();
	}
		
	public void syncPrefs() {		
		docSettings.lineEnd.restoreTemporaryToCurrent();
		lineEndComboBoxListener.setPreference(docSettings.lineEnd);
		lineEndComboBox.setSelectedItem(docSettings.lineEnd.tmp);

		docSettings.saveEncoding.restoreTemporaryToCurrent();
		saveEncodingComboBoxListener.setPreference(docSettings.saveEncoding);
		saveEncodingComboBox.setSelectedItem(docSettings.saveEncoding.tmp);

		docSettings.saveFormat.restoreTemporaryToCurrent();
		saveFormatComboBoxListener.setPreference(docSettings.saveFormat);
		saveFormatComboBox.setSelectedItem(docSettings.saveFormat.tmp);

		docSettings.ownerName.restoreTemporaryToCurrent();
		ownerNameTextFieldListener.setPreference(docSettings.ownerName);
		ownerNameField.setText(docSettings.ownerName.tmp);

		docSettings.ownerEmail.restoreTemporaryToCurrent();
		ownerEmailTextFieldListener.setPreference(docSettings.ownerEmail);
		ownerEmailField.setText(docSettings.ownerEmail.tmp);
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
		if (!docSettings.lineEnd.cur.equals(docSettings.lineEnd.tmp) 
			|| !docSettings.saveEncoding.cur.equals(docSettings.saveEncoding.tmp) 
			|| !docSettings.saveFormat.cur.equals(docSettings.saveFormat.tmp)
		) {
			docSettings.doc.setFileModified(true);
		}
		
		docSettings.lineEnd.cur = docSettings.lineEnd.tmp;
		docSettings.saveEncoding.cur = docSettings.saveEncoding.tmp;
		docSettings.saveFormat.cur = docSettings.saveFormat.tmp;
		docSettings.ownerName.cur = docSettings.ownerName.tmp;
		docSettings.ownerEmail.cur = docSettings.ownerEmail.tmp;

		cancel();
	}

	private void cancel() {
		hide();
	}

	private void restoreToGlobal() {
		docSettings.lineEnd.tmp = Preferences.getPreferenceLineEnding(Preferences.LINE_END).cur;
		docSettings.saveEncoding.tmp = Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur;
		docSettings.saveFormat.tmp = Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur;
		docSettings.ownerName.tmp = Preferences.getPreferenceString(Preferences.OWNER_NAME).cur;
		docSettings.ownerEmail.tmp = Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur;
				
		lineEndComboBox.setSelectedItem(docSettings.lineEnd.tmp);
		saveEncodingComboBox.setSelectedItem(docSettings.saveEncoding.tmp);
		saveFormatComboBox.setSelectedItem(docSettings.saveFormat.tmp);
		ownerNameField.setText(docSettings.ownerName.tmp);
		ownerEmailField.setText(docSettings.ownerEmail.tmp);
	}
}
