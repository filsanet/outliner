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

public class PreferencesPanelLookAndFeel extends AbstractPreferencesPanel implements PreferencesPanel, GUITreeComponent {

	public void applyTemporaryToCurrent() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);

		PreferenceColor pDesktopBackgroundColor = (PreferenceColor) prefs.getPreference(Preferences.DESKTOP_BACKGROUND_COLOR);
		PreferenceColor pPanelBackgroundColor = (PreferenceColor) prefs.getPreference(Preferences.PANEL_BACKGROUND_COLOR);
		PreferenceColor pTextareaForegroundColor = (PreferenceColor) prefs.getPreference(Preferences.TEXTAREA_FOREGROUND_COLOR);
		PreferenceColor pTextareaBackgroundColor = (PreferenceColor) prefs.getPreference(Preferences.TEXTAREA_BACKGROUND_COLOR);
		PreferenceColor pSelectedChildColor = (PreferenceColor) prefs.getPreference(Preferences.SELECTED_CHILD_COLOR);

		// Set the Desktop Background color
		Outliner.jsp.getViewport().setBackground(pDesktopBackgroundColor.cur);
		Outliner.desktop.setBackground(pDesktopBackgroundColor.cur);

		// Set the Panel Background color.
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			Outliner.getDocument(i).panel.setBackground(pPanelBackgroundColor.cur);
		}

		// Update the cellRenderers
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			OutlinerDocument doc = Outliner.getDocument(i);
			for (int j = 0; j < OutlineLayoutManager.CACHE_SIZE; j++) {
				doc.panel.layout.textAreas[j].setSelectionColor(pTextareaForegroundColor.cur);
				doc.panel.layout.textAreas[j].setSelectedTextColor(pTextareaBackgroundColor.cur);
				doc.panel.layout.textAreas[j].setCaretColor(pSelectedChildColor.cur);
			}
		}
		
		// Update the Comment Icons
		OutlineCommentIndicator.createIcons();
		OutlineEditableIndicator.createIcons();
		OutlineMoveableIndicator.createIcons();	
	}
}