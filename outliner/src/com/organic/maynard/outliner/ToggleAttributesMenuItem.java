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
import com.organic.maynard.outliner.event.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.xml.sax.*;

public class ToggleAttributesMenuItem extends AbstractOutlinerMenuItem implements OutlinerDocumentListener, ActionListener, GUITreeComponent {
	
	private static final String A_SHOW = "show";
	private static final String A_HIDE = "hide";
	
	private static String SHOW_ATTRIBUTES = "";
	private static String HIDE_ATTRIBUTES = "";


	// OutlinerDocumentListener Interface
	public void modifiedStateChanged(DocumentEvent e) {}
	
	public void attributesVisibilityChanged(OutlinerDocumentEvent e) {
		if (e.getOutlinerDocument() != null) {
			if (e.getOutlinerDocument().isShowingAttributes()) {
				setShowMode(false);
			} else {
				setShowMode(true);
			}
		}	
	}

	public void hoistDepthChanged(OutlinerDocumentEvent e) {}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		SHOW_ATTRIBUTES = atts.getValue(A_SHOW);
		HIDE_ATTRIBUTES = atts.getValue(A_HIDE);
		
		addActionListener(this);
		Outliner.documents.addOutlinerDocumentListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
		if (doc.isShowingAttributes()) {
			doc.showAttributes(false);
			setShowMode(true);
		} else {
			doc.showAttributes(true);
			setShowMode(false);
		}
	}
	
	private void setShowMode(boolean b) {
		if (b) {
			setText(SHOW_ATTRIBUTES);
		} else {
			setText(HIDE_ATTRIBUTES);
		}
	}
}
