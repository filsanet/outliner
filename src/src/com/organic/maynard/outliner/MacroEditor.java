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

public class MacroEditor extends JDialog implements ActionListener {
	
	public static final String CREATE = "Create";
	public static final String UPDATE = "Update";
	public static final String CANCEL = "Cancel";
	public static final String DELETE = "Delete";
	
	protected Box createButtonBox = Box.createHorizontalBox();
	protected Box updateButtonBox = Box.createHorizontalBox();

	protected JLabel macroTypeName = new JLabel();
	
	protected JScrollPane scrollPane = new JScrollPane();
	
	protected JButton createButton = new JButton(CREATE);
	protected JButton updateButton = new JButton(UPDATE);
	protected JButton deleteButton = new JButton(DELETE);
	protected JButton cancelCreateButton = new JButton(CANCEL);
	protected JButton cancelUpdateButton = new JButton(CANCEL);

	protected MacroConfig macroConfig = null;
	protected MacroManagerFrame frame = null;
	
	// Button Mode
	public static final String BUTTON_MODE_CREATE_TITLE = "New Macro";
	public static final String BUTTON_MODE_UPDATE_TITLE = "Update Macro";

	public static final int BUTTON_MODE_CREATE = 0;
	public static final int BUTTON_MODE_UPDATE = 1;
	
	private int buttonMode = BUTTON_MODE_CREATE;

	
	// The Constructor
	public MacroEditor(MacroManagerFrame frame) {
		super(frame,BUTTON_MODE_CREATE_TITLE,true);
		
		this.frame = frame;

		setSize(350,200);
		setLocationRelativeTo(frame);
		addComponentListener(new WindowSizeManager(350,250));

		WindowListener windowListener = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				MacroEditor me = (MacroEditor) e.getWindow();
				me.cancel();
			}
		};
		addWindowListener(windowListener);
		
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
		this.getContentPane().add(scrollPane,BorderLayout.CENTER);
		this.getContentPane().add(createButtonBox,BorderLayout.SOUTH);
	}

	public void setButtonMode(int buttonMode) {
		if (buttonMode == BUTTON_MODE_CREATE) {
			this.remove(updateButtonBox);
			this.getContentPane().add(createButtonBox,BorderLayout.SOUTH);
		} else if (buttonMode == BUTTON_MODE_UPDATE) {
			this.remove(createButtonBox);
			this.getContentPane().add(updateButtonBox,BorderLayout.SOUTH);	
		} else {
			return;
		}
		
		this.buttonMode = buttonMode;
	}
	
	public int getButtonMode() {return this.buttonMode;}
		
	public void setMacroConfig(MacroConfig macroConfig) {
		this.macroConfig = macroConfig;
		
		// Swap in the new MacroConfig Panel
		scrollPane.setViewportView((JComponent) macroConfig);
		
		// Update the macroTypeName text with the name of the class of the macroConfig.
		this.macroTypeName.setText("Macro Type: " + Outliner.macroManager.getMacroTypeNameFromClassName(macroConfig.getMacro().getClass().getName()));
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
		boolean success = macroConfig.create();
		
		if (success) {
			Macro macro = macroConfig.getMacro();
			// Add it to the Popup Menu
			int i = Outliner.macroPopup.addMacro(macro);
			
			// Add it to the list in the MacroManager
			((DefaultListModel) frame.macroList.getModel()).insertElementAt(macro.getName(),i);
			
			// Save it to disk as a serialized object.
			frame.saveMacro(macro);
			
			setVisible(false);
		} else {
			JOptionPane.showMessageDialog(this,"An Error Occurred.");
		}
	}
	
	private void update() {
		Macro macro = macroConfig.getMacro();
		String oldName = macro.getName();
		
		boolean success = macroConfig.update();
		
		if (success) {
			// Update the popup menu.
			int oldIndex = Outliner.macroPopup.removeMacro(macro);
			int newIndex = Outliner.macroPopup.addMacro(macro);
			
			// Update the list
			DefaultListModel model = (DefaultListModel) frame.macroList.getModel();
			model.remove(oldIndex);
			model.insertElementAt(macro.getName(),newIndex);
			
			// Save it to disk as a serialized object.
			frame.deleteMacro(oldName);
			frame.saveMacro(macro);
			
			setVisible(false);
		} else {
			JOptionPane.showMessageDialog(this,"An Error Occurred.");
		}
	}

	private void delete() {
		boolean success = macroConfig.delete();

		if (success) {
			Macro macro = macroConfig.getMacro();
			
			// Remove it from the Popup Menu
			int index = Outliner.macroPopup.removeMacro(macro);
			
			// Remove it from the list in the MacroManager
			DefaultListModel model = (DefaultListModel) frame.macroList.getModel();
			model.remove(index);
			
			// Remove it from disk
			frame.deleteMacro(macro.getName());
			
			setVisible(false);
		} else {
			JOptionPane.showMessageDialog(this,"An Error Occurred.");
		}
	}
	
	private void cancel() {
		boolean success = macroConfig.cancel();

		if (success) {
			setVisible(false);
		} else {
			JOptionPane.showMessageDialog(this,"Uh Oh, something has gone wrong.");
		}
	}
}
