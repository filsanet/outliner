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
import org.xml.sax.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class MacroEditor extends AbstractGUITreeJDialog implements ActionListener, JoeReturnCodes {

	// Constants
	private static final int INITIAL_WIDTH = 350;
	private static final int INITIAL_HEIGHT = 300;
 	private static final int MINIMUM_WIDTH = 350;
	private static final int MINIMUM_HEIGHT = 300;
	
	private static final String CREATE = "Create";
	private static final String UPDATE = "Update";
	private static final String CANCEL = "Cancel";
	private static final String DELETE = "Delete";
	
	private Box createButtonBox = Box.createHorizontalBox();
	private Box updateButtonBox = Box.createHorizontalBox();

	private JLabel macroTypeName = new JLabel();
	
	private JButton createButton = new JButton(CREATE);
	private JButton updateButton = new JButton(UPDATE);
	private JButton deleteButton = new JButton(DELETE);
	private JButton cancelCreateButton = new JButton(CANCEL);
	private JButton cancelUpdateButton = new JButton(CANCEL);

	private MacroConfig macroConfig = null;
	private MacroManagerFrame frame = null;
	
	// Button Mode
	private static final String BUTTON_MODE_CREATE_TITLE = "New Macro";
	private static final String BUTTON_MODE_UPDATE_TITLE = "Update Macro";

	public static final int BUTTON_MODE_CREATE = 0;
	public static final int BUTTON_MODE_UPDATE = 1;

	
	// The Constructor
	public MacroEditor() {
		super(false, false, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}


	// GUITreeComponentInterface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		frame = Outliner.macroManager;
		frame.macroEditor = this;

		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					MacroEditor me = (MacroEditor) e.getWindow();
					me.cancel();
				}
			}
		);
		
		// Create the layout
		this.getContentPane().setLayout(new BorderLayout());
		
		createButton.addActionListener(this);
		updateButton.addActionListener(this);
		deleteButton.addActionListener(this);
		cancelCreateButton.addActionListener(this);
		cancelUpdateButton.addActionListener(this);

		createButtonBox.add(createButton);
		createButtonBox.add(Box.createHorizontalStrut(5));
		createButtonBox.add(cancelCreateButton);

		updateButtonBox.add(updateButton);
		updateButtonBox.add(Box.createHorizontalStrut(5));
		updateButtonBox.add(deleteButton);
		updateButtonBox.add(Box.createHorizontalStrut(5));
		updateButtonBox.add(cancelUpdateButton);

		// Put it all together
		this.getContentPane().add(macroTypeName,BorderLayout.NORTH);
	}


	public void setMacroConfigAndShow(MacroConfig macroConfig, int buttonMode) {
		// Swap in the new MacroConfig Panel
		if (this.macroConfig != null) {
			this.remove((Component) this.macroConfig);
		}
		this.getContentPane().add((Component) macroConfig,BorderLayout.CENTER);

		this.macroConfig = macroConfig;

		if (buttonMode == BUTTON_MODE_CREATE) {
			this.remove(updateButtonBox);
			this.getContentPane().add(createButtonBox,BorderLayout.SOUTH);
			setTitle(BUTTON_MODE_CREATE_TITLE);
		} else if (buttonMode == BUTTON_MODE_UPDATE) {
			this.remove(createButtonBox);
			this.getContentPane().add(updateButtonBox,BorderLayout.SOUTH);
			setTitle(BUTTON_MODE_UPDATE_TITLE);
		}
		
		// Update the macroTypeName text with the name of the class of the macroConfig.
		this.macroTypeName.setText("Macro Type: " + Outliner.macroManager.getMacroTypeNameFromClassName(macroConfig.getMacro().getClass().getName()));

		show();
	}
	

	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CREATE)) {
			create();
		} else if (e.getActionCommand().equals(UPDATE)) {
			update();
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		} else if (e.getActionCommand().equals(DELETE)) {
			delete();
		}
	}
	
	private void create() {
		if (macroConfig.create()) {
			Macro macro = macroConfig.getMacro();
			
			// Add it to the Popup Menu
			int i = Outliner.macroPopup.addMacro(macro);
			
			// Add it to the list in the MacroManager
			((DefaultListModel) frame.macroList.getModel()).insertElementAt(macro.getName(),i);
			
			// Save it to disk as a serialized object.
			saveMacro(macro);
		} else {
			JOptionPane.showMessageDialog(this,"An Error Occurred.");
		}
		
		hide();
	}
	
	private void update() {
		if (macroConfig.update()) {
			Macro macro = macroConfig.getMacro();
			String oldName = macro.getFileName();

			// Update the popup menu.
			int oldIndex = Outliner.macroPopup.removeMacro(macro);
			int newIndex = Outliner.macroPopup.addMacro(macro);
			
			// Update the list
			DefaultListModel model = (DefaultListModel) frame.macroList.getModel();
			model.remove(oldIndex);
			model.insertElementAt(macro.getName(), newIndex);
			
			// Save it to disk as a serialized object.
			deleteMacro(new File(Outliner.MACROS_DIR + oldName));
			saveMacro(macro);
			
		} else {
			JOptionPane.showMessageDialog(this,"An Error Occurred.");
		}
		
		hide();
	}

	private void delete() {
		if (USER_ABORTED == promptUser("Do you really want to delete this macro?")) {
			return;
		}

		if (macroConfig.delete()) {
			Macro macro = macroConfig.getMacro();
			
			// Remove it from the Popup Menu
			int index = Outliner.macroPopup.removeMacro(macro);
			
			// Remove it from the list in the MacroManager
			((DefaultListModel) frame.macroList.getModel()).remove(index);
			
			// Remove it from disk
			deleteMacro(new File(Outliner.MACROS_DIR + macro.getFileName()));
		} else {
			JOptionPane.showMessageDialog(this,"An Error Occurred.");
		}
		
		hide();
	}
	
	private void cancel() {
		if (!macroConfig.cancel()) {
			JOptionPane.showMessageDialog(this,"Uh Oh, something has gone wrong.");
		}
		
		hide();
	}


	// Macro Saving and Loading Methods
	private void deleteMacro(File file) {
		file.delete();
		LoadMacroCommand.saveConfigFile(new File(Outliner.MACROS_FILE));
	}
		
	private void saveMacro(Macro macro) {
		macro.save(new File(Outliner.MACROS_DIR + macro.getFileName()));
		LoadMacroCommand.saveConfigFile(new File(Outliner.MACROS_FILE));
	}


	// Utility Methods
	private static int promptUser(String msg) {
		Object[] options = {"Yes","No"};
		int result = JOptionPane.showOptionDialog(Outliner.macroManager.macroEditor,
			msg,
			"Confirm Delete",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]
		);
		
		if (result == JOptionPane.NO_OPTION) {
			return USER_ABORTED;
		} else {
			return SUCCESS;
		}
	}
}
