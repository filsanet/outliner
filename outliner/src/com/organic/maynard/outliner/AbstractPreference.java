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

public abstract class AbstractPreference implements Preference, GUITreeComponent {
	
	// Constants
	public static final String A_ID = "id";
	public static final String A_DEFAULT = "default";
	
	
	// GUITreeComponent Interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}
	
	public void startSetup(AttributeList atts) {
		String id = atts.getValue(A_ID);

		setCommand(id);

		// Add this menuItem to the parent menu.
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		prefs.addPreference(id, this);
	}
	
	public void endSetup(AttributeList atts) {}	
	
	
	// Preference Interface
	private String command = null;
	private Validator validator = null;
		
	// Command Parser
	public String getCommand() {return this.command;}
	public void setCommand(String command) {this.command = command;}

	// Validation
	public void setValidator(Validator v) {this.validator = v;}
	public Validator getValidator() {return this.validator;}

	// Abstract methods
	public abstract void restoreCurrentToDefault();
	public abstract void restoreTemporaryToDefault();

	public abstract void setCur(String s);
	public abstract void setDef(String s);
	public abstract void setTmp(String s);

}