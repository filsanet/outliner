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

public class LoadMacroCommand extends Command {
	// Constants
	private static final String EXTENSION_SEPARATOR = ".";
	
	// The Constructors
	public LoadMacroCommand(String name, int numOfArgs) {
		super(name,numOfArgs);
	}

	public synchronized void execute(Vector signature) {
		String path = (String) signature.elementAt(1);
		String className = (String) signature.elementAt(2);
		try {
			// Turn path into a File
			File file = new File(Outliner.MACROS_DIR + path);

			// Create Instance
			System.out.println("  " + path);
			Macro obj = (Macro) Class.forName(className).newInstance();
			
			// Initialize it
			int end = path.lastIndexOf(EXTENSION_SEPARATOR);
			if (end == -1) {end = path.length();}
			obj.setName(path.substring(0, end));
			boolean success = obj.init(file);
			
			if (!success) {
				return;
			}
			
			// Add it to the MacroPopupMenu
			int i = Outliner.macroPopup.addMacro(obj);
			
			// Add it to the list in the MacroManager
			((DefaultListModel) Outliner.macroManager.macroList.getModel()).insertElementAt(obj.getName(),i);
			
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
			JOptionPane.showMessageDialog(null, "Could not save macros config file because of: " + e);
		}
	}
	
	// Need to fix
	private static String prepareConfigFile() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < MacroPopupMenu.macros.size(); i++) {
			Macro macro = (Macro) MacroPopupMenu.macros.get(i);
			
			buffer.append(Outliner.COMMAND_MACRO);
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(macro.getFileName());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(macro.getClass().getName());
			buffer.append(System.getProperty("line.separator"));
		}
		return buffer.toString();
	}
}