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
	public static final String A_STYLE = "style";
	
	public static final String STYLE_SIDE_BY_SIDE = "side_by_side"; // The default
	public static final String STYLE_SINGLE_CENTERED = "single_centered";
	
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
		AbstractPreferencesPanel prefPanel = (AbstractPreferencesPanel) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 3);
		
		// Get the style to use
		String style = atts.getValue(A_STYLE);
		if (style == null) {
			style = STYLE_SIDE_BY_SIDE;
		}
		
		if (style.equals(STYLE_SINGLE_CENTERED)) {
			AbstractPreferencesPanel.addSingleItemCentered(new JLabel(getLabelText()), prefPanel.box);
			AbstractPreferencesPanel.addSingleItemCentered(getComponent(), prefPanel.box);
		} else if (style.equals(STYLE_SIDE_BY_SIDE)) {
			AbstractPreferencesPanel.addPreferenceItem(getLabelText(), getComponent(), prefPanel.box);		
		} else {
			AbstractPreferencesPanel.addPreferenceItem(getLabelText(), getComponent(), prefPanel.box);
		}
		prefPanel.box.add(Box.createVerticalStrut(5));
	}
}


class PreferencesGUITreeTextFieldComponent extends AbstractPreferencesGUITreeComponent {

	// Constants
	public static final String A_SIZE = "size";

	public void startSetup(AttributeList atts) {
		int size = 10;
		try {
			size = Integer.parseInt(atts.getValue(A_SIZE));
		} catch (NumberFormatException e) {}
		JTextField component = new JTextField(size);
		setComponent(component);
		super.startSetup(atts);
		component.addFocusListener(new TextFieldListener(component, getPreference()));
	}
}


class PreferencesGUITreeCheckBoxComponent extends AbstractPreferencesGUITreeComponent {

	public void startSetup(AttributeList atts) {
		JCheckBox component = new JCheckBox();
		setComponent(component);
		super.startSetup(atts);
		component.addActionListener(new CheckboxListener(component, getPreference()));
	}
}


class PreferencesGUITreeComboBoxComponent extends AbstractPreferencesGUITreeComponent {

	public void startSetup(AttributeList atts) {
		// Set the Component
		JComboBox component = new JComboBox();
		setComponent(component);
		super.startSetup(atts);
		component.addItemListener(new ComboBoxListener(component, getPreference()));
	}
}


class PreferencesGUITreeColorButtonComponent extends AbstractPreferencesGUITreeComponent implements ActionListener {

	public void startSetup(AttributeList atts) {
		// Set the Component
		JButton component = new JButton("");
		setComponent(component);
		super.startSetup(atts);
		component.addActionListener(this);
		component.setActionCommand(getLabelText());
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
		PreferenceColor pref = (PreferenceColor) getPreference();
		
		Color newColor = JColorChooser.showDialog(pf, getLabelText(), pref.tmp);
		if (newColor != null) {
			pref.tmp = newColor;
			getComponent().setBackground(pref.tmp);
		}
	}
}
