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

public class PreferenceInt extends AbstractPreference {
	
	public int def = 0;
	public int cur = 0;
	public int tmp = 0;
	
	public IntRangeValidator validator = new IntRangeValidator(0,0,0);
	
	// Constructors
	public PreferenceInt(int def, String command) {
		this(def,0,command);
	}

	public PreferenceInt(int def, int cur, String command, IntRangeValidator validator) {
		this(def,cur,command);
		this.validator = validator;
	}

	public PreferenceInt(int def, int cur, String command) {
		this.def = def;
		this.cur = cur;
		this.tmp = cur;
		setCommand(command);
	}

	public String toString() {return String.valueOf(cur);}

	// Setters with Validation
	public void setValidator(IntRangeValidator validator) {this.validator = validator;}
	
	public void setDef(String value) {this.def = validator.getValidValue(value).intValue();}
	public void setDef(int value) {this.def = validator.getValidValue(value).intValue();}

	public void setCur(String value) {this.cur = validator.getValidValue(value).intValue();}
	public void setCur(int value) {this.cur = validator.getValidValue(value).intValue();}

	public void setTmp(String value) {
		if (validator == null) {System.out.println("Validator is null");}
		this.tmp = validator.getValidValue(value).intValue();
	}
	public void setTmp(int value) {
		if (validator == null) {System.out.println("Validator is null");}
		this.tmp = validator.getValidValue(value).intValue();
	}
	
	// Preference Interface
	public void restoreCurrentToDefault() {cur = def;}
	public void restoreTemporaryToDefault() {tmp = def;}
	public void restoreTemporaryToCurrent() {tmp = cur;}
	public void applyTemporaryToCurrent() {cur = tmp;}
}