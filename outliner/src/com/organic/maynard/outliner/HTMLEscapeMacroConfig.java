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

public class HTMLEscapeMacroConfig extends JPanel implements MacroConfig {
	
	public static final String NAME = "Macro Name";
	
	protected JLabel nameLabel = new JLabel(NAME);

	protected JTextField nameField = new JTextField();
	
	protected ButtonGroup buttonGroup = new ButtonGroup();
	protected JRadioButton escapeRadio = new JRadioButton("Escape");
	protected JRadioButton unescapeRadio = new JRadioButton("Un-Escape");

	// The Constructor
	public HTMLEscapeMacroConfig() {
		
		// Create the layout
		this.setLayout(new BorderLayout());

		buttonGroup.add(escapeRadio);
		buttonGroup.add(unescapeRadio);
		
		Box radioBox = Box.createHorizontalBox();
		radioBox.add(escapeRadio);
		radioBox.add(Box.createHorizontalStrut(5));
		radioBox.add(unescapeRadio);
		
		Box mainBox = Box.createVerticalBox();
		mainBox.add(nameLabel);
		nameField.setMargin(new Insets(1,3,1,3));
		mainBox.add(nameField);
		mainBox.add(Box.createVerticalStrut(10));
		
		// Put it all together
		this.add(mainBox,BorderLayout.NORTH);
		this.add(radioBox,BorderLayout.CENTER);
	}

	
	// MacroConfig Interface
	protected HTMLEscapeMacro macro = null;
	
	public void init(Macro macro) {
		this.macro = (HTMLEscapeMacro) macro;
		if (this.macro.isEscaping()) {
			escapeRadio.setSelected(true);
		} else {
			unescapeRadio.setSelected(true);
		}
		nameField.setText(macro.getName());
	}

	public Macro getMacro() {return this.macro;}
	
	public boolean create() {
		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name) && MacroPopupMenu.validateUniqueness(name)) {
			macro.setName(name);
			if (escapeRadio.isSelected()) {
				macro.setEscaping(true);
			} else {
				macro.setEscaping(false);			
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update() {
		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name)) {
			if (name.equals(macro.getName())) {
				if (escapeRadio.isSelected()) {
					macro.setEscaping(true);
				} else {
					macro.setEscaping(false);			
				}
				
				return true;
			} else if (MacroPopupMenu.validateUniqueness(name)) {
				macro.setName(name);
				if (escapeRadio.isSelected()) {
					macro.setEscaping(true);
				} else {
					macro.setEscaping(false);			
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
