/**
 * Copyright (C) 2001 Maynard Demmon, maynard@organic.com
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
import com.organic.maynard.util.string.StringTools;

public class LoadFileFormatClassCommand extends Command {
	// Constants
	private char[] DELIMITERS = {' ','\t'};
	
	// The Constructors
	public LoadFileFormatClassCommand(String name, int numOfArgs) {
		super(name,numOfArgs);
	}

	public synchronized void execute(Vector signature) {
		String formatType = null;
		String className = null;
		String formatName = null;
		Vector extensions = null;

		try {
			formatType = (String) signature.elementAt(1);
			className = (String) signature.elementAt(2);
			formatName = (String) signature.elementAt(3);
			String extStr = (String) signature.elementAt(4);
			
			extensions = StringTools.split(extStr, '\\', DELIMITERS);
		} catch (ArrayIndexOutOfBoundsException e) {
			// Say nothing since this will happen during normal operation since not all 
			// formats will have extensions set.
		}
		
		Outliner.fileFormatManager.createFileFormat(formatType, formatName, className, extensions);
	}
}