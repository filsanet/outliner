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
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class MacroManagerFrame extends AbstractGUITreeJDialog implements ActionListener {

	// Constants
	private static final int INITIAL_WIDTH = 275;
	private static final int INITIAL_HEIGHT = 400;
 	private static final int MINIMUM_WIDTH = 275;
	private static final int MINIMUM_HEIGHT = 200;
       	
	private static final String NEW = "New";


	// Define Fields and Buttons
	protected MacroEditor macroEditor = null;

	protected ArrayList macroNames = new ArrayList();
	protected ArrayList macroClassNames = new ArrayList();

	private JButton newButton = new JButton(NEW);
	protected JComboBox macroType = new JComboBox();	
	protected JList macroList = new JList();


	// The Constructor
	public MacroManagerFrame() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}
	
	
	// GUITreeComponentInterface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		Outliner.macroManager = this;
		
		// Define New Macro Pulldown area
		newButton.addActionListener(this);
		
		Box newBox = Box.createHorizontalBox();
		newBox.add(macroType);
		newBox.add(Box.createHorizontalStrut(5));
		newBox.add(newButton);
		
		// Define Macro List
		macroList.setModel(new DefaultListModel());
		macroList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		macroList.addMouseListener(
			new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int index = macroList.locationToIndex(e.getPoint());
						DefaultListModel model = (DefaultListModel) macroList.getModel();
						updateMacro((String) model.get(index));
					}
				}
			}
		);
		
		JScrollPane jsp = new JScrollPane(macroList);
		
		// Put it all together
		getContentPane().add(newBox, BorderLayout.NORTH);
		getContentPane().add(jsp, BorderLayout.CENTER);		
	}


	private void updateMacro(String macroName) {
		Macro macro = Outliner.macroPopup.getMacro(macroName);
		
		if (macro != null) {
			displayMacroEditor(macro, MacroEditor.BUTTON_MODE_UPDATE);
		}
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(NEW)) {
			newMacro();
		}
	}
	
	private void newMacro() {
		// Find the ClassName
		String className = getClassNameFromMacroTypeName((String) macroType.getSelectedItem());
		
		// Get the object
		try {
			Class theClass = Class.forName(className);
			Macro macro = (Macro) theClass.newInstance();
			displayMacroEditor(macro, MacroEditor.BUTTON_MODE_CREATE);
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void displayMacroEditor(Macro macro, int buttonMode) {
		MacroConfig macroConfig = (MacroConfig) macro.getConfigurator();
		macroConfig.init(macro);
		macroEditor.setMacroConfigAndShow(macroConfig, buttonMode);
	}


	// Utility Functions
	public String getClassNameFromMacroTypeName(String macroTypeName) {
		for (int i = 0; i < macroNames.size(); i++) {
			if (((String) macroNames.get(i)).equals(macroTypeName)) {
				return (String) macroClassNames.get(i);
			}
		}
		
		return null;
	}

	public String getMacroTypeNameFromClassName(String className) {
		for (int i = 0; i < macroClassNames.size(); i++) {
			if (((String) macroClassNames.get(i)).equals(className)) {
				return (String) macroNames.get(i);
			}
		}
		
		return null;
	}
}