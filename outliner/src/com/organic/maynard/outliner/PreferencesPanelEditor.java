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

import java.awt.Font;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.xml.sax.*;

public class PreferencesPanelEditor extends AbstractPreferencesPanel implements PreferencesPanel, GUITreeComponent {
	
	private final GraphicsEnvironment GRAPHICS_ENVIRONEMNT = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private final String[] LINE_WRAP_OPTIONS = {Preferences.TXT_WORDS, Preferences.TXT_CHARACTERS};

	public void endSetup(AttributeList atts) {
		super.endSetup(atts);
		
		AbstractPreferencesPanel.addArrayToComboBox(GRAPHICS_ENVIRONEMNT.getAvailableFontFamilyNames(), GUITreeComponentRegistry.COMPONENT_FONT_FACE);
		AbstractPreferencesPanel.addArrayToComboBox(LINE_WRAP_OPTIONS, GUITreeComponentRegistry.COMPONENT_LINE_WRAP);
	}
	
	
	public void applyTemporaryToCurrent() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		
		PreferenceInt pUndoQueueSize = (PreferenceInt) prefs.getPreference(Preferences.UNDO_QUEUE_SIZE);
		PreferenceBoolean pShowLineNumbers = (PreferenceBoolean) prefs.getPreference(Preferences.SHOW_LINE_NUMBERS);
		PreferenceString pFontFace = (PreferenceString) prefs.getPreference(Preferences.FONT_FACE);
		PreferenceInt pFontSize = (PreferenceInt) prefs.getPreference(Preferences.FONT_SIZE);
		PreferenceString pLineWrap = (PreferenceString) prefs.getPreference(Preferences.LINE_WRAP);
	
		// Update the undo queue for all the documents immediatly if it is being downsized.
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			Outliner.getDocument(i).undoQueue.trim();
		}
		UndoQueue.updateMenuBar(Outliner.getMostRecentDocumentTouched());

		// Update the line numbers
		if (pShowLineNumbers.cur) {
			OutlineLineNumber.LINE_NUMBER_WIDTH = OutlineLineNumber.LINE_NUMBER_WIDTH_DEFAULT;
		} else {
			OutlineLineNumber.LINE_NUMBER_WIDTH = OutlineLineNumber.LINE_NUMBER_WIDTH_MIN;
		}

		// Update the cellRenderers
		boolean line_wrap = true;
		if (pLineWrap.cur.equals(Preferences.TXT_CHARACTERS)) {
			line_wrap = false;
		}

		OutlinerCellRendererImpl.updateFont(); // Updates fonts for new docs.
		
		// Update fonts for existing docs.
		Font font = new Font(pFontFace.cur, Font.PLAIN, pFontSize.cur);
		
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			OutlinerDocument doc = Outliner.getDocument(i);
			for (int j = 0; j < OutlineLayoutManager.CACHE_SIZE; j++) {
				doc.panel.layout.textAreas[j].setFont(font);
				doc.panel.layout.textAreas[j].setWrapStyleWord(line_wrap);
				
				// Updated line number visibility
				if (pShowLineNumbers.cur) {
					doc.panel.layout.textAreas[j].lineNumber.setOpaque(true);
				} else {
					doc.panel.layout.textAreas[j].lineNumber.setOpaque(false);
				}
			}
		}
	}
}