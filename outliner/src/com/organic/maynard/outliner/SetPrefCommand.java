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
	
	// Constants
	public static final String COMMAND_RECENT_FILE = "recent_file";
	public static final String COMMAND_ENCODING = "encoding";


	// The Constructors
	public SetPrefCommand(String name, int numOfArgs) {
		super(name, numOfArgs);
	}


	public synchronized void execute(Vector signature) {
		String variableName = (String) signature.elementAt(1);
		
		if (variableName.equals(COMMAND_RECENT_FILE)) {
			Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
			PreferenceInt pRecentFilesListSize = (PreferenceInt) prefs.getPreference(Preferences.RECENT_FILES_LIST_SIZE);
			if (RecentFilesList.docInfoList.size() < pRecentFilesListSize.cur) {
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
					System.out.println("  Adding recent file: " + signature.elementAt(6));
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Error Loading Recent File List Item, ArrayIndexOutOfBoundsException: " + e.getMessage());
				}
			}

		} else if (variableName.equals(COMMAND_ENCODING)) {
			Preferences.ENCODINGS.addElement((String) signature.elementAt(2));
			System.out.println("  Adding encoding type: " + signature.elementAt(2));
			
		} else {
			Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
			Preference pref = prefs.getPreference(variableName);

			try {
				pref.setCur((String) signature.elementAt(2));
				pref.restoreTemporaryToCurrent();
				System.out.println("  Setting Pref: " + variableName + " : " + signature.elementAt(2));
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Error Setting Preference, ArrayIndexOutOfBoundsException: " + e.getMessage());
			} catch (NullPointerException e) {
				System.out.println("Error Setting Preference, NullPointerException: " + e.getMessage());
			}
		}
	}
}