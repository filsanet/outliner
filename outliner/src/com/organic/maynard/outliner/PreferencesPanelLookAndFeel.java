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
 
 // we're part of this
package com.organic.maynard.outliner;

// we use these
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import com.organic.maynard.util.string.StanStringTools ;
import org.xml.sax.*;

public class PreferencesPanelLookAndFeel 
	extends AbstractPreferencesPanel 
	implements PreferencesPanel, GUITreeComponent {

	// sets of choice strings for combo boxes
	private static final String [] DOCUMENT_TITLES_NAME_FORMS = {
		GUITreeLoader.reg.getText(Preferences.RF_NF_FULL_PATHNAME), 
		GUITreeLoader.reg.getText(Preferences.RF_NF_TRUNC_PATHNAME), 
		GUITreeLoader.reg.getText(Preferences.RF_NF_FILENAME) 
		} ;

	// document title name forms
	private static final int FULL_PATHNAME = 0 ;
	private static final int TRUNC_PATHNAME = 1 ;
	private static final int JUST_FILENAME = 2 ;
	
	// remembering how we're displaying document titles
	private static int currentDocTitleNameForm = -1 ;

	private static final String TRUNC_STRING = GUITreeLoader.reg.getText("trunc_string");


	// at the end of our setup ...
	public void endSetup(AttributeList atts) {
		// call on the ancestors to their stuff
		super.endSetup(atts);

		// fill the combo boxes with choices
		AbstractPreferencesPanel.addArrayToComboBox(DOCUMENT_TITLES_NAME_FORMS, 
			GUITreeComponentRegistry.COMPONENT_DOCUMENT_TITLES_NAME_FORM);
	
	} // end method endSetup


	public void applyCurrentToApplication() {
		// local vars
		boolean docTitleNameFormChange = false ;
		int nameForm = -1;
		int limit = 0;
		String currentSetting = null;
		String newTitle = null ;
		DocumentInfo docInfo = null ;
		String pathname = null ;
		
		// grab what's been set in the panel
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

		// deal with document titles name form widget
		// get a ref to it
		PreferenceString pDT_Name_Form = (PreferenceString) prefs.getPreference(
			Preferences.DOCUMENT_TITLES_NAME_FORM);
			
		// try to convert it to an int value
		for (nameForm = 0, limit = DOCUMENT_TITLES_NAME_FORMS.length, currentSetting = pDT_Name_Form.getCur();
			nameForm < limit ; nameForm++ ) {
				if (currentSetting.equals(DOCUMENT_TITLES_NAME_FORMS[nameForm])) {
					break ;
				} // end if
			} // end for
			
		// were we able to convert, and is there a change in the doc titles name form ?
		docTitleNameFormChange = (nameForm < limit) && (nameForm != currentDocTitleNameForm) ;
		
		// if there was a change, let's remember the new value
		OutlinerDocument.setTitleNameForm(nameForm) ;

		// for each open document ...
		for (int i = 0; i < Outliner.openDocumentCount(); i++) {
			// get the document
			OutlinerDocument doc = Outliner.getDocument(i);

			// Update the cellRenderers
			for (int j = 0; j < OutlineLayoutManager.CACHE_SIZE; j++) {
				doc.panel.layout.textAreas[j].setSelectionColor(pTextareaForegroundColor.cur);
				doc.panel.layout.textAreas[j].setSelectedTextColor(pTextareaBackgroundColor.cur);
				doc.panel.layout.textAreas[j].setCaretColor(pSelectedChildColor.cur);
			} // end for
			
			// if we have a valid change in doc title name form
			if (docTitleNameFormChange) {
				// grab the doc's info, then its pathname
				docInfo = doc.getDocumentInfo() ;
				pathname = docInfo.getPath () ;
				
				// case out on the form to build the title
				switch (nameForm) {
				
				case FULL_PATHNAME:
				default: 
					newTitle = pathname ;
					break ;
					
				case TRUNC_PATHNAME: 
					newTitle = StanStringTools.getTruncatedPathName(pathname, TRUNC_STRING) ;
					break ;
					
				case JUST_FILENAME: 
					newTitle = StanStringTools.getFileNameFromPathName(pathname) ;
					break ;
					
				} // end switch
				
				// set the title
				doc.setTitle(newTitle) ;
			} // end if we have a valid change in doc title name form
			
		} // end for
		
		// Update the Comment Icons
		OutlineCommentIndicator.createIcons();
		OutlineEditableIndicator.createIcons();
		OutlineMoveableIndicator.createIcons();	
	} // end method
} // end class