/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
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

import com.organic.maynard.util.*;
import java.util.*;

public class SetPrefCommand extends Command {
	
	// Constants
	public static final String COMMAND_ENCODING = "encoding";


	// The Constructors
	public SetPrefCommand(String name, int numOfArgs) {
		super(name, numOfArgs);
	}


	public synchronized void execute(Vector signature) {
		String variableName = (String) signature.elementAt(1);
		
		if (variableName.equals(COMMAND_ENCODING)) {
			Preferences.ENCODINGS.add((String) signature.elementAt(2));
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