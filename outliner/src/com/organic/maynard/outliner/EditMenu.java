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

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.dom.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.xml.sax.*;

public class EditMenu extends AbstractOutlinerMenu implements GUITreeComponent {

	// The Constructors
	public EditMenu() {
		super();
	}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		Outliner.menuBar.editMenu = this;
	}


	// Utility Methods
	protected static void fireKeyEvent(OutlinerDocument doc, int keyMask, int keyChar) {
		OutlinerCellRendererImpl textArea = doc.panel.layout.getUIComponent(doc.tree.getEditingNode());
		if (textArea == null) {return;}
		
		try {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), keyMask, keyChar));
				textArea.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), keyMask, keyChar));
			} else if (doc.tree.getComponentFocus() == OutlineLayoutManager.ICON) {
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), keyMask, keyChar));
				textArea.button.fireKeyEvent(new KeyEvent(textArea, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), keyMask, keyChar));
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}