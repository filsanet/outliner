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
import javax.swing.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */
 
public class HTMLEscapeMacroConfig extends MacroConfigImpl {
	
	private JLabel nameLabel = null;
	private JTextField nameField = new JTextField();
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton escapeRadio = null;
	private JRadioButton unescapeRadio = null;


	// The Constructor
	public HTMLEscapeMacroConfig() {
		super();

		nameLabel = new JLabel(NAME);
		escapeRadio = new JRadioButton(GUITreeLoader.reg.getText("escape"));
		unescapeRadio = new JRadioButton(GUITreeLoader.reg.getText("unescape"));
		
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
	public void init(Macro htmlEscapeMacro) {
		super.init(htmlEscapeMacro);
		
		HTMLEscapeMacro macro = (HTMLEscapeMacro) getMacro();

		if (macro.isEscaping()) {
			escapeRadio.setSelected(true);
		} else {
			unescapeRadio.setSelected(true);
		}
		nameField.setText(macro.getName());
	}
	
	public boolean create() {
		HTMLEscapeMacro macro = (HTMLEscapeMacro) getMacro();

		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name) && MacroPopupMenu.validateUniqueness(name) && MacroPopupMenu.validateRestrictedChars(name)) {
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
		HTMLEscapeMacro macro = (HTMLEscapeMacro) getMacro();

		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name)) {
			if (name.equals(macro.getName())) {
				if (escapeRadio.isSelected()) {
					macro.setEscaping(true);
				} else {
					macro.setEscaping(false);			
				}
				
				return true;
			} else if (MacroPopupMenu.validateUniqueness(name) && MacroPopupMenu.validateRestrictedChars(name)) {
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
}
