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
import javax.swing.event.*;

public class XMLRPCMacroConfig extends JPanel implements MacroConfig {
	
	public static final String NAME = "Macro Name";
	public static final String SERVER = "Server";
	public static final String PORT = "Port";
	public static final String DO_REPLACEMENT = "Do Replacement";
	
	protected JLabel nameLabel = new JLabel(NAME);
	protected JLabel serverLabel = new JLabel(SERVER);
	protected JLabel portLabel = new JLabel(PORT);
	protected JLabel doReplacementLabel = new JLabel(DO_REPLACEMENT);

	protected JTextField nameField = new JTextField();
	protected JTextField serverField = new JTextField();
	protected JTextField portField = new JTextField();

	protected ButtonGroup buttonGroup = new ButtonGroup();
	protected JRadioButton yesRadio = new JRadioButton("Yes");
	protected JRadioButton noRadio = new JRadioButton("No");

	// The Constructor
	public XMLRPCMacroConfig() {
		
		// Create the layout
		this.setLayout(new BorderLayout());

		Box mainBox = Box.createVerticalBox();
		mainBox.add(nameLabel);
		nameField.setMargin(new Insets(1,3,1,3));
		mainBox.add(nameField);
		mainBox.add(Box.createVerticalStrut(5));
		mainBox.add(serverLabel);
		serverField.setMargin(new Insets(1,3,1,3));
		mainBox.add(serverField);
		mainBox.add(Box.createVerticalStrut(5));
		mainBox.add(portLabel);
		portField.setMargin(new Insets(1,3,1,3));
		mainBox.add(portField);
		mainBox.add(Box.createVerticalStrut(5));

		buttonGroup.add(yesRadio);
		buttonGroup.add(noRadio);
		
		Box radioBox = Box.createHorizontalBox();
		radioBox.add(doReplacementLabel);
		radioBox.add(Box.createHorizontalStrut(5));
		radioBox.add(yesRadio);
		radioBox.add(Box.createHorizontalStrut(5));
		radioBox.add(noRadio);

		mainBox.add(radioBox);
		
		// Put it all together
		this.add(mainBox,BorderLayout.NORTH);
	}

	
	// MacroConfig Interface
	protected XMLRPCMacro macro = null;
	
	public void init(Macro xmlrpcMacro) {
		this.macro = (XMLRPCMacro) xmlrpcMacro;
		nameField.setText(macro.getName());
		serverField.setText(macro.getServerName());
		portField.setText(Integer.toString(macro.getPort()));
		if (this.macro.isReplacing()) {
			yesRadio.setSelected(true);
		} else {
			noRadio.setSelected(true);
		}
	}

	public Macro getMacro() {return this.macro;}
	
	public boolean create() {
		String name = nameField.getText();
		String serverName = serverField.getText();
		int port = 8088;
		
		// Validate ServerName
		if (serverName.equals("")) {
			return false;
		}
		
		// Validate Port
		try {
			port = Integer.parseInt(portField.getText());
		} catch (NumberFormatException nfe) {
			return false;
		}
		
		// Validate Name and do the create
		if (validateExistence(name) && validateUniqueness(name)) {
			macro.setName(name);
			macro.setServerName(serverName);
			macro.setPort(port);
			if (yesRadio.isSelected()) {
				macro.setReplacing(true);
			} else {
				macro.setReplacing(false);			
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update() {
		String name = nameField.getText();
		String serverName = serverField.getText();
		int port = 8088;
		
		// Validate ServerName
		if (serverName.equals("")) {
			return false;
		}
		
		// Validate Port
		try {
			port = Integer.parseInt(portField.getText());
		} catch (NumberFormatException nfe) {
			return false;
		}
		
		// Validate Name and do the update
		if (validateExistence(name)) {
			if (name.equals(macro.getName())) {
				macro.setServerName(serverName);
				macro.setPort(port);
				if (yesRadio.isSelected()) {
					macro.setReplacing(true);
				} else {
					macro.setReplacing(false);			
				}
				return true;
			} else if (validateUniqueness(name)) {
				macro.setName(name);
				macro.setServerName(serverName);
				macro.setPort(port);
				if (yesRadio.isSelected()) {
					macro.setReplacing(true);
				} else {
					macro.setReplacing(false);			
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean cancel() {
		// Should Always return true.
		return true;
	}
	
	public boolean delete() {
		// Should Always return true.
		return true;
	}
	
	private boolean validateExistence(String name) {
		if (name.equals("")) {
			return false;
		} else {
			return true;
		}	
	}

	private boolean validateUniqueness(String name) {
		if (Outliner.macroPopup.isNameUnique(name)) {
			return true;
		} else {
			return false;
		}
	}
}
