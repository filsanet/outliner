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

public class DocumentSettingsView extends AbstractGUITreeJDialog implements ActionListener {
	
	// Constants
	private static final int INITIAL_WIDTH = 350;
	private static final int INITIAL_HEIGHT = 400;
	private static final int MINIMUM_WIDTH = 250;
	private static final int MINIMUM_HEIGHT = 300;

	protected static String OK = null;
	protected static String CANCEL = null;
	protected static String RESTORE_TO_GLOBAL = null;
	
	private static String LINE_TERMINATOR = null;
	private static String ENCODING_WHEN_SAVING = null;
	private static String FORMAT_WHEN_SAVING = null;
	private static String OWNER_NAME = null;
	private static String OWNER_EMAIL = null;
	private static String APPLY_FONT_STYLE_FOR_COMMENTS = null;
	private static String APPLY_FONT_STYLE_FOR_EDITABILITY = null;
	private static String APPLY_FONT_STYLE_FOR_MOVEABILITY = null;
	private static String CREATION_DATE = null;
	private static String MODIFICATION_DATE = null;
	
	private static String IS_USING_APPLICATION_PREFS = null;
	private static String IS_USING_DOCUMENT_PREFS = null;


	// GUI Elements
	protected Box box = Box.createVerticalBox();

	protected JButton buttonOK = null;
	protected JButton buttonCancel = null;
	protected JButton buttonRestoreToGlobal = null;
	
	protected JComboBox lineEndComboBox = null;
	protected JComboBox saveEncodingComboBox = null;
	protected JComboBox saveFormatComboBox = null;
	protected JTextField ownerNameField = null;
	protected JTextField ownerEmailField = null;
	protected JCheckBox applyFontStyleForCommentsCheckBox = null;
	protected JCheckBox applyFontStyleForEditabilityCheckBox = null;
	protected JCheckBox applyFontStyleForMoveabilityCheckBox = null;
	
	protected JLabel creationDateLabel = null;
	protected JLabel modificationDateLabel = null;
	protected JLabel isInheritingPrefsLabel = null;
	
	protected ComboBoxListener lineEndComboBoxListener = null;
	protected ComboBoxListener saveEncodingComboBoxListener = null;
	protected ComboBoxListener saveFormatComboBoxListener = null;
	protected TextFieldListener ownerNameTextFieldListener = null;
	protected TextFieldListener ownerEmailTextFieldListener = null;
	protected CheckboxListener applyFontStyleForCommentsCheckBoxListener = null;
	protected CheckboxListener applyFontStyleForEditabilityCheckBoxListener = null;
	protected CheckboxListener applyFontStyleForMoveabilityCheckBoxListener = null;


	// The Constructors
	public DocumentSettingsView() {
		super(false, false, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);

		OK = GUITreeLoader.reg.getText("ok");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		RESTORE_TO_GLOBAL = GUITreeLoader.reg.getText("restore_to_application_preferences");
		
		LINE_TERMINATOR = GUITreeLoader.reg.getText("line_terminator");
		ENCODING_WHEN_SAVING = GUITreeLoader.reg.getText("encoding_when_saving");
		FORMAT_WHEN_SAVING = GUITreeLoader.reg.getText("format_when_saving");
		OWNER_NAME = GUITreeLoader.reg.getText("owner_name");
		OWNER_EMAIL = GUITreeLoader.reg.getText("owner_email");
		APPLY_FONT_STYLE_FOR_COMMENTS = GUITreeLoader.reg.getText("apply_font_style_for_comments");
		APPLY_FONT_STYLE_FOR_EDITABILITY = GUITreeLoader.reg.getText("apply_font_style_for_editability");
		APPLY_FONT_STYLE_FOR_MOVEABILITY = GUITreeLoader.reg.getText("apply_font_style_for_moveability");
		CREATION_DATE = GUITreeLoader.reg.getText("creation_date");
		MODIFICATION_DATE = GUITreeLoader.reg.getText("modification_date");
		
		IS_USING_APPLICATION_PREFS = GUITreeLoader.reg.getText("is_using_application_prefs");
		IS_USING_DOCUMENT_PREFS = GUITreeLoader.reg.getText("is_using_document_prefs");

		buttonOK = new JButton(OK);
		buttonCancel = new JButton(CANCEL);
		buttonRestoreToGlobal = new JButton(RESTORE_TO_GLOBAL);
		
		lineEndComboBox = new JComboBox(Preferences.PLATFORM_IDENTIFIERS);
		saveEncodingComboBox = new JComboBox();
		saveFormatComboBox = new JComboBox();
		ownerNameField = new JTextField(10);
		ownerEmailField = new JTextField(10);
		applyFontStyleForCommentsCheckBox = new JCheckBox();
		applyFontStyleForEditabilityCheckBox = new JCheckBox();
		applyFontStyleForMoveabilityCheckBox = new JCheckBox();
		
		creationDateLabel = new JLabel(" ");
		modificationDateLabel = new JLabel(" ");
		isInheritingPrefsLabel = new JLabel(" ");
		
		lineEndComboBoxListener = new ComboBoxListener(lineEndComboBox, null);
		saveEncodingComboBoxListener = new ComboBoxListener(saveEncodingComboBox, null);
		saveFormatComboBoxListener = new ComboBoxListener(saveFormatComboBox, null);
		ownerNameTextFieldListener = new TextFieldListener(ownerNameField, null);
		ownerEmailTextFieldListener = new TextFieldListener(ownerEmailField, null);
		applyFontStyleForCommentsCheckBoxListener = new CheckboxListener(applyFontStyleForCommentsCheckBox, null);
		applyFontStyleForEditabilityCheckBoxListener = new CheckboxListener(applyFontStyleForEditabilityCheckBox, null);
		applyFontStyleForMoveabilityCheckBoxListener = new CheckboxListener(applyFontStyleForMoveabilityCheckBox, null);
	}

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
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
		buttonRestoreToGlobal.setToolTipText(GUITreeLoader.reg.getText("tooltip_restore_to_global"));

		lineEndComboBox.addItemListener(lineEndComboBoxListener);
		saveEncodingComboBox.addItemListener(saveEncodingComboBoxListener);		
		saveFormatComboBox.addItemListener(saveFormatComboBoxListener);		
		ownerNameField.addFocusListener(ownerNameTextFieldListener);
		ownerEmailField.addFocusListener(ownerEmailTextFieldListener);
		applyFontStyleForCommentsCheckBox.addActionListener(applyFontStyleForCommentsCheckBoxListener);
		applyFontStyleForEditabilityCheckBox.addActionListener(applyFontStyleForEditabilityCheckBoxListener);
		applyFontStyleForMoveabilityCheckBox.addActionListener(applyFontStyleForMoveabilityCheckBoxListener);
			
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

		box.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(OWNER_NAME), box);
		AbstractPreferencesPanel.addSingleItemCentered(ownerNameField, box);

		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(OWNER_EMAIL), box);
		AbstractPreferencesPanel.addSingleItemCentered(ownerEmailField, box);
		
		box.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(APPLY_FONT_STYLE_FOR_COMMENTS), box);
		AbstractPreferencesPanel.addSingleItemCentered(applyFontStyleForCommentsCheckBox, box);
		
		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(APPLY_FONT_STYLE_FOR_EDITABILITY), box);
		AbstractPreferencesPanel.addSingleItemCentered(applyFontStyleForEditabilityCheckBox, box);
		
		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(APPLY_FONT_STYLE_FOR_MOVEABILITY), box);
		AbstractPreferencesPanel.addSingleItemCentered(applyFontStyleForMoveabilityCheckBox, box);
		
		// Define the Outter Box
		JScrollPane jsp = new JScrollPane(box);
		Box outterBox = Box.createVerticalBox();

		AbstractPreferencesPanel.addSingleItemCentered(isInheritingPrefsLabel, outterBox);

		outterBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addPreferenceItem(CREATION_DATE, creationDateLabel, outterBox);
		
		outterBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addPreferenceItem(MODIFICATION_DATE, modificationDateLabel, outterBox);

		outterBox.add(Box.createVerticalStrut(10));

		outterBox.add(jsp);
		
		outterBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(buttonRestoreToGlobal, outterBox);

		getContentPane().add(outterBox,BorderLayout.CENTER);
	
		// Set the default button
		getRootPane().setDefaultButton(buttonOK);
	}
	

	// Configuration 
	private DocumentSettings docSettings = null;
	
	public void configureAndShow(DocumentSettings docSettings) {
		this.docSettings = docSettings;
		
		if (docSettings.useDocumentSettings) {
			isInheritingPrefsLabel.setText(IS_USING_DOCUMENT_PREFS);
			syncToDocumentSettings();
		} else {
			isInheritingPrefsLabel.setText(IS_USING_APPLICATION_PREFS);
			syncToGlobal();
		}

		super.show();
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
		docSettings.useDocumentSettings = true;
		applyChanges();
		hide();
	}
	
	private void cancel() {
		hide();
	}

	private void restoreToGlobal() {
		// We should no longer use document settings because the user explicitly said Restore to Global to them.
		//docSettings.useDocumentSettings = false;
		syncToGlobal();
		applyChanges();
		//hide();
	}

	private void applyChanges() {
		// If anything has changed then mark the doc as modified.
		if (!docSettings.lineEnd.cur.equals(docSettings.lineEnd.tmp) 
			|| !docSettings.saveEncoding.cur.equals(docSettings.saveEncoding.tmp) 
			|| !docSettings.saveFormat.cur.equals(docSettings.saveFormat.tmp)
			|| !docSettings.ownerName.cur.equals(docSettings.ownerName.tmp)
			|| !docSettings.ownerEmail.cur.equals(docSettings.ownerEmail.tmp)
			|| !docSettings.applyFontStyleForComments.cur == docSettings.applyFontStyleForComments.tmp
			|| !docSettings.applyFontStyleForEditability.cur == docSettings.applyFontStyleForEditability.tmp
			|| !docSettings.applyFontStyleForMoveability.cur == docSettings.applyFontStyleForMoveability.tmp
		) {
			docSettings.doc.setFileModified(true);
		}

		// If anything has changed that would effect the GUI then redraw.
		boolean doRedraw = false;
		if (!docSettings.applyFontStyleForComments.cur == docSettings.applyFontStyleForComments.tmp 
			|| !docSettings.applyFontStyleForEditability.cur == docSettings.applyFontStyleForEditability.tmp 
			|| !docSettings.applyFontStyleForMoveability.cur == docSettings.applyFontStyleForMoveability.tmp
		) {
			doRedraw = true;
		}
		
		// Record all the changes.
		docSettings.lineEnd.cur = docSettings.lineEnd.tmp;
		docSettings.saveEncoding.cur = docSettings.saveEncoding.tmp;
		docSettings.saveFormat.cur = docSettings.saveFormat.tmp;
		docSettings.ownerName.cur = docSettings.ownerName.tmp;
		docSettings.ownerEmail.cur = docSettings.ownerEmail.tmp;
		docSettings.applyFontStyleForComments.cur = docSettings.applyFontStyleForComments.tmp;
		docSettings.applyFontStyleForEditability.cur = docSettings.applyFontStyleForEditability.tmp;
		docSettings.applyFontStyleForMoveability.cur = docSettings.applyFontStyleForMoveability.tmp;

		if (doRedraw) {
			docSettings.doc.panel.layout.redraw();
		}	
	}


	// Syncing Methods
	private void syncToDocumentSettings() {		
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

		docSettings.applyFontStyleForComments.restoreTemporaryToCurrent();
		applyFontStyleForCommentsCheckBoxListener.setPreference(docSettings.applyFontStyleForComments);
		applyFontStyleForCommentsCheckBox.setSelected(docSettings.applyFontStyleForComments.tmp);

		docSettings.applyFontStyleForEditability.restoreTemporaryToCurrent();
		applyFontStyleForEditabilityCheckBoxListener.setPreference(docSettings.applyFontStyleForEditability);
		applyFontStyleForEditabilityCheckBox.setSelected(docSettings.applyFontStyleForEditability.tmp);

		docSettings.applyFontStyleForMoveability.restoreTemporaryToCurrent();
		applyFontStyleForMoveabilityCheckBoxListener.setPreference(docSettings.applyFontStyleForMoveability);
		applyFontStyleForMoveabilityCheckBox.setSelected(docSettings.applyFontStyleForMoveability.tmp);
		
		creationDateLabel.setText(docSettings.dateCreated);
		modificationDateLabel.setText(docSettings.dateModified);
	}
	
	private void syncToGlobal() {
		docSettings.lineEnd.tmp = Preferences.getPreferenceLineEnding(Preferences.LINE_END).cur;
		docSettings.saveEncoding.tmp = Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur;
		docSettings.saveFormat.tmp = Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur;
		docSettings.ownerName.tmp = Preferences.getPreferenceString(Preferences.OWNER_NAME).cur;
		docSettings.ownerEmail.tmp = Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur;
		docSettings.applyFontStyleForComments.tmp = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_COMMENTS).cur;
		docSettings.applyFontStyleForEditability.tmp = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_EDITABILITY).cur;
		docSettings.applyFontStyleForMoveability.tmp = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_MOVEABILITY).cur;
				
		lineEndComboBox.setSelectedItem(docSettings.lineEnd.tmp);
		saveEncodingComboBox.setSelectedItem(docSettings.saveEncoding.tmp);
		saveFormatComboBox.setSelectedItem(docSettings.saveFormat.tmp);
		ownerNameField.setText(docSettings.ownerName.tmp);
		ownerEmailField.setText(docSettings.ownerEmail.tmp);
		applyFontStyleForCommentsCheckBox.setSelected(docSettings.applyFontStyleForComments.tmp);
		applyFontStyleForEditabilityCheckBox.setSelected(docSettings.applyFontStyleForEditability.tmp);
		applyFontStyleForMoveabilityCheckBox.setSelected(docSettings.applyFontStyleForMoveability.tmp);

		// We always do these, even though they are document level, because they have no global equivilents.
		creationDateLabel.setText(docSettings.dateCreated);
		modificationDateLabel.setText(docSettings.dateModified);
	}
}
