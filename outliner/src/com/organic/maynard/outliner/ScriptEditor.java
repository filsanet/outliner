/**
 * Copyright (C) 2001 Maynard Demmon, maynard@organic.com
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

public class ScriptEditor extends AbstractGUITreeJDialog implements ActionListener, JoeReturnCodes {

	// Constants
	private static final int INITIAL_WIDTH = 350;
	private static final int INITIAL_HEIGHT = 300;
 	private static final int MINIMUM_WIDTH = 350;
	private static final int MINIMUM_HEIGHT = 300;
	
	private static String CREATE = null;
	private static String UPDATE = null;
	private static String CANCEL = null;
	private static String DELETE = null;
	
	private static String SCRIPT_TYPE = null;
	
	private Box createButtonBox = Box.createHorizontalBox();
	private Box updateButtonBox = Box.createHorizontalBox();

	private JLabel scriptTypeName = new JLabel();
	
	private JButton createButton = null;
	private JButton updateButton = null;
	private JButton deleteButton = null;
	private JButton cancelCreateButton = null;
	private JButton cancelUpdateButton = null;

	private ScriptConfig scriptConfig = null;
	private ScriptsManager frame = null;
	
	// Button Mode
	private static String BUTTON_MODE_CREATE_TITLE = null;
	private static String BUTTON_MODE_UPDATE_TITLE = null;

	public static final int BUTTON_MODE_CREATE = 0;
	public static final int BUTTON_MODE_UPDATE = 1;

	
	// The Constructor
	public ScriptEditor() {
		super(false, false, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);

		BUTTON_MODE_CREATE_TITLE = GUITreeLoader.reg.getText("new_script");
		BUTTON_MODE_UPDATE_TITLE = GUITreeLoader.reg.getText("update_script");

		CREATE = GUITreeLoader.reg.getText("create");
		UPDATE = GUITreeLoader.reg.getText("update");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		DELETE = GUITreeLoader.reg.getText("delete");

		SCRIPT_TYPE = GUITreeLoader.reg.getText("script_type");

		createButton = new JButton(CREATE);
		updateButton = new JButton(UPDATE);
		deleteButton = new JButton(DELETE);
		cancelCreateButton = new JButton(CANCEL);
		cancelUpdateButton = new JButton(CANCEL);

	}


	// GUITreeComponentInterface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		frame = Outliner.scriptsManager;
		frame.scriptEditor = this;

		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					ScriptEditor se = (ScriptEditor) e.getWindow();
					se.cancel();
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
		this.getContentPane().add(scriptTypeName,BorderLayout.NORTH);
	}


	public void setScriptConfigAndShow(ScriptConfig scriptConfig, int buttonMode) {
		// Swap in the new ScriptConfig Panel
		if (this.scriptConfig != null) {
			this.remove((Component) this.scriptConfig);
		}
		this.getContentPane().add((Component) scriptConfig,BorderLayout.CENTER);

		this.scriptConfig = scriptConfig;

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
		this.scriptTypeName.setText(SCRIPT_TYPE + ": " + Outliner.scriptsManager.getScriptTypeNameFromClassName(scriptConfig.getScript().getClass().getName()));

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
		if (scriptConfig.create()) {
			Script script = scriptConfig.getScript();
			
			// Add it to the model
			int i = Outliner.scriptsManager.model.add(script);
			
			// Save it to disk as a serialized object.
			saveScript(script);
		
			hide();
		} else {
			JOptionPane.showMessageDialog(this, GUITreeLoader.reg.getText("message_an_error_occurred"));
		}

	}
	
	private void update() {
		Script script = scriptConfig.getScript();
		String oldName = script.getFileName();

		if (scriptConfig.update()) {

			// Update the popup menu.
			int oldIndex = Outliner.scriptsManager.model.remove(script.getName());
			int newIndex = Outliner.scriptsManager.model.add(script);
			
			// Save it to disk as a serialized object.
			deleteScript(new File(Outliner.SCRIPTS_DIR + oldName));
			saveScript(script);
		
			hide();			
		} else {
			JOptionPane.showMessageDialog(this, GUITreeLoader.reg.getText("message_an_error_occurred"));
		}
	}

	private void delete() {
		if (USER_ABORTED == promptUser(GUITreeLoader.reg.getText("message_do_you_really_want_to_delete_this_script"))) {
			return;
		}

		if (scriptConfig.delete()) {
			Script script = scriptConfig.getScript();
			
			// Remove it from the Popup Menu
			int index = Outliner.scriptsManager.model.remove(script.getName());
			
			// Remove it from disk
			deleteScript(new File(Outliner.SCRIPTS_DIR + script.getFileName()));
		
			hide();
		} else {
			JOptionPane.showMessageDialog(this, GUITreeLoader.reg.getText("message_an_error_occurred"));
		}
	}
	
	private void cancel() {
		if (!scriptConfig.cancel()) {
			JOptionPane.showMessageDialog(this, GUITreeLoader.reg.getText("message_uh_oh"));
		}
		
		hide();
	}


	// Script Saving and Loading Methods
	private void deleteScript(File file) {
		file.delete();
		LoadScriptCommand.saveConfigFile(new File(Outliner.SCRIPTS_FILE));
	}
		
	private void saveScript(Script script) {
		script.save(new File(Outliner.SCRIPTS_DIR + script.getFileName()));
		LoadScriptCommand.saveConfigFile(new File(Outliner.SCRIPTS_FILE));
	}


	// Utility Methods
	private static int promptUser(String msg) {
		String yes = GUITreeLoader.reg.getText("yes");
		String no = GUITreeLoader.reg.getText("no");
		String confirm_delete = GUITreeLoader.reg.getText("confirm_delete");

		Object[] options = {yes, no};
		int result = JOptionPane.showOptionDialog(Outliner.macroManager.macroEditor,
			msg,
			confirm_delete,
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
