/**
 * FileFormatManager class		[srk] added this header info	
 * 
 * Manages file formats
 * 
 * members
 *	constants
 *		class
 *			public
 *				String FORMAT_TYPE_OPEN
 *				String FORMAT_TYPE_SAVE
 *				String FORMAT_TYPE_OPEN_DEFAULT
 *				String FORMAT_TYPE_SAVE_DEFAULT
 *	variables
 *		instance
 *			private
 *				Vector openers
 *				Vector openerNames
 *				Vector savers
 *				Vector saverNames
 *				OpenFileFormat defaultOpenFileFormat
 *				SaveFileFormat defaultSaveFileFormat
 * 				void setExtensions(FileFormat, Vector) {
 *	methods
 * 		instance
 * 			public
 * 				FileFormatManager ()  // constructor
 * 				void createFileFormat(String, String, String, Vector) 
 * 				
 * 				String getOpenFileFormatNameForExtension(String)
 * 				OpenFileFormat getDefaultOpenFileFormat()
 * 				void setDefaultOpenFileFormat(OpenFileFormat) 
 * 				boolean addOpenFormat(String, OpenFileFormat) 
 * 				OpenFileFormat getOpenFormat(String formatName) 
 * 				boolean removeOpenFormat(String formatName)
 * 				
 * 				String getSaveFileFormatNameForExtension(String)
 * 				SaveFileFormat getDefaultSaveFileFormat()
 * 				void setDefaultSaveFileFormat(SaveFileFormat) 
 * 				boolean addSaveFormat(String, SaveFileFormat) 
 * 				SaveFileFormat getSaveFormat(String) 
 * 				boolean removeSaveFormat(String) 
 * 		class
 *			public
 * 				boolean isNameUnique(String, Vector)
 * 				int indexOfName(String, Vector) 
 * 				String loadFile(String filename, String encoding) 
 * 				boolean writeFile(String, byte[]) 
 * 				
 * Portions copyright (C) 2001 Maynard Demmon <maynard@organic.com>
 * Portions copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 *
 * Most recent changes: 8/27/01 9:15AM
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
 
// we're a part of this 
package com.organic.maynard.outliner;

// we use these 
import java.io.*;
import java.util.*;

// the class
public class FileFormatManager {
	
	// public class constants	[srk]
	public static final String FORMAT_TYPE_OPEN = "open";
	public static final String FORMAT_TYPE_SAVE = "save";
	public static final String FORMAT_TYPE_EXPORT = "export";
	public static final String FORMAT_TYPE_IMPORT = "import";
	public static final String FORMAT_TYPE_OPEN_DEFAULT = "open_default";
	public static final String FORMAT_TYPE_SAVE_DEFAULT = "save_default";

	// private instance variables	[srk]
	private Vector openers = new Vector();
	private Vector openerNames = new Vector();
	
	private Vector savers = new Vector();
	private Vector saverNames = new Vector();

	private Vector exporters = new Vector();
	private Vector exporterNames = new Vector();
	
	private OpenFileFormat defaultOpenFileFormat = null;
	private SaveFileFormat defaultSaveFileFormat = null;
	
	// public methods
	
	// The Constructor
	public FileFormatManager() {}

	// create a new file format object, of the OPEN or SAVE flavor	[srk]
	public void createFileFormat(String formatType, String formatName, String className, Vector extensions) {
		try {
			// obtain the class object for className		[srk]
			Class theClass = Class.forName(className);
			
			// if this is an Open format spec .... 	[srk]
			if (formatType.equals(FORMAT_TYPE_OPEN)) {
				
				// create an OpenFileFormat object to hold it
				OpenFileFormat openFileFormat = (OpenFileFormat) theClass.newInstance();
			
				// set filename extensions for the format	[srk]
				setExtensions(openFileFormat, extensions);
		
				// try to add this format to our set of Open formats	[srk]
				boolean success = addOpenFormat(formatName, openFileFormat);
				
				// supply some feedback	[srk]
				// if we succeed ...	[srk]
				if (success) {
					System.out.println("  Open: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					
					} // end else
				
				} // end if FORMAT_TYPE_OPEN
			
			// else if it's a Save format spec ....	[srk]
			else if (formatType.equals(FORMAT_TYPE_SAVE)) {
				
				// create a SaveFileFormat object to hold it
				SaveFileFormat saveFileFormat = (SaveFileFormat) theClass.newInstance();
				
				// set filename extensions for the Save format 	[srk]
				setExtensions(saveFileFormat, extensions) ;
				
				// try to add this format to our set of Save formats		[srk]
				boolean success = addSaveFormat(formatName, saveFileFormat);
				addExportFormat(formatName, saveFileFormat);
				
				// supply some feedback	[srk]
				// if we succeed ...	[srk]
				if (success) {
					System.out.println("  Save: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					} // end else
				
				} // end if FORMAT_TYPE_SAVE
			
			// else if it's a Export format spec ....
			else if (formatType.equals(FORMAT_TYPE_EXPORT)) {
				
				// create a SaveFileFormat object to hold it
				ExportFileFormat exportFileFormat = (ExportFileFormat) theClass.newInstance();
				
				// set filename extensions for the Save format 	[srk]
				setExtensions(exportFileFormat, extensions) ;
				
				// try to add this format to our set of Save formats		[srk]
				boolean success = addExportFormat(formatName, exportFileFormat);
				
				// supply some feedback	[srk]
				// if we succeed ...	[srk]
				if (success) {
					System.out.println("  Export: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					} // end else
				
				} // end if FORMAT_TYPE_EXPORT
			
			// else if it's a Default Open format type ... 
			else if (formatType.equals(FORMAT_TYPE_OPEN_DEFAULT)) {
			 	
			 	// create an OpenFileFormat object to hold it
				OpenFileFormat openFileFormat = (OpenFileFormat) theClass.newInstance();
				
				// set filename extensions for the format	[srk]
				setExtensions(openFileFormat, extensions);
				
				// set this format as our OPEN default		[srk]
				setDefaultOpenFileFormat(openFileFormat);
				
				// try to add this format to our set of Open formats	[srk]
				boolean success = addOpenFormat(formatName, openFileFormat);
				
				// supply some feedback	[srk]
				// if we succeed ...	[srk]
				if (success) {
					System.out.println("  Open: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					}  // end else
				
				} // end if FORMAT_TYPE_OPEN_DEFAULT
			
			// else if it's a Default Save format type ...
			else if (formatType.equals(FORMAT_TYPE_SAVE_DEFAULT)) {
				
				// create a SaveFileFormat object to hold it
				SaveFileFormat saveFileFormat = (SaveFileFormat) theClass.newInstance();
				
				// set filename extensions for the format	[srk]
				setExtensions(saveFileFormat, extensions);
				
				// set this format as our SAVE default		[srk]
				setDefaultSaveFileFormat(saveFileFormat);
				
				// try to add this format to our set of Save formats	[srk]
				boolean success = addSaveFormat(formatName, saveFileFormat);
				addExportFormat(formatName, saveFileFormat);
				
				// supply some feedback	[srk]
				// if we succeed ...	[srk]
				if (success) {
					System.out.println("  Save: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					} // end else	
				} // end if FORMAT_TYPE_SAVE _DEFAULT
				
			} // end try
			
		catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
			} // end catch
		
		catch (Exception e) {
			e.printStackTrace();
			} // end catch
		
		}  // end method CreateFileFormat
	
	// set a format's set of filename extensions
	private void setExtensions(FileFormat format, Vector extensions) {

		// check for a null set
		if (extensions == null) {
			return;
			} // end if
		
		// for each extension in the set ....
		for (int i = 0; i < extensions.size(); i++) {
			
			// force it to lowercase
			String ext = ((String) extensions.get(i)).toLowerCase();
			
			// the first extension in the set is the default
			if (i == 0) {
				format.addExtension(ext, true);
				} // end if
			else {
				format.addExtension(ext, false);
				} // end else
			
		} // end for
		
		} // end method setExtensions

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
	public String getSaveFileFormatNameForExtension(String extension) {
		for (int i = 0; i < savers.size(); i++) {
			SaveFileFormat format = (SaveFileFormat) savers.get(i);
			if (format.extensionExists(extension.toLowerCase())) {
				return (String) saverNames.get(i);
			}
		}
		
		int index = savers.indexOf(getDefaultSaveFileFormat());
		return (String) saverNames.get(index);
		
		} // end getSaveFileFormatNameForExtension
	
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


	public boolean addExportFormat(String formatName, SaveFileFormat format) {
		if (isNameUnique(formatName, exporterNames)) {
			exporterNames.add(formatName);
			exporters.add(format);
			
			// Also add it to the list of formats stored in the preferences
			Preferences.FILE_FORMATS_EXPORT.add(formatName);

			return true;
		}
		return false;
	}
	
	public SaveFileFormat getExportFormat(String formatName) {
		int index = indexOfName(formatName, exporterNames);
		if (index >= 0) {
			return (SaveFileFormat) exporters.elementAt(index);
		}
		return null;
	}
	
	public boolean removeExportFormat(String formatName) {
		int index = indexOfName(formatName, exporterNames);
		if (index >= 0) {
			exporterNames.removeElementAt(index);
			exporters.removeElementAt(index);

			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_EXPORT.removeElementAt(index);

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
					text.append(theLine + PlatformCompatibility.LINE_END_UNIX);
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