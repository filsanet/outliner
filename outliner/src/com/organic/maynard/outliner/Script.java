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

import java.io.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public interface Script extends Serializable {
	
	// Undoable Types
	public String getFileName();

	public String getName();
	public void setName(String name);
	
	public boolean isStartupScript();
	public void setStartupScript(boolean b);

	public boolean isShutdownScript();
	public void setShutdownScript(boolean b);	
	
	public void process() throws Exception; // TODO: should probably have a success/failure return type.

	public ScriptConfig getScriptConfigurator();
	public void setScriptConfigurator(ScriptConfig configurator);
	
	public boolean init(File file);
	public boolean save(File file);
}