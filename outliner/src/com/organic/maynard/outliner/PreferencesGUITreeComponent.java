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

public interface PreferencesGUITreeComponent extends GUITreeComponent {

	public void setComponent(JComponent c);
	public JComponent getComponent();
	
	public void setLabelText(String text);
	public String getLabelText();
	
	public void setPreference(Preference pref);
	public Preference getPreference();

}


abstract class AbstractPreferencesGUITreeComponent implements PreferencesGUITreeComponent {
	
	private String labelText = null;
	private JComponent component = null;
	private Preference pref = null;
	
	
	// Constants
	public static final String A_LABEL = "label";
	
	// PreferencesGUITreeComponent Interface
	public void setComponent(JComponent c) {
		this.component = c;
	}
	
	public JComponent getComponent() {
		return this.component;
	}
	
	public void setLabelText(String text) {
		this.labelText = text;
	}
	
	public String getLabelText() {
		return this.labelText;
	}

	public void setPreference(Preference pref) {
		this.pref = pref;
	}
	
	public Preference getPreference() {
		return this.pref;
	}
	
	
	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}

	public void startSetup(AttributeList atts) {
		// Set the Label
		setLabelText(atts.getValue(A_LABEL));

		// Set the Preference
		Preference pref = (Preference) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 2);
		setPreference(pref);
		
		// Add it to the PreferenceList in the parent panel
		PreferencesPanel prefPanel = (PreferencesPanel) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 3);
		prefPanel.addPreference(this);
		
	}

	public void endSetup(AttributeList atts) {

	}
}

class PreferencesGUITreeTextFieldComponent extends AbstractPreferencesGUITreeComponent {

	// Constants
	public static final String A_SIZE = "size";

	public void startSetup(AttributeList atts) {
		// Set the Component
		int size = 10;
		try {
			size = Integer.parseInt(atts.getValue(A_SIZE));
		} catch (NumberFormatException e) {}
		
		JTextField component = new JTextField(size);
		
		setComponent(component);

		super.startSetup(atts);

		// Add it to the GUI
		AbstractPreferencesPanel prefPanel = (AbstractPreferencesPanel) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 3);
		component.addFocusListener(new TextFieldListener(component, getPreference()));
		
		AbstractPreferencesPanel.addPreferenceItem(getLabelText(), component, prefPanel.box);
		prefPanel.box.add(Box.createVerticalStrut(5));
	}
}


class PreferencesGUITreeCheckBoxComponent extends AbstractPreferencesGUITreeComponent {

	public void startSetup(AttributeList atts) {
		// Set the Component
		JCheckBox component = new JCheckBox();
		
		setComponent(component);

		super.startSetup(atts);

		// Add it to the GUI
		AbstractPreferencesPanel prefPanel = (AbstractPreferencesPanel) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 3);
		component.addActionListener(new CheckboxListener(component, getPreference()));

		AbstractPreferencesPanel.addPreferenceItem(getLabelText(), component, prefPanel.box);
		prefPanel.box.add(Box.createVerticalStrut(5));
	}
}


class PreferencesGUITreeComboBoxComponent extends AbstractPreferencesGUITreeComponent {

	private final GraphicsEnvironment GRAPHICS_ENVIRONEMNT = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private final String[] LINE_WRAP_OPTIONS = {Preferences.TXT_WORDS, Preferences.TXT_CHARACTERS};

	private static final String FONT_FACE = "font_face_component";
	private static final String LINE_WRAP = "line_wrap_component";

	private static final String LINE_ENDING = "line_end_component";
	private static final String ENCODING_WHEN_OPENING = "open_encoding_component";
	private static final String ENCODING_WHEN_SAVING = "save_encoding_component";
	private static final String FORMAT_WHEN_OPENING = "open_format_component";
	private static final String FORMAT_WHEN_SAVING = "save_format_component";
	
	public void startSetup(AttributeList atts) {
		// Set the Component
		JComboBox component;
		if (FONT_FACE.equals(getGUITreeComponentID())) {
			component = new JComboBox(GRAPHICS_ENVIRONEMNT.getAvailableFontFamilyNames());
			
		} else if (LINE_WRAP.equals(getGUITreeComponentID())) {
			component = new JComboBox(LINE_WRAP_OPTIONS);
			
		} else if (LINE_ENDING.equals(getGUITreeComponentID())) {
			component = new JComboBox(Preferences.PLATFORM_IDENTIFIERS);
			
		} else if (ENCODING_WHEN_OPENING.equals(getGUITreeComponentID())) {
			component = new JComboBox();
			for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
				component.addItem((String) Preferences.ENCODINGS.elementAt(i));
			}
			
		} else if (ENCODING_WHEN_SAVING.equals(getGUITreeComponentID())) {
			component = new JComboBox();
			for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
				component.addItem((String) Preferences.ENCODINGS.elementAt(i));
			}
					
		} else if (FORMAT_WHEN_OPENING.equals(getGUITreeComponentID())) {
			component = new JComboBox();
			for (int i = 0; i < Preferences.FILE_FORMATS_OPEN.size(); i++) {
				component.addItem((String) Preferences.FILE_FORMATS_OPEN.elementAt(i));
			}
			
		} else if (FORMAT_WHEN_SAVING.equals(getGUITreeComponentID())) {
			component = new JComboBox();
			for (int i = 0; i < Preferences.FILE_FORMATS_SAVE.size(); i++) {
				component.addItem((String) Preferences.FILE_FORMATS_SAVE.elementAt(i));
			}
			
		} else {
			component = new JComboBox();
			
		}
		
		setComponent(component);

		super.startSetup(atts);

		// Add it to the GUI
		AbstractPreferencesPanel prefPanel = (AbstractPreferencesPanel) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 3);
		component.addItemListener(new ComboBoxListener(component, getPreference()));

		AbstractPreferencesPanel.addPreferenceItem(getLabelText(), component, prefPanel.box);
		prefPanel.box.add(Box.createVerticalStrut(5));
	}
}


class PreferencesGUITreeColorButtonComponent extends AbstractPreferencesGUITreeComponent implements ActionListener {
	private static final String CHOOSE_COLOR = "Choose Color";

	public void startSetup(AttributeList atts) {
		// Set the Component
		JButton component = new JButton("");
		
		setComponent(component);

		super.startSetup(atts);

		// Add it to the GUI
		component.addActionListener(this);
		component.setActionCommand(getLabelText());

		AbstractPreferencesPanel prefPanel = (AbstractPreferencesPanel) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 3);
		
		AbstractPreferencesPanel.addPreferenceItem(getLabelText(), component, prefPanel.box);
		prefPanel.box.add(Box.createVerticalStrut(5));
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
		PreferenceColor pref = (PreferenceColor) getPreference();
		
		Color newColor = JColorChooser.showDialog(pf, CHOOSE_COLOR, pref.tmp);
		if (newColor != null) {
			pref.tmp = newColor;
			getComponent().setBackground(pref.tmp);
		}
	}
}
