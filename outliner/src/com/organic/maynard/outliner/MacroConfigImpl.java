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

import javax.swing.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public abstract class MacroConfigImpl extends JPanel implements MacroConfig {
	
	public static final String NAME = "Macro Name";
	
	// The Constructor
	public MacroConfigImpl() {

	}

	
	// MacroConfig Interface
	private Macro macro = null;

	public void init(Macro macro) {
		this.macro = macro;
	}
	
	public Macro getMacro() {return this.macro;}
	
	public boolean cancel() {
		// Should Always return true.
		return true;
	}
	
	public boolean delete() {
		// Should Always return true.
		return true;
	}
}