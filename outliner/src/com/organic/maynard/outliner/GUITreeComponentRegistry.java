/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002   Stan Krute <Stan@StanKrute.com>
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

import java.util.HashMap;
import com.organic.maynard.util.string.Replace;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class GUITreeComponentRegistry {

	// Constants
	public static final String GOTO_MENU_ITEM = "goto";
	public static final String FIND_MENU_ITEM = "find";
	public static final String STACK_MENU_ITEM = "stack";
	public static final String RECENT_FILE_MENU = "recent_file_list";

	public static final String OPEN_MENU_ITEM = "open";
	public static final String SAVE_MENU_ITEM = "save";
	public static final String SAVE_AS_MENU_ITEM = "save_as";
	public static final String SAVE_ALL_MENU_ITEM = "save_all";
	public static final String REVERT_MENU_ITEM = "revert";
	public static final String EXPORT_MENU_ITEM = "export";
	public static final String EXPORT_SELECTION_MENU_ITEM = "export_selection";
	public static final String IMPORT_MENU_ITEM = "import";
	public static final String CLOSE_MENU_ITEM = "close";
	public static final String CLOSE_ALL_MENU_ITEM = "close_all";
	public static final String QUIT_MENU_ITEM = "quit";

	public static final String UNDO_MENU_ITEM = "undo";
	public static final String REDO_MENU_ITEM = "redo";
	public static final String UNDO_ALL_MENU_ITEM = "undo_all";
	public static final String REDO_ALL_MENU_ITEM = "redo_all";
	public static final String CUT_MENU_ITEM = "cut";
	public static final String COPY_MENU_ITEM = "copy";
	public static final String PASTE_MENU_ITEM = "paste";
	public static final String DELETE_MENU_ITEM = "delete";
	public static final String SELECT_ALL_MENU_ITEM = "select_all";
	public static final String SELECT_NONE_MENU_ITEM = "select_none";
	public static final String SELECT_INVERSE_MENU_ITEM = "select_inverse";
	public static final String EDIT_DOCUMENT_SETTINGS_MENU_ITEM = "edit_document_settings";
	public static final String EDIT_DOCUMENT_ATTRIBUTES_MENU_ITEM = "edit_document_attributes";

	public static final String OUTLINE_TOGGLE_ATTRIBUTES_MENU_ITEM = "toggle_attributes";
	public static final String OUTLINE_TOGGLE_COMMENT_MENU_ITEM = "toggle_comment";
	public static final String OUTLINE_TOGGLE_EXPANSION_MENU_ITEM = "toggle_expansion";
	public static final String OUTLINE_EXPAND_ALL_SUBHEADS_MENU_ITEM = "expand_all_subheads";
	public static final String OUTLINE_EXPAND_EVERYTHING_MENU_ITEM = "expand_everything";
	public static final String OUTLINE_COLLAPSE_TO_PARENT_MENU_ITEM = "collapse_to_parent";
	public static final String OUTLINE_COLLAPSE_EVERYTHING_MENU_ITEM = "collapse_everything";
	public static final String OUTLINE_MOVE_UP_MENU_ITEM = "move_up";
	public static final String OUTLINE_MOVE_DOWN_MENU_ITEM = "move_down";
	public static final String OUTLINE_MOVE_RIGHT_MENU_ITEM = "move_right";
	public static final String OUTLINE_MOVE_LEFT_MENU_ITEM = "move_left";
	public static final String OUTLINE_PROMOTE_MENU_ITEM = "promote";
	public static final String OUTLINE_DEMOTE_MENU_ITEM = "demote";
	public static final String OUTLINE_MERGE_MENU_ITEM = "merge";
	public static final String OUTLINE_MERGE_WITH_SPACES_MENU_ITEM = "merge_with_spaces";
	public static final String OUTLINE_HOIST_MENU_ITEM = "hoist";
	public static final String OUTLINE_DEHOIST_MENU_ITEM = "dehoist";
	public static final String OUTLINE_DEHOIST_ALL_MENU_ITEM = "dehoist_all";

	public static final String PREFERENCES = "prefs";
	public static final String PREFERENCES_FRAME = "preferences_frame";
	public static final String PREFERENCES_PANEL_EDITOR = "preferences_panel_editor";
	public static final String PREFERENCES_PANEL_MISC = "preferences_panel_misc";
	public static final String PREFERENCES_PANEL_OPEN_AND_SAVE = "preferences_panel_open_and_save";
	public static final String PREFERENCES_PANEL_LOOK_AND_FEEL = "preferences_panel_look_and_feel";

	public static final String COMPONENT_FONT_FACE = "font_face_component";
	public static final String COMPONENT_LINE_WRAP = "line_wrap_component";
	public static final String COMPONENT_FILE_PROTOCOL = "file_protocol_component";
	public static final String COMPONENT_LINE_ENDING = "line_end_component";
	
	public static final String COMPONENT_ENCODING_WHEN_OPENING = "open_encoding_component";
	public static final String COMPONENT_ENCODING_WHEN_IMPORTING = "import_encoding_component";
	public static final String COMPONENT_ENCODING_WHEN_SAVING = "save_encoding_component";
	public static final String COMPONENT_ENCODING_WHEN_EXPORTING = "export_encoding_component";
	
	public static final String COMPONENT_FORMAT_WHEN_OPENING = "open_format_component";
	public static final String COMPONENT_FORMAT_WHEN_IMPORTING = "import_format_component";
	public static final String COMPONENT_FORMAT_WHEN_SAVING = "save_format_component";
	public static final String COMPONENT_FORMAT_WHEN_EXPORTING = "export_format_component";

	public static final String JDIALOG_DOCUMENT_SETTINGS_VIEW = "document_settings_view";

	public static final String BSH_CONSOLE = "bsh_console";
	public static final String RUN_AS_BSH_SCRIPT_MENU_ITEM = "run_as_bsh_script";

	// Other Constants
	public static final String PLACEHOLDER_1 = "{$value_1}";
	public static final String PLACEHOLDER_2 = "{$value_2}";
	public static final String PLACEHOLDER_3 = "{$value_3}";

	// Fields
	private HashMap reg = new HashMap();
	private HashMap textResources = new HashMap();


	// Constructors
	public GUITreeComponentRegistry() {}


	// Accessors
	public void add(GUITreeComponent comp) {
  reg.put(comp.getGUITreeComponentID(), comp);
	}

	public GUITreeComponent get(String name) {
  return (GUITreeComponent) reg.get(name);
	}

	public void addText(String key, String value) {
  value = Replace.replace(value,"\\n", "\n");
  value = Replace.replace(value,"\\\\", "\\");

  if (textResources.get(key) != null) {
   System.out.println("WARNING: Writing over existing text repository key: " + key);
  }

  textResources.put(key, value);
 }

 public String getText(String key) {
  String retVal = (String) textResources.get(key);
  if (retVal == null) {
   System.out.println("Invalid text resource key: " + key);
  }
  return retVal;
 }
}
