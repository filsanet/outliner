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

import java.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

import org.xml.sax.*;

import com.organic.maynard.util.string.*;

public class Preferences implements GUITreeComponent {

	// Constants
	public static final String DEPTH_PAD_STRING = "\t"; // Specific to Outliner Docs
	public static final String LINE_END_STRING = "\n"; // Specific to Outliner Docs

	public static final String LINE_END_MAC = "\r";
	public static final String LINE_END_WIN = "\r\n";
	public static final String LINE_END_UNIX = "\n";
	public static final String LINE_END_DEFAULT = System.getProperty("line.separator");
	

	public static final String PLATFORM_MAC = "Mac";
	public static final String PLATFORM_WIN = "Windows";
	public static final String PLATFORM_UNIX = "Unix";
	
	public static final String[] PLATFORM_IDENTIFIERS = {PLATFORM_MAC,PLATFORM_WIN,PLATFORM_UNIX};

	public static final String TXT_WORDS = "words";
	public static final String TXT_CHARACTERS = "characters";

	public static final Vector ENCODINGS = new Vector();
	public static final Vector FILE_FORMATS_OPEN = new Vector();
	public static final Vector FILE_FORMATS_SAVE = new Vector();

	
	// Start Preference Keys
		// Hidden Prefs
			// Misc
			public static final String RENDERER_WIDGIT_CACHE_SIZE = "renderer_widgit_cache_size";
			public static final String TIME_ZONE_FOR_SAVING_DATES = "time_zone_for_saving_dates";
			public static final String MOST_RECENT_SAVE_DIR = "most_recent_save_dir";
			public static final String MOST_RECENT_OPEN_DIR = "most_recent_open_dir";
			public static final String IS_MAXIMIZED = "is_maximized";

			// Main Window
			public static final String MAIN_WINDOW_W = "main_window_width";
			public static final String MAIN_WINDOW_H = "main_window_height";
			public static final String MAIN_WINDOW_X = "main_window_x_offset";
			public static final String MAIN_WINDOW_Y = "main_window_y_offset";


		// Editor Panel
		public static final String FONT_FACE = "font_face";
		public static final String FONT_SIZE = "font_size";
		public static final String LINE_WRAP = "line_wrap";
		public static final String UNDO_QUEUE_SIZE = "undo_queue_size";
		public static final String SHOW_LINE_NUMBERS = "show_line_numbers";
		public static final String SHOW_ATTRIBUTES = "show_attributes";
	
		// Look & Feel Panel
		public static final String DESKTOP_BACKGROUND_COLOR = "desktop_background_color";
		public static final String PANEL_BACKGROUND_COLOR = "panel_background_color";
		public static final String TEXTAREA_BACKGROUND_COLOR = "textarea_background_color";
		public static final String TEXTAREA_FOREGROUND_COLOR = "textarea_foreground_color";
		public static final String SELECTED_CHILD_COLOR = "selected_child_color";
		public static final String LINE_NUMBER_COLOR = "line_number_color";
		public static final String LINE_NUMBER_SELECTED_COLOR = "line_number_selected_color";
		public static final String LINE_NUMBER_SELECTED_CHILD_COLOR = "line_number_selected_child_color";
		public static final String INDENT = "indent";
		public static final String VERTICAL_SPACING = "vertical_spacing";
		public static final String LEFT_MARGIN = "left_margin";
		public static final String TOP_MARGIN = "top_margin";
		public static final String RIGHT_MARGIN = "right_margin";
		public static final String BOTTOM_MARGIN = "bottom_margin";
		
		// Open & Save Panel
		public static final String LINE_END = "line_end";
		public static final String OPEN_ENCODING = "open_encoding";
		public static final String SAVE_ENCODING = "save_encoding";
		public static final String OPEN_FORMAT = "open_format";
		public static final String SAVE_FORMAT = "save_format";
		
		// Misc Panel
		public static final String PRINT_ENVIRONMENT = "print_environment";
		public static final String NEW_DOC_ON_STARTUP = "new_doc_on_startup";
		public static final String RECENT_FILES_LIST_SIZE = "recent_files_list_size";
		public static final String OWNER_NAME = "owner_name";
		public static final String OWNER_EMAIL = "owner_email";

			// WebFile
			public static final String WEB_FILE_SYSTEM = "web_file_system";
			public static final String WEB_FILE_URL = "web_file_url";
			public static final String WEB_FILE_USER = "web_file_user";
			public static final String WEB_FILE_PASSWORD = "web_file_password";
	// End Preference Keys


	// The Constructors
	public Preferences() {}
	

	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}

	public void startSetup(AttributeList atts) {}
	
	public void endSetup(AttributeList atts) {
		System.out.println("Loading Config...");
		Outliner.loadPrefsFile(Outliner.PARSER, Outliner.CONFIG_FILE);
		System.out.println("Done Loading Config.");
		System.out.println("");
		
		System.out.println("Loading Recent File List...");
		Outliner.loadPrefsFile(Outliner.PARSER, Outliner.RECENT_FILES_FILE);
		System.out.println("Done Loading Recent File List.");
		System.out.println("");

		
		// Sync the GUI to the prefs
		PreferencesPanelEditor pEditor = (PreferencesPanelEditor) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_PANEL_EDITOR);
		pEditor.setToCurrent();

		PreferencesPanelOpenAndSave pOpenAndSave = (PreferencesPanelOpenAndSave) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_PANEL_OPEN_AND_SAVE);
		pOpenAndSave.setToCurrent();

		PreferencesPanelLookAndFeel pLookAndFeel = (PreferencesPanelLookAndFeel) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_PANEL_LOOK_AND_FEEL);
		pLookAndFeel.setToCurrent();

		PreferencesPanelMisc pMisc = (PreferencesPanelMisc) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_PANEL_MISC);
		pMisc.setToCurrent();
	}
	
	
	// PreferencesPanel Registry
	private HashMap prefsPanelReg = new HashMap();

	public void addPreferencesPanel(String key, PreferencesPanel prefPanel) {
		prefsPanelReg.put(key, prefPanel);
	}
	
	public PreferencesPanel getPreferencesPanel(String key) {
		return (PreferencesPanel) prefsPanelReg.get(key);
	}
	
	public Iterator getPreferencesPanelKeys() {
		return prefsPanelReg.keySet().iterator();
	}

	
	// Preferences Registry
	private HashMap prefsReg = new HashMap();
	
	public void addPreference(String key, Preference pref) {
		prefsReg.put(key, pref);
	}
	
	public Preference getPreference(String key) {
		return (Preference) prefsReg.get(key);
	}
	
	public Iterator getPreferenceKeys() {
		return prefsReg.keySet().iterator();
	}
	
	
	// Static Registry Accessors
	public static PreferenceBoolean getPreferenceBoolean(String key) {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		return (PreferenceBoolean) prefs.getPreference(key);
	}

	public static PreferenceInt getPreferenceInt(String key) {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		return (PreferenceInt) prefs.getPreference(key);
	}

	public static PreferenceString getPreferenceString(String key) {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		return (PreferenceString) prefs.getPreference(key);
	}

	public static PreferenceColor getPreferenceColor(String key) {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		return (PreferenceColor) prefs.getPreference(key);
	}

	public static PreferenceLineEnding getPreferenceLineEnding(String key) {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		return (PreferenceLineEnding) prefs.getPreference(key);
	}
	
		
	// Syncing Preferences	
	public static void restoreCurrentToDefault() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		Iterator it = prefs.getPreferenceKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Preference pref = prefs.getPreference(key);
			pref.restoreCurrentToDefault();
		}
	}

	public static void restoreTemporaryToDefault() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		Iterator it = prefs.getPreferenceKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Preference pref = prefs.getPreference(key);
			pref.restoreTemporaryToDefault();
		}
	}

	public static void restoreTemporaryToCurrent() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		Iterator it = prefs.getPreferenceKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Preference pref = prefs.getPreference(key);
			pref.restoreTemporaryToCurrent();
		}
	}

	public static void applyTemporaryToCurrent() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);

		Iterator it = prefs.getPreferenceKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Preference pref = prefs.getPreference(key);
			pref.applyTemporaryToCurrent();
		}
				
		it = prefs.getPreferencesPanelKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			PreferencesPanel prefPanel = prefs.getPreferencesPanel(key);
			prefPanel.applyTemporaryToCurrent();
		}
	}


	// Saving Config File
	public static void saveConfigFile(String filename) {
		try {
			FileWriter fw = new FileWriter(filename);
			fw.write(prepareConfigFile());
			fw.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not save preferences because of: " + e);
		}
	}

	private static String prepareConfigFile() {
		StringBuffer buffer = new StringBuffer();

		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		Iterator it = prefs.getPreferenceKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Preference pref = prefs.getPreference(key);
			buffer.append(Outliner.COMMAND_SET);
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(pref.getCommand());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(StringTools.escape(pref.toString(), '\\', null));
			buffer.append(LINE_END_DEFAULT);
		}

		return buffer.toString();
	}


	// Line Ending and Platform conversions
	public static String platformToLineEnding(String platform) {
		if (platform.equals(PLATFORM_MAC)) {
			return LINE_END_MAC;
		} else if (platform.equals(PLATFORM_WIN)) {
			return LINE_END_WIN;
		} else if (platform.equals(PLATFORM_UNIX)) {
			return LINE_END_UNIX;
		} else {
			return LINE_END_DEFAULT;
		}
	}

	public static String lineEndingToPlatform(String line_ending) {
		if (line_ending.equals(LINE_END_MAC)) {
			return PLATFORM_MAC;
		} else if (line_ending.equals(LINE_END_WIN)) {
			return PLATFORM_WIN;
		} else if (line_ending.equals(LINE_END_UNIX)) {
			return PLATFORM_UNIX;
		} else {
			System.out.println("Unknown line ending: " + line_ending);
			return "UNKNOWN";
		}
	}
}
