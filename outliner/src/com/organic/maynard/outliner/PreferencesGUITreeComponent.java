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
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
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
		
		if (!clickBlocker) {
			clickBlocker = true;
			Color newColor = JColorChooser.showDialog(pf, getLabelText(), pref.tmp);
			if (newColor != null) {
				pref.tmp = newColor;
				getComponent().setBackground(pref.tmp);
			}
			clickBlocker = false;
		}
	}
	
	// This prevents double clicks from launching the color chooser twice. This seems like a bug since
	// you would expect only one action event to be created when the user double-clicks on a button,
	// but apparently 2 are created. Until the "bug" is really figured out this hack will make things a 
	// little better.
	private boolean clickBlocker = false; 
}
