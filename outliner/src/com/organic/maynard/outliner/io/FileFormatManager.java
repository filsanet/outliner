/**
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

package com.organic.maynard.outliner.io;

import com.organic.maynard.outliner.util.preferences.*;
import java.io.*;
import java.util.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */
 
public class FileFormatManager {

	private static final boolean VERBOSE = false;
	
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
	private ArrayList openers = new ArrayList();
	private ArrayList openerNames = new ArrayList();

	private ArrayList savers = new ArrayList();
	private ArrayList saverNames = new ArrayList();

	private ArrayList exporters = new ArrayList();
	private ArrayList exporterNames = new ArrayList();

	private ArrayList importers = new ArrayList();
	private ArrayList importerNames = new ArrayList();

	private OpenFileFormat defaultOpenFileFormat = null;
	private SaveFileFormat defaultSaveFileFormat = null;
	private OpenFileFormat defaultImportFileFormat = null;
	private SaveFileFormat defaultExportFileFormat = null;

	
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
					if (VERBOSE) {
						System.out.println("  Open: " + className + " -> " + formatName);
					}
				} else {
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
					if (VERBOSE) {
						System.out.println("  Save: " + className + " -> " + formatName);
					}
				} else {
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
					if (VERBOSE) {
						System.out.println("  Import: " + className + " -> " + formatName);
					}
				} else {
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
					if (VERBOSE) {
						System.out.println("  Export: " + className + " -> " + formatName);
					}
				} else {
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
					if (VERBOSE) {
						System.out.println("  Open Default: " + className + " -> " + formatName);
					}
				} else {
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

				// also try to add it to our set of Export formats
				addExportFormat(formatName, defSaveFileFormat);

				// supply some feedback
				// if we succeed ...
				if (success) {
					if (VERBOSE) {
						System.out.println("  Save Default: " + className + " -> " + formatName);
					}
				} else {
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
					if (VERBOSE) {
						System.out.println("  Import Default: " + className + " -> " + formatName);
					}
				} else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
				}

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
					if (VERBOSE) {
						System.out.println("  Export: " + className + " -> " + formatName);
					}
				} else {
					// failure comes from duplication
					System.out.println("  Duplicate File Format Name: " + formatName);
				} // end else
			} // end if FORMAT_TYPE_EXPORT _DEFAULT

		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// set a format's set of filename extensions
	private void setExtensions(FileFormat format, Vector extensions) {
		if (extensions == null) {
			return;
		}

		for (int i = 0, limit = extensions.size(); i < limit; i++) {
			// the first extension in the set is the default
			if (i == 0) {
				format.addExtension(((String) extensions.get(i)).toLowerCase(), true);
			} else {
				format.addExtension(((String) extensions.get(i)).toLowerCase(), false);
			}
		}
	}

	//---------------------- Open Accessors --------------------------

	public String getOpenFileFormatNameForExtension(String extension) {
		String lowerCaseExtension = extension.toLowerCase();
		for (int i = 0, limit = openers.size(); i < limit; i++) {
			OpenFileFormat format = (OpenFileFormat) openers.get(i);
			if (format.extensionExists(lowerCaseExtension)) {
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
			return (OpenFileFormat) openers.get(index);
		}
		return null;
	}

	public boolean removeOpenFormat(String formatName) {
		int index = indexOfName(formatName, openerNames);
		if (index >= 0) {
			openerNames.remove(index);
			openers.remove(index);

			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_OPEN.remove(index);

			return true;
		}
		return false;
	}


	//------------------------- Save Accessors ---------------------------

	public String getSaveFileFormatNameForExtension(String extension) {
		String lowerCaseExtension = extension.toLowerCase();
		for (int i = 0, limit = savers.size(); i < limit; i++) {
			SaveFileFormat format = (SaveFileFormat) savers.get(i);
			if (format.extensionExists(lowerCaseExtension)) {
				return (String) saverNames.get(i);
			}
		}

		int index = savers.indexOf(getDefaultSaveFileFormat());
		return (String) saverNames.get(index);
	}

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
			return (SaveFileFormat) savers.get(index);
		}
		return null;
	}

	public boolean removeSaveFormat(String formatName) {
		int index = indexOfName(formatName, saverNames);
		if (index >= 0) {
			saverNames.remove(index);
			savers.remove(index);

			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_SAVE.remove(index);

			return true;
		}
		return false;
	}
	//====================   import methods   ====================

	public String getImportFileFormatNameForExtension(String extension) {
		String lowerCaseExtension = extension.toLowerCase();
		for (int i = 0, limit = importers.size(); i < limit; i++) {
			OpenFileFormat format = (OpenFileFormat) importers.get(i);
			if (format.extensionExists(lowerCaseExtension)) {
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
		if (isNameUnique(formatName, importerNames)) {
			importerNames.add(formatName);
			importers.add(format);

			// Also add it to the list of formats stored in the preferences
			Preferences.FILE_FORMATS_IMPORT.add(formatName);

			return true;
		}
		return false;
	}


	// get the import format
	public OpenFileFormat getImportFormat(String formatName) {
		int index = indexOfName(formatName, importerNames);
		if (index >= 0) {
			return (OpenFileFormat) importers.get(index);
		}
		return null;
	}

	// set up a default import format  [srk] 12/31/01 11:09PM
	private void setDefaultImportFileFormat(OpenFileFormat aFileFormat) {
		defaultImportFileFormat = aFileFormat;
	}

	public boolean removeImportFormat(String formatName) {
		int index = indexOfName(formatName, importerNames);
		if (index >= 0) {
			importerNames.remove(index);
			importers.remove(index);

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
			return (SaveFileFormat) exporters.get(index);
		}
		return null;
	}

	public boolean removeExportFormat(String formatName) {
		int index = indexOfName(formatName, exporterNames);
		if (index >= 0) {
			exporterNames.remove(index);
			exporters.remove(index);

			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_EXPORT.remove(index);

			return true;
		}
		return false;
	}

	// set up a default export format  [srk] 12/31/01 11:12 PM
	private void setDefaultExportFileFormat(SaveFileFormat aFileFormat) {
		defaultExportFileFormat = aFileFormat;
	}




//====================   utility methods   ====================

	// determine whether a name is not yet a member of a vector
	private static boolean isNameUnique(String name, ArrayList list) {
		for (int i = 0, limit = list.size(); i < limit; i++) {
			if (name.equals(list.get(i).toString())) {
				return false;
			}
		}
		return true;
	}

	// determine a name's position within a vector
	private static int indexOfName(String name, ArrayList list) {
		for (int i = 0, limit = list.size(); i < limit; i++) {
			if (name.equals(list.get(i).toString())) {
				return i;
			}
		}
		return -1;
	}
}
