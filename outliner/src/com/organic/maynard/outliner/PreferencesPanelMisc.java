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

public class PreferencesPanelMisc extends AbstractPreferencesPanel implements PreferencesPanel, GUITreeComponent {
	
	public void applyCurrentToApplication() {		
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);

		PreferenceInt pScrollSpeed = (PreferenceInt) prefs.getPreference(Preferences.MOUSE_WHEEL_SCROLL_SPEED);

		// Trim the recent file menu since we may have a new size.
		RecentFilesList.trim();

		// Set the scroll speed on outliner. This will be inherited by all GUI elements contained within.
		Outliner.outliner.setScrollSpeed(pScrollSpeed.cur); 
	}
}