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

import java.io.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public interface Macro extends Serializable {
	
	// Undoable Types
	public static final int NOT_UNDOABLE = -1;
	public static final int SIMPLE_UNDOABLE = 0;
	public static final int COMPLEX_UNDOABLE = 1;
	public static final int RAW_MACRO_UNDOABLE = 2;

	public String getFileName();
	
	public String getName();
	public void setName(String name);
	
	public boolean isUndoable();
	public int getUndoableType();
	
	public MacroConfig getConfigurator();
	public void setConfigurator(MacroConfig configurator);

	public NodeRangePair process(NodeRangePair nodeRangePair);
	
	public boolean init(File file);
	public boolean save(File file);
}