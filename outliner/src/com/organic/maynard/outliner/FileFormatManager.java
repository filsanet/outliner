/**
 * FileFormatManager class
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
 *				 void setExtensions(FileFormat, Vector) {
 *	methods
 *			instance
 *				public
 *				 FileFormatManager ()  // constructor
 *				 void createFileFormat(String, String, String, Vector)
 *
 *				 String getOpenFileFormatNameForExtension(String)
 *				 OpenFileFormat getDefaultOpenFileFormat()
 *				void setDefaultOpenFileFormat(OpenFileFormat)
 *				boolean addOpenFormat(String, OpenFileFormat)
 *				OpenFileFormat getOpenFormat(String formatName)
 *				boolean removeOpenFormat(String formatName)
 *
 *				String getSaveFileFormatNameForExtension(String)
 *				SaveFileFormat getDefaultSaveFileFormat()
 *				void setDefaultSaveFileFormat(SaveFileFormat)
 *				boolean addSaveFormat(String, SaveFileFormat)
 *				SaveFileFormat getSaveFormat(String)
 *				boolean removeSaveFormat(String)
 *			class
 *			public
 *					boolean isNameUnique(String, Vector)
 *					int indexOfName(String, Vector)
 *					String loadFile(String filename, String encoding)
 *					boolean writeFile(String, byte[])
 *
 * Portions copyright (C) 2001 Maynard Demmon <maynard@organic.com>
 * Portions copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.outliner;

import com.organic.maynard.outliner.util.preferences.*;
import java.io.*;
import java.util.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */
 
public class FileFormatManager {

	// public class constants
	public static final String FORMAT_TYPE_OPEN = "open";
	public static final String FORMAT_TYPE_SAVE = "save";
	public static final String FORMAT_TYPE_IMPORT = "import";
	public static final String FORMAT_TYPE_EXPORT = "export";
	public static final String FORMAT_TYPE_OPEN_DEFAULT = "open_default";
	public static final String FORMAT_TYPE_SAVE_DEFAULT = "save_default";
	public static final String FORMAT_TYPE_IMPORT_DEFAULT = "import_default";
	public static final String FORMAT_TYPE_EXPORT_DEFAULT = "export_default";

	// private instance variables
	private Vector openers = new Vector();
	private Vector openerNames = new Vector();

	private Vector savers = new Vector();
	private Vector saverNames = new Vector();

	private Vector exporters = new Vector();
	private Vector exporterNames = new Vector();

	private Vector importers = new Vector();
	private Vector importerNames = new Vector();

	private OpenFileFormat defaultOpenFileFormat = null;
	private SaveFileFormat defaultSaveFileFormat = null;
	private OpenFileFormat defaultImportFileFormat = null;
	private SaveFileFormat defaultExportFileFormat = null;

	// public methods
	
	// The Constructor
	public FileFormatManager() {}

	// create a new file format object of a particular flavor
	public void createFileFormat(String formatType, String formatName, String className, Vector extensions) {
	
		try {
			// obtain the class object for className
			Class theClass = Class.forName(className);

			// if this is an Open format spec ....
			if (formatType.equals(FORMAT_TYPE_OPEN)) {

				// create an OpenFileFormat object to hold it
				OpenFileFormat openFileFormat = (OpenFileFormat) theClass.newInstance();

				// set filename extensions for the format
				setExtensions(openFileFormat, extensions);

				// try to add this format to our set of Open formats
				boolean success = addOpenFormat(formatName, openFileFormat);

				// supply some feedback
				// if we succeed ...
				if (success) {
					System.out.println("  Open: " + className + " -> " + formatName);
				} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);

				} // end else

			} // end if FORMAT_TYPE_OPEN

			// else if it's a Save format spec ....
			else if (formatType.equals(FORMAT_TYPE_SAVE)) {

				// create a SaveFileFormat object to hold it
				SaveFileFormat saveFileFormat = (SaveFileFormat) theClass.newInstance();

				// set filename extensions for the Save format
				setExtensions(saveFileFormat, extensions) ;
				
				// try to add this format to our set of Save formats
				boolean success = addSaveFormat(formatName, saveFileFormat);
				
				// also try to add it to our set of Export formats
				addExportFormat(formatName, saveFileFormat);

				// supply some feedback
				// if we succeed ...
				if (success) {
					System.out.println("  Save: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					} // end else

				} // end if FORMAT_TYPE_SAVE

			// else if it's an Import format spec ....
			else if (formatType.equals(FORMAT_TYPE_IMPORT)) {

				// create an ImportFileFormat object to hold it
				ImportFileFormat importFileFormat = (ImportFileFormat) theClass.newInstance();

				// set filename extensions for the Import format
				setExtensions(importFileFormat, extensions) ;

				// try to add this format to our set of Import formats
				boolean success = addImportFormat(formatName, importFileFormat);

				// supply some feedback
				// if we succeed ...
				if (success) {
					System.out.println("  Import: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					} // end else

				} // end if FORMAT_TYPE_IMPORT

			// else if it's an Export format spec ....
			else if (formatType.equals(FORMAT_TYPE_EXPORT)) {

				// create an ExportFileFormat object to hold it
				ExportFileFormat exportFileFormat = (ExportFileFormat) theClass.newInstance();

				// set filename extensions for the Export format
				setExtensions(exportFileFormat, extensions) ;

				// try to add this format to our set of Export formats
				boolean success = addExportFormat(formatName, exportFileFormat);

				// supply some feedback
				// if we succeed ...
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
				OpenFileFormat defOpenFileFormat = (OpenFileFormat) theClass.newInstance();

				// set filename extensions for the format
				setExtensions(defOpenFileFormat, extensions);

				// set this format as our OPEN default
				setDefaultOpenFileFormat(defOpenFileFormat);

				// try to add this format to our set of Open formats
				boolean success = addOpenFormat(formatName, defOpenFileFormat);

				// supply some feedback
				// if we succeed ...
				if (success) {
					System.out.println("  Open Default: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					}  // end else

				} // end if FORMAT_TYPE_OPEN_DEFAULT

			// else if it's a Default Save format type ...
			else if (formatType.equals(FORMAT_TYPE_SAVE_DEFAULT)) {

				// create a SaveFileFormat object to hold it
				SaveFileFormat defSaveFileFormat = (SaveFileFormat) theClass.newInstance();

				// set filename extensions for the format
				setExtensions(defSaveFileFormat, extensions);

				// set this format as our SAVE default
				setDefaultSaveFileFormat(defSaveFileFormat);

				// try to add this format to our set of Save formats
				boolean success = addSaveFormat(formatName, defSaveFileFormat);

				// supply some feedback
				// if we succeed ...
				if (success) {
					System.out.println("  Save Default: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					} // end else
				} // end if FORMAT_TYPE_SAVE _DEFAULT

			// else if it's a Default Import format type ...
			else if (formatType.equals(FORMAT_TYPE_IMPORT_DEFAULT)) {

					// create an OpenFileFormat object to hold it
				OpenFileFormat defImportFileFormat = (OpenFileFormat) theClass.newInstance();

				// set filename extensions for the format
				setExtensions(defImportFileFormat, extensions);

				// set this format as our IMPORT default
				setDefaultImportFileFormat(defImportFileFormat);

				// try to add this format to our set of Import formats
				boolean success = addImportFormat(formatName, defImportFileFormat);

				// supply some feedback
				// if we succeed ...
				if (success) {
					System.out.println("  Import Default: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					}  // end else

				} // end if FORMAT_TYPE_IMPORT_DEFAULT

			// else if it's a Default Export format type ...
			else if (formatType.equals(FORMAT_TYPE_EXPORT_DEFAULT)) {

				// create an SaveFileFormat object to hold it
				SaveFileFormat exportFileFormat = (ExportFileFormat) theClass.newInstance();

				// set filename extensions for the format
				setExtensions(exportFileFormat, extensions);

				// set this format as our EXPORT default
				setDefaultExportFileFormat(exportFileFormat);

				// try to add this format to our set of Export formats
				boolean success = addExportFormat(formatName, exportFileFormat);
				addExportFormat(formatName, exportFileFormat);

				// supply some feedback
				// if we succeed ...
				if (success) {
					System.out.println("  Export: " + className + " -> " + formatName);
					} // end if
				else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
					} // end else
				} // end if FORMAT_TYPE_EXPORT _DEFAULT

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

	//---------------------- Open Accessors --------------------------

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
			Preferences.FILE_FORMATS_OPEN.remove(index);

			return true;
		}
		return false;
	}


	//------------------------- Save Accessors ---------------------------

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
			Preferences.FILE_FORMATS_SAVE.remove(index);

			return true;
		}
		return false;
	}
	//====================   import methods   ====================

	public String getImportFileFormatNameForExtension(String extension) {
		for (int i = 0; i < importers.size(); i++) {
			OpenFileFormat format = (OpenFileFormat) importers.get(i);
			if (format.extensionExists(extension.toLowerCase())) {
				return (String) importerNames.get(i);
			}
		}

		int index = importers.indexOf(getDefaultImportFileFormat());
		return (String) importerNames.get(index);
	}

	public OpenFileFormat getDefaultImportFileFormat() {
		return defaultImportFileFormat;
	}


	// add an import format to the set of such  [srk] 12/31/01 11:01PM
	private boolean addImportFormat(String formatName, OpenFileFormat format) {

		// if we don't have this one yet
		if (isNameUnique(formatName, importerNames)) {

			// add it
			importerNames.add(formatName);
			importers.add(format);

			// Also add it to the list of formats stored in the preferences
			Preferences.FILE_FORMATS_IMPORT.add(formatName);

			// exit fat'n'happy
			return true;

		} // end if

		// else we already have this one
		return false;

	} // end method addImportFormat


	// get the import format
	public OpenFileFormat getImportFormat(String formatName) {

				int index = indexOfName(formatName, importerNames);

		if (index >= 0) {
					 return (OpenFileFormat) importers.elementAt(index);
				}

		return null;
		}

	// set up a default import format  [srk] 12/31/01 11:09PM
	private void setDefaultImportFileFormat(OpenFileFormat aFileFormat) {

		defaultImportFileFormat = aFileFormat;

		} // end method setDefaultImportFileFormat

	public boolean removeImportFormat(String formatName) {
		int index = indexOfName(formatName, importerNames);
		if (index >= 0) {
			importerNames.removeElementAt(index);
			importers.removeElementAt(index);

			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_IMPORT.remove(index);

			return true;
		}
		return false;
	}


//====================   export methods   ====================

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
			Preferences.FILE_FORMATS_EXPORT.remove(index);

			return true;
		}
		return false;
	}

	// set up a default export format  [srk] 12/31/01 11:12 PM
	private void setDefaultExportFileFormat(SaveFileFormat aFileFormat) {

		defaultExportFileFormat = aFileFormat;

		} // end method setDefaultExportFileFormat




//====================   utility methods   ====================

	// determine whether a name is not yet a member of a vector
	public static boolean isNameUnique(String name, Vector list) {
		for (int i = 0; i < list.size(); i++) {
			if (name.equals(list.elementAt(i).toString())) {
				return false;
			}
		}
		return true;
	}

	// determine a name's position within a vector
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
