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
	
	private static final String NAME = "Macro Name";
	private static final String URL = "URL";
	private static final String DO_REPLACEMENT = "Do Replacement";
	private static final String CALL = "XML-RPC Call";
	
	private JLabel nameLabel = new JLabel(NAME);
	private JLabel urlLabel = new JLabel(URL);
	private JLabel doReplacementLabel = new JLabel(DO_REPLACEMENT);
	private JLabel callLabel = new JLabel(CALL);

	private JTextField nameField = new JTextField();
	private JTextField urlField = new JTextField();
	private JTextArea callTextArea = new JTextArea();

	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton yesRadio = new JRadioButton("Yes");
	private JRadioButton noRadio = new JRadioButton("No");

	// The Constructor
	public XMLRPCMacroConfig() {
		
		// Create the layout
		this.setLayout(new BorderLayout());
		
		Insets insets = new Insets(1,3,1,3);
		nameField.setMargin(insets);
		urlField.setMargin(insets);

		// Prep the textarea
		callTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		callTextArea.setTabSize(2);
		callTextArea.setMargin(insets);
		JScrollPane callScrollPane = new JScrollPane(callTextArea);

		// Setup mainBox
		Box mainBox = Box.createVerticalBox();
		
		mainBox.add(nameLabel);
		mainBox.add(nameField);
		
		mainBox.add(Box.createVerticalStrut(5));
		
		mainBox.add(urlLabel);
		mainBox.add(urlField);
		
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

		mainBox.add(Box.createVerticalStrut(5));

		mainBox.add(callLabel);
		
		// Put it all together
		this.add(mainBox,BorderLayout.NORTH);
		this.add(callScrollPane,BorderLayout.CENTER);
	}

	
	// MacroConfig Interface
	private XMLRPCMacro macro = null;
	
	public void init(Macro xmlrpcMacro) {
		this.macro = (XMLRPCMacro) xmlrpcMacro;
		nameField.setText(macro.getName());
		urlField.setText(macro.getURL());
		callTextArea.setText(macro.getCall());
		if (this.macro.isReplacing()) {
			yesRadio.setSelected(true);
		} else {
			noRadio.setSelected(true);
		}
	}

	public Macro getMacro() {return this.macro;}
	
	public boolean create() {
		String name = nameField.getText();
		String url = urlField.getText();
		
		// Validate URL
		if (url.equals("")) {
			return false;
		}
		
		// Validate Name and do the create
		if (MacroPopupMenu.validateExistence(name) && MacroPopupMenu.validateUniqueness(name)) {
			macro.setName(name);
			macro.setURL(url);
			macro.setCall(callTextArea.getText());
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
		String url = urlField.getText();
		
		// Validate URL
		if (url.equals("")) {
			return false;
		}
		
		// Validate Name and do the update
		if (MacroPopupMenu.validateExistence(name)) {
			if (name.equals(macro.getName())) {
				macro.setURL(url);
				macro.setCall(callTextArea.getText());
				if (yesRadio.isSelected()) {
					macro.setReplacing(true);
				} else {
					macro.setReplacing(false);			
				}
				return true;
			} else if (MacroPopupMenu.validateUniqueness(name)) {
				macro.setName(name);
				macro.setURL(url);
				macro.setCall(callTextArea.getText());
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
}
