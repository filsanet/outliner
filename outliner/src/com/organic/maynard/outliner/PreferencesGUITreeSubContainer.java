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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import org.xml.sax.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

class PreferencesGUITreeSubContainer extends JPanel implements GUITreeComponent {
	// Constants
	public static final String A_LABEL = "label";
	public static final String A_STYLE = "style";

	public static final String STYLE_BEVELED = "beveled";
	public static final String STYLE_TITLED = "titled";


	// GUI Components
	private Box box = Box.createVerticalBox();
	
	
	// Constructor
	public PreferencesGUITreeSubContainer() {
		super.add(box);
	}
	
	// Overidden Methods from Container
	public Component add(Component c) {
		box.add(c);
		return c;
	}


	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}

	public void startSetup(AttributeList atts) {
		// Set Border Style
		String style = atts.getValue(A_STYLE);
		if (style.equals(STYLE_BEVELED)) {
			setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new EmptyBorder(new Insets(5,5,5,5))));
		} else if (style.equals(STYLE_TITLED)) {
			//setBorder(new TitledBorder(new EmptyBorder(new Insets(5,5,5,5)), atts.getValue(A_LABEL)));
			setBorder(new TitledBorder(LineBorder.createBlackLineBorder(), atts.getValue(A_LABEL)));
		}
		
		AbstractPreferencesPanel prefPanel = (AbstractPreferencesPanel) GUITreeLoader.getAncestorElementOfClass("com.organic.maynard.outliner.AbstractPreferencesPanel");
		prefPanel.startAddSubContainer(this);
	}

	public void endSetup(AttributeList atts) {
		AbstractPreferencesPanel prefPanel = (AbstractPreferencesPanel) GUITreeLoader.getAncestorElementOfClass("com.organic.maynard.outliner.AbstractPreferencesPanel");
		prefPanel.endAddSubContainer(this);
	}
}