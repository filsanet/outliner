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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.xml.sax.*;

public class PreferencesPanelOpenAndSave extends AbstractPreferencesPanel implements PreferencesPanel, GUITreeComponent {

	public void endSetup(AttributeList atts) {
		super.endSetup(atts);
		
		AbstractPreferencesPanel.addArrayToComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS, GUITreeComponentRegistry.COMPONENT_LINE_ENDING);
		AbstractPreferencesPanel.addArrayToComboBox(Preferences.ENCODINGS.toArray(), GUITreeComponentRegistry.COMPONENT_ENCODING_WHEN_OPENING);
		AbstractPreferencesPanel.addArrayToComboBox(Preferences.ENCODINGS.toArray(), GUITreeComponentRegistry.COMPONENT_ENCODING_WHEN_SAVING);
		AbstractPreferencesPanel.addArrayToComboBox(Preferences.FILE_FORMATS_OPEN.toArray(), GUITreeComponentRegistry.COMPONENT_FORMAT_WHEN_OPENING);
		AbstractPreferencesPanel.addArrayToComboBox(Preferences.FILE_FORMATS_SAVE.toArray(), GUITreeComponentRegistry.COMPONENT_FORMAT_WHEN_SAVING);
	}
	
	public void applyCurrentToApplication() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		PreferenceLineEnding pLineEnd = (PreferenceLineEnding) prefs.getPreference(Preferences.LINE_END);
		PreferenceString pSaveEncoding = (PreferenceString) prefs.getPreference(Preferences.SAVE_ENCODING);
		PreferenceString pSaveFormat = (PreferenceString) prefs.getPreference(Preferences.SAVE_FORMAT);

		// Update Document Settings
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			OutlinerDocument doc = Outliner.getDocument(i);
			
			// Only update files that do not have overriding document settings.
			if (!doc.settings.useDocumentSettings) {
				doc.settings.lineEnd.def = pLineEnd.tmp;
				doc.settings.lineEnd.cur = pLineEnd.tmp;
				doc.settings.lineEnd.tmp = pLineEnd.tmp;
				doc.settings.saveEncoding.def = pSaveEncoding.tmp;
				doc.settings.saveEncoding.cur = pSaveEncoding.tmp;
				doc.settings.saveEncoding.tmp = pSaveEncoding.tmp;
				doc.settings.saveFormat.def = pSaveFormat.tmp;
				doc.settings.saveFormat.cur = pSaveFormat.tmp;
				doc.settings.saveFormat.tmp = pSaveFormat.tmp;
				//doc.setFileModified(true);
			}
		}
	}
}