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
	private static String USE_CREATE_MOD_DATES = null;
	private static String CREATE_MOD_DATES_FORMAT = null;

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
	protected JCheckBox useCreateModDatesCheckBox = null;
	protected JTextField createModDatesFormatField = null;

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
	protected CheckboxListener useCreateModDatesCheckBoxListener = null;
	protected TextFieldListener createModDatesFormatTextFieldListener = null;

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
		USE_CREATE_MOD_DATES = GUITreeLoader.reg.getText("use_create_mod_dates"); // ???
		CREATE_MOD_DATES_FORMAT = GUITreeLoader.reg.getText("create_mod_dates_format"); // ???

		IS_USING_APPLICATION_PREFS = GUITreeLoader.reg.getText("is_using_application_prefs");
		IS_USING_DOCUMENT_PREFS = GUITreeLoader.reg.getText("is_using_document_prefs");

		buttonOK = new JButton(OK);
		buttonCancel = new JButton(CANCEL);
		buttonRestoreToGlobal = new JButton(RESTORE_TO_GLOBAL);

		lineEndComboBox = new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
		saveEncodingComboBox = new JComboBox();
		saveFormatComboBox = new JComboBox();
		ownerNameField = new JTextField(10);
		ownerEmailField = new JTextField(10);
		applyFontStyleForCommentsCheckBox = new JCheckBox();
		applyFontStyleForEditabilityCheckBox = new JCheckBox();
		applyFontStyleForMoveabilityCheckBox = new JCheckBox();
		useCreateModDatesCheckBox = new JCheckBox();
		createModDatesFormatField = new JTextField(10);

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
		useCreateModDatesCheckBoxListener = new CheckboxListener(useCreateModDatesCheckBox, null);
		createModDatesFormatTextFieldListener = new TextFieldListener(createModDatesFormatField, null);
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
		useCreateModDatesCheckBox.addActionListener(useCreateModDatesCheckBoxListener);
		createModDatesFormatField.addFocusListener(createModDatesFormatTextFieldListener);

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

		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(USE_CREATE_MOD_DATES), box);
		AbstractPreferencesPanel.addSingleItemCentered(useCreateModDatesCheckBox, box);

		box.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(CREATE_MOD_DATES_FORMAT), box);
		AbstractPreferencesPanel.addSingleItemCentered(createModDatesFormatField, box);

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

		if (docSettings.useDocumentSettings()) {
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
		docSettings.setUseDocumentSettings(true);
		applyChanges();
		hide();
	}

	private void cancel() {
		hide();
	}

	private void restoreToGlobal() {
		// We should no longer use document settings because the user explicitly said Restore to Global to them.
		docSettings.setUseDocumentSettings(false);
		syncToGlobal();
		applyChanges();
		hide();
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
			|| !docSettings.getUseCreateModDates().cur == docSettings.getUseCreateModDates().tmp
			|| !docSettings.getCreateModDatesFormat().cur.equals(docSettings.getCreateModDatesFormat().tmp)
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
		docSettings.getUseCreateModDates().cur = docSettings.getUseCreateModDates().tmp;
		docSettings.getCreateModDatesFormat().cur = docSettings.getCreateModDatesFormat().tmp;
		
		docSettings.updateSimpleDateFormat(docSettings.getCreateModDatesFormat().cur);
		//System.out.println("UPDATED: " + docSettings.useCreateModDates.cur);
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

		docSettings.getUseCreateModDates().restoreTemporaryToCurrent();
		useCreateModDatesCheckBoxListener.setPreference(docSettings.getUseCreateModDates());
		useCreateModDatesCheckBox.setSelected(docSettings.getUseCreateModDates().tmp);

		docSettings.getCreateModDatesFormat().restoreTemporaryToCurrent();
		createModDatesFormatTextFieldListener.setPreference(docSettings.getCreateModDatesFormat());
		createModDatesFormatField.setText(docSettings.getCreateModDatesFormat().tmp);

		creationDateLabel.setText(docSettings.dateCreated);
		modificationDateLabel.setText(docSettings.dateModified);
		
		//System.out.println("SYNC TO DOC: " + docSettings.useCreateModDates.tmp);
	}

	private void syncToGlobal() {
		docSettings.lineEnd.tmp = Preferences.getPreferenceLineEnding(Preferences.SAVE_LINE_END).cur;
		docSettings.saveEncoding.tmp = Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur;
		docSettings.saveFormat.tmp = Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur;
		docSettings.ownerName.tmp = Preferences.getPreferenceString(Preferences.OWNER_NAME).cur;
		docSettings.ownerEmail.tmp = Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur;
		docSettings.applyFontStyleForComments.tmp = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_COMMENTS).cur;
		docSettings.applyFontStyleForEditability.tmp = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_EDITABILITY).cur;
		docSettings.applyFontStyleForMoveability.tmp = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_MOVEABILITY).cur;
		docSettings.getUseCreateModDates().tmp = Preferences.getPreferenceBoolean(Preferences.USE_CREATE_MOD_DATES).cur;
		docSettings.getCreateModDatesFormat().tmp = Preferences.getPreferenceString(Preferences.CREATE_MOD_DATES_FORMAT).cur;

		lineEndComboBox.setSelectedItem(docSettings.lineEnd.tmp);
		lineEndComboBoxListener.setPreference(docSettings.lineEnd);
		
		saveEncodingComboBox.setSelectedItem(docSettings.saveEncoding.tmp);
		saveEncodingComboBoxListener.setPreference(docSettings.saveEncoding);
		
		saveFormatComboBox.setSelectedItem(docSettings.saveFormat.tmp);
		saveFormatComboBoxListener.setPreference(docSettings.saveFormat);
		
		ownerNameField.setText(docSettings.ownerName.tmp);
		ownerNameTextFieldListener.setPreference(docSettings.ownerName);
		
		ownerEmailField.setText(docSettings.ownerEmail.tmp);
		ownerEmailTextFieldListener.setPreference(docSettings.ownerEmail);
		
		applyFontStyleForCommentsCheckBox.setSelected(docSettings.applyFontStyleForComments.tmp);
		applyFontStyleForCommentsCheckBoxListener.setPreference(docSettings.applyFontStyleForComments);
		
		applyFontStyleForEditabilityCheckBox.setSelected(docSettings.applyFontStyleForEditability.tmp);
		applyFontStyleForEditabilityCheckBoxListener.setPreference(docSettings.applyFontStyleForEditability);
		
		applyFontStyleForMoveabilityCheckBox.setSelected(docSettings.applyFontStyleForMoveability.tmp);
		applyFontStyleForMoveabilityCheckBoxListener.setPreference(docSettings.applyFontStyleForMoveability);
		
		useCreateModDatesCheckBox.setSelected(docSettings.getUseCreateModDates().tmp);
		useCreateModDatesCheckBoxListener.setPreference(docSettings.getUseCreateModDates());
		
		createModDatesFormatField.setText(docSettings.getCreateModDatesFormat().tmp);
		createModDatesFormatTextFieldListener.setPreference(docSettings.getCreateModDatesFormat());
		
		// We always do these, even though they are document level, because they have no global equivilents.
		creationDateLabel.setText(docSettings.dateCreated);
		modificationDateLabel.setText(docSettings.dateModified);
		
		//System.out.println("SYNC TO GLOBAL: " + docSettings.useCreateModDates.tmp);
	}
}

