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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.xml.sax.*;

public abstract class AbstractOutlinerMenuItem extends JMenuItem implements GUITreeComponent {

	// Constants
	public static final String CTRL = "control";
	public static final String SHIFT = "shift";
	public static final String ALT = "alt";
	public static final String TAB = "tab";
	public static final String UP = "up";
	public static final String DOWN = "down";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String PAGE_UP = "page_up";
	public static final String PAGE_DOWN = "page_down";
	
	public static final String A_TEXT = "text";
	public static final String A_KEY_BINDING = "keybinding";
	public static final String A_KEY_BINDING_MODIFIERS = "keybindingmodifiers";

	public AbstractOutlinerMenuItem() {}


	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}
	
	public void startSetup(AttributeList atts) {
		// Set the title of the menuItem
		String title = atts.getValue(A_TEXT);
		setText(title);
		
		// Set KeyBinding
		int mask = 0;
		try {
			String keyBindingModifiers = atts.getValue(A_KEY_BINDING_MODIFIERS);
			if (keyBindingModifiers.indexOf(CTRL) != -1) {mask += Event.CTRL_MASK;}
			if (keyBindingModifiers.indexOf(SHIFT) != -1) {mask += Event.SHIFT_MASK;}
			if (keyBindingModifiers.indexOf(ALT) != -1) {mask += Event.ALT_MASK;}
		} catch (NullPointerException e) {
			//e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			//e.printStackTrace();
		}
		
		try {		
			String keyBinding = atts.getValue(A_KEY_BINDING);
			
			char keyBindingChar = keyBinding.charAt(0);
			if (keyBinding.equals(TAB)) {
				keyBindingChar = KeyEvent.VK_TAB;
			} else if (keyBinding.equals(UP)) {
				keyBindingChar = KeyEvent.VK_UP;
			} else if (keyBinding.equals(DOWN)) {
				keyBindingChar = KeyEvent.VK_DOWN;
			} else if (keyBinding.equals(LEFT)) {
				keyBindingChar = KeyEvent.VK_LEFT;
			} else if (keyBinding.equals(RIGHT)) {
				keyBindingChar = KeyEvent.VK_RIGHT;
			} else if (keyBinding.equals(PAGE_UP)) {
				keyBindingChar = KeyEvent.VK_PAGE_UP;
			} else if (keyBinding.equals(PAGE_DOWN)) {
				keyBindingChar = KeyEvent.VK_PAGE_DOWN;
			}
			
			setAccelerator(KeyStroke.getKeyStroke(keyBindingChar, mask, false));
		} catch (NullPointerException e) {
			//e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			//e.printStackTrace();
		}

		// Add this menuItem to the parent menu.
		JMenu menu = (JMenu) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 2);
		menu.add(this);
	}
	
	public void endSetup() {}
}