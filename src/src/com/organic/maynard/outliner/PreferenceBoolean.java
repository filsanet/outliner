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

public class PreferenceBoolean extends AbstractPreference {
	
	public boolean def = false;
	public boolean cur = false;
	public boolean tmp = false;

	public BooleanValidator validator = new BooleanValidator();
	
	// Constructors
	public PreferenceBoolean(boolean def, String command) {
		this(def,false,command);
	}

	public PreferenceBoolean(boolean def, boolean cur, String command, BooleanValidator validator) {
		this(def,cur,command);
		this.validator = validator;
	}

	public PreferenceBoolean(boolean def, boolean cur, String command) {
		this.def = def;
		this.cur = cur;
		this.tmp = cur;
		setCommand(command);
	}

	public String toString() {
		return String.valueOf(cur);
	}

	// Setters with Validation
	public void setValidator(BooleanValidator validator) {this.validator = validator;}
	
	public void setDef(String value) {this.def = validator.getValidValue(value);}
	public void setDef(boolean value) {this.def = value;}

	public void setCur(String value) {this.cur = validator.getValidValue(value);}
	public void setCur(boolean value) {this.cur = value;}

	public void setTmp(String value) {this.tmp = validator.getValidValue(value);}
	public void setTmp(boolean value) {this.tmp = value;}
	
	// Preference Interface
	public void restoreCurrentToDefault() {cur = def;}
	public void restoreTemporaryToDefault(){tmp = def;}
	public void restoreTemporaryToCurrent(){tmp = cur;}
	public void applyTemporaryToCurrent(){cur = tmp;}
}