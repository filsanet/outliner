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

public class PreferencesPanelEditor extends AbstractPreferencesPanel implements ActionListener, GUITreeComponent {
	
	// Constants
	public static final String SHOW_LINE_NUMBERS = "Show Line Numbers";
	public static final String UNDO_QUEUE_SIZE = "Undo Queue Size";
	public static final String FONT_SIZE = "Font Size";
	public static final String LINE_WRAP = "Line Wrap";

	// Define Fields and Buttons
	private final JTextField UNDO_QUEUE_SIZE_FIELD = new JTextField(4);
	private final JTextField FONT_SIZE_FIELD = new JTextField(4);
	private final GraphicsEnvironment GRAPHICS_ENVIRONEMNT = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private final JComboBox FONT_FACE_COMBOBOX = new JComboBox(GRAPHICS_ENVIRONEMNT.getAvailableFontFamilyNames());
	private final String[] LINE_WRAP_OPTIONS = {Preferences.TXT_WORDS, Preferences.TXT_CHARACTERS};
	private final JComboBox LINE_WRAP_COMBOBOX = new JComboBox(LINE_WRAP_OPTIONS);
	private final JCheckBox SHOW_LINE_NUMBERS_CHECKBOX = new JCheckBox();
	private final JButton RESTORE_DEFAULT_EDITOR_BUTTON = new JButton(PreferencesFrame.RESTORE_DEFAULTS);


	// The Constructor
	public PreferencesPanelEditor() {}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		
		RESTORE_DEFAULT_EDITOR_BUTTON.addActionListener(this);		
		UNDO_QUEUE_SIZE_FIELD.addFocusListener(new TextFieldListener(UNDO_QUEUE_SIZE_FIELD, Preferences.UNDO_QUEUE_SIZE));
		FONT_SIZE_FIELD.addFocusListener(new TextFieldListener(FONT_SIZE_FIELD, Preferences.FONT_SIZE));
		FONT_FACE_COMBOBOX.addItemListener(new ComboBoxListener(FONT_FACE_COMBOBOX, Preferences.FONT_FACE));
		LINE_WRAP_COMBOBOX.addItemListener(new ComboBoxListener(LINE_WRAP_COMBOBOX, Preferences.LINE_WRAP));
		SHOW_LINE_NUMBERS_CHECKBOX.addActionListener(new CheckboxListener(SHOW_LINE_NUMBERS_CHECKBOX, Preferences.SHOW_LINE_NUMBERS));

		setToCurrent();

		Box editorBox = Box.createVerticalBox();

		JLabel editorLabel = new JLabel(atts.getValue(AbstractPreferencesPanel.A_TITLE));
		AbstractPreferencesPanel.addSingleItemCentered(editorLabel, editorBox);
		
		editorBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(FONT_FACE_COMBOBOX, editorBox);
		AbstractPreferencesPanel.addPreferenceItem(FONT_SIZE, FONT_SIZE_FIELD, editorBox);

		editorBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addPreferenceItem(LINE_WRAP, LINE_WRAP_COMBOBOX, editorBox);

		editorBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addPreferenceItem(UNDO_QUEUE_SIZE, UNDO_QUEUE_SIZE_FIELD, editorBox);

		editorBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addPreferenceItem(SHOW_LINE_NUMBERS, SHOW_LINE_NUMBERS_CHECKBOX, editorBox);

		editorBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(RESTORE_DEFAULT_EDITOR_BUTTON, editorBox);
		
		add(editorBox);

		super.startSetup(atts);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(PreferencesFrame.RESTORE_DEFAULTS)) {
			try {
				UNDO_QUEUE_SIZE_FIELD.setText(String.valueOf(Preferences.UNDO_QUEUE_SIZE.def));
				Preferences.UNDO_QUEUE_SIZE.restoreTemporaryToDefault();

				FONT_SIZE_FIELD.setText(String.valueOf(Preferences.FONT_SIZE.def));
				Preferences.FONT_SIZE.restoreTemporaryToDefault();

				FONT_FACE_COMBOBOX.setSelectedItem(Preferences.FONT_FACE.def);
				Preferences.FONT_FACE.restoreTemporaryToDefault();

				LINE_WRAP_COMBOBOX.setSelectedItem(Preferences.LINE_WRAP.def);
				Preferences.LINE_WRAP.restoreTemporaryToDefault();

				SHOW_LINE_NUMBERS_CHECKBOX.setSelected(Preferences.SHOW_LINE_NUMBERS.def);
				Preferences.SHOW_LINE_NUMBERS.restoreTemporaryToDefault();
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		}
	}
	
	public void setToCurrent() {
		UNDO_QUEUE_SIZE_FIELD.setText(String.valueOf(Preferences.UNDO_QUEUE_SIZE.cur));
		FONT_SIZE_FIELD.setText(String.valueOf(Preferences.FONT_SIZE.cur));
		FONT_FACE_COMBOBOX.setSelectedItem(Preferences.FONT_FACE.cur);
		LINE_WRAP_COMBOBOX.setSelectedItem(Preferences.LINE_WRAP.cur);
		SHOW_LINE_NUMBERS_CHECKBOX.setSelected(Preferences.SHOW_LINE_NUMBERS.cur);
	}
}