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

import org.xml.sax.*;

public class PreferenceLineEnding extends AbstractPreference implements GUITreeComponent {
	
	public String def = "";
	public String cur = "";
	public String tmp = "";


	// Constructors
	public PreferenceLineEnding() {
	
	}

	public PreferenceLineEnding(String def, String command) {
		this(def,"",command);
	}

	public PreferenceLineEnding(String def, String cur, String command) {
		this.def = def;
		this.cur = cur;
		this.tmp = cur;
		setCommand(command);
	}


	// GUITreeComponent Interface
	public void endSetup(AttributeList atts) {
		super.endSetup(atts);

		String def = Preferences.lineEndingToPlatform(System.getProperty("line.separator"));
				
		setDef(def);
		setCur(def);
		setTmp(def);
	}	


	// Setters	
	public void setDef(String value) {this.def = value;}
	public void setCur(String value) {this.cur = value;}
	public void setTmp(String value) {this.tmp = value;}


	// Misc Methods
	public String toString() {return String.valueOf(cur);}


	// Preference Interface
	public void restoreCurrentToDefault() {cur = def;}
	public void restoreTemporaryToDefault() {tmp = def;}
	public void restoreTemporaryToCurrent() {tmp = cur;}
	public void applyTemporaryToCurrent() {cur = tmp;}
}