/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
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
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;
import com.organic.maynard.util.*;
import com.organic.maynard.io.FileTools;
import com.organic.maynard.util.string.*;

// WebFile
import javax.swing.filechooser.*;
import com.yearahead.io.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class Outliner extends JFrame implements ClipboardOwner, GUITreeComponent, JoeXMLConstants {
	
	// Constants
	// for [temporary!] conditional debugging code	[srk] 8/04/01 7:33PM
	public static final boolean DEBUG = true;
	    	
	// Language Handling
	public static String LANGUAGE = "";
	
	
	// Directory setup
	public static final String USER_OUTLINER_DIR = "outliner";
	public static String GRAPHICS_DIR = "graphics" + System.getProperty("file.separator");
	public static String PREFS_DIR = "prefs" + System.getProperty("file.separator");
	public static String USER_PREFS_DIR = PREFS_DIR;
	public static final String APP_DIR_PATH = System.getProperty("user.dir") + System.getProperty("file.separator");

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
	public static String FIND_REPLACE_FILE = USER_PREFS_DIR + "find_replace.xml";
	public static String CONFIG_FILE = USER_PREFS_DIR + "config.txt";
	public static String RECENT_FILES_FILE = USER_PREFS_DIR + "recent_files.txt";

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
							appendBuffer.append(Preferences.LINE_END_DEFAULT).append(line);
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
	public static String ENCODINGS_FILE = PREFS_DIR + "encodings.txt";
	public static String FILE_FORMATS_FILE = PREFS_DIR + "file_formats.txt";
	public static String GUI_TREE_FILE = PREFS_DIR + "gui_tree" + LANGUAGE + ".xml";


	// XML Parser
    public static final Parser XML_PARSER = new com.jclark.xml.sax.Driver();	
	
	// Command Parser
	public static final String COMMAND_PARSER_SEPARATOR = "|";
	public static final String COMMAND_SET = "set";
	public static final String COMMAND_MACRO_CLASS = "macro_class";
	public static final String COMMAND_MACRO = "macro";
	public static final String COMMAND_FILE_FORMAT = "file_format";
	public static final CommandParser PARSER = new CommandParser(COMMAND_PARSER_SEPARATOR);
	
	
	// GUI Objects
	public static FindReplaceFrame findReplace = null;
	public static MacroManagerFrame macroManager = null;
	public static MacroPopupMenu macroPopup = null;
	public static FileFormatManager fileFormatManager = null;


	// GUI Settings
	public static Outliner outliner = null;
	public static OutlinerDesktop desktop = new OutlinerDesktop();
	public static JScrollPane jsp = null;
	public static OutlinerDesktopMenuBar menuBar = null;
	public static OutlinerFileChooser chooser = null;
	public static DocumentStatistics statistics = null;
	public static DocumentAttributesView documentAttributes = null;

	static {
		jsp = new JScrollPane(desktop,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED ,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.addComponentListener(new DesktopScrollPaneComponentListener());
	}


	// Help system	[srk] 8/5/01 1:28PM
	public static HelpDocumentsManager helpDoxMgr = null ;


	// GUITreeComponent interface	
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}

	public void startSetup(AttributeList atts) {
		outliner = this;	

		setTitle(atts.getValue(A_TITLE));

		// Load Preferences
		PARSER.addCommand(new SetPrefCommand(COMMAND_SET,2));
		PARSER.addCommand(new LoadMacroClassCommand(COMMAND_MACRO_CLASS,2));
		PARSER.addCommand(new LoadMacroCommand(COMMAND_MACRO,2));
		PARSER.addCommand(new LoadFileFormatClassCommand(COMMAND_FILE_FORMAT,2));

		System.out.println("Loading Encoding Types...");
		loadPrefsFile(PARSER,ENCODINGS_FILE);
		System.out.println("Done Loading Encoding Types.");
		System.out.println("");

		// Setup the FileFormatManager
		fileFormatManager = new FileFormatManager();
		
		System.out.println("Loading File Formats...");
		loadPrefsFile(PARSER,FILE_FORMATS_FILE);
		System.out.println("Done Loading File Formats.");
		System.out.println("");

		// Crank up the Help system	[srk] 8/5/01 1:30PM
		helpDoxMgr = new HelpDocumentsManager() ;
	}
	
	public void endSetup(AttributeList atts) {

		// Setup the Desktop
			// Get Main Window Dimension out of the prefs.
			PreferenceInt pWidth = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_W);
			PreferenceInt pHeight = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_H);
			IntRangeValidator vWidth = (IntRangeValidator) pWidth.getValidator();
			IntRangeValidator vHeight = (IntRangeValidator) pHeight.getValidator();
			
			int minimumWidth = vWidth.getMin();
			int minimumHeight = vHeight.getMin();
			
			int initialWidth = pWidth.cur;
			int initialHeight = pHeight.cur;
			int initialPositionX = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_X).cur;
			int initialPositionY = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_Y).cur;
		
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

		setContentPane(jsp);

		// Set Frame Icon
		ImageIcon icon = new ImageIcon(GRAPHICS_DIR + "frame_icon.gif");
		setIconImage(icon.getImage());

		
		// Setup the MacroManager and the MacroPopupMenu
		loadPrefsFile(PARSER,MACRO_CLASSES_FILE);
		macroPopup = new MacroPopupMenu();
		
		System.out.println("Loading Macros...");
		loadPrefsFile(PARSER,MACROS_FILE);
		System.out.println("Done Loading Macros.");
		System.out.println("");
		
		// Generate Icons
		OutlineButton.createIcons();
		
		// WebFile
		// Note the outliner will have to be restarted if user switches
		// file system preferences since the chooser is created just once.
		// There seems to be a bug where the WEB_FILE_SYSTEM will not
		// work once the native file system is set, otherwise you could
		// just call setFileSystemView() on the file chooser.
		FileSystemView fsv = null;
		if (Preferences.getPreferenceBoolean(Preferences.WEB_FILE_SYSTEM).cur) {
			// if you have authentication enabled on your web server,
			// pass non-null user/password
			fsv = new WebFileSystemView(Preferences.getPreferenceString(Preferences.WEB_FILE_URL).cur,
										Preferences.getPreferenceString(Preferences.WEB_FILE_USER).cur,
										Preferences.getPreferenceString(Preferences.WEB_FILE_PASSWORD).cur);
		}

		// Setup the File Chooser
		chooser = new OutlinerFileChooser(fsv);	

		// Apply the Preference Settings
		Preferences.applyCurrentToApplication();
		
		setVisible(true);

		// Create a Document. This must come after visiblity otherwise the window won't be activated.
		if (Preferences.getPreferenceBoolean(Preferences.NEW_DOC_ON_STARTUP).cur) {
			new OutlinerDocument("");
		}
	}
		
	public static void main(String args[]) {
		// This allows scrollbars to be resized while they are being dragged.
		UIManager.put("ScrollBarUI", "com.organic.maynard.outliner.OutlinerScrollBarUI");


		// See if we've got a preferred language to use. 
		// lang should be a ISO 639 two letter lang code. 
		// List at: http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt
		String lang = null;
		try {
			lang = args[0];
			if (lang != null && lang.length() == 2) {
				LANGUAGE = "." + lang;
				GUI_TREE_FILE = PREFS_DIR + "gui_tree" + LANGUAGE + ".xml";
			}
		} catch (ArrayIndexOutOfBoundsException e) {}

		// Load the GUI
		GUITreeLoader loader = new GUITreeLoader();
		boolean success = loader.load(Outliner.GUI_TREE_FILE);
		if (!success) {
			System.out.println("GUI Loading Error: exiting.");
			System.exit(0);
		}
	
		// See if a file to open was provided at the command line.
		String filepath = null;
		try {
			filepath = args[1];
			if (filepath != null) {
				String extension = filepath.substring(filepath.lastIndexOf(".") + 1,filepath.length());
				String fileFormat = Outliner.fileFormatManager.getOpenFileFormatNameForExtension(extension);
	
				DocumentInfo docInfo = new DocumentInfo();
				docInfo.setPath(filepath);
				docInfo.setEncodingType(Preferences.getPreferenceString(Preferences.OPEN_ENCODING).cur);
				docInfo.setFileFormat(fileFormat);
				
				FileMenu.openFile(docInfo);
			}
		} catch (ArrayIndexOutOfBoundsException e) {}


		// For Debug Purposes
		if (Preferences.getPreferenceBoolean(Preferences.PRINT_ENVIRONMENT).cur) {
			Properties properties = System.getProperties();
			Enumeration names = properties.propertyNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				System.out.println(name + " : " + properties.getProperty(name));
			}
		}
	}
	
	// Utility Methods
	public static boolean isWindows() {
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().startsWith("win")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isJava131() {
		String javaVersion = System.getProperty("java.version");
		if (javaVersion.startsWith("1.3.1")) {
			return true;
		} else {
			return false;
		}	
	}

	
	// Open Document Repository
	private static Vector openDocuments = new Vector();
	private static OutlinerDocument mostRecentDocumentTouched = null;

	public static OutlinerDocument getMostRecentDocumentTouched() {
		return mostRecentDocumentTouched;
	}
	
	public static void setMostRecentDocumentTouched(OutlinerDocument doc) {
		mostRecentDocumentTouched = doc;
		
		if(mostRecentDocumentTouched != null) {
			Outliner.menuBar.windowMenu.selectWindow(doc);
			FindReplaceFrame.enableButtons();
		} else {
			FindReplaceFrame.disableButtons();
			
			updateSaveMenuItem();
			updateSaveAllMenuItem();
			
			UndoQueue.updateMenuBar(doc);
			EditMenu.updateEditMenu(doc);
			
			OutlineMenu.updateOutlineMenu(doc);
			SearchMenu.updateSearchMenu(doc);
			ScriptMenu.updateScriptMenu(doc);
			WindowMenu.updateWindowMenu();
			HelpMenu.updateHelpMenu();	// [srk] 8/5/01 1:06PM
		}
	}
	
	public static void addDocument(OutlinerDocument document) {
		openDocuments.addElement(document);

		// Add it to the WindowMenu
		WindowMenu.addWindow(document);
		
	// Update the close menu items
		CloseFileMenuItem closeItem = (CloseFileMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.CLOSE_MENU_ITEM);
		CloseAllFileMenuItem closeAllItem = (CloseAllFileMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.CLOSE_ALL_MENU_ITEM);
		
		closeItem.setEnabled(true);
		closeAllItem.setEnabled(true);

	// Notify the Help documents manager	[srk 8/5/01 1:12PM]
	helpDoxMgr.someDocumentJustOpened(document) ;
	}
	
	public static OutlinerDocument getDocument(int i) {
		return (OutlinerDocument) openDocuments.elementAt(i);
	}
	
	public static OutlinerDocument getDocument(String filename) {
		for (int i = 0; i < openDocuments.size(); i++) {
			OutlinerDocument doc = getDocument(i);
			if (filename.equals(doc.getFileName())) {
				return doc;
			}
		}
		return null;	
	}
	
	public static void removeDocument(OutlinerDocument document) {
		// Remove the document from the window menus
		WindowMenu.removeWindow(document);
		
		openDocuments.removeElement(document);
		
		IS_CURRENT_DOCUMENT: if (mostRecentDocumentTouched == document) {
			if (openDocumentCount() > 0) {
				for (int i = openDocumentCount() - 1; i >= 0; i--) {
					OutlinerDocument newDoc = getDocument(i);
					if (!newDoc.isIcon()) {
						Outliner.menuBar.windowMenu.changeToWindow(newDoc);
						break IS_CURRENT_DOCUMENT;
					}
				}
			}
			setMostRecentDocumentTouched(null);
			
		}
		
		// Update the Save All Menu Item
		Outliner.updateSaveMenuItem();
		Outliner.updateSaveAllMenuItem();

		// Notify the Help documents manager	[srk 8/5/01 1:23PM]
		helpDoxMgr.someDocumentJustClosed(document);
	}
	
	public static int openDocumentCount() {
		return openDocuments.size();
	}
	
	public static boolean isFileNameUnique(String filename) {
		// For windows we need to normalize the case.
		if (isWindows()) {
			for (int i = 0; i < openDocuments.size(); i++) {
				if (filename.equalsIgnoreCase(getDocument(i).getFileName())) {
					return false;
				}
			}		
		} else {
			for (int i = 0; i < openDocuments.size(); i++) {
				if (filename.equals(getDocument(i).getFileName())) {
					return false;
				}
			}
		}
		return true;
	}


	// File Opening and Saving
	// This method should probably get moved into the FileMenu class if possible.
	public static void updateSaveMenuItem() {
		JMenuItem saveItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SAVE_MENU_ITEM);
		JMenuItem saveAsItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SAVE_AS_MENU_ITEM);
		JMenuItem revertItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.REVERT_MENU_ITEM);
		JMenuItem closeItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.CLOSE_MENU_ITEM);
		JMenuItem closeAllItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.CLOSE_ALL_MENU_ITEM);
	
		if (getMostRecentDocumentTouched() == null) {
			saveItem.setEnabled(false);
			saveAsItem.setEnabled(false);
			revertItem.setEnabled(false);
			closeItem.setEnabled(false);
			closeAllItem.setEnabled(false);
		} else if (getMostRecentDocumentTouched().getFileName().equals("")) {
			saveItem.setEnabled(true);
			saveAsItem.setEnabled(true);
			revertItem.setEnabled(false);
			closeItem.setEnabled(true);
			closeAllItem.setEnabled(true);
		} else if (getMostRecentDocumentTouched().isFileModified()) {
			saveItem.setEnabled(true);
			saveAsItem.setEnabled(true);
			revertItem.setEnabled(true);
			closeItem.setEnabled(true);
			closeAllItem.setEnabled(true);
		} else {
			saveItem.setEnabled(false);
			saveAsItem.setEnabled(true);
			revertItem.setEnabled(false);
			closeItem.setEnabled(true);
			closeAllItem.setEnabled(true);
		}
	}
	
	public static void updateSaveAllMenuItem() {
		boolean enabledState = false;
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			OutlinerDocument doc = Outliner.getDocument(i);
			if (doc.isFileModified() || doc.getFileName().equals("")) {
				enabledState = true;
				break;
			}
		}

		JMenuItem saveAllItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SAVE_ALL_MENU_ITEM);
		saveAllItem.setEnabled(enabledState);
	}


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

	public static void redrawAllOpenDocuments() {
		for (int i = 0; i < openDocuments.size(); i++) {
			getDocument(i).panel.layout.redraw();
		}		
	}
}
