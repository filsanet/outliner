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

public class URLEncodeMacroConfig extends MacroConfigImpl {
		
	private JLabel nameLabel = null;
	private JTextField nameField = new JTextField();
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton encodeRadio = null;
	private JRadioButton decodeRadio = null;
	

	// The Constructor
	public URLEncodeMacroConfig() {
		super();

		nameLabel = new JLabel(NAME);
		encodeRadio = new JRadioButton(GUITreeLoader.reg.getText("encode"));
		decodeRadio = new JRadioButton(GUITreeLoader.reg.getText("decode"));
		
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
	public void init(Macro urlEncodeMacro) {
		super.init(urlEncodeMacro);
		
		URLEncodeMacro macro = (URLEncodeMacro) getMacro();

		if (macro.isEncoding()) {
			encodeRadio.setSelected(true);
		} else {
			decodeRadio.setSelected(true);
		}
		nameField.setText(macro.getName());
	}
	
	public boolean create() {
		URLEncodeMacro macro = (URLEncodeMacro) getMacro();

		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name) && MacroPopupMenu.validateUniqueness(name)) {
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
		URLEncodeMacro macro = (URLEncodeMacro) getMacro();

		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name)) {
			if (name.equals(macro.getName())) {
				if (encodeRadio.isSelected()) {
					macro.setEncoding(true);
				} else {
					macro.setEncoding(false);			
				}
				
				return true;
			} else if (MacroPopupMenu.validateUniqueness(name)) {
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
}
