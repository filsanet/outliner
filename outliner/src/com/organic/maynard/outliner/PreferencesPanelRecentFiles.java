/**
 * Copyright (C) 2002   Stan Krute <Stan@StanKrute.com>
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

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */
 

package com.organic.maynard.outliner;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.xml.sax.*;

// class to handle the Recent Files list options prefs panel
public class PreferencesPanelRecentFiles 
	extends AbstractPreferencesPanel 
	implements PreferencesPanel, GUITreeComponent {

	// sets of choice strings for combo boxes
	private static final String [] RECENT_FILES_ORDERINGS = {
		GUITreeLoader.reg.getText(Preferences.RF_O_CHRONOLOGICAL), 
		GUITreeLoader.reg.getText(Preferences.RF_O_ALPHABETICAL), 
		GUITreeLoader.reg.getText(Preferences.RF_O_ASCII) 
		} ;

	private static final String [] RECENT_FILES_NAME_FORMS = {
		GUITreeLoader.reg.getText(Preferences.RF_NF_FULL_PATHNAME), 
		/* [srk] enable these once RecentFilesList can handle truncated pathnames
		GUITreeLoader.reg.getText(Preferences.RF_NF_TRUNC_PATHNAME), */
		GUITreeLoader.reg.getText(Preferences.RF_NF_FILENAME) 
		} ;

	private static final String [] RECENT_FILES_DIRECTIONS = {
		GUITreeLoader.reg.getText(Preferences.RF_D_TOPTOBOTTOM), 
		GUITreeLoader.reg.getText(Preferences.RF_D_BOTTOMTOTOP) 
		} ;

	public void endSetup(AttributeList atts) {
		// call on the ancestors to their stuff
		super.endSetup(atts);

		// fill the combo boxes with choices
		AbstractPreferencesPanel.addArrayToComboBox(RECENT_FILES_ORDERINGS, 
			GUITreeComponentRegistry.COMPONENT_RECENT_FILES_ORDERING);
			
		AbstractPreferencesPanel.addArrayToComboBox(RECENT_FILES_NAME_FORMS, 
			GUITreeComponentRegistry.COMPONENT_RECENT_FILES_NAME_FORM);
			
		AbstractPreferencesPanel.addArrayToComboBox(RECENT_FILES_DIRECTIONS, 
			GUITreeComponentRegistry.COMPONENT_RECENT_FILES_DIRECTION);
	
	} // end method endSetup

	public void applyCurrentToApplication() {
		int limit = 0;
		String currentSetting = null;
		boolean coolToApply = true ;
		int ordering = 0;
		int nameForm = 0 ;
		int direction = 0 ;
		
		// grab what's been set in the panel
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);

		PreferenceString pRF_Ordering = (PreferenceString) prefs.getPreference(
			Preferences.RECENT_FILES_ORDERING);
		PreferenceString pRF_Name_Form = (PreferenceString) prefs.getPreference(
			Preferences.RECENT_FILES_NAME_FORM);
		PreferenceString pRF_Direction = (PreferenceString) prefs.getPreference(
			Preferences.RECENT_FILES_DIRECTION);
		
		
		// find the position of those strings in their arrays
		for (ordering = 0, limit = RECENT_FILES_ORDERINGS.length, currentSetting = pRF_Ordering.getCur();
			ordering < limit ; ordering++ ) {
				if (currentSetting.equals(RECENT_FILES_ORDERINGS[ordering])) {
					break ;
				} // end if
			} // end for
			
		if (ordering == limit){
			coolToApply = false ;
		} // end if
		
		for (nameForm = 0, limit = RECENT_FILES_NAME_FORMS.length, currentSetting = pRF_Name_Form.getCur();
			nameForm < limit ; nameForm++ ) {
				if (currentSetting.equals(RECENT_FILES_NAME_FORMS[nameForm])) {
					break ;
				} // end if
			} // end for
			
		if (nameForm == limit){
			coolToApply = false ;
		} // end if
		
		for (direction = 0, limit = RECENT_FILES_DIRECTIONS.length, currentSetting = pRF_Direction.getCur();
			direction < limit ; direction++ ) {
				if (currentSetting.equals(RECENT_FILES_DIRECTIONS[direction])) {
					break ;
				} // end if
			} // end for
			
		if (direction == limit){
			coolToApply = false ;
		} // end if
		
		// if we've got a set of valid values
		if (coolToApply) {

			// grab a ref to the recent files list menu item
			RecentFilesList rflmi = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);

			// apply 'em
			rflmi.setDisplayOptions(ordering, nameForm, direction) ;
			
		} // end if we've got a set of valid values
			
	} // end method applyCurrentToApplication
	
} // end class

