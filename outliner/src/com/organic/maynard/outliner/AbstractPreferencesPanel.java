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

public abstract class AbstractPreferencesPanel extends JPanel implements PreferencesPanel, GUITreeComponent {
	
	// Constants
	public static final String A_TITLE = "title";
	public static final String A_ID = "id";

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
	}
	
	public void endSetup(AttributeList atts) {}
	
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