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

public class PreferencesPanelLookAndFeel extends AbstractPreferencesPanel implements PreferencesPanel, ActionListener, GUITreeComponent {
	
	// Constants	
	public static final String FOREGROUND_COLOR = "Foreground Color";
	public static final String BACKGROUND_COLOR = "Background Color";
	public static final String DESKTOP_COLOR = "Desktop Color";
	public static final String TEXT_COLOR = "Text/Selection Color";
	public static final String SELECTED_CHILDREN_COLOR = "Selected Children Color";
	public static final String LINE_NUMBER_COLOR = "Line Number Color";
	public static final String LINE_NUMBER_SELECTED_COLOR = "Line Number Selected Color";
	public static final String LINE_NUMBER_SELECTED_CHILD_COLOR = "Line Number Selected Children Color";

	// Define Fields and Buttons
	private final JButton DESKTOP_BACKGROUND_COLOR_BUTTON = new JButton("");
	private final JButton PANEL_BACKGROUND_COLOR_BUTTON = new JButton("");
	private final JButton TEXTAREA_BACKGROUND_COLOR_BUTTON = new JButton("");
	private final JButton TEXTAREA_FOREGROUND_COLOR_BUTTON = new JButton("");
	private final JButton SELECTED_CHILD_COLOR_BUTTON = new JButton("");
	private final JButton LINE_NUMBER_COLOR_BUTTON = new JButton("");
	private final JButton LINE_NUMBER_SELECTED_COLOR_BUTTON = new JButton("");
	private final JButton LINE_NUMBER_SELECTED_CHILD_COLOR_BUTTON = new JButton("");
	private final JTextField INDENT_FIELD = new JTextField(4);
	private final JTextField VERTICAL_SPACING_FIELD = new JTextField(4);
	private final JTextField LEFT_MARGIN_FIELD = new JTextField(4);
	private final JTextField RIGHT_MARGIN_FIELD = new JTextField(4);
	private final JTextField TOP_MARGIN_FIELD = new JTextField(4);
	private final JTextField BOTTOM_MARGIN_FIELD = new JTextField(4);
	private final JButton RESTORE_DEFAULT_LOOK_AND_FEEL_BUTTON = new JButton(PreferencesFrame.RESTORE_DEFAULTS);


	// The Constructor
	public PreferencesPanelLookAndFeel() {}


	// GUITreeComponent interface
	public void endSetup(AttributeList atts) {
		
		DESKTOP_BACKGROUND_COLOR_BUTTON.addActionListener(this);
		DESKTOP_BACKGROUND_COLOR_BUTTON.setActionCommand(DESKTOP_COLOR);
		PANEL_BACKGROUND_COLOR_BUTTON.addActionListener(this);
		PANEL_BACKGROUND_COLOR_BUTTON.setActionCommand(BACKGROUND_COLOR);
		TEXTAREA_BACKGROUND_COLOR_BUTTON.addActionListener(this);
		TEXTAREA_BACKGROUND_COLOR_BUTTON.setActionCommand(FOREGROUND_COLOR);
		TEXTAREA_FOREGROUND_COLOR_BUTTON.addActionListener(this);
		TEXTAREA_FOREGROUND_COLOR_BUTTON.setActionCommand(TEXT_COLOR);
		SELECTED_CHILD_COLOR_BUTTON.addActionListener(this);
		SELECTED_CHILD_COLOR_BUTTON.setActionCommand(SELECTED_CHILDREN_COLOR);

		LINE_NUMBER_COLOR_BUTTON.addActionListener(this);
		LINE_NUMBER_COLOR_BUTTON.setActionCommand(LINE_NUMBER_COLOR);
		LINE_NUMBER_SELECTED_COLOR_BUTTON.addActionListener(this);
		LINE_NUMBER_SELECTED_COLOR_BUTTON.setActionCommand(LINE_NUMBER_SELECTED_COLOR);
		LINE_NUMBER_SELECTED_CHILD_COLOR_BUTTON.addActionListener(this);
		LINE_NUMBER_SELECTED_CHILD_COLOR_BUTTON.setActionCommand(LINE_NUMBER_SELECTED_CHILD_COLOR);
				
		RESTORE_DEFAULT_LOOK_AND_FEEL_BUTTON.addActionListener(this);		
		INDENT_FIELD.addFocusListener(new TextFieldListener(INDENT_FIELD, Preferences.getPreferenceInt(Preferences.INDENT)));
		VERTICAL_SPACING_FIELD.addFocusListener(new TextFieldListener(VERTICAL_SPACING_FIELD, Preferences.getPreferenceInt(Preferences.VERTICAL_SPACING)));
		LEFT_MARGIN_FIELD.addFocusListener(new TextFieldListener(LEFT_MARGIN_FIELD, Preferences.getPreferenceInt(Preferences.LEFT_MARGIN)));
		RIGHT_MARGIN_FIELD.addFocusListener(new TextFieldListener(RIGHT_MARGIN_FIELD, Preferences.getPreferenceInt(Preferences.RIGHT_MARGIN)));
		TOP_MARGIN_FIELD.addFocusListener(new TextFieldListener(TOP_MARGIN_FIELD, Preferences.getPreferenceInt(Preferences.TOP_MARGIN)));
		BOTTOM_MARGIN_FIELD.addFocusListener(new TextFieldListener(BOTTOM_MARGIN_FIELD, Preferences.getPreferenceInt(Preferences.BOTTOM_MARGIN)));

		Box lookAndFeelBox = Box.createVerticalBox();

		JLabel lookAndFeelLabel = new JLabel(atts.getValue(AbstractPreferencesPanel.A_TITLE));
		AbstractPreferencesPanel.addSingleItemCentered(lookAndFeelLabel, lookAndFeelBox);
		
		lookAndFeelBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addPreferenceItem(DESKTOP_COLOR, DESKTOP_BACKGROUND_COLOR_BUTTON, lookAndFeelBox);
		AbstractPreferencesPanel.addPreferenceItem(BACKGROUND_COLOR, PANEL_BACKGROUND_COLOR_BUTTON, lookAndFeelBox);
		
		lookAndFeelBox.add(Box.createVerticalStrut(5));
		
		AbstractPreferencesPanel.addPreferenceItem(FOREGROUND_COLOR, TEXTAREA_BACKGROUND_COLOR_BUTTON, lookAndFeelBox);
		AbstractPreferencesPanel.addPreferenceItem(TEXT_COLOR, TEXTAREA_FOREGROUND_COLOR_BUTTON, lookAndFeelBox);
		AbstractPreferencesPanel.addPreferenceItem(SELECTED_CHILDREN_COLOR, SELECTED_CHILD_COLOR_BUTTON, lookAndFeelBox);

		lookAndFeelBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addPreferenceItem(LINE_NUMBER_COLOR, LINE_NUMBER_COLOR_BUTTON, lookAndFeelBox);
		AbstractPreferencesPanel.addPreferenceItem(LINE_NUMBER_SELECTED_COLOR, LINE_NUMBER_SELECTED_COLOR_BUTTON, lookAndFeelBox);
		AbstractPreferencesPanel.addPreferenceItem(LINE_NUMBER_SELECTED_CHILD_COLOR, LINE_NUMBER_SELECTED_CHILD_COLOR_BUTTON, lookAndFeelBox);

		lookAndFeelBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addPreferenceItem("Indent", INDENT_FIELD, lookAndFeelBox);

		lookAndFeelBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addPreferenceItem("Vertical Spacing", VERTICAL_SPACING_FIELD, lookAndFeelBox);

		lookAndFeelBox.add(Box.createVerticalStrut(5));

		AbstractPreferencesPanel.addPreferenceItem("Left Margin", LEFT_MARGIN_FIELD, lookAndFeelBox);
		AbstractPreferencesPanel.addPreferenceItem("Right Margin", RIGHT_MARGIN_FIELD, lookAndFeelBox);
		AbstractPreferencesPanel.addPreferenceItem("Top Margin", TOP_MARGIN_FIELD, lookAndFeelBox);
		AbstractPreferencesPanel.addPreferenceItem("Bottom Margin", BOTTOM_MARGIN_FIELD, lookAndFeelBox);

		lookAndFeelBox.add(Box.createVerticalStrut(10));

		AbstractPreferencesPanel.addSingleItemCentered(RESTORE_DEFAULT_LOOK_AND_FEEL_BUTTON, lookAndFeelBox);
		
		add(lookAndFeelBox);
		
		super.endSetup(atts);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(PreferencesFrame.RESTORE_DEFAULTS)) {
			try {
				DESKTOP_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.DESKTOP_BACKGROUND_COLOR).def);
				Preferences.getPreferenceColor(Preferences.DESKTOP_BACKGROUND_COLOR).restoreTemporaryToDefault();

				PANEL_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.PANEL_BACKGROUND_COLOR).def);
				Preferences.getPreferenceColor(Preferences.PANEL_BACKGROUND_COLOR).restoreTemporaryToDefault();

				TEXTAREA_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).def);
				Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).restoreTemporaryToDefault();

				TEXTAREA_FOREGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).def);
				Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).restoreTemporaryToDefault();

				SELECTED_CHILD_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).def);
				Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).restoreTemporaryToDefault();

				LINE_NUMBER_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).def);
				Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).restoreTemporaryToDefault();

				LINE_NUMBER_SELECTED_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).def);
				Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).restoreTemporaryToDefault();

				LINE_NUMBER_SELECTED_CHILD_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).def);
				Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).restoreTemporaryToDefault();

				INDENT_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.INDENT).def));
				Preferences.getPreferenceInt(Preferences.INDENT).restoreTemporaryToDefault();
				
				VERTICAL_SPACING_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.VERTICAL_SPACING).def));
				Preferences.getPreferenceInt(Preferences.VERTICAL_SPACING).restoreTemporaryToDefault();
				
				LEFT_MARGIN_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).def));
				Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).restoreTemporaryToDefault();
				
				RIGHT_MARGIN_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.RIGHT_MARGIN).def));
				Preferences.getPreferenceInt(Preferences.RIGHT_MARGIN).restoreTemporaryToDefault();
				
				TOP_MARGIN_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.TOP_MARGIN).def));
				Preferences.getPreferenceInt(Preferences.TOP_MARGIN).restoreTemporaryToDefault();
				
				BOTTOM_MARGIN_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.BOTTOM_MARGIN).def));
				Preferences.getPreferenceInt(Preferences.BOTTOM_MARGIN).restoreTemporaryToDefault();
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		} else if (e.getActionCommand().equals(DESKTOP_COLOR)) {
			PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
			Color newColor = JColorChooser.showDialog(pf,"Choose Color",Preferences.getPreferenceColor(Preferences.DESKTOP_BACKGROUND_COLOR).tmp);
			if (newColor != null) {
				Preferences.getPreferenceColor(Preferences.DESKTOP_BACKGROUND_COLOR).tmp = newColor;
				DESKTOP_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.DESKTOP_BACKGROUND_COLOR).tmp);
			}
		} else if (e.getActionCommand().equals(BACKGROUND_COLOR)) {
			PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
			Color newColor = JColorChooser.showDialog(pf,"Choose Color",Preferences.getPreferenceColor(Preferences.PANEL_BACKGROUND_COLOR).tmp);
			if (newColor != null) {
				Preferences.getPreferenceColor(Preferences.PANEL_BACKGROUND_COLOR).tmp = newColor;
				PANEL_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.PANEL_BACKGROUND_COLOR).tmp);
			}
		} else if (e.getActionCommand().equals(FOREGROUND_COLOR)) {
			PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
			Color newColor = JColorChooser.showDialog(pf,"Choose Color",Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).tmp);
			if (newColor != null) {
				Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).tmp = newColor;
				TEXTAREA_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).tmp);
			}
		} else if (e.getActionCommand().equals(TEXT_COLOR)) {
			PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
			Color newColor = JColorChooser.showDialog(pf,"Choose Color",Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).tmp);
			if (newColor != null) {
				Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).tmp = newColor;
				TEXTAREA_FOREGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).tmp);
			}
		} else if (e.getActionCommand().equals(SELECTED_CHILDREN_COLOR)) {
			PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
			Color newColor = JColorChooser.showDialog(pf,"Choose Color",Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).tmp);
			if (newColor != null) {
				Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).tmp = newColor;
				SELECTED_CHILD_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).tmp);
			}
		} else if (e.getActionCommand().equals(LINE_NUMBER_COLOR)) {
			PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
			Color newColor = JColorChooser.showDialog(pf,"Choose Color",Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).tmp);
			if (newColor != null) {
				Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).tmp = newColor;
				LINE_NUMBER_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).tmp);
			}
		} else if (e.getActionCommand().equals(LINE_NUMBER_SELECTED_COLOR)) {
			PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
			Color newColor = JColorChooser.showDialog(pf,"Choose Color",Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).tmp);
			if (newColor != null) {
				Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).tmp = newColor;
				LINE_NUMBER_SELECTED_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).tmp);
			}
		} else if (e.getActionCommand().equals(LINE_NUMBER_SELECTED_CHILD_COLOR)) {
			PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
			Color newColor = JColorChooser.showDialog(pf,"Choose Color",Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).tmp);
			if (newColor != null) {
				Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).tmp = newColor;
				LINE_NUMBER_SELECTED_CHILD_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).tmp);
			}
		}
	}
	
	public void setToCurrent() {
		DESKTOP_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.DESKTOP_BACKGROUND_COLOR).cur);
		PANEL_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.PANEL_BACKGROUND_COLOR).cur);
		TEXTAREA_BACKGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).cur);
		TEXTAREA_FOREGROUND_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).cur);
		SELECTED_CHILD_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).cur);

		LINE_NUMBER_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).cur);
		LINE_NUMBER_SELECTED_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).cur);
		LINE_NUMBER_SELECTED_CHILD_COLOR_BUTTON.setBackground(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).cur);
		
		INDENT_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.INDENT).cur));
		VERTICAL_SPACING_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.VERTICAL_SPACING).cur));
		LEFT_MARGIN_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.LEFT_MARGIN).cur));
		RIGHT_MARGIN_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.RIGHT_MARGIN).cur));
		TOP_MARGIN_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.TOP_MARGIN).cur));
		BOTTOM_MARGIN_FIELD.setText(String.valueOf(Preferences.getPreferenceInt(Preferences.BOTTOM_MARGIN).cur));
	}

	public void applyTemporaryToCurrent() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);

		PreferenceColor pDesktopBackgroundColor = (PreferenceColor) prefs.getPreference(Preferences.DESKTOP_BACKGROUND_COLOR);
		PreferenceColor pPanelBackgroundColor = (PreferenceColor) prefs.getPreference(Preferences.PANEL_BACKGROUND_COLOR);
		PreferenceColor pTextareaForegroundColor = (PreferenceColor) prefs.getPreference(Preferences.TEXTAREA_FOREGROUND_COLOR);
		PreferenceColor pTextareaBackgroundColor = (PreferenceColor) prefs.getPreference(Preferences.TEXTAREA_BACKGROUND_COLOR);
		PreferenceColor pSelectedChildColor = (PreferenceColor) prefs.getPreference(Preferences.SELECTED_CHILD_COLOR);

		// Set the Desktop Background color
		Outliner.jsp.getViewport().setBackground(pDesktopBackgroundColor.cur);
		Outliner.desktop.setBackground(pDesktopBackgroundColor.cur);

		// Set the Panel Background color.
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			Outliner.getDocument(i).panel.setBackground(pPanelBackgroundColor.cur);
		}

		// Update the cellRenderers
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			OutlinerDocument doc = Outliner.getDocument(i);
			for (int j = 0; j < OutlineLayoutManager.CACHE_SIZE; j++) {
				doc.panel.layout.textAreas[j].setSelectionColor(pTextareaForegroundColor.cur);
				doc.panel.layout.textAreas[j].setSelectedTextColor(pTextareaBackgroundColor.cur);
				doc.panel.layout.textAreas[j].setCaretColor(pSelectedChildColor.cur);
			}
		}	
	}
}