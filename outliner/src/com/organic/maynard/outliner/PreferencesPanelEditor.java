/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 * 
 *  - Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution. 
 * 
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
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
	
	
	// This is a misnomer, should really by apply current to application.
	public void applyCurrentToApplication() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		
		PreferenceInt pUndoQueueSize = (PreferenceInt) prefs.getPreference(Preferences.UNDO_QUEUE_SIZE);
		PreferenceBoolean pShowLineNumbers = (PreferenceBoolean) prefs.getPreference(Preferences.SHOW_LINE_NUMBERS);
		PreferenceBoolean pShowIndicators = (PreferenceBoolean) prefs.getPreference(Preferences.SHOW_INDICATORS);
		PreferenceString pFontFace = (PreferenceString) prefs.getPreference(Preferences.FONT_FACE);
		PreferenceInt pFontSize = (PreferenceInt) prefs.getPreference(Preferences.FONT_SIZE);
		PreferenceString pLineWrap = (PreferenceString) prefs.getPreference(Preferences.LINE_WRAP);
		PreferenceBoolean pUseCreateModDates = (PreferenceBoolean) prefs.getPreference(Preferences.USE_CREATE_MOD_DATES);
		PreferenceString pCreateModDatesFormat = (PreferenceString) prefs.getPreference(Preferences.CREATE_MOD_DATES_FORMAT);

		// Update Dates For Node Atts
		NodeImpl.isSettingCreateModDates = pUseCreateModDates.cur;
		NodeImpl.updateSimpleDateFormat(pCreateModDatesFormat.cur);
		
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
		
		// Update the Indicators
		if (pShowIndicators.cur) {
			OutlineCommentIndicator.BUTTON_WIDTH = OutlineCommentIndicator.WIDTH_DEFAULT;
			OutlineEditableIndicator.BUTTON_WIDTH = OutlineEditableIndicator.WIDTH_DEFAULT;
			OutlineMoveableIndicator.BUTTON_WIDTH = OutlineMoveableIndicator.WIDTH_DEFAULT;
		} else {
			OutlineCommentIndicator.BUTTON_WIDTH = 0;
			OutlineEditableIndicator.BUTTON_WIDTH = 0;
			OutlineMoveableIndicator.BUTTON_WIDTH = 0;
		}
		
		// Update the cellRenderers
		boolean line_wrap = true;
		if (pLineWrap.cur.equals(Preferences.TXT_CHARACTERS)) {
			line_wrap = false;
		}

		// Update fonts
		OutlinerCellRendererImpl.updateFonts();
		
		// Update renderers in existing docs
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			OutlinerDocument doc = Outliner.getDocument(i);
			for (int j = 0; j < OutlineLayoutManager.CACHE_SIZE; j++) {
				doc.panel.layout.textAreas[j].setWrapStyleWord(line_wrap);
				
				// Hide line numbers if both indicators and line numbers are turned off.
				// We leave them showing otherwise, because it creates a better visual
				// representation in the display when there are indented nodes.
				OutlineLineNumber lineNumber = doc.panel.layout.textAreas[j].lineNumber;
				
				if (pShowLineNumbers.cur || pShowIndicators.cur) {
					lineNumber.setOpaque(true);
				} else {
					lineNumber.setOpaque(false);
				}
				
				if (!pShowLineNumbers.cur) {
					lineNumber.setText("");
				}
			}
		}
	}
}