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

public class LoadFileProtocolClassCommand extends Command {
	// The Constructors
	public LoadFileProtocolClassCommand(String name, int numOfArgs) {
		super(name,numOfArgs);
	}

	public synchronized void execute(Vector signature) {
		String className = null;
		String protocolName = null;

		try {
			className = (String) signature.elementAt(1);
			protocolName = (String) signature.elementAt(2);
		} catch (ArrayIndexOutOfBoundsException e) {}
		
		Outliner.fileProtocolManager.createFileProtocol(protocolName, className);
	}
}