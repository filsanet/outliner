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
import java.awt.Window;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.xml.sax.*;

public class PreferencesFrame extends JFrame implements TreeSelectionListener, ActionListener, GUITreeComponent {

	// Constants
	static final int MIN_WIDTH = 450;
	static final int MIN_HEIGHT = 425;
 
 	static final int INITIAL_WIDTH = 450;
	static final int INITIAL_HEIGHT = 425;

	public static final String A_TITLE = "title";

	// Main Component Containers
	public static final JPanel RIGHT_PANEL = new JPanel();
	public static final CardLayout CARD_LAYOUT = new CardLayout();
	public static final JPanel BOTTOM_PANEL = new JPanel();
		
	// Button Text and Other Copy
	public static final String OK = "OK";
	public static final String CANCEL = "Cancel";
	public static final String APPLY = "Apply";
	public static final String RESTORE_DEFAULTS = "Restore Defaults";

	// Define Fields and Buttons
	public static final JButton BOTTOM_OK = new JButton(OK);
	public static final JButton BOTTOM_CANCEL = new JButton(CANCEL);
	public static final JButton BOTTOM_APPLY = new JButton(APPLY);


	// The Constructor
	public PreferencesFrame() {}


	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}

	public void startSetup(AttributeList atts) {
		setTitle(atts.getValue(A_TITLE));
		setVisible(false);
		
		addComponentListener(new WindowSizeManager(MIN_WIDTH,MIN_HEIGHT));
		addWindowListener(new PreferencesFrameWindowMonitor());
			
		// Create the Layout
		setSize(INITIAL_WIDTH,INITIAL_HEIGHT);
		setResizable(true);
		
		// Define the Bottom Panel
		BOTTOM_PANEL.setLayout(new FlowLayout());
		
		BOTTOM_OK.addActionListener(this);
		BOTTOM_PANEL.add(BOTTOM_OK);

		BOTTOM_CANCEL.addActionListener(this);
		BOTTOM_PANEL.add(BOTTOM_CANCEL);

		BOTTOM_APPLY.addActionListener(this);
		BOTTOM_PANEL.add(BOTTOM_APPLY);
		
		// Set the default button
		getRootPane().setDefaultButton(BOTTOM_OK);

		// Define the Right Panel
		RIGHT_PANEL.setLayout(CARD_LAYOUT);
	}
	
	public void endSetup() {
		// Define the JTree		
		JTree tree = new JTree(treeModel);
		tree.addTreeSelectionListener(this);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Put it all together
		JScrollPane jsp = new JScrollPane(tree);
		JScrollPane jsp2 = new JScrollPane(RIGHT_PANEL);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,jsp,jsp2);
		getContentPane().add(BOTTOM_PANEL, BorderLayout.SOUTH);
		getContentPane().add(splitPane, BorderLayout.CENTER);		
	}


	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");
	private DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
	
	public void addPanelToTree(String name) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
			rootNode.add(node);
	}
		
	// TreeSelectionListener interface
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		CARD_LAYOUT.show(RIGHT_PANEL, (String) node.getUserObject());
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(OK)) {
			main_ok();
		} else if (e.getActionCommand().equals(APPLY)) {
			main_apply();
		} else if (e.getActionCommand().equals(CANCEL)) {
			main_cancel();
		}
	}
	
	private void main_ok() {
		main_apply();
		this.setVisible(false);
	}

	private void main_apply() {
		Preferences.applyTemporaryToCurrent();
		Preferences.saveConfigFile(Outliner.CONFIG_FILE);
		Outliner.redrawAllOpenDocuments();
	}

	public void main_cancel() {
		// Restore Prefs
		Preferences.restoreTemporaryToCurrent();
		
		// Restore GUI
		PreferencesPanelOpenAndSave ppos = (PreferencesPanelOpenAndSave) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_PANEL_OPEN_AND_SAVE);
		ppos.setToCurrent();

		PreferencesPanelLookAndFeel pplf = (PreferencesPanelLookAndFeel) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_PANEL_LOOK_AND_FEEL);
		pplf.setToCurrent();

		PreferencesPanelMisc ppm = (PreferencesPanelMisc) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_PANEL_MISC);
		ppm.setToCurrent();

		PreferencesPanelEditor ppe = (PreferencesPanelEditor) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_PANEL_EDITOR);
		ppe.setToCurrent();
		
		// Hide Window
		this.setVisible(false);
	}
}


public class TextFieldListener implements FocusListener {
	private JTextField field = null;
	private Preference pref = null;
	
	public TextFieldListener(JTextField field, Preference pref) {
		this.field = field;
		this.pref = pref;
	}
	
	// FocusListener Interface
	public void focusGained(FocusEvent e) {
		handleUpdate();
	}
	
	public void focusLost(FocusEvent e) {
		handleUpdate();
	}
	
	private void handleUpdate() {
		// We can simplify this when we move more methods into the Preference Interface.
		if (pref instanceof PreferenceInt) {
			PreferenceInt prefInt = (PreferenceInt) pref;
			prefInt.setTmp(field.getText());
			field.setText(String.valueOf(prefInt.tmp));
		} else if (pref instanceof PreferenceString) {
			PreferenceString prefString = (PreferenceString) pref;
			prefString.setTmp(field.getText());
			field.setText(prefString.tmp);
		}
	}
}

public class ComboBoxListener implements ItemListener {
	private JComboBox box = null;
	private Preference pref = null;
	
	public ComboBoxListener(JComboBox box, Preference pref) {
		this.box = box;
		this.pref = pref;
	}
	
	// ItemListener Interface
	public void itemStateChanged(ItemEvent e) {
		handleUpdate();
	}
		
	private void handleUpdate() {
		if (pref instanceof PreferenceString) {
			PreferenceString prefString = (PreferenceString) pref;
			prefString.setTmp((String) box.getSelectedItem());
			box.setSelectedItem(prefString.tmp);
		}	
	}
}

public class CheckboxListener implements ActionListener {
	private JCheckBox checkbox = null;
	private Preference pref = null;
	
	public CheckboxListener(JCheckBox checkbox, Preference pref) {
		this.checkbox = checkbox;
		this.pref = pref;
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		handleUpdate();
	}
	
	private void handleUpdate() {
		if (pref instanceof PreferenceBoolean) {
			PreferenceBoolean prefBoolean = (PreferenceBoolean) pref;
			prefBoolean.setTmp(checkbox.isSelected());
			checkbox.setSelected(prefBoolean.tmp);
		}	
	}
}


public class PreferencesFrameWindowMonitor extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
		PreferencesFrame pf = (PreferencesFrame) e.getWindow();
		pf.main_cancel();
	}
}
