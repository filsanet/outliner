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
 
package com.organic.maynard.outliner.util.preferences;

import com.organic.maynard.outliner.*;

import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import org.xml.sax.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public abstract class AbstractPreferencesPanel extends JPanel implements PreferencesPanel, GUITreeComponent, ActionListener {
	
	// Constants
	public static final String A_TITLE = "title";
	public static final String A_ID = "id";

	// GUI Components
	public Box box = Box.createVerticalBox();
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
		
		// Add the preference panel at the appropriate depth.
		int depth = 0;
		while (true) {
			GUITreeComponent c = GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - ++depth);
			if(!(c instanceof PreferencesPanel)) {
				depth--;
				break;
			}
		}
		pf.addPanelToTree(title, depth);
		
		// Add this panel to the PreferencesPanel Registry
		Outliner.prefs.addPreferencesPanel(id, this);

		// Start setting up box
		addSingleItemCentered(new JLabel(title), box);
		
		box.add(Box.createVerticalStrut(10));
	}
	
	public void endSetup(AttributeList atts) {

		RESTORE_DEFAULT_EDITOR_BUTTON.addActionListener(this);		
		
		box.add(Box.createVerticalStrut(5));
		AbstractPreferencesPanel.addSingleItemCentered(RESTORE_DEFAULT_EDITOR_BUTTON, box);

		add(box);
		
		// Update all the prefs.
		setToCurrent();
	}
	
	
	// PreferencesPanel Interface
	private ArrayList containerStack = new ArrayList();
	
	public Container getCurrentContainer() {
		if (containerStack.size() > 0) {
			return (Container) containerStack.get(containerStack.size() - 1);
		} else {
			return this.box;
		}
	}
	
	public void startAddSubContainer(Container c) {
		containerStack.add(c);
	}
	
	public void endAddSubContainer(Container c) {
		containerStack.remove(containerStack.size() - 1);
		AbstractPreferencesPanel.addSingleItemCentered((JComponent) c, getCurrentContainer());
		getCurrentContainer().add(Box.createVerticalStrut(5));
	}

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

		for (int i = 0, limit = getPreferenceListSize(); i < limit; i++) {
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
							
			} else if (comp instanceof PreferencesGUITreeTextAreaComponent) {
				JTextArea list = (JTextArea) ((JScrollPane) comp.getComponent()).getViewport().getView();
				PreferenceStringList prefStringList = (PreferenceStringList) pref;

				StringBuffer buf = new StringBuffer();
				for (int j = 0; j < prefStringList.cur.size(); j++) {
					buf.append(prefStringList.cur.get(j)).append("\n");
				}
				list.setText(buf.toString());
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
									
					} else if (comp instanceof PreferencesGUITreeTextAreaComponent) {
						JTextArea list = (JTextArea) ((JScrollPane) comp.getComponent()).getViewport().getView();
						PreferenceStringList prefStringList = (PreferenceStringList) pref;
						
						StringBuffer buf = new StringBuffer();
						for (int j = 0; j < prefStringList.def.size(); j++) {
							buf.append(prefStringList.def.get(j)).append("\n");
						}
						list.setText(buf.toString());
				
					}
					
					pref.restoreTemporaryToDefault();
				}
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		}
	}
	
	
	// Static Methods
	public static void addArrayToComboBox(Object[] array, String componentID) {
		JComboBox component = (JComboBox) ((PreferencesGUITreeComboBoxComponent) GUITreeLoader.reg.get(componentID)).getComponent();
		for (int i = 0; i < array.length; i++) {
			component.addItem(array[i].toString());
		}	
	}
	
	private static Dimension prefDim = new Dimension(3,1);
	
	public static void addPreferenceItem(String text, JComponent field, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel(text));
		box.add(Box.createRigidArea(prefDim));
		field.setMaximumSize(field.getPreferredSize());
		box.add(field);
		container.add(box);
	}

	public static void addSingleItemCentered(JComponent component, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		component.setMaximumSize(component.getPreferredSize());
		box.add(component);
		box.add(Box.createHorizontalGlue());
		container.add(box);
	}
}