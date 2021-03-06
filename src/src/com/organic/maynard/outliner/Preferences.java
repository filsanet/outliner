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

import com.organic.maynard.util.string.*;

public class Preferences {

	// The Constructors
	public Preferences() {}
	

	// Static Fields (Global Preferences) These are saved.
	public static final Preference[] pref_list = new Preference[22];

	public static final String DEPTH_PAD_STRING = "\t";
	public static final String LINE_END_STRING = "\n";

	public static final String LINE_END_MAC = "\r";
	public static final String LINE_END_WIN = "\r\n";
	public static final String LINE_END_UNIX = "\n";

	public static final String PLATFORM_MAC = "Mac";
	public static final String PLATFORM_WIN = "Windows";
	public static final String PLATFORM_UNIX = "Unix";
	
	public static final String[] PLATFORM_IDENTIFIERS = {PLATFORM_MAC,PLATFORM_WIN,PLATFORM_UNIX};

	public static final Vector ENCODINGS = new Vector();
	
	public static final String TXT_WORDS = "words";
	public static final String TXT_CHARACTERS = "characters";
	
	// Default Values
	public static final boolean IS_MAXIMIZED_DEFAULT = false;
	public static final boolean PRINT_ENVIRONMENT_DEFAULT = false;
	public static final boolean NEW_DOC_ON_STARTUP_DEFAULT = true;
	public static final Color DESKTOP_BACKGROUND_COLOR_DEFAULT = new Color(90,90,90);
	public static final Color PANEL_BACKGROUND_COLOR_DEFAULT = new Color(255,255,255);
	public static final Color TEXTAREA_BACKGROUND_COLOR_DEFAULT = new Color(255,255,255);
	public static final Color TEXTAREA_FOREGROUND_COLOR_DEFAULT = new Color(0,0,0);
	public static final Color SELECTED_CHILD_COLOR_DEFAULT = new Color(127,127,127);
	public static final int INDENT_DEFAULT = 15;
	public static final int VERTICAL_SPACING_DEFAULT = 2;
	public static final int LEFT_MARGIN_DEFAULT = 5;
	public static final int TOP_MARGIN_DEFAULT = 5;
	public static final int RIGHT_MARGIN_DEFAULT = 5;
	public static final int BOTTOM_MARGIN_DEFAULT = 5;
	public static final int UNDO_QUEUE_SIZE_DEFAULT = 100;
	public static final int RECENT_FILES_LIST_SIZE_DEFAULT = 15;
	public static final int FONT_SIZE_DEFAULT = 12;
	public static final String FONT_FACE_DEFAULT = "SansSerif";
	public static final String LINE_WRAP_DEFAULT = TXT_WORDS;

	public static final String LINE_END_DEFAULT = System.getProperty("line.separator");
	public static final String OPEN_ENCODING_DEFAULT = "ISO-8859-1";
	public static final String SAVE_ENCODING_DEFAULT = "ISO-8859-1";

	
	// Editing Settings
	public static final PreferenceInt UNDO_QUEUE_SIZE = new PreferenceInt(UNDO_QUEUE_SIZE_DEFAULT,UNDO_QUEUE_SIZE_DEFAULT,"undo_queue_size",SetPrefCommand.UNDO_QUEUE_SIZE_VALIDATOR);
	
	// Look and Feel Settings
		// Color
		public static final PreferenceColor DESKTOP_BACKGROUND_COLOR = new PreferenceColor(DESKTOP_BACKGROUND_COLOR_DEFAULT,DESKTOP_BACKGROUND_COLOR_DEFAULT,"desktop_background_color");
		public static final PreferenceColor PANEL_BACKGROUND_COLOR = new PreferenceColor(PANEL_BACKGROUND_COLOR_DEFAULT,PANEL_BACKGROUND_COLOR_DEFAULT,"panel_background_color");
		public static final PreferenceColor TEXTAREA_BACKGROUND_COLOR = new PreferenceColor(TEXTAREA_BACKGROUND_COLOR_DEFAULT,TEXTAREA_BACKGROUND_COLOR_DEFAULT,"textarea_background_color");
		public static final PreferenceColor TEXTAREA_FOREGROUND_COLOR = new PreferenceColor(TEXTAREA_FOREGROUND_COLOR_DEFAULT,TEXTAREA_FOREGROUND_COLOR_DEFAULT,"textarea_foreground_color");
		public static final PreferenceColor SELECTED_CHILD_COLOR = new PreferenceColor(SELECTED_CHILD_COLOR_DEFAULT,SELECTED_CHILD_COLOR_DEFAULT,"selected_child_color");
		
		// Spatial
		public static final PreferenceInt INDENT = new PreferenceInt(INDENT_DEFAULT,15,"indent",SetPrefCommand.INDENT_VALIDATOR);
		public static final PreferenceInt VERTICAL_SPACING = new PreferenceInt(VERTICAL_SPACING_DEFAULT,VERTICAL_SPACING_DEFAULT,"vertical_spacing",SetPrefCommand.VERTICAL_SPACING_VALIDATOR);
		public static final PreferenceInt LEFT_MARGIN = new PreferenceInt(LEFT_MARGIN_DEFAULT,LEFT_MARGIN_DEFAULT,"left_margin",SetPrefCommand.LEFT_MARGIN_VALIDATOR);
		public static final PreferenceInt TOP_MARGIN = new PreferenceInt(TOP_MARGIN_DEFAULT,TOP_MARGIN_DEFAULT,"top_margin",SetPrefCommand.TOP_MARGIN_VALIDATOR);
		public static final PreferenceInt RIGHT_MARGIN = new PreferenceInt(RIGHT_MARGIN_DEFAULT,RIGHT_MARGIN_DEFAULT,"right_margin",SetPrefCommand.RIGHT_MARGIN_VALIDATOR);
		public static final PreferenceInt BOTTOM_MARGIN = new PreferenceInt(BOTTOM_MARGIN_DEFAULT,BOTTOM_MARGIN_DEFAULT,"bottom_margin",SetPrefCommand.BOTTOM_MARGIN_VALIDATOR);

		// Fonts
		public static final PreferenceString FONT_FACE = new PreferenceString(FONT_FACE_DEFAULT,FONT_FACE_DEFAULT,"font_face");
		public static final PreferenceInt FONT_SIZE = new PreferenceInt(FONT_SIZE_DEFAULT,FONT_SIZE_DEFAULT,"font_size",SetPrefCommand.FONT_SIZE_VALIDATOR);
		public static final PreferenceString LINE_WRAP = new PreferenceString(LINE_WRAP_DEFAULT,LINE_WRAP_DEFAULT,"line_wrap");
		
	// Open and Save Settings
		public static final PreferenceString LINE_END = new PreferenceString(lineEndingToPlatform(LINE_END_DEFAULT),lineEndingToPlatform(LINE_END_DEFAULT),"line_end");
		public static final PreferenceString OPEN_ENCODING = new PreferenceString(OPEN_ENCODING_DEFAULT,OPEN_ENCODING_DEFAULT,"open_encoding");
		public static final PreferenceString SAVE_ENCODING = new PreferenceString(SAVE_ENCODING_DEFAULT,SAVE_ENCODING_DEFAULT,"save_encoding");
		public static final PreferenceBoolean IS_MAXIMIZED = new PreferenceBoolean(IS_MAXIMIZED_DEFAULT,false,"is_maximized");
		
	// Misc Settings
	public static final PreferenceBoolean PRINT_ENVIRONMENT = new PreferenceBoolean(PRINT_ENVIRONMENT_DEFAULT,false,"print_environment");
	public static final PreferenceBoolean NEW_DOC_ON_STARTUP = new PreferenceBoolean(NEW_DOC_ON_STARTUP_DEFAULT,false,"new_doc_on_startup");
	public static final PreferenceInt RECENT_FILES_LIST_SIZE = new PreferenceInt(RECENT_FILES_LIST_SIZE_DEFAULT,RECENT_FILES_LIST_SIZE_DEFAULT,"recent_files_list_size",SetPrefCommand.RECENT_FILES_LIST_SIZE_VALIDATOR);

	static {
		pref_list[0] = PRINT_ENVIRONMENT;
		pref_list[1] = DESKTOP_BACKGROUND_COLOR;
		pref_list[2] = PANEL_BACKGROUND_COLOR;
		pref_list[3] = TEXTAREA_BACKGROUND_COLOR;
		pref_list[4] = TEXTAREA_FOREGROUND_COLOR;
		pref_list[5] = INDENT;
		pref_list[6] = VERTICAL_SPACING;
		pref_list[7] = LEFT_MARGIN;
		pref_list[8] = TOP_MARGIN;
		pref_list[9] = RIGHT_MARGIN;
		pref_list[10] = BOTTOM_MARGIN;
		pref_list[11] = UNDO_QUEUE_SIZE;
		pref_list[12] = RECENT_FILES_LIST_SIZE;	
		pref_list[13] = SELECTED_CHILD_COLOR;	
		pref_list[14] = FONT_SIZE;	
		pref_list[15] = FONT_FACE;	
		pref_list[16] = LINE_END;	
		pref_list[17] = OPEN_ENCODING;	
		pref_list[18] = SAVE_ENCODING;	
		pref_list[19] = LINE_WRAP;	
		pref_list[20] = NEW_DOC_ON_STARTUP;	
		pref_list[21] = IS_MAXIMIZED;	
	}
		
	// Static Methods	
	public static void restoreCurrentToDefault() {
		for (int i = 0; i < pref_list.length; i++) {
			pref_list[i].restoreCurrentToDefault();
		}
	}

	public static void restoreTemporaryToDefault() {
		for (int i = 0; i < pref_list.length; i++) {
			pref_list[i].restoreTemporaryToDefault();
		}
	}

	public static void restoreTemporaryToCurrent() {
		for (int i = 0; i < pref_list.length; i++) {
			pref_list[i].restoreTemporaryToCurrent();
		}
	}

	public static void applyTemporaryToCurrent() {
		// Update the undo queue for all the documents immediatly if it is being downsized.
		if (UNDO_QUEUE_SIZE.tmp < UNDO_QUEUE_SIZE.cur) {
			UNDO_QUEUE_SIZE.applyTemporaryToCurrent();
			for (int i = 0; i < Outliner.openDocumentCount(); i++) {
				Outliner.getDocument(i).undoQueue.trim();
			}
		}

		// Update the recent file list for all the documents immediatly if it is being downsized.
		if (RECENT_FILES_LIST_SIZE.tmp < RECENT_FILES_LIST_SIZE.cur) {
			RECENT_FILES_LIST_SIZE.applyTemporaryToCurrent();
			RecentFilesList.trim();
		}

		// Update the file modified status
		if (!LINE_END.tmp.equals(LINE_END.cur) || !SAVE_ENCODING.tmp.equals(SAVE_ENCODING.cur)) {
			for (int i = 0; i < Outliner.openDocumentCount(); i++) {
				OutlinerDocument doc = Outliner.getDocument(i);
				// Only update files that do not have overriding document settings.
				if (!doc.settings.useDocumentSettings) {
					doc.settings.lineEnd.def = LINE_END.tmp;
					doc.settings.lineEnd.cur = LINE_END.tmp;
					doc.settings.lineEnd.tmp = LINE_END.tmp;
					doc.settings.saveEncoding.def = SAVE_ENCODING.tmp;
					doc.settings.saveEncoding.cur = SAVE_ENCODING.tmp;
					doc.settings.saveEncoding.tmp = SAVE_ENCODING.tmp;
					//doc.setFileModified(true);
				}
			}
		}

		for (int i = 0; i < pref_list.length; i++) {
			pref_list[i].applyTemporaryToCurrent();
		}

		// Set the Desktop Background color
		Outliner.jsp.getViewport().setBackground(Preferences.DESKTOP_BACKGROUND_COLOR.cur);
		Outliner.desktop.setBackground(Preferences.DESKTOP_BACKGROUND_COLOR.cur);

		// Set the Panel Background color.
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			Outliner.getDocument(i).panel.setBackground(Preferences.PANEL_BACKGROUND_COLOR.cur);
		}

		Font font = new Font(Preferences.FONT_FACE.cur,Font.PLAIN,Preferences.FONT_SIZE.cur);

		boolean line_wrap = true;
		if (Preferences.LINE_WRAP.cur.equals(TXT_CHARACTERS)) {
			line_wrap = false;
		}

		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			OutlinerDocument doc = Outliner.getDocument(i);
			for (int j = 0; j < outlineLayoutManager.CACHE_SIZE; j++) {
				doc.panel.layout.textAreas[j].setFont(font);
				doc.panel.layout.textAreas[j].setSelectionColor(Preferences.TEXTAREA_FOREGROUND_COLOR.cur);
				doc.panel.layout.textAreas[j].setSelectedTextColor(Preferences.TEXTAREA_BACKGROUND_COLOR.cur);
				doc.panel.layout.textAreas[j].setWrapStyleWord(line_wrap);
			}
		}
	}

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
		for (int i = 0; i < pref_list.length; i++) {
			buffer.append(Outliner.COMMAND_SET + Outliner.COMMAND_PARSER_SEPARATOR + pref_list[i].getCommand() + Outliner.COMMAND_PARSER_SEPARATOR + pref_list[i].toString() + System.getProperty("line.separator"));
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