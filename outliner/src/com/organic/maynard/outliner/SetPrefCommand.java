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

import com.organic.maynard.util.*;
import java.util.*;

public class SetPrefCommand extends Command {
	
	public static final String COMMAND_RECENT_FILE = "recent_file";
	public static final String COMMAND_ENCODING = "encoding";

	// Setup Validators
	protected static final IntRangeValidator INDENT_VALIDATOR = new IntRangeValidator(1, 99, Preferences.INDENT_DEFAULT);	
	protected static final IntRangeValidator VERTICAL_SPACING_VALIDATOR = new IntRangeValidator(0, 99, Preferences.VERTICAL_SPACING_DEFAULT);	
	protected static final IntRangeValidator LEFT_MARGIN_VALIDATOR = new IntRangeValidator(0, 99, Preferences.LEFT_MARGIN_DEFAULT);	
	protected static final IntRangeValidator RIGHT_MARGIN_VALIDATOR = new IntRangeValidator(0, 99, Preferences.RIGHT_MARGIN_DEFAULT);	
	protected static final IntRangeValidator TOP_MARGIN_VALIDATOR = new IntRangeValidator(0, 99, Preferences.TOP_MARGIN_DEFAULT);	
	protected static final IntRangeValidator BOTTOM_MARGIN_VALIDATOR = new IntRangeValidator(0, 99, Preferences.BOTTOM_MARGIN_DEFAULT);	
	protected static final IntRangeValidator UNDO_QUEUE_SIZE_VALIDATOR = new IntRangeValidator(0, Integer.MAX_VALUE, Preferences.UNDO_QUEUE_SIZE_DEFAULT);	
	protected static final IntRangeValidator RECENT_FILES_LIST_SIZE_VALIDATOR = new IntRangeValidator(0, 99, Preferences.RECENT_FILES_LIST_SIZE_DEFAULT);	
	protected static final IntRangeValidator FONT_SIZE_VALIDATOR = new IntRangeValidator(6, 36, Preferences.FONT_SIZE_DEFAULT);	

	protected static final BooleanValidator BOOLEAN_VALIDATOR = new BooleanValidator();	


	// The Constructors
	public SetPrefCommand(String name, int numOfArgs) {
		super(name, numOfArgs);
	}

	public synchronized void execute(Vector signature) {
		String variableName = (String) signature.elementAt(1);

		if (variableName.equals(Preferences.DESKTOP_BACKGROUND_COLOR.getCommand())) {
			setColorPref(signature, Preferences.DESKTOP_BACKGROUND_COLOR);
		
		} else if (variableName.equals(Preferences.PANEL_BACKGROUND_COLOR.getCommand())) {
			setColorPref(signature, Preferences.PANEL_BACKGROUND_COLOR);

		} else if (variableName.equals(Preferences.TEXTAREA_BACKGROUND_COLOR.getCommand())) {
			setColorPref(signature, Preferences.TEXTAREA_BACKGROUND_COLOR);

		} else if (variableName.equals(Preferences.TEXTAREA_FOREGROUND_COLOR.getCommand())) {
			setColorPref(signature, Preferences.TEXTAREA_FOREGROUND_COLOR);

		} else if (variableName.equals(Preferences.SELECTED_CHILD_COLOR.getCommand())) {
			setColorPref(signature, Preferences.SELECTED_CHILD_COLOR);

		} else if (variableName.equals(Preferences.LINE_NUMBER_COLOR.getCommand())) {
			setColorPref(signature, Preferences.LINE_NUMBER_COLOR);

		} else if (variableName.equals(Preferences.LINE_NUMBER_SELECTED_COLOR.getCommand())) {
			setColorPref(signature, Preferences.LINE_NUMBER_SELECTED_COLOR);

		} else if (variableName.equals(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR.getCommand())) {
			setColorPref(signature, Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR);

		} else if (variableName.equals(Preferences.PRINT_ENVIRONMENT.getCommand())) {
			setBooleanPref(signature, Preferences.PRINT_ENVIRONMENT, BOOLEAN_VALIDATOR);

		} else if (variableName.equals(Preferences.SHOW_LINE_NUMBERS.getCommand())) {
			setBooleanPref(signature, Preferences.SHOW_LINE_NUMBERS, BOOLEAN_VALIDATOR);

		} else if (variableName.equals(Preferences.IS_MAXIMIZED.getCommand())) {
			setBooleanPref(signature, Preferences.IS_MAXIMIZED, BOOLEAN_VALIDATOR);

		} else if (variableName.equals(Preferences.NEW_DOC_ON_STARTUP.getCommand())) {
			setBooleanPref(signature, Preferences.NEW_DOC_ON_STARTUP, BOOLEAN_VALIDATOR);

		} else if (variableName.equals(Preferences.INDENT.getCommand())) {
			setIntPref(signature, Preferences.INDENT, INDENT_VALIDATOR);

		} else if (variableName.equals(Preferences.VERTICAL_SPACING.getCommand())) {
			setIntPref(signature, Preferences.VERTICAL_SPACING, VERTICAL_SPACING_VALIDATOR);

		} else if (variableName.equals(Preferences.LEFT_MARGIN.getCommand())) {
			setIntPref(signature, Preferences.LEFT_MARGIN, LEFT_MARGIN_VALIDATOR);

		} else if (variableName.equals(Preferences.TOP_MARGIN.getCommand())) {
			setIntPref(signature, Preferences.TOP_MARGIN, TOP_MARGIN_VALIDATOR);

		} else if (variableName.equals(Preferences.RIGHT_MARGIN.getCommand())) {
			setIntPref(signature, Preferences.RIGHT_MARGIN, RIGHT_MARGIN_VALIDATOR);

		} else if (variableName.equals(Preferences.BOTTOM_MARGIN.getCommand())) {
			setIntPref(signature, Preferences.BOTTOM_MARGIN, BOTTOM_MARGIN_VALIDATOR);

		} else if (variableName.equals(Preferences.UNDO_QUEUE_SIZE.getCommand())) {
			setIntPref(signature, Preferences.UNDO_QUEUE_SIZE, UNDO_QUEUE_SIZE_VALIDATOR);

		} else if (variableName.equals(Preferences.RECENT_FILES_LIST_SIZE.getCommand())) {
			setIntPref(signature, Preferences.RECENT_FILES_LIST_SIZE, RECENT_FILES_LIST_SIZE_VALIDATOR);
		
		} else if (variableName.equals(Preferences.FONT_SIZE.getCommand())) {
			setIntPref(signature, Preferences.FONT_SIZE, FONT_SIZE_VALIDATOR);
		
		} else if (variableName.equals(Preferences.FONT_FACE.getCommand())) {
			setStringPref(signature, Preferences.FONT_FACE);

		} else if (variableName.equals(Preferences.LINE_WRAP.getCommand())) {
			setStringPref(signature, Preferences.LINE_WRAP);
		
		} else if (variableName.equals(Preferences.LINE_END.getCommand())) {
			setStringPref(signature, Preferences.LINE_END);
		
		} else if (variableName.equals(Preferences.OPEN_ENCODING.getCommand())) {
			setStringPref(signature, Preferences.OPEN_ENCODING);
		
		} else if (variableName.equals(Preferences.SAVE_ENCODING.getCommand())) {
			setStringPref(signature, Preferences.SAVE_ENCODING);

		} else if (variableName.equals(Preferences.OPEN_FORMAT.getCommand())) {
			setStringPref(signature, Preferences.OPEN_FORMAT);
		
		} else if (variableName.equals(Preferences.SAVE_FORMAT.getCommand())) {
			setStringPref(signature, Preferences.SAVE_FORMAT);

		} else if (variableName.equals(Preferences.MOST_RECENT_OPEN_DIR.getCommand())) {
			setStringPref(signature, Preferences.MOST_RECENT_OPEN_DIR);

		} else if (variableName.equals(Preferences.MOST_RECENT_SAVE_DIR.getCommand())) {
			setStringPref(signature, Preferences.MOST_RECENT_SAVE_DIR);

		} else if (variableName.equals(Preferences.OWNER_NAME.getCommand())) {
			setStringPref(signature, Preferences.OWNER_NAME);

		} else if (variableName.equals(Preferences.OWNER_EMAIL.getCommand())) {
			setStringPref(signature, Preferences.OWNER_EMAIL);

		} else if (variableName.equals(Preferences.TIME_ZONE_FOR_SAVING_DATES.getCommand())) {
			setStringPref(signature, Preferences.TIME_ZONE_FOR_SAVING_DATES);
		
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
						
						(String) signature.elementAt(17),
						(String) signature.elementAt(18)
					);
					RecentFilesList.docInfoList.addElement(docInfo);
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Error Loading Recent File List Item, ArrayIndexOutOfBoundsException: " + e.getMessage());
				}
			}

		} else if (variableName.equals(COMMAND_ENCODING)) {
			Preferences.ENCODINGS.addElement((String) signature.elementAt(2));
		}
	}
	
	
	// Utility Methods
	private void setIntPref(Vector signature, PreferenceInt pref, IntRangeValidator validator) {
		try {
			pref.cur = validator.getValidValue((String) signature.elementAt(2)).intValue();
			pref.restoreTemporaryToCurrent();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error Setting int Preference, ArrayIndexOutOfBoundsException: " + e.getMessage());
		}
	}

	private void setBooleanPref(Vector signature, PreferenceBoolean pref, BooleanValidator validator) {
		try {
			pref.cur = validator.getValidValue((String) signature.elementAt(2));
			pref.restoreTemporaryToCurrent();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error Setting Boolean Preference, ArrayIndexOutOfBoundsException: " + e.getMessage());
		}
	}

	private void setColorPref(Vector signature, PreferenceColor pref) {
		try {
			pref.cur = PreferenceColor.parseColor((String) signature.elementAt(2));
			pref.restoreTemporaryToCurrent();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error Setting Color Preference, ArrayIndexOutOfBoundsException: " + e.getMessage());
		}
	}

	private void setStringPref(Vector signature, PreferenceString pref) {
		try {
			pref.cur = (String) signature.elementAt(2);
			pref.restoreTemporaryToCurrent();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error Setting String Preference, ArrayIndexOutOfBoundsException: " + e.getMessage());
		}
	}
}