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

import java.lang.reflect.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class MacroManagerFrame extends JFrame implements ActionListener {

	static final int MIN_WIDTH = 350;
	static final int MIN_HEIGHT = 300;

	static final int INITIAL_WIDTH = 350;
	static final int INITIAL_HEIGHT = 300;
        	
	public static final String NEW = "New";
	
	public MacroEditor macroEditor = null;

	public Vector macroNames = new Vector();
	public Vector macroClassNames = new Vector();

	// Define Fields and Buttons
	public JButton newButton = new JButton(NEW);
	public JComboBox macroType = new JComboBox();	
	public JList macroList = new JList();
		
	// The Constructor
	public MacroManagerFrame() {
		super("Macro Manager");
		setVisible(false);
				
		macroEditor = new MacroEditor(this);
		
		addComponentListener(new WindowSizeManager(MIN_WIDTH,MIN_HEIGHT));
		addWindowListener(new MacroManagerFrameWindowMonitor());
		
		setSize(INITIAL_WIDTH,INITIAL_HEIGHT);
		setResizable(true);
		
		// Define New Macro Pulldown area
		newButton.addActionListener(this);
		
		Box newBox = Box.createHorizontalBox();
		newBox.add(macroType);
		newBox.add(Box.createHorizontalStrut(5));
		newBox.add(newButton);
		
		// Define Macro List
		macroList.setModel(new DefaultListModel());
		macroList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = macroList.locationToIndex(e.getPoint());
					DefaultListModel model = (DefaultListModel) macroList.getModel();
					updateMacro((String) model.get(index));
				}
			}
		};
		macroList.addMouseListener(mouseListener);
		
		JScrollPane jsp = new JScrollPane(macroList);
		
		// Put it all together
		getContentPane().add(newBox, BorderLayout.NORTH);
		getContentPane().add(jsp, BorderLayout.CENTER);
		
	}

	private void updateMacro(String macroName) {
		Macro macro = Outliner.macroPopup.getMacro(macroName);
		
		if (macro != null) {
			displayMacroEditor(macro,MacroEditor.BUTTON_MODE_UPDATE);
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
			displayMacroEditor(macro,MacroEditor.BUTTON_MODE_CREATE);
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public String getClassNameFromMacroTypeName(String macroTypeName) {
		String className = "";
		int index = -1;
		for (int i = 0; i < macroNames.size(); i++) {
			if (((String) macroNames.elementAt(i)).equals(macroTypeName)) {
				index = i;
				break;
			}
		}
		
		if (index >= 0) {
			className = (String) macroClassNames.elementAt(index);
		}
		
		return className;
	}

	public String getMacroTypeNameFromClassName(String className) {
		String macroTypeName = "";
		int index = -1;
		for (int i = 0; i < macroClassNames.size(); i++) {
			if (((String) macroClassNames.elementAt(i)).equals(className)) {
				index = i;
				break;
			}
		}
		
		if (index >= 0) {
			macroTypeName = (String) macroNames.elementAt(index);
		}
		
		return macroTypeName;
	}
	
	private void displayMacroEditor(Macro macro, int buttonMode) {
		MacroConfig macroConfig = (MacroConfig) macro.getConfigurator();
		macroConfig.init(macro);
		macroEditor.setMacroConfig(macroConfig);
		macroEditor.setButtonMode(buttonMode);
		if (buttonMode == MacroEditor.BUTTON_MODE_CREATE) {
			macroEditor.setTitle(MacroEditor.BUTTON_MODE_CREATE_TITLE);
		} else if (buttonMode == MacroEditor.BUTTON_MODE_UPDATE) {
			macroEditor.setTitle(MacroEditor.BUTTON_MODE_UPDATE_TITLE);
		}
		macroEditor.setVisible(true);
	}
	
	// Macro Saving and Loading Methods
	public void deleteMacro(String name) {
		String filename = Outliner.MACROS_DIR + name + ".ser";
		File file = new File(filename);
		file.delete();
	}
		
	public void saveMacro(Macro macro) {
		String filename = Outliner.MACROS_DIR + macro.getName() + ".ser";
		
		try {
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename));
			stream.writeObject(macro);
			stream.close();
		
		} catch (IOException ioe) {
			System.out.println("Exception: " + ioe);		
		}
	}
	
	public Macro loadMacro(String filename) {
		Macro macro = null;
		
		try {
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(filename));
			macro = (Macro) stream.readObject();
			stream.close();
		} catch (OptionalDataException ode) {
			System.out.println("Exception loading " + filename + ": " + ode);
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception loading " + filename + ": " + cnfe);
		} catch (FileNotFoundException fnfe) {
			System.out.println("Exception loading " + filename + ": " + fnfe);
		} catch (StreamCorruptedException sce) {
			System.out.println("Exception loading " + filename + ": " + sce);		
		} catch (IOException ioe) {
			System.out.println("Exception loading " + filename + ": " + ioe);		
		}
		
		return macro;
	}
}

public class MacroManagerFrameWindowMonitor extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
		MacroManagerFrame mmf = (MacroManagerFrame) e.getWindow();
		mmf.setVisible(false);
	}
}







