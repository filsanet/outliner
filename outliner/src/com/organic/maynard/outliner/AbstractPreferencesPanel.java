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
import java.util.*;

import org.xml.sax.*;

public abstract class AbstractPreferencesPanel extends JPanel implements PreferencesPanel, GUITreeComponent, ActionListener {
	
	// Constants
	public static final String A_TITLE = "title";
	public static final String A_ID = "id";

	// GUI Components
	protected Box box = Box.createVerticalBox();
	protected final JButton RESTORE_DEFAULT_EDITOR_BUTTON = new JButton(PreferencesFrame.RESTORE_DEFAULTS);

	// The Constructor
	public AbstractPreferencesPanel() {}


	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}

	public void startSetup(AttributeList atts) {
		String title = atts.getValue(A_TITLE);
		String id = atts.getValue(A_ID);


		// Add this panel to the PreferencesFrame.
		PreferencesFrame.RIGHT_PANEL.add(this, title);
		PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
		pf.addPanelToTree(title);
		
		// Add this panel to the PreferencesPanel Registry
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		prefs.addPreferencesPanel(id, this);

		// Start setting up box
		JLabel label = new JLabel(atts.getValue(A_TITLE));
		addSingleItemCentered(label, box);
		
		box.add(Box.createVerticalStrut(10));
	}
	
	public void endSetup(AttributeList atts) {

		RESTORE_DEFAULT_EDITOR_BUTTON.addActionListener(this);		
		
		box.add(Box.createVerticalStrut(5));
		AbstractPreferencesPanel.addSingleItemCentered(RESTORE_DEFAULT_EDITOR_BUTTON, box);

		add(box);
	}
	
	
	// PreferencesPanel Interface
	private ArrayList prefs = new ArrayList();
	
	public void addPreference(PreferencesGUITreeComponent pref) {
		prefs.add(pref);
	}
	
	public PreferencesGUITreeComponent getPreference(int i) {
		return (PreferencesGUITreeComponent) prefs.get(i);
	}
	
	public int getPreferenceListSize() {
		return prefs.size();
	}

	public void setToCurrent() {

		for (int i = 0; i < getPreferenceListSize(); i++) {
			PreferencesGUITreeComponent comp = getPreference(i);
			Preference pref = comp.getPreference();
			
			if (comp instanceof PreferencesGUITreeTextFieldComponent) {
				JTextField text = (JTextField) comp.getComponent();
				text.setText(pref.getCur());
				
			} else if (comp instanceof PreferencesGUITreeComboBoxComponent) {
				JComboBox comboBox = (JComboBox) comp.getComponent();
				comboBox.setSelectedItem(pref.getCur());
							
			} else if (comp instanceof PreferencesGUITreeCheckBoxComponent) {
				JCheckBox checkBox = (JCheckBox) comp.getComponent();
				checkBox.setSelected(new Boolean(pref.getCur()).booleanValue());
							
			} else if (comp instanceof PreferencesGUITreeColorButtonComponent) {
				JButton button = (JButton) comp.getComponent();
				PreferenceColor prefColor = (PreferenceColor) pref;
				button.setBackground(prefColor.cur);
							
			}
		}
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(PreferencesFrame.RESTORE_DEFAULTS)) {
			try {
			
				for (int i = 0; i < getPreferenceListSize(); i++) {
					PreferencesGUITreeComponent comp = getPreference(i);
					Preference pref = comp.getPreference();
					
					if (comp instanceof PreferencesGUITreeTextFieldComponent) {
						JTextField text = (JTextField) comp.getComponent();
						text.setText(pref.getDef());
						
					} else if (comp instanceof PreferencesGUITreeComboBoxComponent) {
						JComboBox comboBox = (JComboBox) comp.getComponent();
						comboBox.setSelectedItem(pref.getDef());
									
					} else if (comp instanceof PreferencesGUITreeCheckBoxComponent) {
						JCheckBox checkBox = (JCheckBox) comp.getComponent();
						checkBox.setSelected(new Boolean(pref.getDef()).booleanValue());
									
					} else if (comp instanceof PreferencesGUITreeColorButtonComponent) {
						JButton button = (JButton) comp.getComponent();
						PreferenceColor prefColor = (PreferenceColor) pref;
						button.setBackground(prefColor.def);
									
					}
					
					pref.restoreTemporaryToDefault();
				}
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		}
	}
	
	
	// Static Methods
	protected static void addArrayToComboBox(Object[] array, String componentID) {
		JComboBox component = (JComboBox) ((PreferencesGUITreeComboBoxComponent) GUITreeLoader.reg.get(componentID)).getComponent();
		for (int i = 0; i < array.length; i++) {
			component.addItem(array[i].toString());
		}	
	}
	
	protected static void addPreferenceItem(String text, JComponent field, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel(text));
		box.add(Box.createRigidArea(new Dimension(3,1)));
		field.setMaximumSize(field.getPreferredSize());
		box.add(field);
		container.add(box);
	}

	protected static void addSingleItemCentered(JComponent component, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		component.setMaximumSize(component.getPreferredSize());
		box.add(component);
		box.add(Box.createHorizontalGlue());
		container.add(box);
	}
}