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
import org.xml.sax.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public abstract class AbstractOutlinerMenuItem extends JMenuItem implements GUITreeComponent, JoeXMLConstants {

	// Constants
	private static final String DELETE = "delete";
	private static final String CTRL = "control";
	private static final String SHIFT = "shift";
	private static final String ALT = "alt";
	private static final String TAB = "tab";
	private static final String UP = "up";
	private static final String DOWN = "down";
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	private static final String PAGE_UP = "page_up";
	private static final String PAGE_DOWN = "page_down";
	private static final String F2 = "f2";
	private static final String F3 = "f3";
	private static final String F4 = "f4";
	private static final String F5 = "f5";
	private static final String F6 = "f6";
	private static final String F7 = "f7";
	private static final String F8 = "f8";
	private static final String F9 = "f9";
	private static final String F10 = "f10";
	private static final String F11 = "f11";
	private static final String F12 = "f12";
	
	
	// Constructors
	public AbstractOutlinerMenuItem() {}


	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}
	
	public void startSetup(AttributeList atts) {
		// Set the title of the menuItem
		setText(atts.getValue(A_TEXT));
		
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
			} else if (keyBinding.equals(DELETE)) {
				keyBindingChar = KeyEvent.VK_DELETE;
			} else if (keyBinding.equals(F11)) {
				keyBindingChar = KeyEvent.VK_F11;
			} else if (keyBinding.equals(F12)) {
				keyBindingChar = KeyEvent.VK_F12;
			}
			
			setAccelerator(KeyStroke.getKeyStroke(keyBindingChar, mask, false));
		} catch (NullPointerException e) {
			//e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			//e.printStackTrace();
		}

		// Add this menuItem to the parent menu.
		((JMenu) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 2)).add(this);
	}
	
	public void endSetup(AttributeList atts) {}
}