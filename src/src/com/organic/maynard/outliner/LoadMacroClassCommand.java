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

public class LoadMacroClassCommand extends Command {
	
	public Outliner outliner = null;
	
	// The Constructors
	public LoadMacroClassCommand(String name, int numOfArgs, Outliner outliner) {
		super(name,numOfArgs);
		this.outliner = outliner;
	}

	public synchronized void execute(Vector signature) {
		String className = (String) signature.elementAt(1);
		String name = (String) signature.elementAt(2);
		
		Outliner.macroManager.macroNames.add(name);
		Outliner.macroManager.macroClassNames.add(className);
		Outliner.macroManager.macroType.addItem(name);
	}
}