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
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
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
		
		AbstractPreferencesPanel.addArrayToComboBox(Preferences.FILE_PROTOCOLS.toArray(), GUITreeComponentRegistry.COMPONENT_FILE_PROTOCOL);
		AbstractPreferencesPanel.addArrayToComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS, GUITreeComponentRegistry.COMPONENT_LINE_ENDING);
		AbstractPreferencesPanel.addArrayToComboBox(Preferences.ENCODINGS.toArray(), GUITreeComponentRegistry.COMPONENT_ENCODING_WHEN_OPENING);
		AbstractPreferencesPanel.addArrayToComboBox(Preferences.ENCODINGS.toArray(), GUITreeComponentRegistry.COMPONENT_ENCODING_WHEN_SAVING);
		AbstractPreferencesPanel.addArrayToComboBox(Preferences.FILE_FORMATS_OPEN.toArray(), GUITreeComponentRegistry.COMPONENT_FORMAT_WHEN_OPENING);
		AbstractPreferencesPanel.addArrayToComboBox(Preferences.FILE_FORMATS_SAVE.toArray(), GUITreeComponentRegistry.COMPONENT_FORMAT_WHEN_SAVING);
	}
	
	public void applyCurrentToApplication() {
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);
		PreferenceString pFileProtocol = (PreferenceString) prefs.getPreference(Preferences.FILE_PROTOCOL);
		PreferenceLineEnding pLineEnd = (PreferenceLineEnding) prefs.getPreference(Preferences.LINE_END);
		PreferenceString pSaveEncoding = (PreferenceString) prefs.getPreference(Preferences.SAVE_ENCODING);
		PreferenceString pSaveFormat = (PreferenceString) prefs.getPreference(Preferences.SAVE_FORMAT);

		// Update Document Settings
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			OutlinerDocument doc = Outliner.getDocument(i);
			
			// Only update files that do not have overriding document settings.
			if (!doc.settings.useDocumentSettings) {
				doc.settings.fileProtocol.def = pFileProtocol.tmp;
				doc.settings.fileProtocol.cur = pFileProtocol.tmp;
				doc.settings.fileProtocol.tmp = pFileProtocol.tmp;
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
		
		// Synchronize default protocol in model.
		Outliner.fileProtocolManager.synchronizeDefault();
		Outliner.fileProtocolManager.synchonizeDefaultMenuItem();
		
		
	}
}