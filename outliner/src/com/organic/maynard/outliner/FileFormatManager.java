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

import java.lang.reflect.*;

import java.io.*;
import java.util.*;

public class FileFormatManager {
	
	// Constants
	public static final String FORMAT_TYPE_OPEN = "open";
	public static final String FORMAT_TYPE_SAVE = "save";


	private Vector openers = new Vector();
	private Vector openerNames = new Vector();
	
	private Vector savers = new Vector();
	private Vector saverNames = new Vector();
	
	// The Constructor
	public FileFormatManager() {}

	public void createFileFormat(String formatType, String formatName, String className) {
		if (formatType.equals(FORMAT_TYPE_OPEN)) {
			try {
				Class theClass = Class.forName(className);
				OpenFileFormat openFileFormat = (OpenFileFormat) theClass.newInstance();
				boolean success = addOpenFormat(formatName, openFileFormat);
				if (success) {
					System.out.println("\tOpen: " + className + " -> " + formatName);
				} else {
					System.out.println("Duplicate File Format Name: " + formatName);
				}
			} catch (ClassNotFoundException cnfe) {
				System.out.println("Exception: " + className + " " + cnfe);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (formatType.equals(FORMAT_TYPE_SAVE)) {
			try {
				Class theClass = Class.forName(className);
				SaveFileFormat saveFileFormat = (SaveFileFormat) theClass.newInstance();
				boolean success = addSaveFormat(formatName, saveFileFormat);
				if (success) {
					System.out.println("\tSave: " + className + " -> " + formatName);
				} else {
					System.out.println("Duplicate File Format Name: " + formatName);
				}
			} catch (ClassNotFoundException cnfe) {
				System.out.println("Exception: " + className + " " + cnfe);
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
	}

	// Open Accessors
	public boolean addOpenFormat(String formatName, OpenFileFormat format) {
		if (isNameUnique(formatName, openerNames)) {
			openerNames.add(formatName);
			openers.add(format);
			
			// Also add it to the list of formats stored in the preferences
			Preferences.FILE_FORMATS_OPEN.add(formatName);
			
			return true;
		}
		return false;
	}
	
	public OpenFileFormat getOpenFormat(String formatName) {
		int index = indexOfName(formatName, openerNames);
		if (index >= 0) {
			return (OpenFileFormat) openers.elementAt(index);
		}
		return null;
	}
	
	public boolean removeOpenFormat(String formatName) {
		int index = indexOfName(formatName, openerNames);
		if (index >= 0) {
			openerNames.removeElementAt(index);
			openers.removeElementAt(index);
			
			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_OPEN.removeElementAt(index);

			return true;
		}
		return false;	
	}
	
	
	// Save Accessors
	public boolean addSaveFormat(String formatName, SaveFileFormat format) {
		if (isNameUnique(formatName, saverNames)) {
			saverNames.add(formatName);
			savers.add(format);
			
			// Also add it to the list of formats stored in the preferences
			Preferences.FILE_FORMATS_SAVE.add(formatName);

			return true;
		}
		return false;
	}
	
	public SaveFileFormat getSaveFormat(String formatName) {
		int index = indexOfName(formatName, saverNames);
		if (index >= 0) {
			return (SaveFileFormat) savers.elementAt(index);
		}
		return null;
	}
	
	public boolean removeSaveFormat(String formatName) {
		int index = indexOfName(formatName, saverNames);
		if (index >= 0) {
			saverNames.removeElementAt(index);
			savers.removeElementAt(index);

			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_SAVE.removeElementAt(index);

			return true;
		}
		return false;	
	}


	// Utility Methods
	public static boolean isNameUnique(String name, Vector list) {
		for (int i = 0; i < list.size(); i++) {
			if (name.equals(list.elementAt(i).toString())) {
				return false;
			}
		}
		return true;
	}
	
	public static int indexOfName(String name, Vector list) {
		for (int i = 0; i < list.size(); i++) {
			if (name.equals(list.elementAt(i).toString())) {
				return i;
			}
		}
		return -1;
	}
}