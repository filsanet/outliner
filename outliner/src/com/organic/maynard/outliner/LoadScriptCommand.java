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

import com.organic.maynard.util.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class LoadScriptCommand extends Command {
	
	// Constants
	private static final String EXTENSION_SEPARATOR = ".";
	
	
	// The Constructors
	public LoadScriptCommand(String name, int numOfArgs) {
		super(name,numOfArgs);
	}


	public synchronized void execute(Vector signature) {
		String path = (String) signature.elementAt(1);
		String className = (String) signature.elementAt(2);
		
		boolean isStartupScript = false;
		try {
			isStartupScript = (new Boolean((String) signature.elementAt(3))).booleanValue();
		} catch (ArrayIndexOutOfBoundsException e) {
			isStartupScript = false;
		}

		boolean isShutdownScript = false;
		try {
			isShutdownScript = (new Boolean((String) signature.elementAt(4))).booleanValue();
		} catch (ArrayIndexOutOfBoundsException e) {
			isShutdownScript = false;
		}
		
		try {
			// Turn path into a File
			File file = new File(Outliner.SCRIPTS_DIR + path);

			// Create Instance
			Script obj = (Script) Class.forName(className).newInstance();
			obj.setStartupScript(isStartupScript);
			obj.setShutdownScript(isShutdownScript);
			
			// Initialize it
			int end = path.lastIndexOf(EXTENSION_SEPARATOR);
			if (end == -1) {end = path.length();}
			obj.setName(path.substring(0, end));
			boolean success = obj.init(file);
			
			if (!success) {
				return;
			}

			// Add it to the Model
			if (ScriptsManagerModel.validateUniqueness(obj.getName()) && MacroPopupMenu.validateRestrictedChars(obj.getName())) {
				System.out.println("  " + path);
				int i = Outliner.scriptsManager.model.add(obj);			
			} else {
				System.out.println("  WARNING: duplicate script entry: " + path);			
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// Config File
	public static void saveConfigFile(File file) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(prepareConfigFile());
			fw.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, GUITreeLoader.reg.getText("message_could_not_save_scripts_config") + ": " + e);
		}
	}
	
	// Need to fix. [md] I don't see what's wrong anymore?
	private static String prepareConfigFile() {
		StringBuffer buffer = new StringBuffer();
		ScriptsManagerModel model = Outliner.scriptsManager.model;
		
		for (int i = 0; i < model.getSize(); i++) {
			Script script = (Script) model.get(i);
			
			buffer.append(Outliner.COMMAND_SCRIPT);
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(script.getFileName());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(script.getClass().getName());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(script.isStartupScript());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(script.isShutdownScript());
			buffer.append(System.getProperty("line.separator"));
		}
		
		return buffer.toString();
	}
}