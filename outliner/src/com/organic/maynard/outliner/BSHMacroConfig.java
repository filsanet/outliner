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

public class BSHMacroConfig extends JPanel implements MacroConfig {
	
	public static final String NAME = "Macro Name";
	public static final String PATTERN = "BeanShell Code";
	
	protected JLabel nameLabel = new JLabel(NAME);
	protected JLabel patternLabel = new JLabel(PATTERN);

	protected JTextField nameField = new JTextField();
	protected JTextArea patternTextArea = new JTextArea();

	// The Constructor
	public BSHMacroConfig() {
		
		// Create the layout
		this.setLayout(new BorderLayout());

		Box mainBox = Box.createVerticalBox();
		mainBox.add(nameLabel);
		nameField.setMargin(new Insets(1,3,1,3));
		mainBox.add(nameField);
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(patternLabel);

		// Prep the textarea
		patternTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		patternTextArea.setTabSize(2);
		patternTextArea.setMargin(new Insets(1,3,1,3));
		
		JScrollPane patternScrollPane = new JScrollPane(patternTextArea);
		
		// Put it all together
		this.add(mainBox,BorderLayout.NORTH);
		this.add(patternScrollPane,BorderLayout.CENTER);
	}

	
	// MacroConfig Interface
	protected BSHMacro bshmacro = null;
	
	public void init(Macro macro) {
		this.bshmacro = (BSHMacro) macro;
		patternTextArea.setText(bshmacro.getScript());
		nameField.setText(bshmacro.getName());
	}

	public Macro getMacro() {return this.bshmacro;}
	
	public boolean create() {
		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name) && MacroPopupMenu.validateUniqueness(name)) {
			bshmacro.setName(name);
			bshmacro.setScript(patternTextArea.getText());
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update() {
		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name)) {
			if (name.equals(bshmacro.getName())) {
				bshmacro.setScript(patternTextArea.getText());
				return true;
			} else if (MacroPopupMenu.validateUniqueness(name)) {
				bshmacro.setName(name);
				bshmacro.setScript(patternTextArea.getText());
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
