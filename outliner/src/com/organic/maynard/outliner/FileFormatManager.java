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
	public static final String FORMAT_TYPE_OPEN_DEFAULT = "open_default";
	public static final String FORMAT_TYPE_SAVE_DEFAULT = "save_default";


	private Vector openers = new Vector();
	private Vector openerNames = new Vector();
	
	private Vector savers = new Vector();
	private Vector saverNames = new Vector();
	
	private OpenFileFormat defaultOpenFileFormat = null;
	private SaveFileFormat defaultSaveFileFormat = null;
	
	// The Constructor
	public FileFormatManager() {}

	public void createFileFormat(String formatType, String formatName, String className, Vector extensions) {
		try {
			Class theClass = Class.forName(className);
			if (formatType.equals(FORMAT_TYPE_OPEN)) {
				OpenFileFormat openFileFormat = (OpenFileFormat) theClass.newInstance();
				setExtensions(openFileFormat, extensions);
				boolean success = addOpenFormat(formatName, openFileFormat);
				if (success) {
					System.out.println("\tOpen: " + className + " -> " + formatName);
				} else {
					System.out.println("Duplicate File Format Name: " + formatName);
				}
			} else if (formatType.equals(FORMAT_TYPE_SAVE)) {
				SaveFileFormat saveFileFormat = (SaveFileFormat) theClass.newInstance();
				boolean success = addSaveFormat(formatName, saveFileFormat);
				if (success) {
					System.out.println("\tSave: " + className + " -> " + formatName);
				} else {
					System.out.println("Duplicate File Format Name: " + formatName);
				}
			} else if (formatType.equals(FORMAT_TYPE_OPEN_DEFAULT)) {
				OpenFileFormat openFileFormat = (OpenFileFormat) theClass.newInstance();
				setExtensions(openFileFormat, extensions);
				setDefaultOpenFileFormat(openFileFormat);
				boolean success = addOpenFormat(formatName, openFileFormat);
				if (success) {
					System.out.println("\tOpen: " + className + " -> " + formatName);
				} else {
					System.out.println("Duplicate File Format Name: " + formatName);
				}
			} else if (formatType.equals(FORMAT_TYPE_SAVE_DEFAULT)) {
				SaveFileFormat saveFileFormat = (SaveFileFormat) theClass.newInstance();
				setDefaultSaveFileFormat(saveFileFormat);
				boolean success = addSaveFormat(formatName, saveFileFormat);
				if (success) {
					System.out.println("\tSave: " + className + " -> " + formatName);
				} else {
					System.out.println("Duplicate File Format Name: " + formatName);
				}		
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setExtensions(OpenFileFormat format, Vector extensions) {
		if (extensions == null) {
			return;
		}
		
		for (int i = 0; i < extensions.size(); i++) {
			String ext = ((String) extensions.get(i)).toLowerCase();
			if (i == 0) {
				format.addExtension(ext, true);
			} else {
				format.addExtension(ext, false);
			}
		}
	}

	// Open Accessors
	public String getOpenFileFormatNameForExtension(String extension) {
		for (int i = 0; i < openers.size(); i++) {
			OpenFileFormat format = (OpenFileFormat) openers.get(i);
			if (format.extensionExists(extension.toLowerCase())) {
				return (String) openerNames.get(i);
			}
		}
		
		int index = openers.indexOf(getDefaultOpenFileFormat());
		return (String) openerNames.get(index);
	}
	
	public OpenFileFormat getDefaultOpenFileFormat() {
		return defaultOpenFileFormat;
	}

	public void setDefaultOpenFileFormat(OpenFileFormat defaultOpenFileFormat) {
		this.defaultOpenFileFormat = defaultOpenFileFormat;
	}

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
	public SaveFileFormat getDefaultSaveFileFormat() {
		return defaultSaveFileFormat;
	}
	
	public void setDefaultSaveFileFormat(SaveFileFormat defaultSaveFileFormat) {
		this.defaultSaveFileFormat = defaultSaveFileFormat;
	}

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

	// File Opening/Saving Methods
	public static String loadFile(String filename, String encoding) {
		// This could be optimized to return a string array or List/Vector instead.
		StringBuffer text = new StringBuffer("");
		try {
			FileInputStream fileInputStream = new FileInputStream(filename);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,encoding);
			BufferedReader buffer = new BufferedReader(inputStreamReader);
			
			boolean eof = false;
			while (!eof) {
				String theLine = buffer.readLine();
				if (theLine == null) {
					eof = true;
				} else {
					text.append(theLine + Preferences.LINE_END_UNIX);
				}
			}
			
			fileInputStream.close();
		} catch (FileNotFoundException fnfe) {
			System.out.println("File not found.");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return text.toString();
	}
	
	public static boolean writeFile(String filename, byte[] bytes) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filename);
			//OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, encoding);
			
			fileOutputStream.write(bytes);
			fileOutputStream.flush();
			fileOutputStream.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}