/**
 * Portions copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002  Stan Krute <Stan@StanKrute.com>
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
 
package com.organic.maynard.outliner.util.preferences;

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import com.organic.maynard.util.string.StanStringTools ;
import org.xml.sax.*;

public class PreferencesPanelLookAndFeel extends AbstractPreferencesPanel implements PreferencesPanel, GUITreeComponent {


	// GUITreeComponent Interface
	public void endSetup(AttributeList atts) {
		super.endSetup(atts);

		// fill title name form combo box with choices
		OutlinerDocument.fillTitleNameFormCombo();
	}


	// PreferencePanel Interface
	public void applyCurrentToApplication() {
		Preferences prefs = Outliner.prefs;

		PreferenceColor pDesktopBackgroundColor = (PreferenceColor) prefs.getPreference(Preferences.DESKTOP_BACKGROUND_COLOR);
		PreferenceColor pPanelBackgroundColor = (PreferenceColor) prefs.getPreference(Preferences.PANEL_BACKGROUND_COLOR);
		PreferenceColor pTextareaForegroundColor = (PreferenceColor) prefs.getPreference(Preferences.TEXTAREA_FOREGROUND_COLOR);
		PreferenceColor pTextareaBackgroundColor = (PreferenceColor) prefs.getPreference(Preferences.TEXTAREA_BACKGROUND_COLOR);
		PreferenceColor pSelectedChildColor = (PreferenceColor) prefs.getPreference(Preferences.SELECTED_CHILD_COLOR);

		// Set the Desktop Background color
		Outliner.jsp.getViewport().setBackground(pDesktopBackgroundColor.cur);
		Outliner.desktop.setBackground(pDesktopBackgroundColor.cur);

		// for each open document ...
		for (int i = 0, limit = Outliner.documents.openDocumentCount(); i < limit; i++) {
			// get the document
			OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getDocument(i);
			
			// Set the Panel Background color.
			doc.panel.setBackground(pPanelBackgroundColor.cur);

			// Update the cellRenderers
			for (int j = 0; j < OutlineLayoutManager.CACHE_SIZE; j++) {
				OutlinerCellRendererImpl renderer = doc.panel.layout.textAreas[j];
				renderer.setSelectionColor(pTextareaForegroundColor.cur);
				renderer.setSelectedTextColor(pTextareaBackgroundColor.cur);
				renderer.setCaretColor(pSelectedChildColor.cur);
			}
		}
		
		// Update the Comment Icons
		OutlineCommentIndicator.createIcons();
		OutlineEditableIndicator.createIcons();
		OutlineMoveableIndicator.createIcons();
		
		// sync up with any title name form changes
		OutlinerDocument.syncTitleNameForms();
	}
}