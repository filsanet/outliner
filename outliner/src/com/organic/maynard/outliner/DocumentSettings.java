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

public class DocumentSettings {
	protected OutlinerDocument doc = null;

	public boolean useDocumentSettings = false;
	
	// Editable Settings
	private String sLineEnd = Preferences.getPreferenceLineEnding(Preferences.LINE_END).cur;
	private String sSaveEncoding = Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur;
	private String sSaveFormat = Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur;
	private String sOwnerName = Preferences.getPreferenceString(Preferences.OWNER_NAME).cur;
	private String sOwnerEmail = Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur;
	private boolean sApplyFontStyleForComments = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_COMMENTS).cur;
	private boolean sApplyFontStyleForEditability = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_EDITABILITY).cur;
	private boolean sApplyFontStyleForMoveability = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_MOVEABILITY).cur;
	
	public PreferenceLineEnding lineEnd = new PreferenceLineEnding(sLineEnd, sLineEnd, "");
	public PreferenceString saveEncoding = new PreferenceString(sSaveEncoding, sSaveEncoding, "");
	public PreferenceString saveFormat = new PreferenceString(sSaveFormat, sSaveFormat, "");
	public PreferenceString ownerName = new PreferenceString(sOwnerName, sOwnerName, "");
	public PreferenceString ownerEmail = new PreferenceString(sOwnerEmail, sOwnerEmail, "");
	public PreferenceBoolean applyFontStyleForComments = new PreferenceBoolean(sApplyFontStyleForComments, sApplyFontStyleForComments, "");
	public PreferenceBoolean applyFontStyleForEditability = new PreferenceBoolean(sApplyFontStyleForEditability, sApplyFontStyleForEditability, "");
	public PreferenceBoolean applyFontStyleForMoveability = new PreferenceBoolean(sApplyFontStyleForMoveability, sApplyFontStyleForMoveability, "");

	// Hidden Settings
	public String dateCreated = new String("");
	public String dateModified = new String("");
	
	
	private String sFileProtocol = Preferences.getPreferenceString(Preferences.FILE_PROTOCOL).cur;
	public PreferenceString fileProtocol = new PreferenceString(sFileProtocol, sFileProtocol, "");


	// The Constructors
	public DocumentSettings(OutlinerDocument document) {
		this.doc = document;
	}
	
	public void destroy() {
		doc = null;
		lineEnd = null;
		saveEncoding = null;
		saveFormat = null;
		ownerName = null;
		ownerEmail = null;
		dateCreated = null;
		dateModified = null;
		fileProtocol = null;
		applyFontStyleForComments = null;
		applyFontStyleForEditability = null;
		applyFontStyleForMoveability = null;
	}

	public void show() {
		((DocumentSettingsView) GUITreeLoader.reg.get(GUITreeComponentRegistry.JDIALOG_DOCUMENT_SETTINGS_VIEW)).configureAndShow(this);
	}
}
