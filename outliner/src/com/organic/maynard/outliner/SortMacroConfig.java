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

public class SortMacroConfig extends MacroConfigImpl {
	
	private static String COMPARATOR = null;
	private JLabel nameLabel = null;
	private JLabel comparatorLabel = null;
	private JTextField nameField = new JTextField();
	private JTextArea comparatorTextArea = new JTextArea();

	// The Constructor
	public SortMacroConfig() {
		super();

		COMPARATOR = GUITreeLoader.reg.getText("comparator");
		nameLabel = new JLabel(NAME);
		comparatorLabel = new JLabel(COMPARATOR);
		
		// Create the layout
		this.setLayout(new BorderLayout());
		
		Insets insets = new Insets(1,3,1,3);

		Box mainBox = Box.createVerticalBox();
		mainBox.add(nameLabel);
		nameField.setMargin(insets);
		mainBox.add(nameField);
		
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(comparatorLabel);

		// Prep the textarea
		comparatorTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		comparatorTextArea.setTabSize(2);
		comparatorTextArea.setMargin(insets);
		
		JScrollPane comparatorScrollPane = new JScrollPane(comparatorTextArea);
		
		// Put it all together
		this.add(mainBox,BorderLayout.NORTH);
		this.add(comparatorScrollPane,BorderLayout.CENTER);
	}

	
	// MacroConfig Interface
	public void init(Macro sortMacro) {
		super.init(sortMacro);
		
		SortMacro macro = (SortMacro) getMacro();

		comparatorTextArea.setText(macro.getComparator());
		nameField.setText(macro.getName());
	}
	
	public boolean create() {
		SortMacro macro = (SortMacro) getMacro();

		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name) && MacroPopupMenu.validateUniqueness(name)) {
			macro.setName(name);
			macro.setComparator(comparatorTextArea.getText());
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update() {
		SortMacro macro = (SortMacro) getMacro();

		String name = nameField.getText();

		if (MacroPopupMenu.validateExistence(name)) {
			if (name.equals(macro.getName())) {
				macro.setComparator(comparatorTextArea.getText());
				return true;
			} else if (MacroPopupMenu.validateUniqueness(name)) {
				macro.setName(name);
				macro.setComparator(comparatorTextArea.getText());
				return true;
			}
		}
		return false;
	}
}
