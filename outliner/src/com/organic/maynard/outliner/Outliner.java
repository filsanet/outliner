/**
 * Portions copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002  Stan Krute <Stan@StanKrute.com>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
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

import com.organic.maynard.outliner.util.find.*;
import com.organic.maynard.outliner.dom.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;
import com.organic.maynard.util.*;
import com.organic.maynard.io.FileTools;
import com.organic.maynard.util.string.*;

// MouseWheel
import gui.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class Outliner extends JMouseWheelFrame implements ClipboardOwner, GUITreeComponent, JoeXMLConstants {
	
	// Constants
	// for [temporary!] conditional debugging code	[srk] 8/04/01 7:33PM
	public static final boolean DEBUG = true;
	    	
	// Language Handling
	public static String LANGUAGE = "en"; // Defaults to English.
	
	
	// Directory setup
	public static final String USER_OUTLINER_DIR = "outliner";
	
	// [deric] 31sep2001, We want to be able to specify the graphics dir via a Property in the packaging for MacOS X. If it isn't defined it defaults to the usual "graphics". 
	public static String GRAPHICS_DIR = System.getProperty("com.organic.maynard.outliner.Outliner.graphicsdir", "graphics") + System.getProperty("file.separator");
	// [deric] 31sep2001, Same as above but for the prefs dir.
	public static String PREFS_DIR = System.getProperty("com.organic.maynard.outliner.Outliner.prefsdir", "prefs") + System.getProperty("file.separator");
	public static String USER_PREFS_DIR = PREFS_DIR;
	public static final String APP_DIR_PATH = System.getProperty("user.dir") + System.getProperty("file.separator");
	// [srk] 11-2-01 a few more useful directory constants
	public static String DOX_DIR = System.getProperty("com.organic.maynard.outliner.Outliner.doxdir", "dox") + System.getProperty("file.separator");
	public static String EXTRAS_DIR = System.getProperty("com.organic.maynard.outliner.Outliner.extrasdir", "extras") + System.getProperty("file.separator");
	public static String LIB_DIR = System.getProperty("com.organic.maynard.outliner.Outliner.libdir", "lib") + System.getProperty("file.separator");
	public static String LOGS_DIR = System.getProperty("com.organic.maynard.outliner.Outliner.logsdir", "logs") + System.getProperty("file.separator");
	

	// Find out if we've got a home directory to work with for user preferences, if
	// not then we use the prefs dir as usual.
	static {
		String userhome = System.getProperty("user.home");
		if ((userhome != null) && !userhome.equals("")) {
			USER_PREFS_DIR = userhome + System.getProperty("file.separator") + USER_OUTLINER_DIR + System.getProperty("file.separator");
		}
	}

	// These prefs should be under the users prefs dir, or if no user prefs dir exists then
	// they should be under the apps prefs dir.
	public static String MACROS_DIR = USER_PREFS_DIR + "macros" + System.getProperty("file.separator");
	public static String MACROS_FILE = USER_PREFS_DIR + "macros.txt";
	public static String SCRIPTS_DIR = USER_PREFS_DIR + "scripts" + System.getProperty("file.separator");
	public static String SCRIPTS_FILE = USER_PREFS_DIR + "scripts.txt";
	public static String FIND_REPLACE_FILE = USER_PREFS_DIR + "find_replace.xml";
	public static String CONFIG_FILE = USER_PREFS_DIR + "config.txt";
	public static String RECENT_FILES_FILE = USER_PREFS_DIR + "recent_files.ser";

	// Make the directories in case they don't exist.
	static {
		boolean isCreated = false;
		
		File prefsFile = new File(PREFS_DIR);
		isCreated = prefsFile.mkdirs();
		if (isCreated) {
			System.out.println("Created Preferences Directory: " + prefsFile.getPath());
		}
		
		File userPrefsFile = new File(USER_PREFS_DIR);
		isCreated = userPrefsFile.mkdirs();
		if (isCreated) {
			System.out.println("Created User Preferences Directory: " + userPrefsFile.getPath());
		}
		
		// Create macros directory it it doesn't exist.
		File macrosFile = new File(MACROS_DIR);
		isCreated = macrosFile.mkdirs();
		if (isCreated) {
			System.out.println("Created Macros Directory: " + macrosFile.getPath());
		}
		
		// Copy over any macros files that don't exist
			// First, turn the macros.txt file into a hashtable of lines keyed by the macro name.
			char[] delimiters = {'\n','\r'};
			Vector lines = StringTools.split(FileTools.readFileToString(new File(PREFS_DIR + "macros.txt")),'\\', delimiters);
			Hashtable indexedLines = new Hashtable();
			for (int i = 0; i < lines.size(); i++) {
				String line = (String) lines.elementAt(i);
				if (line.indexOf("|") != -1) {
					int start = line.indexOf("|");
					int end = line.indexOf("|", start + 1);
					String key = line.substring(start + 1, end);
					indexedLines.put(key, line);
				}
			}
			
			// Second, copy macros that don't exist.
			StringBuffer appendBuffer = new StringBuffer();

			File fromMacrosFile = new File(PREFS_DIR + "macros");
			File[] macrosFiles = fromMacrosFile.listFiles();
			
			for (int i = 0; i < macrosFiles.length; i++) {
				File fromFile = macrosFiles[i];
				File toFile = new File(MACROS_DIR + fromFile.getName());
				
				if (!toFile.exists()) {
					try {
						FileTools.copy(fromFile, toFile);
						String line = (String) indexedLines.get(fromFile.getName());
						if (line != null) {
							appendBuffer.append(PlatformCompatibility.LINE_END_DEFAULT).append(line);
						}
						System.out.println("\tCopying macro: " + fromFile.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					macrosFiles[i] = null; // Set to null, so later we know what got copied.
				}
			}
		
			// Third, either copy over entire macros.txt file if it doesn't exist, or append new lines to existing macros.txt file.
			File userMacrosFile = new File(MACROS_FILE);
			if (!userMacrosFile.exists()) {
				System.out.println("Copying over macros config file: " + userMacrosFile.getPath());
				try {
					FileTools.copy(new File(PREFS_DIR + "macros.txt"), userMacrosFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					FileWriter fw = new FileWriter(userMacrosFile.getPath(), true);
					fw.write(appendBuffer.toString());
					fw.flush();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		// Create scripts directory it it doesn't exist.
		File scriptsFile = new File(SCRIPTS_DIR);
		isCreated = scriptsFile.mkdirs();
		if (isCreated) {
			System.out.println("Created Scripts Directory: " + scriptsFile.getPath());
		}
		
		// Copy over any scripts files that don't exist
			// First, turn the scripts.txt file into a hashtable of lines keyed by the macro name.
			lines = StringTools.split(FileTools.readFileToString(new File(PREFS_DIR + "scripts.txt")),'\\', delimiters);
			indexedLines = new Hashtable();
			for (int i = 0; i < lines.size(); i++) {
				String line = (String) lines.elementAt(i);
				if (line.indexOf("|") != -1) {
					int start = line.indexOf("|");
					int end = line.indexOf("|", start + 1);
					String key = line.substring(start + 1, end);
					indexedLines.put(key, line);
				}
			}
			
			// Second, copy macros that don't exist.
			appendBuffer = new StringBuffer();

			File fromScriptsFile = new File(PREFS_DIR + "scripts");
			File[] scriptsFiles = fromScriptsFile.listFiles();
			
			for (int i = 0; i < scriptsFiles.length; i++) {
				File fromFile = scriptsFiles[i];
				File toFile = new File(SCRIPTS_DIR + fromFile.getName());
				
				if (!toFile.exists()) {
					try {
						FileTools.copy(fromFile, toFile);
						String line = (String) indexedLines.get(fromFile.getName());
						if (line != null) {
							appendBuffer.append(PlatformCompatibility.LINE_END_DEFAULT).append(line);
						}
						System.out.println("\tCopying script: " + fromFile.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					scriptsFiles[i] = null; // Set to null, so later we know what got copied.
				}
			}
		
			// Third, either copy over entire macros.txt file if it doesn't exist, or append new lines to existing macros.txt file.
			File userScriptsFile = new File(SCRIPTS_FILE);
			if (!userScriptsFile.exists()) {
				System.out.println("Copying over scripts config file: " + userScriptsFile.getPath());
				try {
					FileTools.copy(new File(PREFS_DIR + "scripts.txt"), userScriptsFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					FileWriter fw = new FileWriter(userScriptsFile.getPath(), true);
					fw.write(appendBuffer.toString());
					fw.flush();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		// Copy over find_replace.xml from installation directory if it doesn't exist in the user's home directory.
		File userFindReplaceFile = new File(FIND_REPLACE_FILE);
		if (!userFindReplaceFile.exists()) {
			System.out.println("Copying over find_replace config file: " + userFindReplaceFile.getPath());
			try {
				FileTools.copy(new File(PREFS_DIR + "find_replace.xml"), userFindReplaceFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// These dirs/files should always be under the apps prefs dir.
	public static String MACRO_CLASSES_FILE = PREFS_DIR + "macro_classes.txt";
	public static String SCRIPT_CLASSES_FILE = PREFS_DIR + "script_classes.txt";
	public static String ENCODINGS_FILE = PREFS_DIR + "encodings.txt";
	public static String FILE_FORMATS_FILE = PREFS_DIR + "file_formats.txt";
	public static String GUI_TREE_FILE = PREFS_DIR + "gui_tree." + LANGUAGE + ".xml";


	// XML Parser
    public static final Parser XML_PARSER = new com.jclark.xml.sax.Driver();	


	// Command Parser
	public static final String COMMAND_PARSER_SEPARATOR = "|";
	public static final String COMMAND_SET = "set";
	public static final String COMMAND_MACRO_CLASS = "macro_class";
	public static final String COMMAND_MACRO = "macro";
	public static final String COMMAND_SCRIPT_CLASS = "script_class";
	public static final String COMMAND_SCRIPT = "script";
	public static final String COMMAND_FILE_FORMAT = "file_format";
	public static final String COMMAND_FILE_PROTOCOL = "file_protocol";
	public static final CommandParser PARSER = new CommandParser(COMMAND_PARSER_SEPARATOR);
	
	
	// GUI Objects
	public static FindReplaceFrame findReplace = null;
	public static FindReplaceResultsDialog findReplaceResultsDialog = null;
	public static MacroManagerFrame macroManager = null;
	public static ScriptsManager scriptsManager = null;
	public static MacroPopupMenu macroPopup = null;
	public static FileFormatManager fileFormatManager = null;
	public static FileProtocolManager fileProtocolManager = null;
	
	// DOM Objects
	public static DocumentRepository documents = new DocumentRepository();
	
	public static Preferences prefs = null;


	// GUI Settings
	public static Outliner outliner = null;
	public static OutlinerDesktop desktop = new OutlinerDesktop();
	public static JScrollPane jsp = null;
	public static OutlinerDesktopMenuBar menuBar = null;
	public static DocumentStatistics statistics = null;
	public static DocumentAttributesView documentAttributes = null;

	//static {
	//	jsp = new JScrollPane(desktop,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED ,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	//	jsp.addComponentListener(new DesktopScrollPaneComponentListener());
	//}


	// Help system	[srk] 8/5/01 1:28PM
	public static HelpDocumentsManager helpDoxMgr = null;


	// GUITreeComponent interface	
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}

	public void startSetup(AttributeList atts) {
		outliner = this;
		
		// MouseWheel
		if (PlatformCompatibility.isWindows()) {
			JMouseWheelSupport.setMinScrollDistance(1);
		}

		setTitle(atts.getValue(A_TITLE));

		// Load Preferences
		PARSER.addCommand(new SetPrefCommand(COMMAND_SET,2));
		PARSER.addCommand(new LoadMacroClassCommand(COMMAND_MACRO_CLASS,2));
		PARSER.addCommand(new LoadMacroCommand(COMMAND_MACRO,2));
		PARSER.addCommand(new LoadScriptClassCommand(COMMAND_SCRIPT_CLASS,2));
		PARSER.addCommand(new LoadScriptCommand(COMMAND_SCRIPT,2));
		PARSER.addCommand(new LoadFileFormatClassCommand(COMMAND_FILE_FORMAT,2));
		PARSER.addCommand(new LoadFileProtocolClassCommand(COMMAND_FILE_PROTOCOL,2));

		System.out.println("Loading Encoding Types...");
		loadPrefsFile(PARSER,ENCODINGS_FILE);
		System.out.println("Done Loading Encoding Types.");
		System.out.println("");

		// Setup the FileFormatManager and FileProtocolManager
		fileFormatManager = new FileFormatManager();
		fileProtocolManager = new FileProtocolManager();
		
		System.out.println("Loading File Formats...");
		loadPrefsFile(PARSER,FILE_FORMATS_FILE);
		System.out.println("Done Loading File Formats.");
		System.out.println("");


		// Crank up the Help system	[srk] 8/5/01 1:30PM
		helpDoxMgr = new HelpDocumentsManager() ;
	}
	
	public void endSetup(AttributeList atts) {

		// Setup the Desktop
		
		// Set the Window Location.
			// Get Main Window Dimension out of the prefs.
			PreferenceInt pWidth = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_W);
			PreferenceInt pHeight = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_H);
			PreferenceInt pInitialPositionX = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_X);
			PreferenceInt pInitialPositionY = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_Y);

			IntRangeValidator vWidth = (IntRangeValidator) pWidth.getValidator();
			IntRangeValidator vHeight = (IntRangeValidator) pHeight.getValidator();
			IntRangeValidator vInitialPositionX = (IntRangeValidator) pInitialPositionX.getValidator();
			IntRangeValidator vInitialPositionY = (IntRangeValidator) pInitialPositionY.getValidator();
			
			int minimumWidth = vWidth.getMin();
			int minimumHeight = vHeight.getMin();
			
			int initialWidth = pWidth.cur;
			int initialHeight = pHeight.cur;
			int initialPositionX = pInitialPositionX.cur;
			int initialPositionY = pInitialPositionY.cur;
		
			// Make sure initial position isn't off screen, or even really close to the edge.
			int bottom_left_inset = 100;
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			
			if (initialPositionX < vInitialPositionX.getMin()) {
				initialPositionX = vInitialPositionX.getMin();
			}
			
			if (initialPositionX > (screenSize.width - bottom_left_inset)) {
				initialPositionX = screenSize.width - bottom_left_inset;
			}

			if (initialPositionY < vInitialPositionY.getMin()) {
				initialPositionY = vInitialPositionY.getMin();
			}
			
			if (initialPositionY > (screenSize.height - bottom_left_inset)) {
				initialPositionY = screenSize.height - bottom_left_inset;
			}
						
			setLocation(initialPositionX, initialPositionY);


		addComponentListener(new WindowSizeManager(initialWidth, initialHeight, minimumWidth, minimumHeight));
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					QuitMenuItem item = (QuitMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.QUIT_MENU_ITEM);
					item.quit();
				}
			}
		);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// Setup Desktop ScrollPane and set the ContentPane.
		jsp = new JScrollPane(desktop,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED ,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.addComponentListener(new DesktopScrollPaneComponentListener());
		setContentPane(jsp);

		// Set Frame Icon
		ImageIcon icon = new ImageIcon(GRAPHICS_DIR + "frame_icon.gif");
		setIconImage(icon.getImage());

		// Initialize open/save_as/export/export_selection menus.
		fileProtocolManager.synchronizeDefault();
		fileProtocolManager.synchronizeMenus();
		
		// Setup the MacroManager and the MacroPopupMenu
		loadPrefsFile(PARSER,MACRO_CLASSES_FILE);
		macroPopup = new MacroPopupMenu();
		
		System.out.println("Loading Macros...");
		loadPrefsFile(PARSER,MACROS_FILE);
		System.out.println("Done Loading Macros.");
		System.out.println("");

		// Setup the ScriptManager
		loadPrefsFile(PARSER,SCRIPT_CLASSES_FILE);
		
		System.out.println("Loading Scripts...");
		loadPrefsFile(PARSER,SCRIPTS_FILE);
		System.out.println("Done Loading Scripts.");
		System.out.println("");
		
		// Setup the FindReplaceResultsDialog
		findReplaceResultsDialog = new FindReplaceResultsDialog();
		
		// Generate Icons
		OutlineButton.createIcons();

		// Apply the Preference Settings
		Preferences.applyCurrentToApplication();
		
		setVisible(true);
	}
	
	
	// after the static initializers run, execution continues here	
	public static void main(String args[]) {
		// This allows scrollbars to be resized while they are being dragged.
		UIManager.put("ScrollBarUI", PlatformCompatibility.getScrollBarUIClassName());

		// See if we've got a preferred language to use. 
		// lang should be a ISO 639 two letter lang code. 
		// List at: http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt
		String lang = null;
		try {
			lang = args[0];
			if (lang != null && lang.length() == 2) {
				LANGUAGE = lang;
				GUI_TREE_FILE = PREFS_DIR + "gui_tree." + LANGUAGE + ".xml";
			}
		} catch (ArrayIndexOutOfBoundsException e) {}
		
		// try to load the GUI
		GUITreeLoader loader = new GUITreeLoader();
		boolean success = loader.load(Outliner.GUI_TREE_FILE);
		// if we fail ...
		if (!success) {
			// confess
			System.out.println("GUI Loading Error: exiting.");
			// outta here
			System.exit(0);
		} // end if

		// Run startup scripts. We're doing this just prior to opening any documents.
		ScriptsManagerModel.runStartupScripts();
		
		// Create a Document. This must come after visiblity otherwise the window won't be activated.
		if (Preferences.getPreferenceBoolean(Preferences.NEW_DOC_ON_STARTUP).cur) {
			new OutlinerDocument("");
		} // end if we're new-doccing on startup
		
		// See if the command line included a file to be opened.
		String filepath = null;
		try {
			// build up the filepath
			filepath = args[1];
			// [srk] the following is needed because java*.exe splits up
			//	pathnames containing spaces into multiple arg strings
			for (int i= 2, argCount = args.length; i< argCount; i++) {
				filepath = filepath + " " +  args[i] ;
			} // end for
			
			// if the filepath is present and non-empty ...
			if ((filepath != null) && (! filepath.equals("")) && (! filepath.equals("%1")) ) {
				
				// ensure that we have a full pathname [srk]
				filepath = canonicalPath(filepath) ;
				
				// grab the file's extension
				String extension = filepath.substring(filepath.lastIndexOf(".") + 1,filepath.length());
				
				// use the extension to figure out the file's format
				String fileFormat = Outliner.fileFormatManager.getOpenFileFormatNameForExtension(extension);
	
				// crank up a fresh docInfo struct
				DocumentInfo docInfo = new DocumentInfo();
				docInfo.setPath(filepath);
				docInfo.setEncodingType(Preferences.getPreferenceString(Preferences.OPEN_ENCODING).cur);
				docInfo.setFileFormat(fileFormat);
				
				// try to open up the file
				FileMenu.openFile(docInfo, fileProtocolManager.getDefault());
			} // end if the filepath is present and non-empty
		} // end try
		catch (ArrayIndexOutOfBoundsException e) {
		} // end catch


		// For Debug Purposes
		if (Preferences.getPreferenceBoolean(Preferences.PRINT_ENVIRONMENT).cur) {
			Properties properties = System.getProperties();
			Enumeration names = properties.propertyNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				System.out.println(name + " : " + properties.getProperty(name));
			} // end while
		} // end if
		
	} // end method main

	// ClipboardOwner Interface
	public static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}
	
	
	// Misc Methods
	public static void loadPrefsFile(CommandParser parser, String filename) {
		CommandQueue commandQueue = new CommandQueue(25);
		commandQueue.loadFromFile(filename);
		
		while (commandQueue.getSize() > 0) {
			try {
				parser.parse((String) commandQueue.getNext());
			} catch (UnknownCommandException uce) {
				System.out.println("Unknown Command");
			}
		}	
	}

	
	
	// all calls for a new JoeTree come thru here
	// if changing the class that's implementing JoeTree, do so here
	public static JoeTree newTree (OutlinerDocument document) {
		
		// JoeTree currently implemented by TreeContext
		if (document == null) {
			return new TreeContext() ;
		}
		else {
			return new TreeContext(document) ;
		} // end else
	} // end method newTree


	// all calls for a new JoeNodeList come thru here
	// if changing the class that's implementing JoeNodeList, do so here
	public static JoeNodeList newNodeList (int initialCapacity) {
		
		// JoeNodeList currently implemented by NodeList
		return new NodeList(initialCapacity) ;
		
	} // end method newNodeList
	
	
	// ensures a canonical pathname
	private static String canonicalPath (String inputString) {
		String canon = null ;
		File file = new File(inputString) ;

		try {
			canon = file.getCanonicalPath() ;
		}
		catch (IOException e) {return inputString ;}
		
		return canon ;
		
	} // end method canonicalPath

} // end class Outliner
