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
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;

public class ScriptsManager extends AbstractGUITreeJDialog implements ActionListener {
	
	// Constants
	private static final int INITIAL_WIDTH = 400;
	private static final int INITIAL_HEIGHT = 400;
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 300;

	private static String NEW = null;

	public ScriptsManagerModel model = new ScriptsManagerModel();
	public ThreadsTableModel threadsTableModel = new ThreadsTableModel();
	
	protected ScriptEditor scriptEditor = null;

	// Define Fields and Buttons
	protected ArrayList scriptNames = new ArrayList();	
	protected ArrayList scriptClassNames = new ArrayList();

	private JButton newButton = null;
	protected JComboBox scriptType = new JComboBox();	

	private JLabel scriptLabel = null;

	private JLabel threadLabel = null;

	
	// GUI Elements


	// The Constructors
	public ScriptsManager() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);

		NEW = GUITreeLoader.reg.getText("new");

		newButton = new JButton(NEW);
		scriptLabel = new JLabel(GUITreeLoader.reg.getText("script"));
		threadLabel = new JLabel(GUITreeLoader.reg.getText("thread"));
	}

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		Outliner.scriptsManager = this;
		
		// Define New Script Pulldown area
		newButton.addActionListener(this);
		
		Box newBox = Box.createHorizontalBox();
		newBox.add(scriptType);
		newBox.add(Box.createHorizontalStrut(5));
		newBox.add(newButton);

		// Setup Script Box
		Box scriptBox = Box.createVerticalBox();

		scriptBox.add(scriptLabel);		
		scriptBox.add(new JScrollPane(new ScriptsTable()));
		
		scriptBox.add(Box.createVerticalStrut(5));

		scriptBox.add(threadLabel);		
		scriptBox.add(new JScrollPane(new ThreadsTable()));
				
		// Put it all together
		getContentPane().add(newBox, BorderLayout.NORTH);
		getContentPane().add(scriptBox, BorderLayout.CENTER);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(NEW)) {
			newScript();
		}
	}
	
	private void newScript() {
		// Find the ClassName
		String className = getClassNameFromScriptTypeName((String) scriptType.getSelectedItem());
		
		// Get the object
		try {
			Class theClass = Class.forName(className);
			Script script = (Script) theClass.newInstance();
			displayScriptEditor(script, ScriptEditor.BUTTON_MODE_CREATE);
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	protected void displayScriptEditor(Script script, int buttonMode) {
		ScriptConfig scriptConfig = (ScriptConfig) script.getScriptConfigurator();
		scriptConfig.init(script);
		scriptEditor.setScriptConfigAndShow(scriptConfig, buttonMode);
	}
	
	// Utility Functions
	public String getClassNameFromScriptTypeName(String scriptTypeName) {
		for (int i = 0; i < scriptNames.size(); i++) {
			if (((String) scriptNames.get(i)).equals(scriptTypeName)) {
				return (String) scriptClassNames.get(i);
			}
		}
		
		return null;
	}

	public String getScriptTypeNameFromClassName(String className) {
		for (int i = 0; i < scriptClassNames.size(); i++) {
			if (((String) scriptClassNames.get(i)).equals(className)) {
				return (String) scriptNames.get(i);
			}
		}
		
		return null;
	}
}
