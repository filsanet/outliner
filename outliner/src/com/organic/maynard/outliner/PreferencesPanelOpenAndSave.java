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

public class PreferencesPanelOpenAndSave extends AbstractPreferencesPanel implements PreferencesPanel, ActionListener, GUITreeComponent {
	
	// Constants
	public static final String DEFAULT_LINE_TERMINATOR = "Default Line Terminator";
	public static final String DEFAULT_ENCODING_WHEN_OPENING = "Default Encoding when opening.";
	public static final String DEFAULT_ENCODING_WHEN_SAVING = "Default Encoding when saving.";
	public static final String DEFAULT_FORMAT_WHEN_OPENING = "Default Format when opening.";
	public static final String DEFAULT_FORMAT_WHEN_SAVING = "Default Format when saving.";

	// Define Fields and Buttons
	private final JComboBox LINE_END_COMBOBOX = new JComboBox(Preferences.PLATFORM_IDENTIFIERS);
	private final JComboBox OPEN_ENCODING_COMBOBOX = new JComboBox();
	private final JComboBox SAVE_ENCODING_COMBOBOX = new JComboBox();

	private final JComboBox OPEN_FORMAT_COMBOBOX = new JComboBox();
	private final JComboBox SAVE_FORMAT_COMBOBOX = new JComboBox();
	
	private final JButton RESTORE_DEFAULT_OPEN_AND_SAVE_BUTTON = new JButton(PreferencesFrame.RESTORE_DEFAULTS);


	// The Constructor
	public PreferencesPanelOpenAndSave() {}


	// GUITreeComponent interface
	public void endSetup(AttributeList atts) {
		
		RESTORE_DEFAULT_OPEN_AND_SAVE_BUTTON.addActionListener(this);		
		LINE_END_COMBOBOX.addItemListener(new ComboBoxListener(LINE_END_COMBOBOX, Preferences.getPreferenceLineEnding(Preferences.LINE_END)));
		
		for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
			String encoding = (String) Preferences.ENCODINGS.elementAt(i);
			OPEN_ENCODING_COMBOBOX.addItem(encoding);
			SAVE_ENCODING_COMBOBOX.addItem(encoding);
		}
		
		OPEN_ENCODING_COMBOBOX.addItemListener(new ComboBoxListener(OPEN_ENCODING_COMBOBOX, Preferences.getPreferenceString(Preferences.OPEN_ENCODING)));
		SAVE_ENCODING_COMBOBOX.addItemListener(new ComboBoxListener(SAVE_ENCODING_COMBOBOX, Preferences.getPreferenceString(Preferences.SAVE_ENCODING)));

		for (int i = 0; i < Preferences.FILE_FORMATS_OPEN.size(); i++) {
			OPEN_FORMAT_COMBOBOX.addItem((String) Preferences.FILE_FORMATS_OPEN.elementAt(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_SAVE.size(); i++) {
			SAVE_FORMAT_COMBOBOX.addItem((String) Preferences.FILE_FORMATS_SAVE.elementAt(i));
		}

		OPEN_FORMAT_COMBOBOX.addItemListener(new ComboBoxListener(OPEN_FORMAT_COMBOBOX, Preferences.getPreferenceString(Preferences.OPEN_FORMAT)));
		SAVE_FORMAT_COMBOBOX.addItemListener(new ComboBoxListener(SAVE_FORMAT_COMBOBOX, Preferences.getPreferenceString(Preferences.SAVE_FORMAT)));

		Box openAndSaveBox = Box.createVerticalBox();

		JLabel openAndSaveLabel = new JLabel(atts.getValue(AbstractPreferencesPanel.A_TITLE));
		AbstractPreferencesPanel.addSingleItemCentered(openAndSaveLabel, openAndSaveBox);
		
		openAndSaveBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(DEFAULT_LINE_TERMINATOR), openAndSaveBox);
		AbstractPreferencesPanel.addSingleItemCentered(LINE_END_COMBOBOX, openAndSaveBox);

		openAndSaveBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(DEFAULT_ENCODING_WHEN_OPENING), openAndSaveBox);
		AbstractPreferencesPanel.addSingleItemCentered(OPEN_ENCODING_COMBOBOX, openAndSaveBox);

		openAndSaveBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(DEFAULT_ENCODING_WHEN_SAVING), openAndSaveBox);
		AbstractPreferencesPanel.addSingleItemCentered(SAVE_ENCODING_COMBOBOX, openAndSaveBox);

		openAndSaveBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(DEFAULT_FORMAT_WHEN_OPENING), openAndSaveBox);
		AbstractPreferencesPanel.addSingleItemCentered(OPEN_FORMAT_COMBOBOX, openAndSaveBox);

		openAndSaveBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(DEFAULT_FORMAT_WHEN_SAVING), openAndSaveBox);
		AbstractPreferencesPanel.addSingleItemCentered(SAVE_FORMAT_COMBOBOX, openAndSaveBox);

		openAndSaveBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(RESTORE_DEFAULT_OPEN_AND_SAVE_BUTTON, openAndSaveBox);
		
		add(openAndSaveBox);

		super.endSetup(atts);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(PreferencesFrame.RESTORE_DEFAULTS)) {
			try {
				LINE_END_COMBOBOX.setSelectedItem(Preferences.getPreferenceLineEnding(Preferences.LINE_END).def);
				Preferences.getPreferenceLineEnding(Preferences.LINE_END).restoreTemporaryToDefault();

				OPEN_ENCODING_COMBOBOX.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_ENCODING).def);
				Preferences.getPreferenceString(Preferences.OPEN_ENCODING).restoreTemporaryToDefault();

				SAVE_ENCODING_COMBOBOX.setSelectedItem(Preferences.getPreferenceString(Preferences.SAVE_ENCODING).def);
				Preferences.getPreferenceString(Preferences.SAVE_ENCODING).restoreTemporaryToDefault();

				OPEN_FORMAT_COMBOBOX.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_FORMAT).def);
				Preferences.getPreferenceString(Preferences.OPEN_FORMAT).restoreTemporaryToDefault();

				SAVE_FORMAT_COMBOBOX.setSelectedItem(Preferences.getPreferenceString(Preferences.SAVE_FORMAT).def);
				Preferences.getPreferenceString(Preferences.SAVE_FORMAT).restoreTemporaryToDefault();
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		}
	}
	
	public void setToCurrent() {
		LINE_END_COMBOBOX.setSelectedItem(Preferences.getPreferenceLineEnding(Preferences.LINE_END).cur);
		OPEN_ENCODING_COMBOBOX.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_ENCODING).cur);
		SAVE_ENCODING_COMBOBOX.setSelectedItem(Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur);
		OPEN_FORMAT_COMBOBOX.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_FORMAT).cur);
		SAVE_FORMAT_COMBOBOX.setSelectedItem(Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur);
	}

	public void applyTemporaryToCurrent() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		PreferenceLineEnding pLineEnd = (PreferenceLineEnding) prefs.getPreference(Preferences.LINE_END);
		PreferenceString pSaveEncoding = (PreferenceString) prefs.getPreference(Preferences.SAVE_ENCODING);
		PreferenceString pSaveFormat = (PreferenceString) prefs.getPreference(Preferences.SAVE_FORMAT);

		// Update Document Settings
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			OutlinerDocument doc = Outliner.getDocument(i);
			
			// Only update files that do not have overriding document settings.
			if (!doc.settings.useDocumentSettings) {
				doc.settings.lineEnd.def = pLineEnd.tmp;
				doc.settings.lineEnd.cur = pLineEnd.tmp;
				doc.settings.lineEnd.tmp = pLineEnd.tmp;
				doc.settings.saveEncoding.def = pSaveEncoding.tmp;
				doc.settings.saveEncoding.cur = pSaveEncoding.tmp;
				doc.settings.saveEncoding.tmp = pSaveEncoding.tmp;
				doc.settings.saveFormat.def = pSaveFormat.tmp;
				doc.settings.saveFormat.cur = pSaveFormat.tmp;
				doc.settings.saveFormat.tmp = pSaveFormat.tmp;
				//doc.setFileModified(true);
			}
		}
	}
}