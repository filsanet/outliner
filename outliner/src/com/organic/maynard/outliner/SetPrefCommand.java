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
import java.util.*;
import java.io.*;

import com.organic.maynard.util.*;

public class SetPrefCommand extends Command {
	
	public static final String COMMAND_RECENT_FILE = "recent_file";
	public static final String COMMAND_ENCODING = "encoding";
	
	public Outliner outliner = null;

	// Setup Validators
	public static final IntRangeValidator INDENT_VALIDATOR = new IntRangeValidator(5, 99, Preferences.INDENT_DEFAULT);	
	public static final IntRangeValidator VERTICAL_SPACING_VALIDATOR = new IntRangeValidator(0, 99, Preferences.VERTICAL_SPACING_DEFAULT);	
	public static final IntRangeValidator LEFT_MARGIN_VALIDATOR = new IntRangeValidator(0, 99, Preferences.LEFT_MARGIN_DEFAULT);	
	public static final IntRangeValidator RIGHT_MARGIN_VALIDATOR = new IntRangeValidator(0, 99, Preferences.RIGHT_MARGIN_DEFAULT);	
	public static final IntRangeValidator TOP_MARGIN_VALIDATOR = new IntRangeValidator(0, 99, Preferences.TOP_MARGIN_DEFAULT);	
	public static final IntRangeValidator BOTTOM_MARGIN_VALIDATOR = new IntRangeValidator(0, 99, Preferences.BOTTOM_MARGIN_DEFAULT);	
	public static final IntRangeValidator UNDO_QUEUE_SIZE_VALIDATOR = new IntRangeValidator(0, Integer.MAX_VALUE, Preferences.UNDO_QUEUE_SIZE_DEFAULT);	
	public static final IntRangeValidator RECENT_FILES_LIST_SIZE_VALIDATOR = new IntRangeValidator(0, 99, Preferences.RECENT_FILES_LIST_SIZE_DEFAULT);	
	public static final IntRangeValidator FONT_SIZE_VALIDATOR = new IntRangeValidator(8, 36, Preferences.FONT_SIZE_DEFAULT);	

	public static final BooleanValidator BOOLEAN_VALIDATOR = new BooleanValidator();	
	
	// The Constructors
	public SetPrefCommand(String name, int numOfArgs, Outliner outliner) {
		super(name,numOfArgs);
		this.outliner = outliner;
	}

	public synchronized void execute(Vector signature) {
		String variableName = (String) signature.elementAt(1);

		if (variableName.equals(Preferences.DESKTOP_BACKGROUND_COLOR.getCommand())) {
			Preferences.DESKTOP_BACKGROUND_COLOR.cur = parseColor((String) signature.elementAt(2));
			Preferences.DESKTOP_BACKGROUND_COLOR.restoreTemporaryToCurrent();
		
		} else if (variableName.equals(Preferences.PANEL_BACKGROUND_COLOR.getCommand())) {
			Preferences.PANEL_BACKGROUND_COLOR.cur = parseColor((String) signature.elementAt(2));
			Preferences.PANEL_BACKGROUND_COLOR.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.TEXTAREA_BACKGROUND_COLOR.getCommand())) {
			Preferences.TEXTAREA_BACKGROUND_COLOR.cur = parseColor((String) signature.elementAt(2));
			Preferences.TEXTAREA_BACKGROUND_COLOR.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.TEXTAREA_FOREGROUND_COLOR.getCommand())) {
			Preferences.TEXTAREA_FOREGROUND_COLOR.cur = parseColor((String) signature.elementAt(2));
			Preferences.TEXTAREA_FOREGROUND_COLOR.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.SELECTED_CHILD_COLOR.getCommand())) {
			Preferences.SELECTED_CHILD_COLOR.cur = parseColor((String) signature.elementAt(2));
			Preferences.SELECTED_CHILD_COLOR.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.PRINT_ENVIRONMENT.getCommand())) {
			Preferences.PRINT_ENVIRONMENT.cur = BOOLEAN_VALIDATOR.getValidValue((String) signature.elementAt(2));
			Preferences.PRINT_ENVIRONMENT.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.IS_MAXIMIZED.getCommand())) {
			Preferences.IS_MAXIMIZED.cur = BOOLEAN_VALIDATOR.getValidValue((String) signature.elementAt(2));
			Preferences.IS_MAXIMIZED.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.NEW_DOC_ON_STARTUP.getCommand())) {
			Preferences.NEW_DOC_ON_STARTUP.cur = BOOLEAN_VALIDATOR.getValidValue((String) signature.elementAt(2));
			Preferences.NEW_DOC_ON_STARTUP.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.INDENT.getCommand())) {
			Preferences.INDENT.cur = INDENT_VALIDATOR.getValidValue((String) signature.elementAt(2)).intValue();
			Preferences.INDENT.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.VERTICAL_SPACING.getCommand())) {
			Preferences.VERTICAL_SPACING.cur = VERTICAL_SPACING_VALIDATOR.getValidValue((String) signature.elementAt(2)).intValue();
			Preferences.VERTICAL_SPACING.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.LEFT_MARGIN.getCommand())) {
			Preferences.LEFT_MARGIN.cur = LEFT_MARGIN_VALIDATOR.getValidValue((String) signature.elementAt(2)).intValue();
			Preferences.LEFT_MARGIN.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.TOP_MARGIN.getCommand())) {
			Preferences.TOP_MARGIN.cur = TOP_MARGIN_VALIDATOR.getValidValue((String) signature.elementAt(2)).intValue();
			Preferences.TOP_MARGIN.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.RIGHT_MARGIN.getCommand())) {
			Preferences.RIGHT_MARGIN.cur = RIGHT_MARGIN_VALIDATOR.getValidValue((String) signature.elementAt(2)).intValue();
			Preferences.RIGHT_MARGIN.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.BOTTOM_MARGIN.getCommand())) {
			Preferences.BOTTOM_MARGIN.cur = BOTTOM_MARGIN_VALIDATOR.getValidValue((String) signature.elementAt(2)).intValue();
			Preferences.BOTTOM_MARGIN.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.UNDO_QUEUE_SIZE.getCommand())) {
			Preferences.UNDO_QUEUE_SIZE.cur = UNDO_QUEUE_SIZE_VALIDATOR.getValidValue((String) signature.elementAt(2)).intValue();
			Preferences.UNDO_QUEUE_SIZE.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.RECENT_FILES_LIST_SIZE.getCommand())) {
			Preferences.RECENT_FILES_LIST_SIZE.cur = RECENT_FILES_LIST_SIZE_VALIDATOR.getValidValue((String) signature.elementAt(2)).intValue();
			Preferences.RECENT_FILES_LIST_SIZE.restoreTemporaryToCurrent();
		
		} else if (variableName.equals(Preferences.FONT_SIZE.getCommand())) {
			Preferences.FONT_SIZE.cur = FONT_SIZE_VALIDATOR.getValidValue((String) signature.elementAt(2)).intValue();
			Preferences.FONT_SIZE.restoreTemporaryToCurrent();
		
		} else if (variableName.equals(Preferences.FONT_FACE.getCommand())) {
			Preferences.FONT_FACE.cur = (String) signature.elementAt(2);
			Preferences.FONT_FACE.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.LINE_WRAP.getCommand())) {
			Preferences.LINE_WRAP.cur = (String) signature.elementAt(2);
			Preferences.LINE_WRAP.restoreTemporaryToCurrent();
		
		} else if (variableName.equals(Preferences.LINE_END.getCommand())) {
			Preferences.LINE_END.cur = (String) signature.elementAt(2);
			Preferences.LINE_END.restoreTemporaryToCurrent();
		
		} else if (variableName.equals(Preferences.OPEN_ENCODING.getCommand())) {
			Preferences.OPEN_ENCODING.cur = (String) signature.elementAt(2);
			Preferences.OPEN_ENCODING.restoreTemporaryToCurrent();
		
		} else if (variableName.equals(Preferences.SAVE_ENCODING.getCommand())) {
			Preferences.SAVE_ENCODING.cur = (String) signature.elementAt(2);
			Preferences.SAVE_ENCODING.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.OPEN_FORMAT.getCommand())) {
			Preferences.OPEN_FORMAT.cur = (String) signature.elementAt(2);
			Preferences.OPEN_FORMAT.restoreTemporaryToCurrent();
		
		} else if (variableName.equals(Preferences.SAVE_FORMAT.getCommand())) {
			Preferences.SAVE_FORMAT.cur = (String) signature.elementAt(2);
			Preferences.SAVE_FORMAT.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.MOST_RECENT_OPEN_DIR.getCommand())) {
			Preferences.MOST_RECENT_OPEN_DIR.cur = (String) signature.elementAt(2);
			Preferences.MOST_RECENT_OPEN_DIR.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.MOST_RECENT_SAVE_DIR.getCommand())) {
			Preferences.MOST_RECENT_SAVE_DIR.cur = (String) signature.elementAt(2);
			Preferences.MOST_RECENT_SAVE_DIR.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.OWNER_NAME.getCommand())) {
			Preferences.OWNER_NAME.cur = (String) signature.elementAt(2);
			Preferences.OWNER_NAME.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.OWNER_EMAIL.getCommand())) {
			Preferences.OWNER_EMAIL.cur = (String) signature.elementAt(2);
			Preferences.OWNER_EMAIL.restoreTemporaryToCurrent();

		} else if (variableName.equals(Preferences.TIME_ZONE_FOR_SAVING_DATES.getCommand())) {
			Preferences.TIME_ZONE_FOR_SAVING_DATES.cur = (String) signature.elementAt(2);
			Preferences.TIME_ZONE_FOR_SAVING_DATES.restoreTemporaryToCurrent();
		
		} else if (variableName.equals(COMMAND_RECENT_FILE)) {
			if (RecentFilesList.docInfoList.size() < Preferences.RECENT_FILES_LIST_SIZE.cur) {
				try {
					// Create a new DocumentInfo object
					DocumentInfo docInfo = new DocumentInfo(
						(String) signature.elementAt(2),
						(String) signature.elementAt(3),
						(String) signature.elementAt(4),
						(String) signature.elementAt(5),
						(String) signature.elementAt(6),
						(String) signature.elementAt(7),
						(String) signature.elementAt(8),
						(String) signature.elementAt(9),
						(String) signature.elementAt(10),
						(String) signature.elementAt(11),
						
						Integer.parseInt((String) signature.elementAt(12)),
						Integer.parseInt((String) signature.elementAt(13)),
						Integer.parseInt((String) signature.elementAt(14)),
						Integer.parseInt((String) signature.elementAt(15)),
						Integer.parseInt((String) signature.elementAt(16)),
						
						(String) signature.elementAt(17)
					);
					RecentFilesList.docInfoList.addElement(docInfo);
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
		} else if (variableName.equals(COMMAND_ENCODING)) {
			Preferences.ENCODINGS.addElement((String) signature.elementAt(2));
		}
	}
	
	private void setStringPref(Vector signature, PreferenceString pref) {
		try {
			pref.cur = (String) signature.elementAt(2);
			pref.restoreTemporaryToCurrent();
		} catch (ArrayIndexOutOfBoundsException e) {}
	}
		
	// Will go away when color is configured in the gui.
	private static final Color parseColor(String rgb) {
		// Split it into 3 ints
		int[] values = {0,0,0};
		int count = 0;
		StringTokenizer tokenizer = new StringTokenizer(rgb,",");
		while (tokenizer.hasMoreTokens()) {
			try {
				int value = Integer.parseInt(tokenizer.nextToken());
				if (value > 255) {value = 255;}
				if (value < 0) {value = 0;}
				values[count] = value;
			} catch (ArrayIndexOutOfBoundsException aiobe) {
				// Do Nothing
			} catch (Exception e) {
				values[count] = 0;				
			}
			count++;
		}
		return new Color(values[0],values[1],values[2]);
	}
}