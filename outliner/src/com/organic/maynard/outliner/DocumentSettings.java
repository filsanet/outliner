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

public class DocumentSettings {
 protected OutlinerDocument doc = null;

 public boolean useDocumentSettings = false;

 // Editable Settings
 private String sLineEnd = Preferences.getPreferenceLineEnding(Preferences.SAVE_LINE_END).cur;
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
