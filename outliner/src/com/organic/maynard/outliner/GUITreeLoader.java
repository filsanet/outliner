/**
 * Copyright (C) 2001 Maynard Demmon, maynard@organic.com
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

import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.StringTools;

import java.lang.reflect.*;

import javax.swing.*;

import org.xml.sax.*;

public class GUITreeLoader extends HandlerBase {

	// Constants
	public static final String E_SEPARATOR = "separator";

	public static final String A_ID = "id";
	public static final String A_CLASS = "class";
	public static final String A_POSITION = "position";


	// Class Fields
    private static Parser parser = new com.jclark.xml.sax.Driver();
	private static boolean errorOccurred = false;

	public static GUITreeComponentRegistry reg = new GUITreeComponentRegistry();
	public static ArrayList elementStack = new ArrayList();

	
	
	// Constructors
	public GUITreeLoader() {
		parser.setDocumentHandler(this);
		parser.setErrorHandler(this);	
	}

	
	// OpenFileFormat Interface
	public boolean load(String file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			parser.parse(new InputSource(fileInputStream));
			if (errorOccurred) {
				return false;
			}
			return true;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	// Sax DocumentHandler Implementation
	public void startDocument () {}
    
	public void endDocument () {}

	public void startElement (String name, AttributeList atts) {
		// Special Handling for elements that are not GUITreeComponents
		if (name.equals(E_SEPARATOR)) {
			try {
				JMenu menu = (JMenu) elementStack.get(elementStack.size() - 1);
				menu.insertSeparator(menu.getItemCount());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
				
		// Get Standard Attributes
		String componentName = atts.getValue(A_ID);
		String className = atts.getValue(A_CLASS);

		// Process Elements
		try {
			System.out.println("Setting component: " + className);
			GUITreeComponent obj = (GUITreeComponent) Class.forName(className).newInstance();
			
			// Set the GUITreeComponent's name
			obj.setGUITreeComponentID(componentName);

			// Update Stack
			elementStack.add(obj);
			
			// Add it to the registry
			reg.add(obj);
			
			// Call startSetup
			obj.startSetup(atts);
			
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			System.out.println(e);
		}
		

		// Special Handling for elements with custom attributes

	}
	
	public void endElement (String name) throws SAXException {
		// Special Handling for elements that are not GUITreeComponents
		if (name.equals(E_SEPARATOR)) {
			return;
		}
		
		// Get GUITreeComponent from stack
		GUITreeComponent obj = (GUITreeComponent) elementStack.get(elementStack.size() - 1);
		
		// Call endSetup
		obj.endSetup();
		
		// Update Stack
		elementStack.remove(elementStack.size() - 1);
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		
		// Does nothing right now.
	}
	
	
	// ErrorHandler Interface
	public void error(SAXParseException e) {
		System.out.println("SAXParserException Error: " + e);
		this.errorOccurred = true;
	}

	public void fatalError(SAXParseException e) {
		System.out.println("SAXParserException Fatal Error: " + e);
		this.errorOccurred = true;
	}

	public void warning(SAXParseException e) {
		System.out.println("SAXParserException Warning: " + e);
		this.errorOccurred = true;
	}
}


public class GUITreeComponentRegistry {

	// Constants
	public static final String GOTO_MENU_ITEM = "goto";
	public static final String FIND_MENU_ITEM = "find";
	public static final String STACK_MENU_ITEM = "stack";
	public static final String RECENT_FILE_MENU = "recent_file_list";
	
	public static final String SAVE_MENU_ITEM = "save";
	public static final String SAVE_AS_MENU_ITEM = "save_as";
	public static final String SAVE_ALL_MENU_ITEM = "save_all";
	public static final String REVERT_MENU_ITEM = "revert";
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
	public static final String SELECT_ALL_MENU_ITEM = "select_all";
	public static final String EDIT_DOCUMENT_SETTINGS_MENU_ITEM = "edit_document_settings";

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
	
	private HashMap reg = new HashMap();
	
	// Constructors
	public GUITreeComponentRegistry() {
	
	}
	
	public void add(GUITreeComponent comp) {
		reg.put(comp.getGUITreeComponentID(), comp);
	}

	public GUITreeComponent get(String name) {
		return (GUITreeComponent) reg.get(name);
	}
}


public interface GUITreeComponent {
	// Undoable Types
	public String getGUITreeComponentID();
	public void setGUITreeComponentID(String id);
	
	public void startSetup(AttributeList atts);
	public void endSetup();
}
