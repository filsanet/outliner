/**
 * Copyright (C) 2003 Maynard Demmon, maynard@organic.com
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

package com.organic.maynard.swing;

/**
 * A simple component for viewing HTML
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.Document;
import java.net.URL;
import java.io.IOException;

public class HTMLViewer extends JEditorPane implements HyperlinkListener {
	
	// Instance Fields
	
	
	// Constructor
	public HTMLViewer() {
		super();
		
		this.setEditable(false);
		
		HTMLEditorKit htmlKit = new HTMLEditorKit();
		this.setEditorKit(htmlKit);
		
		this.addHyperlinkListener(this);
	}
	
	
	// HyperlinkListener Interface
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
			URL url = e.getURL();
			try {
				this.setPage(url);
			} catch (IOException ex) {
				System.out.println("IOException: " + ex.getMessage());
			}
		}
	}
	
	// Other Methods
	public String getTitle() {
		Document doc = getDocument();
		return (String) doc.getProperty(Document.TitleProperty);
	}
}