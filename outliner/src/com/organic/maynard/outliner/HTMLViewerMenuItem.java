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

package com.organic.maynard.outliner;

/**
 * Loads an HTML page via the HTMLViewerDialog.
 *
 * @author  $Author$
 * @version $Revision$, $Date$
 */
 
import com.organic.maynard.outliner.guitree.*;
import java.awt.event.*;
import org.xml.sax.*;

public class HTMLViewerMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent, JoeReturnCodes {
	
	// Constants
	public static final String A_PATH = "path";
	
	
	// Instance Fields
	private String resource_path = null;
	
	
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		String path = atts.getValue(A_PATH);
		if (path == null) {
			System.out.println("WARNING: path attribute not provided for HTMLViewerMenuItem: " + getText());
		}
		setResourcePath(path);
		
		addActionListener(this);
		setEnabled(true);
	}
	
	
	// Accessors
	public void setResourcePath(String path) {
		this.resource_path = path;
	}
	
	public String getResourcePath() {
		return this.resource_path;
	}
	
	// "rsrc/about.html"
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		Outliner.html_viewer.setHTML(Thread.currentThread().getContextClassLoader().getResource(getResourcePath()));
		Outliner.html_viewer.show();
	}
}