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

public class PreferenceColor extends AbstractPreference {
	
	public Color def = new Color(255,255,255);
	public Color cur = new Color(255,255,255);
	public Color tmp = new Color(255,255,255);
	
	// Constructors
	public PreferenceColor(Color def, String command) {
		this(def,new Color(255,255,255),command);
	}

	public PreferenceColor(Color def, Color cur, String command) {
		this.def = new Color(def.getRGB());
		this.cur = new Color(cur.getRGB());
		this.tmp = new Color(cur.getRGB());
		setCommand(command);
	}

	public String toString() {
		return (cur.getRed() + "," + cur.getGreen() + "," + cur.getBlue());
	}
	
	// Preference Interface
	public void restoreCurrentToDefault() {
		cur = new Color(def.getRGB());
	}
	
	public void restoreTemporaryToDefault(){
		tmp = new Color(def.getRGB());
	}

	public void restoreTemporaryToCurrent(){
		tmp = new Color(cur.getRGB());
	}

	public void applyTemporaryToCurrent(){
		cur = new Color(tmp.getRGB());
	}
}