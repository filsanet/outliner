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
import javax.swing.event.*;

import org.xml.sax.*;

public class PreferencesPanelMisc extends AbstractPreferencesPanel implements PreferencesPanel, ActionListener, GUITreeComponent {
	
	// Constants
	public static final String RECENT_FILES_LIST_SIZE = "Recent Files List Size";
	public static final String PRINT_ENVIRONMENT = "Print Environemnt";
	public static final String NEW_DOCUMENT_ON_STARTUP = "New Document On Startup";
	public static final String OWNER_NAME = "Owner Name";
	public static final String OWNER_EMAIL = "Owner Email";
	public static final String OPEN_SAVE_REMOTELY = "Open/Save Remotely";
	public static final String REMOTE_URL = "Remote URL";
	public static final String REMOTE_USERNAME = "Remote Username";
	public static final String REMOTE_PASSWORD = "Remote Password";

	// Define Fields and Buttons
	private final JTextField RECENT_FILES_LIST_SIZE_FIELD = new JTextField(2);
	private final JCheckBox PRINT_ENVIRONMENT_CHECKBOX = new JCheckBox();
	private final JCheckBox NEW_DOC_ON_STARTUP_CHECKBOX = new JCheckBox();
	private final JTextField OWNER_NAME_FIELD = new JTextField(10);
	private final JTextField OWNER_EMAIL_FIELD = new JTextField(10);
	private final JButton RESTORE_DEFAULT_MISC_BUTTON = new JButton(PreferencesFrame.RESTORE_DEFAULTS);

		// WebFile
		private final JCheckBox WEB_FILE_SYSTEM_CHECKBOX = new JCheckBox();
		private final JTextField WEB_FILE_URL_FIELD = new JTextField(10);
		private final JTextField WEB_FILE_USER_FIELD = new JTextField(10);
		private final JTextField WEB_FILE_PASSWORD_FIELD = new JTextField(10);


	// The Constructor
	public PreferencesPanelMisc() {}


	// GUITreeComponent interface
	public void endSetup(AttributeList atts) {
		
		RESTORE_DEFAULT_MISC_BUTTON.addActionListener(this);		
		RECENT_FILES_LIST_SIZE_FIELD.addFocusListener(new TextFieldListener(RECENT_FILES_LIST_SIZE_FIELD, Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE)));
		PRINT_ENVIRONMENT_CHECKBOX.addActionListener(new CheckboxListener(PRINT_ENVIRONMENT_CHECKBOX, Preferences.getPreferenceBoolean(Preferences.PRINT_ENVIRONMENT)));
		NEW_DOC_ON_STARTUP_CHECKBOX.addActionListener(new CheckboxListener(NEW_DOC_ON_STARTUP_CHECKBOX, Preferences.getPreferenceBoolean(Preferences.NEW_DOC_ON_STARTUP)));
		OWNER_NAME_FIELD.addFocusListener(new TextFieldListener(OWNER_NAME_FIELD, Preferences.getPreferenceString(Preferences.OWNER_NAME)));
		OWNER_EMAIL_FIELD.addFocusListener(new TextFieldListener(OWNER_EMAIL_FIELD, Preferences.getPreferenceString(Preferences.OWNER_EMAIL)));

			// WebFile
			WEB_FILE_SYSTEM_CHECKBOX.addActionListener(new CheckboxListener(WEB_FILE_SYSTEM_CHECKBOX, Preferences.getPreferenceBoolean(Preferences.WEB_FILE_SYSTEM)));
			WEB_FILE_URL_FIELD.addFocusListener(new TextFieldListener(WEB_FILE_URL_FIELD, Preferences.getPreferenceString(Preferences.WEB_FILE_URL)));
			WEB_FILE_USER_FIELD.addFocusListener(new TextFieldListener(WEB_FILE_USER_FIELD, Preferences.getPreferenceString(Preferences.WEB_FILE_USER)));
			WEB_FILE_PASSWORD_FIELD.addFocusListener(new TextFieldListener(WEB_FILE_PASSWORD_FIELD, Preferences.getPreferenceString(Preferences.WEB_FILE_PASSWORD)));

		Box miscBox = Box.createVerticalBox();

		JLabel miscLabel = new JLabel(atts.getValue(AbstractPreferencesPanel.A_TITLE));
		AbstractPreferencesPanel.addSingleItemCentered(miscLabel, miscBox);
		
		miscBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addPreferenceItem(RECENT_FILES_LIST_SIZE, RECENT_FILES_LIST_SIZE_FIELD, miscBox);
		AbstractPreferencesPanel.addPreferenceItem(PRINT_ENVIRONMENT, PRINT_ENVIRONMENT_CHECKBOX, miscBox);
		AbstractPreferencesPanel.addPreferenceItem(NEW_DOCUMENT_ON_STARTUP, NEW_DOC_ON_STARTUP_CHECKBOX, miscBox);
		AbstractPreferencesPanel.addPreferenceItem(OWNER_NAME, OWNER_NAME_FIELD, miscBox);
		AbstractPreferencesPanel.addPreferenceItem(OWNER_EMAIL, OWNER_EMAIL_FIELD, miscBox);

		// WebFile
		miscBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addPreferenceItem(OPEN_SAVE_REMOTELY, WEB_FILE_SYSTEM_CHECKBOX, miscBox);
		AbstractPreferencesPanel.addPreferenceItem(REMOTE_URL, WEB_FILE_URL_FIELD, miscBox);
		AbstractPreferencesPanel.addPreferenceItem(REMOTE_USERNAME, WEB_FILE_USER_FIELD, miscBox);
		AbstractPreferencesPanel.addPreferenceItem(REMOTE_PASSWORD, WEB_FILE_PASSWORD_FIELD, miscBox);

		miscBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(RESTORE_DEFAULT_MISC_BUTTON, miscBox);
		
		add(miscBox);

		super.endSetup(atts);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(PreferencesFrame.RESTORE_DEFAULTS)) {
			try {
				RECENT_FILES_LIST_SIZE_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).def));
				Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).restoreTemporaryToDefault();
				
				PRINT_ENVIRONMENT_CHECKBOX.setSelected(Preferences.getPreferenceBoolean(Preferences.PRINT_ENVIRONMENT).def);
				Preferences.getPreferenceBoolean(Preferences.PRINT_ENVIRONMENT).restoreTemporaryToDefault();

				NEW_DOC_ON_STARTUP_CHECKBOX.setSelected(Preferences.getPreferenceBoolean(Preferences.NEW_DOC_ON_STARTUP).def);
				Preferences.getPreferenceBoolean(Preferences.NEW_DOC_ON_STARTUP).restoreTemporaryToDefault();

				OWNER_NAME_FIELD.setText(String.valueOf(Preferences.getPreferenceString(Preferences.OWNER_NAME).def));
				Preferences.getPreferenceString(Preferences.OWNER_NAME).restoreTemporaryToDefault();

				OWNER_EMAIL_FIELD.setText(String.valueOf(Preferences.getPreferenceString(Preferences.OWNER_EMAIL).def));
				Preferences.getPreferenceString(Preferences.OWNER_EMAIL).restoreTemporaryToDefault();

				// WebFile
				WEB_FILE_SYSTEM_CHECKBOX.setSelected(Preferences.getPreferenceBoolean(Preferences.WEB_FILE_SYSTEM).def);
				Preferences.getPreferenceBoolean(Preferences.WEB_FILE_SYSTEM).restoreTemporaryToDefault();

				WEB_FILE_URL_FIELD.setText(String.valueOf(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).def));
				Preferences.getPreferenceString(Preferences.WEB_FILE_URL).restoreTemporaryToDefault();

				WEB_FILE_USER_FIELD.setText(String.valueOf(Preferences.getPreferenceString(Preferences.WEB_FILE_USER).def));
				Preferences.getPreferenceString(Preferences.WEB_FILE_USER).restoreTemporaryToDefault();

				WEB_FILE_PASSWORD_FIELD.setText(String.valueOf(Preferences.getPreferenceString(Preferences.WEB_FILE_PASSWORD).def));
				Preferences.getPreferenceString(Preferences.WEB_FILE_PASSWORD).restoreTemporaryToDefault();
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		}
	}
	
	public void setToCurrent() {
		RECENT_FILES_LIST_SIZE_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.RECENT_FILES_LIST_SIZE).cur));
		PRINT_ENVIRONMENT_CHECKBOX.setSelected(Preferences.getPreferenceBoolean(Preferences.PRINT_ENVIRONMENT).cur);
		NEW_DOC_ON_STARTUP_CHECKBOX.setSelected(Preferences.getPreferenceBoolean(Preferences.NEW_DOC_ON_STARTUP).cur);
		OWNER_NAME_FIELD.setText(String.valueOf(Preferences.getPreferenceString(Preferences.OWNER_NAME).cur));
		OWNER_EMAIL_FIELD.setText(String.valueOf(Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur));
		
		// WebFile
		WEB_FILE_SYSTEM_CHECKBOX.setSelected(Preferences.getPreferenceBoolean(Preferences.WEB_FILE_SYSTEM).cur);
		WEB_FILE_URL_FIELD.setText(String.valueOf(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur));
		WEB_FILE_USER_FIELD.setText(String.valueOf(Preferences.getPreferenceString(Preferences.WEB_FILE_USER).cur));
		WEB_FILE_PASSWORD_FIELD.setText(String.valueOf(Preferences.getPreferenceString(Preferences.WEB_FILE_PASSWORD).cur));
	}
	
	public void applyTemporaryToCurrent() {		
		// Update the recent file list.
		RecentFilesList.trim();	
	}
}