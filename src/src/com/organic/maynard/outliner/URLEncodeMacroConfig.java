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

public class URLEncodeMacroConfig extends JPanel implements MacroConfig {
	
	public static final String NAME = "Macro Name";
	
	protected JLabel nameLabel = new JLabel(NAME);

	protected JTextField nameField = new JTextField();
	
	protected ButtonGroup buttonGroup = new ButtonGroup();
	protected JRadioButton encodeRadio = new JRadioButton("Encode");
	protected JRadioButton decodeRadio = new JRadioButton("Decode");

	// The Constructor
	public URLEncodeMacroConfig() {
		
		// Create the layout
		this.setLayout(new BorderLayout());

		buttonGroup.add(encodeRadio);
		buttonGroup.add(decodeRadio);
		
		Box radioBox = Box.createHorizontalBox();
		radioBox.add(encodeRadio);
		radioBox.add(Box.createHorizontalStrut(5));
		radioBox.add(decodeRadio);
		
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
	protected URLEncodeMacro macro = null;
	
	public void init(Macro macro) {
		this.macro = (URLEncodeMacro) macro;
		if (this.macro.isEncoding()) {
			encodeRadio.setSelected(true);
		} else {
			decodeRadio.setSelected(true);
		}
		nameField.setText(macro.getName());
	}

	public Macro getMacro() {return this.macro;}
	
	public boolean create() {
		String name = nameField.getText();

		if (validateExistence(name) && validateUniqueness(name)) {
			macro.setName(name);
			if (encodeRadio.isSelected()) {
				macro.setEncoding(true);
			} else {
				macro.setEncoding(false);			
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update() {
		String name = nameField.getText();

		if (validateExistence(name)) {
			if (name.equals(macro.getName())) {
				if (encodeRadio.isSelected()) {
					macro.setEncoding(true);
				} else {
					macro.setEncoding(false);			
				}
				
				return true;
			} else if (validateUniqueness(name)) {
				macro.setName(name);
				if (encodeRadio.isSelected()) {
					macro.setEncoding(true);
				} else {
					macro.setEncoding(false);			
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
