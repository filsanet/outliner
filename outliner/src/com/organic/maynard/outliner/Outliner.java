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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import com.organic.maynard.util.*;

public class Outliner extends JFrame {

	public static final String PREFS_DIR = "prefs" + System.getProperty("file.separator");
	public static final String MACROS_DIR = PREFS_DIR + "macros" + System.getProperty("file.separator");
	public static final String GRAPHICS_DIR = "graphics";
	
	public static final String MACRO_CLASSES_FILE = PREFS_DIR + "macro_classes.txt";
	public static final String CONFIG_FILE = PREFS_DIR + "config.txt";
	public static final String RECENT_FILES_FILE = PREFS_DIR + "recent_files.txt";
	public static final String ENCODINGS_FILE = PREFS_DIR + "encodings.txt";
	public static final String FILE_FORMATS_FILE = PREFS_DIR + "file_formats.txt";
	
	public static final String COMMAND_PARSER_SEPARATOR = "|";
	public static final String COMMAND_SET = "set";
	public static final String COMMAND_MACRO_CLASS = "macro_class";
	public static final String COMMAND_FILE_FORMAT = "file_format";
	public static final CommandParser PARSER = new CommandParser(COMMAND_PARSER_SEPARATOR);
	
	public static PreferencesFrame prefs = null;
	public static FindReplaceFrame findReplace = null;
	public static MacroManagerFrame macroManager = null;
	public static MacroPopupMenu macroPopup = null;
	public static FileFormatManager fileFormatManager = null;

	// GUI
	static final int MIN_WIDTH = 450;
	static final int MIN_HEIGHT = 450;
 
 	static final int INITIAL_WIDTH = 600;
	static final int INITIAL_HEIGHT = 600;
	
	public static Outliner outliner = null;
	public static OutlinerDesktop desktop = new OutlinerDesktop();
	public static JScrollPane jsp;
	public static OutlinerDesktopMenuBar menuBar;

	static {
		jsp = new JScrollPane(desktop,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED ,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.addComponentListener(new DesktopScrollPaneComponentListener());
	}

	public Outliner() {
		super();		
		
		outliner = this;
		
		// Load Preferences
		PARSER.addCommand(new SetPrefCommand(COMMAND_SET,2,this));
		PARSER.addCommand(new LoadMacroClassCommand(COMMAND_MACRO_CLASS,2,this));
		PARSER.addCommand(new LoadFileFormatClassCommand(COMMAND_FILE_FORMAT,2,this));

		loadPrefsFile(PARSER,CONFIG_FILE);
		loadPrefsFile(PARSER,RECENT_FILES_FILE);
		loadPrefsFile(PARSER,ENCODINGS_FILE);

		// Setup the FileFormatManager
		fileFormatManager = new FileFormatManager();
		System.out.println("Loading File Formats...");
		loadPrefsFile(PARSER,FILE_FORMATS_FILE);
		System.out.println("Done Loading File Formats.");
				
		// Now setup the Preferences Frame since we now have the prefs loaded from the config file.
		prefs = new PreferencesFrame();
		
		// Setup the FindReplace Frame
		findReplace = new FindReplaceFrame();
		
		// Setup the MacroManager and the MacroPopupMenu
		macroManager = new MacroManagerFrame();
		loadPrefsFile(PARSER,MACRO_CLASSES_FILE);
		
		macroPopup = new MacroPopupMenu();
		macroPopup.init();
		
		// Setup the Desktop
		menuBar = new OutlinerDesktopMenuBar();
		addComponentListener(new WindowSizeManager(INITIAL_WIDTH,INITIAL_HEIGHT,MIN_WIDTH,MIN_HEIGHT));
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					FileMenu.quit();
				}
			}
		);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		setTitle("Outliner");
		setContentPane(jsp);
		setJMenuBar(menuBar);
		
		// Set Frame Icon
		ImageIcon icon = new ImageIcon(GRAPHICS_DIR + System.getProperty("file.separator") + "frame_icon.gif");
		setIconImage(icon.getImage());
		
		// Set the Desktop Color
		jsp.getViewport().setBackground(Preferences.DESKTOP_BACKGROUND_COLOR.cur);
		desktop.setBackground(Preferences.DESKTOP_BACKGROUND_COLOR.cur);

		// Setup the File Chooser
		setupFileChooser();

		setVisible(true);

		// Create a Document. This must come after visiblity otherwise the window won't be activated.
		if (Preferences.NEW_DOC_ON_STARTUP.cur) {
			new OutlinerDocument("");
		}
	}
		
	public static void main(String args[]) {
		UIManager.put("ScrollBarUI", "com.organic.maynard.outliner.OutlinerScrollBarUI");

		Outliner outliner = new Outliner();
		
		// For Debug Purposes
		if (Preferences.PRINT_ENVIRONMENT.cur) {
			Properties properties = System.getProperties();
			Enumeration names = properties.propertyNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				System.out.println(name + " : " + properties.getProperty(name));
			}
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
			WindowMenu.updateWindowMenu();
		}
	}
	
	public static void addDocument(OutlinerDocument document) {
		openDocuments.addElement(document);

		// Update the close menu item
		menuBar.fileMenu.FILE_CLOSE_ITEM.setEnabled(true);
		menuBar.fileMenu.FILE_CLOSE_ALL_ITEM.setEnabled(true);
	}
	
	public static OutlinerDocument getDocument(int i) {
		return (OutlinerDocument) openDocuments.elementAt(i);
	}
	
	public static void removeDocument(OutlinerDocument document) {
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
	}
	
	public static int openDocumentCount() {
		return openDocuments.size();
	}
	
	public static boolean isFileNameUnique(String filename) {
		for (int i = 0; i < openDocuments.size(); i++) {
			if (filename.equals(getDocument(i).getFileName())) {
				return false;
			}
		}
		return true;
	}


	// File Opening and Saving
	public static JFileChooser chooser = new JFileChooser();
	public static PreferenceString chooserLineEnd = null;
	public static PreferenceString chooserEncoding = null;
	public static PreferenceString chooserFileFormat = null;

	public static JPanel openAccessory = new JPanel();
	public static JPanel saveAccessory = new JPanel();
	
	public static JComboBox lineEndComboBox = new JComboBox(Preferences.PLATFORM_IDENTIFIERS);
	public static JComboBox encodingComboBox = new JComboBox();
	public static JComboBox openEncodingComboBox = new JComboBox();
	public static JComboBox openFormatComboBox = new JComboBox();
	public static JComboBox saveFormatComboBox = new JComboBox();

	private static void setupFileChooser() {
		chooserLineEnd = new PreferenceString(Preferences.LINE_END.cur,Preferences.LINE_END.cur,"");
		chooserEncoding = new PreferenceString(Preferences.SAVE_ENCODING.cur,Preferences.SAVE_ENCODING.cur,"");
		chooserFileFormat = new PreferenceString(Preferences.SAVE_FORMAT.cur,Preferences.SAVE_FORMAT.cur,"");

		lineEndComboBox.addItemListener(new ComboBoxListener(lineEndComboBox, chooserLineEnd));
		encodingComboBox.addItemListener(new ComboBoxListener(encodingComboBox, chooserEncoding));
		openEncodingComboBox.addItemListener(new ComboBoxListener(openEncodingComboBox, chooserEncoding));
		openFormatComboBox.addItemListener(new ComboBoxListener(openFormatComboBox, chooserFileFormat));
		saveFormatComboBox.addItemListener(new ComboBoxListener(saveFormatComboBox, chooserFileFormat));
		
		for (int i = 0; i < Preferences.ENCODINGS.size(); i++) {
			String encoding = (String) Preferences.ENCODINGS.elementAt(i);
			encodingComboBox.addItem(encoding);
			openEncodingComboBox.addItem(encoding);
		}
		
		for (int i = 0; i < Preferences.FILE_FORMATS_OPEN.size(); i++) {
			openFormatComboBox.addItem((String) Preferences.FILE_FORMATS_OPEN.elementAt(i));
		}

		for (int i = 0; i < Preferences.FILE_FORMATS_SAVE.size(); i++) {
			saveFormatComboBox.addItem((String) Preferences.FILE_FORMATS_SAVE.elementAt(i));
		}
		
		// Layout save panel
		Box box = Box.createVerticalBox();

		addSingleItemCentered(new JLabel("Line Terminator"), box);
		addSingleItemCentered(lineEndComboBox, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("File Encoding"), box);
		addSingleItemCentered(encodingComboBox, box);

		box.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("File Format"), box);
		addSingleItemCentered(saveFormatComboBox, box);

		saveAccessory.add(box,BorderLayout.CENTER);
		
		// Layout open panel
		Box box2 = Box.createVerticalBox();

		addSingleItemCentered(new JLabel("File Encoding"), box2);
		addSingleItemCentered(openEncodingComboBox, box2);

		box2.add(Box.createVerticalStrut(5));

		addSingleItemCentered(new JLabel("File Format"), box2);
		addSingleItemCentered(openFormatComboBox, box2);

		openAccessory.add(box2,BorderLayout.CENTER);
	}

	private static void addSingleItemCentered(JComponent component, Container container) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		component.setMaximumSize(component.getPreferredSize());
		box.add(component);
		box.add(Box.createHorizontalGlue());
		container.add(box);
	}
	
	public static void updateSaveMenuItem() {
		if (getMostRecentDocumentTouched() == null) {
			Outliner.menuBar.fileMenu.FILE_SAVE_ITEM.setEnabled(false);
			Outliner.menuBar.fileMenu.FILE_SAVE_AS_ITEM.setEnabled(false);
			Outliner.menuBar.fileMenu.FILE_REVERT_ITEM.setEnabled(false);
			Outliner.menuBar.fileMenu.FILE_CLOSE_ITEM.setEnabled(false);
			Outliner.menuBar.fileMenu.FILE_CLOSE_ALL_ITEM.setEnabled(false);
		} else if (getMostRecentDocumentTouched().getFileName().equals("")) {
			Outliner.menuBar.fileMenu.FILE_SAVE_ITEM.setEnabled(true);
			Outliner.menuBar.fileMenu.FILE_SAVE_AS_ITEM.setEnabled(true);
			Outliner.menuBar.fileMenu.FILE_REVERT_ITEM.setEnabled(false);
			Outliner.menuBar.fileMenu.FILE_CLOSE_ITEM.setEnabled(true);
			Outliner.menuBar.fileMenu.FILE_CLOSE_ALL_ITEM.setEnabled(true);
		} else if (getMostRecentDocumentTouched().isFileModified()) {
			Outliner.menuBar.fileMenu.FILE_SAVE_ITEM.setEnabled(true);
			Outliner.menuBar.fileMenu.FILE_SAVE_AS_ITEM.setEnabled(true);
			Outliner.menuBar.fileMenu.FILE_REVERT_ITEM.setEnabled(true);
			Outliner.menuBar.fileMenu.FILE_CLOSE_ITEM.setEnabled(true);
			Outliner.menuBar.fileMenu.FILE_CLOSE_ALL_ITEM.setEnabled(true);
		} else {
			Outliner.menuBar.fileMenu.FILE_SAVE_ITEM.setEnabled(false);
			Outliner.menuBar.fileMenu.FILE_SAVE_AS_ITEM.setEnabled(true);
			Outliner.menuBar.fileMenu.FILE_REVERT_ITEM.setEnabled(false);
			Outliner.menuBar.fileMenu.FILE_CLOSE_ITEM.setEnabled(true);
			Outliner.menuBar.fileMenu.FILE_CLOSE_ALL_ITEM.setEnabled(true);
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

		Outliner.menuBar.fileMenu.FILE_SAVE_ALL_ITEM.setEnabled(enabledState);
	}
	
	
	// Misc Methods
	private static void loadPrefsFile(CommandParser parser, String filename) {
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
			OutlinerDocument doc = getDocument(i);
			doc.panel.layout.draw();
			doc.panel.layout.setFocus(doc.tree.getEditingNode(),doc.tree.getComponentFocus());
		}		
	}
}